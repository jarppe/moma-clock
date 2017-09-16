(ns clock.numbers)

(def ^:const PI Math/PI)
(def ^:const PIx2 (* PI 2.0))
(def ^:const PIp2 (/ PI 2.0))
(def ^:const PIp4 (/ PI 4.0))

(defn ->a [p]
  (* p (/ PIx2 8)))

(def numbers (->> [; 0
                   [2 4] [6 4]
                   [0 4] [0 4]
                   [0 2] [0 6]
                   ; 1
                   [5 5] [4 4]
                   [5 5] [0 4]
                   [5 5] [0 0]
                   ; 2
                   [2 2] [6 4]
                   [2 4] [6 0]
                   [0 2] [6 6]
                   ; 3
                   [2 2] [6 4]
                   [2 2] [6 0]
                   [2 2] [6 0]
                   ; 4
                   [4 4] [4 4]
                   [0 2] [6 0]
                   [5 5] [0 0]
                   ; 5
                   [2 4] [6 6]
                   [0 2] [6 4]
                   [2 2] [0 6]
                   ; 6
                   [2 4] [6 6]
                   [0 4] [6 4]
                   [2 0] [0 6]
                   ; 7
                   [2 2] [6 4]
                   [5 5] [0 4]
                   [5 5] [0 0]
                   ; 8
                   [2 4] [6 4]
                   [2 4] [6 4]
                   [2 0] [6 0]
                   ; 9
                   [2 4] [6 4]
                   [0 2] [0 4]
                   [2 2] [6 0]]
                  (mapcat identity)
                  (map ->a)
                  (partition 12)
                  (into [])))

(defn get-number-a [now pointer-index]
  (let [digit (nth now (-> pointer-index (mod 16) (/ 4) Math/floor))
        number (nth numbers digit)
        x (-> pointer-index (mod 4) Math/floor)
        y (-> pointer-index (/ 4) Math/floor (mod 3))]
    (nth number (+ x (* y 4)))))
