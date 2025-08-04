package cn.jb.boot.biz.item.service.impl;

import cn.jb.boot.biz.item.entity.BomUsed;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.entity.MesItemUse;
import cn.jb.boot.biz.item.mapper.BomUsedMapper;
import cn.jb.boot.biz.item.mapper.MesItemStockMapper;
import cn.jb.boot.biz.item.mapper.MesItemUseMapper;
import cn.jb.boot.framework.common.exception.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**  导入一级bom后：
 *  mes_item_use → t_bom_used .
 *  作用：根据已经导入到 mes_item_use 表中的一级用料（即直接的母件-子件关系），递归地构建出整棵 BOM 树，然后把结果写入到 t_bom_used 表中
 *  */
@Service
public class BomTreeServiceImpl
		extends ServiceImpl<BomUsedMapper, BomUsed> {

	@Resource private MesItemUseMapper   useMapper;
	@Resource private MesItemStockMapper stockMapper;

	@Transactional
	public void rebuildByRoot(String rootItemNo) {


		// ❌ 原来这样查不到：
		// MesItemStock root = stockMapper.selectById(rootItemNo);

		// ✅ 改成按 item_no 字段查询：
		MesItemStock root = stockMapper.selectOne(
				new LambdaQueryWrapper<MesItemStock>()
						.eq(MesItemStock::getItemNo, rootItemNo)
		);
		if (root == null) {
			throw new ServiceException("根物料["+rootItemNo+"]不存在");
		}

		String rootBomNo = root.getBomNo();  // 根的图纸号


//		/* 1删旧 */
		this.remove(new LambdaQueryWrapper<BomUsed>()
				.eq(BomUsed::getItemNo, rootItemNo));

//		/* ② 递归展开 */
		List<BomUsed> buf = new ArrayList<>();
		dfs(rootItemNo, rootBomNo,rootItemNo, buf);

//		/* ③ 1补根节点 */
		BomUsed self = new BomUsed();
		self.setItemNo(rootItemNo);
		self.setBomNo(rootBomNo);
		self.setParentCode(rootItemNo);
		self.setUseItemNo(rootItemNo);
		self.setUseItemType("01");
		self.setUseItemCount(BigDecimal.ONE);
		self.setFixedUsed(BigDecimal.ONE); // 新增：固定用量与数量保持一致
		self.setItemNos(rootItemNo);
		buf.add(self);

//		/* ④ 批量写入 */
		this.saveBatch(buf, 1000);
	}

	/** 深度优先展开 */
	private void dfs(String root, String rootBomNo, String parent, List<BomUsed> out) {

		List<MesItemUse> children = useMapper.selectList(
				new LambdaQueryWrapper<MesItemUse>()
						.eq(MesItemUse::getItemNo, parent));

		for (MesItemUse mu : children) {
			BomUsed bu = new BomUsed();
			bu.setItemNo(root);
			bu.setBomNo(rootBomNo);              // ← 写入根的 bom_no
			bu.setParentCode(parent);
			bu.setUseItemNo(mu.getUseItemNo());
			bu.setUseItemType(mu.getUseItemType());
			BigDecimal qty = mu.getUseItemCount();
			if (qty == null) {                 // use_item_count 为空时，用固定用量
				qty = mu.getFixedUse();
			}
			bu.setUseItemCount(mu.getUseItemCount());
			bu.setFixedUsed(qty);              // 新增：固定用量同步

			bu.setItemNos(root + "|" + mu.getUseItemNo());
			out.add(bu);

			if ("01".equals(mu.getUseItemType())) {
				dfs(root,  rootBomNo,mu.getUseItemNo(), out);   // 下钻子 BOM
			}
		}
	}
}
