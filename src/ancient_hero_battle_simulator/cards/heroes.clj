(ns ancient-hero-battle-simulator.cards.heroes)

(def heroes
  [;; --- S TIER HEROES ---
   {:id 1
    :name "Achilles"
    :description "Legendary Warrior of Troy"
    :type :assassin
    :tier :s
    :stats {:health 75 :power 95 :defense 70 :agility 100 :intelligence 90}}

   {:id 2
    :name "King Arthur"
    :description "The Once and Future King"
    :type :strategist
    :tier :s
    :stats {:health 80 :power 80 :defense 70 :agility 70 :intelligence 95}}

   {:id 3
    :name "Hercules"
    :description "Son of Zeus"
    :type :bruiser
    :tier :s
    :stats {:health 80 :power 100 :defense 80 :agility 55 :intelligence 60}}

   {:id 4
    :name "Beowulf"
    :description "Slayer of Monsters"
    :type :balanced
    :tier :s
    :stats {:health 90 :power 90 :defense 85 :agility 80 :intelligence 85}}

   {:id 5
    :name "Leonidas"
    :description "King of Sparta"
    :type :tank-defender
    :tier :s
    :stats {:health 90 :power 80 :defense 100 :agility 70 :intelligence 85}}

   {:id 6
    :name "Gilgamesh"
    :description "King of Uruk"
    :type :tank-warrior
    :tier :s
    :stats {:health 100 :power 80 :defense 80 :agility 75 :intelligence 80}}

   ;; --- A TIER HEROES ---
   {:id 7
    :name "Miyamoto Musashi"
    :description "Legendary Samurai Duelist"
    :type :assassin
    :tier :a
    :stats {:health 70 :power 85 :defense 65 :agility 95 :intelligence 88}}

   {:id 8
    :name "Saladin"
    :description "Sultan and Master Strategist"
    :type :strategist
    :tier :a
    :stats {:health 70 :power 78 :defense 65 :agility 72 :intelligence 92}}

   {:id 9
    :name "Cú Chulainn"
    :description "Irish Hero and Spear Master"
    :type :bruiser
    :tier :a
    :stats {:health 75 :power 90 :defense 75 :agility 60 :intelligence 65}}

   {:id 10
    :name "Odysseus"
    :description "Cunning King of Ithaca"
    :type :balanced
    :tier :a
    :stats {:health 80 :power 85 :defense 80 :agility 80 :intelligence 90}}

   {:id 11
    :name "Hector"
    :description "Trojan Prince and Defender"
    :type :tank-defender
    :tier :a
    :stats {:health 85 :power 75 :defense 95 :agility 65 :intelligence 80}}

   {:id 12
    :name "Ramses II"
    :description "Pharaoh and Mighty Warrior"
    :type :tank-warrior
    :tier :a
    :stats {:health 90 :power 78 :defense 82 :agility 70 :intelligence 85}}])