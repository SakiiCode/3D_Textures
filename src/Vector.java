

public class Vector {
	public float x, y, z;
	
	public Vector(){
		this.x = 0;
		this.y = 0;
		this.z = 0;
		//this.length = 0;
	}
	
	public Vector(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
		//length=(float) Math.sqrt(x*x+y*y+z*z);
	}
	
	public Vector(int[] pos) {
		this.x = pos[0];
		this.y = pos[1];
		this.z = pos[2];
		//length=(float) Math.sqrt(x*x+y*y+z*z);
	}
	
	public Vector(float[] pos) {
		this.x = pos[0];
		this.y = pos[1];
		this.z = pos[2];
		//length=(float) Math.sqrt(x*x+y*y+z*z);
	}
	
	public Vector(float[] xy, float z) {
		this.x = xy[0];
		this.y = xy[1];
		this.z = z;
		//length=(float) Math.sqrt(x*x+y*y+z*z);
	}
	
	public Vector(byte[] pos) {
		this.x = pos[0];
		this.y = pos[1];
		this.z = pos[2];
		//length=(float) Math.sqrt(x*x+y*y+z*z);
	}
	
	public Vector normalize(){
		float length = (float) Math.sqrt(x*x+y*y+z*z);

		if(length != 0){
			x=x/length;
			y=y/length;
			z=z/length;
		}
		
		length = (float) Math.sqrt(x*x+y*y+z*z);

		return this;
	}
	
	
	public Vector CrossProduct(Vector V)
	{
		Vector CrossVector = new Vector(
				y * V.z - z * V.y,
				z * V.x - x * V.z,
				x * V.y - y * V.x);
		CrossVector.normalize();
		return CrossVector;
		
	}
	
	
	public float DotProduct(float x, float y, float z){
		return this.x*x+this.y*y+this.z*z;
	}
	
	public float DotProduct(Vector V){
		return x*V.x+y*V.y+z*V.z;
	}
	
	public float getLength(){
		float length=(float) Math.sqrt(x*x+y*y+z*z);
		return length;
	}
	
	@Override
	public String toString(){
		return "Vector[ x: "+x+", y: " + y + ", z: " + z+"]";
		
	}
	
	public Vector multiply(float value){
		return new Vector(x*value,y*value,z*value);
	}
	
	public Vector add(Vector V){
		return new Vector(x+V.x,y+V.y,z+V.z);
	}
	
	public Vector add(float x2, float y2, float z2){
		return new Vector(x+x2,y+y2,z+z2);
	}
	

	
	public Vector opposite(){
		return new Vector(-x, -y, -z);
	}
	

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Vector))
			return false;
		Vector other = (Vector) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		return true;
	}

	public Vector set(float x, float y, float z){
		this.x=x;
		this.y=y;
		this.z=z;
		return this;
	}
	
	public Vector set(Vector V){
		this.x=V.x;
		this.y=V.y;
		this.z=V.z;
		return this;
	}
	public Vector set(float[] pos){
		this.x=pos[0];
		this.y=pos[1];
		this.z=pos[2];
		return this;
	}
	
	public float distance(Vector V) {
		Vector sub = V.multiply(-1).add(this);
		return sub.getLength();
	}
	
}
