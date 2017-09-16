(ns clock.state
  (:require [clock.numbers :as n]))

(defn get-time []
  (let [d (js/Date.)
        hours (.getHours d)
        minutes (.getMinutes d)
        seconds (.getSeconds d)
        ms (.getMilliseconds d)
        h1 (Math/floor (/ hours 10.0))
        h2 (- hours (* h1 10))
        m1 (Math/floor (/ minutes 10.0))
        m2 (- minutes (* m1 10))
        s1 (Math/floor (/ seconds 10.0))
        s2 (- seconds (* s1 10))
        ds (Math/floor (/ ms 100.0))]
    [h1 h2 m1 m2 s1 s2 ds]))

(def stay identity)

(defn run [{:keys [a v] :as pointer}]
  (assoc pointer :a (+ a v)))

(defn norm [a]
  (if (> a n/PI)
    (- a n/PIx2)
    a))

(defn cross-over? [t a a' v]
  (let [comp (if (pos? v) <= >=)]
    (comp (-> a (- t) (norm))
          0
          (-> a' (- t) (norm)))))

(defn trace [{:keys [t a v] :as pointer}]
  (let [a' (-> a (+ v) (mod n/PIx2))]
    (if (cross-over? t a a' v)
      (-> pointer
          (assoc :mode stay)
          (assoc :a t))
      (-> pointer
          (assoc :a a')))))

(def initial-pointer {:a 0.00
                      :v 0.03
                      :t 0.0
                      :mode run})

(defn initial-pointers []
  (-> (repeat (* 8 3 2) initial-pointer)
      (into [])))

(defonce state (atom {:pointers (initial-pointers)
                      :now nil}))

(defn set-target-action
  ([t v]
   (fn [pointers _]
     (->> pointers
          (map (fn [pointer]
                 (-> pointer
                     (assoc :t t)
                     (assoc :v v)
                     (assoc :mode trace)))))))
  ([t1 v1 t2 v2]
   (fn [pointers _]
     (->> pointers
          (map-indexed (fn [n pointer]
                         (-> pointer
                             (assoc :t (if (odd? n) t1 t2))
                             (assoc :v (if (odd? n) v1 v2))
                             (assoc :mode trace))))))))

(defn set-run-action
  ([v]
   (fn [pointers _]
     (->> pointers
          (map (fn [pointer]
                 (-> pointer
                     (assoc :mode run)
                     (assoc :v v)))))))
  ([v1 v2]
   (fn [pointers _]
     (->> pointers
          (map-indexed (fn [n pointer]
                         (-> pointer
                             (assoc :mode run)
                             (assoc :v (if (odd? n) v1 v2)))))))))

(defn set-run-col-action [col v]
  (fn [pointers _]
    (->> pointers
         (map-indexed (fn [n pointer]
                        (if (-> n (mod 16) (/ 2) Math/floor (= col))
                          (-> pointer
                              (assoc :mode run)
                              (assoc :v v))
                          pointer))))))

(defn set-stop-action []
  (fn [pointers _]
    (->> pointers
         (map (fn [pointer]
                (assoc pointer :mode stay))))))

(defn set-show-time [v]
  (fn [pointers now]
    (->> pointers
         (map-indexed (fn [n pointer]
                        (-> pointer
                            (assoc :mode trace)
                            (assoc :t (n/get-number-a now n))
                            (assoc :v v)))))))

(defn set-show-index []
  (fn [pointers _]
    (->> pointers
         (map-indexed (fn [n pointer]
                        (-> pointer
                            (assoc :mode trace)
                            (assoc :t (* n (/ n/PIx2 48)))
                            (assoc :v 0.05)))))))

(def actions {0 (set-target-action n/PIp2 -0.04 (- n/PIp2) -0.04)
              40 (set-run-col-action 7 0.04)
              41 (set-run-col-action 6 0.04)
              42 (set-run-col-action 5 0.04)
              43 (set-run-col-action 4 0.04)
              44 (set-run-col-action 3 0.04)
              45 (set-run-col-action 2 0.04)
              46 (set-run-col-action 1 0.04)
              47 (set-run-col-action 0 0.04)
              90 (set-target-action n/PIp2 0.04 (- n/PIp2) 0.04)
              110 (set-run-col-action 0 -0.04)
              111 (set-run-col-action 1 -0.04)
              112 (set-run-col-action 2 -0.04)
              113 (set-run-col-action 3 -0.04)
              114 (set-run-col-action 4 -0.04)
              115 (set-run-col-action 5 -0.04)
              116 (set-run-col-action 6 -0.04)
              117 (set-run-col-action 7 -0.04)
              130 (set-show-time -0.04)

              300 (set-target-action n/PIp2 -0.04 (- n/PIp2) -0.04)
              340 (set-run-col-action 7 0.04)
              341 (set-run-col-action 6 0.04)
              342 (set-run-col-action 5 0.04)
              343 (set-run-col-action 4 0.04)
              344 (set-run-col-action 3 0.04)
              345 (set-run-col-action 2 0.04)
              346 (set-run-col-action 1 0.04)
              347 (set-run-col-action 0 0.04)
              390 (set-target-action n/PIp2 0.04 (- n/PIp2) 0.04)
              410 (set-run-col-action 0 -0.04)
              411 (set-run-col-action 1 -0.04)
              412 (set-run-col-action 2 -0.04)
              413 (set-run-col-action 3 -0.04)
              414 (set-run-col-action 4 -0.04)
              415 (set-run-col-action 5 -0.04)
              416 (set-run-col-action 6 -0.04)
              417 (set-run-col-action 7 -0.04)
              430 (set-show-time -0.04)})

(defn default-action [pointers _]
  pointers)

(defn apply-pointers-action [pointers [_ _ _ _ s1 s2 ds :as now]]
  (let [sec (+ (* s1 100)
               (* s2 10)
               ds)
        action (actions sec default-action)]
    (action pointers now)))

(defn update-action [{:keys [now] :as state} new-now]
  (if (= now new-now)
    state
    (-> state
        (assoc :now new-now)
        (update :pointers apply-pointers-action new-now))))

(defn update-pointers [state]
  (update state :pointers (fn [pointers]
                            (->> pointers
                                 (map (fn [{:keys [mode] :as pointer}]
                                        (mode pointer)))
                                 (into [])))))

(defn update-state [state now]
  (-> state
      (update-action now)
      (update-pointers)))

(defn pointers [force?]
  (let [prev (-> state
                 deref
                 :pointers)
        next (-> state
                 (swap! update-state (get-time))
                 :pointers)]
    (when (or force? (not= prev next))
      next)))
