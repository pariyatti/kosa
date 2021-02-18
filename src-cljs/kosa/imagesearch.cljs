(ns kosa.imagesearch
  (:require [json-html.core :refer [edn->hiccup]]
            [reagent.core :as r]
            [reagent-forms.core :refer [bind-fields init-field value-of]]
            [lambdaisland.fetch :as fetch]
            [kuti.mediabox]))

(def results (r/atom []))
(def selected-image (r/atom {}))

(defn reset-results [result]
  (let [edn (js->clj (:body result) :keywordize-keys true)]
    (reset! results edn)))

(defn result-source [text]
  (-> (fetch/get "http://localhost:3000/api/v1/search.json"
                 {:query-params {:q text}})
      (js/Promise.resolve)
      (.then #(reset-results %)))
  @results)

(defn show-image [img]
  [:img {:src (-> img :image-attachment :url) :width 100 :height 100}])

(defn choose-image [img]
  (reset! selected-image {:crux.db/id (-> img :image-attachment :crux.db/id)
                          :url (-> img :image-attachment :url)}))

(def form-template
  [:div {:field             :mediabox
         :id                :imagesearch
         :data-source       result-source
         :selections        results
         :selected-media    selected-image
         :result-fn         show-image
         :choice-fn         choose-image
         :input-placeholder "Type an image name"
         ;; TODO: move default classes inside mediabox
         :input-class       "form-control"
         :list-class        "mediabox-list"
         :item-class        "mediabox-item"
         :highlight-class   "highlighted-item"}])

(defn widget []
  (let [doc (atom {:imagesearch nil})]
    (fn []
      [:div
       [:label "Image Search"]
       [bind-fields form-template doc]
       [:br]])))

(r/render-component [widget] (.getElementById js/document "imagesearch"))
