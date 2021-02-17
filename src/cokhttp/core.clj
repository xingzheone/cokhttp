(ns cokhttp.core
 (:require [clojure.string :as str])
 (:import [okhttp3 Call Callback OkHttpClient Headers MediaType Request Request$Builder FormBody
                   FormBody$Builder RequestBody Response ResponseBody OkHttpClient$Builder HttpUrl RequestBody$Companion MediaType$Companion]
          ;[okhttp3.MediaType Companion]
          ;[okhttp3.RequestBody Companion]
          (javax.net.ssl X509TrustManager TrustManager KeyManagerFactory KeyManager SSLContext)
          (java.security SecureRandom)
          (java.security.cert X509Certificate)
          (java.util.concurrent TimeUnit)))
(defonce client (delay (-> (OkHttpClient$Builder.)
                           (.connectTimeout 40 TimeUnit/SECONDS)
                           (.writeTimeout 10 TimeUnit/SECONDS)
                           (.readTimeout 40 TimeUnit/SECONDS) .build)))

(defn send-request
 ([url query body method]
  (let [
        ;start        (now)
        url        (let [ub (.newBuilder (HttpUrl/parse url))]
                    (doall (map #(.addQueryParameter ub (name (first %)) (str (second %))) query))
                    (.build ub))
        req        (if (= :post method)
                    (-> (Request$Builder.) (.url ^HttpUrl url)
                        (.post (RequestBody/create (MediaType/parse "application/json; charset=utf-8") (str body)))
                        .build)
                    (-> (Request$Builder.) (.url ^HttpUrl url)
                        .build))
        res        (-> @client (.newCall req) (.execute))
        resp-body  (-> res (.body) (.string))
        status     (.code res)
        ;requesttime  (- (now) start)
        requesturl (str url)]
   (prn url resp-body status requesturl)
   ))
 )
;kotlin 有 companion object 的概念.外部无法调用 所以下面的create是clojure调用的正确方法.不要被Deprecated所困惑
(defn post [url body media-type]
 (let [req (-> (Request$Builder.) (.url ^String url)
               (.post (RequestBody/create  (str body) (MediaType/parse media-type)))
               ;(.post (RequestBody$Companion (create ^String body ^MediaType (.parse MediaType$Companion media-type))))
               .build)
       res        (-> @client (.newCall req) (.execute))
       resp-body  (-> res (.body) (.string))]
  resp-body)
 )
(defn ok-get
 ([url] (ok-get url nil))
 ([url query] (send-request url query nil :get)))
(defn ok-post
 ([url] (ok-post url nil))
 ([url body] (ok-post url nil body))
 ([url query body] (send-request url query body :post)))

(comment
 (send-request "http://qht.cloudvast.com" {} nil :get)
 (send-request "http://api.cloudvast.com" {} nil :get)
 (send-request "http://baidu.com" {} nil :get)
 (post "https://api.github.com/markdown/raw"
       "|发布的版本
        |--------
        |
        | * _1.0_ May 6, 2013
        | * _1.1_ June 15, 2013
        | * _1.2_ August 11, 2013
        |"  "text/x-markdown; charset=utf-8")
 )

