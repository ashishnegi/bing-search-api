(ns bingsearchapi.core
  (:require [clj-http.client :as client])
  (:require [clojure.data.json :as json])
  (:require (clojure.java.io))
)

(defn load-props
  [file-name]
  (with-open [^java.io.Reader reader (clojure.java.io/reader file-name)] 
    (let [props (java.util.Properties.)]
      (.load props reader)
      (into {} (for [[k v] props] [(keyword k) (read-string v)])))))

(def properties (load-props "src/bingsearchapi/dev.conf"))

(def mainUrl  "https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/")

(def queryStart   "?Query=%27")
(def queryEnd     "%27")
(def formatOfResponse "json")
(def topWords         "&$top=2")



(def base64EncodedKey (:base64Key properties) )

(defn makeQuery [searchWord type]
  (clojure.string/join "" [mainUrl type queryStart searchWord queryEnd]))

(defn addTop [query]
  (clojure.string/join "" [query topWords]))

(defn addFormat [query]
  (clojure.string/join "" [query "&$format=" formatOfResponse]))
  
(defn getResult [searchWord type]
  (-> searchWord 
       (makeQuery type)
       addTop
       addFormat
       
       (client/get {:headers 
                    {"Authorization" 
                     (clojure.string/join "" [ "Basic " 
                                        base64EncodedKey ])}})))


(def result (getResult "ashish negi" "Image"))

(def imageResult (json/read-str (:body result)))

;// print the first image url 
(-> imageResult (get "d") 
			(get "results")
			(get 0)
			(get "MediaUrl"))

(def webResult 
  (json/read-str 
   (:body (getResult "ashish negi" "Web"))))


(-> webResult 
    (get "d")
    (get "results")
    (get 0)
    (get "Url"))
   
