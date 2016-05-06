package com.android.minicap;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;

public class DeviceSocketClient {
	private Socket socket = null;
	private String msg = "";
	private Service sc = null;
	private Thread thread = null;
	private Display display = null;
	private GC gc = null;
	
	public DeviceSocketClient(Display display, GC gc) {
		this.display = display;
		this.gc = gc;
	}
	    
	    public boolean connect(String host,int port) throws Exception{
	    	try {  
	            socket = new Socket(host, port);
	            
	            sc = new Service(socket);
		    	thread = new Thread(sc);
		    	thread.start();
	        } catch (Exception ex) {
	        	throw new Exception("[send]连接服务器失败");
	        }
	    	return true;
	    }
	    
	    public void send(String msg){
	    	sc.sendmsg(msg);
	    }
	    
	    public class Service implements Runnable {
	    	
	    	private Socket socket;
		   	private BufferedReader in = null;
		   	private PrintWriter pout = null;
		   	private String msg = "";
		   	
		   	private DataInputStream input = null;
		   	
		   	public Service(Socket socket) {
		   		this.socket = socket;  
		   		try {
		   			//in =  new BufferedReader(new InputStreamReader(socket.getInputStream()));  
		   			//pout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
		   			input = new DataInputStream(socket.getInputStream());
		   		} catch (IOException e) {
		   			 e.printStackTrace();
		   		 }
		   	}
		   	
		   	public void sendmsg(String message) {
		   		pout.println(message);
		   	}

		    public final int LittleEndian2BigEndian(byte[] w)
	        {
		        return ( w[ 3 ] ) << 24
		               | ( w[ 2 ] & 0xff ) << 16
		               | ( w[ 1 ] & 0xff ) << 8
		               | ( w[ 0 ] & 0xff );
	        }
		    
		    private void save(byte[] imageBytes) {
		    	File outputFile = new File("d:/output.jpg");  
		        FileOutputStream outputFileStream = null; 
		        
		        try {  
		            outputFileStream = new FileOutputStream(outputFile); 
		            outputFileStream.write(imageBytes);
		        } catch (FileNotFoundException e) {  
		            e.printStackTrace();  
		        } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						outputFileStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		    }
		    
			private Image createImage(byte[] imageBytes) {
				Image image = null;
				try {
					//save(imageBytes);
					ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
					image = new Image(display, bais);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return image;
			}
		   	
			public void run() {
				try{
					byte version = 0;
					byte header_length = 0;
					byte[] pid = new byte[4];
					byte[] width = new byte[4];
					byte[] height = new byte[4];
					byte[] vwidth = new byte[4];
					byte[] vheight = new byte[4];
					
					byte orientation = 0;
					byte quirk_bitflags = 0;
					
					
					version = input.readByte();
					header_length = input.readByte();
					input.readFully(pid);
					input.readFully(width);
					input.readFully(height);
					input.readFully(vwidth);
					input.readFully(vheight);
					orientation = input.readByte();
					quirk_bitflags = input.readByte();
					
							
//					System.out.println("version:" + version);
//					System.out.println("header_length:" + header_length);
//					System.out.println("pid:" + this.LittleEndian2BigEndian(pid));
//					System.out.println("width:" + this.LittleEndian2BigEndian(width));
//					System.out.println("width:" + this.LittleEndian2BigEndian(height));
//					System.out.println("vwidth:" + this.LittleEndian2BigEndian(vwidth));
//					System.out.println("vheight:" + this.LittleEndian2BigEndian(vheight));
//					System.out.println("orientation:" + orientation);
//					System.out.println("quirk_bitflags:" + quirk_bitflags);
					
					while(true) {
						byte[] frame_size = new byte[4];
						input.readFully(frame_size);
						byte[] data = new byte[this.LittleEndian2BigEndian(frame_size)];
						input.readFully(data);
						Image image = createImage(data);
						gc.drawImage(image, 0, 0, image.getImageData().width, image.getImageData().height, 0, 0, 310, 480);
						image.dispose();
					}
				}catch(Exception ex) {
					Logger.getLogger(DeviceSocketClient.class).error(ex);
				}
			}
	    	
	    }
	    
	    private ImageData getImage(byte[] data, int width, int lenght) throws IOException {
	    	ByteArrayInputStream in = new ByteArrayInputStream(data);
	    	BufferedImage image = ImageIO.read(in); 
	    	
	    	return getImageData(image, width, lenght);
	    }
	    
		public static ImageData getImageData(BufferedImage bufferedImage, int scaledWidth, int scaledHeight){
			 ImageData data = null;
			 
			 BufferedImage scaledImage = scaledImage(bufferedImage,scaledWidth,scaledHeight);
			 data = getImageData2(scaledImage);
			 
			 //System.out.println(WIDTH + " " + HEIGHT);
		     return data == null ? null : data;
			 //return data == null ? null : data.scaledTo(100, 200);
			 //return data;
		 }
		
		private static ImageData getImageData2(BufferedImage bufferedImage){
	        DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
	        //System.out.println("robot:" +colorModel.getRedMask() + " "+colorModel.getGreenMask() + " "+colorModel.getBlueMask());   
	        PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel
	                .getBlueMask());
	        ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel
	                .getPixelSize(), palette);
	        WritableRaster raster = bufferedImage.getRaster();
	        int[] pixelArray = new int[3];
	        for (int y = 0; y < data.height; y++) {
	            for (int x = 0; x < data.width; x++) {
	                raster.getPixel(x, y, pixelArray);
	                int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
	                data.setPixel(x, y, pixel);
	            }
	        }
	        return data;
	    }
	    
		private static BufferedImage scaledImage(BufferedImage bufferedImage,
				 int width,int height){
			 java.awt.Image scaledImage = bufferedImage.getScaledInstance(width, height, 
					 java.awt.Image.SCALE_AREA_AVERAGING);
			 
			 BufferedImage scaledBufferedImage = new BufferedImage(width, height,
	                BufferedImage.TYPE_INT_RGB);
	        Graphics2D g2 = scaledBufferedImage.createGraphics();
	        g2.drawImage(scaledImage, 0, 0, width, height, java.awt.Color.white, null);
	        g2.dispose();
	        
	        float[] kernelData2 = { -0.125f, -0.125f, -0.125f, -0.125f, 2,
	                -0.125f, -0.125f, -0.125f, -0.125f };
	        Kernel kernel = new Kernel(3, 3, kernelData2);
	        java.awt.image.ConvolveOp cOp = new java.awt.image.ConvolveOp(kernel, java.awt.image.ConvolveOp.EDGE_NO_OP, null);
	        scaledBufferedImage = cOp.filter(scaledBufferedImage, null);
	        
	        return scaledBufferedImage;
		 }
	    
		public void disconnect(){
	    	try {
	    		if(socket != null){
	    			socket.close();
	    		}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
}

