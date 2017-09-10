(ns clock.numbers)

(def numbers [; 0
              [[2 4] [6 4]
               [0 4] [0 4]
               [0 2] [0 6]]
              ; 1
              [[5 5] [4 4]
               [5 5] [0 4]
               [5 5] [0 0]]
              ; 2
              [[2 2] [6 4]
               [2 4] [6 0]
               [0 2] [6 6]]
              ; 3
              [[2 2] [6 4]
               [2 2] [6 0]
               [2 2] [6 0]]
              ; 4
              [[4 4] [4 4]
               [0 2] [6 0]
               [5 5] [0 0]]
              ; 5
              [[2 4] [6 6]
               [0 2] [6 4]
               [2 2] [0 6]]
              ; 6
              [[2 4] [6 6]
               [0 4] [6 4]
               [2 0] [0 6]]
              ; 7
              [[2 2] [6 4]
               [5 5] [0 4]
               [5 5] [0 0]]
              ; 8
              [[2 4] [6 4]
               [2 4] [6 4]
               [2 0] [6 0]]
              ; 9
              [[2 4] [6 4]
               [0 2] [0 4]
               [2 2] [6 0]]])

(defn pointers [now x y]
  (let [n (->> (Math/floor (/ x 2.0))
               (nth now)
               (nth numbers))
        y' (mod y 3)
        x' (mod x 2)]
    (nth n (+ (* 2 y') x'))))


(defn now []
  (let [d (js/Date.)
        hours (.getHours d)
        minutes (.getMinutes d)
        h1 (Math/floor (/ hours 10.0))
        h2 (- hours (* h1 10))
        m1 (Math/floor (/ minutes 10.0))
        m2 (- minutes (* m1 10))]
    [h1 h2 m1 m2]))
