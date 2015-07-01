package com.android.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtil {
	String src = "";
	String dest = "";
	int rate = 0;
	
	public ImageUtil(String src, String dest, int rate){
		this.src = src;
		this.dest = dest;
		this.rate = rate;
	}
	
	private int[] getRGB(int pixel){
		int[] rgb = new int[3];
		
		rgb[0] = (pixel & 0xff0000 ) >> 16 ;
		rgb[1] = (pixel & 0xff00 ) >> 8 ;
		rgb[2] = (pixel & 0xff );
		
		return rgb;
	}
	
	private boolean verifyRGB(int[] rgb_1,int[] rgb_2){
		boolean iRet = true;
		for(int i=0;i<rgb_1.length;i++){
			if(rgb_1[i] != rgb_2[i]) {
				iRet = false;
				break;
			}
		}
		return iRet;
	}
	
	public static BufferedImage getBufferImage(String path) throws IOException{
		File file = new File(path);
		return ImageIO.read(file);
	}
	
	
	public boolean compare(int startX,int startY,int width,int height) throws IOException {
		BufferedImage bi_1;
		BufferedImage bi_2;
		
		int[] rgb_1 ;
		int[] rgb_2 ;
		
		int fPixel = 0;
		int pPixel = 0;
		
		bi_1 = getBufferImage(src);
		bi_2 = getBufferImage(dest);

		if(bi_1.getWidth() == bi_2.getWidth() && bi_1.getHeight() == bi_2.getHeight())
		{
			for (int y = (startY+1); y < (startY+height); y++) {
				for (int x = (startX+1) ; x < (startX+width); x++) {
					rgb_1 = getRGB(bi_1.getRGB(x,y));
					rgb_2 = getRGB(bi_2.getRGB(x,y));
					if(verifyRGB(rgb_1,rgb_2) == true)
					{
						pPixel++;
					}else{
						fPixel++;
					}
				}
			}
		}
		
		float pRate = ((float)((float)pPixel/(float)(pPixel+fPixel))*100);
		//String str = " Similarity rate="+pRate+"%";
		//Log.addLog(str);
		if(pRate >= rate){
			return true;
		}else{
			return false;
		}
	}
}
