-- 需求：查询数控激光切割机，加工了多少数量，可以根据报工得出这个台设备 切割了多少bom 以及数量？

-- 下面是一个常见的实现思路，结合本仓库的表结构（如t_work_report、t_work_order、mes_item_stock、t_device_info 等）编写 MySQL 查询，用于统计指定激光设备在“激光下料”工序报工后切割的 BOM 数量及对应数量：
-- 
-- 表关联关系
-- 
-- t_work_report：记录报工信息，包含 work_order_id（对应工单）和 real_count（报工数量），status 字段值为 03 表示“验收通过”。
-- 
-- t_work_order：记录工单信息，其中 procedure_name 表示工序名称、device_id 表示生产设备、item_no 表示物料编码。
-- 
-- mes_item_stock：通过 item_no 可查询到对应的 bom_no。
-- 
-- t_device_info：通过 device_id 查询设备名称或其他信息。
-- 
-- 查询思路
-- 
-- 只统计报工状态为“验收通过”的记录：twr.status = '03'。
-- 
-- 对于设备或工序筛选：
-- 
-- 设备 ID 在给出的三台激光切割机 ID 中：two.device_id IN (424950740994121728, 424949962023788480, 424949962023788544)。
-- 
-- 或者工序名称包含“激光下料”“激光切割”等关键词：two.procedure_name LIKE '%激光%'（根据实际工序名称调整）。
-- 
-- 关联 mes_item_stock 获取 BOM 编号，必要时可通过 t_device_info 查询设备名称。
-- 
-- 按设备和 BOM 分组，统计每个 BOM 的完成数量；如果仅想看每台设备总共切割了多少不同 BOM 和总数量，可去掉 bom_no 分组。
--  

SELECT
    di.device_name     AS 设备名称,
    mis.bom_no         AS BOM编号,
    COUNT(*)           AS 报工次数,
    SUM(twr.real_count) AS 完成数量
FROM t_work_report twr
JOIN t_work_order  two ON twr.work_order_id = two.id
JOIN t_device_info di  ON di.id = two.device_id
LEFT JOIN mes_item_stock mis ON mis.item_no = two.item_no
WHERE twr.status = '03'
  AND (
        two.device_id IN (424950740994121728, 424949962023788480, 424949962023788544)
        OR two.procedure_name LIKE '%激光%'
      )
GROUP BY di.device_name, mis.bom_no
ORDER BY di.device_name, mis.bom_no;


-- 该语句会列出三台设备对应的每个 BOM 编号及其切割数量（取自 real_count 合计）。
-- 
-- 如果希望得到“每台设备切割了多少种 BOM、总数量”，可将 GROUP BY 改为仅按 di.device_name 分组，并使用 COUNT(DISTINCT mis.bom_no) 统计 BOM 种类。
-- 
-- 通过上述方式即可根据报工数据统计激光切割机的实际加工情况。



-- ================================================================================================================== 


-- 需求：报工数量 是怎么得到的  什么思路？
 
-- “报工数量”通常保存在 t_work_report 表中，相应字段多为 real_count（或类似名称）。当工人完成某个工序后，通过系统报工时会输入本次实际完成的数量，该值即被存入该表。统计时的基本思路如下：
-- 
-- 定位报工数据
-- 查询 t_work_report 表，筛选 status='03'（验收通过）的记录，只统计已审核的报工。
-- 
-- 关联工单和设备
-- 通过 t_work_report.work_order_id 与 t_work_order.id 关联，根据工序名称（例如“激光下料”）或设备 ID 过滤需要的报工数据。
-- 
-- 求和报工数量
-- 对于符合条件的记录，累加 real_count 字段即可得到某台设备或某个 BOM 的总报工数量：


SELECT SUM(twr.real_count) AS 总报工数量
FROM t_work_report twr
JOIN t_work_order two ON twr.work_order_id = two.id
WHERE twr.status = '03'
  AND two.device_id IN (...)
  -- 或 other 条件
因此，“报工数量”其实就是每次工人报工时手动或系统记录的生产完成数（real_count），统计时只需按需要的条件汇总这些数值即可。
















