(ns youtube-decompile.mixins)

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
                  (when (and (= (-> evt .-data) js/YT.PlayerState.ENDED)
                             @repeat)
                    (-> evt .-target (.seekTo start))))]
       (js/YT.Player. id #js {:events #js {:onReady       play
                                           :onStateChange loop}}))
     state)})
