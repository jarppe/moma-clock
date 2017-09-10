(defproject moma-clock "0.1.0-SNAPSHOT"
  :description "Art project inspired by https://store.moma.org/home/clocks/clock-clock-24/119937-119937.html"
  :dependencies [[org.clojure/clojure "1.9.0-alpha20"]
                 [org.clojure/clojurescript "1.9.908"]]

  :plugins [[lein-figwheel "0.5.13"]
            [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src"]
  :figwheel {:repl false}

  :profiles {:dev {:resource-paths ["target/dev/resources"]
                   :dependencies [[binaryage/devtools "0.9.4"]
                                  [figwheel-sidecar "0.5.13"]
                                  [com.cemerick/piggieback "0.2.2"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}
             :prod {:resource-paths ["target/prod/resources"]}}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src"]
                        :figwheel {:websocket-host :js-client-host}
                        :compiler {:main clock.main
                                   :asset-path "/js/out"
                                   :output-to "target/dev/resources/public/js/main.js"
                                   :output-dir "target/dev/resources/public/js/out"
                                   :source-map true
                                   :source-map-timestamp true
                                   :closure-defines {goog.DEBUG true}
                                   :external-config {:devtools/config {:features-to-install [:formatters :hints]
                                                                       :fn-symbol "F"
                                                                       :print-config-overrides true}}
                                   :preloads [devtools.preload]}}
                       {:id "prod"
                        :source-paths ["src"]
                        :compiler {:main clock.main
                                   :optimizations :advanced
                                   :pretty-print false
                                   :output-to "target/prod/resources/public/js/main.js"
                                   :output-dir "target/prod/resources/public/js/out"
                                   :closure-defines {goog.DEBUG false}}}]}

  :auto-clean false)
