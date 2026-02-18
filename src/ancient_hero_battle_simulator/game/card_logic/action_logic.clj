(ns ancient-hero-battle-simulator.game.card-logic.action-logic
  (:require [ancient-hero-battle-simulator.game.game-state :as state]
            [ancient-hero-battle-simulator.game.deck-menagment :as deck-menagment]
            [ancient-hero-battle-simulator.game.utilility :as util]))

(defn apply-mind-control! [card field enemy-field player-color]
  (let [enemies (state/heroes-on-field @enemy-field)]
    (if (seq enemies)
      (let [target (util/choose-hero enemies "Enemy Hero" "to control")
            new-owner (if (= player-color :blue) :red :blue)
            updated-target (assoc target
                                  :controlled true
                                  :control-rounds 1
                                  :original-owner new-owner)]
        (if (state/place-hero-on-field! field updated-target)
          (do
            (state/remove-hero-from-field! enemy-field target)
            (println (format "\n[UTILITY] %s takes control of %s for 1 round!"
                             (:name card) (:name target))))
          (do
            (println "\nNo empty slots to place controlled hero!")
            false)))
      (do (println "No enemy heroes to control!") false))))

(defn apply-draw-effect! [card hand deck]
  (let [n (:draw (:effect card))
        drawn (deck-menagment/draw-from-deck deck n)]
    (println (format "\n[UTILITY] %s draws %d cards!\n" (:name card) n))
    (doseq [c drawn]
      (println (str "+ " (:name c)) "\n"))
    (deck-menagment/add-to-hand! hand drawn)))

(defn apply-skip-attack! [enemy-field]
  (let [enemies (state/heroes-on-field @enemy-field)]
    (if (seq enemies)
      (let [target (util/choose-hero enemies "Enemy Hero" "to skip attack")
            updated-target (assoc target :skip-attack? true)]

        (state/remove-hero-from-field! enemy-field target)
        (state/place-hero-on-field! enemy-field updated-target)

        (println (format "\n[UTILITY] %s will skip their next attack!\n"
                         (:name target))))
      (println "No enemy heroes to affect!"))))

(defn apply-utility-effect! [card field enemy-field hand deck player-name]
  (let [effect (:effect card)
        player-color (if (= player-name "BLUE") :blue :red)]
    (cond
      (:skip-next-attack effect) (apply-skip-attack! enemy-field)
      (:draw effect)             (apply-draw-effect! card hand deck)
      (:take-control effect)     (apply-mind-control! card field enemy-field player-color))))

(defn apply-single-buff!
  [card field stat-key amount stat-label]
  (let [allies (state/heroes-on-field @field)]
    (if (seq allies)
      (let [target (util/choose-hero allies "Your Hero" "to buff")]
        (swap! (:current-stats target) update stat-key + amount)
        (println
         (format "\n[BUFF] %s increases %s's %s by %d for this turn! Current %s: %d\n"
                 (:name card)
                 (:name target)
                 stat-label
                 amount
                 stat-label
                 (get @(:current-stats target) stat-key))))
      (println "No heroes available to buff!"))))

(defn apply-buff-effect! [card field]
  (let [effect (:effect card)]
    (cond
      (:increase-health effect)
      (apply-single-buff! card field :health (:increase-health effect) "health")

      (:increase-defense effect)
      (apply-single-buff! card field :defense (:increase-defense effect) "defense")

      (:increase-power effect)
      (apply-single-buff! card field :power (:increase-power effect) "power")

      (:increase-intelligence effect)
      (apply-single-buff! card field :intelligence (:increase-intelligence effect) "intelligence")

      (:increase-agility effect)
      (apply-single-buff! card field :agility (:increase-agility effect) "agility"))))

