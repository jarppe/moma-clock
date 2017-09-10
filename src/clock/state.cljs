(ns clock.state
  (:require [clock.numbers :as n]))

(def PI Math/PI)
(def PIx2 (* Math/PI 2.0))
(def PIp2 (/ Math/PI 2.0))
(def R (/ PIx2 8.0))

; a1 (- (* a1 R) PIp2)
; a2 (- (* a2 R) PIp2)

(defn initial-state []
  (-> (repeat (* 8 3) [0 0])
      (into [])))

(defonce state (atom {:clocks (initial-state)
                      :mode :run
                      :prev-ts 0}))

(defn update-clocks [{:keys [clocks mode prev-ts]} ts]
  (let [td (- ts prev-ts)]
    {:clocks (->> clocks
                  (map (fn [[a1 a2]]
                         [(* PIx2 (/ ts 5000.0))
                          (* PIx2 (/ ts 4000.0))]))
                  (into []))
     :mode mode
     :prev-ts ts}))

(defn update-to [ts]
  (-> state
      (swap! update-clocks ts)
      (:clocks)))
