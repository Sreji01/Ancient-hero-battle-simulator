(ns ancient-hero-battle-simulator.cards.traps)

(def traps
  [
   {:id 301
    :name "Spike Pit"
    :category :trap
    :type :damage
    :trigger :enemy-hero-placed
    :effect {:damage 20}
    :description "Deal 20 damage to a placed hero."}

   {:id 302
    :name "Snare Trap"
    :category :trap
    :type :stun
    :trigger :enemy-hero-placed
    :effect {:stun 1}
    :description "Stun enemy hero for 1 turn when they are placed."}

   {:id 303
    :name "Trap of Confusion"
    :category :trap
    :type :control
    :trigger :enemy-hero-placed
    :effect {:swap-position true}
    :description "Take control of an enemy hero when they are placed for 1 turn."}

   {:id 304
    :name "Poison Gas"
    :category :trap
    :type :damage-over-time
    :trigger :enemy-attack
    :effect {:damage-per-turn 10 :turns 3}
    :description "Deal 10 damage to attacking hero per turn for 3 turns."}

   {:id 305
    :name "Cursed Idol"
    :category :trap
    :type :debuff
    :trigger :enemy-attack
    :effect {:reduce-power 20}
    :description "Reduce the attacking hero's power by 20 for one turn."}

   {:id 306
    :name "Crippling Guard"
    :category :trap
    :type :debuff
    :trigger :enemy-attack
    :effect {:reduce-defense 20}
    :description "Reduce the attacking hero's defense by 20 for one turn."}

   {:id 307
    :name "Fatigue Curse"
    :category :trap
    :type :debuff
    :trigger :enemy-attack
    :effect {:reduce-agility 20}
    :description "Reduce the attacking hero's agility by 20 for one turn."}

   {:id 308
    :name "Weaken Mind"
    :category :trap
    :type :debuff
    :trigger :enemy-attack
    :effect {:reduce-intelligence 20}
    :description "Reduce the attacking hero's intelligence by 20 for one turn."}

   {:id 309
    :name "Siphon Vitality"
    :category :trap
    :type :debuff
    :trigger :enemy-attack
    :effect {:reduce-health 20}
    :description "Reduce the attacking hero's health by 20 for one turn."}

   {:id 310
    :name "Mirror Trap"
    :category :trap
    :type :reflect
    :trigger :enemy-attack
    :effect {:reflect-damage 15}
    :description "Reflect 15 damage back to the attacker."}

   {:id 311
    :name "Magic Barrier"
    :category :trap
    :type :utility
    :trigger :enemy-attack
    :effect {:absorb-damage 20}
    :description "Absorb 20 damage from the next attack."}

   {:id 312
    :name "Defender's Mirror"
    :category :trap
    :type :control
    :trigger :enemy-attack
    :effect {:swap-with-defender true}
    :description "When an enemy hero attacks, swap all their stats with a defending hero for 1 turn."}

   {:id 313
    :name "Exploding Runes"
    :category :trap
    :type :damage
    :trigger :enemy-action
    :effect {:damage 30}
    :description "Deal 30 damage to an enemy player when he plays an action."}

   {:id 314
    :name "Action Mirror"
    :category :trap
    :type :reflect
    :trigger :enemy-action
    :effect {:copy-action true}
    :description "When the enemy plays an action, copy it and play it against them immediately."}

   {:id 315
    :name "Action Jammer"
    :category :trap
    :type :control
    :trigger :enemy-action
    :effect {:negate-action true}
    :description "Negates the next action played by the enemy."}

   {:id 316
    :name "Battle Frenzy"
    :category :trap
    :type :buff
    :trigger :player-attack
    :effect {:increase-health 10
             :increase-power 10
             :increase-defense 10
             :increase-agility 10
             :increase-intelligence 10}
    :description "Activates when you attack: Increase all your hero's stats by 10 for one turn."}])