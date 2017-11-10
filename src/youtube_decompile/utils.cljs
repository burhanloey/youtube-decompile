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

(defn where-is-timestamp?
  "Check whether the timestamps are on right side. Check by majority. lefties
  and righties are lists of tuple(stack,needle). Returns selected parameter."
  [& {:keys [lefties righties]}]
  (let [lefties-count  (->> lefties
                            (filter #(apply string/starts-with? %))
                            count)
        righties-count (->> righties
                            (filter #(apply string/ends-with? %))
                            count)]
    (if (> righties-count lefties-count)
      righties
      lefties)))

(defn lookup-timestamp
  "Lookup timestamp from string."
  [lines]
  (let [lefties  (->> lines
                      (map #(vector % (first-timestamp %)))
                      (filter second))
        righties (->> lines
                      (map #(vector % (last-timestamp %)))
                      (filter second))
        selected (where-is-timestamp? :lefties lefties
                                      :righties righties)]
    (map second selected)))

(defn parse-timestamps
  "Parse the inputs of timestamps.

  Return a list of :title, :start, and :end map."
  [timestamps]
  (let [lines            (string/split timestamps #"\n")
        starts           (lookup-timestamp lines)
        ends             (as-> (filter identity starts) %
                           (drop 1 %)
                           (vec %)
                           (conj % nil)) ; nil = until the end of video
        lines-and-starts (->> (map vector lines starts)
                              (filter second))]
    (js/console.log (cljs.pprint/pprint (->> (map conj lines-and-starts ends)
                                             (map #(hash-map :title (first %)
                                                             :start (to-seconds (second %))
                                                             :end   (dec (to-seconds (last %))))))))
    (->> (map conj lines-and-starts ends)
         (map #(hash-map :title (first %)
                         :start (to-seconds (second %))
                         :end   (dec (to-seconds (last %))))))))

(defn parse-video-id
  "Parse Youtube video id from url."
  [url]
  (second (re-find #"v=([^&]*)" url)))
