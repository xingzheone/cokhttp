(ns cokhttp.core
 (:require [clojure.string :as str])
 (:import [okhttp3 Call Callback OkHttpClient Headers MediaType Request Request$Builder FormBody
                   FormBody$Builder RequestBody Response ResponseBody OkHttpClient$Builder HttpUrl]
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
 )

