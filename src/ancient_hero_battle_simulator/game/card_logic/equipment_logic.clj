(ns ancient-hero-battle-simulator.game.card-logic.equipment-logic
  (:require
   [ancient-hero-battle-simulator.game.game-state :as state]
   [ancient-hero-battle-simulator.game.utilility :as util]))

(def stat-map
  {:increase-power        :power
   :increase-defense      :defense
   :increase-agility      :agility
   :increase-intelligence :intelligence})

(defn apply-equipment-to-hero! [hero effect]
  (swap! (:current-stats hero)
         (fn [stats]
           (reduce
            (fn [s [k v]]
              (if-let [stat (stat-map k)]
                (update s stat + v)
                s))
            stats
            effect))))

(defn apply-equipment-effect! [card field]
  (let [heroes (state/heroes-on-field @field)]
    (if (seq heroes)
      (let [target (util/choose-hero heroes "Your Hero" "to equip")
            updated (update target :equipment (fnil conj []) card)]
        (state/update-hero-on-field! field updated)
        (apply-equipment-to-hero! updated (:effect card))
        (println (format "\n%s equips %s!" (:name target) (:name card)))
        true)
      (do
        (println "\nNo heroes to equip!\n")
        false))))