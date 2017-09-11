(ns clock.state
  (:require [clock.numbers :as n]))

(def PI Math/PI)
(def PIx2 (* Math/PI 2.0))
(def PIp2 (/ Math/PI 2.0))

; a1 (- (* a1 R) PIp2)
; a2 (- (* a2 R) PIp2)

(defn initial-state []
  (-> (repeat (* 8 3) [0 0])
      (into [])))

(defn now []
  (let [d (js/Date.)
        hours (.getHours d)
        minutes (.getMinutes d)
        h1 (Math/floor (/ hours 10.0))
        h2 (- hours (* h1 10))
        m1 (Math/floor (/ minutes 10.0))
        m2 (- minutes (* m1 10))]
    [h1 h2 m1 m2 (.getSeconds d)]))

(defonce state (atom {:clocks (initial-state)
                      :correct (initial-state)
                      :mode :run
                      :prev-ts 0}))

(defn update-clocks [{:keys [clocks mode prev-ts correct]} ts now]
  (let [td (- ts prev-ts)]
    {:clocks (->> clocks
                  (map (fn [[a1 a2]]
                         [(+ a1 0.01)
                          (+ a2 0.02)]))
                  (into []))
     :mode mode
     :prev-ts ts
     :correct correct}))


(comment
  (->> (for [y (range 3)
             x (range 8)]
         (n/pointers now x y))
       (into [])))

(defn update-to [ts]
  (let [n (now)]
    (-> state
        (swap! update-clocks ts n)
        (:clocks))))
