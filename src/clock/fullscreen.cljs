(ns clock.fullscreen)

#_(defn request-fullscreen [element]
    (when-let [rfs (->> ["requestFullscreen"
                         "webkitRequestFullscreen"
                         "webkitRequestFullScreen"
                         "mozRequestFullScreen"
                         "msRequestFullscreen"]
                        (some (fn [fn-name]
                                (aget element fn-name))))]
      (.call rfs element)))

(defn fullscreen-element []
  (->> ["fullscreenElement"
        "webkitFullscreenElement"
        "webkitFullScreenElement"
        "mozFullScreenElement"
        "msFullscreenElement"]
       (some (fn [prop-name]
               (aget js/document prop-name)))))

(defn fullscreen? []
  (let [r (boolean (fullscreen-element))]
    (js/console.log "fullscreen?" r)
    r))

(defn request-fullscreen []
  (let [element (.-documentElement js/document)]
    (when-let [rfs (->> ["requestFullscreen"
                         "webkitRequestFullscreen"
                         "webkitRequestFullScreen"
                         "mozRequestFullScreen"
                         "msRequestFullscreen"]
                        (some (fn [fn-name]
                                (aget element fn-name))))]
      (js/console.log "request-fullscreen" rfs)
      (.call rfs element))))

(defn exit-fullscreen []
  (when-let [efs (->> ["exitFullscreen"
                       "webkitExitFullscreen"
                       "webkitExitFullScreen"
                       "mozCancelFullScreen"
                       "msExitFullscreen"]
                      (some (fn [fn-name]
                              (aget js/document fn-name))))]
    (js/console.log "exit-fullscreen" efs)
    (.call efs js/document)))

(defn toggle-fullscreen []
  (if (fullscreen?)
    (exit-fullscreen)
    (request-fullscreen)))

; function toggleFullScreen() {
;   if (!document.fullscreenElement) {
;     document.documentElement.requestFullscreen();
;   } else {
;     if (document.exitFullscreen) {
;       document.exitFullscreen();
;     }
;   }
; }

