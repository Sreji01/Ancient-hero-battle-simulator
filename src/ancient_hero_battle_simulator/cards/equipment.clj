(ns ancient-hero-battle-simulator.cards.equipment)

(def equipment
  [;; --- A TIER EQUIPMENT (+20 / +8) ---
   {:id 1
    :name "Sword of Might"
    :category :equipment
    :tier :a
    :effect {:increase-power 20}
    :description "A sharp blade that increases a hero's power by 20."}

   {:id 2
    :name "Shield of Fortitude"
    :category :equipment
    :tier :a
    :effect {:increase-defense 20}
    :description "A sturdy shield that increases a hero's defense by 20."}

   {:id 3
    :name "Boots of Swiftness"
    :category :equipment
    :tier :a
    :effect {:increase-agility 20}
    :description "Enchanted boots that increase a hero's agility by 20."}

   {:id 4
    :name "Helm of Wisdom"
    :category :equipment
    :tier :a
    :effect {:increase-intelligence 20}
    :description "A crown of insight that increases a hero's intelligence by 20."}

   {:id 5
    :name "Armor of Vitality"
    :category :equipment
    :tier :a
    :effect {:increase-health 20}
    :description "Reinforced plate that increases a hero's health by 20."}

   {:id 6
    :name "Banner of Heroes"
    :category :equipment
    :tier :a
    :effect {:increase-power 8
             :increase-defense 8
             :increase-health 8
             :increase-agility 8
             :increase-intelligence 8}
    :description "A legendary standard that increases ALL stats by 8."}

   ;; --- B TIER EQUIPMENT (+10 / +4) ---
   {:id 7
    :name "Bronze Gladius"
    :category :equipment
    :tier :b
    :effect {:increase-power 10}
    :description "Reliable bronze infantry sword. Power +10."}

   {:id 8
    :name "Aspis Shield"
    :category :equipment
    :tier :b
    :effect {:increase-defense 10}
    :description "Heavy hoplite shield made of wood and bronze. Defense +10."}

   {:id 9
    :name "Wind-Step Sandals"
    :category :equipment
    :tier :b
    :effect {:increase-agility 10}
    :description "Finely crafted leather sandals for rapid movement. Agility +10."}

   {:id 10
    :name "Strategist's Circlet"
    :category :equipment
    :tier :b
    :effect {:increase-intelligence 10}
    :description "A metal band worn by war councilors. Intelligence +10."}

   {:id 11
    :name "Cuirass of the Guard"
    :category :equipment
    :tier :b
    :effect {:increase-health 10}
    :description "Standard issue breastplate for elite guards. Health +10."}

   {:id 12
    :name "Standard of the Legion"
    :category :equipment
    :tier :b
    :effect {:increase-power 4
             :increase-defense 4
             :increase-health 4
             :increase-agility 4
             :increase-intelligence 4}
    :description "A disciplined army's flag. Increases ALL stats by 4."}])
