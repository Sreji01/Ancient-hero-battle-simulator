(ns ancient-hero-battle-simulator.heroes)

(def achilles
  {:id 1
   :name "Achilles"
   :title "Legendary Warrior of Troy"
   :type :assassin
   :tier :s
   :stats {:health 75
           :power 95
           :defense 70
           :agility 100
           :intelligence 90}})

(def miyamoto-musashi
  {:id 2
   :name "Miyamoto Musashi"
   :title "Legendary Samurai Duelist"
   :type :assassin
   :tier :a
   :stats {:health 70
           :power 85
           :defense 65
           :agility 95
           :intelligence 88}})

(def king-arthur
  {:id 3
   :name "King Arthur"
   :title "The Once and Future King"
   :type :strategist
   :tier :s
   :stats {:health 80
           :power 80
           :defense 70
           :agility 70
           :intelligence 95}})

(def saladin
  {:id 4
   :name "Saladin"
   :title "Sultan and Master Strategist"
   :type :strategist-mage-knight
   :tier :a
   :stats {:health 70
           :power 78
           :defense 65
           :agility 72
           :intelligence 92}})

(def hercules
  {:id 5
   :name "Hercules"
   :title "Son of Zeus"
   :type :bruiser
   :tier :s
   :stats {:health 80
           :power 100
           :defense 80
           :agility 55
           :intelligence 60}})

(def cu-chulainn
  {:id 6
   :name "Cú Chulainn"
   :title "Irish Hero and Spear Master"
   :type :bruiser
   :tier :a
   :stats {:health 75
           :power 90
           :defense 75
           :agility 60
           :intelligence 65}})

(def beowulf
  {:id 7
   :name "Beowulf"
   :title "Slayer of Monsters"
   :type :balanced
   :tier :s
   :stats {:health 90
           :power 90
           :defense 85
           :agility 80
           :intelligence 85}})

(def odysseus
  {:id 8
   :name "Odysseus"
   :title "Cunning King of Ithaca"
   :type :balanced
   :tier :a
   :stats {:health 80
           :power 85
           :defense 80
           :agility 80
           :intelligence 90}})

(def leonidas
  {:id 9
   :name "Leonidas"
   :title "King of Sparta"
   :type :tank-defender
   :tier :s
   :stats {:health 90
           :power 80
           :defense 100
           :agility 70
           :intelligence 85}})

(def hector
  {:id 10
   :name "Hector"
   :title "Trojan Prince and Defender"
   :type :tank-defender
   :tier :a
   :stats {:health 85
           :power 75
           :defense 95
           :agility 65
           :intelligence 80}})

(def gilgamesh
  {:id 11
   :name "Gilgamesh"
   :title "King of Uruk"
   :type :tank-warrior
   :tier :s
   :stats {:health 100
           :power 80
           :defense 80
           :agility 75
           :intelligence 80}})

(def ramses-ii
  {:id 12
   :name "Ramses II"
   :title "Pharaoh and Mighty Warrior"
   :type :tank-warrior
   :tier :a
   :stats {:health 90
           :power 78
           :defense 82
           :agility 70
           :intelligence 85}})

(def heroes
  [achilles
   miyamoto-musashi
   king-arthur
   saladin
   hercules
   cu-chulainn
   beowulf
   odysseus
   leonidas
   hector
   gilgamesh
   ramses-ii])
