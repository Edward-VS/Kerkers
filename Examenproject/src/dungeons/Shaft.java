package dungeons;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;
import dungeons.exception.IllegalDirectionException;
import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.obstacle.Obstacle;
import dungeons.util.Direction;
import dungeons.util.Point;

/**
 * A Shaft is a SquareDungeon that can only contain squares in one specific direction. All maximum dimensions are 1, except for 
 * the dimension that point in the direction of the Shaft. This restriction implies that the maximum dimensions of a Shaft must comply
 * with more conditions compared to a SquareDungeon. The direction of a shaft is immutable.
 * 
 * A Shaft can not contain Rocks as its squares, and obstacles in the direction of the shaft may not be Walls (they can be Doors).
 * Shafts are filled entirely with Squares.
 * 
 * A Shaft offers a method for extending it. This method will throw an exception if extension is not possible given the parent of the
 * composite dungeon (amongst other reasons).
 * 
 * @see Documentation of SquareDungeon is also valid.
 * 
 * @invar A Shaft must always have maximum dimensions that are smaller or equal to the maximum allowed dimensions,
 * 		bigger than (0,0,0), in compliance with the dimensions of its parent dungeon (if any) and legal in any other way.
 * 		In addition, a Shaft must always have maximum dimensions of which two are equal to 1.
 *		| hasProperMaximumDimensions()
 * @invar All the squares in a Shaft must be in a legal state. They may not be rocks, and may not contain walls in the direction
 * 		of the shaft except at the ends. All positions in a shaft must contain a square if the shaft in not terminated.
 * 		A shaft must always be completely filled up with squares.
 * 		| hasProperSquares()
 * @invar The parent of a Shaft (if any) must be in a legal state, and the association between its parent dungeon must be
 * 		properly set. 
 * 		| hasProperParentDungeon()
 * @invar The direction of a shaft is in compliance with its maximum dimensions.
 * 		| hasProperDirection()
 *
 * @author Edward Van Sieleghem & Christof Vermeersch
 */
public class Shaft extends SquareDungeon{
	
	/**
	 * Construct a new dungeon with the given maximum dimensions.
	 * 
	 * @param maximumDimensions
	 *		The dimensions outside of which no squares can be added.
	 * @param direction
	 * 		The direction in which this shaft is directed.
	 * @post The maximum dimensions of this dungeon are set to the given maximum dimensions
	 * 		| new.getMaximumDimensions() == maximumDimensions
	 * @post The direction of this shaft is set to the given direction.
	 * 		| new.getDirection() == direction
	 * @post TODO fill
	 * @throws IllegalMaximumDimensionsException(maximumDimensions)
	 * 		[MUST] The given maximum dimensions are not legal
	 * 		| !canHaveAsMaximumDimensions(maximumDimensions)
	 * @throws IllegalDirectionException(Direction)
	 * 		[MUST] The given direction is not valid, or not in compliance with the given maximum dimensions
	 * 		| canHaveAsDirectionForMaximumDimensions(maximumDimensions, direction)
	 */
	public Shaft(Point maximumDimensions, Direction direction) throws IllegalMaximumDimensionsException, IllegalDirectionException {
		super(maximumDimensions);
		if(!canHaveAsDirectionForMaximumDimensions(direction, maximumDimensions))
			throw new IllegalDirectionException(direction);
		setDirection(direction);
		
		// Fill the dungeon with squares.
		Point pos = Point.ORIGIN;
		while(getMaximumDimensions().isEqualOrBiggerThanPoint(pos)){
			// add square and try to remove wall (will fail if first square in line)
			addAsSquareAt(new Square(), pos, getDirection().oppositeDirection());
			pos = direction.getPointInThisDirectionOf(pos);
		}
	}
	
	
	
	/****************************************************
	 * DIRECTION
	 ****************************************************/

	/**
	 * Retrieve the direction of this shaft.
	 */
	@Basic @Immutable
	public Direction getDirection(){
		return direction;
	}
	
	/**
	 * Check whether the given direction is valid for any shaft.
	 * 
	 * @param direction
	 * 		A direction
	 * @return False if the given direction is not effective
	 * 		| if direction == null
	 * 		| 	then result == false
	 * @return Else true if and only if the given direction is NORTH, EAST or UP. (This limitation is set
	 * 		because extending a shaft can only be done in those directions, else de maximum dimension of
	 * 		the shaft would become negative.
	 * 		| result == ( direction == Direction.NORTH
	 * 		|			|| direction == Direction.EAST
	 * 		|			|| direction == Direction.UP )
	 */
	@Raw
	public static boolean isValidDirection(Direction direction){
		if(direction == null)
			return false;
		return direction == Direction.NORTH || direction == Direction.EAST || direction == Direction.UP;
	}
	
