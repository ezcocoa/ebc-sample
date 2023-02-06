(ns ebc-ranking.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(def datas [{:id 1
             :name "비밀운세"
             :product_cnt 100
             :sale_cnt 91
             }
            {:id 2
             :name "런칭 파티"
             :product_cnt 10
             :sale_cnt 9
             }
            {:id 3
             :name "그랜드캐년"
             :product_cnt 15
             :sale_cnt 12
             }
            {:id 4
             :name "바드후 섬"
             :product_cnt 1
             :sale_cnt 1
             }
            {:id 5
             :name "파라여"
             :product_cnt 2
             :sale_cnt 0
             }])

(defn max-sale-cnt
  "가장 많이 판매된 제품 정보를 가져온다."
  [l]
  (:sale_cnt
   (apply max-key :sale_cnt l)))

;; 수식
;; Rank((판매 수량 / 발행 수량) * 비율 가중치) + ((판매 수량 / 가장 많이 판매된 수량) * 판매량 가중치)
(defn rank
  "가장 인기 있는 상품 정보를 가져온다."
  [l wgt]
  (let [{:keys [ratio_wgt sale_wgt]} wgt
        max_p_cnt (max-sale-cnt l)]
    (->>
     (map (fn [x]
            (let [sale_ratio (float
                              (* (/ (:sale_cnt x)
                                    (:product_cnt x))
                                 ratio_wgt
                                 ))
                  sale_per (float
                            (* (/ (:sale_cnt x)
                                  max_p_cnt)
                               sale_wgt
                               ))]
              (assoc x
                     :sale_ratio sale_ratio
                     :sale_per sale_per
                     :priority (+ sale_ratio sale_per))
              )) l)
     (map #(dissoc %
                   :sale_ratio
                   :sale_per
                   :product_cnt
                   :sale_cnt))

     (sort-by :priority #(compare %2 %1))
     )))

(comment
  (rank datas {:ratio_wgt 7
               :sale_wgt 3}))

;; => (
;; {:id 1, :name "비밀운세", :priority 9.369999885559082}
;; {:id 4, :name "바드후 섬", :priority 7.032967034727335}
;; {:id 2, :name "런칭 파티", :priority 6.596703499555588}
;; {:id 3, :name "그랜드캐년", :priority 5.995604306459427}
;; {:id 5, :name "파라여", :priority 0.0})





