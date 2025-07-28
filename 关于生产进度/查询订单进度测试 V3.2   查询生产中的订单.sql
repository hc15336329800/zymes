


-- 查询订单
 
WITH cte AS (
  SELECT
    tod.order_no AS orderNo,      -- 订单号
    po.item_no   AS itemNo,       -- 顶层物料编码（母件）
    mis.bom_no   AS bomNo,        -- 母件的 BOM 编号
    ROW_NUMBER() OVER (
      PARTITION BY tod.order_no
      ORDER BY po.item_no         -- 如需按其他规则取第一条，可调整此处
    ) AS rn
  FROM t_order_dtl tod
  JOIN t_work_order two         ON two.order_dtl_id = tod.id
  JOIN t_production_order po    ON po.sales_order_no = tod.order_no
  JOIN mes_item_stock mis       ON mis.item_no = po.item_no
  WHERE
    tod.order_dtl_status IN ('03', '08', '09')
    AND EXISTS (
      SELECT 1
      FROM t_work_report twr
      WHERE twr.work_order_id = two.id
        AND twr.status = '03'
    )
)
SELECT
  orderNo,
  itemNo,
  bomNo
FROM cte
WHERE rn = 1;