	/**
	 * Check whether the given direction is legal with the given maximum dimensions.
	 * 
	 * @param direction
	 * 		The direction to check
	 * @param maximumDimensions
	 * 		The maximum dimensions that the given direction must be in compliance with
	 * @return If the a shaft can not have the given direction as its direction, then false
	 * 		| if !isValidDirection(direction)
	 * 		|	then result == false
	 * @return Else if the given direction is EAST, then true if and only if the maximum dimensions
	 * 		have y and z coordinates that are equals to 1.
	 * 		| else if direction == Direction.EAST
	 *		|	then result == ( maximumDimensions.getY() == 1 && maximumDimensions.getZ() == 1 )
	 * @return Else if the given direction is NORTH, then true if and only if the maximum dimensions
	 * 		have x and z coordinates that are equals to 1.
	 * 		| else if direction == Direction.NORTH
	 *		|	then result == ( maximumDimensions.getX() == 1 && maximumDimensions.getZ() == 1 )
	 * @return Else if the given direction is UP, then true if and only if the maximum dimensions
	 * 		have x and y coordinates that are equals to 1.
	 * 		| else if direction == Direction.UP
	 *		|	then result == ( maximumDimensions.getX() == 1 && maximumDimensions.getY() == 1 )
	 * @return Else false;
	 */
	//TODO must be private?
	@Raw
	public boolean canHaveAsDirectionForMaximumDimensions(Direction direction, Point maximumDimensions) {
		if(!isValidDirection(direction) || maximumDimensions == null)
			return false;
		if(direction == Direction.EAST)
			return maximumDimensions.getY() == 1 && maximumDimensions.getZ() == 1;
		if(direction == Direction.NORTH)
			return maximumDimensions.getX() == 1 && maximumDimensions.getZ() == 1;
		if(direction == Direction.UP)
			return maximumDimensions.getX() == 1 && maximumDimensions.getY() == 1;
		return false;
	}
	
	/**
	 * Check whether this shaft has a proper direction.
	 * 
	 * @return True if the direction of this shaft is valid, and in compliance with the maximum
	 * 		dimensions of this dungeon.
	 * 		| result == canHaveAsDirectionForMaximumDimensions(getDirection(), getMaximumDimensions())
	 */
	@Raw
	public boolean hasProperDirection(){
		return canHaveAsDirectionForMaximumDimensions(getDirection(), getMaximumDimensions());
	}
	
	/**
	 * Set the direction of this shaft in compliance to the given direction.
	 * 
	 * @param direction
	 * 		A direction
	 * @post The direction of this shaft is the given direction.
	 * 		| new.getDirection() == direction
	 * @thows IllegalDirectionException(direction)
	 * 		[MUST] The given direction is not valid for any shaft.
	 * 		| !isValidDirection(direction)
	 */
	@Model @Raw
	private void setDirection(Direction direction) throws IllegalDirectionException{
		if(!isValidDirection(direction))
			throw new  IllegalDirectionException(direction);
		this.direction = direction;
	}
	
	/*
	 * The direction of this shaft (immutable)
	 */
	private Direction direction;
	
	
	
	
	/****************************************************
	 * DIMENSIONS
	 ****************************************************/
	
	/**
	 * Check whether the given maximum dimension are legal as new maximum dimensions.
	 * 
	 * @param maximumDimensions
	 *		A point that represents the new maximum dimensions.
	 * @return If the given maximum dimension are not legal, or one the the coordinates is smaller
	 * 		then the respective coordinate of the current maximum dimensions, then false.
	 *		| if !canHaveAsMaximumDimensions(maximumDimensions)
	 *		|		|| !maximumDimensions.isEqualOrBiggerThanPoint(getMaximumDimensions())
	 *		|	then result == false
	 * @return Else true if and only if the given maximum dimensions are in compliance
	 * 		with the direction of this shaft.
	 * 		| else result == canHaveAsDirectionForMaximumDimensions(getDirection(), maximumDimensions)
	 */
	public boolean canAcceptAsNewMaximumDimensions(Point maximumDimensions){
		if(!super.canAcceptAsNewMaximumDimensions(maximumDimensions))
			return false;
		if(!canHaveAsDirectionForMaximumDimensions(getDirection(), maximumDimensions))
			return false;
		return true;
	}
	
	/**
	 * Set the new maximum dimensions of this dungeon to the given maximum dimensions, and fill the new
	 * position with new squares.
	 * 
	 * @param maximumDimensions
	 *		The new maximum dimensions for this dungeon
	 * @post The maximum dimensions of this dungeon are the given maximum dimensions.
	 *		| new.getmaximumDimensions() == maximumDimensions
	 * @post All possible positions in this dungeon are filled.
	 * 		| TODO
	 * @throws IllegalMaximumDimensionsException(maximumDimensions)
	 *		[MUST] The given new maximum dimensions
	 *		| !canAcceptAsNewMaximumDimensions(maximumDimensions)
	 */
	public void changeMaximumDimensions(Point maximumDimensions) throws IllegalMaximumDimensionsException {
		Point pos = getMaximumDimensions();
		super.changeMaximumDimensions(maximumDimensions);
		while(getMaximumDimensions().isEqualOrBiggerThanPoint(pos)){
			// add square and try to remove wall (will fail if first square in line)
			addAsSquareAt(new Square(), pos, getDirection().oppositeDirection());
			pos = direction.getPointInThisDirectionOf(pos);
		}
	}
	
