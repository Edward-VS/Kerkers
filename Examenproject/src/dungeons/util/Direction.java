package dungeons.util;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import dungeons.util.Point;

/**
 * Enumeration of directions.
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 *
 */
public enum Direction {
	NORTH(new Point(0,1,0)), WEST(new Point(-1,0,0)), SOUTH(new Point(0,-1,0)), EAST(new Point(1,0,0)), UP(new Point(0,0,1)), DOWN(new Point(0,0,-1));
	
	/**
	 * Construct a new direction with the given position relative to the origin in which the
	 * direction should point.
	 * 
	 * @param x
	 * 		The position relative to the origin in which this direction should point.
	 * @post The relative position associated with this direction is the given relative position
	 * 		| new.getRelativePosition() == relPosiiton
	 */
	private Direction(Point relPosition){
		this.relPosition = relPosition;
	}
	
	/**
	 * Get the position relative to the origin in which this direction points.
	 */
	@Basic @Immutable
	public Point getRelativePosition(){
		return relPosition;
	}
	
	/**
	 * Get the position in this direction of the given position.
	 * 
	 * @param position
	 * 		A position
	 * @return The relative position associated with this direction added to the given position
	 * 		| result == position.add(getRelativePosition())
	 */
	public Point getPointInThisDirectionOf(Point position){
		return position.add(relPosition);
	}
	
	/**
	 * Get the inverse of this direction.
	 * 
	 * @return The opposite direction of the given direction.
	 * 		| this == Direction.NORTH => result == Direction.SOUTH
	 *		| this == Direction.SOUTH => result == Direction.NORTH
	 *		| this == Direction.EAST => result == Direction.WEST
	 *		| this == Direction.WEST => result == Direction.EAST
	 *		| this == Direction.UP => result == Direction.DOWN
	 *		| this == Direction.DOWN => result == Direction.UP
	 */
	@Immutable
	public Direction oppositeDirection(){
		switch(this){
		case DOWN:
			return UP;
		case EAST:
			return WEST;
		case NORTH:
			return SOUTH;
		case SOUTH:
			return NORTH;
		case UP:
			return DOWN;
		case WEST:
			return EAST;
		}
		assert false;
		return null; // will never happen
	}
	
	/**
	 * The position relative to the origin associated with this direction
	 * @invar The norm of this position is maximum one.
	 * 		| relPosition.norm() <= 1
	 */
	private Point relPosition;
	
}
