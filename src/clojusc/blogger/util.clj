(ns clojusc.blogger.util
  (:require
    [cheshire.core :as json]
    [clojure.java.io :as io]))

(defn read-json
  [filename]
  (if filename
    (json/parse-stream (io/reader filename) true)))

(defn get-args
  [client args]
  (merge
    (:config client)
    args))
