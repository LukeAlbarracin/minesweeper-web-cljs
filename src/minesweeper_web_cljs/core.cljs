(ns minesweeper-web-cljs.core
    (:require
      [reagent.core :as r]))
      ;[stylefy.core :as stylefy]))

;(stylefy/init)

(defn rand-bool [range] ;; RANDOM BOOLEAN AT A GIVEN PROBABILITY (THE HIGHER THE LESS LIKELY TO BE TRUE)
  (zero? (rand-int range)))

(def grid (atom (mapv vec (partition 5 (take 25 (repeatedly #(rand-bool 4))))))) ;; CREATES A 5x5 GRID of bools

(defn how-many-mines [depth] ;; CREATES A VECTOR OF THE SURROUNDING SQUARES' COORDINATES WITH A PERMUTATION FUNCTION
  (let [foo (vec (range (- depth) (inc depth)))] ;; RANGE OF SQUARES TO LOOK THRU BASED ON PARAMETER 'depth'
    (mapv vec (mapcat (fn [x] (map (fn [y] (concat [x] [y])) foo)) foo)))) ;; RETURNS A VECTOR OF ALL POSSIBLE COORDINATES (SHORTENED PERMUTATION)
  
(defn coordinates [i j] (mapv vec (map #(map + [i j] %) (how-many-mines 1)))) ;; APPLIES ADDITION TO POSSIBLE COORDINATES WITH INPUT COORDINATES

(defn solved-square [i j] ;; RETURNS HOW MANY MINES ARE PRESENT TO THE CLICKED SQUARE
  (count ;; COUNTS HOW MANY TRUES ARE PRESENT WITHIN A CONCATENATED VECTOR
    (filter identity ;; REMOVES FALSE BY FILTERING OUT NON-TRUE AND NIL
      (map 
        (fn [x] 
          (try
            (nth (nth @grid (first x))(second x))
            (catch js/Error e false) ;; INDEX OUT OF BOUNDS ERROR CHECKING WITH REACT.JS
            (finally)))
      (coordinates i j)))))

(defn mine? [i j] ;; CHECKS IF THE PLAYER HAS CLICKED A MINE (WHICH INDICATES A LOSS)
  (if (= true (nth (nth @grid i) j))
    (js/alert "You have hit a mine... You lose!!!")
    (solved-square i j)))

(defn button-coordinates [size]
  (let [nums (mapv #(* % 100) (range 0 size)) mutable-vec (r/atom [])]
    (doseq [x nums y nums] (swap! mutable-vec #(conj @mutable-vec (concat [(+ 10 x)] [(+ 10 y)]))))
    (mapv vec @mutable-vec)))

(defn mine-sweeper []
  [:div [:h2 "Welcome to Reagent"]]
  [:svg {:style {:border "3px solid"
             :background "white"
             :width "500px"
             :height "500px"}}
  (for [xys (button-coordinates 5)]
      (let [mutable-text (r/atom "X")]
        ^{:key xys}
        [:g
          [:rect {:x (first xys) 
              :y (second xys) 
              :fill "darkgray" 
              :width 80 
              :height 80 
              :on-click #(js/alert (mine? (/ (first xys)100) (/ (second xys)100)))}]
          [:text {:x (+ (first xys) 25) 
              :y (+ (second xys) 60) 
              :fill "red" 
              :style 
              {:font "bold 50px san-serif"}} 
              @mutable-text]]))])
    
(defn mount-root []
  (r/render [mine-sweeper] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