	/**
	 * Extend this shaft with the given amount.
	 * 
	 * @param amount
	 * 		The amount to extend this shaft with
	 * @effect The dimensions of this shaft are enlarged with the given amount in the direction of this shaft.
	 * 		| if direction == Direction.EAST
	 * 		|	then changeMaximumDimensions(getMaximumDimensions().add(new Point(amount, 0, 0)))
	 * 		| else if direction == Direction.NORTH
	 * 		|	then changeMaximumDimensions(getMaximumDimensions().add(new Point(0, amount, 0)))
	 * 		| else if direction == Direction.UP
	 * 		|	then changeMaximumDimensions(getMaximumDimensions().add(new Point(0, 0, amount)))
	 * @throws IllegalMaximumDimensionsException [MUST]
	 * 		The given amount is smaller than zero
	 * 		| amount < 0
	 */
	public void extend(int amount) throws IllegalMaximumDimensionsException{
		if(direction == Direction.EAST){
			changeMaximumDimensions(getMaximumDimensions().add(new Point(amount, 0, 0)));
		}else if(direction == Direction.NORTH){
			changeMaximumDimensions(getMaximumDimensions().add(new Point(0, amount, 0)));
		}else if(direction == Direction.UP){
			changeMaximumDimensions(getMaximumDimensions().add(new Point(0, 0, amount)));
		}
	}

	
	
	// -+-+-+-+- SQUARE MANAGERMENT - TOTAL -+-+-+-+- //
	
	/**
	 * Check whether the given square is valid for this dungeon.
	 * TODO doc
	 * @param square
	 * 		The square to check
	 * @return False if the given square is terminated
	 * 		| if square.isTerminated()
	 * 		|	then result == false
	 * @return Else true if and only if the given square is not null or it is null
	 * 		and this dungeon is terminated.
	 * 		| else result == ( square != null || isTerminated() )
	 */
	public boolean canHaveAsSquare(Square square){
		if(!super.canHaveAsSquare(square))
			return false;
		if(square == null && !isTerminated())
			return false;
		if(square instanceof Rock)
			return false;
		return true;
	}
	
	/**
	 * Check whether the given square can be placed on the given position in this dungeon.
	 * 
	 * @param square
	 * 		The square to check.
	 * @param position
	 * 		The proposed position of the given square.
	 * @return If the given square is not legal in this dungeon then false
	 * 		| if !canHaveAsSquare(square)
	 * 		|	then result == false
	 * @return Else if the given position is smaller than zero then false
	 * 		| else if !position.isEqualOrBiggerThanValue(0)
	 * 		|	then result == false
	 * @return Else if the given position is not contained in any dungeon that can contain
	 * 		squares, then false
	 * 		| else if getDungeonContainingPosition(position) == null
	 * 		|	then result == false
	 * @return Else true if and only if the given position is smaller than the maximum dimensions
	 *  	| else result == position.isSmallerThan(maximumDimensions)
	 */
	public boolean canHaveAsSquareAt(Square square, Point position){
		if(!canHaveAsSquare(square))
			return false;
		if(!position.isEqualOrBiggerThanValue(0))
			return false;
		if(!position.isSmallerThan(getMaximumDimensions()))
			return false;
		if(getDungeonContainingPosition(position) == null)
			return false;
		return true;
		//TODO call super
	}
	
	/**
	 * Check if this dungeon has legal squares at all positions
	 * 
	 * @return If some of the squares in this dungeon are not legal at their position then false.
	 * 		| if for some s in getSquares():
	 * 		|	( !canHaveAsSquareAtPosition(s, getPositionOfSquare(s)) )
	 * 		| then result == false
	 * @return Else true if and only if all possible positions in this dungeon contain a square.
	 * 		| TODO formal?
	 */
	public boolean hasProperSquares(){
		if(!super.hasProperSquares())
			return false;
		// iterate over all position in one line
		Point pos = Point.ORIGIN;
		while(getMaximumDimensions().isEqualOrBiggerThanPoint(pos)){
			if(getSquareAt(pos) == null)
				return false;
			pos = direction.getPointInThisDirectionOf(pos);
		}
		return true;
	}
	
	@Raw
	public boolean canHaveAsObstacleAt(Obstacle obstacle, Point position, Direction direction){
		// TODO
		return true; 
	}
	
	// TODO contructie van shaft maakt zelf squares aan; geen squares kunnen verwijders worden, als het niet getermineerd is. -> canHaveAsSquareAtPosition()
	// aanvaard nooit null, behalve als de shaft terminated is. Squares kunnen wel toegevoegd worden (=vervangen), maar obstacels moeten opnieuw voldoen.
	// Methode wordt aangeboden om een shaft te verlengen.
	// NOTE extending a shaft must take in account the parent dungeon
}
