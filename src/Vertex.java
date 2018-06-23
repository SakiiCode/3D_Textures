import java.awt.Point;
import java.awt.geom.Point2D;



public class Vertex {
	Vector pos;
	UVZ uvz = new UVZ();
	int u, v;
	Point proj;
	
	
	public Vertex(float x, float y, float z, int u, int v) {
		this.pos=new Vector(x, y,z);
		this.u=u;
		this.v = v;
		proj = new Point();
		update();
	}
	
	public Vertex(Vector vec, int u, int v) {
		this.pos=new Vector(vec.x, vec.y,vec.z); // a biztonság kedvéért objektumot másolunk
		this.u=u;
		this.v = v;
		proj = new Point();
		update();
	}
	
	public void update() {
		// 1/z , u/z , v/z kiszámítása
		// z nem a kamera és a pont távolsága, hanem a kamera helyének, és a pontnak a kamera irányára vetített helyének távolsága
		// (egyszerû skalárszorzat)
		Vector ViewToPoint = pos.add(Main.CameraPos.multiply(-1)); 
		double z = ViewToPoint.DotProduct(Main.CameraAim);
		uvz.iz=1/z;
		uvz.uz=u/z;
		uvz.vz=v/z;
		
		// 2d koordináták kiszámítása
		Point2D spec = Vertex.convert3Dto2D(pos.x, pos.y, pos.z);
		proj = new Point((int)spec.getX(), (int)spec.getY());
		
		
	}
	
	
	// ezzel a két rettentõ bonyolult függvénnyel 3d koordinátákból 2d-t kapunk
	// nincs benne a nem látható tárgyak levágása!
	// dx, dy-t elég framenként egyszer kiszámolni, de az egyszerûség kedvéért itt hagytam
	public static Point2D convert3Dto2D(float x, float y, float z) {
		
		Vector v = new Vector(x, y, z);
		
		
		float centerX = Main.Frame.getWidth() / 2f;
		float centerY = Main.Frame.getHeight() / 2f;
		int zoom = 500;
		
		
	
		
		Point2D.Float P1 =  P(v);
		
		Vector ViewTo = Main.CameraPos.add(Main.CameraAim);
		
		Point2D.Float P2 = P(ViewTo);
	
		float dx = - zoom * P2.x + centerX;
		float dy = - zoom * P2.y + centerY;
		
		float x2d = dx + zoom * P1.x;
		float y2d = dy + zoom * P1.y;
	
		P1.setLocation(x2d, y2d);
		return P1;
	
	}

	public static Point2D.Float P(Vector v) {
		Vector ViewToPoint = v.add(Main.CameraPos.multiply(-1));
	
		float t = Main.CameraAim.DotProduct(ViewToPoint);
	
		Vector proj = Main.CameraPos.add(ViewToPoint.multiply(1/t));
	
		Vector RightViewVector = Main.CameraAim.CrossProduct(new Vector(0, 0, 1));
		Vector BottomViewVector = RightViewVector.CrossProduct(Main.CameraAim).multiply(-1);
		return new Point2D.Float(RightViewVector.DotProduct(proj), BottomViewVector.DotProduct(proj));
	}
	

}
