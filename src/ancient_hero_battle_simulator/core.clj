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

(defn select-hero [team hero-number]
  (println (str "\nSelect " team " team hero " hero-number " (enter number):"))
  (loop []
    (let [input-str (read-line)
          input     (try
                      (Integer/parseInt input-str)
                      (catch NumberFormatException _
                        nil))]
      (if (nil? input)
        (do
          (println "Invalid input. Please enter a number.")
          (recur))
        (if-let [hero (first (filter #(= (:id %) input) heroes/heroes))]
          (do
            (println (str (:name hero) " selected!"))
            hero)
          (do
            (println "Hero not found. Try again.")
            (recur)))))))

(defn fight [blue red]
  (println "Fight!"))

(defn select-1v1 []
  (println "\n--- 1v1 Combat ---")
  (list-heroes)
  (let [blue-hero (select-hero "Blue" 1)
        red-hero  (select-hero "Red" 1)]
    (fight blue-hero red-hero)))

(defn select-2v2 []
  (println "\n--- 2v2 Combat ---")
  (list-heroes)
  (let [blue-team [(select-hero "Blue" 1)
                   (select-hero "Blue" 2)]
        red-team  [(select-hero "Red" 1)
                   (select-hero "Red" 2)]]
    (fight blue-team red-team)))

(defn select-3v3 []
  (println "\n--- 3v3 Combat ---")
  (list-heroes)
  (let [blue-team [(select-hero "BLUE" 1)
                   (select-hero "BLUE" 2)
                   (select-hero "BLUE" 3)]
        red-team  [(select-hero "RED" 1)
                   (select-hero "RED" 2)
                   (select-hero "RED" 3)]]
    (fight blue-team red-team)))

(defn select-combat []
  (loop []
    (show-combat-menu)
    (let [choice (read-line)]
      (case choice
        "1" (do (select-1v1) (recur))
        "2" (do (select-2v2) (recur))
        "3" (do (select-3v3) (recur))
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
