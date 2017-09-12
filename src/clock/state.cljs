(ns clock.state
  (:require [clock.numbers :as n]))

(defn initial-state []
  (-> (repeat (* 8 3) [0 0])
      (into [])))

(defn get-time []
  (let [d (js/Date.)
        hours (.getHours d)
        minutes (.getMinutes d)
        seconds (.getSeconds d)
        h1 (Math/floor (/ hours 10.0))
        h2 (- hours (* h1 10))
        m1 (Math/floor (/ minutes 10.0))
        m2 (- minutes (* m1 10))
        s1 (Math/floor (/ seconds 10.0))
        s2 (- seconds (* s1 10))]
    [h1 h2 m1 m2 s1 s2]
    #_[m1 m2 s1 s2]))

(defonce state (atom {:clocks (initial-state)
                      :mode :trace-time}))

(defn show-time [{:keys [clocks] :as state}]
  (let [now (get-time)]
    (assoc state :clocks (->> (for [y (range 3)
                                    x (range 8)]
                                (n/pointers now x y))
                              (into [])))))

(def T 0.1)

(defn zeroish? [v]
  (-> v Math/abs (< T)))

(defn trace-a-dist [t a]
  (let [d (- t a)]
    (cond
      (< (Math/abs d) T) 0.0
      (neg? d) (+ d n/PIx2)
      :else d)))

(defn trace-time [{:keys [clocks pointer-modes] :as state}]
  (let [now (get-time)]

    (->> (for [y (range 3)
               x (range 8)]
           (let [index (+ (* 8 y) x)
                 [t1 t2] (n/pointers now x y)
                 [a1 a2] (nth clocks index)
                 p1 (-> a1 (- 0.003) (mod n/PIx2))
                 p2 (-> a2 (- 0.002) (mod n/PIx2))]
             #_(when (and (zero? x) (zero? y))
                 (js/console.log "t1" t1 "p1" p1 "d" (- t1 p1) "z?" (zeroish? (- t1 p1))))
             [(if (zeroish? (- t1 p1)) t1 p1)
              (if (zeroish? (- t2 p2)) t2 p2)]))
         (into []))))

(defn rotate-slowly [state]
  (update state :clocks (fn [clocks]
                          (->> clocks
                               (map (fn [[a1 a2]]
                                      [(-> a1 (+ 0.03) (mod n/PIx2))
                                       (-> a2 (+ 0.02) (mod n/PIx2))]))
                               (into [])))))

(def modes {:show-time show-time
            :rotate-slowly rotate-slowly
            :trace-time trace-time})

(defn update-clocks [{:keys [mode] :as state} ts]
  (let [move-fn trace-time #_(modes mode trace-time)]
    (move-fn state)))

(defn update-pointers-to [ts]
  (-> state
      (swap! update-clocks ts)
      (:clocks)))
