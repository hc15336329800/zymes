



-- 执行存储过程刷新数据
CALL refresh_order_progress();

-- 查询结果
SELECT * FROM t_order_progress_summary 
ORDER BY created_time DESC, order_no;

-- 也可以添加条件查询
SELECT * FROM t_order_progress_summary
WHERE created_time BETWEEN '2023-01-01' AND '2023-12-31'
ORDER BY progress_percent DESC;