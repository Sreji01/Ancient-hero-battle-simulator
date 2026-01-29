(ns ancient-hero-battle-simulator.core
  (:gen-class)
  (:require [ancient-hero-battle-simulator.cards.heroes :as heroes]
            [ancient-hero-battle-simulator.cards.actions :as actions]
            [ancient-hero-battle-simulator.cards.traps :as traps]
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
    (let [tier-info (when (and (:tier card) (= (:category card) :hero))
                      (str "[" (-> (:category card) name str/capitalize)
                           " " (-> (:tier card) name str/upper-case) "-Tier] "))
          type-info (when (and (:type card) (not (:tier card)) (not= (:category card) :hero))
                      (str "[" (-> (:type card) name str/capitalize) "] "))]
      (println (str (:id card) ". " (or tier-info type-info) (:name card) " - " (:description card))))))


(defn announce-random-picks [blue-picks red-picks type]
  (println (format "\n--- Randomly assigning %s ---" type))
  (Thread/sleep 1500)
  (doseq [i (range (count blue-picks))]
    (let [blue-card (nth blue-picks i)
          red-card (nth red-picks i)]
      (println (str "Blue Player gets: " (:name blue-card)))
      (Thread/sleep 200)
      (println (str "Red Player gets: " (:name red-card)))
      (Thread/sleep 200))))

