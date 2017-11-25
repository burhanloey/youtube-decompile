(ns youtube-decompile.app-state
  (:require [rum.core :as rum]))

(defonce app-state (atom {:youtube-url ""
                          :timestamps ""
                          :force-at-zero true
                          :timestamp-location "left"}))

(def youtube-url        (rum/cursor-in app-state [:youtube-url]))
(def timestamps         (rum/cursor-in app-state [:timestamps]))
(def splitted-videos    (rum/cursor-in app-state [:splitted-videos]))
(def force-at-zero?     (rum/cursor-in app-state [:force-at-zero]))
(def timestamp-location (rum/cursor-in app-state [:timestamp-location]))
