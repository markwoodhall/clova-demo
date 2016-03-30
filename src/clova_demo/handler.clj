(ns clova-demo.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [clova.core :as clova]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]))

(def command-validation-set (clova/validation-set [:command-type clova/required? [clova/one-of? ["CREATE-USER" "EDIT-USER"]]
                                                   :command-time clova/required? clova/date?]))

(defroutes app-routes
  (POST "/handle" [command]
        (let [validated (clova/validate command-validation-set command)]
          (if (:valid? validated)
            {:body command
             :status 200}
            {:body
             {:message (str "Command " command " failed validation with errors: " (reduce str (:results validated)))}
             :status 400})))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (wrap-json-response)
      (wrap-json-params)))
