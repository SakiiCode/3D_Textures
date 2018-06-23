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
		
		//a rajzot a képernyõn belül kell tartani, kívülre nem rajzolhatunk
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
		

		//vertexrõl vertexre körbemegyünk
		for(int i=0;i<vertices.size();i++) {
			
			Vertex v1 = vertices.get(i); 
			Vertex v2 = i != vertices.size()-1 ? vertices.get(i+1) : vertices.get(0);
			
			

			Point p1 = v1.proj;
			Point p2 = v2.proj;
			
			//p1 és p2 meredeksége
			double m=(p2.y==p1.y) ? 0.0 : (p2.x-p1.x)*1.0/(p2.y-p1.y);
			
			// mindenképpen y pozitív irányban szeretnénk végigmenni a polygon oldalán, de lehet, hogy p2 van feljebb.
			boolean reverse = (p1.y<p2.y);
			int xmin = reverse ? p1.x : p2.x;
			int ymin= reverse ? p1.y : p2.y; // y határértékei adott szakaszon
			int ymax = reverse ? p2.y : p1.y;
			
			/*ymin=Math.max(ymin, 0);
			ymax=Math.min(ymax, Main.screenHeight);
			xmin=Math.max(xmin, 0);*/
			
			double x  = xmin; //aktuális x érték

			
			// megkeressük az adott sor bal és jobb szélét, társítunk a két ponthoz UVZ-t is
			// y-t 1-gyel, x-et m-mel léptetjük
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
		
		// a befoglaló téglalap méretei
		//int imgx = Collections.min(bufferXmin.values());
		int imgw = Collections.max(bufferXmax.values()) -  Collections.min(bufferXmin.values());
		//int imgy = ymin;
		//int imgh = ymax-ymin;

		
		//g.setColor(Color.BLACK);
		if(imgw>0) { //ha merõlegesen állunk ne rajzoljon
			
			// átmeneti kép, erre rajzoljuk a polygont, és ezt rajzoljuk az ablakra
			//BufferedImage img = new BufferedImage(imgw, imgh, BufferedImage.TYPE_INT_ARGB);
			
			//a polygon minden során végigmegyünk
			for(int y=ymin;y<ymax;y++)
			{
				//a polygon adott sorának valódi széleinek adatai
				int xmin=bufferXmin.get(y);
				int xmax=bufferXmax.get(y);
				
				UVZ uvzmin = bufferUVZmin.get(y);
				UVZ uvzmax = bufferUVZmax.get(y);
			 
				// eltároljuk 1/z, u/z, v/z "meredekségét", hogy ne kelljen minden pixelnél újra kiszámolni
				double Siz = Util.getSlope(xmin, xmax, uvzmin.iz, uvzmax.iz);
				double Suz = Util.getSlope(xmin, xmax, uvzmin.uz, uvzmax.uz);
				double Svz = Util.getSlope(xmin, xmax, uvzmin.vz, uvzmax.vz);
				
				//egy sorban csak a szükséges pixeleket érintjük, nem rajzolunk a képernyõn kívülre
				int xmin2=Math.max(xmin, 0);
				int xmax2=Math.min(xmax, Main.screenWidth);
				for(int x=xmin2;x<xmax2;x++)
				{
				 
				 	//ez a két sor nem ide tartozik, csak egy példa, ha esetleg vízszintesen szeretnél egy képet torzítani
				 	//double u=interp(xmin, xmax, nx, 0, texture.getWidth());
				 	//double v=interp(ymin, ymax, y, 0, texture.getHeight());
					
					
					
				 
					//a sor két szélérõl befele interpoláljuk 1/z,u/z,v/z változókat
					double iz=Util.interpSlope(xmin, x, uvzmin.iz, Siz);
				 	double uz=Util.interpSlope(xmin, x, uvzmin.uz, Suz);
				 	double vz=Util.interpSlope(xmin, x, uvzmin.vz, Svz);
				 	
				 	// a mögöttünk lévõ tárgyak levágását jobban kell megoldani, de a hiba elkerüléséhez ez is elég
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
