package dungeons.util;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;

/**
 * A Point is an immutable 3D coordinate representation. The comparison between Points using <code>compareTo()</code>
 * first compares y (front-back), than x (left-right) and lastly z (bottom-top).
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 */
public class Point implements Comparable<Point>{

	public static final Point ORIGIN = new Point(0,0,0);
	
	private int x;
	private int y;
	private int z;
	
	/**
	 * Construct a new point with the given coordinates.
	 * 
	 * @param x
	 * 		The left-right coordinate (left is smaller than right)		
	 * @param y
	 * 		The front-back coordinate (front is smaller than back)
	 * @param z
	 * 		The bottom-top coordinate (bottom is smaller than top)
	 * @post The x-coordinate of this point is the given x-value.
	 * 		| new.getX() = x
	 * @post The y-coordinate of this point is the given y-value.
	 * 		| new.getY() = y
	 * @post The z-coordinate of this point is the given z-value.
	 * 		| new.getZ() = z
	 */
	public Point(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Construct a new point that is the origin.
	 * 
	 * @effect Construct a new point with coordinate (0,0,0)
	 * 		| Point(0, 0, 0)
	 */
	public Point(){
		this(0,0,0);
	}
	
	/**
	 * Get the left-right(x) coordinate of this point (left < right).
	 */
	@Basic
	@Immutable
	public int getX(){
		return x;
	}
	
	/**
	 * Get the front-back(y) coordinate of this point (front < back).
	 */
	@Basic
	@Immutable
	public int getY(){
		return y;
	}
	
	/**
	 * Get the bottom-top(z) coordinate of this point (bottom < top).
	 */
	@Basic
	@Immutable
	public int getZ(){
		return z;
	}

	/**
	 * Add the dimensions of the given point to those of this point.
	 * 
	 * @param p
	 * 		The point to add
	 * @return A new Point of which the dimensions are equals to the dimension of this enlarged with the dimensions
	 * 		of the given point.
	 * 		| result == new Point(getX()+p.getX(), getY()+p.getY(), getZ()+p.getZ())
	 */
	public Point add(Point p){
		return new Point(getX()+p.getX(), getY()+p.getY(), getZ()+p.getZ());
	}
	
	/**
	 * Subtract the dimensions of the given point from those of this point.
	 * 
	 * @param p
	 * 		The point to subtract from this point
	 * @return A new Point of which the dimensions are equals to the dimension of this reduced by the dimensions
	 * 		of the given point.
	 * 		| result == new Point(getX()-p.getX(), getY()-p.getY(), getZ()-p.getZ())
	 */
	public Point subtract(Point p){
		return new Point(getX()-p.getX(), getY()-p.getY(), getZ()-p.getZ());
	}
	
	/**
	 * Check whether the coordinates of this point are all smaller than- or equal to the given value.
	 * 
	 * @param value
	 * 		The value to check against.
	 * @return True if all coordinates of this points are all smaller than- or equal to the given value.
	 * 		| result == getX() <= value
	 * 		|		&& getY() <= value
	 * 		|		&& getZ() <= value
	 */
	public boolean isEqualOrSmallerThanValue(int value){
		return x <= value && y <= value && z <= value;
	}
	
	/**
	 * Check whether the coordinates of this point are all bigger than- or equal to the given value.
	 * 
	 * @param value
	 * 		The value to check against
	 * @return True if all coordinates of this points are all bigger than- or equal to the given value.
	 * 		| result == getX() >= value
	 * 		|		&& getY() >= value
	 * 		|		&& getZ() >= value
	 */
	public boolean isEqualOrBiggerThanValue(int value){
		return x >= value && y >= value && z >= value;
	}
	
	/**
	 * Check whether the coordinates of this point are bigger than- or equal to the coordinates
	 * of the given point.
	 * 
	 * @param point
	 * 		The point to check against.
	 * @return True if all coordinates of this points are bigger than- or equal to the coordinates
	 * 		of the given point.
	 * 		| result == getX() >= other.getX()
	 * 		|		&& getY() >= other.getY()
	 * 		|		&& getZ() >= other.getZ()
	 */
	public boolean isEqualOrBiggerThanPoint(Point other){
		return x >= other.x && y >= other.y && z >= other.z;
	}
	
	/**
	 * Check whether the coordinates of this point are smaller than- or equal to the coordinates
	 * of the given point.
	 * 
	 * @param point
	 * 		The point to check against.
	 * @return True if all coordinates of this points are smaller than- or equal to the coordinates
	 * 		of the given point.
	 * 		| result == getX() <= other.getX()
	 * 		|		&& getY() <= other.getY()
	 * 		|		&& getZ() <= other.getZ()
	 */
	public boolean isEqualOrSmallerThanPoint(Point other){
		return x <= other.x && y <= other.y && z <= other.z;
	}
	
	/**
	 * Check whether this point lays between the given two values. 
	 * 
	 * @param min
	 * 		The minimum value each coordinate may have (inclusive)
	 * @param max
	 * 		The maximum value each coordinate max have (exclusive)
	 * @return True if all coordinates (independently) lay between the given two values
	 * 		|result == ( min <= getX() && max > getX()
	 *		|	&& min <= getY() && max > getY()
	 *		|	&& min <= getZ() && max > getZ() )
	 */
	public boolean between(int min, int max){
		return min <= getX() && max > getX()
				&& min <= getY() && max > getY()
				&& min <= getZ() && max > getZ();
	}
	
	/**
	 * Check whether all the coordinates of this point are smaller then their respective
	 * coordinate of the given point.
	 * 
	 * @param other
	 * 		The point to check against
	 * @return True if all the coordinates of this point are smaller then their respective
	 * coordinate of the given point.
	 * 		| result == (getX() < other.getX() && getY() < other.getY() && getZ() < other.getZ();
	 */
	public boolean isSmallerThan(Point other){
		return x < other.x && y < other.y && z < other.z;
	}

	
	/**
	 * Check whether two cubes overlap
	 * 
	 * @param ps
	 * 		The starting point of the first cube (inclusive)
	 * @param pe
	 * 		The ending point of the first cube (exclusive)
	 * @param qs
	 * 		The stating point of the second cube (inclusive)
	 * @param qe
	 * 		The ending point of the second cube (exclusive)
	 * @return True if the two cubes with the given dimensions overlap
	 * 		| result == pe.getX() > qs.getX() && ps.getX() < qe.getX())
	 *		|			&& (pe.getY() > qs.getY() && ps.getY() < qe.getY())
	 *		|			&& (pe.getZ() > qs.getZ() && ps.getZ() < qe.getZ()
	 */
	public static boolean overlap(Point ps, Point pe, Point qs, Point qe){     
		return (pe.getX() > qs.getX() && ps.getX() < qe.getX())
				&& (pe.getY() > qs.getY() && ps.getY() < qe.getY())
				&& (pe.getZ() > qs.getZ() && ps.getZ() < qe.getZ());
	}
	
	/**
	 * Check whether this point is the same as the given object.
	 * 
	 * @return True if the given object is an instance of <code>Point</code> and has the same
	 * 		x-, y- and z-coordinate.
	 * 		| result == other instanceof Point
	 * 					&& (getX() == ((Point)other).x)
	 * 					&& (getY() == ((Point)other).y)
	 * 					&& (getZ() == ((Point)other).z)
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof Point) {
			Point p = (Point) other;
			return (x == p.x) && (y == p.y) && (z == p.z);
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		int hash = 1;
		hash = hash * 17 + x;
        hash = hash * 31 + y;
        hash = hash * 13 + z;
		return hash;
	}

	/**
	 * Compare this point the the given other point.
	 * First the y-coordinates are compared. If those are equal the x coordinates are compared.
	 * Finally the z-coordinates are compared.
	 * 
	 * @return A positive, neutral or negative integer, if this point is greater, equal or smaller
	 * than the given point.
	 * @note front < back << left < right << bottom < top
	 */
	@Override
	public int compareTo(Point other) {
		if(other.z == z){
			if(other.x == x){
				if(other.y == y)
					return 0;
				if(other.y > y)
					return -1;
				return 1;
			}
			if(other.x > x)
				return -1;
			return 1;
		}
		if(other.z > z)
			return -1;
		return 1;
	}
	
	@Override
	public String toString(){
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
	
}
