(ns ancient-hero-battle-simulator.core
  (:gen-class)
  (:require [ancient-hero-battle-simulator.cards.heroes :as heroes])
  (:require [ancient-hero-battle-simulator.cards.actions :as actions])
  (:require [ancient-hero-battle-simulator.cards.equipment :as equipment]
            [clojure.string :as str]))

(defn alive? [hero]
  (pos? @(:current-hp hero)))

(defn dead? [hero]
  (zero? @(:current-hp hero)))

(defn choose-hero [team team-name unavailable-heroes]
  (println (str "\nSelect a hero from " team-name " Team to attack (enter number):"))
  (let [available-team (vec (filter #(and (alive? %) (not (contains? @unavailable-heroes (:id %)))) team))]
    (loop []
      (doseq [[id hero] (map-indexed vector available-team)]
        (println (str (inc id) ". " (:name hero) " " @(:current-hp hero) " HP")))
      (if-let [input (try (Integer/parseInt (read-line))
                          (catch NumberFormatException _ nil))]
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

(defn attack [attacker defender]
  (let [atk-stats (:stats attacker)
        def-stats (:stats defender)
       
        hit-chance (+ 80
                      (* (:intelligence atk-stats) 0.1)
                      (* (- (:agility def-stats)) 0.15))
       
        roll (rand-int 100)]

    (Thread/sleep 2000)

    (println (str "\n" (:name attacker) " attacks " (:name defender) "!"))
    (Thread/sleep 2000)

    (if (> roll hit-chance)
      (do
        (println (str (:name defender) " dodged the attack!"))
        (Thread/sleep 2000))
      (let [raw-damage (:power atk-stats)
            reduction (* (:defense def-stats) 0.5)
            damage (int (max 5 (- raw-damage reduction)))]

        (swap! (:current-hp defender)
               #(max 0 (- % damage)))

        (println (str (:name attacker) " deals " damage " damage to " (:name defender) "!"))
        (Thread/sleep 2000)))))

(defn enemy-attack [blue-team red-team attacked-heroes defended-heroes]
  (let [available-red (vec (filter #(and (alive? %)
                                         (not (contains? @attacked-heroes (:id %))))
                                   red-team))
        available-blue (vec (filter #(and (alive? %)
                                          (not (contains? @defended-heroes (:id %))))
                                    blue-team))]
    (when (and (seq available-red) (seq available-blue))
      (println "\n[Enemy turn!]")
      (Thread/sleep 2000)

      (let [attacker (rand-nth available-red)
            defender (rand-nth available-blue)]
        (println (:name attacker) " selected to attack!")
        (Thread/sleep 2000)
        (println (:name defender) " selected as target!")
        (attack attacker defender)
        (swap! attacked-heroes conj (:id attacker))
        (swap! defended-heroes conj (:id defender))))))

(defn team-alive? [team]
  (some alive? team))

(defn fight [blue-cards red-cards]
  (loop [round 1]
    (cond
      (not (team-alive? blue-cards))
      (println "\n🏆 RED TEAM WINS!.")

      (not (team-alive? red-cards))
      (println "\n🏆 BLUE TEAM WINS!.")

      :else
      (do
        (println (str "\n=== ROUND " round " ==="))
        (let [attacked-heroes (atom #{})
              defended-heroes (atom #{})
              dead-announced  (atom #{})]

          (loop []
            (when (and (team-alive? blue-cards)
                       (team-alive? red-cards)
                       (some #(and (alive? %)
                                   (not (contains? @attacked-heroes (:id %))))
                             blue-cards)
                       (some #(and (alive? %)
                                   (not (contains? @defended-heroes (:id %))))
                             red-cards))

              (let [[attacker defender]
                    (choose-combatants blue-cards red-cards attacked-heroes defended-heroes)]

                (attack attacker defender)

                (swap! attacked-heroes conj (:id attacker))
                (swap! defended-heroes conj (:id defender))

                (when (and (dead? defender)
                           (not (contains? @dead-announced (:id defender))))
                  (println (str (:name defender) " is dead!"))
                  (swap! dead-announced conj (:id defender)))

                (enemy-attack blue-cards red-cards attacked-heroes defended-heroes)

                (doseq [hero (concat blue-cards red-cards)]
                  (when (and (dead? hero)
                             (not (contains? @dead-announced (:id hero))))
                    (println (str (:name hero) " is dead!"))
                    (swap! dead-announced conj (:id hero)))))

              (recur))))
        (recur (inc round))))))

(defn list-cards [cards selected-cards]
  (doseq [card (remove #(contains? @selected-cards (:id %)) cards)]
    (let [tier-info (when (:tier card)
                      (str "[" (str/upper-case (name (:tier card))) " Tier] "))
          type-info (when (and (:type card) (not (:tier card)))
                      (str "[" (str/capitalize (name (:type card))) "] "))]
      (println
       (str (:id card) ". " tier-info type-info (:name card) " - " (:description card))))))

(defn select-card [player card-number selected-cards cards card-type]
  (loop []
    (println (format "\n%s: Select %s %d (enter number):" player card-type card-number))
    (list-cards cards selected-cards)
    (if-let [id (try (Integer/parseInt (read-line))
                     (catch NumberFormatException _ nil))]
      (if-let [card (some #(and (= (:id %) id) %) cards)]
        (if (contains? @selected-cards id)
          (do
            (println "\nAlready selected! Choose another one.")
            (recur))
          (do
            (swap! selected-cards conj id)
            (println (str "\n" (:name card) " selected!"))
            card))
        (do
          (println "\nCard not found.")
          (recur)))
      (do
        (println "\nInvalid input.")
        (recur)))))

(defn init-hero [hero]
  (assoc hero :current-hp (atom (get-in hero [:stats :health]))))

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
  (loop [n 1
         blue-picks []
         red-picks []
         turn :blue]
    (cond
      (> n count) [blue-picks red-picks]

      (= turn :blue)
      (let [pick (select-card "Blue Player" n selected-in-cat cards type)]
        (recur n (conj blue-picks pick) red-picks :red))

      (= turn :red)
      (let [pick (if (= mode "1")
                   (random-computer-pick selected-in-cat cards type)
                   (select-card "Red Player" n selected-in-cat cards type))]
        (recur (inc n) blue-picks (conj red-picks pick) :blue)))))

(defn random-all-picks [count cards]
  (let [shuffled (shuffle cards)]
    [(vec (take count shuffled))                 
     (vec (take count (drop count shuffled)))])) 

(defn announce-random-picks [blue-picks red-picks type]
  (println (format "\n--- Randomly assigning %s ---" type))
  (doseq [i (range (count blue-picks))]
    (let [blue-card (nth blue-picks i)
          red-card (nth red-picks i)]
      (println (str "Blue Player gets: " (:name blue-card)))
      (Thread/sleep 1000) 
      (println (str "Red Player gets: " (:name red-card)))
      (Thread/sleep 1000))))

(defn select-method []
  (println "\nSelect Card Selection Method:")
  (println "1. Draft")
  (println "2. Random")
  (let [choice (read-line)]
    (if (= choice "2") "random" "draft")))

(defn pick-category
  [method mode count cards type selected]
  (if (= method "draft")
    (draft-category mode count cards type selected)
    (let [[blue red] (random-all-picks count cards)]
      (announce-random-picks blue red type)
      [blue red])))

(defn select-nvn [mode n]
  (println (format "\n--- %dv%d Combat ---" n n))
  (let [method (select-method)
        selected (atom #{})

        hero-count   (* n 2)
        action-count (+ 2 (* n 2))
        equip-count  n

        [blue-heroes red-heroes]
        (pick-category method mode hero-count heroes/heroes "Hero Cards" selected)

        [blue-actions red-actions]
        (pick-category method mode action-count actions/actions "Action Cards" selected)

        [blue-equipment red-equipment]
        (pick-category method mode equip-count equipment/equipment "Equipment Cards" selected)

        blue-cards {:heroes    (mapv init-hero blue-heroes)
                    :actions   blue-actions
                    :equipment blue-equipment}

        red-cards  {:heroes    (mapv init-hero red-heroes)
                    :actions   red-actions
                    :equipment red-equipment}]

    (println "\nAll cards assigned! Starting battle...")
    (Thread/sleep 1000)
    (fight (:heroes blue-cards) (:heroes red-cards))))

(defn show-combat-menu []
  (println "\nSelect combat type")
  (println "1. 1v1")
  (println "2. 2v2")
  (println "3. 3v3")
  (println "4. Back to main menu"))

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
    (println "\nSelect Mode:")
    (println "1. Singleplayer (PvE)")
    (println "2. Multiplayer (PvP)")
    (case (read-line)
      "1" (do (println "\nPvE selected!") "1")
      "2" (do (println "\nPvP selected!") "2")
      (do
        (println "\nInvalid choice")
        (recur)))))

(defn show-main-menu []
  (println "\nMain Menu")
  (println "1. Fight!")
  (println "2. Create your hero")
  (println "3. Exit"))

(defn -main []
  (loop []
    (show-main-menu)
      (case (read-line)
        "1" (let [mode (select-mode)]
              (select-combat mode)
              (recur))
        "2" (do (println "Create your hero...") (recur))
        "3" (println "Bye!")
        (do (println "Invalid choice") (recur)))))