(ns cokhttp.core
 (:require [clojure.string :as str])
 (:import [okhttp3 Call Callback OkHttpClient Headers MediaType Request Request$Builder FormBody
                   FormBody$Builder RequestBody Response ResponseBody OkHttpClient$Builder HttpUrl RequestBody$Companion MediaType$Companion]
          (javax.net.ssl X509TrustManager TrustManager KeyManagerFactory KeyManager SSLContext)
          (java.security SecureRandom)
          (java.security.cert X509Certificate)
          (java.util.concurrent TimeUnit)))
(defonce client (delay (-> (OkHttpClient$Builder.)
                           (.connectTimeout 40 TimeUnit/SECONDS)
                           (.writeTimeout 10 TimeUnit/SECONDS)
                           (.readTimeout 40 TimeUnit/SECONDS) .build)))

(defn url-params [url params]
 (if params
  (let [ub (.newBuilder (HttpUrl/parse url))]
   (doall (map #(.addQueryParameter ub (name (first %)) (str (second %))) params))
   (.build ub))
  (HttpUrl/parse url)
  )
 )
;kotlin 有 companion object 的概念.外部无法调用 所以下面的create是clojure调用的正确方法.不要被Deprecated所困惑
(defn post
 ([url params body media-type]
  (post (url-params url params) body media-type)
  )
 ([url body media-type]
  (let [url       (if (string? url) (url-params url nil) url)
        req       (-> (Request$Builder.) (.url ^HttpUrl url)
                      (.post (RequestBody/create (str body) (MediaType/parse media-type)))
                      .build)
        res       (-> @client (.newCall req) (.execute))
        resp-body (-> res (.body) (.string))]
   resp-body)
  )
 )
;WARNING: get already refers to: #'clojure.core/get in namespace: cokhttp.core, being replaced by: #'cokhttp.core/get
;Parameter specified as non-null is null: method okhttp3.HttpUrl$Companion.parse, parameter $this$toHttpUrlOrNull
;如果使用get则会报上面的错误..
(defn hget
 ([url params]
  (hget (url-params url params))
  )
 ([url]
  (let [url       (if (string? url) (url-params url nil) url)
        req       (-> (Request$Builder.) (.url ^HttpUrl url) .build)
        res       (-> @client (.newCall req) (.execute))
        resp-body (-> res (.body) (.string))]
   resp-body))
 )

(comment
 (get "http://baidu.com")
 (get "http://baidu.com" {:a :some})
 (post "https://api.github.com/markdown/raw"
       "|发布的版本
        |--------
        |
        | * _1.0_ May 6, 2013
        | * _1.1_ June 15, 2013
        | * _1.2_ August 11, 2013
        |" "text/x-markdown; charset=utf-8")
 (post "https://api.github.com/markdown/raw" {:a 3}
       "|发布的版本
        |--------
        |
        | * _1.0_ May 6, 2013
        | * _1.1_ June 15, 2013
        | * _1.2_ August 11, 2013
        |" "text/x-markdown; charset=utf-8")
 )

