package com.afunms.common.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;




import com.afunms.initialize.ResourceCenter;
import com.afunms.system.model.User;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 这个Servlet处理图表页面传来的导出图片请求
 * 
 */
@SuppressWarnings("serial")
public class ArmChartServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String widthStr = request.getParameter("width") == null ? "650"
				: request.getParameter("width");

		String heightStr = (request.getParameter("height") == null) ? "300"
				: request.getParameter("height");
		// 页面flash的宽度和高度
		int width = Integer.parseInt(widthStr);
		int height = Integer.parseInt(heightStr);

		BufferedImage result = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		// 页面是将一个个像素作为参数传递进来的,所以如果图表越大,处理时间越长
		for (int y = 0; y < height; y++) {
			int x = 0;
			String[] row = request.getParameter("r" + y).split(",");
			for (int c = 0; c < row.length; c++) {
				String[] pixel = row[c].split(":"); // 十六进制颜色数组
				int repeat = pixel.length > 1 ? Integer.parseInt(pixel[1]) : 1;
				for (int l = 0; l < repeat; l++) {
					result.setRGB(x, y, Integer.parseInt(pixel[0], 16));
					x++;
				}
			}
		}
		response.setContentType("image/png");
		response.addHeader("Content-Disposition","attachment;filename=\"amchart.png\"");
		Graphics2D g = result.createGraphics();
		// 处理图形平滑度
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(result, 0, 0, width, height, null);
		g.dispose();

		ServletOutputStream f = response.getOutputStream();
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(f);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(result);
		param.setQuality((float) (100 / 100.0), true);// 设置图片质量,100最大,默认70
		encoder.encode(result, param);
		HttpSession session = request.getSession();
		User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
		String name = vo.getId()+"";
		String fileName = ResourceCenter.getInstance().getSysPath() + "temp/"
				+ name + "port.png";
		File file = new File(fileName);

		// ImageIO.write(result, "png", file);
		try { 
			ImageIO.write(result, "png", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ImageIO.write(result, "JPEG", response.getOutputStream());// 输出图片
		 f.close();
	}
	 
	
}