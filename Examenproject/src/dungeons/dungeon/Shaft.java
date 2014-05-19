package dungeons.dungeon;

import be.kuleuven.cs.som.annotate.Raw;
import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.square.Square;
import dungeons.util.Direction;
import dungeons.util.Point;

/**
 * <p>A Shaft is a SquareDungeon that can only contain squares in one specific direction. All maximum dimensions are 1, except for 
 * the dimension that point in the direction of the Shaft. This restriction implies that the maximum dimensions of a Shaft must comply
 * with more conditions compared to a SquareDungeon. The direction of a shaft is only immutable if the shaft has maximum dimensions
 * larger than (1,1,1).</p>
 * 
 * <p>A Shaft can not contain Rocks as its squares, and obstacles in the direction of the shaft may not be Walls (they can be Doors).
 * Shafts are filled entirely with Squares.</p>
 * 
 * <p>A Shaft offers a method for extending it. This method will throw an exception if extension is not possible given the parent of the
 * composite dungeon (amongst other reasons).</p>
 * 
 * @see Documentation of SquareDungeon is also valid.
 * 
 * @invar A Shaft must always have maximum dimensions that are smaller than  or equal to the maximum allowed dimensions,
 * 		bigger than (0,0,0), in compliance with the dimensions of its parent dungeon (if any) and legal in any other way.
 * 		In addition, a Shaft must always have maximum dimensions which can be parsed into a direction.
 *		| hasProperMaximumDimensions()
 * @invar All the squares in a Shaft must be in a legal state. They may not be rocks, and may not contain walls in the direction
 * 		of the shaft except at the ends. All positions in a shaft must contain a square if the shaft in not terminated.
 * 		| hasProperSquares()
 * @invar The parent of a Shaft (if any) must be in a legal state, and the association between its parent dungeon must be
 * 		properly set. 
 * 		| hasProperParentDungeon()
 *
 * @author Edward Van Sieleghem & Christof Vermeersch
 */
public class Shaft extends SquareDungeon{
	
	/**
	 * Construct a new dungeon with the given maximum dimensions.
	 * 
	 * @param maximumDimensions
	 *		The dimensions outside of which no squares can be added.
	 * @post The maximum dimensions of this dungeon are set to the given maximum dimensions
	 * 		| new.getMaximumDimensions() == maximumDimensions
	 * @post The shaft has a normal square at all positions
	 * 		| for each position in Point.ORIGIN..maximumDimensions.subtract(Point.CUBE):
	 * 		|	getSquareAt(position) != null
	 * @throws IllegalMaximumDimensionsException(maximumDimensions)
	 * 		[MUST] The given maximum dimensions are not legal
	 * 		| !canHaveAsMaximumDimensions(maximumDimensions)
	 */
	public Shaft(Point maximumDimensions) throws IllegalMaximumDimensionsException {
		super(maximumDimensions);
		
		Point pos = Point.ORIGIN;
		while(pos.isSmallerThan(getMaximumDimensions())){
			// add square and try to remove wall (the later will fail if first square in line)
			addAsSquareAt(new Square(), pos, getDirection().oppositeDirection());
			pos = getDirection().getPointInThisDirectionOf(pos);
		}
		
		assert hasProperSquares();
	}
	
	/**
	 * Construct a new shaft with maximum dimensions (1,1,1).
	 * 
	 * @effect Construct a new dungeon with dimensions (1,1,1).
	 * 		| this(Point.CUBE)
	 * @note The direction is the default direction, but enlarging the dungeon (by setting new maximum dimensions
	 * 		can reset that direction. (If maximum dimensions are not (1,1,1) that direction is immutable)
	 */
	public Shaft() throws IllegalMaximumDimensionsException {
		this(Point.CUBE);
	}
	
	
	
	/****************************************************
	 * DIMENSIONS
	 ****************************************************/
	
	/**
	 * Get the direction of this shaft.
	 * 
	 * @return The direction that is parsed from the maximum dimensions of this dungeon.
	 * 		| result == parseDirectionFromMaximumDimensions(getMaximumDimensions())
	 */
	public Direction getDirection(){
		return parseDirectionFromMaximumDimensions(getMaximumDimensions());
	}
	
