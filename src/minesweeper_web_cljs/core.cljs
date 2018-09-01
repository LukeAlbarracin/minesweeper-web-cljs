(ns ^:figwheel-always minesweeper-web-cljs.core
    (:require [reagent.core :as r :refer [atom]]))

(defn- rand-bool [range] ;; RANDOM BOOLEAN AT A GIVEN PROBABILITY (THE HIGHER THE LESS LIKELY TO BE TRUE)
  (zero? (rand-int range)))

(defonce ^:private app-state 
  (atom 
  {:bool-grid (mapv vec (partition 5 (take 25 (repeatedly #(rand-bool 4))))) ;; CHANGED TO SOLVED GRID
  :visual-grid (mapv vec (repeat 5 (repeat 5 "?"))) 
  :game-over false }))

(defn- how-many-mines [depth] ;; CREATES A VECTOR OF THE SURROUNDING SQUARES' COORDINATES WITH A PERMUTATION FUNCTION
  (let [foo (vec (range (- depth) (inc depth)))] ;; RANGE OF SQUARES TO LOOK THRU BASED ON PARAMETER 'depth'
    (mapv vec (mapcat (fn [x] (map (fn [y] (concat [x] [y])) foo)) foo)))) ;; RETURNS A VECTOR OF ALL POSSIBLE COORDINATES (SHORTENED PERMUTATION)
  
(defn- coordinates [i j] (mapv vec (map #(map + [i j] %) (how-many-mines 1)))) ;; APPLIES ADDITION TO POSSIBLE COORDINATES WITH INPUT COORDINATES

(defn- solved-square [i j] ;; RETURNS HOW MANY MINES ARE PRESENT TO THE CLICKED SQUARE
  (count ;; COUNTS HOW MANY TRUES ARE PRESENT WITHIN A CONCATENATED VECTOR
    (filter identity ;; REMOVES FALSE BY FILTERING OUT NON-TRUE AND NIL
      (map 
        (fn [i j] 
          (try
            (get-in (@app-state :bool-grid) [i j])
            (catch js/Error e false) ;; INDEX OUT OF BOUNDS ERROR CHECKING WITH REACT.JS
            (finally)))
      (coordinates i j)))))

(defn- mine? [i j] ;; CHECKS IF THE PLAYER HAS CLICKED A MINE (WHICH INDICATES A LOSS)
  (if (= true (get-in (@app-state :bool-grid) [i j]))
    (js/alert "You have hit a mine... You lose!!!")
    (solved-square i j)))

(defn- render-single-square [i j]
  (if (get-in (:visual-grid @app-state) [i j]) "O" "X"))

(defn- button-svg-coordinates [size]
  (let [nums (mapv #(* % 100) (range 0 size)) mutable-vec (atom [])]
    (doseq [x nums y nums] (swap! mutable-vec #(conj @mutable-vec (concat [x] [y]))))
    (mapv vec @mutable-vec)))

(defn- mine-sweeper [] 
  (when (not= true (:game-over @app-state))
  [:div [:h2 "Welcome to Minesweeper!"]
  (into [:svg {:style {:border "3px solid"
             :background "white"
             :view-box "0 0 5 5"
             :width "500px"
             :height "500px"}}
  (for [xys (button-svg-coordinates 5)]
        (let [i (/ (first xys) 100) j (/(second xys) 100)]
        ^{:key [i j]} 
        [:g
          [:rect {:x (+ (first xys) 10)
              :y (+ (second xys) 10)
              :fill "darkgray" 
              :width 80
              :height 80 
              :on-click 
              (fn [_] (reset! app-state (assoc-in @app-state [:visual-grid i j] (mine? i j))))
                  }]
          [:text {:x (+ (first xys) 25) 
              :y (+ (second xys) 60) 
              :fill "red" 
              :on-click (fn [_] (reset! app-state (assoc-in @app-state [:visual-grid i j] (mine? i j))))
              :style {:font "bold 50px san-serif"}} 
              (get-in @app-state [:visual-grid i j])]
         ]))])]))
    
(defn mount-root []
  (r/render [mine-sweeper] (.getElementById js/document "app")))

(defn init! []
  (mount-root))

(defn on-js-reload []
  (prn (:visual-grid @app-state)))
