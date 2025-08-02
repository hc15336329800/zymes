package cn.jb.boot.biz.item.vo.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportResult {
	private List<String> successList = new ArrayList<>();
	private List<FailItem> failList = new ArrayList<>();

	@Data
	public static class FailItem {
		private String itemNo;
		private String reason;
	}
}
