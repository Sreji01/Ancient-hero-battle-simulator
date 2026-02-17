(ns ancient-hero-battle-simulator.game.deck-menagment
  (:require
   [ancient-hero-battle-simulator.game.ui :as ui]
   [ancient-hero-battle-simulator.game.game-state :as state]
   [ancient-hero-battle-simulator.cards.heroes :as heroes]
   [ancient-hero-battle-simulator.cards.actions :as actions]
   [ancient-hero-battle-simulator.cards.traps :as traps]
   [ancient-hero-battle-simulator.cards.equipment :as equipment]))

(defn draw-count [n first-draw?]
  (if first-draw?
    (case n
      1 3
      2 5
      3 7
      4 9)
    1))

(defn draw-from-deck [deck n]
  (let [drawn (take n @deck)]
    (swap! deck #(vec (drop n %)))
    drawn))

(defn first-draw-with-hero [deck n]
  (loop []
    (let [drawn (draw-from-deck deck n)]
      (if (some :stats drawn)
        drawn
        (do
          (swap! deck #(shuffle (vec (concat drawn %))))
          (recur))))))

(defn draw-cards [deck n first-draw?]
  (let [count (draw-count n first-draw?)]
    (if first-draw?
      (first-draw-with-hero deck count)
      (draw-from-deck deck count))))

(defn remove-card-from-hand! [hand card]
  (swap! hand (fn [curr] (vec (remove #(= (:id %) (:id card)) curr)))))

(defn add-to-hand! [hand cards]
  (doseq [card cards]
    (swap! hand conj card)))

(defn select-card [player card-number selected-cards cards card-type]
  (loop []
    (println (format "\n%s: Select %s %d (enter number):" player card-type card-number))
    (ui/list-cards cards selected-cards)
    (let [available (remove #(contains? @selected-cards (:id %)) cards)]
      (if-let [choice (try (Integer/parseInt (read-line)) (catch NumberFormatException _ nil))]
        (if-let [card (when (and (>= choice 1) (<= choice (count available)))
                        (nth available (dec choice)))]
          (if (contains? @selected-cards (:id card))
            (do (println "\nAlready selected!") (recur))
            (do (swap! selected-cards conj (:id card)) (println (str "\n" (:name card) " selected!")) card))
          (do (println "\nCard not found.") (recur)))
        (do (println "\nInvalid input.") (recur))))))

(defn random-computer-pick [selected-in-cat cards type]
  (let [available (remove #(contains? @selected-in-cat (:id %)) cards)
        picked (rand-nth available)]
    (println (format "\nComputer (Red Player) is picking %s...\n" type))
    (Thread/sleep 2000)
    (swap! selected-in-cat conj (:id picked))
    (println (str (:name picked) " selected!"))
    (Thread/sleep 1000)
    picked))

(defn draft-category
  [mode count cards type selected-in-cat]
  (reset! selected-in-cat #{})
  (loop [n 1
         blue-picks []
         red-picks []
         turn :blue]
    (cond
      (> n count)
      [blue-picks red-picks]

      (= turn :blue)
      (let [pick (select-card "Blue Player" n selected-in-cat cards type)]
        (recur n
               (conj blue-picks pick)
               red-picks
               :red))

      (= turn :red)
      (let [pick (if (= mode "1")
                   (random-computer-pick selected-in-cat cards type)
                   (select-card "Red Player" n selected-in-cat cards type))]
        (recur (inc n)
               blue-picks
               (conj red-picks pick)
               :blue)))))

(defn random-all-picks [count cards]
  (let [shuffled (shuffle cards)]
    [(vec (take count shuffled)) (vec (take count (drop count shuffled)))]))

(defn pick-category [method mode count cards type selected]
  (if (= method "draft")
    (draft-category mode count cards type selected)
    (let [[blue red] (random-all-picks count cards)]
      (ui/announce-random-picks blue red type)
      [blue red])))

(defn pick-all-cards [method mode counts selected]
  {:heroes    (pick-category method mode (:heroes counts) heroes/heroes "Hero Cards" selected)
   :actions   (pick-category method mode (:actions counts) actions/actions "Action Cards" selected)
   :traps     (pick-category method mode (:traps counts) traps/traps "Trap Cards" selected)
   :equipment (pick-category method mode (:equipment counts) equipment/equipment "Equipment Cards" selected)})

(defn card-counts [n]
  {:heroes    (* n 2)
   :actions   (+ 2 n)
   :traps     (+ 2 n)
   :equipment n})

(defn prepare-cards [method mode n selected]
  (let [counts (card-counts n)
        {:keys [heroes actions traps equipment]} (pick-all-cards method mode counts selected)]
    {:blue (state/init-team {:heroes (first heroes)
                             :actions (first actions)
                             :traps (first traps)
                             :equipment (first equipment)})
     :red  (state/init-team {:heroes (second heroes)
                             :actions (second actions)
                             :traps (second traps)
                             :equipment (second equipment)})}))