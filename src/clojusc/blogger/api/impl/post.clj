(ns clojusc.blogger.api.impl.post
  (:require
    [clojusc.blogger.request :as request]
    [clojusc.blogger.routes :as routes]
    [clojusc.blogger.util :as util]))

(defn get-posts
  ([this]
    (get-posts this {}))
  ([this args]
    (get-posts this args {}))
  ([this args httpc-opts]
    (let [{blog-id :blog-id :as args} (util/get-args this args)]
      (request/get
       (routes/get-url this :posts-all args [:blog-id])
       (request/add-default-opts this args httpc-opts)))))

(def behaviour
  {:get-posts get-posts})