(defn heal-hero! [hero amount]
  (let [max-hp (get-in hero [:stats :health])]
    (swap! (:current-hp hero)
           #(min max-hp (+ % amount)))))

(defn apply-heal-effect! [card field]
  (let [effect (:effect card)
        allies (state/heroes-on-field @field)]
    (cond
      (:restore effect)
      (if (seq allies)
        (let [target (util/choose-hero allies "Your Hero" "to heal")
              heal (:restore effect)]
          (heal-hero! target heal)
          (println (format "\n[HEAL] %s restores %d HP to %s!\n"
                           (:name card) heal (:name target))))
        (println "No allies to heal!"))

      (:restore-all-allies effect)
      (let [heal (:restore-all-allies effect)]
        (doseq [hero allies]
          (heal-hero! hero heal))
        (println (format "\n[AOE HEAL] %s restores %d HP to ALL allies!\n"
                         (:name card) heal)))

      :else
      (println "Unknown heal effect."))))

(defn apply-last-stand! [card field]
  (let [allies (state/heroes-on-field @field)
        reduction (:reduce-damage (:effect card))]
    (if (seq allies)
      (let [target (util/choose-hero allies "Your Hero" "to activate Last Stand")]
        (swap! (:current-stats target) update :damage-reduction (fnil + 0) reduction)
        (println (format "\n[DEFENSE] %s reduces damage taken by %s by %d for one turn!\n"
                         (:name card) (:name target) reduction)))
      (println "No allies available for Last Stand!"))))

(defn apply-dodge-roll! [card field]
  (let [allies (state/heroes-on-field @field)
        chance (:evade (:effect card))]
    (if (seq allies)
      (let [target (util/choose-hero allies "Your Hero" "to receive dodge roll")]
        (swap! (:current-stats target) assoc :evade chance)
        (println (format "\n[DEFENSE] %s gives %s a %d%% chance to evade the next attack!\n"
                         (:name card) (:name target) chance)))
      (println "No allies available for Dodge Roll!"))))

(defn apply-shield-wall! [card field]
  (let [allies (state/heroes-on-field @field)
        reduction (:reduce-damage-all-enemies (:effect card))]
    (doseq [hero allies]
      (swap! (:current-stats hero) update :damage-reduction (fnil + 0) reduction))
    (println (format "\n[DEFENSE] %s reduces damage taken by all allies by %d this turn!\n"
                     (:name card) reduction))))

(defn apply-defense-effect! [card field]
  (let [effect (:effect card)]
    (cond
      (:reduce-damage-all-enemies effect) (apply-shield-wall! card field)
      (:evade effect)                     (apply-dodge-roll! card field)
      (:reduce-damage effect)             (apply-last-stand! card field)
      :else (println "Unknown defense effect.")))
  (println (format "Effect for type %s is not yet implemented." (:type card))))

(defn apply-damage-effect! [card enemy-field enemy-player-hp]
  (let [effect (:effect card)]
    (cond
      (:damage effect)
      (let [defenders (state/heroes-on-field @enemy-field)]
        (if (seq defenders)
          (let [target (util/choose-hero defenders "Target Enemy" "to attack")
                dmg (:damage effect)]
            (swap! (:current-hp target) #(max 0 (- % dmg)))
            (println (format "\n[DAMAGE] %s deals %d damage to %s!\n"
                             (:name card) dmg (:name target)))
            (state/check-and-remove-dead! target enemy-field))
          (println "\nNo enemies to target! Effect wasted.")))

      (:damage-all-enemies effect)
      (let [defenders (state/heroes-on-field @enemy-field)
            dmg (:damage-all-enemies effect)]
        (doseq [target defenders]
          (swap! (:current-hp target) #(max 0 (- % dmg)))
          (state/check-and-remove-dead! target enemy-field))
        (println (format "[AOE] %s deals %d damage to ALL enemy heroes!\n"
                         (:name card) dmg)))

      (:player-damage effect)
      (let [dmg (:player-damage effect)]
        (swap! enemy-player-hp #(max 0 (- % dmg)))
        (println (format "[DIRECT] %s deals %d damage to the enemy player!\n"
                         (:name card) dmg))))))

(defn apply-action-effect! [card field enemy-field enemy-player-hp hand deck player-name]
  (case (:type card)
    :attack  (apply-damage-effect! card enemy-field enemy-player-hp)
    :defense (apply-defense-effect! card field)
    :heal    (apply-heal-effect! card field)
    :buff    (apply-buff-effect! card field)
    :utility (apply-utility-effect! card field enemy-field hand deck player-name)
    (println (format "Effect for type %s is not yet implemented." (:type card)))))