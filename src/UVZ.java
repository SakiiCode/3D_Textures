import java.awt.Point;

public class UVZ {
	double iz, uz, vz;
	public UVZ(double Zinv,double  uz,double  vz) {
		this.iz = Zinv;
		this.uz = uz;
		this.vz = vz;
	}
	
	public UVZ() {
		
	}
	
	// a h�rom v�ltoz�t t�vols�g alapj�n interpol�ljuk
	public static UVZ interp(Point p1, Point p2, Point pos, UVZ uvz1, UVZ uvz2) {
		UVZ result = new UVZ();
		double distanceratio = p1.distance(pos.x, pos.y) / p1.distance(p2.x, p2.y);
		result.iz = Util.interp(0, 1, distanceratio, uvz1.iz, uvz2.iz);
		result.uz = Util.interp(0, 1, distanceratio, uvz1.uz, uvz2.uz);
		result.vz = Util.interp(0, 1, distanceratio, uvz1.vz, uvz2.vz);
				
		return result;
	}

	@Override
	public String toString() {
		return "UVZ [invZ=" + iz + ", uz=" + uz + ", vz=" + vz + "]";
	}
	
	
}