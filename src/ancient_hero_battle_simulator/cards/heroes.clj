(ns ancient-hero-battle-simulator.cards.heroes)

(def heroes
  [;; --- S TIER HEROES ---
   {:id 101
    :name "Achilles"
    :description "Legendary Warrior of Troy"
    :category :hero
    :type :assassin
    :tier :s
    :stats {:health 80 :power 90 :defense 85 :agility 100 :intelligence 95}}

   {:id 102
    :name "Odysseus"
    :description "Master Strategist of the Trojan War"
    :category :hero
    :type :strategist
    :tier :s
    :stats {:health 95 :power 85 :defense 90 :agility 80 :intelligence 100}}

   {:id 103
    :name "Hercules"
    :description "Son of Zeus"
    :category :hero
    :type :bruiser
    :tier :s
    :stats {:health 85 :power 100 :defense 95 :agility 90 :intelligence 80}}

   {:id 104
    :name "Leonidas"
    :description "King of Sparta"
    :category :hero
    :type :defender
    :tier :s
    :stats {:health 90 :power 80 :defense 100 :agility 95 :intelligence 85}}

   {:id 105
    :name "Gilgamesh"
    :description "King of Uruk"
    :category :hero
    :type :warrior
    :tier :s
    :stats {:health 100 :power 95 :defense 80 :agility 85 :intelligence 90}}

   {:id 106
    :name "King Arthur"
    :description "The Once and Future King"
    :category :hero
    :type :balanced
    :tier :s
    :stats {:health 90 :power 90 :defense 90 :agility 90 :intelligence 90}}

   ;; --- A TIER HEROES ---
   {:id 107
    :name "Miyamoto Musashi"
    :description "Legendary Samurai Duelist"
    :category :hero
    :type :assassin
    :tier :a
    :stats {:health 70 :power 80 :defense 75 :agility 90 :intelligence 85}}

   {:id 108
    :name "Saladin"
    :description "Sultan and Master Strategist"
    :category :hero
    :type :strategist
    :tier :a
    :stats {:health 85 :power 75 :defense 80 :agility 70 :intelligence 90}}

   {:id 109
    :name "Cú Chulainn"
    :description "Irish Hero and Spear Master"
    :category :hero
    :type :bruiser
    :tier :a
    :stats {:health 75 :power 90 :defense 85 :agility 80 :intelligence 70}}

   {:id 110
    :name "Hector"
    :description "Trojan Prince and Defender"
    :category :hero
    :type :defender
    :tier :a
    :stats {:health 80 :power 70 :defense 90 :agility 85 :intelligence 75}}

   {:id 111
    :name "Ragnar Lothbrok"
    :description "Legendary Viking Warrior"
    :category :hero
    :type :warrior
    :tier :a
    :stats {:health 90 :power 85 :defense 70 :agility 75 :intelligence 80}}

   {:id 112
    :name "Beowulf"
    :description "Slayer of Monsters"
    :category :hero
    :type :balanced
    :tier :a
    :stats {:health 80 :power 80 :defense 80 :agility 80 :intelligence 80}}

   ;; --- B TIER HEROES ---
   {:id 113
    :name "Arjuna"
    :description "Peerless Archer of the Mahabharata"
    :category :hero
    :type :assassin
    :tier :b
    :stats {:health 60 :power 70 :defense 65 :agility 80 :intelligence 75}}

   {:id 114
    :name "Sun Tzu"
    :description "Author of The Art of War"
    :category :hero
    :type :strategist
    :tier :b
    :stats {:health 75 :power 65 :defense 70 :agility 60 :intelligence 80}}

   {:id 115
    :name "Guan Yu"
    :description "General of Unmatched Loyalty"
    :category :hero
    :type :bruiser
    :tier :b
    :stats {:health 65 :power 80 :defense 75 :agility 70 :intelligence 60}}

   {:id 116
    :name "Vlad the Impaler"
    :description "Fearsome Wallachian Defender"
    :category :hero
    :type :defender
    :tier :b
    :stats {:health 70 :power 60 :defense 80 :agility 75 :intelligence 65}}

   {:id 117
    :name "Sargon of Akkad"
    :description "First Great Conqueror"
    :category :hero
    :type :warrior
    :tier :b
    :stats {:health 80 :power 75 :defense 60 :agility 65 :intelligence 70}}

   {:id 118
    :name "Aeneas"
    :description "Founder of Roman Legacy"
    :category :hero
    :type :balanced
    :tier :b
    :stats {:health 70 :power 70 :defense 70 :agility 70 :intelligence 70}}

   ;; --- C TIER HEROES ---
   {:id 119
    :name "Icarus"
    :description "Flew Too Close to the Sun"
    :category :hero
    :type :assassin
    :tier :c
    :stats {:health 50 :power 60 :defense 55 :agility 70 :intelligence 65}}

   {:id 120
    :name "Croesus"
    :description "Wealth Without Wisdom"
    :category :hero
    :type :strategist
    :tier :c
    :stats {:health 65 :power 55 :defense 60 :agility 55 :intelligence 70}}

   {:id 121
    :name "Enkidu"
    :description "Wild Man of the Steppes"
    :category :hero
    :type :bruiser
    :tier :c
    :stats {:health 55 :power 70 :defense 65 :agility 60 :intelligence 50}}

   {:id 122
    :name "Priam"
    :description "Aged King of Troy"
    :category :hero
    :type :defender
    :tier :c
    :stats {:health 60 :power 50 :defense 70 :agility 65 :intelligence 55}}

   {:id 123
    :name "Goliath"
    :description "Mighty but Slow Giant"
    :category :hero
    :type :warrior
    :tier :c
    :stats {:health 70 :power 65 :defense 50 :agility 55 :intelligence 60}}

   {:id 124
    :name "Jason"
    :description "Leader of the Argonauts"
    :category :hero
    :type :balanced
    :tier :c
    :stats {:health 60 :power 60 :defense 60 :agility 60 :intelligence 60}}])