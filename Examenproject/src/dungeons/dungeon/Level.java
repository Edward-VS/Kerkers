package dungeons.dungeon;

import be.kuleuven.cs.som.annotate.Raw;
import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.util.Point;

/**
 * <p>A Level is a SquareDungeon that can only contain squares in the z=0 plane (given that all squares have
 * a zero-bases position). This restriction implies that the maximum dimensions of a Level must comply
 * with more conditions compared to a SquareDungeon.</p>
 * 
 * @see Documentation of SquareDungeon is also valid.
 * 
 * @invar A Level must always have maximum dimensions that are smaller or equal to the maximum allowed dimensions,
 * 		bigger than (0,0,0), in compliance with the dimensions of its parent dungeon (if any) and legal in any other way.
 * 		In addition, a Level must always have a maximum z dimension of 1.
 *		| hasProperMaximumDimensions()
 * @invar All the squares in a Level must be in a legal state.
 * 		| hasProperSquares()
 * @invar The parent of a Level (if any) must be in a legal state, and the association between its parent dungeon must be
 * 		properly set. 
 * 		| hasProperParentDungeon()
 *
 * @author Edward Van Sieleghem & Christof Vermeersch
 */

public class Level extends SquareDungeon{

	/**
	 * Construct a new dungeon with the given maximum dimensions.
	 * 
	 * @param maximumDimensions
	 *		The dimensions outside of which no squares can be added.
	 * @post The maximum dimensions of this dungeon are set to the given maximum dimensions
	 * 		| new.getMaximumDimensions() == maximumDimensions
	 * @throws IllegalMaximumDimensionsException(maximumDimensions)
	 * 		[MUST] The given maximum dimensions are not legal
	 * 		| !canHaveAsMaximumDimensions(maximumDimensions)
	 */
	public Level(Point maximumDimensions) throws IllegalMaximumDimensionsException{
		super(maximumDimensions);
	}
	
	/**
	 * Construct a new level with maximum dimensions (1,1,1). (it can contain one square)
	 * 
	 * @effect A new dungeon is constructed with maximum dimensions (1,1,1)
	 * 		| this(Point.CUBE)
	 */
	public Level() throws IllegalMaximumDimensionsException {
		this(Point.CUBE);
	}
	
	
	/***********************************************************
	 * DIMENSIONS
	 ***********************************************************/
	
	/**
	 * Check whether the given maximum dimensions are valid for this dungeon.
	 * 
	 * @param maximumDimensions
	 *		A point that represents the maximum dimensions.
	 * @return If the given maximum dimensions do not comply with the conditions imposed on it by a
	 * 		super-type of Level, then false.
	 * 		| if !super.canHaveAsMaximumDimensions(maximumDimensions)
	 * 		|	then result == false
	 * @return Else if the given maximum dimensions have a z-coordinate that is bigger than 1, then false.
	 * 		| else if maximumDimensions.getZ() != 1
	 * 		|	then result == false
	 * @return Else true
	 * 		| else result == false
	 */
	@Raw @Override
	public boolean canHaveAsMaximumDimensions(Point maximumDimensions) {
		if(!super.canHaveAsMaximumDimensions(maximumDimensions))
			return false;
		if(maximumDimensions.getZ() != 1)
			return false;
		return true;
	}
	
	/**
	 * Enlarge this level with the given amount.
	 * 
	 * @param x
	 * 		The amount to enlarge this level with in the x direction.
	 * @param y
	 * 		The amount to enlarge this level with in the y direction.
	 * @pre The x and y values are bigger than or equal to zero
	 * 		| x >= 0 && y >= 0
	 * @effect The dimensions of this level are enlarged with the given amount.
	 * 		| changeMaximumDimensions(getMaximumDimensions().add(new Point(x, y, 0)))
	 * @throws IllegalMaximumDimensionsException [MUST]
	 * 		The new maximum dimensions are illegal
	 * 		| !canHaveAsMaximumDimsnesions(getMaximumDimensions().add(new Point(x, y, 0)))
	 */
	public void enlarge(int x, int y) throws IllegalMaximumDimensionsException{
		changeMaximumDimensions(getMaximumDimensions().add(new Point(x, y, 0)));
	}
	
}
