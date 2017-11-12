(ns youtube-decompile.core
  (:require [rum.core :as rum]
            [youtube-decompile.routes :refer [hook-browser-navigation!]]
            [youtube-decompile.views.contents :refer [contents]]
            [youtube-decompile.views.inputs :refer [inputs]]
            [youtube-decompile.views.outputs :refer [outputs]]))

(hook-browser-navigation!)

(rum/defc app []
  [:div.container
   [:div.row
    (contents)]
   [:div.row
    [:div.pure-u-md-2-5
     [:div.column (inputs)]]
    [:div.pure-u-md-3-5
     [:div.column (outputs)]]]])

(rum/mount (app) (js/document.getElementById "app"))

(defn on-js-reload []
  )
