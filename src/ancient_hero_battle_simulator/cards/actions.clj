(ns ancient-hero-battle-simulator.cards.actions)

(def actions
  [{:id 1
    :name "Power Strike"
    :category :action
    :type :attack
    :effect {:damage 25}
    :description "Deal 25 damage to target enemy hero."}

   {:id 2
    :name "Battle Surge"
    :category :action
    :type :attack
    :effect {:damage-all-enemies 10}
    :description "Deal 10 damage to each enemy hero."}

   {:id 3
    :name "Reckless Strike"
    :category :action
    :type :attack
    :effect {:probabilities [{:chance 50 :target :enemy :damage 75}
                             {:chance 50 :target :self  :damage 75}]}
    :description "Risky attack: Deal 50 damage to yourself or enemy."}

   {:id 4
    :name "Assassinate"
    :category :action
    :type :attack
    :effect {:player-damage 50}
    :description "Deal 50 damage directly to the enemy player."}

   {:id 5
    :name "Shield Wall"
    :category :action
    :type :defense
    :effect {:reduce-damage 15}
    :description "Reduce all damage taken this turn by 15."}

   {:id 6
    :name "Fortify"
    :category :action
    :type :defense
    :effect {:increase-defense 20}
    :description "Increase a hero's defense by 20 for one turn."}

   {:id 7
    :name "Dodge Roll"
    :category :action
    :type :defense
    :effect {:evade 50}
    :description "Target hero has 50% chance to evade the next attack."}

   {:id 8
    :name "Parry"
    :category :action
    :type :defense
    :effect {:reflect-damage 10}
    :description "Reflect 10 damage back to attacker on next hit."}

   {:id 9
    :name "Heal"
    :category :action
    :type :heal
    :effect {:restore 30}
    :description "Restore 30 HP to target hero."}

   {:id 10
    :name "Divine Favor"
    :category :action
    :type :heal
    :effect {:restore 10}
    :description "Restore 10 HP to all friendly heroes."}

   {:id 11
    :name "Weaken"
    :category :action
    :type :debuff
    :effect {:reduce-power 15}
    :description "Reduce target enemy hero's power by 15 for one turn."}

   {:id 12
    :name "Cripple Armor"
    :category :action
    :type :debuff
    :effect {:reduce-defense 15}
    :description "Reduce target enemy hero's defense by 15 for one turn."}

   {:id 13
    :name "Tactical Retreat"
    :category :action
    :type :utility
    :effect {:remove-hero true}
    :description "Return a hero to your hand, avoiding damage this turn."}

   {:id 14
    :name "Inspire"
    :category :action
    :type :utility
    :effect {:increase-power 15}
    :description "Increase all allied heroes' power by 15 for one turn."}

   {:id 15
    :name "Defensive Formation"
    :category :action
    :type :utility
    :effect {:increase-defense 15}
    :description "Increase all allied heroes' defense by 15 for one turn."}

   {:id 16
    :name "Tactical Focus"
    :category :action
    :type :utility
    :effect {:draw 2}
    :description "Draw 2 cards."}])
