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
    :name "Assassinate"
    :category :action
    :type :attack
    :effect {:player-damage 30}
    :description "Deal 30 damage directly to the enemy player."}

   {:id 4
    :name "Shield Wall"
    :category :action
    :type :defense
    :effect {:reduce-damage 10}
    :description "Reduce all damage taken by 10."}

   {:id 5
    :name "Dodge Roll"
    :category :action
    :type :defense
    :effect {:evade 50}
    :description "Target hero has a 50% chance to evade the next attack."}

   {:id 6
    :name "Last Stand"
    :category :action
    :type :defense
    :effect {:reduce-damage 25}
    :description "Reduce damage taken by a hero by 25."}

   {:id 7
    :name "Heal"
    :category :action
    :type :heal
    :effect {:restore 30}
    :description "Restore 30 HP to target hero."}

   {:id 8
    :name "Divine Favor"
    :category :action
    :type :heal
    :effect {:restore 10}
    :description "Restore 10 HP to all friendly heroes."}

   {:id 9
    :name "Vital Surge"
    :category :action
    :type :buff
    :effect {:increase-health 20}
    :description "Increase a hero's health by 20."}

   {:id 10
    :name "Iron Resolve"
    :category :action
    :type :buff
    :effect {:increase-defense 20}
    :description "Increase a hero's defense by 20."}

   {:id 11
    :name "Overpower"
    :category :action
    :type :buff
    :effect {:increase-power 20}
    :description "Increase a hero's power by 20."}

   {:id 12
    :name "Battle Insight"
    :category :action
    :type :buff
    :effect {:increase-intelligence 20}
    :description "Increase a hero's intelligence by 20."}

   {:id 13
    :name "Adrenal Rush"
    :category :action
    :type :buff
    :effect {:increase-agility 20}
    :description "Increase a hero's agility by 20."}

   {:id 14
    :name "Tactical Retreat"
    :category :action
    :type :utility
    :effect {:remove-hero true}
    :description "Return a hero to your hand, avoiding damage."}

   {:id 15
    :name "Tactical Focus"
    :category :action
    :type :utility
    :effect {:draw 2}
    :description "Draw 2 cards."}

   {:id 16
    :name "Mind Control"
    :category :action
    :type :utility
    :effect {:take-control :enemy-hero}
    :description "Take control of an enemy hero."}])
