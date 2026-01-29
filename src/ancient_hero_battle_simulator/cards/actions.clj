(ns ancient-hero-battle-simulator.cards.actions)

(def actions
  [{:id 201
    :name "Power Strike"
    :category :action
    :type :attack
    :effect {:damage 25}
    :description "Deal 25 damage to target enemy hero."}

   {:id 202
    :name "Battle Surge"
    :category :action
    :type :attack
    :effect {:damage-all-enemies 10}
    :description "Deal 10 damage to each enemy hero."}

   {:id 203
    :name "Assassinate"
    :category :action
    :type :attack
    :effect {:player-damage 30}
    :description "Deal 30 damage directly to the enemy player."}

   {:id 204
    :name "Shield Wall"
    :category :action
    :type :defense
    :effect {:reduce-damage-all-enemies 10}
    :description "Reduce all damage taken by 10."}

   {:id 205
    :name "Dodge Roll"
    :category :action
    :type :defense
    :effect {:evade 50}
    :description "Target hero has a 50% chance to evade the next attack."}

   {:id 206
    :name "Last Stand"
    :category :action
    :type :defense
    :effect {:reduce-damage 25}
    :description "Reduce damage taken by a hero by 25."}

   {:id 207
    :name "Heal"
    :category :action
    :type :heal
    :effect {:restore 30}
    :description "Restore 30 HP to target hero."}

   {:id 208
    :name "Divine Favor"
    :category :action
    :type :heal
    :effect {:restore-all-allies 10}
    :description "Restore 10 HP to all friendly heroes."}

   {:id 209
    :name "Vital Surge"
    :category :action
    :type :buff
    :effect {:increase-health 20}
    :description "Increase a hero's health by 20."}

   {:id 210
    :name "Iron Resolve"
    :category :action
    :type :buff
    :effect {:increase-defense 20}
    :description "Increase a hero's defense by 20."}

   {:id 211
    :name "Overpower"
    :category :action
    :type :buff
    :effect {:increase-power 20}
    :description "Increase a hero's power by 20."}

   {:id 212
    :name "Battle Insight"
    :category :action
    :type :buff
    :effect {:increase-intelligence 20}
    :description "Increase a hero's intelligence by 20."}

   {:id 213
    :name "Adrenal Rush"
    :category :action
    :type :buff
    :effect {:increase-agility 20}
    :description "Increase a hero's agility by 20."}

   {:id 214
    :name "Tactical Retreat"
    :category :action
    :type :utility
    :effect {:remove-hero true}
    :description "Return a hero to your hand, avoiding damage."}

   {:id 215
    :name "Tactical Focus"
    :category :action
    :type :utility
    :effect {:draw 2}
    :description "Draw 2 cards."}

   {:id 216
    :name "Mind Control"
    :category :action
    :type :utility
    :effect {:take-control :enemy-hero}
    :description "Take control of an enemy hero."}])