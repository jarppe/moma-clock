(ns clock.fullscreen)

(defn fullscreen-element []
  (->> ["fullscreenElement"
        "webkitFullscreenElement"
        "webkitFullScreenElement"
        "mozFullScreenElement"
        "msFullscreenElement"]
       (some (fn [prop-name]
               (aget js/document prop-name)))))

(defn fullscreen? []
  (boolean (fullscreen-element)))

(defn request-fullscreen []
  (let [element (.-documentElement js/document)]
    (when-let [rfs (->> ["requestFullscreen"
                         "webkitRequestFullscreen"
                         "webkitRequestFullScreen"
                         "mozRequestFullScreen"
                         "msRequestFullscreen"]
                        (some (fn [fn-name]
                                (aget element fn-name))))]
      (.call rfs element))))

(defn exit-fullscreen []
  (when-let [efs (->> ["exitFullscreen"
                       "webkitExitFullscreen"
                       "webkitExitFullScreen"
                       "mozCancelFullScreen"
                       "msExitFullscreen"]
                      (some (fn [fn-name]
                              (aget js/document fn-name))))]
    (.call efs js/document)))

(defn toggle-fullscreen []
  (if (fullscreen?)
    (exit-fullscreen)
    (request-fullscreen)))
