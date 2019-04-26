package newpackage;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;  
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;  
import java.util.ArrayList;  
import java.util.List;  
import javax.swing.JFrame;  
import javax.swing.JPanel;  
import org.opencv.videoio.VideoCapture;
import org.opencv.core.Core;  
import org.opencv.core.Mat;   
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;  
import org.opencv.core.Size;  
import org.opencv.highgui.*;  
import org.opencv.imgproc.*;

import org.opencv.core.CvType;

public class maincode{  

	private static final char[] Point = null;

	public static void main(String arg[]){  
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);  
        try {
			trackobject();
		} catch (AWTException e) {
			
			e.printStackTrace();
		} 
		return;  
	}
	
	private static void trackobject() throws AWTException{
		
		JFrame frame1 = new JFrame("Camera");  
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame1.setSize(640,480);  
		frame1.setBounds(0, 0, frame1.getWidth(), frame1.getHeight());  
		Panel panel1 = new Panel();  
		frame1.setContentPane(panel1);  
		frame1.setVisible(true); 
		//HSV penceresi ayarlarý
		JFrame frame2 = new JFrame("HSV");  
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame2.setSize(640,480);  
		frame2.setBounds(300,100, frame2.getWidth()+0, 0+frame2.getHeight());  
		Panel panel2 = new Panel();  
		frame2.setContentPane(panel2);  
		frame2.setVisible(true);  
		//treshold penceresi ayarlarý
		JFrame frame4 = new JFrame("Threshold");  
		frame4.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame4.setSize(640,480);  
		frame4.setBounds(900,300, frame2.getWidth()+0, 0+frame2.getHeight());  
		Panel panel4 = new Panel();  
		frame4.setContentPane(panel4);      
		frame4.setVisible(true);  
	    VideoCapture capture =new VideoCapture(0); //webcam baþlýyor (çalýþýyor) harici webcam için 1
		capture.set(10, 0);
		capture.set(3, 1366);
	    capture.set(4, 768);
		capture.set(15, -2);
		Mat webcam_image=new Mat();  
		Mat hsv_image=new Mat();  
		Mat thresholded=new Mat();  
		Mat thresholded2=new Mat();  
		capture.read(webcam_image);  
		frame1.setSize(webcam_image.width()+40,webcam_image.height()+60);  
		frame2.setSize(webcam_image.width()+40,webcam_image.height()+60);  
		//frame3.setSize(webcam_image.width()+40,webcam_image.height()+60);  
		frame4.setSize(webcam_image.width()+40,webcam_image.height()+60);  
		Mat array255=new Mat(webcam_image.height(),webcam_image.width(),CvType.CV_8UC1);  
		array255.setTo(new Scalar(255)); 
	    Mat distance=new Mat(webcam_image.height(),webcam_image.width(),CvType.CV_8UC1);  
		List<Mat> lhsv = new ArrayList<Mat>(3);      
		Mat circles = new Mat(); 
		Scalar hsv_min = new Scalar(50, 50, 50, 0);  //takip edilecek rengin hsv formatýnda yüzdelikleri
	    Scalar hsv_max = new Scalar(70, 255, 255, 0);   
	
		double[] data=new double[3];  
		if( capture.isOpened())  
		{  
			while( true )  
			{  
				capture.read(webcam_image);  
				if( !webcam_image.empty() )  
				{  
					
					Imgproc.cvtColor(webcam_image, hsv_image, Imgproc.COLOR_BGR2HSV);  
					Core.inRange(hsv_image, hsv_min, hsv_max, thresholded);           
					Imgproc.erode(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8,8)));
					Imgproc.dilate(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8)));
				    Core.split(hsv_image, lhsv); 
					Mat S = lhsv.get(1);  
					Mat V = lhsv.get(2);  
					Core.subtract(array255, S, S);  
					Core.subtract(array255, V, V);  
					S.convertTo(S, CvType.CV_32F);  
					V.convertTo(V, CvType.CV_32F);  
					Core.magnitude(S, V, distance);  
					Core.inRange(distance,new Scalar(0.0), new Scalar(200.0), thresholded2);  
					Core.bitwise_and(thresholded, thresholded2, thresholded);  
					
					Imgproc.GaussianBlur(thresholded, thresholded, new Size(9,9),0,0);  
					List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
					Imgproc.HoughCircles(thresholded, circles, Imgproc.CV_HOUGH_GRADIENT, 2, thresholded.height()/8, 25, 25, 0, 0);   
					Imgproc.findContours(thresholded, contours, thresholded2, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
					Imgproc.drawContours(webcam_image, contours, -1, new Scalar(255, 0, 0), 2); 
					 
					Imgproc.line(webcam_image, new Point(150,50), new Point(202,200), new Scalar(100,10,10)/*CV_BGR(100,10,10)*/, 3);  
					Imgproc.circle(webcam_image, new Point(210,210), 10, new Scalar(100,10,10),3);  
					data=webcam_image.get(210, 210);  
					Imgproc.putText(webcam_image,String.format("("+String.valueOf(data[0])+","+String.valueOf(data[1])+","+String.valueOf(data[2])+")"),new Point(30, 30) , 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
,1.0,new Scalar(100,10,10,255),3); 
					Imgproc.line(webcam_image, new Point(0,384), new Point(1366,384),new Scalar(0,0,255),4);  
					Imgproc.line(hsv_image, new Point(0,384), new Point(1366,384),new Scalar(0,0,255),4);  
					Imgproc.line(webcam_image, new Point(400,0), new Point(400,1366),new Scalar(0,255,0),4);
		            
		            
				    System.out.println();
					int thickness = 2;
					int lineType = 8;
					Point start = new Point(0,0);
					Point end=new Point(0,0);
					Scalar black = new Scalar( 100, 10, 10 );
			        int rows = circles.rows();  
					int elemSize = (int)circles.elemSize();  
					float[] data2 = new float[rows * elemSize/4];  
					
					if (data2.length>0){  
                              circles.get(0, 0, data2); 
                                             
                              for(int i=0; i<data2.length; i=i+3) {  
                                Point center= new Point(data2[i], data2[i+1]);  
                                
                                try {
                                                int xCoord = (int) ((int) data2[i]);
                                                int yCoord = (int) ((int) data2[i+1]);
                                               
                                                Robot robot = new Robot();
                                              } catch (AWTException e) {
                                            }

                                    }
                            }  
					 

					List<Moments> mu = new ArrayList<Moments>(contours.size());
                            for (int i = 0; i < contours.size(); i++) {
                                mu.add(i, Imgproc.moments(contours.get(i), false));
                                Moments p = mu.get(i);
                                int x = (int) (p.get_m10() / p.get_m00());
                                int y = (int) (p.get_m01() / p.get_m00());
                                int xval = 400; //kameranýn pixel sayýsýnýn yarýsý (ortasýný bulmak için) 
                                int value = xval - x;
                                System.out. println("value:" + value);
                                if(value < 0)
                                {
                                	System.out.println("saða");
                                }
                                if(value > 0)
                                {
                                	System.out.println("sola");
                                }
                                if(value == 0)
                                {
                                	System.out.println("dur");
                                	
                                }
                                try {

                                    Robot robot = new Robot();
                                    
                                } catch (AWTException e) {
                                }
                                //cismin kütle merkezini circle ile belirleyecek kýsým
                                Imgproc.circle(webcam_image, new Point(x, y), 4, new Scalar(255,49,0,255), 4);
                                Imgproc.circle(hsv_image, new Point(x, y), 4, new Scalar(255,49,0,255), 4);
                                
                            }
				    Imgproc.circle(hsv_image, new Point(210,210), 10, new Scalar(100,10,10),3);  
					data=hsv_image.get(210, 210);  
					Imgproc.putText(hsv_image,String.format("("+String.valueOf(data[0])+","+String.valueOf(data[1])+","+String.valueOf(data[2])+")"),new Point(30, 30) , 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
							,1.0,new Scalar(100,10,10,255),3);  

					distance.convertTo(distance, CvType.CV_8UC1);  
					Imgproc.line(distance, new Point(150,50), new Point(202,200), new Scalar(100)/*CV_BGR(100,10,10)*/, 3);  
					data=(double[])distance.get(210, 210);  
				    Imgproc.putText(distance,String.format("("+String.valueOf(data[0])+")"),new Point(30, 30) , 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
							,1.0,new Scalar(100),3);   
					panel1.setimagewithMat(webcam_image);  
					panel2.setimagewithMat(hsv_image);  
					panel4.setimagewithMat(thresholded);  
					frame1.repaint();  
					frame2.repaint();  
					frame4.repaint();  
                }  
				else  
				{  
				   break;  
				}  
				
				
			
			}  
		}  
	

	


	}
} 