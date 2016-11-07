package com.utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

/** 图片处理工具类 */
public class ImageUtil {
	
	/***
	 * 压缩图片
	 * @param file	原始图片
	 * @param size	要压缩到的大小
	 * @return	图片对象
	 * @throws IOException
	 */
	public BufferedImage resetImg(File file, Dimension size) throws IOException {
		return resetImg(file, size.width, size.height);
	}
	
	/***
	 * 压缩图片
	 * @param file	原始图片
	 * @param w	要压缩到的宽度(<=0则压缩至当前宽度的一半)
	 * @param h	要压缩到的高度(<=0则压缩至当前高度的一半)
	 * @return	图片对象
	 * @throws IOException
	 */
	public BufferedImage resetImg(File file, int w, int h) throws IOException {
		Image tempImg = Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath());
		BufferedImage img = toBufferedImage(tempImg);
		w = w>0?w:img.getWidth(null)/2;
		h = h>0?h:img.getHeight(null)/2;
		Image img2 = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
		BufferedImage img2Buffered = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) img2Buffered.getGraphics();
		g.drawImage(img2, 0, 0, null);
		return img2Buffered;
	}
	
	
	/***
	 * 将Image转换为bufferedImage
	 * @param image	原image图片
	 * @return	转换后的图片
	 */
	public static BufferedImage toBufferedImage(Image image) {
	    if (image instanceof BufferedImage) {
	        return (BufferedImage)image;
	     }
	 
	    // This code ensures that all the pixels in the image are loaded
	     image = new ImageIcon(image).getImage();
	 
	    // Determine if the image has transparent pixels; for this method's
	    // implementation, see e661 Determining If an Image Has Transparent Pixels
	    //boolean hasAlpha = hasAlpha(image);
	 
	    // Create a buffered image with a format that's compatible with the screen
	     BufferedImage bimage = null;
	     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    try {
	        // Determine the type of transparency of the new buffered image
	        int transparency = Transparency.OPAQUE;
	       /* if (hasAlpha) {
	         transparency = Transparency.BITMASK;
	         }*/
	 
	        // Create the buffered image
	         GraphicsDevice gs = ge.getDefaultScreenDevice();
	         GraphicsConfiguration gc = gs.getDefaultConfiguration();
	         bimage = gc.createCompatibleImage(
	         image.getWidth(null), image.getHeight(null), transparency);
	     } catch (Exception e) {
	    	 e.printStackTrace();
	     }
	 
	    if (bimage == null) {
	        // Create a buffered image using the default color model
	        int type = BufferedImage.TYPE_INT_RGB;
	        //int type = BufferedImage.TYPE_3BYTE_BGR;//by wang
	        /*if (hasAlpha) {
	         type = BufferedImage.TYPE_INT_ARGB;
	         }*/
	         bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
	     }
	 
	    // Copy image to buffered image
	     Graphics g = bimage.createGraphics();
	 
	    // Paint the image onto the buffered image
	     g.drawImage(image, 0, 0, null);
	     g.dispose();
	 
	    return bimage;
	}
	
	public static void main(String[] args) {
		try {
			new ImageUtil();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
