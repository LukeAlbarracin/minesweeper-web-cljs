(ns minesweeper-web-cljs.prod
  (:require
    [minesweeper-web-cljs.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
