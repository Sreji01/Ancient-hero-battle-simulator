(ns ancient-hero-battle-simulator.game.logic
  (:require [ancient-hero-battle-simulator.game.ui :as ui]
            [ancient-hero-battle-simulator.game.game-state :as state]))

(defn heal-hero! [hero amount]
  (let [max-hp (get-in hero [:stats :health])]
    (swap! (:current-hp hero)
           #(min max-hp (+ % amount)))))

(defn read-choice [max]
  (if-let [input (try (Integer/parseInt (read-line)) (catch Exception _ nil))]
    (when (and (>= input 1) (<= input max))
      input)
    nil))

(defn choose-hero [team team-name action-text]
  (println (str "\nSelect a hero from " team-name " Team " action-text " (enter number):"))
  (let [heroes (state/available-heroes team (atom #{}))]
    (loop []
      (ui/print-heroes heroes)
      (if-let [choice (read-choice (count heroes))]
        (heroes (dec choice))
        (do (println "Invalid input.") (recur))))))-

(defn apply-heal-effect [card field]
  (let [effect (:effect card)
        allies (state/heroes-on-field @field)]
    (cond
      (:restore effect)
      (if (seq allies)
        (let [target (choose-hero allies "Your Hero" "to heal")
              heal (:restore effect)]
          (heal-hero! target heal)
          (println (format "[HEAL] %s restores %d HP to %s!\n"
                           (:name card) heal (:name target))))
        (println "\nNo allies to heal!"))

      (:restore-all-allies effect)
      (let [heal (:restore-all-allies effect)]
        (doseq [hero allies]
          (heal-hero! hero heal))
        (println (format "[AOE HEAL] %s restores %d HP to ALL allies!\n"
                         (:name card) heal)))

      :else
      (println "Unknown heal effect."))))

(defn apply-damage-effect [card enemy-field enemy-player-hp]
  (let [effect (:effect card)]
    (cond
      (:damage effect)
      (let [defenders (state/heroes-on-field @enemy-field)]
        (if (seq defenders)
          (let [target (choose-hero defenders "Target Enemy" "to attack")
                dmg (:damage effect)]
            (swap! (:current-hp target) #(max 0 (- % dmg)))
            (println (format "[DAMAGE] %s deals %d damage to %s!\n"
                             (:name card) dmg (:name target))))
          (println "\nNo enemies to target! Effect wasted.")))

      (:damage-all-enemies effect)
      (let [defenders (state/heroes-on-field @enemy-field)
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
  (case (:type card)
    :attack  (apply-damage-effect card enemy-field enemy-player-hp)
    :defense (println "Defense effects coming soon...\n")
    :heal    (apply-heal-effect card field)
    :buff    (println "Buff effects coming soon...\n")
    :utility (println "Utility effects coming soon...\n")
    (println (format "Effect for type %s is not yet implemented." (:type card)))))

(defn apply-card-effect! [card field enemy-field enemy-player-hp]
  (when (= (:category card) :action)
    (apply-action-effect card field enemy-field enemy-player-hp)))

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

(defn perform-attack [player-name attacker enemy-field enemy-player-hp]
  (let [defenders (state/heroes-on-field @enemy-field)
        defender  (choose-hero defenders
                               (if (= player-name "BLUE") "Red" "Blue")
                               "to attack")]
    (attack attacker defender enemy-player-hp)))