(ns clojusc.blogger.api)

;; Tentative API
;
; get-blog [args] (blog-id or url)
; get-blogs [args] (user-id)

; get-comments [args] (blog-id post-id)
; get-comments [args] (blog-id) - for all posts
; get-comment [args] (blog-id post-id comment-id)
; approve-comment [args] (blog-id post-id comment-id)
; delete-comment [args] (blog-id post-id comment-id)
; spam-comment [args](blog-id post-id comment-id)
; remove-comment-content [args] (blog-id post-id comment-id)

; get-pages [args] (blog-id)
; get-page [args] (blog-id page-id)
; delete-page [args] (blog-id page-id)
; add-page [args] (blog-id)
; update-page [args] (blog-id page-id)

; get-posts [args] (blog-id)
; get-post [args] (blog-id post-id)
; get-post [args] (blog-id path)
; search [args] (blog-id ...)
; add-post [args] (blog-id)
; delete-post [args] (blog-id post-id)
; update-post [args] (blog-id post-id)
; publish-post [args] (blog-id post-id)
; unpublish-post [args] (blog-id post-id)

; get-user [args] (user-id)

; get-user-blog [args] (user-id blog-id)

; get-pageviews [args] (blog-id)

; get-user-post [args] (user-id blog-id post-id)
; get-user-posts [args] (user-id blog-id)
