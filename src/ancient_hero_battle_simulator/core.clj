(ns ancient-hero-battle-simulator.core
  (:gen-class)
  (:require [ancient-hero-battle-simulator.cards.heroes :as heroes]
            [ancient-hero-battle-simulator.cards.actions :as actions]
            [ancient-hero-battle-simulator.cards.equipment :as equipment]
            [clojure.string :as str]))

(defn alive? [hero]
  (pos? @(:current-hp hero)))

(defn dead? [hero]
  (zero? @(:current-hp hero)))

(defn team-alive? [team]
  (some alive? team))

(defn init-hero [hero]
  (assoc hero :current-hp (atom (get-in hero [:stats :health]))))

(defn list-cards [cards selected-cards]
  (doseq [card (remove #(contains? @selected-cards (:id %)) cards)]
    (let [tier-info (when (:tier card) (str "[" (str/upper-case (name (:tier card))) " Tier] "))
          type-info (when (and (:type card) (not (:tier card))) (str "[" (str/capitalize (name (:type card))) "] "))]
      (println (str (:id card) ". " tier-info type-info (:name card) " - " (:description card))))))

(defn announce-random-picks [blue-picks red-picks type]
  (println (format "\n--- Randomly assigning %s ---" type))
  (Thread/sleep 1500)
  (doseq [i (range (count blue-picks))]
    (let [blue-card (nth blue-picks i)
          red-card (nth red-picks i)]
      (println (str "Blue Player gets: " (:name blue-card)))
      (Thread/sleep 700)
      (println (str "Red Player gets: " (:name red-card)))
      (Thread/sleep 700))))

(defn show-main-menu []
  (println "\nMain Menu\n1. Fight!\n2. Create your hero\n3. Exit"))

(defn show-combat-menu []
  (println "\nSelect combat type\n1. 1v1\n2. 2v2\n3. 3v3\n4. Back to main menu"))

(defn attack [attacker defender]
  (let [atk-stats (:stats attacker)
        def-stats (:stats defender)
        hit-chance (+ 80 (* (:intelligence atk-stats) 0.1) (* (- (:agility def-stats)) 0.15))
        roll (rand-int 100)]
    (Thread/sleep 2000)
    (println (str "\n" (:name attacker) " attacks " (:name defender) "!"))
    (Thread/sleep 2000)
    (if (> roll hit-chance)
      (do (println (str (:name defender) " dodged the attack!")) (Thread/sleep 2000))
      (let [raw-damage (:power atk-stats)
            reduction (* (:defense def-stats) 0.5)
            damage (int (max 5 (- raw-damage reduction)))]
        (swap! (:current-hp defender) #(max 0 (- % damage)))
        (println (str (:name attacker) " deals " damage " damage to " (:name defender) "!"))
        (Thread/sleep 2000)))))

(defn choose-hero [team team-name unavailable-heroes]
  (println (str "\nSelect a hero from " team-name " Team to attack (enter number):"))
  (let [available-team (vec (filter #(and (alive? %) (not (contains? @unavailable-heroes (:id %)))) team))]
    (loop []
      (doseq [[id hero] (map-indexed vector available-team)]
        (println (str (inc id) ". " (:name hero) " " @(:current-hp hero) " HP")))
      (if-let [input (try (Integer/parseInt (read-line)) (catch NumberFormatException _ nil))]
        (if (and (>= input 1) (<= input (count available-team)))
          (available-team (dec input))
          (do (println "Invalid choice.") (recur)))
        (do (println "Invalid input.") (recur))))))

(defn choose-combatants [blue-team red-team attacked-heroes defended-heroes]
  (let [attacker (choose-hero blue-team "Blue" attacked-heroes)]
    (println (:name attacker) " selected to attack!")
    (let [defender (choose-hero red-team "Red" defended-heroes)]
      (println (:name defender) " selected as target!")
      [attacker defender])))

(defn enemy-attack [blue-team red-team attacked-heroes defended-heroes]
  (let [available-red (vec (filter #(and (alive? %) (not (contains? @attacked-heroes (:id %)))) red-team))
        available-blue (vec (filter #(and (alive? %) (not (contains? @defended-heroes (:id %)))) blue-team))]
    (when (and (seq available-red) (seq available-blue))
      (println "\n[Enemy turn!]") (Thread/sleep 2000)
      (let [attacker (rand-nth available-red)
            defender (rand-nth available-blue)]
        (println (:name attacker) " selected to attack!") (Thread/sleep 2000)
        (println (:name defender) " selected as target!")
        (attack attacker defender)
        (swap! attacked-heroes conj (:id attacker))
        (swap! defended-heroes conj (:id defender))))))

(defn print-card [card]
  (cond
    (and (:stats card) (:current-hp card))
    (format "[%s %dHP]" (:name card) @(:current-hp card))

    :else
    (format "[%s]" (:name card))))

(defn print-field [field orientation]
  (let [[top bottom]
        (if (= orientation :hero-bottom)
          [:action :hero]
          [:hero :action])]

    (doseq [slot field]
      (print
       (if-let [c (get slot top)]
         (str (print-card c) " ")
         "[    ] ")))
    (println)

    (doseq [slot field]
      (print
       (if-let [c (get slot bottom)]
         (str (print-card c) " ")
         "[    ] ")))
    (println)))

(defn display-board [blue-field red-field n]
  (print-field @red-field :hero-bottom)

  (println (apply str (repeat (* n 6) "=")))

  (print-field @blue-field :hero-top)

  (println))

(defn first-empty-slot-index [field]
  (first
   (keep-indexed
    (fn [i slot]
      (when (empty? slot) i))
    field)))

(defn selection-phase
  [player-name hand field enemy-field n]
  (println (str "\n--- " player-name " SELECTION PHASE ---"))
  (Thread/sleep 500)

  (loop []
    (doseq [[id card] (map-indexed vector @hand)]
      (println (str (inc id) ". " (:name card) " - " (:description card))))

    (println "\nChoose a card to play:")
    (if-let [choice (try (Integer/parseInt (read-line))
                         (catch Exception _ nil))]
      (if-let [card (nth @hand (dec choice) nil)]
        (do
          (println (str "\n" player-name " plays: " (:name card)))
          (swap! hand #(vec (remove #{card} %)))

          (if-let [idx (first-empty-slot-index @field)]
            (let [slot (if (:stats card)
                         {:hero card}
                         {:action card})]
              (swap! field assoc idx slot))
            (println "No empty slots available!"))

          (display-board
           (if (= player-name "BLUE") field enemy-field)
           (if (= player-name "BLUE") enemy-field field)
           n)

          card)
        (do (println "Invalid choice.") (recur)))
      (do (println "Invalid input.") (recur)))))

(defn draw-cards-from-pool [n {:keys [heroes actions equipment]}]
  (let [total (case n
                1 3
                2 5
                3 7)
        pool (concat heroes actions equipment)]
    (loop []
      (let [drawn (take total (shuffle pool))]
        (if (some :stats drawn)
          drawn
          (recur))))))

(defn draw-phase [player-name n cards hand]
  (println (str "\n--- " player-name " DRAW PHASE ---"))
  (Thread/sleep 1000)

  (let [drawn (draw-cards-from-pool n cards)]
    (doseq [card drawn]
      (Thread/sleep 800)
      (println (str player-name " draws: " (:name card)))
      (swap! hand conj card))))

(defn heroes-on-field [field]
  (->> field
       (map :hero)
       (filter some?)
       (filter alive?)
       vec))

(defn attack-phase [player-name field enemy-field]
  (println (str "\n--- " player-name " ATTACK PHASE ---"))
  (Thread/sleep 800)

  (let [attackers (heroes-on-field @field)
        defenders (heroes-on-field @enemy-field)]
    (when (and (seq attackers) (seq defenders))
      (let [attacker (choose-hero attackers player-name (atom #{}))
            defender (choose-hero defenders
                                  (if (= player-name "BLUE") "Red" "Blue")
                                  (atom #{}))]
        (attack attacker defender)))))

(defn player-turn
  [player-name cards n field enemy-field can-attack?]
  (println "\n==============================")
  (println (str ">>> " player-name " PLAYER TURN <<<"))
  (println "==============================")

  (let [hand (atom [])]
    (draw-phase player-name n cards hand)
    (Thread/sleep 500)
    (selection-phase player-name hand field enemy-field n)

    (when can-attack?
      (attack-phase player-name field enemy-field))))

(defn init-field [n]
  (vec (repeat n {})))

(defn fight-cards [blue-cards red-cards n]
  (let [blue-field (atom (init-field n))
        red-field  (atom (init-field n))]

    (player-turn "BLUE" blue-cards n blue-field red-field false)
    (player-turn "RED"  red-cards  n red-field  blue-field true)

    (loop []
        (player-turn "BLUE" blue-cards n blue-field red-field true)
        (player-turn "RED"  red-cards  n red-field  blue-field true)
        (recur))))

(defn select-card [player card-number selected-cards cards card-type]
  (loop []
    (println (format "\n%s: Select %s %d (enter number):" player card-type card-number))
    (list-cards cards selected-cards)
    (if-let [id (try (Integer/parseInt (read-line)) (catch NumberFormatException _ nil))]
      (if-let [card (some #(and (= (:id %) id) %) cards)]
        (if (contains? @selected-cards id)
          (do (println "\nAlready selected!") (recur))
          (do (swap! selected-cards conj id) (println (str "\n" (:name card) " selected!")) card))
        (do (println "\nCard not found.") (recur)))
      (do (println "\nInvalid input.") (recur)))))

(defn random-computer-pick [selected-in-cat cards type]
  (let [available (remove #(contains? @selected-in-cat (:id %)) cards)
        picked (rand-nth available)]
    (println (format "\nComputer (Red Player) is picking %s..." type))
    (Thread/sleep 2000)
    (swap! selected-in-cat conj (:id picked))
    (println (str (:name picked) " selected!"))
    (Thread/sleep 1000)
    picked))

(defn draft-category [mode count cards type selected-in-cat]
  (reset! selected-in-cat #{})
  (loop [n 1 blue-picks [] red-picks [] turn :blue]
    (cond
      (> n count) [blue-picks red-picks]
      (= turn :blue) (let [pick (select-card "Blue Player" n selected-in-cat cards type)]
                       (recur n (conj blue-picks pick) red-picks :red))
      (= turn :red) (let [pick (if (= mode "1") (random-computer-pick selected-in-cat cards type) (select-card "Red Player" n selected-in-cat cards type))]
                      (recur (inc n) blue-picks (conj red-picks pick) :blue)))))

(defn random-all-picks [count cards]
  (let [shuffled (shuffle cards)]
    [(vec (take count shuffled)) (vec (take count (drop count shuffled)))]))

(defn select-method []
  (println "\nSelect Card Selection Method:\n1. Draft\n2. Random")
  (if (= (read-line) "2") "random" "draft"))

(defn pick-category [method mode count cards type selected]
  (if (= method "draft")
    (draft-category mode count cards type selected)
    (let [[blue red] (random-all-picks count cards)]
      (announce-random-picks blue red type)
      [blue red])))

(defn select-nvn [mode n]
  (println (format "\n--- %dv%d Combat ---" n n))
  (let [method (select-method)
        selected (atom #{})
        hero-count (* n 2)
        action-count (+ 2 (* n 2))
        equip-count n
        [blue-heroes red-heroes] (pick-category method mode hero-count heroes/heroes "Hero Cards" selected)
        [blue-actions red-actions] (pick-category method mode action-count actions/actions "Action Cards" selected)
        [blue-equipment red-equipment] (pick-category method mode equip-count equipment/equipment "Equipment Cards" selected)
        blue-cards {:heroes (mapv init-hero blue-heroes) :actions blue-actions :equipment blue-equipment}
        red-cards {:heroes (mapv init-hero red-heroes) :actions red-actions :equipment red-equipment}]
    (println "\nAll cards assigned! Starting battle...")
    (Thread/sleep 1000)
    (fight-cards blue-cards red-cards n)))

(defn select-combat [mode]
  (loop []
    (show-combat-menu)
    (case (read-line)
      "1" (do (select-nvn mode 1) (recur))
      "2" (do (select-nvn mode 2) (recur))
      "3" (do (select-nvn mode 3) (recur))
      "4" (println "Returning to main menu...")
      (do (println "Invalid choice") (recur)))))

(defn select-mode []
  (loop []
    (println "\nSelect Mode:\n1. Singleplayer (PvE)\n2. Multiplayer (PvP)")
    (case (read-line)
      "1" (do (println "\nPvE selected!") "1")
      "2" (do (println "\nPvP selected!") "2")
      (do (println "\nInvalid choice") (recur)))))

(defn -main []
  (loop []
    (show-main-menu)
    (case (read-line)
      "1" (let [mode (select-mode)] (select-combat mode) (recur))
      "2" (do (println "Create your hero...") (recur))
      "3" (println "Bye!")
      (do (println "Invalid choice") (recur)))))