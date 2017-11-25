(ns youtube-decompile.utils
  (:require [clojure.string :as string]))

(def timestamp-regex #"(?:(?:([01]?\d|2[0-3]):)?([0-5]?\d):)?([0-5]?\d)")

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

(defn first-timestamp
  "Get first matched timestamp from string."
  [line]
  (first (re-find timestamp-regex line)))

(defn last-timestamp
  "Get last matched timestamp from string."
  [line]
  (first (last (re-seq timestamp-regex line))))

(defn lookup-timestamp
  "Lookup timestamp from string."
  [& {:keys [lines timestamp-location]}]
  (let [lookup-fn (if (= timestamp-location "right")
                    last-timestamp
                    first-timestamp)]
    (map lookup-fn lines)))

(defn parse-timestamps
  "Parse the inputs of timestamps.

  Return a list of :title, :start, and :end map."
  [{:keys [timestamps timestamp-location]}]
  (let [lines            (string/split timestamps #"\n")
        starts           (lookup-timestamp :lines lines
                                           :timestamp-location timestamp-location)
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
  "Parse YouTube video id from url."
  [url]
  (second (re-find #"v=([^&]*)" url)))
