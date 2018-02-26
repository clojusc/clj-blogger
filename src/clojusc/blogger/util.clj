(ns clojusc.blogger.util
  (:require
    [cheshire.core :as json]
    [clojure.java.io :as io]
    [clojure.string :as string])
  (:import
    (java.security KeyFactory)
    (java.security.spec PKCS8EncodedKeySpec)
    (java.util Base64)))

(defn read-json
  [filename]
  (if filename
    (json/parse-stream (io/reader filename) true)))

(defn get-args
  [client args]
  (merge
    (:config client)
    args))

(defn base64-encode
  [string-data]
  (.encodeToString (Base64/getEncoder)
                   (.getBytes string-data)))

(defn base64-decode
  [base64-string]
  (.decode (Base64/getDecoder) base64-string))

(defn get-epoch-time
  []
  (quot (System/currentTimeMillis) 1000))

(defn str->bytes
  [string-data]
  (->> string-data
       (map (comp byte int))))

(defn str->byte-array
  [string-data]
  (->> string-data
       str->bytes
       byte-array
       bytes))

(defn key-without-comments
  [ascii-key]
  (->> (string/split ascii-key #"\n")
       rest
       butlast
       (string/join "")))

(defn ascii-key->bytes-key
  [ascii-key]
  (->> ascii-key
       key-without-comments
       base64-decode
       byte-array
       bytes))

(defn ascii->priv-key
  [ascii-key]
  (let [key-bytes (ascii-key->bytes-key ascii-key)
        key-spec (new PKCS8EncodedKeySpec key-bytes)
        key-factory (KeyFactory/getInstance "RSA")]
    (.generatePrivate key-factory key-spec)))

(defn get-args
  [client args]
  (merge (:defaults client) args))
