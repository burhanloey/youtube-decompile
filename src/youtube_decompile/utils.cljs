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

(defn first-timestamp
  "Get first matched timestamp from string."
  [line]
  (first (re-find timestamp-regex line)))

(defn last-timestamp
  "Get last matched timestamp from string."
  [line]
  (first (last (re-seq timestamp-regex line))))

(defn count'
  "Count timestamps using :using function. timestamps is a list of
  tuple(line, timestamp) like tuple(stack, needle)."
  [timestamps & {:keys [using]}]
  (->> timestamps
       (filter second)
       (filter #(apply using %))
       count))

(defn where-is-timestamp?
  "Check whether the timestamps are on right side. Check by majority. lefties
  and righties are lists of tuple(line, timestamp) like tuple(stack,needle).
  Returns selected parameter."
  [lefties & {righties :or}]
  (let [lefties-count  (count' lefties :using string/starts-with?)
        righties-count (count' righties :using string/ends-with?)]
    (if (> righties-count lefties-count)
      righties
      lefties)))

(defn lookup-timestamp
  "Lookup timestamp from string."
  [lines]
  (let [lefties  (map #(vector % (first-timestamp %)) lines)
        righties (map #(vector % (last-timestamp %)) lines)
        selected (where-is-timestamp? lefties :or righties)]
    (map second selected)))

(defn parse-timestamps
  "Parse the inputs of timestamps.

  Return a list of :title, :start, and :end map."
  [timestamps]
  (let [lines            (string/split timestamps #"\n")
        starts           (lookup-timestamp lines)
        lines-and-starts (->>  (map vector lines starts) ; remove nil only after
                               (filter second))          ; assoc w/ its origin line
        ends             (as-> (filter identity starts) %
                           (drop 1 %)
                           (vec %)
                           (conj % nil)) ; nil = until the end of video
        ]
    (->> (map conj lines-and-starts ends)
         (map #(hash-map :title (first %)
                         :start (to-seconds (second %))
                         :end   (dec (to-seconds (last %))))))))

(defn parse-video-id
  "Parse Youtube video id from url."
  [url]
  (second (re-find #"v=([^&]*)" url)))
