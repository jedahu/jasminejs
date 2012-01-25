(ns jasminejs.core)

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
  `(let [e# ((aget js/window "expect") ~b)]
     (.call (aget e# ~(name matcher)) e# ~a)))
  ([matcher a]
   `(expect ~matcher nil ~a)))

(defmacro expect-not
  ([matcher a b]
   `(let [e# (aget ((aget js/window "expect") ~b) "not")]
      (.call (aget e# ~(name matcher)) e# ~a)))
  ([matcher a]
   `(expect-not ~matcher false ~a)))

(defmacro add-matcher [name pred msg]
  `(before
     (let [this# (~'js* "this")]
       (.call (aget this# "addMatchers")
              this#
              (.-strobj {~name (fn [expected#]
                                 (let [this# (~'js* "this")
                                       actual# (aget this# "actual")]
                                   (aset this# "message" #(~msg expected# actual#))
                                   (~pred expected# actual#)))})))))

(defmacro check [s & body]
  `((aget js/window "describe")
      ~s
      (fn []
        (add-matcher "=" = #(str "Expected: " (pr-str %1)
                                 ". Actual: " (pr-str %2)))
        ~@body)))

(defmacro it [s & body]
  `((aget js/window "it") ~s
      (fn [] (do ~@body))))
