(ns clojusc.blogger.request
  (:require
    [clojure.string :as string]))

(defn get-url
  [client resource-path]
  (str (:endpoint client) resource-path))

(defn add-as-json
  [http-opts]
  (assoc http-opts :as :json))

(defn add-token
  [client http-opts]
  (assoc-in http-opts [:query-params :access_token] (:token client)))

(defn add-query-items
  [args use-keys http-opts]
  (assoc http-opts :query-params (merge (:query-params http-opts)
                                        (select-keys args use-keys))))

(defn add-debugging
  [http-opts]
  (assoc http-opts :throw-entire-message? true
                   :debug true
                   :debug-body true))
