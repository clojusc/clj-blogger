(ns clojusc.blogger.api.impl.blog
  (:require
    [clojusc.blogger.request :as request]
    [clojusc.blogger.routes :as routes]
    [clojusc.blogger.util :as util]))

(defn- blog-path
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
    (let [{:keys [blog-id url] :as args} (util/get-args this args)]
      (if url
        (request/get
         (routes/get-url this :blog-by-url)
         (->> httpc-opts
              (request/add-default-opts this args)
              (request/add-query-items args [:url])))
        (request/get
         (routes/get-url this :blog-by-id [blog-id])
         (request/add-default-opts this args httpc-opts))))))

;; Looking for get-blogs? see the `impl.user` namespace, since getting
;; all blogs actually a function of the user.

(defn get-pageviews
  ([this]
    (get-pageviews this {}))
  ([this args]
    (get-pageviews this args {}))
  ([this args httpc-opts]
    (let [{blog-id :blog-id :as args} (util/get-args this args)]
      (request/get
       (routes/get-url this :blog-pageviews [blog-id])
       (request/add-default-opts this args httpc-opts)))))

(def behaviour
  {:get-blog get-blog
   :get-pageviews get-pageviews})
