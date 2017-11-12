(ns youtube-decompile.views.contents
  (:require [rum.core :as rum]))

(rum/defc title []
  [:h1 "YouTube Decompile"])

(rum/defc description []
  [:p "Decompile/split long YouTube videos using timestamps from comment"])

(rum/defc warning []
  [:blockquote
   (str "YouTube text has some problem with line break during copy "
        "pasting. To get them working properly, select all the "
        "comment box including the author informations.")])

(rum/defc contents []
  [:div
   (title)
   (description)
   (warning)])
