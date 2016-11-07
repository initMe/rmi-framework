package com.manager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.bean.utils.webcode.CheckBean;
import com.bean.utils.webcode.CheckResultBean;
import com.bean.utils.webcode.CodeBean;
import com.utils.BeanSort;

/** 验证码处理器 */
public class WebCodeExecutor {
	/** code所有值 */
	private List<CodeBean> codeList = new ArrayList<CodeBean>();
	/** 单个字符长宽 */
	private Dimension onceDim = new Dimension(12, 20);
	/** RGB上下25个色度的模糊度，防止验证码使用过模糊化 */
	public static int likeRate = 25;
	/** 起始坐标 */
	private Point startPoint = new Point(5, 1);

	public WebCodeExecutor() {
		init();
	}

	/** 初始化 */
	private void init() {
		codeList.clear();
		for (char ch = '0'; ch <= '9'; ch++) {
			codeList.add(new CodeBean(ch + ""));
		}
		for (char ch = 'A'; ch <= 'Z'; ch++) {
			codeList.add(new CodeBean(ch + ""));
		}
		for (char ch = 'a'; ch <= 'z'; ch++) {
			codeList.add(new CodeBean(ch + ""));
		}

		// 创建空图，用以计算和获取字符图形上点的分布
		BufferedImage newImg = new BufferedImage((onceDim.width * 4) + 1, onceDim.height + 1, BufferedImage.TYPE_3BYTE_BGR); // BufferedImage.TYPE_3BYTE_BGR;
		Graphics2D g = newImg.createGraphics();
		// 循环分析
		for (CodeBean code : codeList) {
			g.setColor(Color.white);
			g.fillRect(0, 0, newImg.getWidth(), newImg.getHeight());
			g.setFont(new Font("楷体", Font.BOLD, 22));
			g.setColor(Color.black);
			g.drawString(code.getCode(), 0, 16);
			List<Point> drawList = new ArrayList<Point>();
			for (int x = 0; x < onceDim.width; x++) {
				for (int y = 0; y < onceDim.height; y++) {
					if (newImg.getRGB(x, y) == Color.black.getRGB()) {
						drawList.add(new Point(x, y));
					}
				}
			}
			//			ic.change(newImg);
			// 插入该字符的图形点坐标
			code.setDrawList(drawList);
		}
	}

	/** 将验证码上的4位图片拆分出来 */
	private BufferedImage[] splitImg(BufferedImage baseImg) {
		Graphics g = baseImg.getGraphics();
		g.setColor(Color.white);
		for (int x = 0; x < baseImg.getWidth(); x++) {
			for (int y = 0; y < baseImg.getHeight(); y++) {
				Color c = new Color(baseImg.getRGB(x, y) & 0xFFFFFF);
				if (c.getRed() > 160 || c.getGreen() > 160 || c.getBlue() > 160) {
					g.drawLine(x, y, x, y);
				}
			}
		}
		BufferedImage imgs[] = new BufferedImage[4];
		for (int i = 0; i < imgs.length; i++) {
			imgs[i] = baseImg.getSubimage(startPoint.x + (onceDim.width * i), 0, onceDim.width, onceDim.height);
		}
		return imgs;
	}

	/** 依据原图，获取其中的验证码 
	 * @throws Exception */
	public String getCode(String url) throws Exception {
		return getCode(url, null);
	}

	/** 依据原图，获取其中的验证码 
	 * @throws Exception */
	public String getCode(BufferedImage webImg) throws Exception {
		long begin = System.currentTimeMillis();
		BufferedImage[] imgs = splitImg(webImg);
		String resultCode = "";
		// 排序器
		BeanSort<CheckResultBean> resultSort = new BeanSort<CheckResultBean>("num_rate");
		for (int index = 1; index <= 4; index++) {
			BufferedImage nowImg = imgs[index - 1];
			Map<Point, Color> imgMap = getImgPoint(nowImg);
			// 获取验证码颜色值
			CheckResultBean crb = checkCode(imgMap);
			if (crb == null) {
				resultCode += "?";
				continue;
			}
			// 验证码字符眼色
			int codeRgb = crb.getRgb();
			// 依据概率高的，再遍历一遍全图，把相似点整合后重新计算概率，忽略大于1的概率结果
			// 分析出干扰线
			List<Point> removeList = new ArrayList<Point>();
			for (Point p : imgMap.keySet()) {
				Color c = imgMap.get(p);
				if (!like(codeRgb, c.getRGB())) {
					removeList.add(p);
				}
			}
			// 移除干扰线
			for (Point p : removeList) {
				imgMap.remove(p);
			}
			List<CheckResultBean> resultList = new ArrayList<CheckResultBean>();
			// 对比已定的字符点，进行最终对比
			for (CodeBean cb : codeList) {
				CheckResultBean result = new CheckResultBean();
				result.setRgb(codeRgb);
				result.setCode(cb.getCode());
				for (Point p : cb.getDrawList()) {
					if (imgMap.containsKey(p)) {
						result.setNum(result.getNum() + 1);
					}
				}
				result.setRate(result.getNum() * 1.0D / cb.getDrawList().size());
				result.setNum_rate(result.getNum() * result.getRate());
				resultList.add(result);
			}
			resultSort.unSortList(resultList);
			resultCode += resultList.get(0).getCode();
			System.out.println("字符" + resultList.get(0).getCode() + "相似度:" + resultList.get(0).getRate());
			// 清理，准备下一次执行
			resultList.clear();
		}
		System.out.println("code2：" + resultCode + "\t用时：" + (System.currentTimeMillis() - begin) + "毫秒");
		return resultCode;
	}

