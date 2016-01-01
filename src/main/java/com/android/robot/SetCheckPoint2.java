package com.android.robot;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.android.python.Area;
import com.android.util.XmlUtil;

public class SetCheckPoint2 {
	
	private int startX = 0;  
    private int startY = 0;  
    private int endX = 0;  
    private int endY = 0;  
    private int offsetX = 0;  
    private int offsetY = 0;
    
    private int realWidth = 0;
    private int realHeight = 0;
    
    private GC gc = null;
    private Image image = null;
    private ArrayList<Image> imagesList = new ArrayList<Image>();
    private Image tempImage = null;
    private Text tLeft = null;
    private Text tRight = null;
    private Text tlWidth = null;
    private Text tlHeight = null;
    private Text tMatchPoint = null;
    private String file = null;
    private Shell parent = null;
    private Shell shell = null;
    
    private boolean isEditable = false;
    
    
    public SetCheckPoint2(Shell parent,String file){
    	this.parent = parent;
    	this.file = file;
    }
	
	private void setParams(int x,int y,int width,int height){
		this.tLeft.setText(Integer.toString(x*this.realWidth/320));
		this.tRight.setText(Integer.toString(y*this.realHeight/480));
		this.tlWidth.setText(Integer.toString(width*this.realWidth/320));
		this.tlHeight.setText(Integer.toString(height*this.realHeight/480));
	}
	
	private Area loadScreenArea(String pngFile) throws Exception{
		Area area = null;
		String xmlArea = System.getProperty("user.dir") + 
				"/workspace/" + file.substring(0, file.indexOf("/"))+ "/screen_area.xml";
		System.out.println(xmlArea);
		XmlUtil xmlUtil = new XmlUtil(xmlArea);
		Document doc = xmlUtil.parse(xmlArea);
		Element root = doc.getDocumentElement();

		NodeList childs = root.getChildNodes();
        if(childs!=null){
        	for(int i=0;i<childs.getLength();i++){
        		Node node = childs.item(i);
        		if(node.getNodeType()==Node.ELEMENT_NODE){
        			NamedNodeMap map = node.getAttributes();
        			String fileName = map.getNamedItem("file").getNodeValue();
        			if(fileName.equals(pngFile)){
        				area = new Area();
        				area.setFile(fileName);
        				area.setX(Integer.parseInt(map.getNamedItem("x").getNodeValue()));
        				area.setY(Integer.parseInt(map.getNamedItem("y").getNodeValue()));
        				area.setWidth(Integer.parseInt(map.getNamedItem("width").getNodeValue()));
        				area.setHeight(Integer.parseInt(map.getNamedItem("height").getNodeValue()));
        			}
        		}
        	}
        	
        }
        return area;
	}
	
	private void updateScreenArea(String pngFile, Area area) throws Exception{
		String xmlArea = System.getProperty("user.dir") + 
				"/workspace/" + file.substring(0, file.indexOf("/"))+ "/screen_area.xml";
		XmlUtil xmlUtil = new XmlUtil(xmlArea);
		Document doc = xmlUtil.parse(xmlArea);
		Element root = doc.getDocumentElement();

		NodeList childs = root.getChildNodes();
		if(childs!=null){
			//del
        	for(int i=0;i<childs.getLength();i++){
        		Node node = childs.item(i);
        		if(node.getNodeType()==Node.ELEMENT_NODE){
        			NamedNodeMap map = node.getAttributes();
        			String fileName = map.getNamedItem("file").getNodeValue();
        			if(fileName.equals(pngFile)){
        				root.removeChild(node);
        				break;
        			}
        		}
        	}
        }
		
		//add
        Hashtable<String,String> attri = new Hashtable();
        attri.put("file", area.getFile());
        attri.put("x", String.valueOf(area.getX()));
        attri.put("y", String.valueOf(area.getY()));
        attri.put("width", String.valueOf(area.getWidth()));
        attri.put("height", String.valueOf(area.getHeight()));
        xmlUtil.appendNode(root.getOwnerDocument(), "area", "", attri);
        xmlUtil.flush(doc);
	}
    public void drawRectangle(GC gc) {  
        offsetX = endX - startX;  
        offsetY = endY - startY; 
        if (gc != null) {
            gc.setLineWidth(3);
            gc.setForeground(new Color(Display.getDefault(), 255, 0, 0));
            setParams(startX, startY, offsetX, offsetY);
            gc.drawRectangle(startX, startY, offsetX, offsetY);  
            tempImage = new Image(Display.getDefault(), ClassLoader.getSystemResourceAsStream("icons/temp.png"));
            gc.copyArea(tempImage, 0, 0);
            imagesList.add(image);
        }  
    }
    
