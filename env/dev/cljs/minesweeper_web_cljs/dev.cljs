(ns ^:figwheel-no-load minesweeper-web-cljs.dev
  (:require
    [minesweeper-web-cljs.core :as core]
    [devtools.core :as devtools]))


(enable-console-print!)

(devtools/install!)

(core/init!)
