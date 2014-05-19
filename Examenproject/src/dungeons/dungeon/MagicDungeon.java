package dungeons.dungeon;

import java.util.List;

import dungeons.util.Point;

/**
 * <p>A dungeon that has magical properties.</p>
 * 
 * <p>MagicDungeons can be scrambled which permutes squares, creates new ones, and collapses rocks.</p>
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 */
public interface MagicDungeon extends Dungeon{
	
	/**
	 * Scramble the squares and obstacles in this dungeon.
	 * 
	 * @post Squares are permuted at a minimum rate of MagicDungeonHelper.getPermutationFactor(). The permuted squares belong 
	 * 		to a magical dungeon.
	 * 		| for some squareOne in getSquares():
	 * 		|	for some squareTwo in getSquares():
	 * 		|		new.getPositionOfSquare(squareOne) == this.getPositionOfSquare(squareTwo)
	 * 		|		&& new.getPositionOfSquare(squareTwo) == this.getPositionOfSquare(squareOne)
	 * 		|		&& MagicDungeonHelper.canParticipateInScramble(getDungeonContainingPosition(getPositionOfSquare(squareOne))
	 * 		|		&& MagicDungeonHelper.canParticipateInScramble(getDungeonContainingPosition(getPositionOfSquare(squareTwo))
	 * @post Empty spots in the dungeon are filled with squares at a maximum rate of MagicDungeonHelper.getFillFactor().
	 * 		| for some position in Point.ORIGIN..getMaximumDimensions():
	 * 		|	this.getSquareAt(position) == null && new.getSquareAt(position) != null
	 * @effect Collapseable squares collapse at a rate of MagicDungeonHelper.getCollapseFactor().
	 * 		| for some square in getSquares():
	 * 		|	if square.canCollapse()
	 * 		|	then collapse(square)
	 * @throws  IllegalStateException
	 * 		[MUST] This dungeon is terminated
	 * 		| isTerminated()
	 */
	public void scramble() throws IllegalStateException;
	
	/**
	 * Retrieve all the positions of actual squares that can be swapped during a scramble
	 * 
	 * @return A collection a positions of actual squares that can be scrambled
	 * 		| for each position in result:
	 * 		|	hasSquareAt(position)
	 * 		|	&& MagicDungeonHelper.canParticipateInScramble(getDungeonContainingPosition(position))
	 */
	public List<Point> getSwappablePositions();
	
}
