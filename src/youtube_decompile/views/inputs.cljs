(ns youtube-decompile.views.inputs
  (:require [rum.core :as rum]
            [youtube-decompile.routes :as routes]
            [youtube-decompile.app-state :as state]
            [youtube-decompile.utils :refer [parse-timestamps]]))

(rum/defc youtube-url-input < rum/reactive []
  [:div
   [:label {:for "youtube-url"} "YouTube URL:"]
   [:input#youtube-url
    {:type "text"
     :onChange #(reset! state/youtube-url (-> % .-target .-value))
     :value (rum/react state/youtube-url)}]])

(rum/defc guide-text []
  [:p (str "YouTube text has some problem with line break during copy "
           "pasting. To get them working properly, select all the "
           "comment box including the author informations.")])

(rum/defc force-at-zero-button < rum/reactive []
  (let [text (if (rum/react state/force-at-zero?) "Yes" "No")]
    [:div.row
     [:div.column
      [:button.button-outline
       {:onClick #(swap! state/force-at-zero? not)}
       (str "Force video to start from beginning: " text)]]]))

(rum/defc timestamps-input < rum/reactive []
  [:div
   [:label {:for "timestamps"} "Timestamps:"]
   [:textarea#timestamps
    {:onChange #(reset! state/timestamps (-> % .-target .-value))
     :value (rum/react state/timestamps)}]])

(rum/defc decompile-button < rum/reactive []
  (let [current-state (rum/react state/app-state)]
    [:a.button
     {:href (routes/decompile
             {:query-params (select-keys current-state [:youtube-url
                                                        :timestamps])})}
     "Decompile"]))

(rum/defc inputs []
  [:div
   (youtube-url-input)
   (guide-text)
   (timestamps-input)
   (force-at-zero-button)
   (decompile-button)])
