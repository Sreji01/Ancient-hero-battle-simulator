(ns ancient-hero-battle-simulator.cards.equipment)

(def equipment
  [;; --- A TIER EQUIPMENT (+10 / +5) ---
   {:id 1
    :name "Sword of Might"
    :category :equipment
    :tier :a
    :effect {:increase-power 10}
    :description "A sharp blade that increases a hero's power by 10."}

   {:id 2
    :name "Shield of Fortitude"
    :category :equipment
    :tier :a
    :effect {:increase-defense 10}
    :description "A sturdy shield that increases a hero's defense by 10."}

   {:id 3
    :name "Boots of Swiftness"
    :category :equipment
    :tier :a
    :effect {:increase-agility 10}
    :description "Enchanted boots that increase a hero's agility by 10."}

   {:id 4
    :name "Helm of Wisdom"
    :category :equipment
    :tier :a
    :effect {:increase-intelligence 10}
    :description "A crown of insight that increases a hero's intelligence by 10."}

   {:id 5
    :name "Armor of Vitality"
    :category :equipment
    :tier :a
    :effect {:increase-health 10}
    :description "Reinforced plate that increases a hero's health by 10."}

   {:id 6
    :name "Banner of Heroes"
    :category :equipment
    :tier :a
    :effect {:increase-power 5
             :increase-defense 5
             :increase-health 5
             :increase-agility 5
             :increase-intelligence 5}
    :description "A legendary standard that increases all of a hero's stats by 5."}

   ;; --- B TIER EQUIPMENT (+5 / +2.5) ---
   {:id 7
    :name "Bronze Gladius"
    :category :equipment
    :tier :b
    :effect {:increase-power 5}
    :description "A reliable bronze blade that increases a hero's power by 5."}

   {:id 8
    :name "Aspis Shield"
    :category :equipment
    :tier :b
    :effect {:increase-defense 5}
    :description "A well-crafted shield that increases a hero's defense by 5."}

   {:id 9
    :name "Wind-Step Sandals"
    :category :equipment
    :tier :b
    :effect {:increase-agility 5}
    :description "Light footwear that increases a hero's agility by 5."}

   {:id 10
    :name "Strategist's Circlet"
    :category :equipment
    :tier :b
    :effect {:increase-intelligence 5}
    :description "A simple circlet that increases a hero's intelligence by 5."}

   {:id 11
    :name "Cuirass of the Guard"
    :category :equipment
    :tier :b
    :effect {:increase-health 5}
    :description "Protective armor that increases a hero's health by 5."}

   {:id 12
    :name "Standard of the Legion"
    :category :equipment
    :tier :b
    :effect {:increase-power 2.5
             :increase-defense 2.5
             :increase-health 2.5
             :increase-agility 2.5
             :increase-intelligence 2.5}
    :description "A battle standard that increases all of a hero's stats by 2.5."}])
