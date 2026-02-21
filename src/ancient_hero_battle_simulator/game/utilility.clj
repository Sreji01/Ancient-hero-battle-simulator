(ns ancient-hero-battle-simulator.game.utilility
  (:require [ancient-hero-battle-simulator.game.ui :as ui]
            [ancient-hero-battle-simulator.game.game-state :as state]))

(defn read-choice [max]
  (if-let [input (try (Integer/parseInt (read-line)) (catch Exception _ nil))]
    (when (and (>= input 1) (<= input max))
      input)
    nil))

(defn read-int []
  (try
    (Integer/parseInt (read-line))
    (catch Exception _ nil)))

(defn choose-hero [team team-name action-text]
  (println (str "\nSelect a hero from " team-name " Team " action-text " (enter number):"))
  (let [heroes (state/available-heroes team (atom #{}))]
    (loop []
      (ui/print-heroes heroes)
      (if-let [choice (read-choice (count heroes))]
        (let [selected-hero (heroes (dec choice))]
          (println (str "\n" (:name selected-hero) " selected!"))
          selected-hero)
        (do (println "\nInvalid input.\n")
            (recur))))))