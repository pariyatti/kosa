(ns kosa.layouts.shared.header
  (:require [hiccup2.core :as h]
            [buddy.auth :as auth]))

(def logout-js
  "This is a rather weird hack to drop Basic HTTP Auth credentials
   from the client. Adapted from the bookmarklet suggested here:
   https://stackoverflow.com/questions/233507/how-to-log-out-user-from-web-site-using-basic-authentication"
  "javascript:(function (c) {
  var a, b = 'You will be logged out now.';
  try {
    a = document.execCommand('ClearAuthenticationCache')
  } catch (d) {
  }
  a || ((a = window.XMLHttpRequest ? new window.XMLHttpRequest : window.ActiveXObject ? new ActiveXObject('Microsoft.XMLHTTP') : void 0) ? (a.open('HEAD', c || location.href, !0, 'logout', (new Date).getTime().toString()), a.send(''), a = 1) : a = void 0);
  a || (b = 'Your browser is too old or too weird to support log out functionality. Close all windows and restart the browser.');
  if (alert(b)) {
    window.open('/login','_self');
  } else {
    window.open('/login','_self');
  }
})(/*pass safeLocation here if you need*/);")

(defn session-btn [req]
  (if (auth/authenticated? req)
    [:li [:a {:href "#"
              :onclick logout-js} "Logout"]]
    [:li [:a {:href "/login"} "Login"]]))

(defn render [req
              title home-url
              other-title other-home-url]
  (h/html
   [:header {:class "header"}
    [:div {:class "header-logo"}
     [:a {:href home-url}
      [:img {:src "/images/pariyatti-logo-256-solid.png"
             :width "40"
             :height "40"}]]
     [:a {:href home-url}
      [:span title]]]

    [:div {:class "header-user"}
     [:p {:class "user-email"}
      (when other-title
        [:a {:href other-home-url} (str "Switch to " other-title)])
      [:span "&nbsp; | &nbsp;"]
      [:div.menu-wrapper
       [:input {:id "menu-check" :class "menu"
                :type "checkbox" :name "menu"}]
       [:label {:class "menu btn btn-secondary" :for "menu-check"}
        "Account"]
       [:ul {:class "submenu"}
        #_[:li [:a {:href "#"} "TODO: Settings"]]
        (session-btn req)]]]]]))
