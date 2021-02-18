(ns kuti.mediabox
  (:require-macros
    [reagent-forms.macros :refer [render-element]])
  (:require
    [clojure.string :as string :refer [trim]]
    [reagent.core :as r :refer [atom]]
    [reagent-forms.core :as rf]))

(defn- scroll-to [element idx]
  (let [list-elem (-> element
                      .-target
                      .-parentNode
                      (.getElementsByTagName "ul")
                      (.item 0))
        idx       (if (< idx 0) 0 idx)
        item-elem (-> list-elem
                      .-children
                      (.item idx))
        [item-height offset-top] (if item-elem
                                   [(.-scrollHeight item-elem)
                                    (.-offsetTop item-elem)]
                                   [0 0])]
    (set! (.-scrollTop list-elem)
          (- offset-top
             (* 2 item-height)))))

(defmethod rf/init-field :mediabox
  [[type {:keys [id data-source input-class list-class item-class highlight-class
                 input-placeholder result-fn choice-fn clear-on-focus? selections selected-media get-index]
          :as   attrs
          :or   {result-fn       identity
                 choice-fn       identity
                 clear-on-focus? true}}] {:keys [doc get save!]}]
  (let [typeahead-hidden? (atom true)
        mouse-on-list?    (atom false)
        selected-index    (atom -1)
        selections        (or selections (atom []))
        selected-media    (or selected-media (atom {}))
        get-index         (or get-index (constantly -1))
        choose-selected   #(when (and (not-empty @selections) (> @selected-index -1))
                             (let [choice (nth @selections @selected-index)]
                               (save! id choice)
                               (choice-fn choice)
                               (reset! typeahead-hidden? true)))]

    (render-element attrs doc
                    [type
                     [:input {:type        :text
                              :disabled    (:disabled attrs)
                              :placeholder input-placeholder
                              :class       input-class
                              :value       (let [v (get id)]
                                             (if-not (iterable? v)
                                               v (first v)))
                              :on-focus    #(when clear-on-focus? (save! id nil))
                              :on-blur     #(when-not @mouse-on-list?
                                              (reset! typeahead-hidden? true)
                                              (reset! selected-index -1))

                              :on-change   #(when-let [value (trim (rf/value-of %))]
                                              (reset! selections (data-source (.toLowerCase value)))
                                              (save! id (rf/value-of %))
                                              (reset! typeahead-hidden? false)
                                              (reset! selected-index (if (= 1 (count @selections)) 0 -1)))

                              :on-key-down #(do
                                              (case (.-which %)
                                                38 (do
                                                     (.preventDefault %)
                                                     (when-not (or @typeahead-hidden? (<= @selected-index 0))
                                                       (swap! selected-index dec)
                                                       (scroll-to % @selected-index)))
                                                40 (do
                                                     (.preventDefault %)
                                                     (if @typeahead-hidden?
                                                       (do

                                                         (reset! selections (data-source :all))
                                                         (reset! selected-index (get-index (-> %
                                                                                               rf/value-of
                                                                                               trim)
                                                                                           @selections))
                                                         (reset! typeahead-hidden? false)
                                                         (scroll-to % @selected-index))
                                                       (when-not (= @selected-index (dec (count @selections)))
                                                         (save! id (rf/value-of %))
                                                         (swap! selected-index inc)
                                                         (scroll-to % @selected-index))))
                                                9 (choose-selected)
                                                13 (do
                                                     (.preventDefault %)
                                                     (choose-selected))
                                                27 (do (reset! typeahead-hidden? true)
                                                       (reset! selected-index -1))
                                                "default"))}]

                     [:ul {:style          {:display (if (or (empty? @selections) @typeahead-hidden?) :none :block)}
                           :class          list-class
                           :on-mouse-enter #(reset! mouse-on-list? true)
                           :on-mouse-leave #(reset! mouse-on-list? false)}
                      (doall
                        (map-indexed
                          (fn [index result]
                            [:li {:tab-index     index
                                  :key           index
                                  :class         (if (= @selected-index index) highlight-class item-class)
                                  :on-mouse-over #(do
                                                    ;; NOTE: this used to be `.-target` but since our contents are an `img` tag, we
                                                    ;;       need to target the actual `li`. there might be a much better way. -sd
                                                    (reset! selected-index (js/parseInt (.getAttribute (.-currentTarget %) "tabIndex"))))
                                  :on-click      #(do
                                                    (.preventDefault %)
                                                    (reset! typeahead-hidden? true)
                                                    (save! id result)
                                                    (choice-fn result))}
                             (result-fn result)])
                          @selections))]

                     ;; show selected url:
                     [:br]
                     (if-let [url (:url @selected-media)]
                       [:img {:src url :width "300" :height "300"}]
                       [:div "No media chosen."])])))
