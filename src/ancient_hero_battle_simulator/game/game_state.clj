(ns ancient-hero-battle-simulator.game.game-state)

(defn alive? [hero]
  (pos? @(:current-hp hero)))

(defn dead? [hero]
  (zero? @(:current-hp hero)))

(defn init-hero [hero]
  (assoc hero :current-hp (atom (get-in hero [:stats :health]))))

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

(defn get-slot-config [ctype]
  (case ctype
    :hero      {:key :hero :finder first-empty-action-slot-index :msg "plays" :err "No empty hero slots!"}
    :action    {:key :action :finder first-empty-action-slot-index :msg "plays" :err "No empty action slots!"}
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

(defn place-card-on-field! [card field key idx]
  (swap! field update idx assoc key card))

(defn heroes-on-field [field]
  (->> field
       (map :hero)
       (filter some?)
       (filter alive?)
       vec))

(defn playable-cards [hand used-types]
  (->> @hand
       (filter #(not (contains? used-types (:category %))))
       (sort-by #(get category-priority (:category %) 99))
       vec))

(defn available-heroes [team unavailable]
  (vec (filter #(and (alive? %)
                     (not (contains? @unavailable (:id %))))
               team)))

(defn game-over? [blue-hp red-hp]
  (or (<= @blue-hp 0) (<= @red-hp 0)))

(defn get-winner [blue-hp red-hp]
  (cond
    (<= @blue-hp 0) "RED"
    (<= @red-hp 0) "BLUE"
    :else nil))