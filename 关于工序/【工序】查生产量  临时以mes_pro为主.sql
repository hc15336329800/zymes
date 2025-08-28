
-- 明白✅：以后不看 last_flag，改为以 mes_procedure 的顺序号 seq_no 为准：
-- — 先把报工里的 procedure_code 映射到 mes_procedure.seq_no；
-- — 每个（订单、物料）取最大 seq_no作为“最后一道”；
-- — 统计该步的 real_count（可选只算 03，或放宽到 01/02/03）。
 

WITH base AS (
  SELECT tod.order_no AS orderNo, two.item_no AS itemNo, mis.bom_no AS bomNo, twr.created_time,
         ROW_NUMBER() OVER (PARTITION BY tod.order_no ORDER BY twr.created_time DESC) AS rn
  FROM t_work_report twr
  JOIN t_work_order two        ON two.id  = twr.work_order_id
  LEFT JOIN t_order_dtl tod    ON tod.id  = two.order_dtl_id
  LEFT JOIN mes_item_stock mis ON mis.item_no = two.item_no
  WHERE twr.status IN ('01','02','03')
),
-- ① 报工code → mes_procedure 映射出 seq
wr AS (
  SELECT tod.order_no AS orderNo, two.item_no AS itemNo,
         CAST(mp.seq_no AS UNSIGNED) AS seqNo, twr.real_count
  FROM t_work_report twr
  JOIN t_work_order two ON two.id = twr.work_order_id
  JOIN t_order_dtl  tod ON tod.id = two.order_dtl_id
  JOIN mes_procedure mp
       ON mp.item_no = two.item_no
      AND CAST(mp.procedure_code AS UNSIGNED) = CAST(two.procedure_code AS UNSIGNED)
  WHERE twr.status = '03'  -- ← 如需包含 01/02 一起统计，改成 IN ('01','02','03')
),
-- ② 每(订单,物料)取最大 seq
maxseq AS (
  SELECT orderNo, itemNo, MAX(seqNo) AS lastSeqNo
  FROM wr GROUP BY orderNo, itemNo
),
-- ③ 该“最大 seq”的报工正品数
qty AS (
  SELECT w.orderNo, w.itemNo, SUM(w.real_count) AS lastReportQty
  FROM wr w JOIN maxseq m
    ON m.orderNo = w.orderNo AND m.itemNo = w.itemNo AND w.seqNo = m.lastSeqNo
  GROUP BY w.orderNo, w.itemNo
),
-- ④ 反查该 seq 的工序码（同一 seq 如有多码，取最小码做代表）
last_code AS (
  SELECT m.orderNo, m.itemNo, m.lastSeqNo,
         MIN(CAST(mp.procedure_code AS UNSIGNED)) AS lastProcedureCode
  FROM maxseq m
  JOIN mes_procedure mp
    ON mp.item_no = m.itemNo AND CAST(mp.seq_no AS UNSIGNED) = m.lastSeqNo
  GROUP BY m.orderNo, m.itemNo, m.lastSeqNo
)
SELECT b.orderNo, b.bomNo, b.itemNo,
       lc.lastProcedureCode, lc.lastSeqNo,
       COALESCE(q.lastReportQty, 0) AS lastReportQty
FROM base b
LEFT JOIN last_code lc ON lc.orderNo = b.orderNo AND lc.itemNo = b.itemNo
LEFT JOIN qty       q  ON q.orderNo  = b.orderNo AND q.itemNo  = b.itemNo
WHERE b.rn = 1
ORDER BY b.orderNo;
