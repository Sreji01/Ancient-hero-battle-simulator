(ns ancient-hero-battle-simulator.core-test
  (:require [midje.sweet :refer [fact facts =>]]
            [clojure.core :refer [with-redefs]]
            [ancient-hero-battle-simulator.game.game-state :as state]
            [ancient-hero-battle-simulator.game.utilility :as util]
            [ancient-hero-battle-simulator.game.card-logic.action-logic :as action-logic]
            [ancient-hero-battle-simulator.game.card-logic.trap-logic :as trap-logic]
            [ancient-hero-battle-simulator.cards.actions :as card-actions]))

(facts "Hero life state"
       (fact "hero is alive when hp > 0"
             (state/alive? {:current-hp (atom 10)}) => true)

       (fact "hero is dead when hp is 0"
             (state/dead? {:current-hp (atom 0)}) => true)

       (fact "dead hero is not alive"
             (state/alive? {:current-hp (atom 0)}) => false))

(fact "Power Strike deals targeted damage to a single enemy hero"
      (let [hero1 {:name "H1" :current-hp (atom 50)}
            hero2 {:name "H2" :current-hp (atom 50)}
            enemy-field (atom [{:hero hero1} {:hero hero2}])
            enemy-hp (atom 200)
            power-strike (first (filter #(= (:name %) "Power Strike") card-actions/actions))]
        (with-redefs [read-line (fn [] "1")]
          (action-logic/apply-damage-effect! power-strike enemy-field enemy-hp))
        @(:current-hp hero1) => 25
        @(:current-hp hero2) => 50
        @enemy-hp => 200))

(fact "Battle Surge deals AOE damage to all enemy heroes"
      (let [hero1 {:name "H1" :current-hp (atom 80)}
            hero2 {:name "H2" :current-hp (atom 50)}
            enemy-field (atom [{:hero hero1} {:hero hero2}])
            enemy-hp (atom 200)
            battle-surge (first (filter #(= (:name %) "Battle Surge") card-actions/actions))]
        (action-logic/apply-damage-effect! battle-surge enemy-field enemy-hp)
        @(:current-hp hero1) => 70
        @(:current-hp hero2) => 40
        @enemy-hp => 200))

(fact "Assassinate deals direct damage to enemy player HP"
      (let [enemy-hp (atom 100)
            assassinate (first (filter #(= (:name %) "Assassinate") card-actions/actions))]
        (action-logic/apply-damage-effect! assassinate (atom []) enemy-hp)
        @enemy-hp => 70))

(fact "Heal restores 30 HP to a selected ally hero"
      (let [hero {:name "H1" :stats {:health 100} :current-hp (atom 40)}
            field (atom [{:hero hero}])
            heal (first (filter #(= (:name %) "Heal") card-actions/actions))]
        (with-redefs [read-line (fn [] "1")]
          (action-logic/apply-heal-effect! heal field))
        @(:current-hp hero) => 70))

(fact "Divine Favor restores HP to all allied heroes"
      (let [hero1 {:name "H1" :stats {:health 100} :current-hp (atom 50)}
            hero2 {:name "H2" :stats {:health 80} :current-hp (atom 75)}
            field (atom [{:hero hero1} {:hero hero2}])
            divine-favor (first (filter #(= (:name %) "Divine Favor") card-actions/actions))]
        (action-logic/apply-heal-effect! divine-favor field)
        @(:current-hp hero1) => 60
        @(:current-hp hero2) => 80))

(fact "Vital Surge increases hero health by 20"
      (let [hero {:name "H1"
                  :current-hp (atom 100)
                  :current-stats (atom {:health 100 :defense 10 :power 20 :intelligence 5 :agility 5})}
            field (atom [{:hero hero}])
            vital-surge {:name "Vital Surge"
                         :type :buff
                         :category :action
                         :effect {:increase-health 20}}]
        (with-redefs [read-line (fn [] "1")]
          (action-logic/apply-buff-effect! vital-surge field))
        (get @(:current-stats hero) :health) => 120))

(fact "Iron Resolve increases hero defense by 20"
      (let [hero {:name "H1"
                  :current-hp (atom 100)
                  :current-stats (atom {:health 100 :defense 30 :power 20 :intelligence 5 :agility 5})}
            field (atom [{:hero hero}])
            iron-resolve {:name "Iron Resolve"
                          :type :buff
                          :category :action
                          :effect {:increase-defense 20}}]
        (with-redefs [read-line (fn [] "1")]
          (action-logic/apply-buff-effect! iron-resolve field))
        (get @(:current-stats hero) :defense) => 50))

(fact "Overpower increases hero power by 20"
      (let [hero {:name "H1"
                  :current-hp (atom 100)
                  :current-stats (atom {:health 100 :defense 10 :power 50 :intelligence 5 :agility 5})}
            field (atom [{:hero hero}])
            overpower {:name "Overpower"
                       :type :buff
                       :category :action
                       :effect {:increase-power 20}}]
        (with-redefs [read-line (fn [] "1")]
          (action-logic/apply-buff-effect! overpower field))
        (get @(:current-stats hero) :power) => 70))

(fact "Battle Insight increases hero intelligence by 20"
      (let [hero {:name "H1"
                  :current-hp (atom 100)
                  :current-stats (atom {:health 100 :defense 10 :power 20 :intelligence 10 :agility 5})}
            field (atom [{:hero hero}])
            battle-insight {:name "Battle Insight"
                            :type :buff
                            :category :action
                            :effect {:increase-intelligence 20}}]
        (with-redefs [read-line (fn [] "1")]
          (action-logic/apply-buff-effect! battle-insight field))
        (get @(:current-stats hero) :intelligence) => 30))

(fact "Adrenal Rush increases hero agility by 20"
      (let [hero {:name "H1"
                  :current-hp (atom 100)
                  :current-stats (atom {:health 100 :defense 10 :power 20 :intelligence 5 :agility 20})}
            field (atom [{:hero hero}])
            adrenal-rush {:name "Adrenal Rush"
                          :type :buff
                          :category :action
                          :effect {:increase-agility 20}}]
        (with-redefs [read-line (fn [] "1")]
          (action-logic/apply-buff-effect! adrenal-rush field))
        (get @(:current-stats hero) :agility) => 40))

(fact "Skip Attack marks enemy hero to skip next attack"
      (let [hero (state/init-hero
                  {:id 1
                   :name "H1"
                   :stats {:health 100 :power 10 :defense 5 :agility 20 :intelligence 10}})
            field (atom [{:hero hero}])]
        (with-redefs [util/choose-hero (fn [_ _ _] hero)]
          (action-logic/apply-skip-attack! field))
        (:skip-attack? (:hero (first @field))) => true))

(fact "Mind Control moves hero to other field and marks him controlled"
      (let [hero (state/init-hero {:id 2 :name "H1" :stats {:health 100 :power 10 :defense 5 :agility 20 :intelligence 10}})
            enemy-field (atom [{:hero hero}])
            my-field (atom [{}])] 
        (with-redefs [util/choose-hero (fn [_ _ _] hero)]
          (action-logic/apply-mind-control! {:name "Mind Control"} my-field enemy-field :blue))

        (:hero (first @enemy-field)) => nil
        (:hero (first @my-field)) =not=> nil
        (let [h (:hero (first @my-field))]
          (:controlled h) => true
          (:control-rounds h) => 1
          (:original-owner h) => :red)))

(fact "Mind Control fails when no empty slots available"
      (let [hero (state/init-hero {:id 2 :name "H1" :stats {:health 100 :power 10 :defense 5 :agility 20 :intelligence 10}})
            enemy-field (atom [{:hero hero}])
            my-field (atom [{:hero (state/init-hero {:id 1 :name "H2" :stats {:health 100 :power 10 :defense 5 :agility 20 :intelligence 10}})}])]
        (with-redefs [util/choose-hero (fn [_ _ _] hero)]
          (action-logic/apply-mind-control! {:name "Mind Control"} my-field enemy-field :blue))

        (:hero (first @enemy-field)) =not=> nil
        (:id (:hero (first @my-field))) => 1
        (:controlled (:hero (first @enemy-field))) => nil))

(fact "Cannot place hero when only one free slot remains and enemy has controlled hero"
      (let [controlled-hero (assoc (state/init-hero {:id 3 :name "H3" :stats {:health 100 :power 10 :defense 5 :agility 20 :intelligence 10}})
                                   :controlled true
                                   :control-rounds 1
                                   :original-owner :red)
            my-field (atom [{:hero (state/init-hero {:id 1 :name "H1" :stats {:health 100 :power 10 :defense 5 :agility 20 :intelligence 10}})}
                            {}])
            enemy-field (atom [{:hero controlled-hero}])]
        (state/can-place-hero? my-field enemy-field) => false))

(fact "Draw effect adds cards to hand from deck"
      (let [card {:name "Tactical Focus"
                  :type :utility
                  :category :action
                  :effect {:draw 2}}
            deck (atom [{:id 1 :name "Card1"} {:id 2 :name "Card2"} {:id 3 :name "Card3"}])
            hand (atom [])]
        (action-logic/apply-draw-effect! card hand deck)
        (count @hand) => 2))

(fact "Last Stand applies damage reduction to a selected ally hero"
      (let [hero {:name "H1"
                  :current-stats (atom {:health 100 :defense 20})
                  :current-hp (atom 100)}
            field (atom [{:hero hero}])
            last-stand {:name "Last Stand"
                        :type :defense
                        :category :action
                        :effect {:reduce-damage 15}}]
        (with-redefs [util/choose-hero (fn [_ _ _] hero)]
          (action-logic/apply-last-stand! last-stand field))
        (get @(:current-stats hero) :damage-reduction) => 15))

(fact "Dodge Roll gives an ally a chance to evade next attack"
      (let [hero {:name "H2"
                  :current-stats (atom {:health 100 :defense 10})
                  :current-hp (atom 100)}
            field (atom [{:hero hero}])
            dodge-roll {:name "Dodge Roll"
                        :type :defense
                        :category :action
                        :effect {:evade 50}}]
        (with-redefs [util/choose-hero (fn [_ _ _] hero)]
          (action-logic/apply-dodge-roll! dodge-roll field))
        (get @(:current-stats hero) :evade) => 50))

(fact "Shield Wall reduces damage for all allied heroes on field"
      (let [hero1 {:name "H1" :current-stats (atom {:health 100}) :current-hp (atom 100)}
            hero2 {:name "H2" :current-stats (atom {:health 100}) :current-hp (atom 100)}
            field (atom [{:hero hero1} {:hero hero2}])
            shield-wall {:name "Shield Wall"
                         :type :defense
                         :category :action
                         :effect {:reduce-damage-all-enemies 20}}]
        (action-logic/apply-shield-wall! shield-wall field)
        (get @(:current-stats hero1) :damage-reduction) => 20
        (get @(:current-stats hero2) :damage-reduction) => 20))

       (fact "Spike Pit deals damage to a hero"
             (let [hero {:name "H1" :current-hp (atom 50)}
                   enemy-field (atom [{:hero hero}])
                   spike-pit {:name "Spike Pit"
                              :type :damage
                              :effect {:damage 20}}]
               (trap-logic/apply-spike-pit! spike-pit hero enemy-field)
               @(:current-hp hero) => 30))

       (fact "Snare Trap stuns a hero"
             (let [hero {:name "H2"
                         :current-hp (atom 50)
                         :current-stats (atom {})}
                   enemy-field (atom [{:hero hero}])
                   snare-trap {:name "Snare Trap"
                               :type :stun
                               :category :trap
                               :trigger :enemy-hero-placed
                               :effect {:stun 1}}]
               (trap-logic/apply-snare-trap! snare-trap hero enemy-field)
               (:stunned? @(:current-stats hero)) => true
               (:stun-rounds @(:current-stats hero)) => 1))

(fact "Poison Gas applies damage over time to attacker"
      (let [attacker    {:name          "Attacker1"
                         :current-hp    (atom 100)
                         :current-stats (atom {:health 100 :power 50 :defense 10 :agility 20 :intelligence 10})}
            target      {:name          "Target1"
                         :current-hp    (atom 100)
                         :current-stats (atom {:defense 10})}
            trap        {:id       304
                         :name     "Poison Gas"
                         :category :trap
                         :type     :damage-over-time
                         :trigger  :enemy-attack
                         :effect   {:damage-per-turn 10 :turns 3}}
            enemy-field (atom [{:action trap}])]
        (with-redefs [read-line (fn [] "y")]
          (trap-logic/handle-enemy-attack-traps! enemy-field attacker target))
        (:dot @(:current-stats attacker)) => {:damage 10 :turns 3}))

(fact "Cursed Idol reduces attacking hero power by 20"
      (let [attacker    {:name          "Attacker2"
                         :current-hp    (atom 100)
                         :current-stats (atom {:power 50})}
            target      {:name          "Target2"
                         :current-hp    (atom 100)
                         :current-stats (atom {:defense 10})}
            trap        {:id       305
                         :name     "Cursed Idol"
                         :category :trap
                         :type     :debuff
                         :trigger  :enemy-attack
                         :effect   {:reduce-power 20}}
            enemy-field (atom [{:action trap}])]
        (with-redefs [read-line (fn [] "y")]
          (trap-logic/handle-enemy-attack-traps! enemy-field attacker target))
        (get @(:current-stats attacker) :power) => 30))

(fact "Crippling Guard reduces attacking hero defense by 20"
      (let [attacker    {:name          "Attacker3"
                         :current-hp    (atom 100)
                         :current-stats (atom {:defense 40})}
            target      {:name          "Target3"
                         :current-hp    (atom 100)
                         :current-stats (atom {:defense 10})}
            trap        {:id       306
                         :name     "Crippling Guard"
                         :category :trap
                         :type     :debuff
                         :trigger  :enemy-attack
                         :effect   {:reduce-defense 20}}
            enemy-field (atom [{:action trap}])]
        (with-redefs [read-line (fn [] "y")]
          (trap-logic/handle-enemy-attack-traps! enemy-field attacker target))
        (get @(:current-stats attacker) :defense) => 20))

(fact "Fatigue Curse reduces attacking hero agility by 20"
      (let [attacker    {:name          "Attacker4"
                         :current-hp    (atom 100)
                         :current-stats (atom {:agility 35})}
            target      {:name          "Target4"
                         :current-hp    (atom 100)
                         :current-stats (atom {:defense 10})}
            trap        {:id       307
                         :name     "Fatigue Curse"
                         :category :trap
                         :type     :debuff
                         :trigger  :enemy-attack
                         :effect   {:reduce-agility 20}}
            enemy-field (atom [{:action trap}])]
        (with-redefs [read-line (fn [] "y")]
          (trap-logic/handle-enemy-attack-traps! enemy-field attacker target))
        (get @(:current-stats attacker) :agility) => 15))

(fact "Weaken Mind reduces attacking hero intelligence by 20"
      (let [attacker    {:name          "Attacker5"
                         :current-hp    (atom 100)
                         :current-stats (atom {:intelligence 25})}
            target      {:name          "Target5"
                         :current-hp    (atom 100)
                         :current-stats (atom {:defense 10})}
            trap        {:id       308
                         :name     "Weaken Mind"
                         :category :trap
                         :type     :debuff
                         :trigger  :enemy-attack
                         :effect   {:reduce-intelligence 20}}
            enemy-field (atom [{:action trap}])]
        (with-redefs [read-line (fn [] "y")]
          (trap-logic/handle-enemy-attack-traps! enemy-field attacker target))
        (get @(:current-stats attacker) :intelligence) => 5))

(fact "Siphon Vitality reduces attacking hero health by 20"
      (let [attacker    {:name          "Attacker6"
                         :current-hp    (atom 100)
                         :current-stats (atom {:current-hp 100})}
            target      {:name          "Target6"
                         :current-hp    (atom 100)
                         :current-stats (atom {:defense 10})}
            trap        {:id       309
                         :name     "Siphon Vitality"
                         :category :trap
                         :type     :debuff
                         :trigger  :enemy-attack
                         :effect   {:reduce-health 20}}
            enemy-field (atom [{:action trap}])]
        (with-redefs [read-line (fn [] "y")]
          (trap-logic/handle-enemy-attack-traps! enemy-field attacker target))
        (get @(:current-stats attacker) :current-hp) => 80))

(fact "Mirror Trap reflects 15 damage to attacker"
      (let [attacker    {:name          "Attacker7"
                         :current-hp    (atom 50)
                         :current-stats (atom {})}
            target      {:name          "Target7"
                         :current-hp    (atom 100)
                         :current-stats (atom {:defense 10})}
            trap        {:id       310
                         :name     "Mirror Trap"
                         :category :trap
                         :type     :reflect
                         :trigger  :enemy-attack
                         :effect   {:reflect-damage 15}}
            enemy-field (atom [{:action trap}])]
        (with-redefs [read-line (fn [] "y")]
          (trap-logic/handle-enemy-attack-traps! enemy-field attacker target))
        @(:current-hp attacker) => 35))

(fact "Magic Barrier absorbs 20 damage from next attack"
      (let [attacker    {:name          "Attacker8"
                         :current-hp    (atom 100)
                         :current-stats (atom {})}
            target      {:name          "Target8"
                         :current-hp    (atom 100)
                         :current-stats (atom {:defense 10})}
            trap        {:id       311
                         :name     "Magic Barrier"
                         :category :trap
                         :type     :utility
                         :trigger  :enemy-attack
                         :effect   {:absorb-damage 20}}
            enemy-field (atom [{:action trap}])]
        (with-redefs [read-line (fn [] "y")]
          (trap-logic/handle-enemy-attack-traps! enemy-field attacker target))
        (get @(:current-stats attacker) :absorb) => 20))

(fact "Defender's Mirror sets reflect-attack flag on attacker"
      (let [attacker    {:name          "Attacker9"
                         :current-hp    (atom 100)
                         :current-stats (atom {})}
            target      {:name          "Target9"
                         :current-hp    (atom 100)
                         :current-stats (atom {:defense 10})}
            trap        {:id       312
                         :name     "Defender's Mirror"
                         :category :trap
                         :type     :control
                         :trigger  :enemy-attack
                         :effect   {:reflect-attack true}}
            enemy-field (atom [{:action trap}])]
        (with-redefs [read-line (fn [] "y")]
          (trap-logic/handle-enemy-attack-traps! enemy-field attacker target))
        (get @(:current-stats attacker) :reflect-attack) => true))