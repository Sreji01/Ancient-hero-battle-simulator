(ns ancient-hero-battle-simulator.game.ui
  (:gen-class)
  (:require [clojure.string :as str]))

(defn print-attack-message [attacker target outcome damage]
  (Thread/sleep 1500)
  (println (str "\n" (:name attacker) " attacks " (:name target) "!\n"))
  (Thread/sleep 2000)
  (case outcome
    :dodge-roll (println (str (:name target) " dodged with Dodge Roll!"))
    :dodge      (println (str (:name target) " dodged the attack!"))
    :hit        (when (> damage 0)
                    (println (str (:name attacker) " deals " damage " damage to " (:name target) "!")))))

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

(defn announce-battle [n]
  (println (format "\n--- %dv%d Combat ---" n n))
  (println "\nAll cards assigned! Starting battle..."))

(defn show-main-menu []
  (println "\nMain Menu\n1. Fight!\n2. Create your hero\n3. Exit"))

(defn show-mode-menu []
  (println "\nSelect Mode:\n1. Singleplayer (PvE)\n2. Multiplayer (PvP)\n3. Back to main menu"))

(defn show-combat-menu []
  (println "\nSelect combat type\n1. 1v1\n2. 2v2\n3. 3v3\n4. 4v4\n5. Back to main menu"))

(defn show-method-menu []
  (println "\nSelect Card Selection Method:\n1. Draft\n2. Random\n3. Back to main menu"))

(defn card-display-name [card]
  (if (= (:category card) :trap)
    "TRAP"
    (:name card)))

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

(defn print-card [card]
  (cond
    (and (:stats card) (:current-hp card))
    (format "[%s %dHP]" (:name card) @(:current-hp card))

    (:trigger card)
    "[TRAP]"

    :else
    (format "[%s]" (:name card))))

(defn list-cards [cards selected-cards]
  (let [available (remove #(contains? @selected-cards (:id %)) cards)]
    (doseq [[idx card] (map-indexed vector available)]
      (let [tier-info (when (and (:tier card) (= (:category card) :hero))
                        (str "[" (-> (:category card) name str/capitalize)
                             " " (-> (:tier card) name str/upper-case) "-Tier] "))
            type-info (when (and (:type card) (not (:tier card)) (not= (:category card) :hero))
                        (str "[" (-> (:type card) name str/capitalize) "] "))]
        (println (str (inc idx) ". " (or tier-info type-info) (:name card) " - " (:description card)))))))

(defn print-heroes [heroes]
  (doseq [[id hero] (map-indexed vector heroes)]
    (println (str (inc id) ". " (:name hero) " " @(:current-hp hero) " HP"))))

(defn print-playable-cards [playable]
  (println "Choose a card to play:")
  (doseq [[i card] (map-indexed vector playable)]
    (println (str (inc i) ". " (format-card card))))
  (println (str (inc (count playable)) ". End Selection Phase")))

(defn print-field [field orientation]
  (let [[top bottom]
        (if (= orientation :hero-bottom)
          [:action :hero]
          [:hero :action])]
    (doseq [slot field]
      (print (if-let [c (get slot top)]
               (str (print-card c) " ")
               "[    ] ")))
    (println)
    (doseq [slot field]
      (print (if-let [c (get slot bottom)]
               (str (print-card c) " ")
               "[    ] ")))
    (println)))

(defn display-board [blue-field red-field n]
  (print-field @red-field :hero-bottom)
  (println (apply str (repeat (* n 6) "=")))
  (print-field @blue-field :hero-top)
  (println))

(defn show-board-for-player [player-name field enemy-field n]
  (display-board
   (if (= player-name "BLUE") field enemy-field)
   (if (= player-name "BLUE") enemy-field field)
   n))

(defn show-draw [player-name card]
  (println (str player-name " draws: " (:name card))))

(defn print-card-play [player-name card msg]
  (println (format "\n%s %s: %s\n" player-name msg (card-display-name card))))

(defn print-player-hp [player-name hp]
  (println (format "%s PLAYER HEALTH: %d HP" player-name @hp)))

(defn print-player-turn [player-name player-hp]
  (println "\n==============================")
  (println (str ">>> " player-name " PLAYER TURN <<<"))
  (println "==============================")
  (print-player-hp player-name player-hp))

(defn show-selection-header [player-name]
  (println (str "\n--- " player-name " SELECTION PHASE ---\n")))

(defn print-attackers [available-attackers]
  (println "\nSelect a hero to attack:")
  (doseq [[idx hero] (map-indexed vector available-attackers)]
    (println (str (inc idx) ". " (:name hero) " " @(:current-hp hero) " HP")))
  (println (str (inc (count available-attackers)) ". End Attack Phase")))