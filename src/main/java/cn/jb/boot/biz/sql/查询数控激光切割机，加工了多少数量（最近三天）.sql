SELECT
    di.device_name      AS 设备名称,
    mis.bom_no          AS BOM编号,
    COUNT(*)            AS 报工次数,
    SUM(twr.real_count) AS 完成数量,
    MAX(twr.created_time) AS 最近报工时间
FROM t_work_report twr
JOIN t_work_order  two ON twr.work_order_id = two.id
JOIN t_device_info di  ON di.id = two.device_id
LEFT JOIN mes_item_stock mis ON mis.item_no = two.item_no
WHERE twr.status = '03'
  AND (
        two.device_id IN ( 424949962023788480, 424949962023788544)
       )
  AND twr.created_time >= DATE_SUB(NOW(), INTERVAL 3 DAY)
GROUP BY di.device_name, mis.bom_no
ORDER BY di.device_name, mis.bom_no;