    public ImageData getImageData(BufferedImage bufferedImage){
        DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
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
	
    public static BufferedImage getBufferedImage(ImageIcon icon){
        int width = 320;//icon.getIconWidth();
        int height = 480;//icon.getIconHeight();
        ImageObserver observer = icon.getImageObserver();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics gc = bufferedImage.createGraphics();
        gc.drawImage(icon.getImage(), 0, 0, observer);
        
        return bufferedImage;
    }
    
	public void canvasDrawImage(GC gc) {
        try {  
        	
            FileInputStream input = new FileInputStream(new File(System.getProperty("user.dir") + "/workspace/" + file));
            ImageData imageData = new ImageData(input); 
            image = new Image(Display.getDefault(), imageData);

            this.realHeight = imageData.height;
            this.realWidth = imageData.width;
            gc.drawImage(image, 0, 0,imageData.width,imageData.height,0,0,320,480);
            input.close();
            imagesList.add(image);
        } catch (Exception e) {
            e.printStackTrace();  
        }
    }
	
	public void backOperatoin(KeyEvent e) {  
        if ((e.stateMask & SWT.CTRL) != 0) {  
            if (e.keyCode == 'z') {//CTRL + z
                if (imagesList != null && imagesList.size() > 0) {
                    int size = imagesList.size();  
                    //取出最后一个元素  
                    image = imagesList.get(size - 1);  
                    //显示到Canvas中  
                    if (image != null) {  
                    	gc.drawImage(image, 0, 0,image.getImageData().width,image.getImageData().height,0,0,320,480);
                       // gc.drawImage(image, 0, 0);  
                    }  
                    //删除该元素  
                    imagesList.remove(size - 1);
                    setParams(0,0,0,0);
                    //System.out.println(imagesList.size());  
                } else {
                    canvasDrawImage(gc);  
                }
            }  
        }  
    }

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		shell = new Shell(parent);
		shell.open();
		shell.setSize(700, 600);
		shell.setText("设置比对信息");
		shell.setLayout(new GridLayout(2,false));
		
		Group group = new Group(shell, SWT.NONE);
		group.setText("设置比对信息");
		group.setLayoutData(new GridData(330, 490));
		group.setLayout(new GridLayout());
		
		final Canvas canvas = new Canvas(group, SWT.NONE);
		canvas.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
               // backOperatoin(e);  
                // canvas.redraw();  
            }  
            public void keyReleased(KeyEvent e) {  
                //backOperatoin(e);  
                // canvas.redraw();  
            }  
        }); 
		//GridData gd_canvas = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		canvas.setLayout(new GridLayout());
		canvas.setLayoutData(new GridData(320, 480));
		canvas.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
            	gc = new GC(canvas);
            	canvasDrawImage(gc);
            	
            	if(false == isEditable){
	            	Area area = null;
					try {
						System.out.println(file);
						area = loadScreenArea(file);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	            	if(area != null){
	            		startX = area.getX()*320/realWidth;
	            		startY = area.getY()*480/realHeight;
	            		endX = (area.getX()+area.getWidth())*320/realWidth;
	            		endY = (area.getY()+area.getHeight())*480/realHeight;
	            		drawRectangle(gc);
	            	}
            	}else{
            		drawRectangle(gc);
            	}
            }
		});
		
		canvas.addMouseListener(new MouseAdapter() {  
            @Override  
            public void mouseDown(MouseEvent e) {  
                startX = e.x;  
                startY = e.y;
                if(isEditable == true){
                	//repaint screen
                	canvasDrawImage(gc);
                }
            }  
  
            @Override  
            public void mouseUp(MouseEvent e) {  
                endX = e.x;  
                endY = e.y;
                if(isEditable == true)
                	drawRectangle(gc);
            }  
        });  
		
		canvas.redraw();
		Group group2 = new Group(shell, SWT.NONE);
		group2.setText("设置比对坐标");
		group2.setLayoutData(new GridData(330, 490));
		group2.setLayout(new GridLayout(2,false));
		//Label
		Label lbMatchPoint = new Label(group2, SWT.CENTER);
		lbMatchPoint.setText("     相似率(%)：      ");
		
		//Text
		tMatchPoint = new Text(group2, SWT.BORDER);
		GridData gd_tMatchPoint = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_tMatchPoint.heightHint = 15;
		gd_tMatchPoint.widthHint = 118;
		tMatchPoint.setLayoutData(gd_tMatchPoint);
		tMatchPoint.setText("100");
		
		Label lbArea = new Label(group2, SWT.CENTER);
		lbArea.setText("     图片比对区域(像素)：      ");
		new Label(group2, SWT.CENTER);
		
		//Left
		Label lbLeft = new Label(group2, SWT.CENTER);
		lbLeft.setText("               左边：");
		
		//Text
		tLeft = new Text(group2, SWT.BORDER);
		GridData gd_Left = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_Left.heightHint = 15;
		gd_Left.widthHint = 118;
		tLeft.setLayoutData(gd_Left);
		tLeft.setText("0");
		
		//Right
		Label lbRight = new Label(group2, SWT.CENTER);
		lbRight.setText("               顶部：");
		
		//Text
		tRight = new Text(group2, SWT.BORDER);
		GridData gd_Right = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_Right.heightHint = 15;
		gd_Right.widthHint = 118;
		tRight.setLayoutData(gd_Right);
		tRight.setText("0");
		
		//Width
		Label lbWidth = new Label(group2, SWT.CENTER);
		lbWidth.setText("               宽度：");
		
		//Text
		tlWidth = new Text(group2, SWT.BORDER);
		GridData gd_Width = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_Width.heightHint = 15;
		gd_Width.widthHint = 118;
		tlWidth.setLayoutData(gd_Width);
		tlWidth.setText("0");
		
		//Height
		Label lbHeight = new Label(group2, SWT.CENTER);
		lbHeight.setText("               高度：");
		
		//Text
		tlHeight = new Text(group2, SWT.BORDER);
		GridData gd_Height = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_Height.heightHint = 15;
		gd_Height.widthHint = 118;
		tlHeight.setLayoutData(gd_Height);
		tlHeight.setText("0");
		
		final Button btnEdit = new Button(group2,SWT.NONE);
		btnEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				isEditable = !isEditable;
				if(true == isEditable){
					canvasDrawImage(gc);
					btnEdit.setText("编辑完毕");
				}else{
					btnEdit.setText("编辑图片");
				}
					
			}
		});
		GridData gd_Edit = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_Edit.heightHint = 32;
		gd_Edit.widthHint = 96;
		btnEdit.setLayoutData(gd_Edit);
		btnEdit.setText("编辑图片");
		
		Button back = new Button(group2,SWT.NONE);
		back.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				isEditable = false;
				Area area = new Area();
				area.setFile(file);
				area.setX(Integer.parseInt(tLeft.getText().trim()));
				area.setY(Integer.parseInt(tRight.getText().trim()));
				
				area.setWidth(Integer.parseInt(tlWidth.getText().trim()));
				area.setHeight(Integer.parseInt(tlHeight.getText().trim()));
				try {
					updateScreenArea(file, area);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				btnEdit.setText("编辑图片");
			}
		});
		GridData gd_back = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_back.heightHint = 32;
		gd_back.widthHint = 96;
		back.setLayoutData(gd_back);
		back.setText("保存设置");
		
		shell.layout();
		while (shell!=null&&!shell.isDisposed()) {
			if (display!=null&&!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
}
