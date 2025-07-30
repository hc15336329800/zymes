CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_refresh_laser_report_summary`(
    OUT affected_rows INT
)
BEGIN
    INSERT INTO t_laser_report_summary (
        order_no, device_id, device_name, mother_bom_no, child_bom_no, use_item_no,
        report_count, finished_qty, last_report_time
    )
    SELECT
        tod.order_no,
        two.device_id,
        di.device_name,
        COALESCE(ops.bom_no, mis_parent.bom_no),
        mis_child.bom_no,
        bu.use_item_no,
        COUNT(*),
        IFNULL(SUM(twr.real_count), 0),
        MAX(twr.created_time)
    FROM t_work_report twr
    JOIN t_work_order two ON twr.work_order_id = two.id
    JOIN t_device_info di ON di.id = two.device_id
    JOIN t_order_dtl tod ON tod.id = two.order_dtl_id
    LEFT JOIN mes_item_stock mis_parent ON mis_parent.item_no = two.item_no
    LEFT JOIN t_order_progress_summary ops ON ops.order_no = tod.order_no AND ops.item_name = mis_parent.item_name
    LEFT JOIN t_bom_used bu ON bu.item_no = two.item_no AND bu.use_item_type = '01'
    LEFT JOIN mes_item_stock mis_child ON mis_child.item_no = bu.use_item_no
    WHERE twr.STATUS = '03'
        AND (two.device_id IN (424949962023788480, 424949962023788544)
             OR two.procedure_name LIKE '%激光%')
        AND twr.created_time >= DATE_SUB(NOW(), INTERVAL 3 DAY)   -- 写死为3天
    GROUP BY
        tod.order_no,
        two.device_id,
        COALESCE(ops.bom_no, mis_parent.bom_no),
        mis_child.bom_no,
        bu.use_item_no;

    SET affected_rows = ROW_COUNT();
END
