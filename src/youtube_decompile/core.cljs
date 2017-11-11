(ns youtube-decompile.core
  (:require [rum.core :as rum]
            [youtube-decompile.mixins :refer [loop-video]]
            [youtube-decompile.utils :refer [parse-timestamps
                                             parse-video-id]]))

(defonce app-state (atom {}))

(def youtube-url     (rum/cursor-in app-state [:youtube-url]))
(def timestamps      (rum/cursor-in app-state [:timestamps]))
(def splitted-videos (rum/cursor-in app-state [:splitted-videos]))

(rum/defc inputs []
  [:div
   [:label {:for "youtube-url"} "YouTube URL: "]
   [:input#youtube-url
    {:type "text"
     :onChange #(reset! youtube-url (-> % .-target .-value))}]
   [:p (str "YouTube text has some problem with line break during copy "
            "pasting. To get them working properly, select all the "
            "comment box including the author informations.")]
   [:p (str "Also, when the timestamp is 0 seconds, "
            "it will start from the last point you stopped. Put at "
            "least one second for the timestamp to counter it.")]
   [:label {:for "timestamps"} "Timestamps:"]
   [:textarea#timestamps
    {:onChange #(reset! timestamps (-> % .-target .-value))}]
   [:button
    {:onClick #(reset! splitted-videos (parse-timestamps @timestamps))}
    "Decompile"]])

(rum/defc video-frame < loop-video
  [{:keys [start end repeat]}]
  [:iframe {:id (str "iframe-" start)
            :width 560 :height 315
            :src (str "https://www.youtube.com/embed/"
                      (parse-video-id @youtube-url)
                      "?start=" start "&end=" end "&enablejsapi=1")
            :frameBorder 0 :allowFullScreen true}])

(rum/defcs splitted-video-item < (rum/local false ::display-video)
                               < (rum/local false ::repeat-video)
  [state {:keys [title start end] :as data}]
  (let [display-video? (::display-video state)
        repeat-video? (::repeat-video state)]
    [:div
     [:h3
      title
      [:button
       {:style {:margin "0 7px"}
        :onClick #(reset! display-video? true)} "play"]
      [:button.button-outline
       {:onClick #(swap! repeat-video? not)}
       (if @repeat-video?
         "repeat: yes"
         "repeat: no")]
      [:button.button-clear
       {:onClick #(reset! display-video? false)} "close"]]
     (when @display-video?
       (video-frame (assoc data :repeat repeat-video?)))]))

(rum/defc outputs < rum/reactive []
  [:div
   (for [{:keys [start] :as splitted-data} (rum/react splitted-videos)]
     (-> (splitted-video-item splitted-data)
         (rum/with-key start)))])

(rum/defc app []
  [:div.container
   [:div.row
    [:div.column
     (inputs)
     (outputs)]]])

(rum/mount (app) (js/document.getElementById "app"))

(defn on-js-reload []
  )
