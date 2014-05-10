package dungeons;

import be.kuleuven.cs.som.annotate.Raw;
import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.util.Point;

/**
 * A Level is a SquareDungeon that can only contain squares in the z=0 plane (given that all squares have
 * a zero-bases position). This restriction implies that the maximum dimensions of a Level must comply
 * with more conditions compared to a SquareDungeon.
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
 *
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
	
	
	
	/***********************************************************
	 * DIMENSIONS
	 ***********************************************************/
	
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
	 * @return Else if the given maximum dimensions have a z-coordinate that is bigger than 1, then false.
	 * 		| if  
	 */
	@Raw
	public boolean canHaveAsMaximumDimensions(Point maximumDimensions) {
		if(!super.canHaveAsMaximumDimensions(maximumDimensions))
			return false;
		if(maximumDimensions.getZ() != 1)
			return false;
		return true;
	}
	
}
