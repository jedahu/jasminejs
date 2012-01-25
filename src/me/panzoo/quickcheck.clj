(ns me.panzoo.quickcheck)

(defmacro gen* [path]
  `(~(reduce
       (fn [form segment]
         `(aget ~form ~segment))
       'js/qc
       (map name path))))

(defmacro gen [& path]
  `(gen* ~path))

(defmacro gens [paths]
  `(vec
     (for [p# ~paths]
       (gen* p#))))

(defmacro qcheck [title gens f]
  `(.call (aget (aget js/window "qc") "declare") nil ~title (. ~gens -array) ~f))

(defmacro note-arg [tcase a]
  `(.call (aget ~tcase "noteArg") ~tcase ~a))

(defmacro invariant [tcase bool]
  `(.call (aget ~tcase "assert") ~tcase ~bool))
