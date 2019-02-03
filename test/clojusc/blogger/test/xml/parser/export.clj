(ns clojusc.blogger.test.xml.parser.export
  (:require
    [cheshire.core :as json]
    [clojure.java.io :as io]
    [clojure.test :refer :all]
    [clojusc.blogger.xml.parser.export :as export]
    [clojusc.twig :as logger])
  (:import
    (clojure.data.xml.node Element)
    (clojure.lang PersistentHashMap)))

(logger/set-level! '[clojusc.blogger] :fatal)

(def sample-export "samples/export.xml")
(def draft-post-id "tag:blogger.com,1999:blog-8825992.post-2878203379063759109")
(def published-post-id "tag:blogger.com,1999:blog-8825992.post-2491940924950122045")
(def non-post-id "tag:blogger.com,1999:blog-8825992.layout")
(def non-existant-id "tag:blogger.com,1999:blog-8825992.post-1234567898765432123")
(def blog-zip (export/xml-resource->zip sample-export))

(deftest post-id?
  (is (= [true true false true]
         (mapv (comp not nil? export/post-id?)
               [draft-post-id published-post-id non-post-id non-existant-id]))))

(deftest term-comment?
  (is (export/term-comment? "http://schemas.google.com/blogger/2008/kind#comment"))
  (is (not
        (export/term-comment? "http://schemas.google.com/blogger/2008/kind#template"))))

(deftest atom-ns
  (is (= :xmlns.http%3A%2F%2Fwww.w3.org%2F2005%2FAtom/thing
         (export/ns-tag :thing)))
  (is (= :xmlns.http%3A%2F%2Fpurl.org%2Fatom%2Fapp%23/thing
         (export/ns-tag export/atom-app-ns :thing))))

(deftest extract-xml-text
  (let [blog-post (export/get-entry blog-zip published-post-id)]
    (is (= published-post-id
           (export/extract-xml-text blog-post :id)))
    (is (= "Ubuntu 7.04 on iMac Core 2 Duo, 2G"
           (export/extract-xml-text blog-post :title)))
    (is (= 1344
           (count (export/extract-xml-text blog-post :content)))))
  (let [blog-post (export/get-entry blog-zip draft-post-id)]
    (is (= draft-post-id
           (export/extract-xml-text blog-post :id)))
    (is (= "Hello, Clojure: LFE nuzzling up to the JVM"
           (export/extract-xml-text blog-post :title)))
    (is (= 0
           (count (export/extract-xml-text blog-post :content))))))

(deftest extract-author
  (let [blog-post (export/get-entry blog-zip published-post-id)]
    (is (= "Duncan McGreggor"
           (:name (export/extract-author blog-post)))))
  (let [blog-post (export/get-entry blog-zip draft-post-id)]
    (is (= "Duncan McGreggor"
           (:name (export/extract-author blog-post))))))

(deftest extract-tags
  (let [blog-post (export/get-entry blog-zip published-post-id)]
    (is (= ["ubuntu" "apple" "hardware" "mac os x" "canonical"]
           (export/extract-tags blog-post))))
  (let [blog-post (export/get-entry blog-zip draft-post-id)]
    (is (= ["erjang" "erlang" "lfe" "clojure" "jvm" "lisp" "java"]
           (export/extract-tags blog-post)))))

(deftest extract-draft-bool
  (let [blog-post (export/get-entry blog-zip published-post-id)]
    (is (= false
           (export/extract-draft-bool blog-post))))
  (let [blog-post (export/get-entry blog-zip draft-post-id)]
    (is (= true
           (export/extract-draft-bool blog-post)))))

(deftest extract-url
  (let [blog-post (export/get-entry blog-zip published-post-id)]
    (is (= "https://oubiwann.blogspot.com/2007/08/ubuntu-704-on-imac-core-2-duo-2g.html"
           (export/extract-url
            blog-post "Ubuntu 7.04 on iMac Core 2 Duo, 2G"))))
  (let [blog-post (export/get-entry blog-zip draft-post-id)]
    (is (= nil
           (export/extract-url
            blog-post "Hello, Clojure: LFE nuzzling up to the JVM")))))

(deftest get-post-ids
  (is (= [draft-post-id published-post-id]
         (doall (export/get-post-ids blog-zip)))))

(deftest get-entry
  (is (= [Element]
         (mapv type (export/get-entry blog-zip published-post-id))))
  (is (= []
         (mapv type (export/get-entry blog-zip non-existant-id)))))

(deftest get-posts
  (is (= 2 (count (export/get-posts blog-zip)))))

(deftest extract-post
  (let [post (export/extract-post
               (export/get-entry blog-zip published-post-id))]
    (is (= [:author :content :draft? :id :published :tags :title :updated :url]
           (sort (keys post))))
    (is (= "Ubuntu 7.04 on iMac Core 2 Duo, 2G" (:title post)))
    (is (not (:draft? post)))
    (is (= "https://oubiwann.blogspot.com/2007/08/ubuntu-704-on-imac-core-2-duo-2g.html"
           (:url post))))
  (let [post (export/extract-post
               (export/get-entry blog-zip draft-post-id))]
    (is (= [:author :content :draft? :id :published :tags :title :updated :url]
           (sort (keys post))))
    (is (= "Hello, Clojure: LFE nuzzling up to the JVM" (:title post)))
    (is (:draft? post))
    (is (not (:url post)))))

(deftest extract-post-non-post-id
  (let [post (export/extract-post
               (export/get-entry blog-zip non-post-id))]
    (is (= [:author :content :draft? :id :published :tags :title :updated :url]
           (sort (keys post))))
    (is (= "Template: Electric Duncan" (:title post)))
    (is (not (:draft? post)))
    (is (not (:url post)))))

(deftest extract-non-existant-post
  (let [post (export/extract-post
               (export/get-entry blog-zip non-existant-id))]
    (is (= [] (sort (keys post))))))

(deftest extract-posts
  (let [posts (export/extract-posts blog-zip)]
    (is (= 2 (count posts)))
    (is (= [PersistentHashMap PersistentHashMap]
           (mapv type posts)))
    (is (= ["Hello, Clojure: LFE nuzzling up to the JVM"
            "Ubuntu 7.04 on iMac Core 2 Duo, 2G"]
           (mapv :title posts)))
    (is (= [true false]
           (mapv :draft? posts)))
    (is (= [nil
            "https://oubiwann.blogspot.com/2007/08/ubuntu-704-on-imac-core-2-duo-2g.html"]
           (mapv :url posts)))))
