package dungeons.dungeon;

import java.util.List;
import java.util.Random;

import be.kuleuven.cs.som.annotate.Basic;
import dungeons.exception.IllegalDungeonException;
import dungeons.square.Square;
import dungeons.util.Direction;
import dungeons.util.Point;

/**
 * <p>A helper class which includes implementations of methods that are mutual to all classes implementing
 * the MagicDungeon interface.</p>
 * 
 * <p>The magic dungeon that is linked to this helper is mutable.</p>
 * 
 * @invar The magic dungeon that is linked to this helper must be legal
 * 		| hasProperMagicDungeon()
 * @invar The factors that determine the behavior of a scramble must be in the range of [0,1]
 * 		| isValidFactor(getPermutationFactor()) && isValidFactor(getFillFactor()) && isValidFactor(getCollapseFactor())
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 */
public class MagicDungeonHelper {

	/**
	 * Construct a new helper for the given magic dungeon.
	 * 
	 * @param magicDungeon
	 * 		The magic dungeon of this helper
	 * @post This helper now has the given dungeon as it magic dungeon.
	 * 		| new.getMagicDungeon() == magicDungeon
	 * @throws IllegalDungeonException
	 * 		The given magic dungeon is not legal for any helper class.
	 * 		| ! canHaveAsMagicDungeon()
	 */
	public MagicDungeonHelper(MagicDungeon magicDungeon) throws IllegalDungeonException{
		setMagicDungeon(magicDungeon);
	}
	
	
	
	/********************************************
	 * MAGIC DUNGEON MANAGEMENT
	 ********************************************/
	
	/**
	 * Retrieve the magic dungeon that is linked to this helper.
	 */
	@Basic
	public MagicDungeon getMagicDungeon(){
		return magicDungeon;
	}
	
	/**
	 * Check whether this helper can have the given dungeon as its magic dungeon.
	 * 
	 * @param dungeon
	 * 		The dungeon to check.
	 * @return If the given dungeon is not effective, then false.
	 * 		| if dungeon == null
	 * 		|	then result == false
	 * @return Else if the given dungeon is terminated, then false.
	 * 		| else if dungeon.isTerminated()
	 * 		|	then result == false
	 * @return Else true
	 * 		| else result == true
	 */
	public boolean canHaveAsMagicDungeon(MagicDungeon dungeon){
		if(dungeon == null)
			return false;
		if(dungeon.isTerminated())
			return false;
		return true;
	}
	
	/**
	 * Check whether this helper has a legal magic dungeon associated to it.
	 * 
	 * @return True if and only if the current magic dungeon is legal for this helper.
	 * 		| result == canHaveAsMagicDungeon(getMagicDungeon())
	 */
	public boolean hasProperMagicDungeon(){
		return canHaveAsMagicDungeon(getMagicDungeon());
	}
	
	/**
	 * Set the given dungeon as the dungeon that is associated with this helper.
	 * 
	 * @param magicDungeon
	 * 		The dungeon to set
	 * @post The dungeon associated with this helper is the given dungeon.
	 * 		| new.getMagicDungeon() == magicDungeon
	 * @throws IllegalDungeonException
	 * 		[MUST] The given dungeon is not legal as dungeon for this helper.
	 * 		| !canHaveAsMagicDungeon(magicDungeon)
	 */
	public void setMagicDungeon(MagicDungeon magicDungeon) throws IllegalDungeonException{
		if(!canHaveAsMagicDungeon(magicDungeon))
			throw new IllegalDungeonException(magicDungeon);
		this.magicDungeon = magicDungeon;
	}
	
	/**
	 * The dungeon that is linked to this helper.
	 */
	private MagicDungeon magicDungeon;
	
	
	
	/********************************************
	 * SCRAMBLE
	 ********************************************/
	
