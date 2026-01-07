(ns ancient-hero-battle-simulator.core
  (:gen-class))

(defn show-menu []
  (println "1. Select heroes for combat")
  (println "2. Create your hero")
  (println "3. Exit"))

(defn -main []
  (loop []
    (show-menu)
    (let [choice (read-line)]
      (case choice
        "1" (println "Select heroes...")
        "2" (println "Create your hero...")
        "3" (println "Bye!")
        (println "Invalid choice"))
      (when-not (= choice "3")
        (recur)))))