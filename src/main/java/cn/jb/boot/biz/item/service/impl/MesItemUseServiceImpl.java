package cn.jb.boot.biz.item.service.impl;

import cn.jb.boot.biz.item.entity.BomUsed;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.entity.MesItemUse;
import cn.jb.boot.biz.item.enums.ItemType;
import cn.jb.boot.biz.item.mapper.BomUsedMapper;
import cn.jb.boot.biz.item.mapper.MesItemUseMapper;
import cn.jb.boot.biz.item.service.BomUsedService;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.biz.item.service.MesItemUseService;
import cn.jb.boot.biz.item.vo.request.ItemNoRequest;
import cn.jb.boot.biz.item.vo.request.MesItemUseCreateRequest;
import cn.jb.boot.biz.item.vo.request.MesItemUsedUploadRequest;
import cn.jb.boot.biz.item.vo.response.ItemResp;
import cn.jb.boot.biz.item.vo.response.MesItemUseInfoResponse;
import cn.jb.boot.biz.item.vo.response.UseItemTreeResp;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.framework.config.SpringContextUtil;
import cn.jb.boot.framework.enums.ValidEnum;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.util.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 产品用料表 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 16:55:58
 */
@Service
@Slf4j
public class MesItemUseServiceImpl extends ServiceImpl<MesItemUseMapper, MesItemUse> implements MesItemUseService {

	@Resource
	private MesItemUseMapper mapper;
	@Resource
	private MesItemStockService stockService;
	@Resource
	private BomUsedService bomUsedService;

	@Resource
	private MesItemUseService mesItemUseService;


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Resource
	private BomUsedMapper bomUsedMapper;

	/** bom树构造器  （  将 List<BomUsed> list  + String root   构建为前端树结构）*/
//    @Override
//    public UseItemTreeResp itemUseTreeNew(ItemNoRequest params) {
//
//        String root = params.getItemNo();
//        // ───────────【修改】一次性查询：根节点及其所有后代
//        List<BomUsed> list = bomUsedMapper.selectList(
//                new LambdaQueryWrapper<BomUsed>()
//                        .and(w -> w.eq(BomUsed::getItemNos, root)
//                                .or().likeRight(BomUsed::getItemNos, root + "|"))
//        );
//        // ───────────【保留】内存组装递归树， 使用原来的
////        return BomUsedUtil.tree(list, root);   原始的正常使用
//        return BomUsedNewUtil.tree(list, root);  //测试
//
//
//    }


