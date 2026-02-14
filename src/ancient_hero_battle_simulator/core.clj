(ns ancient-hero-battle-simulator.core
  (:gen-class)
  (:require [ancient-hero-battle-simulator.game.game-flow :as game]))

(defn -main []
  (game/start-game))