	/**
	 * Scramble the squares and obstacles in this dungeon.
	 * 
	 * @post Squares of the dungeon linked to this helper are permuted at a minimum rate of MagicDungeonHelper.getPermutationFactor().
	 * 		The permuted squares can participate in a scramble.
	 * 		| for some squareOne in getSquares():
	 * 		|	for some squareTwo in getSquares():
	 * 		|		new.getPositionOfSquare(squareOne) == this.getPositionOfSquare(squareTwo)
	 * 		|		&& new.getPositionOfSquare(squareTwo) == this.getPositionOfSquare(squareOne)
	 * 		|		&& MagicDungeonHelper.canParticipateInScramble(getDungeonContainingPosition(getPositionOfSquare(squareOne))
	 * 		|		&& MagicDungeonHelper.canParticipateInScramble(getDungeonContainingPosition(getPositionOfSquare(squareTwo))
	 * @post Empty spots in the dungeon linked to this helper are filled with squares at a maximum rate of MagicDungeonHelper.getFillFactor().
	 * 		| for some position in Point.ORIGIN..getMaximumDimensions():
	 * 		|	this.getSquareAt(position) == null && new.getSquareAt(position) != null
	 * @effect Collapseable squares in the dungeon linked to this helper collapse at a rate of MagicDungeonHelper.getCollapseFactor().
	 * 		| for some square in getSquares():
	 * 		|	if square.canCollapse()
	 * 		|	then collapse(square)
	 * @note For swapping squares an exact swap rate can be realized.
	 * 		For adding new squares, a less robust approach is used (if adding fails too much times in a row,
	 * 		then no more squares are added)
	 */
	public void scramble(){
		Random rand = new Random(System.currentTimeMillis());
		
		// Squares are swapped
		List<Point> swappable = magicDungeon.getSwappablePositions();
		int swappableInit = swappable.size();
		int swapped = 0;
		
		if(swappable.size() >= 2){
			// we swap until the permutation factor is fulfilled
			while( ((float)swapped/swappableInit) < permutation_factor && swappable.size() >= 2){
				int i = rand.nextInt(swappable.size());
				Point p = swappable.remove(i);
				i = rand.nextInt(swappable.size());
				Square s = magicDungeon.getSquareAt(swappable.remove(i));
				swapped+=2;
				magicDungeon.swapSquareAt(p, s);
			}
		}
		
		// Empty spaces are filled
		int emptySpace = magicDungeon.getEmptrySpace();
		int added = 0;
		if(emptySpace != 0){
			int tryCount = 0;
			while(((float) added/emptySpace) < fill_factor && tryCount < 10){
				Point p = magicDungeon.getMaximumDimensions().randomSmallerThanThis(rand);
				if(magicDungeon.hasSquareAt(p)){
					tryCount++;
				}else{
					added++;
					tryCount = 0;
					magicDungeon.addAsSquareAt(new Square(), p, Direction.values()[rand.nextInt(6)]);
				}
			}
		}
		
		// Collapseable squares are collapsed. We can not use a SquareIterator here, since that would throw a ConcurrentModificationException
		// (squares are removes from treeMaps via a method other than SquareIterator.remove() ).
		for(Square s: magicDungeon.getSquares()){
			if(s.canCollapse() && rand.nextFloat() < collapse_factor){
				magicDungeon.collapse(s);
			}
		}
	}
	
	/**
	 * Check whether the given sub dungeon can participate in a scramble of any magicCompositeDungeon.
	 * 
	 * @param dungeon
	 * 		The sub dungeon to check
	 * @return True if the given dungeon is an instance of MagicDungeon
	 * 		| result == ( dungeon instanceof MagicDungeon )
	 */
	public static boolean canParticipateInScramble(Dungeon dungeon){
		return dungeon instanceof MagicDungeon;
	}

	/**
	 * Get the rate at which squares are permuted during a scramble.
	 */
	@Basic
	public static float getPermutationFactor() {
		return permutation_factor;
	}

	/**
	 * Get the rate at which empty position are filled during a scramble.
	 */
	@Basic
	public static float getFillFactor() {
		return fill_factor;
	}
	
	/**
	 * Get the rate at which collapsable squares are collapsed during a scramble.
	 */
	@Basic
	public static float getCollapseFactor() {
		return collapse_factor;
	}
	
	/**
	 * Set the rate at which squares are permuted during a scramble.
	 * 
	 * @param permutationFactor
	 * 		The rate at which squares are permuted during a scramble
	 * @pre The given value must be in the range of [0,1]
	 * 		| 0 <= permutationFactor && permutationFactor >= 1
	 * @post The permutation factor of this helper if the given value.
	 * 		| new.getPermutationFactor() == permutationFactor
	 */
	public static void setPermutationFactor(float permutationFactor) {
		MagicDungeonHelper.permutation_factor = permutationFactor;
	}

	/**
	 * Set the rate at which empty position are filled during a scramble.
	 * 
	 * @param fillFactor
	 * 		The rate at which empty position are filled during a scramble
	 * @pre The given value must be in the range of [0,1]
	 * 		| 0 <= fillFactor && fillFactor >= 1
	 * @post The fill factor of this helper if the given value.
	 * 		| new.getFillFactor() == fillFactor
	 */
	public static void setFillFactor(float fillFactor) {
		MagicDungeonHelper.fill_factor = fillFactor;
	}

	/**
	 * Set the rate at which collapsable squares are collapsed during a scramble.
	 * 
	 * @param collapseFactor
	 * 		The rate at which collapsable squares are collapsed during a scramble.
	 * @pre The given value must be in the range of [0,1]
	 * 		| 0 <= collapseFactor && collapseFactor >= 1
	 * @post The collapse factor of this helper if the given value.
	 * 		| new.getCollapseFactor() == collapseFactor
	 */
	public static void setCollapseFactor(float collapseFactor) {
		MagicDungeonHelper.collapse_factor = collapseFactor;
	}
	
	/**
	 * Check whether the given factor can be used as any of the factors that determine scramble behaviour.
	 * 
	 * @param factor
	 * 		The factor to check
	 * @return True if the given value is in the range of [0,1]
	 * 		| factor >= 0 && factor <= 1
	 */
	public static boolean isValidFactor(float factor){
		return factor >= 0.0f && factor <= 1.0f;
	}

	/**
	 * The rate at which squares are swapped during a scramble.
	 * @invar A float between 0.0 and 1.0 (inclusive)
	 */
	private static float permutation_factor = 0.1f;
	
	/**
	 * The rate at which new squares are created during a scramble.
	 * @invar A float between 0.0 and 1.0 (inclusive)
	 */
	private static float fill_factor = 0.2f;
	
	/**
	 * The rate at which Rocks collapse during a scramble.
	 * @invar A float between 0.0 and 1.0 (inclusive)
	 */
	private static float collapse_factor = 0.15f;
	
}
