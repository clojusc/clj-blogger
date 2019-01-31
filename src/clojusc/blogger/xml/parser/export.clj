(ns clojusc.blogger.xml.parser.export
	"Functions for parsing XML export data from blogger."
	(:require
		[clojure.data.xml :as xml]
		[clojure.data.zip.xml :as zip-xml]
		[clojure.java.io :as io]
		[clojure.zip :as zip]
		[taoensso.timbre :as log])
	(:import
		(java.net URLEncoder)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Constants   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def atom-ns "http://www.w3.org/2005/Atom")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   General Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn ns-tag
	[tag]
	(if (keyword? tag)
		(keyword
			(format "xmlns.%s/%s"
				      (URLEncoder/encode (str atom-ns) "UTF-8")
				      (URLEncoder/encode (name tag) "UTF-8")))
		tag))

(defn xml->zip
	[input]
	(-> input
		  io/input-stream
		  xml/parse
		  zip/xml-zip))

(defn xml-resource->zip
	[resource-file]
	(xml->zip (io/resource resource-file)))

(defn xml->
	[data tags]
	(apply zip-xml/xml-> (cons data (mapv ns-tag tags))))

(defn xml1->
	[data tags]
	(apply zip-xml/xml1-> (cons data (mapv ns-tag tags))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Blogger Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn is-post?
	[id]
	(re-matches #"tag:blogger.com.*\.post-.*" id))

(defn entry-id?
	[id]
	(fn [loc]
		(let [current-id (xml1-> loc [:id zip-xml/text])]
			(log/debug "Comparing " current-id " with " id " ... Equal? " (= current-id id))
			(= current-id id))))

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
	(xml-> xml-zipped-data [:entry (entry-id? id) zip/children]))