	/** 依据原图，获取其中的验证码 
	 * @throws Exception */
	public String getCode(String url, StringBuffer sessionId) throws Exception {
		BufferedImage webImg = getImage(url, sessionId);
		return getCode(webImg);
	}

	/**
	 * 校验两个颜色的相似度
	 * @param baseRgb	 基础颜色
	 * @param likeRgb	要校验的颜色
	 * @return    是否相似(true:相似)
	 * @return boolean   
	 * @exception    
	 * @since  1.0.0
	 */
	public static boolean like(int baseRgb, int likeRgb) {
		Color base = new Color(baseRgb);
		Color like = new Color(likeRgb);
		if (base.getRed() + likeRate < like.getRed()
				|| base.getRed() - likeRate > like.getRed()) {
			return false;
		}
		if (base.getGreen() + likeRate < like.getGreen()
				|| base.getGreen() - likeRate > like.getGreen()) {
			return false;
		}
		if (base.getBlue() + likeRate < like.getBlue()
				|| base.getBlue() - likeRate > like.getBlue()) {
			return false;
		}
		return true;
	}

	/** 获取图片中所有不为背景的点 */
	private Map<Point, Color> getImgPoint(BufferedImage img) {
		Map<Point, Color> imgMap = new HashMap<Point, Color>();
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				int rgb = img.getRGB(x, y) & 0xFFFFFF;
				Color c = new Color(rgb);
				if (like(rgb, Color.white.getRGB())) {
					continue;
				}
				imgMap.put(new Point(x, y), c);
			}
		}
		return imgMap;
	}

	/** 获取验证码的颜色值   */
	private CheckResultBean checkCode(Map<Point, Color> imgMap) {
		CheckResultBean result = new CheckResultBean();
		List<CheckBean> checkList = new ArrayList<CheckBean>();
		for (Point p : imgMap.keySet()) {
			Color c = imgMap.get(new Point(p.x, p.y));
			int rgb = c.getRGB();
			if (like(rgb, Color.white.getRGB())) {
				continue;
			}
			CheckBean check = new CheckBean();
			check.setRgb(rgb);
			int findIndex = checkList.indexOf(check);
			if (findIndex >= 0) {
				check = checkList.get(findIndex);
			}
			else {
				checkList.add(check);
			}
			check.setNum(check.getNum() + 1);
		}
		if (checkList.size() == 0) {
			return null;
		}
		BeanSort<CheckBean> bs = new BeanSort<CheckBean>("num");
		bs.unSortList(checkList);
		double rate = checkList.get(0).getNum() * 1.0d / imgMap.size();
		result.setNum(checkList.get(0).getNum());
		result.setRate(rate);
		result.setRgb(checkList.get(0).getRgb());
		return result;
	}

	/** 获取远程图片流 
	 * @param sessionId */
	public ByteArrayInputStream getInput(String url, StringBuffer sessionId) throws Exception {
		URL realUrl = new URL(url);
		// 打开和URL之间的连接
		URLConnection connection = realUrl.openConnection();
		//不超时  
		connection.setConnectTimeout(0);
		//不允许缓存  
		connection.setUseCaches(false);
		connection.setDefaultUseCaches(false);
		InputStream input = ((HttpURLConnection) connection).getInputStream();
		if (sessionId != null) {
			String cookieValue = connection.getHeaderField("Set-Cookie");
			sessionId.delete(0, sessionId.length()).append(cookieValue.split(";")[0]);
		}
		ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
		byte[] bts = new byte[1024];
		int length = 0;
		while ((length = input.read(bts)) > 0) {
			byteArrayOutput.write(bts, 0, length);
		}
		byteArrayOutput.flush();
		return new ByteArrayInputStream(byteArrayOutput.toByteArray());
	}

	/** 获取远程图片对象 */
	public BufferedImage getImage(String url, StringBuffer sessionId) throws Exception {
		ByteArrayInputStream in = getInput(url, sessionId);
		return ImageIO.read(in);
		//		return ImageIO.read(new URL(url));
		//		return ImageIO.read(new File("C:\\Users\\F12_end\\Desktop\\seekUrlAction.gif"));
	}

	public static void main(String[] args) {
		try {
			//			CodeExecutor1 ce = new CodeExecutor1();
			//			long begin = System.currentTimeMillis();
			//			String code = ce.getCode("http://fapiao.youshang.com/seekUrlAction.do?querytype=yzm&cityid=11100&invoiceTypeId=1110001&rand="+System.currentTimeMillis());
			//			System.out.println("code："+ code +"\t用时："+ (System.currentTimeMillis()-begin)+"毫秒");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
