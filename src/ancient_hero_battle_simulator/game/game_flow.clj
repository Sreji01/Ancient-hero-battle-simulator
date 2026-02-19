(ns ancient-hero-battle-simulator.game.game-flow
  (:require
   [ancient-hero-battle-simulator.game.ui :as ui]
   [ancient-hero-battle-simulator.game.combat-logic :as combat-logic]
   [ancient-hero-battle-simulator.game.card-logic.action-logic :as action-logic]
   [ancient-hero-battle-simulator.game.card-logic.trap-logic :as trap-logic]
   [ancient-hero-battle-simulator.game.deck-menagment :as deck-managment]
   [ancient-hero-battle-simulator.game.game-state :as state]))

(defn read-int []
  (try
    (Integer/parseInt (read-line))
    (catch Exception _ nil)))

(defn valid-attacker-choice? [input available-attackers]
  (and input
       (>= input 1)
       (<= input (inc (count available-attackers)))))

(defn choose-attacker [input available-attackers]
  (nth available-attackers (dec input)))

(defn end-attack-phase? [input available-attackers]
  (= input (inc (count available-attackers))))

(defn attack-phase-loop
  [player-name available-attackers field enemy-field enemy-player-hp]
  (when (seq available-attackers)
    (let [defenders (state/heroes-on-field @enemy-field)]
      (if (empty? defenders)
        (println "No enemies to attack!")
        (do
          (ui/print-attackers available-attackers)
          (let [input (read-int)]
            (cond
              (not (valid-attacker-choice? input available-attackers))
              (do (println "Invalid input.")
                  (recur player-name available-attackers field enemy-field enemy-player-hp))

              (end-attack-phase? input available-attackers)
              (println "\nEnding attack phase...")

              :else
              (let [attacker (choose-attacker input available-attackers)]
                (combat-logic/perform-attack player-name attacker field enemy-field enemy-player-hp)
                (recur player-name
                       (vec (remove #(= (:id %) (:id attacker)) available-attackers))
                       field enemy-field enemy-player-hp)))))))))

(defn attack-phase [player-name field enemy-field enemy-player-hp]
  (println (str "\n--- " player-name " ATTACK PHASE ---"))
  (Thread/sleep 800)
  (attack-phase-loop player-name
                     (state/heroes-on-field @field)
                     field
                     enemy-field
                     enemy-player-hp))

(defn apply-card-effect! [card field enemy-field enemy-player-hp hand deck player-name]
  (case (:category card)
    :action    (action-logic/apply-action-effect!       card field enemy-field enemy-player-hp hand deck player-name)
    :equipment (println "Equipment logic not yet implemented")
    (println "Unknown card category:" (:category card))))

(defn execute-card-play! [card hand field enemy-field enemy-player-hp player-name deck n]
  (let [ctype (:category card)
        {:keys [key finder msg err]} (state/get-slot-config ctype)
        idx (finder @field)]
    (cond
      (not idx)
      {:success false :err err}

      (and (= ctype :hero) (not (state/can-place-hero? field enemy-field)))
      {:success false :err "\nCannot place hero - a slot must be reserved for returning controlled hero!\n"}

      :else
      (do
        (ui/print-card-play player-name card msg)
        (let [effect-result (if (or (= ctype :action) (= ctype :equipment))
                              (apply-card-effect! card field enemy-field enemy-player-hp hand deck player-name)
                              true)]
          (if (false? effect-result)
            {:success false :err "\nCard effect failed!\n"}
            (do
              (deck-managment/remove-card-from-hand! hand card)
              (state/place-card-on-field! card field key idx)
              (let [board-shown? (when (= ctype :hero)
                                   (ui/show-board-for-player player-name field enemy-field n)
                                   (let [placed-hero   (:hero (nth @field idx))
                                         defender-name (if (= player-name "BLUE") "RED" "BLUE")
                                         trap-activated? (trap-logic/handle-enemy-hero-placed! enemy-field field placed-hero defender-name)]
                                     (when trap-activated?
                                       (ui/show-board-for-player player-name field enemy-field n))
                                     true))]
                {:success true :board-shown? (boolean board-shown?)}))))))))


(defn handle-choice
  [choice playable hand field enemy-field enemy-player-hp player-name used-types show-board deck n]
  (cond
    (= choice (inc (count playable)))
    {:done true :used-types used-types}

    (and (>= choice 1) (<= choice (count playable)))
    (let [card (nth playable (dec choice))
          result (execute-card-play! card hand field enemy-field enemy-player-hp player-name deck n)]
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
      (println "Invalid choice.")
      {:done false :used-types used-types})))

(defn selection-phase
  [player-name hand field enemy-field enemy-player-hp n deck]
  (ui/show-selection-header player-name)
  (Thread/sleep 800)
  (let [show-board #(ui/show-board-for-player player-name field enemy-field n)]
    (show-board)
    (loop [used-types #{}]
      (let [playable (state/playable-cards hand used-types)]
        (when (seq playable)
          (ui/print-playable-cards playable)
          (if-let [choice (read-int)]
            (let [{:keys [done used-types]}
                  (handle-choice choice playable hand field enemy-field enemy-player-hp player-name used-types show-board deck n)]
              (when-not done
                (recur used-types)))
            (do
              (println "Invalid input.")
              (recur used-types))))))))

(defn draw-phase [player-name n deck hand first-draw?]
  (println (str "\n--- " player-name " DRAW PHASE ---"))
  (Thread/sleep 1000)
  (let [drawn (deck-managment/draw-cards deck n first-draw?)]
    (doseq [card drawn]
      (Thread/sleep 500)
      (ui/show-draw player-name card))
    (deck-managment/add-to-hand! hand drawn)))

(defn player-turn
  [player-name n deck hand field enemy-field
   player-hp enemy-player-hp
   can-attack? first-draw?]
  (ui/print-player-turn player-name player-hp)
  (Thread/sleep 800)
  (draw-phase player-name n deck hand first-draw?)
  (Thread/sleep 400)
  (selection-phase player-name hand field enemy-field enemy-player-hp n deck)
  (when can-attack?
    (attack-phase player-name field enemy-field enemy-player-hp)))

(defn end-round! [game-state]
  (let [{:keys [blue red]} game-state]
    (state/restore-mind-controlled-heroes! (:field blue) (:field red))
    (state/clear-action-slots! (:field blue))
    (state/clear-action-slots! (:field red))
    (state/reset-current-stats! (:field blue))
    (state/reset-current-stats! (:field red))))

(defn regular-round [game-state]
  (let [{:keys [blue red n]} game-state]
    (player-turn "BLUE" n (:deck blue) (:hand blue) (:field blue) (:field red)
                 (:hp blue) (:hp red) true false)

    (player-turn "RED" n (:deck red) (:hand red) (:field red) (:field blue)
                 (:hp red) (:hp blue) true false)

    (end-round! game-state)))

(defn first-round [game-state]
  (let [{:keys [blue red n]} game-state]
    (player-turn "BLUE" n (:deck blue) (:hand blue) (:field blue) (:field red)
                 (:hp blue) (:hp red) false true)

    (player-turn "RED" n (:deck red) (:hand red) (:field red) (:field blue)
                 (:hp red) (:hp blue) true true)

    (end-round! game-state)))

(defn game-loop [game-state]
  (let [{:keys [blue red]} game-state]
    (loop []
      (when-not (state/game-over? (:hp blue) (:hp red))
        (regular-round game-state)
        (recur)))
    (state/get-winner (:hp blue) (:hp red))))

(defn fight-cards [blue-cards red-cards n]
  (let [game-state (state/create-game-state blue-cards red-cards n)]
    (first-round game-state)
    (let [winner (game-loop game-state)]
      (println (format "\n %s PLAYER WINS! " winner))
      winner)))

(defn select-method []
  (loop []
    (ui/show-method-menu)
    (case (read-line)
      "1" "draft"
      "2" "random"
      "3" (do (println "Returning to main menu...") :back)
      (do (println "Invalid choice") (recur)))))

(defn select-nvn [mode n]
  (let [method (select-method)]
    (if (= method :back)
      :back
      (let [selected (atom #{})
            cards (deck-managment/prepare-cards method mode n selected)]
        (ui/announce-battle n)
        (Thread/sleep 1000)
        (fight-cards (:blue cards) (:red cards) n)))))

(defn select-combat [mode]
  (loop []
    (ui/show-combat-menu)
    (case (read-line)
      "1" (if (= (select-nvn mode 1) :back) nil (recur))
      "2" (if (= (select-nvn mode 2) :back) nil (recur))
      "3" (if (= (select-nvn mode 3) :back) nil (recur))
      "4" (if (= (select-nvn mode 4) :back) nil (recur))
      "5" (println "Returning to main menu...")
      (do (println "Invalid choice") (recur)))))

(defn select-mode []
  (loop []
    (ui/show-mode-menu)
    (case (read-line)
      "1" (do (println "\nPvE selected!") "1")
      "2" (do (println "\nPvP selected!") "2")
      "3" (do (println "Returning to main menu...") :back)
      (do (println "\nInvalid choice") (recur)))))

(defn start-game []
  (loop []
    (ui/show-main-menu)
    (case (read-line)
      "1" (do
            (let [mode (select-mode)]
              (when-not (= mode :back)
                (select-combat mode)))
            (recur))
      "2" (do (println "Create your hero...") (recur))
      "3" (println "Bye!")
      (do (println "Invalid choice") (recur)))))