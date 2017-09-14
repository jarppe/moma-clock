(ns clock.render
  (:require [clock.state :as s]
            [clock.numbers :as n]))

(def ^:const circle-style "#333")
(def ^:const pointer-style "#aaa")

(defn clear [{:keys [ctx width height] :as state}]
  (.clearRect ctx 0 0 width height)
  state)

(defn render-rims [{:keys [ctx width height off-x off-y r rp2 clock-size] :as state}]
  (-> ctx .-strokeStyle (set! circle-style))
  (doseq [y (range 3)
          x (range 8)]
    (doto ctx
      (.save)
      (.beginPath)
      (.translate (+ off-x rp2 (* x r))
                  (+ off-y rp2 (* y r)))
      (.arc 0 0 clock-size 0 n/PIx2 true)
      (.stroke)
      (.restore))))

(defn render-pointer [{:keys [ctx off-x off-y r rp2 clock-size]} x y {:keys [a]}]
  (doto ctx
    (.save)
    (.translate (+ off-x rp2 (* x r))
                (+ off-y rp2 (* y r)))
    (.rotate (- n/PIp2))
    (.rotate a)
    (.moveTo 0 0)
    (.lineTo clock-size 0)
    (.restore)))

(defn render-pointers [{:keys [ctx off-x off-y r rp2 clock-size]} pointers]
  (-> ctx .-strokeStyle (set! pointer-style))
  (-> ctx .-lineWidth (set! 2.0))
  (-> ctx .beginPath)
  (doseq [pointer-index (range 0 (* 8 3 2) 2)
          :let [x (-> pointer-index (/ 2) Math/floor (mod 8))
                y (-> pointer-index (/ 4) Math/floor (mod 3))
                a1 (-> pointers (nth pointer-index) :a)
                a2 (-> pointers (nth (inc pointer-index)) :a)]]
    (doto ctx
      (.save)
      (.translate (+ off-x rp2 (* x r))
                  (+ off-y rp2 (* y r)))
      (.rotate (- a1 n/PIp2))
      (.moveTo 0 0)
      (.lineTo clock-size 0)
      (.rotate (+ (- a1) a2))
      (.moveTo 0 0)
      (.lineTo clock-size 0)
      (.restore)))
  (-> ctx .stroke))

(defn show-time [{:keys [ctx]}]
  (let [date (js/Date.)
        h (.getHours date)
        m (.getMinutes date)
        s (.getSeconds date)
        time (str (if (< h 10) "0")
                  h ":"
                  (if (< m 10) "0")
                  m ":"
                  (if (< s 10) "0")
                  s)]
    (doto ctx
      (-> (.clearRect 0 0 200 20))
      (-> (.fillText time 10 10)))))

(defn render [state force?]
  (when-let [pointers (s/pointers force?)]
    (doto state
      (clear)
      (render-rims)
      (render-pointers pointers)))
  #_(show-time state))

(comment
  (doseq [pointer-index (range (* 8 3 2))
          y (range 3)
          x (range 8)
          :let [pointer
                index (+ (* y 16) (* x 2))
                a1 (-> pointers (nth index) :a)
                a2 (-> pointers (nth (inc index)) :a)]]
    (doto ctx
      (.save)
      (.translate (+ off-x rp2 (* x r))
                  (+ off-y rp2 (* y r)))
      (.rotate (- n/PIp2))
      (.rotate a1)
      (.moveTo 0 0)
      (.lineTo clock-size 0)
      (.rotate (+ (- a1) a2))
      (.moveTo 0 0)
      (.lineTo clock-size 0)
      (.restore))))