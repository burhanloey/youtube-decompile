(ns youtube-decompile.mixins)

(def YTPlayer      (aget js/YT "Player"))
(def +ended-state+ (aget js/YT "PlayerState" "ENDED"))

(def loop-video
  ;; A mixin to loop YouTube video when :repeat state is set to true.
  {:did-mount
   (fn [state]
     (let [[{:keys [start repeat]}] (:rum/args state)
           id   (-> (:rum/react-component state)
                    js/ReactDOM.findDOMNode
                    (aget "id"))
           play (fn [evt] (-> evt .-target .playVideo))
           loop (fn [evt]
                  (when (and (= (-> evt .-data) +ended-state+)
                             @repeat)
                    (-> evt .-target (.seekTo start))))]
       (YTPlayer. id #js {:events #js {:onReady       play
                                       :onStateChange loop}}))
     state)})
