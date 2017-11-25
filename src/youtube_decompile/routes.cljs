(ns youtube-decompile.routes
  (:require [youtube-decompile.app-state :as state]
            [youtube-decompile.utils :refer [parse-timestamps]]
            [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

(secretary/set-config! :prefix "#")

(defroute decompile "/splitting" [query-params]
  (swap! state/app-state merge query-params)
  (reset! state/splitted-videos (parse-timestamps @state/app-state)))

(defn hook-browser-navigation! []
  (let [h (History.)]
    (events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
    (doto h (.setEnabled true))))
