(ns clock.render
  (:require [clock.state :as s]))

(def PI Math/PI)
(def PIx2 (* PI 2.0))
(def PIp2 (/ PI 2.0))
(def R (/ PIx2 8.0))

(def circle-style "#ddd")
(def pointer-style "black")

(defn render [{:keys [ctx width height off-x off-y r rp2 clock-size]} ts]
  ; Circles:
  (doto ctx
    (-> (.clearRect 0 0 width height))
    (-> .-strokeStyle (set! circle-style)))
  (doseq [x (range 8)
          y (range 3)]
    (doto ctx
      (.save)
      (.beginPath)
      (.translate (+ off-x rp2 (* x r))
                  (+ off-y rp2 (* y r)))
      (.arc 0 0 clock-size 0 PIx2 true)
      (.stroke)
      (.restore)))
  ; Pointers:
  (let [state (s/update-to ts)]
    (doto ctx
      (-> .-strokeStyle (set! pointer-style))
      (-> .beginPath))
    (doseq [y (range 3)
            x (range 8)
            :let [[a1 a2] (nth state (+ (* y 8) x))]]
      (doto ctx
        (.save)
        (.translate (+ off-x rp2 (* x r))
                    (+ off-y rp2 (* y r)))
        (.rotate a1)
        (.moveTo 0 0)
        (.lineTo clock-size 0)
        (.rotate (+ (* -1.0 a1) a2))
        (.moveTo 0 0)
        (.lineTo clock-size 0)
        (.restore))))
  (-> ctx .stroke))
