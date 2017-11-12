(ns youtube-decompile.core
  (:require [rum.core :as rum]
            [youtube-decompile.routes :refer [hook-browser-navigation!]]
            [youtube-decompile.views.inputs :refer [inputs]]
            [youtube-decompile.views.outputs :refer [outputs]]))

(hook-browser-navigation!)

(rum/defc app []
  [:div.container
   [:div.row
    [:div.column
     (inputs)
     (outputs)]]])

(rum/mount (app) (js/document.getElementById "app"))

(defn on-js-reload []
  )
