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

(defn fight [blue-team red-team]
  (println "\nBlue Team:") 
  (doseq [[id hero] (map-indexed vector blue-team)]
    (println (str (inc id) ". " (:name hero))))

  (let [attacker (loop []
                   (println "\nSelect a hero from Blue Team to attack (enter number):")
                   (if-let [input (try (Integer/parseInt (read-line))
                                        (catch NumberFormatException _ nil))]
                     (if (and (>= input 1) (<= input (count blue-team)))
                       (blue-team (dec input))
                       (do (println "Invalid choice.") (recur)))
                     (do (println "Invalid input.") (recur))))]
    (println (:name attacker) " selected!")

    (println "\nRed Team:")
    (doseq [[id hero] (map-indexed vector red-team)]
      (println (str (inc id) ". " (:name hero))))

    (let [defender (loop []
                     (println "\nSelect a hero from Red Team to attack (enter number):")
                     (if-let [choice (try (Integer/parseInt (read-line))
                                          (catch NumberFormatException _ nil))]
                       (if (and (>= choice 1) (<= choice (count red-team)))
                         (red-team (dec choice))
                         (do (println "Invalid choice.") (recur)))
                       (do (println "Invalid input.") (recur))))]
      (println (:name defender) " selected!")

      (println (str "\n" (:name attacker) " attacks " (:name defender) "!")))))

(defn select-nvn [n]
  (println (format "\n--- %dv%d Combat ---" n n))
  (list-heroes)
  (let [selected-heroes (atom #{})
        blue-team (mapv #(select-hero "Blue" % selected-heroes) (range 1 (inc n)))
        red-team  (mapv #(select-hero "Red" % selected-heroes)  (range 1 (inc 2)))]
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
