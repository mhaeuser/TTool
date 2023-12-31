package project.alwaystry;

import android.graphics.*;
import android.view.View;

import java.util.LinkedList;

public class AvatarBDCompositionConnectorAndroid extends TGConnectorAndroid{

	private Paint paint;
	private LinkedList internalpoints;
	private int D = 26;
	private int d = 20;
	
	public AvatarBDCompositionConnectorAndroid(int _minWidth, int _minHeight,int _maxWidth,int _maxHeight,TGConnectingPointAndroid p1,TGConnectingPointAndroid p2,View panel){
		super(_minWidth, _minHeight, _maxWidth, _maxHeight, p1, p2, panel);
		
		type = TGComponentAndroid.AVATARBD_COMPOSITION_CONNECTOR;
		
		paint = new Paint();
		paint.setStrokeWidth(2);
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
	}
	
	
	public void internalDrawing(Canvas canvas){
		
		if(p1.isFree() || p2.isFree()){
			p1.setState(TGConnectingPointAndroid.NORMAL);
			p2.setState(TGConnectingPointAndroid.NORMAL);
			((AvatarBDPanelAndroid)panel).getCompolist().remove(this);
			return;
		}
			
		if(selected){
			paint.setColor(Color.RED);
		}else{
			paint.setColor(Color.BLACK);
		}
		
		if(movingHead){
			paint.setColor(Color.MAGENTA);
		}
		
		int dd = d;
		int DD = D;
		
		int x1 =p1.getX();
		int y1 =p1.getY();
		int x2 =p2.getX();
		int y2 = p2.getY();
		
		canvas.drawLine(x1, y1, x2, y2, paint);
		double xd[] = new double[4];
		double yd[] = new double[4];
		
		if(x1 == x2){
			if (y1 > y2) {
				xd[0]=x2; yd[0]=y2+DD;
				xd[1]=x2+(dd/2); yd[1]=y2+(DD/2);
				xd[2]=x2; yd[2]=y2;
				xd[3]=x2-(dd/2); yd[3]=y2+(DD/2);
			}else{
				xd[0]=x2; yd[0]=y2-DD;
				xd[1]=x2+(dd/2); yd[1]=y2-(DD/2);
				xd[2]=x2; yd[2]=y2;
				xd[3]=x2-(dd/2); yd[3]=y2-(DD/2);
			}
		}else{
			xd[0] = x2;
			yd[0] = y2;
			
			int x0 = x1 - x2;
			int y0 = y1 - y2;
			double k = 1/(Math.sqrt((x0*x0)+(y0*y0)));
			double u = x0*k;
			double v = y0*k;
			
			double Ex = DD*u;
			double Ey = DD*v;
			double Fx = dd*v;
			double Fy = -dd*u;
			
			//Q
			xd[1] = x2+((Ex+Fx)/2);
			yd[1] = y2+((Ey+Fy)/2);
			
			//R
			xd[2] = x2+Ex;
			yd[2] = y2+Ey;
			
			//S
			xd[3] = xd[1] - Fx;
			yd[3] = yd[1] - Fy;
		}
		
		Path path = new Path();
		path.moveTo((float)xd[0], (float)yd[0]);
		path.lineTo((float)xd[1], (float)yd[1]);
		path.lineTo((float)xd[2], (float)yd[2]);
		path.lineTo((float)xd[3], (float)yd[3]);
		path.lineTo((float)xd[0], (float)yd[0]);
		
		paint.setStyle(Paint.Style.FILL);
		
		canvas.drawPath(path, paint);

	}


	public LinkedList getInternalpoints() {
		return internalpoints;
	}

	public void setInternalpoints(LinkedList internalpoints) {
		this.internalpoints = internalpoints;
	}
	
	protected void addInternalPoint(Point point){
		internalpoints.add(point);
	}

	protected boolean editOndoubleClick(int _x, int _y) {
		return false;
	}
}
