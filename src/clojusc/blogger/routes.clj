(ns clojusc.blogger.routes)

(def resource-paths
  ;; See https://developers.google.com/blogger/docs/3.0/reference/
  {;; Blogs Section
   :blog-by-id "/blogs/%s"
   :blog-by-url "/blogs/byurl"
   :pageviews "/blogs/%s/pageviews"
   ;; Comments Section
   :comments-all "/blogs/%s/comments"
   :comments-post "/blogs/%s/posts/%s/comments"
   :comment "/blogs/%s/posts/%s/comments/%s"
   :comment-approve "/blogs/%s/posts/%s/comments/%s/approve"
   :comment-spam "/blogs/%s/posts/%s/comments/%s/spam"
   :comment-remove-content "/blogs/%s/posts/%s/comments/%s/removecontent"
   ;; Pages Section
   :pages "/blogs/%s/pages"
   :page "/blogs/%s/pages/%s"
   ;; Posts Section
   :posts-all "/blogs/%s/posts"
   :posts-by-search "/blogs/%s/posts/search"
   :posts-by-path "/blogs/%s/posts/bypath"
   :post "/blogs/%s/posts/%s"
   :post-publish "/blogs/%s/posts/%s/publish"
   :post-revert "/blogs/%s/posts/%s/revert"
   ;; Users Section
   :user "/users/%s"
   :blog-user "/users/%s/blogs/%s"
   :blogs-by-user "/users/%s/blogs"
   :post-user "/users/%s/blogs/%s/posts/%s"
   :posts-user "/users/%s/blogs/%s/posts"})

(defn get-url
  ([client route-key]
    (str (:endpoint client) (route-key resource-paths)))
  ([client route-key args arg-keys]
   (apply format
          (concat
           [(str (:endpoint client) (route-key resource-paths))]
           (map args arg-keys)))))
