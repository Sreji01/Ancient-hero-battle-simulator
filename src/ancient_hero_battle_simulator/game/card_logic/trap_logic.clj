(ns ancient-hero-battle-simulator.game.card-logic.trap-logic
  (:require [ancient-hero-battle-simulator.game.game-state :as state]
            [clojure.string :as str]))

(defn apply-dot-effects! [field]
  (doseq [hero (state/heroes-on-field @field)]
    (when-let [dot (get @(:current-stats hero) :dot)]
      (let [{:keys [damage turns]} dot]
        (println (format "\n[DOT] %s takes %d damage! (%d turns remaining)"
                         (:name hero) damage turns))
        (swap! (:current-hp hero) #(max 0 (- % damage)))
        (let [new-turns (dec turns)]
          (if (zero? new-turns)
            (do
              (swap! (:current-stats hero) dissoc :dot)
              (println (format "[DOT] %s is no longer poisoned!" (:name hero))))
            (swap! (:current-stats hero) assoc :dot {:damage damage :turns new-turns})))
        (state/check-and-remove-dead! hero field)))))

(defn apply-enemy-attack-trap! [trap attacker target]
  (case (:type trap)
    :damage-over-time
    (let [{:keys [damage-per-turn turns]} (:effect trap)]
      (swap! (:current-stats attacker) assoc :dot {:damage damage-per-turn :turns turns})
      (println (format "\n[TRAP] %s applies %d damage over %d turns to %s\n"
                       (:name trap) damage-per-turn turns (:name attacker))))

    :debuff
    (let [original-stats @(:current-stats attacker)]
      (doseq [[stat key] [[:power :reduce-power]
                          [:defense :reduce-defense]
                          [:agility :reduce-agility]
                          [:intelligence :reduce-intelligence]
                          [:current-hp :reduce-health]]]
        (when-let [v (get-in trap [:effect key])]
          (swap! (:current-stats attacker) update stat - v)
          (println (format "\n[TRAP] %s reduces %s of %s by %d\n"
                           (:name trap) (name stat) (:name attacker) v))))
      (let [tar-stats    @(:current-stats target)
            reduction    (* (:defense tar-stats 0) 0.5)
            orig-power   (:power original-stats 0) 
            new-power    (:power @(:current-stats attacker) 0)
            original-dmg (int (max 5 (- orig-power reduction)))
            debuffed-dmg (int (max 5 (- new-power reduction)))]
        (println (format "\n[INFO] %s would have dealt %d damage, now deals %d damage\n"
                         (:name attacker) original-dmg debuffed-dmg))))

    :reflect
    (let [{:keys [reflect-damage]} (:effect trap)]
      (swap! (:current-hp attacker) #(max 0 (- % reflect-damage)))
      (println (format "\n[TRAP] %s reflects %d damage to attacker %s\n"
                       (:name trap) reflect-damage (:name attacker))))

    :utility
    (let [{:keys [absorb-damage]} (:effect trap)]
      (swap! (:current-stats attacker) assoc :absorb absorb-damage)
      (println (format "\n[TRAP] %s absorbs %d damage from next attack\n"
                       (:name trap) absorb-damage)))

    :control
    (let [{:keys [reflect-attack]} (:effect trap)]
      (when reflect-attack
        (swap! (:current-stats attacker) assoc :reflect-attack true)
        (println (format "\n[TRAP] %s will reflect next attack back to attacker %s\n"
                         (:name trap) (:name attacker)))))

    (println "Unknown trap type.")))

(defn confirm? [message]
  (print message)
  (flush)
  (let [input (str/lower-case (read-line))]
    (= input "y")))

(defn handle-enemy-attack-traps! [defender-field attacker target]
  (let [traps (state/traps-with-trigger defender-field :enemy-attack)]
    (reduce (fn [activated? trap]
              (if (confirm? (str "\nActivate trap: " (:name trap) "? (y/n)\n"))
                (do
                  (apply-enemy-attack-trap! trap attacker target)
                  (state/remove-trap-from-field! defender-field trap)
                  true)
                activated?))
            false
            traps)))

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