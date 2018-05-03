(ns pikogram.views
  (:require [re-frame.core :as re-frame]
            [pikogram.subs :as subs]
            [clojure.string :refer [replace capitalize]]))

(re-frame/reg-sub
  ::getFiltersValue
  (fn [db _]
    (let [grayscale (get-in db [:grayscale])
          sepia (get-in db [:sepia])
          brightness (get-in db [:brightness])
          contrast (get-in db [:contrast])
          invert (get-in db [:invert])
          saturate (get-in db [:saturate])
          opacity (get-in db [:opacity])]
      (str "grayscale(" grayscale "%) "
           "sepia(" sepia "%) "
           "brightness(" brightness "%) "
           "contrast(" contrast "%) "
           "invert(" invert "%) "
           "saturate(" saturate "%) "
           "opacity(" opacity "%)"))))


(re-frame/reg-event-db
  ::setFiltersValue
  (fn [db [_ payload]]
    (let [path (get-in payload [:path])
          value (get-in payload [:value])]
      (assoc-in db path value))))

(re-frame/reg-sub
  ::getFilterValue
  (fn [db [_ path]]
    (get-in db path)))

(defn- input [path]
  (let [id (replace (str (first path)) ":" "")
        value @(re-frame/subscribe [::getFilterValue path])]
    [:section {:class "polzynok"}
     [:label {:for id
              :class "polzynok__label"}
      (capitalize id)]
     [:input {:type "range"
              :min "0"
              :id id
              :max "100"
              :step "1"
              :value value
              :defaultValue 0
              :on-change #(re-frame/dispatch [::setFiltersValue {:path path
                                                                 :value (-> % .-target .-value)}])}]]))

(defn main-panel []
  (let [filters @(re-frame/subscribe [::getFiltersValue])]
    [:section {:class "pikogram"}
     [:img {:src "pik.jpg"
            :class "picture"
            :width "400"
            :height "400"
            :style {:filter filters}}]
     (input [:sepia])
     (input [:brightness])
     (input [:invert])
     (input [:contrast])
     (input [:saturate])
     (input [:opacity])
     (input [:grayscale])
     [:div {:class "logo"}
      "pikogram"
      [:button {:class "print-button"
                :on-click #(.print js/window)}
       "print"]]]))
