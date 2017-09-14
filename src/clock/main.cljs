(ns clock.main
  (:require [goog.events :as e]
            [clock.render :as r]
            [clock.fullscreen :as fs]))

(defonce ctx (atom nil))

(defn make-ctx [width height]
  (let [r (Math/min (/ width 8.0) (/ height 3.0))
        off-x (/ (- width (* r 8.0)) 2.0)
        off-y (/ (- height (* r 3.0)) 2.0)
        clock-size (* (/ r 2.0) 0.94)]
    {:width width
     :height height
     :off-x off-x
     :off-y off-y
     :r r
     :rp2 (/ r 2.0)
     :clock-size clock-size}))

(defn reset-ctx! []
  (js/console.log "reset-ctx!")
  (let [canvas (js/document.getElementById "app")
        width (.-clientWidth canvas)
        height (.-clientHeight canvas)]
    (doto canvas
      (-> .-width (set! width))
      (-> .-height (set! height)))
    (reset! ctx (-> (make-ctx width height)
                    (assoc :ctx (.getContext canvas "2d"))))))

(defn on-dblclick [e]
  (.preventDefault e)
  (.stopPropagation e)
  (reset-ctx!)
  (fs/toggle-fullscreen))

(defn init! []
  (when-not @ctx
    (js/console.log "init")
    (.addEventListener js/window "resize" reset-ctx!)
    (.addEventListener js/document "dblclick" on-dblclick)
    (reset-ctx!)
    ((fn animation [ts]
       (r/render @ctx ts)
       (js/window.requestAnimationFrame animation)) 0)))

(init!)
