(ns youtube-decompile.views.outputs
  (:require [clojure.string :as string]
            [rum.core :as rum]
            [youtube-decompile.app-state :as state]
            [youtube-decompile.mixins :refer [loop-video]]
            [youtube-decompile.parsers :refer [parse-video-id]]))

(rum/defc play-button [{:keys [display-video]}]
  [:button
   {:style {:margin "0 7px"}
    :onClick #(reset! display-video true)} "play"])

(rum/defc repeat-button [{:keys [repeat-video]}]
  (let [text (if @repeat-video "repeat: yes" "repeat: no")]
    [:button.button-outline
     {:onClick #(swap! repeat-video not)}
     text]))

(rum/defc close-button [{:keys [display-video]}]
  [:button.button-clear
   {:onClick #(reset! display-video false)} "close"])

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
  [:div
   [:h4 title]
   [:div
    (play-button {:display-video (::display-video state)})
    (repeat-button {:repeat-video (::repeat-video state)})
    (close-button {:display-video (::display-video state)})]
   (when @(::display-video state)
     (video-frame {:repeat (::repeat-video state)} data))
   [:hr]])

(defn match-title
  "Returns a function to check whether title contains search-string. Regex is
  case-insensitive."
  [search-string]
  (fn [{:keys [title]}]
    (re-find (re-pattern (str "(?i)" search-string)) title)))

(rum/defcs outputs < rum/reactive
                   < (rum/local "" ::filter)
  [state]
  (let [search-string (::filter state)
        splitted-data (rum/react state/splitted-videos)
        filtered-data (if (string/blank? @search-string)
                        splitted-data
                        (filter (match-title @search-string) splitted-data))]
    [:div
     [:div.row
      [:label.column "Splitted videos:"]
      [:input.column {:type "text" :placeholder "Search..."
                      :onChange #(reset! search-string (-> % .-target .-value))}]]
     (for [{:keys [title] :as data} filtered-data]
       (-> (splitted-video-item data)
           (rum/with-key title)))]))