(defn attack [attacker defender defender-player-hp]
  (let [atk-stats (:stats attacker)
        def-stats (:stats defender)
        hit-chance (+ 80 (* (:intelligence atk-stats) 0.1)
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
        (swap! (:current-hp defender) #(max 0 (- % damage)))
        (swap! defender-player-hp #(max 0 (- % damage)))

        (println (str (:name attacker) " deals " damage " damage to "
                      (:name defender) "!"))
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

#_(defn choose-combatants [blue-team red-team attacked-heroes defended-heroes]
  (let [attacker (choose-hero blue-team "Blue" attacked-heroes)]
    (println (:name attacker) " selected to attack!")
    (let [defender (choose-hero red-team "Red" defended-heroes)]
      (println (:name defender) " selected as target!")
      [attacker defender])))

#_(defn enemy-attack [blue-team red-team attacked-heroes defended-heroes]
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

    (:trigger card)
    "[TRAP]"

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

(defn first-empty-hero-slot-index [field]
  (first
   (keep-indexed
    (fn [i slot]
      (when (or (nil? (:hero slot)) (empty? (:hero slot)))
        i))
    field)))

(defn first-empty-action-slot-index [field]
  (first
   (keep-indexed
    (fn [i slot]
      (when (or (nil? (:action slot)) (empty? (:action slot)))
        i))
    field)))

(defn card-type [card]
  (:category card))

(defn format-card [card]
  (cond
    (= (:category card) :hero)
    (str "[Hero " (-> (:tier card) name str/upper-case) "-Tier] "
         (:name card) " - " (:description card))

    (= (:category card) :equipment)
    (str "[Equipment " (-> (:tier card) name str/upper-case) "-Tier] "
         (:name card) " - " (:description card))

    (= (:category card) :action)
    (str "[Action] " (:name card) " - " (:description card))

    (= (:category card) :trap)
    (str "[Trap] " (:name card) " - " (:description card))

    :else
    (:name card)))

(def category-priority
  {:hero 0
   :action 1
   :trap 2
   :equipment 3})

(defn heroes-on-field [field]
  (->> field
       (map :hero)
       (filter some?)
       (filter alive?)
       vec))

(defn apply-damage-effect [card enemy-field enemy-player-hp]
  (let [effect (:effect card)]
    (cond
      (:damage effect)
      (let [defenders (heroes-on-field @enemy-field)]
        (if (seq defenders)
          (let [target (choose-hero defenders "Target Enemy" (atom #{}))
                dmg (:damage effect)]
            (swap! (:current-hp target) #(max 0 (- % dmg)))
            (println (format "[DAMAGE] %s deals %d damage to %s!\n"
                             (:name card) dmg (:name target))))
          (println "\nNo enemies to target! Effect wasted.")))

      (:damage-all-enemies effect)
      (let [defenders (heroes-on-field @enemy-field)
            dmg (:damage-all-enemies effect)]
        (doseq [target defenders]
          (swap! (:current-hp target) #(max 0 (- % dmg))))
        (println (format "[AOE] %s deals %d damage to ALL enemy heroes!\n"
                         (:name card) dmg)))

      (:player-damage effect)
      (let [dmg (:player-damage effect)]
        (swap! enemy-player-hp #(max 0 (- % dmg)))
        (println (format "[DIRECT] %s deals %d damage to the enemy player!\n"
                         (:name card) dmg))))))

(defn apply-action-effect [card field enemy-field enemy-player-hp]
  (let [type (:type card)]
    (case type
      :attack (apply-damage-effect card enemy-field enemy-player-hp)

      :defense (println "Defense effects coming soon...\n")

      :heal (println "Healing effects coming soon...\n")

      :buff (println "Buff effecst coming soon...\n")
      
      :utility (println "Utility effects coming soon...\n")

      (println (format "Effect for type %s is not yet implemented." type)))))

(defn get-slot-config [ctype]
  (case ctype
    :hero      {:key :hero :finder first-empty-hero-slot-index :msg "plays" :err "No empty hero slots!"}
    :action    {:key :action :finder first-empty-action-slot-index :msg "plays" :err "No empty action slots!"}
    :trap      {:key :action :finder first-empty-action-slot-index :msg "places" :err "No empty trap slots!"}
    :equipment {:key :action :finder first-empty-action-slot-index :msg "equips" :err "No empty equipment slots!"}))

(defn execute-card-play! [card hand field enemy-field enemy-player-hp player-name]
  (let [ctype (card-type card)
        {:keys [key finder msg err]} (get-slot-config ctype)
        idx (finder @field)]
    (if idx
      (let [display-name (if (= ctype :trap) "TRAP" (:name card))]
        (println (format "\n%s %s: %s\n" player-name msg display-name))

        (swap! hand (fn [curr] (vec (remove #(= (:id %) (:id card)) curr))))

        (when (= ctype :action)
          (apply-action-effect card field enemy-field enemy-player-hp))

        (swap! field update idx assoc key card)
        {:success true})
      {:success false :err err})))

(defn selection-phase [player-name hand field enemy-field enemy-player-hp n]
  (println (str "\n--- " player-name " SELECTION PHASE ---\n"))
  (Thread/sleep 800)
  (let [show-board #(display-board
                     (if (= player-name "BLUE") field enemy-field)
                     (if (= player-name "BLUE") enemy-field field) n)]
    (show-board)
    (loop [used-types #{}]
      (let [playable (->> @hand
                          (filter #(not (contains? used-types (card-type %))))
                          (sort-by #(get category-priority (:category %) 99))
                          vec)]
        (when (seq playable)
          (println "Choose a card to play:")
          (doseq [[i card] (map-indexed vector playable)]
            (println (str (inc i) ". " (format-card card))))
          (println (str (inc (count playable)) ". End Selection Phase"))

          (if-let [choice (try (Integer/parseInt (read-line)) (catch Exception _ nil))]
            (cond
              (= choice (inc (count playable)))
              (println "\nEnding selection phase...")

              (and (>= choice 1) (<= choice (count playable)))
              (let [card (nth playable (dec choice))
                    result (execute-card-play! card hand field enemy-field enemy-player-hp player-name)]
                (if (:success result)
                  (do
                    (Thread/sleep 1000)
                    (show-board)
                    (recur (conj used-types (card-type card))))
                  (do (println (:err result)) (recur used-types))))

              :else (do (println "Invalid choice.") (recur used-types)))
            (do (println "Invalid input.") (recur used-types))))))))

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

(defn draw-phase [player-name n deck hand first-draw?]
  (println (str "\n--- " player-name " DRAW PHASE ---"))
  (Thread/sleep 1000)

  (let [count (if first-draw?
                (case n
                  1 3
                  2 5
                  3 7
                  4 9)
                1)
        drawn (if first-draw?
                (first-draw-with-hero deck count)
                (draw-from-deck deck 1))]
    (doseq [card drawn]
      (Thread/sleep 500)
      (println (str player-name " draws: " (:name card)))
      (swap! hand conj card))))

(defn attack-phase [player-name field enemy-field enemy-player-hp]
  (println (str "\n--- " player-name " ATTACK PHASE ---"))
  (Thread/sleep 800)

  (loop [available-attackers (heroes-on-field @field)]
    (when (seq available-attackers)
      (let [defenders (heroes-on-field @enemy-field)]
        (if (empty? defenders)
          (println "No enemies to attack!")
          (do
            (println "\nSelect a hero to attack:")
            (doseq [[idx hero] (map-indexed vector available-attackers)]
              (println (str (inc idx) ". " (:name hero) " " @(:current-hp hero) " HP")))
            (println (str (inc (count available-attackers)) ". End Attack Phase"))

            (if-let [input (try (Integer/parseInt (read-line))
                                (catch NumberFormatException _ nil))]
              (cond
                (= input (inc (count available-attackers)))
                (println "\nEnding attack phase...")

                (and (>= input 1) (<= input (count available-attackers)))
                (let [attacker (nth available-attackers (dec input))
                      defender (choose-hero defenders
                                            (if (= player-name "BLUE") "Red" "Blue")
                                            (atom #{}))]
                  (attack attacker defender enemy-player-hp)
                  (recur (vec (remove #(= (:id %) (:id attacker)) available-attackers))))

                :else
                (do (println "Invalid choice.") (recur available-attackers)))

              (do (println "Invalid input.") (recur available-attackers)))))))))


(defn print-player-hp [player-name hp]
  (println (format "%s PLAYER HEALTH: %d HP" player-name @hp)))

(defn player-turn
  [player-name n deck hand field enemy-field
   player-hp enemy-player-hp
   can-attack? first-draw?]

  (println "\n==============================")
  (println (str ">>> " player-name " PLAYER TURN <<<"))
  (println "==============================")

  (print-player-hp player-name player-hp)
  (Thread/sleep 800)

  (draw-phase player-name n deck hand first-draw?)
  (Thread/sleep 400)

  ;; Prosleđujemo enemy-player-hp ovde:
  (selection-phase player-name hand field enemy-field enemy-player-hp n)

  (when can-attack?
    (attack-phase player-name field enemy-field enemy-player-hp))

  ;; Čišćenje iskorišćenih akcija sa table na kraju poteza
  (swap! field
         #(mapv
           (fn [slot]
             (if (and (:action slot)
                      (= (:category (:action slot)) :action)) ;; Provera kategorije
               (dissoc slot :action)
               slot))
           %)))

(defn init-field [n]
  (vec (repeat n {})))

(defn initial-player-hp [n]
  (* 300 n))

(defn init-player-deck [{:keys [heroes actions traps equipment]}]
  (atom (shuffle (vec (concat heroes actions traps equipment)))))

(defn fight-cards [blue-cards red-cards n]
  (let [blue-field (atom (init-field n))
        red-field  (atom (init-field n))
        blue-deck  (init-player-deck blue-cards)
        red-deck   (init-player-deck red-cards)
        blue-hand  (atom [])
        red-hand   (atom [])
        blue-hp    (atom (initial-player-hp n))
        red-hp     (atom (initial-player-hp n))]

    (player-turn "BLUE" n blue-deck blue-hand blue-field red-field
                 blue-hp red-hp false true)

    (player-turn "RED"  n red-deck red-hand red-field blue-field
                 red-hp blue-hp true true)

    (loop []
      (when (and (pos? @blue-hp) (pos? @red-hp))
        (player-turn "BLUE" n blue-deck blue-hand blue-field red-field
                     blue-hp red-hp true false)
        (player-turn "RED"  n red-deck red-hand red-field blue-field
                     red-hp blue-hp true false)
        (recur)))))

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
        action-count (+ 2 n)
        trap-count (+ 2 n)
        equip-count n
        [blue-heroes red-heroes] (pick-category method mode hero-count heroes/heroes "Hero Cards" selected)
        [blue-actions red-actions] (pick-category method mode action-count actions/actions "Action Cards" selected)
        [blue-traps red-traps] (pick-category method mode trap-count traps/traps "Trap Cards" selected)
        [blue-equipment red-equipment] (pick-category method mode equip-count equipment/equipment "Equipment Cards" selected)
        blue-cards {:heroes (mapv init-hero blue-heroes) :actions blue-actions :traps blue-traps :equipment blue-equipment}
        red-cards {:heroes (mapv init-hero red-heroes) :actions red-actions :traps red-traps :equipment red-equipment}]
    (println "\nAll cards assigned! Starting battle...")
    (Thread/sleep 1000)
    (fight-cards blue-cards red-cards n)))

(defn show-main-menu []
  (println "\nMain Menu\n1. Fight!\n2. Create your hero\n3. Exit"))

(defn show-combat-menu []
  (println "\nSelect combat type\n1. 1v1\n2. 2v2\n3. 3v3\n4. 4v4\n5. Back to main menu"))

(defn select-combat [mode]
  (loop []
    (show-combat-menu)
    (case (read-line)
      "1" (do (select-nvn mode 1) (recur))
      "2" (do (select-nvn mode 2) (recur))
      "3" (do (select-nvn mode 3) (recur))
      "4" (do (select-nvn mode 4) (recur))
      "5" (println "Returning to main menu...")
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