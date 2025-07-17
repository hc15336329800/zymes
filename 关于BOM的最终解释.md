
## bom分为两张表（目前方式为展平树）






```  导入bom逻辑
# mes_item_stock：存放bom顶层根（只是用于展示 、bom列表）（物料）
# t_bom_used ： 存放bom根+bom子  （存放完整父子节点数据、bom列表中的某个bom的依赖详情）（关系）
#  1、导入bom清单：先将bom根导入mes_item_stock   2、导入关系：某个原料或者子件  依赖那个根节点  

 
```  同步bom逻辑
# 外同步：读取erp中间表数据写入 mes_item_stock物料、写入临时依赖mes_item_use、
# 内同步：查询新修改的跟，然后mes_item_stock跟+mes_item_use临时数据 、组装平展格式  一条条写入 t_bom_used 
# t_bom_used ： 存放bom根+bom子  （存放完整父子节点数据、bom列表中的某个bom的依赖详情）（关系）


 


## 部署正确
