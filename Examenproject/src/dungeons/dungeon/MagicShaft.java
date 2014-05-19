package dungeons.dungeon;

import java.util.ArrayList;
import java.util.List;

import dungeons.exception.IllegalDungeonException;
import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.util.Point;

/**
 * <p>A Shaft that has magical properties.</p>
 * 
 * @see Documentation of MagicDungeon and Shaft is also valid.
 * 
 * @invar A MagicShaft must always have maximum dimensions that are smaller than  or equal to the maximum allowed dimensions,
 * 		bigger than (0,0,0), in compliance with the dimensions of its parent dungeon (if any) and legal in any other way.
 * 		In addition, a Shaft must always have maximum dimensions which can be parsed into a direction.
 *		| hasProperMaximumDimensions()
 * @invar All the squares in a MagicShaft must be in a legal state. They may not be rocks, and may not contain walls in the direction
 * 		of the shaft except at the ends. All positions in a shaft must contain a square if the shaft in not terminated.
 * 		| hasProperSquares()
 * @invar The parent of a MagicShaft (if any) must be in a legal state, and the association between its parent dungeon must be
 * 		properly set. 
 * 		| hasProperParentDungeon()
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 */
public class MagicShaft extends Shaft implements MagicDungeon {

	/**
	 * Construct a new magical shaft with the given maximum dimensions.
	 * 
	 * @param maximumDimensions
	 *		The dimensions outside of which no squares can be added.
	 * @post The maximum dimensions of this dungeon are set to the given maximum dimensions
	 * 		| new.getMaximumDimensions() == maximumDimensions
	 * @post The shaft has a normal square at all positions
	 * 		| for each position in Point.ORIGIN..maximumDimensions.subtract(Point.CUBE):
	 * 		|	getSquareAt(position) != null
	 * @throws IllegalMaximumDimensionsException(maximumDimensions)
	 * 		[MUST] The given maximum dimensions are not legals
	 * 		| !canHaveAsMaximumDimensions(maximumDimensions)
	 */
	public MagicShaft(Point maximumDimensions) throws IllegalMaximumDimensionsException {
		super(maximumDimensions);
	}
	
	/**
	 * Construct a new shaft with maximum dimensions (1,1,1).
	 * 
	 * @effect Construct a new dungeon with dimensions (1,1,1).
	 * 		| super()
	 */
	public MagicShaft() throws IllegalMaximumDimensionsException {
		super();
	}

	/************************************************
	 * MAGICAL
	 ************************************************/
	
	/**
	 * Scramble the squares and obstacles in this dungeon.
	 * 
	 * @see MagicDungeon
	 */
	@Override
	public void scramble() throws IllegalStateException{
		try {
			(new MagicDungeonHelper(this)).scramble();
		} catch (IllegalDungeonException e) {
			throw new IllegalStateException("This dugneon is termianted");
		}
	}
	
	/**
	 * Retrieve all the positions of actual squares that can be swapped during a scramble
	 * 
	 * @return A collection a positions of actual squares that can be scrambled
	 * 		| for each square in getSquare():
	 * 		|	result.contains(getPositionOfSquare(square))
	 */
	public List<Point> getSwappablePositions(){
		return new ArrayList<Point>(squares.keySet());
	}

}