	/**
	 * bom树构造器  （  将 List<BomUsed> list  + String root   构建为前端树结构）
	 */
	@Override
	public UseItemTreeResp itemUseTreeNew(ItemNoRequest params) {
		String root = params.getItemNo();
		try {


			// ───────────【修改】一次性查询：根节点及其所有后代
			List<BomUsed> list = bomUsedMapper.selectList(
					new LambdaQueryWrapper<BomUsed>()
							.and(w -> w.eq(BomUsed::getItemNos, root)
									.or().likeRight(BomUsed::getItemNos, root + "|"))
			);
			if (list == null || list.isEmpty()) {
				throw new RuntimeException("未查询到BOM数据，无法构建用料树！");  // 主动抛异常
			}

// ───────────【保留】内存组装递归树
			UseItemTreeResp tree = BomUsedNewUtil.tree(list, root);
			if (tree == null) {
				throw new RuntimeException("用料树构建失败，请稍候重试！");  // 主动抛异常
			}
			return tree;


		} catch (Exception e) {
			log.error("itemUseTreeNew 构建用料树失败，itemNo={}", root, e);
			throw new RuntimeException("用料树加载失败，请稍候重试！");

		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void useDateFromErp(Set<String> items) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				ThreadPool.commonExecute(() -> {
					List<String> list = new ArrayList<>();
					for (String itemNo : items) {
						SpringContextUtil.getBeanByClass(BomUsedService.class).loadParBomData(itemNo, list);
					}
				});
			}
		});
	}


	@Override
	public UseItemTreeResp itemUseTree(ItemNoRequest params) {
		//用料树
		List<BomUsed> list = bomUsedService.tree(params.getItemNo());
		return BomUsedUtil.tree(list, params.getItemNo());
	}


	@Override
	public void export(HttpServletResponse response, String id) {


	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void upload(HttpServletRequest request) {
		MultipartFile file = FileUtil.getFile(request);
		List<MesItemUsedUploadRequest> list = EasyExcelUtil.importExcel(file, MesItemUsedUploadRequest.class);
		if (CollectionUtils.isNotEmpty(list)) {
			list = checkByBoms(list);
			list = checkByItemNos(list);
			if (CollectionUtils.isNotEmpty(list)) {
				List<String> itemNos =
						list.stream().map(MesItemUsedUploadRequest::getItemNo).collect(Collectors.toList());
				this.remove(new LambdaQueryWrapper<MesItemUse>().in(MesItemUse::getItemNo, itemNos));
				List<MesItemUsedUploadRequest> dataList = list.stream().filter(d -> !d.getItemNo().equals(d.getUseItemNo())).collect(Collectors.toList());
				List<MesItemUse> mesItemUses = PojoUtil.copyList(dataList, MesItemUse.class);
				this.saveBatch(mesItemUses);
				Set<String> items = mesItemUses.stream().filter(d -> ItemType.isMaterials(d.getUseItemType())).map(MesItemUse::getUseItemNo).collect(Collectors.toSet());
				items.addAll(itemNos);
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
					@Override
					public void afterCommit() {
						ThreadPool.commonExecute(() -> {
							List<String> list = new ArrayList<>();
							for (String itemNo : items) {
								SpringContextUtil.getBeanByClass(BomUsedService.class).loadParBomData(itemNo, list);
							}
						});
					}
				});

			}
		}
	}


	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void saveOrUpdateItem(MesItemUseCreateRequest params) {
		MesItemUse miu = PojoUtil.copyBean(params, MesItemUse.class);
		String userItemNo = params.getUseItemNo();
		checkItem(miu);
		if (params.getItemNo().equals(params.getUseItemNo())) {
			throw new CavException("用料编码不能和产品编码相同，请检查：" + params.getItemNo());
		}
		MesItemStock mis = stockService.getByItemNo(userItemNo);
		miu.setUseItemCount(params.getFixedUse());
		miu.setUseItemType(mis.getItemType());
		miu.setVariUse(BigDecimal.ZERO);
		miu.setVariUseAssist(BigDecimal.ZERO);
		this.saveOrUpdate(miu);
		MesItemStock itemStock = stockService.getByItemNo(params.getItemNo());
		bomUsedService.loadBomData(itemStock);
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				ThreadPool.commonExecute(() -> {
					SpringContextUtil.getBeanByClass(BomUsedService.class).loadParBomData(itemStock.getItemNo(), new ArrayList<>());
				});
			}
		});
	}

	private void checkItem(MesItemUse miu) {
		LambdaQueryWrapper<MesItemUse> eq = new LambdaQueryWrapper<MesItemUse>().eq(MesItemUse::getItemNo,
						miu.getItemNo())
				.eq(MesItemUse::getUseItemNo, miu.getUseItemNo());
		MesItemUse one = this.getOne(eq);
		if (Objects.nonNull(one)) {
			if (StringUtils.isEmpty(miu.getId())) {
				throw new CavException(miu.getItemNo() + "下已存在物料：" + miu.getUseItemNo());
			} else {
				if (!miu.getId().equals(one.getId())) {
					throw new CavException(miu.getItemNo() + "下已存在物料：" + miu.getUseItemNo());
				}
			}
		}
	}

	@Override
	public List<ItemResp> itemList(ItemNoRequest params) {
		List<MesItemStock> list = stockService.list(new LambdaUpdateWrapper<MesItemStock>()
				.eq(MesItemStock::getIsValid, ValidEnum.VALID.getCode())
				.ne(MesItemStock::getItemNo, params.getItemNo()));
		return list.stream().map(d -> {
			String itemName = ItemType.isBom(d.getItemType()) ? d.getItemName() + "(" + d.getBomNo() + ")"
					: d.getItemName() + "(" + d.getItemNo() + ")";
			return new ItemResp(d.getItemNo(), itemName);
		}).collect(Collectors.toList());
	}

	@Override
	public MesItemUseInfoResponse getInfoById(String id) {
		MesItemUse miu = this.getById(id);
		MesItemUseInfoResponse mius = PojoUtil.copyBean(miu, MesItemUseInfoResponse.class);
		String itemName = DictUtil.getDictName(DictType.BOM_NO, mius.getItemNo());
		String useItemName = DictUtil.getDictName(DictType.BOM_NO, mius.getUseItemNo());
		mius.setBomNo(DictUtil.getDictLabel(DictType.BOM_NO, mius.getItemNo()));
		mius.setUseBomNo(DictUtil.getDictLabel(DictType.BOM_NO, mius.getUseItemNo()));

		mius.setItemName(itemName);
		mius.setUseItemName(useItemName);
		return mius;
	}


	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void delete(String id) {
		MesItemUse use = this.getById(id);
		String itemNo = use.getItemNo();
		this.removeById(id);
		MesItemStock mis = stockService.getByItemNo(itemNo);
		bomUsedService.loadBomData(mis);
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				ThreadPool.commonExecute(() -> {
					SpringContextUtil.getBeanByClass(BomUsedService.class).loadParBomData(mis.getItemNo(), new ArrayList<>());
				});
			}
		});
	}


	@Nullable
	private List<MesItemUsedUploadRequest> checkByItemNos(List<MesItemUsedUploadRequest> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			List<String> useItemNos =
					list.stream().map(MesItemUsedUploadRequest::getUseItemNo).collect(Collectors.toList());
			Map<String, MesItemStock> useItemMap = stockService.getByItemNos(useItemNos);
			list = list.stream().filter(d -> useItemMap.containsKey(d.getUseItemNo())).peek(d -> {
						MesItemStock mis = useItemMap.get(d.getUseItemNo());
						d.setUseItemType(mis.getItemType());
						d.setUseItemMeasure(mis.getItemMeasure());
						d.setUseItemCount(d.getFixedUse());
					}).filter(d -> StringUtils.isNotBlank(d.getUseItemNo()))
					.filter(d -> !d.getItemNo().equals(d.getUseItemNo()))
					.collect(Collectors.toList());
		}
		return list;
	}

	@NotNull
	private List<MesItemUsedUploadRequest> checkByBoms(List<MesItemUsedUploadRequest> list) {
		List<String> boms = list.stream().map(MesItemUsedUploadRequest::getBomNo).collect(Collectors.toList());
		Map<String, MesItemStock> bomMap = stockService.getByBomNos(boms);
		list = list.stream().filter(d -> bomMap.containsKey(d.getBomNo())).peek(d -> {
			MesItemStock mis = bomMap.get(d.getBomNo());
			d.setItemNo(mis.getItemNo());
		}).collect(Collectors.toList());
		return list;
	}

	@Transactional(rollbackFor = Throwable.class)
	public void insertItemUsed(List<MesItemUsedUploadRequest> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			list = checkByBoms(list);
			list = checkByItemNos(list);
			if (CollectionUtils.isNotEmpty(list)) {
				List<String> itemNos =
						list.stream().map(MesItemUsedUploadRequest::getItemNo).collect(Collectors.toList());
				this.remove(new LambdaQueryWrapper<MesItemUse>().in(MesItemUse::getItemNo, itemNos));
				List<MesItemUsedUploadRequest> dataList = list.stream().filter(d -> !d.getItemNo().equals(d.getUseItemNo())).collect(Collectors.toList());
				List<MesItemUse> mesItemUses = PojoUtil.copyList(dataList, MesItemUse.class);
				this.saveBatch(mesItemUses);
				Set<String> items = mesItemUses.stream().filter(d -> ItemType.isMaterials(d.getUseItemType())).map(MesItemUse::getUseItemNo).collect(Collectors.toSet());
				items.addAll(itemNos);
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
					@Override
					public void afterCommit() {
						ThreadPool.commonExecute(() -> {
							List<String> list = new ArrayList<>();
							for (String itemNo : items) {
								SpringContextUtil.getBeanByClass(BomUsedService.class).loadParBomData(itemNo, list);
							}
						});
					}
				});
			}
		}
	}

}
