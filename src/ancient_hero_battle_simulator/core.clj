(ns ancient-hero-battle-simulator.core
  (:gen-class))

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

(defn select-combat []
  (loop []
    (show-combat-menu)
    (let [choice (read-line)]
      (case choice
        "1" (do (println "You chose 1v1!") (recur))
        "2" (do (println "You chose 2v2!") (recur))
        "3" (do (println "You chose 3v3!") (recur))
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
