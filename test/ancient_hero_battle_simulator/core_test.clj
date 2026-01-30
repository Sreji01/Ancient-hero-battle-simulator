(ns ancient-hero-battle-simulator.core-test
  (:require [midje.sweet :refer [fact facts =>]]
            [clojure.core :refer [with-redefs]]
            [ancient-hero-battle-simulator.core :as core]
            [ancient-hero-battle-simulator.cards.actions :as card-actions]))

(facts "Hero life state"
       (fact "hero is alive when hp > 0"
             (core/alive? {:current-hp (atom 10)}) => true)

       (fact "hero is dead when hp is 0"
             (core/dead? {:current-hp (atom 0)}) => true)

       (fact "dead hero is not alive"
             (core/alive? {:current-hp (atom 0)}) => false))

(fact "Assassinate deals direct damage to enemy player HP"
      (let [enemy-hp (atom 100)
            assassinate (first (filter #(= (:name %) "Assassinate") card-actions/actions))]
        (core/apply-damage-effect assassinate (atom []) enemy-hp)
        @enemy-hp => 70))

(fact "Battle Surge deals AOE damage to all enemy heroes"
      (let [hero1 {:name "H1" :current-hp (atom 80)}
            hero2 {:name "H2" :current-hp (atom 50)}
            enemy-field (atom [{:hero hero1} {:hero hero2}])
            enemy-hp (atom 200)
            battle-surge (first (filter #(= (:name %) "Battle Surge") card-actions/actions))]
        (core/apply-damage-effect battle-surge enemy-field enemy-hp)
        @(:current-hp hero1) => 70
        @(:current-hp hero2) => 40
        @enemy-hp => 200))

(fact "Power Strike deals targeted damage to a single enemy hero"
      (let [hero1 {:name "H1" :current-hp (atom 50)}
            hero2 {:name "H2" :current-hp (atom 50)}
            enemy-field (atom [{:hero hero1} {:hero hero2}])
            enemy-hp (atom 200)
            power-strike (first (filter #(= (:name %) "Power Strike") card-actions/actions))]
        (with-redefs [read-line (fn [] "1")]
          (core/apply-damage-effect power-strike enemy-field enemy-hp))
        @(:current-hp hero1) => 25
        @(:current-hp hero2) => 50
        @enemy-hp => 200))

(fact "Heal restores 30 HP to a selected ally hero"
      (let [hero {:name "H1"
                  :stats {:health 100}
                  :current-hp (atom 40)}
            field (atom [{:hero hero}])
            heal (first (filter #(= (:name %) "Heal")
                                card-actions/actions))]

        (with-redefs [read-line (fn [] "1")]
          (core/apply-heal-effect heal field))

        @(:current-hp hero) => 70
        ))

(fact "Divine Favor restores HP to all allied heroes"
      (let [hero1 {:name "H1"
                   :stats {:health 100}
                   :current-hp (atom 50)}
            hero2 {:name "H2"
                   :stats {:health 80}
                   :current-hp (atom 75)}
            field (atom [{:hero hero1} {:hero hero2}])
            divine-favor (first (filter #(= (:name %) "Divine Favor")
                                        card-actions/actions))]

        (core/apply-heal-effect divine-favor field)

        @(:current-hp hero1) => 60   ;
        @(:current-hp hero2) => 80)) ; 