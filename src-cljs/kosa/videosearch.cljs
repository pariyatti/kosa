(ns kosa.videosearch
  (:require [json-html.core :refer [edn->hiccup]]
            [reagent.core :as r]
            [reagent-forms.core :refer [bind-fields init-field value-of]]
            [lambdaisland.fetch :as fetch]
            [kuti.mediabox]))

(defn row [input]
  [:div.row
   [:div.col-md-5 input]])

(def results (r/atom []))

(def selected-video (r/atom {}))

(defn reset-results [result]
  (.log js/console (:body result))
  (let [edn (js->clj (:body result) :keywordize-keys true)
        _ (.log js/console (str "edn = " edn))]
    (reset! results edn)))

(defn result-source [text]
  (let [_ (.log js/console (str "searching: " text))
        _ (.log js/console (str "results = " @results))
        result (->
                (fetch/get "http://localhost:3000/api/v1/search.json"
                           {:query-params {:q text}})
                (js/Promise.resolve)
                (.then #(reset-results %)))]
    (.log js/console (str "result = " result))
    @results))

(defn show-video [img]
  [:img {:src (-> img :video-attachment :url) :width 100 :height 100}])

(defn choose-video [img]
  (reset! selected-video {:xt/id (-> img :video-attachment :xt/id)
                           :url (-> img :video-attachment :url)}))


(def form-template
  [:div
   (row [:div {:field             :mediabox
               :id                :ta
               :data-source       result-source
               :selections        results
               :selected-media    selected-video
               :result-fn         show-video
               :choice-fn         choose-video
               :input-placeholder "Type an video name"
               ;; TODO: move default classes inside mediabox
               :input-class       "form-control"
               :list-class        "mediabox-list"
               :item-class        "mediabox-item"
               :highlight-class   "highlighted-item"}])
   [:br]])

(defn page []
  (let [doc (atom {:pick-one :bar})]
    (fn []
      [:div
       [:label "Video Picker"]
       ;; [:div (str "results = " @results)] ;; for debugging

       [bind-fields
        form-template
        doc]

       ;; [:hr]
       ;; [:h1 "Document State"]
       ;; [edn->hiccup @doc]
       ])))

(r/render-component [page] (.getElementById js/document "videosearch"))
