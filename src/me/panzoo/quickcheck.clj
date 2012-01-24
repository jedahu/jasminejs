(ns me.panzoo.quickcheck)

(defmacro gen* [path]
  `((.. (.generator js/qc) ~@path)))

(defmacro gen [& path]
  `(gen* ~path))

(defmacro gens [paths]
  `(vec
     (for [p# ~paths]
       (gen* p#))))

(defmacro qcheck [title gens f]
  `(.declare js/qc ~title (.array ~gens) ~f))

(defmacro note-arg [tcase a]
  `(.noteArg ~tcase ~a))

(defmacro invariant [tcase bool]
  `(.assert ~tcase ~bool))
