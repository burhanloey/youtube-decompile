(ns youtube-decompile.core
  (:require [rum.core :as rum]
            [youtube-decompile.views.inputs :refer [inputs]]
            [youtube-decompile.views.outputs :refer [outputs]]))

(rum/defc app []
  [:div.container
   [:div.row
    [:div.column
     (inputs)
     (outputs)]]])

(rum/mount (app) (js/document.getElementById "app"))

(defn on-js-reload []
  )
