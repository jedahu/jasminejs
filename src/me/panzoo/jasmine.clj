(ns me.panzoo.jasmine)

(defmacro check [s & body]
  `((aget js/window "describe") ~s (fn [] ~@body)))

(defmacro it [s & body]
  `((aget js/window "it") ~s
      (fn [] (do ~@body))))

(defmacro before [& body]
  `((aget js/window "beforeEach") (fn [] ~@body)))

(defmacro after [& body]
  `((aget js/window "afterEach") (fn [] ~@body)))

(defmacro runs [& body]
  `((aget js/window "runs") (fn [] ~@body)))

(defmacro waits [ms]
  `((aget js/window "waits") ~ms))

(defmacro waits-for [& forms]
  (let [[s ms & tail] forms]
    `(let [f# (aget js/window "waitsFor")]
       (cond
         (and (string? ~s) (number? ~ms)) (f# (fn [] ~@(drop 2 forms)) ~s ~ms)
         (string? s) (f# (fn [] ~@(rest forms)) ~s)
         :else (f# (fn [] ~@forms))))))

(defmacro expect
  ([matcher a b]
  `(let [e# ((aget js/window "expect") ~a)]
     (.call (aget e# ~(name matcher)) e# ~b)))
  ([matcher a]
   `(expect ~matcher ~a nil)))

(defmacro expect-not
  ([matcher a b]
   `(let [e# (aget ((aget js/window "expect") ~a) "not")]
      (.call (aget e# ~(name matcher)) e# ~b)))
  ([matcher a]
   `(expect-not ~matcher ~a false)))