	/**
	 * Check whether the given maximum dimensions are valid for this dungeon.
	 * 
	 * @param maximumDimensions
	 *		A point that represents the maximum dimensions.
	 * @return If the given maximum dimensions are not legal in a dungeon of the super-type of this dungeon,
	 * 		then false.
	 * 		| if !super.canHaveAsMaximumDimensions(maximumDimensions)
	 * 		|	then result == false
	 * @return Else if the given maximum dimensions can not be parsed into a direction then false.
	 * 		| else if parseDirectionFromMaximumDimensions(maximumDimensions) == null
	 * 		|	then result == false
	 * @return Else true.
	 * 		| else result == true
	 */
	@Raw
	public boolean canHaveAsMaximumDimensions(Point maximumDimensions) {
		if(!super.canHaveAsMaximumDimensions(maximumDimensions))
			return false;
		if(parseDirectionFromMaximumDimensions(maximumDimensions) == null)
			return false;
		return true;
	}
	
	/**
	 * Parse a direction from the given maximumDimensions.
	 * 
	 * @param maximumDimensions
	 * 		The dimensions to check
	 * @return If the x coordinate is bigger than 1, then EAST if all other coordinates are one
	 *		| if maximumDimensions.getX() > 1 && maximumDimensions.getY() == 1 && maximumDimensions.getZ() == 1
	 *		|	then result == Direction.EAST
	 * @return Else if the y coordinate is bigger than 1, then NORTH if all other coordinates are one
	 *		| else if maximumDimensions.getY() > 1 && maximumDimensions.getX() == 1 && maximumDimensions.getZ() == 1
	 *		|	then result == Direction.NORTH
	 * @return Else if the z coordinate is bigger than 1, then UP if all other coordinates are one
	 *		| else if maximumDimensions.getZ() > 1 && maximumDimensions.getX() == 1 && maximumDimensions.getY() == 1
	 *		|	then result == Direction.UP
	 * @return Else if all coordinates are 1, then the default direction
	 * 		| else if maximumDimensions.getZ() == 1 && maximumDimensions.getX() == 1 && maximumDimensions.getY() == 1
	 *		|	then result == DEFAULT_DIRECTION
	 * @return Else null
	 * 		| else result == null
	 */
	public static Direction parseDirectionFromMaximumDimensions(Point maximumDimensions){
		if(maximumDimensions.getX() > 1 && maximumDimensions.getY() == 1 && maximumDimensions.getZ() == 1)
			return Direction.EAST;
		if(maximumDimensions.getY() > 1 && maximumDimensions.getX() == 1 && maximumDimensions.getZ() == 1)
			return Direction.NORTH;
		if(maximumDimensions.getZ() > 1 && maximumDimensions.getX() == 1 && maximumDimensions.getY() == 1)
			return Direction.UP;
		if(maximumDimensions.getZ() == 1 && maximumDimensions.getX() == 1 && maximumDimensions.getY() == 1)
			return DEFAULT_DIRECTION;
		return null;
	}
	
	/**
	 * Set the new maximum dimensions of this dungeon to the given maximum dimensions, and fill the new
	 * position with new squares.
	 * 
	 * @param maximumDimensions
	 *		The new maximum dimensions for this dungeon
	 * @post The maximum dimensions of this dungeon are the given maximum dimensions.
	 *		| new.getmaximumDimensions() == maximumDimensions
	 * @post The shaft has a normal square at all positions
	 * 		| for each position in Point.ORIGIN..maximumDimensions.subtract(Point.CUBE):
	 * 		|	getSquareAt(position) != null
	 * @throws IllegalMaximumDimensionsException(maximumDimensions)
	 *		[MUST] The given new maximum dimensions can not be used as new maximum dimensions for this dungeon.
	 *		| !canAcceptAsNewMaximumDimensions(maximumDimensions)
	 */
	public void changeMaximumDimensions(Point maximumDimensions) throws IllegalMaximumDimensionsException {
		Point curMax = getMaximumDimensions();
		super.changeMaximumDimensions(maximumDimensions);
		// direction might have changed now: we use getDirection.
		Point pos = getDirection().getPointInThisDirectionOf(curMax.subtract(Point.CUBE));
		while(pos.isSmallerThan(getMaximumDimensions())){
			// add square and try to remove wall (will fail if first square in line)
			addAsSquareAt(new Square(), pos, getDirection().oppositeDirection());
			pos = getDirection().getPointInThisDirectionOf(pos);
		}
	}
	
