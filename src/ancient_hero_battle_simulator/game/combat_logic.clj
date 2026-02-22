(ns ancient-hero-battle-simulator.game.combat-logic
  (:require [ancient-hero-battle-simulator.game.ui :as ui]
            [ancient-hero-battle-simulator.game.game-state :as state]
            [ancient-hero-battle-simulator.game.utilility :as util]
            [ancient-hero-battle-simulator.game.card-logic.trap-logic :as trap-logic]))

(defn calculate-hit? [attacker target]
  (let [atk-stats @(:current-stats attacker)
        tar-stats @(:current-stats target)
        hit-chance (+ 80 (* (:intelligence atk-stats) 0.1)
                      (* (- (:agility tar-stats)) 0.15))
        evade-chance (or (:evade tar-stats) 0)
        roll (rand-int 100)]
    (cond
      (< roll evade-chance) :dodge-roll
      (> (rand-int 100) hit-chance) :dodge
      :else :hit)))

(defn compute-damage [attacker target]
  (let [atk-stats @(:current-stats attacker)
        tar-stats @(:current-stats target)
        raw-damage (:power atk-stats)
        reduction (* (:defense tar-stats 0) 0.5)
        damage-reduction (:damage-reduction tar-stats 0)]
    (int (max 5 (- raw-damage reduction damage-reduction)))))

(defn apply-damage! [target target-player-hp damage]
  (swap! (:current-hp target) #(max 0 (- % damage)))
  (swap! target-player-hp #(max 0 (- % damage))))

(defn attack! [attacker target target-player-hp player-field enemy-field]
  (Thread/sleep 1500)
  (ui/print-attack-message attacker target)
  (trap-logic/handle-player-attack-traps! player-field attacker)
  (trap-logic/handle-enemy-attack-traps! enemy-field attacker target)
  (let [outcome (calculate-hit? attacker target)
        damage  (if (= outcome :hit)
                  (compute-damage attacker target)
                  0)]
    (when (= outcome :dodge-roll)
      (swap! (:current-stats target) dissoc :evade))

    (when (= outcome :hit)
      (apply-damage! target target-player-hp damage)
      (state/check-and-remove-dead! target enemy-field)

      (let [stats-atom (:current-stats target)]
        (when (> (:damage-reduction @stats-atom 0) 0)
          (swap! stats-atom assoc :damage-reduction 0))))
    (Thread/sleep 1500)
    (ui/print-outcome attacker target outcome damage)))

(defn perform-attack [player-name attacker player-field enemy-field enemy-player-hp]
  (if (:stunned? attacker)
    (do
      (println (str "\n" (:name attacker) " is stunned and cannot attack this turn!"))
      (state/update-hero-on-field! player-field (dissoc attacker :stunned? :stun-rounds)))
    (if (:skip-attack? attacker)
      (do
        (println (str "\n" (:name attacker) " is forced to skip their attack!"))
        (state/update-hero-on-field! player-field (dissoc attacker :skip-attack?)))
      (let [targets (state/heroes-on-field @enemy-field)
            target  (util/choose-hero targets
                                      (if (= player-name "BLUE") "Red" "Blue")
                                      "to attack")]
        (attack! attacker target enemy-player-hp  player-field enemy-field)))))

(defn choose-attacker [input available-attackers]
  (nth available-attackers (dec input)))

(defn end-attack-phase? [input available-attackers]
  (= input (inc (count available-attackers))))

(defn valid-attacker-choice? [input available-attackers]
  (and input
       (>= input 1)
       (<= input (inc (count available-attackers)))))

(defn attack-phase-loop
  [player-name available-attackers field enemy-field enemy-player-hp]
  (when (seq available-attackers)
    (let [defenders (state/heroes-on-field @enemy-field)]
      (if (empty? defenders)
        (println "No enemies to attack!")
        (do
          (ui/print-attackers available-attackers)
          (let [input (util/read-int)]
            (cond
              (not (valid-attacker-choice? input available-attackers))
              (do (println "Invalid input.")
                  (recur player-name available-attackers field enemy-field enemy-player-hp))

              (end-attack-phase? input available-attackers)
              (println "\nEnding attack phase...")

              :else
              (let [attacker (choose-attacker input available-attackers)]
                (perform-attack player-name attacker field enemy-field enemy-player-hp)
                (recur player-name
                       (vec (remove #(= (:id %) (:id attacker)) available-attackers))
                       field enemy-field enemy-player-hp)))))))))