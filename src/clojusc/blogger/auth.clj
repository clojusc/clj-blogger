(ns clojusc.blogger.auth
  (:require
    [buddy.sign.jws :as jws]
    [cheshire.core :as json]
    [clj-http.client :as httpc]
    [clojure.string :as string]
    [clojusc.blogger.util :as util]))

(def token-url "https://www.googleapis.com/oauth2/v4/token")

(defn get-creds
  [client]
  (util/read-json (:creds-file client)))

(def google-jwt-token-format-data
  {:alg "RS256"
   :typ "JWT"})

(defn get-alg
  []
  (-> google-jwt-token-format-data
      :alg
      string/lower-case
      keyword))

(defn jwt-header
  []
  (-> google-jwt-token-format-data
      json/generate-string
      util/base64-encode))

(defn google-jwt-claim-data
  [creds]
  (let [now (util/get-epoch-time)
        expire (+ now (* 59 60))]
    {:iss (:client_email creds)
     :scope "https://www.googleapis.com/auth/blogger"
     :aud "https://www.googleapis.com/oauth2/v4/token"
     :exp expire
     :iat now}))

(defn jwt-claim-set
  [creds]
  (-> creds
      google-jwt-claim-data
      json/generate-string
      util/base64-encode))

(defn jwt-signature
  [creds header claim-set]
  (let [sign #'jws/calculate-signature]
    (sign {:key (util/ascii->priv-key (:private_key creds))
           :alg (get-alg)
           :header header
           :payload claim-set})))

(defn create-jwt
  "Service accounts require a JSON Web Token.

  See https://developers.google.com/identity/protocols/OAuth2ServiceAccount."
  [creds]
  (let [header (jwt-header)
        claim-set (jwt-claim-set creds)
        signature (jwt-signature creds header claim-set)]
    (string/join "." [header claim-set signature])))

(defn get-token
  [client]
  (let [creds (get-creds client)
        grant-type "urn:ietf:params:oauth:grant-type:jwt-bearer"
        jwt (create-jwt creds)]
    (->> {:form-params
          {:grant_type grant-type
           :assertion jwt}
          ; :throw-entire-message? true
          ; :debug true
          ; :debug-body true
          :as :json}
         (httpc/post token-url)
         :body
         :access_token)))

(defn update-token
  [client]
  (assoc client :token (get-token client)))
