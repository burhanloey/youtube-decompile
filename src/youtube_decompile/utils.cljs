(ns youtube-decompile.utils
  (:require [clojure.string :as string]))

(def timestamp-regex #"(?:(?:([01]?\d|2[0-3]):)?([0-5]?\d):)?([0-5]?\d)")

(defn to-seconds
  "Convert timestamp string to seconds."
  [timestamp]
  (let [colon-count (get (frequencies timestamp) ":")]
    (case colon-count
      2 (let [[hour minute second] (string/split timestamp #":")]
          (+ (* (js/parseInt hour) 3600)
             (* (js/parseInt minute) 60)
             (js/parseInt second)))
      1 (let [[minute second] (string/split timestamp #":")]
          (+ (* (js/parseInt minute) 60)
             (js/parseInt second)))
      0 (js/parseInt timestamp)
      nil)))

(defn parse-timestamps
  "Parse the inputs of timestamps.

  Return a list of :title, :start, and :end map."
  [timestamps]
  (let [lines            (string/split timestamps #"\n")
        starts           (map #(first (re-find timestamp-regex %)) lines)
        ends             (as-> (filter identity starts) %
                           (drop 1 %)
                           (vec %)
                           (conj % nil)) ; nil = until the end of video
        lines-and-starts (->> (map vector lines starts)
                              (filter #(second %)))]
    (->> (map conj lines-and-starts ends)
         (map #(hash-map :title (first %)
                         :start (to-seconds (second %))
                         :end   (dec (to-seconds (last %))))))))

(defn parse-video-id
  "Parse Youtube video id from url."
  [url]
  (second (re-find #"v=([^&]*)" url)))
