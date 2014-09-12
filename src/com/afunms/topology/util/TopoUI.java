package com.afunms.topology.util;

import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.EquipImage;
import com.afunms.topology.model.HintItem;
import java.util.List;

public class TopoUI {

	// 示意图元
	public String createGallery(List<String> list, int index, int buttonStyle) {
		String selectGradeOne = "";
		String galleryPanel = "";
		String resTypeName = "";
		String buttonPanel = "";
		if (list != null) {

			selectGradeOne = "&nbsp; <select id=\"resTypeSort\" onchange=\"updateGalleryPanel();\">";

			for (int i = 0; i < list.size(); ++i) {
				resTypeName = (String) list.get(i);
				selectGradeOne = selectGradeOne + "<option value=\""
						+ resTypeName + "\">" + resTypeName + "</option>";
			}

			selectGradeOne = selectGradeOne + "</select>";
		}

		if (buttonStyle == 0) {
			buttonPanel = "<a class=\"buttonStyle_1\"   href=\"#\" onclick=\\'ShowHide(\"1\",\"none\");\\'>取 消</a>";
		} else if (buttonStyle == 1) {
			buttonPanel = "<input type=\"button\" value=\"保存\" style=\"width:50\"  onclick=\"save()\">" +
					"&nbsp;&nbsp;<input type=\"button\" value=\"关闭\" style=\"width:50\"  onclick=\"window.close();\">";
		}

		String html = "<tr><td nowrap align=\"right\" height=\"24\" width=\"20%\">图元类型&nbsp;</td>" +
				"<td nowrap width=\"80%\">"
				+ selectGradeOne
				+ "</td></tr><tr><td colspan=\"2\"><table width=\"100%\"><tr>"
				+ "<td align=\"center\" width=\"20%\" id=\"galleryPanel\">"
				+ galleryPanel
				+ "</td></tr></table></td></tr>"
				+ "<br><table width=\"200\" align=\"center\"><tr><td align=\"center\">"
				+ buttonPanel + "</td>" + "</tr></table>";

		return html;
	}

	// 实体图元
	public String createGallery(List<String> list) {

		String selectGradeOne = "";
		String galleryPanel = "";
		String category = "";
		String cn_name = "";
		String buttonPanel = "";
		if (list != null) {

			selectGradeOne = "&nbsp; <select id=\"resTypeSort\" onchange=\"updateGalleryPanel();\">";

			for (int i = 0; i < list.size(); ++i) {
				category = (String) list.get(i).split(",")[0];
				cn_name = (String) list.get(i).split(",")[1];
				selectGradeOne = selectGradeOne + "<option value=\"" + category
						+ "\">" + cn_name + "</option>";
			}

			selectGradeOne = selectGradeOne + "</select>";
		}

		buttonPanel = "<input type=\"button\" value=\"保存\" style=\"width:50\"  onclick=\"save()\">" +
				      "&nbsp;&nbsp;<input type=\"button\" value=\"关闭\" style=\"width:50\"  onclick=\"window.close();\">";

		String html = "<tr><td  nowrap align=\"right\" height=\"24\" width=\"20%\">图元类型&nbsp;" +
				"</td><td align=\"left\" style=\"padding-left:0px;\">"
				+ selectGradeOne
				+ "</td></tr><tr><td colspan=\"2\"><table width=\"100%\"><tr>"
				+ "<td align=\"center\" width=\"20%\" id=\"galleryPanel\">"
				+ galleryPanel
				+ "</td></tr></table></td></tr>"
				+ "<br><table width=\"200\" align=\"center\"><tr><td align=\"center\">"
				+ buttonPanel + "</td>" + "</tr></table>";

		return html;
	}

	// 示意图元表
	public String getGalleryPanel(List<HintItem> list) {

		String galleryPanel = "";

		try {
			for (int j = 0; j < list.size(); ++j) {
				HintItem dto = (HintItem) list.get(j);
				String type = dto.getSortName();
				String path = dto.getWebIconPath();
				galleryPanel = galleryPanel
						+ "<img width=32 height=32 onClick=\"changeStyle(this.id)\" id=\""
						+ path + "\" src=\"" + path + "\"  alt=\"" + type
						+ "\"/>&nbsp;&nbsp;";
			}
		} catch (Exception e) {
			SysLogger.error("TopoUI.getGalleryPanel()", e);
		}
		return galleryPanel;
	}

	// 实体图元表
	public String getEquipGalleryPanel(List<EquipImage> list) {

		String galleryPanel = "";

		try {
			for (int j = 0; j < list.size(); ++j) {
				EquipImage dto = (EquipImage) list.get(j);
				String id = dto.getId() + "";
				String type = dto.getCnName();
				String path = dto.getPath();
				galleryPanel = galleryPanel
						+ "<img width=32 height=32 onClick=\"changeStyle(this.id)\" id=\""
						+ id + "\" src=\"" + path + "\"  alt=\"" + type
						+ "\"/>&nbsp;&nbsp;";
			}
		} catch (Exception e) {
			SysLogger.error("TopoUI.getEquipGalleryPanel()", e);
		}
		return galleryPanel;
	}
}
