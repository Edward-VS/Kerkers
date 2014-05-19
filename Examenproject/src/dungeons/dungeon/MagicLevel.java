package dungeons.dungeon;

import java.util.ArrayList;
import java.util.List;

import dungeons.exception.IllegalDungeonException;
import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.util.Point;

/**
 * <p>A Level that has magical properties.</p>
 * 
 * @see Documentation of MagicDungeon and Level is also valid.
 * 
 * @invar A MagicLevel must always have maximum dimensions that are smaller or equal to the maximum allowed dimensions,
 * 		bigger than (0,0,0), in compliance with the dimensions of its parent dungeon (if any) and legal in any other way.
 * 		In addition, a Level must always have a maximum z dimension of 1.
 *		| hasProperMaximumDimensions()
 * @invar All the squares in a MagicLevel must be in a legal state.
 * 		| hasProperSquares()
 * @invar The parent of a MagicLevel (if any) must be in a legal state, and the association between its parent dungeon must be
 * 		properly set. 
 * 		| hasProperParentDungeon()
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 */
public class MagicLevel extends Level implements MagicDungeon{

	/**
	 * Construct a new magical Level with the given maximum dimensions.
	 * 
	 * @param maximumDimensions
	 *		The dimensions outside of which no squares can be added.
	 * @post The maximum dimensions of this dungeon are set to the given maximum dimensions
	 * 		| new.getMaximumDimensions() == maximumDimensions
	 * @throws IllegalMaximumDimensionsException(maximumDimensions)
	 * 		[MUST] The given maximum dimensions are not legal
	 * 		| !canHaveAsMaximumDimensions(maximumDimensions)
	 */
	public MagicLevel(Point maximumDimensions) throws IllegalMaximumDimensionsException {
		super(maximumDimensions);
	}
	
	/**
	 * Construct a new level with maximum dimensions (1,1,1). (it can contain one square)
	 * 
	 * @effect A new dungeon is constructed with maximum dimensions (1,1,1)
	 * 		| super()
	 */
	public MagicLevel() throws IllegalMaximumDimensionsException {
		super();
	}

	/**********************************************
	 * MAGICAL STUFF
	 **********************************************/
	
	/**
	 * Scramble the squares and obstacles in this dungeon.
	 * 
	 * @see MagicDungeon
	 */
	@Override
	public void scramble() throws IllegalStateException {
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
