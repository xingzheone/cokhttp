(defproject cok "0.1.0-SNAPSHOT"
 :description "clojure wrap okhttp"
 :url "https://github.com/xingzheone/cokhttp.git"
 :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
           :url  "https://www.eclipse.org/legal/epl-2.0/"}
 :dependencies [[org.clojure/clojure "1.10.2"]
                [com.squareup.okhttp3/okhttp "4.9.0"]]
 :profiles {:dev {:aot :all}
            :uberjar {:aot :all}}
 :aliases {"ck" ["clj-kondo" "--lint" "src"]}; lein ck  too slow
 :repl-options {:init-ns cokhttp.core})
