(ns ancient-hero-battle-simulator.cards.equipment)

(def equipment
  [{:id 1
    :name "Sword of Might"
    :effect {:increase-power 15}
    :description "Increase a hero's power by 15"}

   {:id 2
    :name "Shield of Fortitude"
    :effect {:increase-defense 15}
    :description "Increase a hero's defense by 15"}

   {:id 3
    :name "Boots of Swiftness"
    :effect {:increase-agility 15}
    :description "Increase a hero's agility by 15"}

   {:id 4
    :name "Helm of Wisdom"
    :effect {:increase-intelligence 15}
    :description "Increase a hero's intelligence by 15"}

   {:id 5
    :name "Armor of Vitality"
    :effect {:increase-health 20}
    :description "Increase a hero's health by 20"}

   {:id 6
    :name "Banner of Heroes"
    :effect {:increase-power 5
             :increase-defense 5
             :increase-health 5
             :increase-agility 5
             :increase-intelligence 5}
    :description "Moderately increases all stats by 5."}])
