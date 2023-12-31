package myutilandroid;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.FloatMath;


public class GraphicLibAndroid {
	
	public static void dashedRect(Canvas canvas, int x1, int y1, int width, int height) {
        
    }

	 public static boolean isInRectangle(int x1, int y1, int x, int y, int width, int height) {
        if ((x1 >= x) && ((x + width) >= x1) && (y1 >= y) && ((y + height) >= y1)) {
            return true;
        }
        return false;
    }
	 
	 public static void doubleColorRect(Canvas c, int x, int y, int endx, int endy, int color1, int color2) {
	        Paint p = new Paint();
	        p.setAntiAlias(true);
	        
		 	p.setColor(color1);
	        c.drawLine(x, y, endx, y,p);
	        c.drawLine(x, y, x, endy,p);
	        p.setColor(color2);
	        c.drawLine(x, endy, endx, endy,p);
	        c.drawLine(endx, y, endx, endy,p);
	    }
	 
	 public static float distanceBetweenTwoP(float x1,float y1, float x2, float y2){
		 float dx = x1- x2;
		 float dy = y1 - y2;
		 
		 return FloatMath.sqrt(dx*dx+dy*dy);
	 }
}
