(ns build
  "A `tools.build` script for the Polylith + Integrant workspace.

   Documentation: https://clojure.org/guides/tools_build"
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.build.api :as b]
            [clojure.tools.deps :as t]
            [clojure.tools.deps.util.dir :refer [with-dir]])
  (:import (java.io File)))

(defn- concat-paths [& paths]
  (->> paths
       (remove nil?)
       (str/join "/")))

(defn- create-basis
  "A Polylith-aware version of the `b/create-basis` fn."
  ([]
   (create-basis nil))
  ([path-to-deps]
   (let [project (concat-paths path-to-deps "deps.edn")]
     (b/create-basis {:project project}))))

;;

(defn clean
  "Deletes a \"target\" dir in a given `:project` or `:brick`.

   Usage:
   ```
   clojure -T:build clean :project app
   clojure -T:build clean :brick components/utils
   ```"
  [& {:keys [project brick]}]
  (let [path (cond
               project (concat-paths "projects" project "target")
               brick   (concat-paths brick "target")
               :else   "target")]
    (b/delete {:path path})))

;;

(def no-main-ns-error-msg
  "The '%s' project's `deps.edn` file does not specify the ':main' namespace in its ':uberjar' alias")

(defn- get-project-aliases []
  (let [edn-fn (juxt :root-edn :project-edn)]
    (-> (t/find-edn-maps)
        (edn-fn)
        (t/merge-edns)
        :aliases)))

(defn- ensure-project-root
  "Ensures there is a valid project with a given `project-name`.
   Returns an absolute path to the project root."
  [project-name]
  (let [project-root (str (System/getProperty "user.dir") "/projects/" project-name)]
    (when-not (and project-name
                   (File/.exists (io/file project-root))
                   (File/.exists (io/file (concat-paths project-root "deps.edn"))))
      (throw (ex-info "Invalid Polylith project specified" {:project project-name})))
    project-root))

(defn uberjar
  "Builds an uberjar for a specified Polylith project.

   Options:
   * :project   — a name of the Polylith project to be built;
   * :uber-file — (optional) a path of the JAR file to build,
                  relative to the project folder; can also be
                  specified in an `:uberjar` alias inside the
                  project's `deps.edn` file; if not specified,
                  defaults to \"target/<project>.jar\".

   The project's `deps.edn` file has to contain the `:uberjar`
   alias, which itself has to contain at least the `:main` key
   specifying the main ns (to compile for later invocation).

   Usage:
   ```
   clojure -T:build uberjar :project app
   ```"
  [{:keys [project uber-file] :as _opts}]
  (let [project-root (ensure-project-root project)
        aliases (with-dir (io/file project-root) (get-project-aliases))
        {main :main alias-uber-file :uber-file} (:uberjar aliases)]
    (when-not main
      (throw (ex-info (format no-main-ns-error-msg project)
                      {:aliases aliases})))
    (clean :project project)
    (binding [b/*project-root* project-root]
      (let [basis (create-basis)
            class-dir "target/classes"
            uber-file (or uber-file
                          alias-uber-file
                          (str "target/" project ".jar"))]
        (b/copy-dir {:src-dirs   ["src" "resources"]
                     :target-dir class-dir})
        (b/compile-clj {:basis        basis
                        :class-dir    class-dir
                        :ns-compile   [main]
                        :compile-opts {:direct-linking true}
                        :bindings     {#'clojure.core/*assert* false}})
        (b/uber {:basis     basis
                 :class-dir class-dir
                 :uber-file uber-file
                 :main      main})
        (println (format "Uberjar '%s' is built." uber-file))))))
