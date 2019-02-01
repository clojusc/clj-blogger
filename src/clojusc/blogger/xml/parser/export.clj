(ns clojusc.blogger.xml.parser.export
  "Functions for parsing XML export data from blogger."
  (:require
    [clojure.data.xml :as xml]
    [clojure.data.zip.xml :as zip-xml]
    [clojure.instant :as instant]
    [clojure.java.io :as io]
    [clojure.string :as string]
    [clojure.zip :as zip]
    [taoensso.timbre :as log])
  (:import
    (java.net URLEncoder)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Constants   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def atom-ns "http://www.w3.org/2005/Atom")
(def atom-app-ns "http://purl.org/atom/app#")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   General Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn ns-tag
  ([tag]
    (ns-tag atom-ns tag))
  ([xml-ns tag]
    (if (keyword? tag)
      (keyword
        (format "xmlns.%s/%s"
                (URLEncoder/encode (str xml-ns) "UTF-8")
                (URLEncoder/encode (name tag) "UTF-8")))
      tag)))

(defn xml->zip
  [input]
  (-> input
      io/input-stream
      xml/parse
      zip/xml-zip))

(defn xml-resource->zip
  [resource-file]
  (xml->zip (io/file (io/resource resource-file))))

(defn xml->
  ([data tags]
    (xml-> atom-ns data tags))
  ([xml-ns data tags]
    (xml-> #'zip-xml/xml-> xml-ns data tags))
  ([func xml-ns data tags]
    (apply func (cons data (mapv #(ns-tag xml-ns %) tags)))))

(defn xml1->
  ([data tags]
    (xml1-> atom-ns data tags))
  ([xml-ns data tags]
    (xml-> #'zip-xml/xml1-> xml-ns data tags)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Blogger Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn is-post?
  [id]
  (re-matches #"tag:blogger.com.*\.post-.*" id))

(defn entry-id?
  [id]
  (fn [loc]
    (let [current-id (xml1-> loc [:id zip-xml/text])
          equal? (= current-id id)]
      (log/debug "Comparing " current-id " with " id " ... Equal? " equal?)
      equal?)))

(defn post-url?
  [title]
  (fn [loc]
    (let [current-link-href (xml1-> loc [(zip-xml/attr :href)])
          current-link-title (xml1-> loc [(zip-xml/attr :title)])
          equal? (= current-link-title title)]
      (log/debug "Comparing " current-link-title " with " title " ... Equal? "
                 equal?)
      equal?)))

(defn content
  [predicate]
  (fn [loc]
    (predicate (zip-xml/text loc))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   XML Query Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn print-entry-ids
  [xml-zipped-data]
  (doall
    (for [id (xml-> xml-zipped-data [:entry :id zip-xml/text])]
      (println id)))
  :ok)

(defn print-post-ids
  [xml-zipped-data]
  (doall
    (for [id (xml-> xml-zipped-data [:entry :id zip-xml/text])]
      (when (is-post? id)
        (println id))))
  :ok)

(defn print-post-ids
  [xml-zipped-data]
  (doall
    (for [id (xml-> xml-zipped-data [:entry :id (content is-post?)])]
      (println id)))
  :ok)

(defn get-entry
  [xml-zipped-data id]
  (xml-> xml-zipped-data [:entry (entry-id? id) zip/node]))

(defn extract-x
  [parsed-coll xml-tags func]
  (log/debug "Extracting from xml-tags:" xml-tags)
  (xml1-> (apply zip/xml-zip parsed-coll) (conj (vec xml-tags) func)))

(defn extract-xs
  [parsed-coll xml-tags func]
  (log/debug "Extracting from xml-tags:" xml-tags)
  (xml-> (apply zip/xml-zip parsed-coll) (conj (vec xml-tags) func)))

(defn extract-xml-text
  [parsed-coll & xml-tags]
  (extract-x parsed-coll xml-tags #'zip-xml/text))

(defn extract-attrs
  [parsed-coll & xml-tags]
  (extract-xs parsed-coll (butlast xml-tags) (zip-xml/attr (last xml-tags))))

(defn extract-draft-bool
  [parsed-coll]
  (if-let [bool-str (xml1-> atom-app-ns
                              (apply zip/xml-zip parsed-coll)
                              [:control :draft zip-xml/text])]
    (= bool-str "yes")
    false))

(defn extract-author
  [parsed-coll]
  {:name (extract-xml-text parsed-coll :author :name)
   :email (extract-xml-text parsed-coll :author :email)
   :uri (extract-xml-text parsed-coll :author :uri)})

(defn- url?
  [s]
  (log/trace "Checking for url:" s)
  (or (string/starts-with? s "http://")
      (string/starts-with? s "https://")))

(defn extract-tags
  [parsed-coll]
  (-> parsed-coll
      (extract-attrs :category :term)
      (#(remove url? %))
      vec))

(defn extract-url
  [parsed-coll title]
  (xml1-> (apply zip/xml-zip parsed-coll)
          [:link (post-url? title) (zip-xml/attr :href)]))

(defn extract-entry
  [parsed-coll]
  (let [title (extract-xml-text parsed-coll :title)]
    {:id (extract-xml-text parsed-coll :id)
     :title title
     :published (instant/read-instant-date
                 (extract-xml-text parsed-coll :published))
     :updated (instant/read-instant-date
               (extract-xml-text parsed-coll :updated))
     :author (extract-author parsed-coll)
     :tags (extract-tags parsed-coll)
     :draft? (extract-draft-bool parsed-coll)
     :content (extract-xml-text parsed-coll :content)
     :url (extract-url parsed-coll title)}))

