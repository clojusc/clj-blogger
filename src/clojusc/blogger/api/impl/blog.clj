(ns clojusc.blogger.api.impl.blog
  (:require
    [clj-http.client :as httpc]
    [clojusc.blogger.auth :as auth]
    [clojusc.blogger.request :as request]
    [clojusc.blogger.routes :as routes]
    [clojusc.blogger.util :as util]))

(defn- get-blog-by-path
  [args]
  (if (:url args)
    (:blog-by-url routes/resource-paths)
    (format (:blog-by-id routes/resource-paths) (:blog-id args))))

(defn get-blog
  ([this]
    (get-blog this {}))
  ([this args]
    (get-blog this args {}))
  ([this args httpc-opts]
    (let [args (util/get-args this args)]
      (:body
       (httpc/get
        (request/get-url this (get-blog-by-path args))
        (->> httpc-opts
             (request/add-token this)
             (request/add-as-json)))))))

(defn get-blogs
  ([this]
    (get-blogs this {}))
  ([this args]
    (get-blogs this args {}))
  ([this args httpc-opts]
    ))

(def behaviour
  {:get-blog get-blog
   :get-blogs get-blogs})
