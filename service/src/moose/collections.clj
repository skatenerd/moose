(ns moose.collections)

(defn iterative-contains? [value items]
  (some #(= % value) items))

(defn conjv [coll item]
  (into [] (conj (vec coll) item)))
