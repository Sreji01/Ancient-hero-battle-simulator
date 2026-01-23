(ns ancient-hero-battle-simulator.core-test
  (:require [midje.sweet :refer :all]
            [ancient-hero-battle-simulator.core :refer :all]))

(facts "Hero life state"

       (fact "hero is alive when hp > 0"
             (alive? {:current-hp (atom 10)}) => true)

       (fact "hero is dead when hp is 0"
             (dead? {:current-hp (atom 0)}) => true)

       (fact "dead hero is not alive"
             (alive? {:current-hp (atom 0)}) => false))