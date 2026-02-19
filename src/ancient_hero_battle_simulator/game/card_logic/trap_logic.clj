(ns ancient-hero-battle-simulator.game.card-logic.trap-logic
  (:require [ancient-hero-battle-simulator.game.game-state :as state]
            [clojure.string :as str]))

(defn apply-snare-trap!
  [card hero enemy-field]
  (let [turns (get-in card [:effect :stun])]
    (swap! (:current-stats hero) assoc :stunned? true :stun-rounds turns)
    (state/remove-hero-from-field! enemy-field hero)
    (state/place-hero-on-field! enemy-field hero)
    (println (format "\n[TRAP] %s stuns %s for %d turn!\n"
                     (:name card) (:name hero) turns))))

(defn apply-trap-of-confusion!
  [card hero field enemy-field player-name]
  (let [original-owner-kw (if (= player-name "BLUE") :red :blue)
        new-owner-kw (if (= player-name "BLUE") :blue :red)
        controlled (assoc hero
                          :controlled true
                          :control-rounds 1
                          :original-owner original-owner-kw
                          :owner new-owner-kw)]
    (if (state/place-hero-on-field! field controlled)
      (do
        (state/remove-hero-from-field! enemy-field hero)
        (println (format "\n[TRAP] %s takes control of %s for 1 turn!\n"
                         (:name card) (:name hero))))
      (println "No space to take control of hero!"))))

(defn apply-spike-pit!
  [card hero enemy-field]
  (let [dmg (get-in card [:effect :damage])]
    (swap! (:current-hp hero) #(max 0 (- % dmg)))
    (println (format "\n[TRAP] %s deals %d damage to %s!\n"
                     (:name card) dmg (:name hero)))
    (state/check-and-remove-dead! hero enemy-field)))

(defn apply-enemy-hero-placed-trap!
  [card field enemy-field player-name placed-hero]
  (case (:type card)
    :damage  (apply-spike-pit! card placed-hero enemy-field)
    :stun    (apply-snare-trap! card placed-hero enemy-field)
    :control (apply-trap-of-confusion! card placed-hero field enemy-field player-name)
    (println "Unknown trap type.")))

(defn confirm? [message]
  (print message)
  (flush)
  (let [input (str/lower-case (read-line))]
    (= input "y")))

(defn handle-enemy-hero-placed!
  [defender-field attacker-field placed-hero defender-name]
  (let [traps (state/traps-with-trigger defender-field :enemy-hero-placed)]
    (reduce (fn [activated? trap]
              (if (confirm? (str "Activate trap: " (:name trap) "? (y/n)\n"))
                (do
                  (apply-enemy-hero-placed-trap! trap defender-field attacker-field defender-name placed-hero)
                  (state/remove-trap-from-field! defender-field trap)
                  true)
                activated?))
            false
            traps)))