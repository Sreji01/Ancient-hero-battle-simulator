(ns ancient-hero-battle-simulator.game.logic
  (:require
   [ancient-hero-battle-simulator.game.ui :as ui]
   [ancient-hero-battle-simulator.game.card-logic.action-logic :as action-logic]
   [ancient-hero-battle-simulator.game.card-logic.trap-logic :as trap-logic]
   [ancient-hero-battle-simulator.game.deck-menagment :as deck-managment]
   [ancient-hero-battle-simulator.game.game-state :as state]))

(defn apply-card-effect! [card field enemy-field player-hp enemy-player-hp hand deck player-name]
  (case (:category card)
    :action
    (let [trap-result (trap-logic/handle-enemy-action-traps! enemy-field field card player-hp hand deck player-name)]
      (if (= trap-result :negated)
        (do
          (println (format "\n %s was negated!\n" (:name card)))
          false)
        (action-logic/apply-action-effect!
         card field enemy-field enemy-player-hp hand deck player-name)))

    :equipment
    (println "Equipment logic not yet implemented")

    (println "Unknown card category:" (:category card))))

(defn apply-effect [card hand field enemy-field player-hp enemy-player-hp deck player-name]
  (if (or (= (:category card) :action)
          (= (:category card) :equipment))
    (apply-card-effect! card field enemy-field player-hp enemy-player-hp hand deck player-name)
    true))

(defn handle-board-and-traps [card field enemy-field player-name n idx]
  (when (= (:category card) :hero)
    (ui/show-board-for-player player-name field enemy-field n)
    (let [placed-hero   (:hero (nth @field idx))
          defender-name (if (= player-name "BLUE") "RED" "BLUE")
          trap-activated? (trap-logic/handle-enemy-hero-placed! enemy-field field placed-hero defender-name)]
      (when trap-activated?
        (ui/show-board-for-player player-name field enemy-field n))
      true)))

(defn execute-card-play! [card hand field enemy-field player-hp enemy-player-hp player-name deck n]
  (ui/print-card-play player-name card (:msg (state/get-slot-config (:category card))))

  (let [{:keys [success idx err]} (state/valid-slot? card field)]
    (cond
      (not success) {:success false :err err}

      (and (= (:category card) :hero)
           (not (state/can-place-hero? field enemy-field)))
      {:success false :err "\nCannot place hero - a slot must be reserved for returning controlled hero!\n"}

      :else
      (let [effect-result (apply-effect card hand field enemy-field player-hp enemy-player-hp deck player-name)]
        (if (false? effect-result)
          {:success false :err "\nCard effect failed!\n"}
          (do
            (deck-managment/remove-card-from-hand! hand card)
            (state/place-card-on-field! card field (:key (state/get-slot-config (:category card))) idx)
            {:success true
             :board-shown? (boolean (handle-board-and-traps card field enemy-field player-name n idx))}))))))


(defn handle-choice
  [choice playable hand field enemy-field player-hp enemy-player-hp player-name used-types show-board deck n]
  (cond
    (= choice (inc (count playable)))
    {:done true :used-types used-types}

    (and (>= choice 1) (<= choice (count playable)))
    (let [card (nth playable (dec choice))
          result (execute-card-play! card hand field enemy-field player-hp enemy-player-hp player-name deck n)]
      (if (:success result)
        (do
          (Thread/sleep 1000)
          (when-not (:board-shown? result)
            (show-board))
          {:done false :used-types (conj used-types (:category card))})
        (do
          (println (:err result))
          {:done false :used-types used-types})))

    :else
    (do
      (println "\nInvalid choice.\n")
      {:done false :used-types used-types})))