	/**
	 * Extend this shaft with the given amount in the current direction.
	 * 
	 * @param amount
	 * 		The amount to extend this shaft with
	 * @pre The given amount is larger than or equal to zero
	 * 		| amount >= 0
	 * @effect The dimensions of this shaft are enlarged with the given amount in the direction of this shaft.
	 * 		| if getDirection() == Direction.EAST
	 * 		|	then changeMaximumDimensions(getMaximumDimensions().add(new Point(amount, 0, 0)))
	 * 		| else if getDirection() == Direction.NORTH
	 * 		|	then changeMaximumDimensions(getMaximumDimensions().add(new Point(0, amount, 0)))
	 * 		| else if getDirection() == Direction.UP
	 * 		|	then changeMaximumDimensions(getMaximumDimensions().add(new Point(0, 0, amount)))
	 * @throws IllegalMaximumDimensionsException [MUST]
	 * 		The new maximum dimensions are not legal illegal
	 * 		See effect clause
	 */
	public void extend(int amount) throws IllegalMaximumDimensionsException{
		Direction direction = getDirection();
		if(direction == Direction.EAST){
			changeMaximumDimensions(getMaximumDimensions().add(new Point(amount, 0, 0)));
		}else if(direction == Direction.NORTH){
			changeMaximumDimensions(getMaximumDimensions().add(new Point(0, amount, 0)));
		}else if(direction == Direction.UP){
			changeMaximumDimensions(getMaximumDimensions().add(new Point(0, 0, amount)));
		}
	}

	/****************************************************
	 * SQUARE MANAGERMENT - TOTAL 
	 ****************************************************/
	
	/**
	 * Check whether this dungeon van have the given square as one of tis squares.
	 * 
	 * @param square
	 * 		The square to check
	 * @return If a super-type of shaft can not have the given square as one of its squares, then false.
	 * 		| if !super.canHaveAsSquare(square)
	 * 		|	then result == false
	 * @return Else if the given square is always surrounded by walls, then false
	 * 		| else !square.notAlwaysSurroundedByWalls()
	 * 		|	then result == false
	 * @return Else true.
	 */
	@Raw @Override
	public boolean canHaveAsSquare(Square square){
		if(!super.canHaveAsSquare(square))
			return false;
		if(!square.notAlwaysSurroundedByWalls())
			return false;
		return true;
	}
	
	/**
	 * Check if this dungeon has legal squares at all positions
	 * 
	 * @return If some of the squares in this dungeon are not legal at their position then false.
	 * 		| if for some s in getSquares():
	 * 		|	( !canHaveAsSquareAtPosition(s, getPositionOfSquare(s)) )
	 * 		| then result == false
	 * @return Else if this dungeon is not terminated, then true if and only if all possible positions in
	 * 		this dungeon contain a square, and there are no obstacles in the direction of the square (except at the ends)
	 * 		| result == ( for each position in Point.ORIGIN..maximumDimensions.subtract(Point.CUBE):
	 * 		|				if !isTerminated()
	 * 		|				then getSquareAt(position) != null
	 * 		|					 && if getSquareAt(position).hasNeighborAt(getDirection())
	 * 		|						then !getSquareAt(position).hasWall(getDirection()) )
	 */
	@Raw
	public boolean hasProperSquares(){
		if(!super.hasProperSquares())
			return false;
		// iterate over all position in one line
		
		if(isTerminated())
			return true;
		Point pos = Point.ORIGIN;
		Direction dir = getDirection();
		while(pos.isSmallerThan(getMaximumDimensions())){
			Square sqrAtPos = getSquareAt(pos);
			if(sqrAtPos == null)
				return false;
			if(sqrAtPos.hasNeighborAt(dir) && sqrAtPos.hasWall(dir))
				return false;
			pos = dir.getPointInThisDirectionOf(pos);
		}
		return true;
	}
	
	/**
	 * Remove the square at the given position from this dungeon if it is present, and if this
	 * dungeon is in a terminated state.
	 * 
	 * @param position
	 * 		The position to remove the square from
	 * @effect If this dungeon is terminated, then try to remove the square from this dungeon.
	 * 		| if isTerminated()
	 * 		|	then super.removeAsSquareAt(position)
	 */
	@Override
	public void removeAsSquareAt(Point position) {
		if(isTerminated())
			super.removeAsSquareAt(position);
	}

	/**
	 * Check whether a wall is allowed in the given direction for this dungeon.
	 * 
	 * @param position
	 * 		The position where the wall is next to.
	 * @param direction
	 * 		The direction of the wall relative to the given position
	 * @return If a square exists in the given direction of the given position, then false.
	 * 		(neighboring squares don't have a wall between them)
	 * 		| result == ( getSquareInDirectionOfPosition(position, direction) == null )
	 * @note This method is used in addAsSquareAt to make sure that walls are destroyed when needed.
	 */
	@Raw @Override
	public boolean canHaveWallAt(Point position, Direction direction){
		return getSquareInDirectionOfPosition(position, direction) == null; 
	}

	/**
	 * The default direction of a shaft with dimensions (1,1,1).
	 */
	public static final Direction DEFAULT_DIRECTION = Direction.EAST;
	
}
