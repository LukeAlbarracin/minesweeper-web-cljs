(ns minesweeper-web-cljs.core
    (:require [rum.core :as rum]))

;; MAY NEED TO ADD MATH COMBO LIBRARY

(enable-console-print!)

(println "This text is printed from src/minesweeper-web-cljs/core.cljs. Go ahead and edit it and see reloading in action.")

(defn rand-bool [range] ;; RANDOM BOOLEAN AT A GIVEN PROBABILITY
  (zero? (rand-int range)))

(def grid (atom (mapv vec (partition 5 (take 25 (repeatedly #(rand-bool 4))))))) ;; CREATES A 5x5 GRID

(defn permutations [nums] ;; CREDIT : DIEGO BASCH STACK OVERFLOW
  (lazy-seq
   (if (seq (rest nums))
     (apply concat (for [x nums]
                     (map #(cons x %) (permutations (remove #{x} nums)))))
     [nums])))
;;(defn bool-minefield []
;;  (mapv vec (partition 5 (mapv #(= % true) (flatten @grid)))))

(defn get-mine [i j] ;; RETRIEVES BOOLEAN VALUE FROM A GIVEN SQUARE AT GIVEN INDEX
  (try
    (nth (nth @grid i) j)
    (catch Exception e false)
    (finally))) ;; CATCHES INDEX OUT OF BOUNDS WHEN COUNTING FOR MINES
  
(defn get-combos [nums i j] (map + [i j] [(first nums) (second nums)]))

(defn solved-square [i j] ;; MUST UPDATE GRID VISUALLY
  (let [foo (mapv vec (distinct (map #(drop 4 %) (permutations [-1 -1 0 0 1 1]))))]
      (count (filter identity (map #(get-mine (first (get-combos % i j)) (second(get-combos % i j))) foo)))))

;; THE COUNT MUST BE DISPLAYED ONTO THE CLICKED SQUARE

(defonce app-state 
  (atom {:text "This is Minesweeper!!!"
         :grid @grid}))

(rum/defc minesweeper []
  [:center
    [:h1 (:text @app-state)]
    [:svg
      {:view-box "0 0 5 5"
      :width 500
      :height 500}
      (for [i (range (count (:grid @app-state)))
            j (range (count (:grid @app-state)))]
        [:rect {:width 0.8
                :height 0.8
                :fill "grey"
                :x i
                :y j
                :on-click
                (fn [e])}])]])

(defn display-mine [])
          
(rum/mount (minesweeper)
  (. js/document (getElementById "app")))

(defn on-js-reload [])

