(ns clojusc.blogger.api.impl.user)

(defn get-blogs
  ([this]
    (get-blogs this {}))
  ([this args]
    (get-blogs this args {}))
  ([this args httpc-opts]
    :not-implemented))

(def behaviour
  {:get-blogs get-blogs})
