(ns ancient-hero-battle-simulator.cards.traps)

(def traps
  [{:id 1
    :name "Spike Pit"
    :type :damage
    :trigger :enemy-hero-placed
    :effect {:damage 20}
    :description "Activates when an enemy hero is placed: Deal 20 damage."}

   {:id 2
    :name "Poison Gas"
    :type :damage-over-time
    :trigger :enemy-attack
    :effect {:damage-per-turn 10 :turns 3}
    :description "Deal 10 damage to attacking hero per turn for 3 turns."}

   {:id 3
    :name "Snare Trap"
    :type :control
    :trigger :enemy-hero-placed
    :effect {:stun 1}
    :description "Stun enemy hero for 1 turn when they are placed."}

   {:id 4
    :name "Exploding Runes"
    :type :damage
    :trigger :enemy-action
    :effect {:damage 30}
    :description "Trigger when enemy plays an action: Deal 30 damage to them."}

   {:id 5
    :name "Cursed Idol"
    :type :debuff
    :trigger :enemy-attack
    :effect {:reduce-power 20}
    :description "Reduce the attacking hero's power by 20 for one turn."}

   {:id 6
    :name "Crippling Guard"
    :type :debuff
    :trigger :enemy-attack
    :effect {:reduce-defense 20}
    :description "Reduce the attacking hero's defense by 20 for one turn."}

   {:id 7
    :name "Fatigue Curse"
    :type :debuff
    :trigger :enemy-attack
    :effect {:reduce-agility 20}
    :description "Reduce the attacking hero's agility by 20 for one turn."}

   {:id 8
    :name "Weaken Mind"
    :type :debuff
    :trigger :enemy-attack
    :effect {:reduce-intelligence 20}
    :description "Reduce the attacking hero's intelligence by 20 for one turn."}

   {:id 9
    :name "Siphon Vitality"
    :type :debuff
    :trigger :enemy-attack
    :effect {:reduce-health 20}
    :description "Reduce the attacking hero's health by 20 for one turn."}

   {:id 10
    :name "Mirror Trap"
    :type :reflect
    :trigger :enemy-attack
    :effect {:reflect-damage 15}
    :description "Reflect 15 damage back to the attacker."}

   {:id 11
    :name "Trap of Confusion"
    :type :control
    :trigger :enemy-hero-placed
    :effect {:swap-position true}
    :description "Take control of an enemy hero when they are placed for 1 turn."}

   {:id 12
    :name "Magic Barrier"
    :type :utility
    :trigger :enemy-attack
    :effect {:absorb-damage 20}
    :description "Absorb 20 damage from the next attack."}

   {:id 13
    :name "Action Mirror"
    :type :reflect
    :trigger :enemy-action
    :effect {:copy-action true}
    :description "When the enemy plays an action, copy it and play it against them immediately."}

   {:id 14
    :name "Action Jammer"
    :type :control
    :trigger :enemy-action
    :effect {:negate-action true}
    :description "Negates the next action played by the enemy"}

   {:id 15
    :name "Battle Frenzy"
    :type :buff
    :trigger :player-attack
    :effect {:increase-health 10
             :increase-power 10
             :increase-defense 10
             :increase-agility 10
             :increase-intelligence 10}
    :description "Activates when you attack: Increase all your hero's stats by 10 for one turn."}

   {:id 16
    :name "Defender's Mirror"
    :type :control
    :trigger :enemy-attack
    :effect {:swap-with-defender true}
    :description "When an enemy hero attacks, swap all their stats with a defending hero for 1 turn."}])
