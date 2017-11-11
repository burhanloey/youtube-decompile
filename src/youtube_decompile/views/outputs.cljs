(ns youtube-decompile.views.outputs
  (:require [rum.core :as rum]
            [youtube-decompile.app-state :as state]
            [youtube-decompile.mixins :refer [loop-video]]
            [youtube-decompile.utils :refer [parse-video-id]]))

(rum/defc video-frame < loop-video
  [_ {:keys [start end]}]
  (let [start' (if (and (zero? start) @state/force-at-zero?) 1 start)]
    [:iframe {:id (str "iframe-" start)
              :width 560 :height 315
              :src (str "https://www.youtube.com/embed/"
                        (parse-video-id @state/youtube-url)
                        "?start=" start' "&end=" end "&enablejsapi=1")
              :frameBorder 0 :allowFullScreen true}]))

(rum/defcs splitted-video-item < (rum/local false ::display-video)
                               < (rum/local false ::repeat-video)
  [state {:keys [title start end] :as data}]
  (let [display-video? (::display-video state)
        repeat-video? (::repeat-video state)
        repeat-text (if @repeat-video? "repeat: yes" "repeat: no")]
    [:div
     [:h3
      title
      [:button
       {:style {:margin "0 7px"}
        :onClick #(reset! display-video? true)} "play"]
      [:button.button-outline
       {:onClick #(swap! repeat-video? not)}
       repeat-text]
      [:button.button-clear
       {:onClick #(reset! display-video? false)} "close"]]
     (when @display-video?
       (video-frame {:repeat repeat-video?} data))]))

(rum/defc outputs < rum/reactive []
  [:div
   (for [{:keys [start] :as splitted-data} (rum/react state/splitted-videos)]
     (-> (splitted-video-item splitted-data)
         (rum/with-key start)))])
