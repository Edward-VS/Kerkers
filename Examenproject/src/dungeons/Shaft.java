package dungeons;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;
import dungeons.util.Direction;
import dungeons.util.Point;

/**
 * A Shaft is a SquareDungeon that can only contain squares in one specific direction. All maximum dimensions are 1, except for 
 * the dimension that point in the direction of the Shaft. This restriction implies that the maximum dimensions of a Shaft must comply
 * with more conditions compared to a SquareDungeon.
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
 * 		| hasProperSquares()
 * @invar The parent of a Shaft (if any) must be in a legal state, and the association between its parent dungeon must be
 * 		properly set. 
 * 		| hasProperParentDungeon()
 *
 * @author Edward Van Sieleghem & Christof Vermeersch
 *
 */

public class Shaft extends SquareDungeon{

	// TODO deduce direction out of maximum dimensions
	
	/**
	 * Construct a new dungeon with the given maximum dimensions.
	 * 
	 * @param maximumDimensions
	 *		The dimensions outside of which no squares can be added.
	 * @post The maximum dimensions of this dungeon are set to the given maximum dimensions
	 * 		| new.getMaximumDimensions() == maximumDimensions
	 * @effect The direction of this shaft is set in compliance with the given direction.
	 * 
	 * @throws IllegalArgumentException
	 * 		[MUST] The given maximum dimensions are not legal
	 * 		| !canHaveAsMaximumDimensions(maximumDimensions)
	 */
	public Shaft(Point maximumDimensions, Direction direction) throws IllegalArgumentException {
		super(maximumDimensions);
		if(canHaveAsMaximumDimensionsInDirection(maximumDimensions, direction))
			throw new IllegalArgumentException("The given maximum dimensions are not valid in the given direction.");
		
		setDirection(direction);
		// TODO Auto-generated constructor stub
	}
	
	// -+-+-+-+- DIRECTION -+-+-+-+- //
	// NOTE a direction is always valid, and can never be changed. As a result methods like isValidDirection() are not needed.
	
	/**
	 * Retrieve the direction of this shaft.
	 */
	@Basic @Immutable
	public Direction getDirection(){
		return direction;
	}
	
	/**
	 * Set the direction of this shaft in compliance to the given direction.
	 * 
	 * @param direction
	 * 		A direction
	 * @post If the given direction is SOUTH or NORTH, then the direction of this shaft is NORTH.
	 * 		| if (direction == Direction.SOUTH || direction == Direction.NORTH )
	 * 		| then new.getDirection() == Direction.NORTH
	 * @post Else if the given direction is EAST or WEST, then the direction of this shaft is EAST.
	 * 		| else if (direction == Direction.EAST || direction == Direction.WEST )
	 * 		| then new.getDirection() == Direction.EAST
	 * @post Else if the given direction is UP or DOWN, then the direction of this shaft is UP.
	 * 		| else if (direction == Direction.UP || direction == Direction.DOWN )
	 * 		| then new.getDirection() == Direction.UP
	 */
	@Model
	private void setDirection(Direction direction){
		if (direction == Direction.SOUTH || direction == Direction.NORTH )
			this.direction = Direction.NORTH;
		else if (direction == Direction.EAST || direction == Direction.WEST )
			this.direction = Direction.EAST;
		else
			this.direction = Direction.UP;
	}
	
	private Direction direction;
	
	// -+-+-+-+- DIMENSIONS -+-+-+-+- //
	
	/**
	 * Check whether the given maximum dimensions are valid for this dungeon.
	 * 
	 * @param maximumDimensions
	 *		A point that represents the maximum dimensions.
	 * @return If the given maximum dimensions are null, than false
	 * 		| if maximumDimensions == null
	 * 		|	then result == false
	 * @return	Else if the given maximum dimensions are bigger that the maximum allowed dimensions
	 *		then false
	 *		| if !maximumDimensions.isEqualOrSmallerThanValue(ABSOLUTE_MAXIMUM_DIMENSIONS)
	 *		|	then result == false
	 * @return Else if the given maximum dimensions are smaller or equal to zero, then false
	 *		| if maximumDimensions.isEqualOrSmallerThanValue(0)
	 *		|	then result == false
	 * @return Else if this dungeon has a parent, and the given maximum dimensions are not legal for this dungeon
	 *		in that parent, then false
	 *		| if getParentDungeon() != null
	 *		|	&& !getParentDungeon().overlapsWithOtherSubDungeon(this, maximumDimensions))
	 *		|	then result == false
	 *		Else true.
	 */
	@Raw
	public boolean canHaveAsMaximumDimensionsInDirection(Point maximumDimensions, Direction direction) {
		if(maximumDimensions == null)
			return false;
		if(!maximumDimensions.isEqualOrSmallerThanValue(ABSOLUTE_MAXIMUM_DIMENSIONS))
			return false;
		if(maximumDimensions.isEqualOrSmallerThanValue(0))
			return false;
		/*if(getParentDungeon() != null && !getParentDungeon().overlapsWithOtherSubDungeon(this, maximumDimensions))
			return false;*/
		return true;
		//TODO
	}

	/**
	 * Check whether this dungeon has proper maximum dimensions.
	 * 
	 * @return True if the current maximum dimensions of this dungeon are legal.
	 * 		| result == canHaveAsMaximumDimensionsInDirection(getMaximumDimensions(), direction)
	 */
	public boolean hasProperMaximumDimensions(){
		return canHaveAsMaximumDimensionsInDirection(getMaximumDimensions(), direction);
	}
	
	public void extend(int amount){
		
	}
	
	
	// -+-+-+-+- SQUARE MANAGERMENT - TOTAL -+-+-+-+- //
	
	/**
	 * Check whether the given square is valid for this dungeon.
	 * 
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
		if(square.isTerminated())
			return false;
		return ( square != null || isTerminated() );
		//TODO call super
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
	
	// TODO contructie van shaft maakt zelf squares aan; geen squares kunnen verwijders worden, als het niet getermineerd is. -> canHaveAsSquareAtPosition()
	// aanvaard nooit null, behalve als de shaft terminated is. Squares kunnen wel toegevoegd worden (=vervangen), maar obstacels moeten opnieuw voldoen.
	// Methode wordt aangeboden om een shaft te verlengen.
	// NOTE extending a shaft must take in account the parent dungeon
}
