(ns ancient-hero-battle-simulator.core
  (:gen-class)
  (:require [ancient-hero-battle-simulator.heroes :as heroes]))

(defn list-heroes []
  (println "\nAvailable Heroes:")
  (doseq [hero heroes/heroes]
    (println (str (:id hero) ". " (:name hero) " - " (:title hero)))))

(defn show-main-menu []
  (println "\nMain Menu")
  (println "1. Select heroes for combat")
  (println "2. Create your hero")
  (println "3. Exit"))

(defn show-combat-menu []
  (println "\nSelect combat type")
  (println "1. 1v1")
  (println "2. 2v2")
  (println "3. 3v3")

  (println "4. Back to main menu"))

(defn select-hero [team hero-number selected-heroes]
  (loop []
    (println (format "\nSelect %s team hero %d (enter number):" team hero-number))
    (if-let [id (try (Integer/parseInt (read-line))
                     (catch NumberFormatException _ nil))]
      (if-let [hero (some #(and (= (:id %) id) %) heroes/heroes)]
        (if (contains? @selected-heroes id)
          (do
            (println "Hero already selected! Choose someone else.")
            (recur))
          (do
            (println (str (:name hero) " selected!"))
            (swap! selected-heroes conj id)
            hero))
        (do
          (println "Hero not found.")
          (recur)))
      (do
        (println "Invalid input.")
        (recur)))))


(defn choose-hero [team team-name unavailable-heroes]
  (println (str "\nSelect a hero from " team-name " Team to attack (enter number):"))
  (let [available-team (vec (filter #(not (contains? @unavailable-heroes (:id %))) team))]
    (loop []
      (doseq [[id hero] (map-indexed vector available-team)]
        (println (str (inc id) ". " (:name hero))))
      (if-let [input (try (Integer/parseInt (read-line))
                          (catch NumberFormatException _ nil))]
        (if (and (>= input 1) (<= input (count available-team)))
          (available-team (dec input))
          (do (println "Invalid choice.") (recur)))
        (do (println "Invalid input.") (recur))))))

(defn choose-combatants [blue-team red-team attacked-heroes defended-heroes]
  (let [attacker (choose-hero blue-team "Blue" attacked-heroes)]
    (println (:name attacker) " selected to attack!")
    (let [defender (choose-hero red-team "Red" defended-heroes)]
      (println (:name defender) " selected as target!")
      [attacker defender])))

(defn attack [attacker defender]
  (Thread/sleep 2000)
  (println (str "\n" (:name attacker) " attacks " (:name defender) "!")))

(defn random-hero [team]
  (rand-nth team))

(defn enemy-attack [blue-team red-team]
  (let [attacker (random-hero red-team)
        defender (random-hero blue-team)]
    (println "\n[Enemy turn!]")
    (Thread/sleep 2000)
    (println (:name attacker) "selected to attack!")
    (Thread/sleep 2000)
    (println (:name defender) "selected as target!")
    (Thread/sleep 2000)
    (println (str "\n" (:name attacker) " attacks " (:name defender) "!"))
    ))

(defn fight [blue-team red-team]
  (let [attacked-heroes (atom #{})
        defended-heroes (atom #{})]
    (while (or (< (count @attacked-heroes) (count blue-team))
               (< (count @defended-heroes) (count red-team)))
      (let [[attacker defender] (choose-combatants blue-team red-team attacked-heroes defended-heroes)]
        (attack attacker defender)
        (swap! attacked-heroes conj (:id attacker))
        (swap! defended-heroes conj (:id defender))
        (enemy-attack blue-team red-team)))))

(defn select-nvn [n]
  (println (format "\n--- %dv%d Combat ---" n n))
  (list-heroes)
  (let [selected-heroes (atom #{})
        blue-team (mapv #(select-hero "Blue" % selected-heroes) (range 1 (inc n)))
        red-team  (mapv #(select-hero "Red"  % selected-heroes) (range 1 (inc n)))]
    (fight blue-team red-team)))

(defn select-combat []
  (loop []
    (show-combat-menu)
    (let [choice (read-line)]
      (case choice
        "1" (do (select-nvn 1) (recur))
        "2" (do (select-nvn 2) (recur))
        "3" (do (select-nvn 3) (recur))
        "4" (println "Returning to main menu...")
        (do (println "Invalid choice") (recur))))))

(defn -main []
  (loop []
    (show-main-menu)
    (let [choice (read-line)]
      (case choice
        "1" (do (select-combat) (recur))
        "2" (do (println "Create your hero...") (recur))
        "3" (println "Bye!")
        (do (println "Invalid choice") (recur))))))
