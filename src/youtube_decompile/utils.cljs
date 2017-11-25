(ns youtube-decompile.utils
  (:require [clojure.string :as string]))

(defmulti to-seconds
  "Convert timestamp string to seconds."
  (fn [timestamp]
    (case (get (frequencies timestamp) ":")
      2 :hh-mm-ss
      1 :mm-ss
      0 :ss
      nil)))

(defmethod to-seconds :hh-mm-ss [timestamp]
  (let [[hour minute second] (string/split timestamp #":")]
    (+ (* (js/parseInt hour) 3600)
       (* (js/parseInt minute) 60)
       (js/parseInt second))))

(defmethod to-seconds :mm-ss [timestamp]
  (let [[minute second] (string/split timestamp #":")]
    (+ (* (js/parseInt minute) 60)
       (js/parseInt second))))

(defmethod to-seconds :ss [timestamp]
  (js/parseInt timestamp))

(defmethod to-seconds :default [_]
  nil)
