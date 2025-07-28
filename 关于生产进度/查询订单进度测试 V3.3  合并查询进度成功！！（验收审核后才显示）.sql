WITH 
-- 替换后的第一部分：找出每个订单的第一条记录（包含新增字段）
cte AS (
  SELECT
    tod.order_no AS order_no,      -- 订单号
    po.item_no   AS item_no,       -- 顶层物料编码（母件）
    mis.bom_no   AS bom_no,        -- 母件的 BOM 编号
    mis.item_name AS item_name,    -- 物料名称
    so.created_time AS created_time, -- 订单创建时间
    so.cust_name AS cust_name,     -- 客户名称
    so.need_num AS need_num,       -- 需求数量
    ROW_NUMBER() OVER (
      PARTITION BY tod.order_no
      ORDER BY po.item_no         -- 如需按其他规则取第一条，可调整此处
    ) AS rn
  FROM t_order_dtl tod
  JOIN t_work_order two         ON two.order_dtl_id = tod.id
  JOIN t_production_order po    ON po.sales_order_no = tod.order_no
  JOIN mes_item_stock mis       ON mis.item_no = po.item_no
  LEFT JOIN t_sale_order so     ON so.order_no = tod.order_no
  WHERE
    tod.order_dtl_status IN ('03', '08', '09')
    AND EXISTS (
      SELECT 1
      FROM t_work_report twr
      WHERE twr.work_order_id = two.id
        AND twr.status = '03'
    )
),

-- 第二段：直接从 cte 拿 order_no + item_no + bom_no 及其他新增字段
params AS (
  SELECT 
    order_no,
    item_no,
    bom_no,
    item_name,
    created_time,
    cust_name,
    need_num
  FROM cte
  WHERE rn = 1
),

-- 累加所有子件的"总标准工时"
order_total AS (
  SELECT
    p.order_no,
    p.item_no,
    p.bom_no,
    SUM(mp.hours_work) AS orderTotalHours
  FROM params p
  JOIN t_bom_used tbu 
    ON tbu.item_no = p.item_no
  JOIN mes_procedure mp 
    ON mp.item_no = tbu.use_item_no
  GROUP BY
    p.order_no,
    p.item_no,
    p.bom_no
),

-- 累加所有子件的"已完成工时"
order_done AS (
  SELECT
    p.order_no,
    p.item_no,
    p.bom_no,
    SUM(mp2.hours_work) AS orderDoneHours
  FROM params p
  JOIN t_bom_used tbu
    ON tbu.item_no = p.item_no
  JOIN t_work_order two
    ON two.item_no = tbu.use_item_no
  JOIN t_order_dtl tod
    ON tod.id           = two.order_dtl_id
   AND tod.order_no     = p.order_no
   AND tod.order_dtl_status IN ('03','08','09')
  JOIN t_work_report twr
    ON twr.work_order_id = two.id
   AND twr.status        = '03'
  JOIN mes_procedure mp2
    ON mp2.item_no        = two.item_no
   AND mp2.procedure_code = two.procedure_code
  GROUP BY
    p.order_no,
    p.item_no,
    p.bom_no
)

-- 最终：每个订单+物料一行，含总工时、已完成工时、进度百分比及新增字段
SELECT
  ot.order_no AS 订单号,
	  ot.bom_no AS BOM编号,
  p.item_name AS 物料名称,
  p.cust_name AS 客户名称,

  p.need_num AS 需求数量,

  ot.orderTotalHours AS 总工时,
  COALESCE(od.orderDoneHours, 0) AS 已完成工时,
  ROUND(
    COALESCE(od.orderDoneHours, 0)
    / NULLIF(ot.orderTotalHours, 0)
    * 100,
    2
  ) AS 进度百分比,
	  p.created_time AS 订单创建时间
FROM order_total ot
LEFT JOIN order_done od
  ON od.order_no = ot.order_no
 AND od.item_no  = ot.item_no
 AND od.bom_no   = ot.bom_no
JOIN params p
  ON p.order_no = ot.order_no
 AND p.item_no = ot.item_no
 AND p.bom_no = ot.bom_no
ORDER BY p.created_time DESC, ot.order_no;