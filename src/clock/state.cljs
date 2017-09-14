(ns clock.state
  (:require [clock.numbers :as n]))

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
    [h1 h2 m1 m2 s1 s2]))

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

(def actions {0 (set-target-action 0 -0.08 0 0.08)
              2 (set-target-action n/PIp2 -0.04 (- n/PIp2) 0.04)
              5 (set-run-action 0.04)
              8 (set-show-time 0.05)
              20 (set-target-action 0 -0.08 0 0.08)
              22 (set-target-action n/PIp2 -0.04 (- n/PIp2) 0.04)
              25 (set-run-action 0.04)
              28 (set-show-time 0.05)
              40 (set-target-action 0 -0.08 0 0.08)
              42 (set-target-action n/PIp2 -0.04 (- n/PIp2) 0.04)
              45 (set-run-action 0.04)
              48 (set-show-time 0.05)})

(defn default-action [pointers _]
  pointers)

(defn apply-pointers-action [pointers now]
  (let [sec (+ (-> now (nth 4) (* 10))
               (-> now (nth 5)))
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
