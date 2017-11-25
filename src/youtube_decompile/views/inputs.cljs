(ns youtube-decompile.views.inputs
  (:require [rum.core :as rum]
            [youtube-decompile.routes :as routes]
            [youtube-decompile.app-state :as state]))

(rum/defc youtube-url-input < rum/reactive []
  [:div
   [:label {:for "youtube-url"} "YouTube URL:"]
   [:input#youtube-url
    {:type "text"
     :onChange #(reset! state/youtube-url (-> % .-target .-value))
     :value (rum/react state/youtube-url)}]])

(defn swap-location [current-text]
  (if (= current-text "left")
    "right"
    "left"))

(rum/defc timestamp-location-button < rum/reactive []
  (let [text (rum/react state/timestamp-location)]
    [:button.button-outline
     {:onClick #(swap! state/timestamp-location swap-location)}
     (str "Timestamp location: " text)]))

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
                                                        :timestamps
                                                        :timestamp-location])})}
     "Decompile"]))

(rum/defc inputs []
  [:div
   (youtube-url-input)
   (timestamps-input)
   (timestamp-location-button)
   (force-at-zero-button)
   (decompile-button)])
