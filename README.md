
## 注意点

```
# 发布版本时候：重置内部bom同步时间以免数据量过大出现错误！！
	private volatile String startTime = "2025-06-30 12:00:00";  //内部BOM依赖从这个日期开始更新 ！


 


## 注意点

代码中已经有递归重构父件的逻辑：
- 在 BomUsedServiceImpl 中 loadParBomData 方法会先查询某个物料作为子件时被哪些母件引用，然后对这些母件调用 loadBomData 并继续向上递归处理
- 在多个业务操作完成后（如上传用料、保存/修改用料、删除用料等），都会异步调用该方法，从而保证修改的子件会触发其所有母件重新构建 BOM。例如上传用料后：
- 因此，只要在更新或同步时调用 loadParBomData，就会自动重构受影响的所有母件 BOM。若想在定时任务 syncErpToMesAll007 中实现“一小时内变更 + 涉及的母件重构”，可以在读取到这一小时内变化的子件后，对每个子件调用该方法即可。


关于唯一约束：

- use_item_no的item_no、use_item_no 不可以重复！
- t_bom_used的item_no、use_item_no 可以重复


更新时间说明：

- 目前更新是按MesItemStock中的修改更新的 ，如果是修改bom的话  ，MesItemStockMapper中的物料是没有更新的 ，只更新了依赖表


bom构建树出发的机制：
- 目前是用料表有新增才会触发！ （现在我已经增加逻辑方法UpdataItemNOUpTime：）
- UpdataItemNOUpTime： 当物料表不更新，只更新依赖表的时候，则查询分析依赖表的物料然后去更新物料表的更新时间，这样整个逻辑遵循原来的逻辑  无需大动
