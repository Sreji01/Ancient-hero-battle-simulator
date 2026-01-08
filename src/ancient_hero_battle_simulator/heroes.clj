(ns ancient-hero-battle-simulator.heroes)

(def achilles
  {:id 1
   :name "Achilles"
   :title "Legendary Warrior of Troy"

   :stats
   {:health 85
    :power 95
    :defense 70
    :agility 100
    :intelligence 80}})

(def king-arthur
  {:id 2
   :name "King Arthur"
   :title "The Once and Future King"

   :stats
   {:health 90
    :power 80
    :defense 85
    :agility 70
    :intelligence 90}})

(def hercules
  {:id 3
   :name "Hercules"
   :title "Son of Zeus"

   :stats
   {:health 100
    :power 100
    :defense 80
    :agility 65
    :intelligence 70}})

(def beowulf
  {:id 4
   :name "Beowulf"
   :title "Slayer of Monsters"

   :stats
   {:health 95
    :power 85
    :defense 90
    :agility 60
    :intelligence 75}})

(def leonidas
  {:id 5
   :name "Leonidas"
   :title "King of Sparta"

   :stats
   {:health 88
    :power 85
    :defense 95
    :agility 75
    :intelligence 80}})

(def gilgamesh
  {:id 6
   :name "Gilgamesh"
   :title "King of Uruk"

   :stats
   {:health 92
    :power 88
    :defense 82
    :agility 78
    :intelligence 85}})

(def heroes
  [achilles
   king-arthur
   hercules
   beowulf
   leonidas
   gilgamesh])