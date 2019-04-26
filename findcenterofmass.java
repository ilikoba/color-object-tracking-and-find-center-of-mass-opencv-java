package newpackage;
import java.awt.AWTException;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

class findcenterofmass {
			private Robot mouse;
			findcenterofmass() throws AWTException{
				
				
			}
			public int getX(List<MatOfPoint> contours){
				List<Moments> mu = new ArrayList<Moments>(contours.size());
				int x=0;
		        for (int i = 0; i < contours.size(); i++) {
		            mu.add(i, Imgproc.moments(contours.get(i), false));
		            Moments p = mu.get(i);
		            x = (int) (p.get_m10() / p.get_m00());
		        }
		        return x;
			}
			public int getY(List<MatOfPoint> contours){
				List<Moments> mu = new ArrayList<Moments>(contours.size());
				int y=0;
		        for (int i = 0; i < contours.size(); i++) {
		            mu.add(i, Imgproc.moments(contours.get(i), false));
		            Moments p = mu.get(i);
		            y = (int) (p.get_m01() / p.get_m00());
		        }
		        return y;
			}
		    
			
			
		}