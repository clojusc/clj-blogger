(ns clojusc.blogger.request
  (:require
    [cheshire.core :as json]
    [clj-http.client :as httpc]
    [clojure.pprint :refer [pprint]]
    [clojure.string :as string])
  (:refer-clojure :exclude [get]))

(def ^:dynamic *print-errors* false)

(defn json->clj
  [data]
  (if (string? data)
    (json/parse-string data true)
    data))

(defn show-error
  [data]
  (if (and *print-errors* (:error data))
    (do
      (pprint data)
      :error)
    data))

(def delete (comp show-error json->clj :body #'httpc/delete))
(def get (comp show-error json->clj :body #'httpc/get))
(def patch (comp show-error json->clj :body #'httpc/patch))
(def post (comp show-error json->clj :body #'httpc/post))
(def put (comp show-error json->clj :body #'httpc/put))

(defn add-no-exceptions
  [http-opts]
  (assoc http-opts :throw-exceptions false))

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
                   :throw-exceptions true
                   :debug true
                   :debug-body true))

(defn add-common-params
  [args http-opts]
  (if (:fields args)
    (add-query-items args [:fields] http-opts)
    http-opts))

(defn add-default-opts
  [client args http-opts]
  (->> http-opts
       (add-token client)
       add-no-exceptions
       add-as-json
       (add-common-params args)))
