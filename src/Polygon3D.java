import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Polygon3D {
	ArrayList<Vertex> vertices = new ArrayList<>(4);
	BufferedImage texture;
	
	HashMap<Integer, Integer> bufferXmin = new HashMap<>(Main.screenHeight);
	HashMap<Integer, Integer> bufferXmax = new HashMap<>(Main.screenHeight);
	
	HashMap<Integer, UVZ> bufferUVZmin = new HashMap<>(Main.screenHeight);
	HashMap<Integer, UVZ> bufferUVZmax = new HashMap<>(Main.screenHeight);
	
	int ymax, ymin;
	
	public Polygon3D(ArrayList<Vertex> vertices, BufferedImage texture) {
		
		this.vertices.addAll(vertices);

		this.texture = texture;
		
	}
	
	public void update() {
		for(Vertex v : vertices) {
			v.update();
		}
		
		ymin = vertices.get(0).proj.y;
		ymax = vertices.get(0).proj.y;
		for(Vertex v : vertices) {
			ymin = Math.min(ymin,v.proj.y);
			ymax = Math.max(ymax,v.proj.y);
		}
		
		//a rajzot a k�perny�n bel�l kell tartani, k�v�lre nem rajzolhatunk
		ymin=Math.max(ymin, 0);
		ymax=Math.max(ymax, 0);
		ymin=Math.min(ymin, Main.FrameBuffer.getHeight());
		ymax=Math.min(ymax, Main.FrameBuffer.getHeight());
		
	}
	
	public void draw(BufferedImage FrameBuffer) {
		
		
		// buffer init
		bufferXmin.clear();
		bufferXmax.clear();
		bufferUVZmin.clear();
		bufferUVZmax.clear();
		

		//vertexr�l vertexre k�rbemegy�nk
		for(int i=0;i<vertices.size();i++) {
			
			Vertex v1 = vertices.get(i); 
			Vertex v2 = i != vertices.size()-1 ? vertices.get(i+1) : vertices.get(0);
			
			

			Point p1 = v1.proj;
			Point p2 = v2.proj;
			
			//p1 �s p2 meredeks�ge
			double m=(p2.y==p1.y) ? 0.0 : (p2.x-p1.x)*1.0/(p2.y-p1.y);
			
			// mindenk�ppen y pozit�v ir�nyban szeretn�nk v�gigmenni a polygon oldal�n, de lehet, hogy p2 van feljebb.
			boolean reverse = (p1.y<p2.y);
			int xmin = reverse ? p1.x : p2.x;
			int ymin= reverse ? p1.y : p2.y; // y hat�r�rt�kei adott szakaszon
			int ymax = reverse ? p2.y : p1.y;
			
			/*ymin=Math.max(ymin, 0);
			ymax=Math.min(ymax, Main.screenHeight);
			xmin=Math.max(xmin, 0);*/
			
			double x  = xmin; //aktu�lis x �rt�k

			
			// megkeress�k az adott sor bal �s jobb sz�l�t, t�rs�tunk a k�t ponthoz UVZ-t is
			// y-t 1-gyel, x-et m-mel l�ptetj�k
			for(int y=ymin;y<ymax;y++) {

				if(bufferXmin.get(y) == null || x < bufferXmin.get(y)) {
					bufferXmin.put(y,(int) x);
					bufferUVZmin.put(y, UVZ.interp(p1, p2, new Point((int) x, y), v1.uvz, v2.uvz));
				}
				
				if(bufferXmax.get(y) == null || x > bufferXmax.get(y)) {
					bufferXmax.put(y,(int) x);
					bufferUVZmax.put(y, UVZ.interp(p1, p2, new Point((int) x, y), v1.uvz, v2.uvz));
				}
				
				x+=m;
				
			}

				
		}
		
		// a befoglal� t�glalap m�retei
		//int imgx = Collections.min(bufferXmin.values());
		int imgw = Collections.max(bufferXmax.values()) -  Collections.min(bufferXmin.values());
		//int imgy = ymin;
		//int imgh = ymax-ymin;

		
		//g.setColor(Color.BLACK);
		if(imgw>0) { //ha mer�legesen �llunk ne rajzoljon
			
			// �tmeneti k�p, erre rajzoljuk a polygont, �s ezt rajzoljuk az ablakra
			//BufferedImage img = new BufferedImage(imgw, imgh, BufferedImage.TYPE_INT_ARGB);
			
			//a polygon minden sor�n v�gigmegy�nk
			for(int y=ymin;y<ymax;y++)
			{
				//a polygon adott sor�nak val�di sz�leinek adatai
				int xmin=bufferXmin.get(y);
				int xmax=bufferXmax.get(y);
				
				UVZ uvzmin = bufferUVZmin.get(y);
				UVZ uvzmax = bufferUVZmax.get(y);
			 
				// elt�roljuk 1/z, u/z, v/z "meredeks�g�t", hogy ne kelljen minden pixeln�l �jra kisz�molni
				double Siz = Util.getSlope(xmin, xmax, uvzmin.iz, uvzmax.iz);
				double Suz = Util.getSlope(xmin, xmax, uvzmin.uz, uvzmax.uz);
				double Svz = Util.getSlope(xmin, xmax, uvzmin.vz, uvzmax.vz);
				
				//egy sorban csak a sz�ks�ges pixeleket �rintj�k, nem rajzolunk a k�perny�n k�v�lre
				int xmin2=Math.max(xmin, 0);
				int xmax2=Math.min(xmax, Main.screenWidth);
				for(int x=xmin2;x<xmax2;x++)
				{
				 
				 	//ez a k�t sor nem ide tartozik, csak egy p�lda, ha esetleg v�zszintesen szeretn�l egy k�pet torz�tani
				 	//double u=interp(xmin, xmax, nx, 0, texture.getWidth());
				 	//double v=interp(ymin, ymax, y, 0, texture.getHeight());
					
					
					
				 
					//a sor k�t sz�l�r�l befele interpol�ljuk 1/z,u/z,v/z v�ltoz�kat
					double iz=Util.interpSlope(xmin, x, uvzmin.iz, Siz);
				 	double uz=Util.interpSlope(xmin, x, uvzmin.uz, Suz);
				 	double vz=Util.interpSlope(xmin, x, uvzmin.vz, Svz);
				 	
				 	// a m�g�tt�nk l�v� t�rgyak lev�g�s�t jobban kell megoldani, de a hiba elker�l�s�hez ez is el�g
				 	if(iz<0) return;
				 	
				 	
				 	//   U=(U/Z)/(1/Z)   V=(V/Z)/(1/Z)  
				 	double u=uz/iz;
				 	double v=vz/iz;
				 	
				 		FrameBuffer.setRGB(x, y, texture.getRGB((int)u, (int)v));
				}
			}
		}
		
	}
	

	
}
