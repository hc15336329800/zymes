
## 注意点

```
# 发布版本时候：重置内部bom同步时间以免数据量过大出现错误！！
	private volatile String startTime = "2025-06-30 12:00:00";  //内部BOM依赖从这个日期开始更新 ！

# 强制推送：git push origin main --force

 


## 注意点

代码中已经有递归重构父件的逻辑：
- 在 BomUsedServiceImpl 中 loadParBomData 方法会先查询某个物料作为子件时被哪些母件引用，然后对这些母件调用 loadBomData 并继续向上递归处理
- 在多个业务操作完成后（如上传用料、保存/修改用料、删除用料等），都会异步调用该方法，从而保证修改的子件会触发其所有母件重新构建 BOM。例如上传用料后：
- 因此，只要在更新或同步时调用 loadParBomData，就会自动重构受影响的所有母件 BOM。若想在定时任务 syncErpToMesAll007 中实现“一小时内变更 + 涉及的母件重构”，可以在读取到这一小时内变化的子件后，对每个子件调用该方法即可。


关于唯一约束：

- use_item_no的item_no、use_item_no 不可以重复！
- t_bom_used的item_no、use_item_no 可以重复


- 新建的数据表要注意！！  
   实体类继承了 BaseEntity，而 BaseEntity 中定义了 createdBy、updatedBy、updatedTime 等字段，会自动映射到数据库字段 created_by、updated_by、updated_time。
   如果你提供的表  中只有 created_time 和 update_time，并没有 created_by、updated_by、updated_time 这些列。
   MyBatis-Plus 在执行 mapper.selectList(queryWrapper) 时会根据实体类生成 SQL，结果会包含这些不存在的字段，导致数据库报 “Unknown column …” 的错误。因此字段与数据库表结构不一致是造成报错的主要原因。


更新时间说明：

2025-07-08: 
- 目前更新是按MesItemStock中的修改更新的 ，如果是修改bom的话  ，MesItemStockMapper中的物料是没有更新的 ，只更新了依赖表

2025-07-09:  bom构建树触发的机制：
- 目前是用料表有新增才会触发！ （现在我已经增加逻辑方法UpdataItemNOUpTime：）
- UpdataItemNOUpTime： 当物料表不更新，只更新依赖表的时候，则查询分析依赖表的物料然后去更新物料表的更新时间，这样整个逻辑遵循原来的逻辑  无需大动
- 目前测试的子件修改相关母件也正常修改了,  但是效率还是太慢  use_item_no中889个依赖  更新了20多分钟. 需要进一步优化



2025-07-21 重构优化总结：
- 告一段落、不在大改、下一步想优化需要去除掉递归构件树中的判断（字母依赖-防环、是否有原料，是否有重复项）然后才能一次进一步进行递归优化

- 如果初始化，只需要重构自身+清空依赖表  （速度很快）
-如果中间表纯新增，也无需追查父件进行修改。 只有修改了才要=向上追查已实现全部更新
