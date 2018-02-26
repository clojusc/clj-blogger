(ns clojusc.blogger.request
  (:require
    [clj-http.client :as httpc]
    [clojure.string :as string])
  (:refer-clojure :exclude [get]))

(def delete (comp :body #'httpc/delete))
(def get (comp :body #'httpc/get))
(def post (comp :body #'httpc/post))
(def put (comp :body #'httpc/put))

(defn add-as-json
  [http-opts]
  (assoc http-opts :as :json))

(defn add-token
  [client http-opts]
  (assoc-in http-opts [:query-params :access_token] (:token client)))

(defn add-query-items
  [args use-keys http-opts]
  (update-in http-opts [:query-params] merge (select-keys args use-keys)))

(defn add-debugging
  [http-opts]
  (assoc http-opts :throw-entire-message? true
                   :debug true
                   :debug-body true))

(defn add-default-opts
  [client http-opts]
  (->> http-opts
       (add-token client)
       add-as-json))
