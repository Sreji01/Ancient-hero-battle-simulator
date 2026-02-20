(ns ancient-hero-battle-simulator.game.game-state)

(defn alive? [hero]
  (pos? @(:current-hp hero)))

(defn dead? [hero]
  (zero? @(:current-hp hero)))

(defn init-hero [hero]
  (assoc hero
         :current-hp (atom (get-in hero [:stats :health]))
         :current-stats (atom (:stats hero))))

(defn init-team [team]
  (update team :heroes #(mapv init-hero %)))

(defn init-field [n]
  (vec (repeat n {})))

(defn initial-player-hp [n]
  (* 300 n))

(defn init-player-deck [{:keys [heroes actions traps equipment]}]
  (atom (shuffle (vec (concat heroes actions traps equipment)))))

(def category-priority
  {:hero 0 :action 1 :trap 2 :equipment 3})

(defn create-game-state [blue-cards red-cards n]
  {:blue {:field (atom (init-field n))
          :deck  (init-player-deck blue-cards)
          :hand  (atom [])
          :hp    (atom (initial-player-hp n))}
   :red  {:field (atom (init-field n))
          :deck  (init-player-deck red-cards)
          :hand  (atom [])
          :hp    (atom (initial-player-hp n))}
   :n n})

(defn first-empty-action-slot-index [field]
  (first
   (keep-indexed
    (fn [i slot]
      (when (or (nil? (:action slot)) (empty? (:action slot)))
        i))
    field)))

(defn first-empty-hero-slot-index [field]
  (first
   (keep-indexed
    (fn [i slot]
      (when (nil? (:hero slot))
        i))
    field)))


(defn get-slot-config [ctype]
  (case ctype
    :hero      {:key :hero :finder first-empty-hero-slot-index :msg "plays" :err "\nNo empty hero slots!\n"}
    :action    {:key :action :finder first-empty-action-slot-index :msg "plays" :err "\nNo empty action slots!\n"}
    :trap      {:key :action :finder first-empty-action-slot-index :msg "places" :err "No empty trap slots!"}
    :equipment {:key :action :finder first-empty-action-slot-index :msg "equips" :err "No empty equipment slots!"}))

(defn clear-action-slots! [field]
  (swap! field
         #(mapv
           (fn [slot]
             (if (and (:action slot)
                      (= (:category (:action slot)) :action))
               (dissoc slot :action)
               slot))
           %)))

(defn heroes-on-field [field]
  (->> field
       (map :hero)
       (filter some?)
       (filter alive?)
       vec))

(defn reset-current-stats! [field]
  (doseq [hero (heroes-on-field @field)]
    (swap! (:current-stats hero) (constantly (:stats hero)))))

(defn place-card-on-field! [card field key idx]
  (swap! field update idx assoc key card))

(defn playable-cards [hand used-types]
  (->> @hand
       (filter #(not (contains? used-types (:category %))))
       (sort-by #(get category-priority (:category %) 99))
       vec))

(defn available-heroes [team unavailable]
  (vec (filter #(and (alive? %)
                     (not (contains? @unavailable (:id %))))
               team)))

(defn valid-slot? [card field]
  (let [{:keys [finder err]} (get-slot-config (:category card))
        idx (finder @field)]
    (if idx
      {:success true :idx idx}
      {:success false :err err})))

(defn game-over? [blue-hp red-hp]
  (or (<= @blue-hp 0) (<= @red-hp 0)))

(defn get-winner [blue-hp red-hp]
  (cond
    (<= @blue-hp 0) "RED"
    (<= @red-hp 0) "BLUE"
    :else nil))

(defn remove-card-from-field!
  [field slot-key card]
  (swap! field
         (fn [slots]
           (mapv
            (fn [slot]
              (if (= (:id (get slot slot-key)) (:id card))
                (dissoc slot slot-key)
                slot))
            slots))))

(defn remove-hero-from-field! [field hero]
  (remove-card-from-field! field :hero hero))

(defn remove-trap-from-field! [field trap]
  (remove-card-from-field! field :action trap))

(defn place-hero-on-field! [field hero]
  (if-let [idx (first-empty-hero-slot-index @field)]
    (do (swap! field update idx assoc :hero hero) true)
    false))

(defn move-hero-back! [hero blue-field red-field]
  (let [owner (:original-owner hero)]
    (remove-hero-from-field! blue-field hero)
    (remove-hero-from-field! red-field hero)
    (case owner
      :blue (place-hero-on-field! blue-field hero)
      :red  (place-hero-on-field! red-field hero)
      (println "Error: Hero has no valid original owner!"))))

(defn update-hero-on-field! [field hero]
  (swap! field (fn [slots]
                 (mapv #(if (= (:id (:hero %)) (:id hero))
                          (assoc % :hero hero)
                          %)
                       slots))))

(defn restore-mind-controlled-heroes! [blue-field red-field]
  (doseq [hero (concat (heroes-on-field @blue-field)
                       (heroes-on-field @red-field))]
    (when (:controlled hero)
      (let [updated (update hero :control-rounds dec)]
        (if (zero? (:control-rounds updated))
          (move-hero-back! (dissoc updated :controlled :control-rounds) blue-field red-field)
          (update-hero-on-field! (if (= (:original-owner updated) :red) blue-field red-field) updated))))))

(defn check-and-remove-dead! [target field]
  (when (dead? target)
    (println (format "\n%s has been defeated!" (:name target)))
    (remove-hero-from-field! field target)))

(defn controlled-heroes-count [field]
  (->> (heroes-on-field @field)
       (filter :controlled)
       count))

(defn free-hero-slots [field]
  (count (filter #(nil? (:hero %)) @field)))

(defn can-place-hero? [my-field enemy-field]
  (let [controlled-on-enemy (controlled-heroes-count enemy-field)
        free-slots (free-hero-slots my-field)]
    (> free-slots controlled-on-enemy)))

(defn traps-with-trigger [field trigger]
  (->> @field
       (map :action)
       (filter some?)
       (filter #(and (= (:category %) :trap)
                     (= (:trigger %) trigger)))))