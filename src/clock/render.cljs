(ns clock.render
  (:require [clock.state :as s]
            [clock.numbers :as n]))

(def ^:const circle-style "#f2f2f2")
(def ^:const pointer-style "#404040")

(defn clear [{:keys [ctx width height] :as state}]
  (.clearRect ctx 0 0 width height)
  state)

(defn render-rims [{:keys [ctx width height off-x off-y r rp2 clock-size] :as state}]
  (-> ctx .-strokeStyle (set! circle-style))
  (doseq [x (range 8)
          y (range 3)]
    (doto ctx
      (.save)
      (.beginPath)
      (.translate (+ off-x rp2 (* x r))
                  (+ off-y rp2 (* y r)))
      (.arc 0 0 clock-size 0 n/PIx2 true)
      (.stroke)
      (.restore))))

(defn render-pointers [{:keys [ctx off-x off-y r rp2 clock-size] :as state} pointers]
  (-> ctx .-strokeStyle (set! pointer-style))
  (-> ctx .-lineWidth (set! 2.0))
  (-> ctx .beginPath)
  (doseq [y (range 3)
          x (range 8)
          :let [index (+ (* y 8) x)
                [a1 a2] (nth pointers index)]]
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
      (.restore)))
  (-> ctx .stroke))

(defn render [{:keys [ctx width height off-x off-y r rp2 clock-size] :as state} ts]
  ; Circles:
  (doto state
    (clear)
    (render-rims)
    (render-pointers (s/update-pointers-to ts))))
