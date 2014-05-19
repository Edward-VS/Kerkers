package dungeons.dungeon;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;
import dungeons.exception.IllegalDungeonException;
import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.square.Square;
import dungeons.util.Direction;
import dungeons.util.Point;
import dungeons.util.SquareIterator;

/**
 * <p>An implementation of the dungeon interface.</p>
 * 
 * @see Dungeon: documentation and invariants from that class are still valid here.
 * 
 * @invar A dungeon must always have maximum dimensions that are smaller or equal to the maximum allowed dimensions (5000,5000,5000), bigger
 *		than (0,0,0), in compliance with the dimensions of its parent dungeon (if any) and legal in any other way.
 *		| hasProperMaximumDimensions()
 * @invar All the squares in a dungeon must be in a legal state.
 * 		| hasProperSquares()
 * @invar The parent of a dungeon (if any) must be in a legal state, and the association between its parent dungeon must be properly set.
 * 		| hasProperParentDungeon()
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 */

public abstract class AbstractDungeon implements Dungeon {

	/**
	 * Construct a new dungeon with the given maximum dimensions.
	 * 
	 * @param maximumDimensions
	 *		The dimensions outside of which no squares can be added.
	 * @post The maximum dimensions of this dungeon are set to the given maximum dimensions
	 * 		| new.getMaximumDimensions() == maximumDimensions
	 * @throws IllegalMaximumDimensionsException
	 * 		[MUST] The given maximum dimensions are not legal
	 * 		| !canHaveAsMaximumDimensions(maximumDimensions)
	 */
	protected AbstractDungeon(Point maximumDimensions) throws IllegalMaximumDimensionsException {
		setMaximumDimensions(maximumDimensions); // throws exception
	}

	/**
	 * Construct a new dungeon with maximum dimensions (1,1,1). (it can contain one square)
	 * 
	 * @effect A new dungeon is constructed with maximum dimensions (1,1,1)
	 * 		| this(Point.CUBE)
	 */
	protected AbstractDungeon() throws IllegalMaximumDimensionsException {
		this(Point.CUBE);
	}

	
	
	/*****************************************************************
	 * DIMENSIONS - DEFENSIVE
	 *****************************************************************/

	/**
	 * Get the maximum dimensions of this dungeon.
	 * 
	 * @see Dungeon
	 */
	@Basic
	public Point getMaximumDimensions() {
		return maximumDimensions;
	}

	/**
	 * Check whether the given maximum dimensions are valid for this dungeon.
	 * 
	 * @param maximumDimensions
	 *		A point that represents the maximum dimensions.
	 * 
	 * @see Dungeon
	 */
	@Raw
	public boolean canHaveAsMaximumDimensions(Point maximumDimensions) {
		if (maximumDimensions == null)
			return false;
		if (!maximumDimensions.isEqualOrSmallerThanValue(ABSOLUTE_MAXIMUM_DIMENSIONS))
			return false;
		if (!maximumDimensions.isEqualOrBiggerThanValue(1))
			return false;
		if (getParentDungeon() != null
				&& getParentDungeon()
						.overlapsWithOtherSubDungeon(this, maximumDimensions, getParentDungeon().getPositionOfSubDungeon(this)))
			return false;
		return true;
	}

	/**
	 * Check whether this dungeon has proper maximum dimensions.
	 * 
	 * @see Dungeon
	 */
	public boolean hasProperMaximumDimensions() {
		return canHaveAsMaximumDimensions(getMaximumDimensions());
	}

	/**
	 * Check whether the given maximum dimension are legal as new maximum dimensions.
	 * 
	 * @param maximumDimensions
	 *		A point that represents the new maximum dimensions.
	 * 
	 * @see Dungeon
	 */
	public boolean canAcceptAsNewMaximumDimensions(Point maximumDimensions) {
		if (canHaveAsMaximumDimensions(maximumDimensions) && maximumDimensions.isEqualOrBiggerThanPoint(this.maximumDimensions))
			return true;
		return false;
	}

	/**
	 * Set the new maximum dimensions of this dungeon to the given maximum dimensions.
	 * 
	 * @param maximumDimensions
	 *		The new maximum dimensions for this dungeon
	 * 
	 * @see Dungeon
	 */
	public void changeMaximumDimensions(Point maximumDimensions) throws IllegalMaximumDimensionsException {
		if (!canAcceptAsNewMaximumDimensions(maximumDimensions))
			throw new IllegalMaximumDimensionsException(maximumDimensions);
		this.maximumDimensions = maximumDimensions;
	}

	/**
	 * Set the new maximum dimensions of this dungeon to the given maximum dimensions.
	 * 
	 * @param maximumDimensions
	 *		The new maximum dimensions for this dungeon
	 * 
	 * @see Dungeon
	 */
	@Model
	@Raw
	protected void setMaximumDimensions(Point maximumDimensions) throws IllegalMaximumDimensionsException {
		if (!canHaveAsMaximumDimensions(maximumDimensions))
			throw new IllegalMaximumDimensionsException(maximumDimensions);
		this.maximumDimensions = maximumDimensions;
	}

	/**
	 * The maximum dimensions of this dungeon.
	 * @invar Exclusive representation of 3 integer coordinates.
	 * E.g. (20,20,20) means that the maximum (zero-based) coordinate of a square is (19,19,19)
	 */
	private Point maximumDimensions;

	/*****************************************************************
	 * SQUARE MANAGEMENT - TOTAL
	 *****************************************************************/

	/**
	 * Retrieve all the squares that belong to this dungeon in a direct or indirect way.
	 */
	@Basic
	public abstract Collection<Square> getSquares();

	/**
	 * Get the square at the given position relative to the origin of this dungeon.
	 * 
	 * @param position
	 * 		A position relative to the origin of this dungeon.
	 * 
	 * @see Dungeon
	 */
	@Basic
	public abstract Square getSquareAt(Point position);

	/**
	 * Get the number of squares that are currently directly or indirectly in this dungeon.
	 */
	@Basic
	public abstract int getNbSquares();

	/**
	 * Get the position of the given square relative to the origin of this dungeon.
	 * 
	 * @param square
	 * 		The square to get the position of.
	 */
	@Basic
	public abstract Point getPositionOfSquare(Square square);

	/**
	 * Get the square that is located next to the given position in the given direction.
	 * 
	 * @param position
	 * 		The position next to which the square should be located.
	 * @param direction
	 * 		The direction in which to look.
	 * 
	 * @see Dungeon
	 */
	public Square getSquareInDirectionOfPosition(Point position, Direction direction) {
		return getSquareAt(direction.getPointInThisDirectionOf(position));
	}

	/**
	 * Retrieve the number of positions within the maximum dimensions of this dungeon that does
	 * not contain a square, but that can contain one.
	 * 
	 * @see Dungeon
	 */
	public int getEmptrySpace() {
		int res = 0;
		// @invar "res" is always positive or zero
		for (SquareDungeon d : getLevelsAndShafts()) {
			res += (d.getMaximumDimensions().size() - d.getSquares().size());
		}
		return res;
	}

	/**
	 * Check whether this dungeon van have the given square as one of tis squares.
	 * 
	 * @param square
	 * 		The square to check
	 * 
	 * @see Dungeon
	 */
	public boolean canHaveAsSquare(Square square) {
		if (isTerminated())
			return false;
		if (square == null)
			return false;
		if (square.isTerminated())
			return false;
		Point pos = getPositionOfSquare(square);
		if (pos != null && getDungeonContainingPosition(pos) != square.getDungeon())
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
	 * 
	 * @see Dungeon
	 */
	public boolean canHaveAsSquareAt(Square square, Point position) {
		if (!canHaveAsSquare(square))
			return false;
		if (!position.isEqualOrBiggerThanValue(0))
			return false;
		if (!position.isSmallerThan(maximumDimensions))
			return false;
		if (square.getDungeon() != null && !getPositionOfSquare(square).equals(position))
			return false;
		return true;
	}

	/**
	 * Check if all the squares of this dungeon are in a legal state, at a legal position.
	 * 
	 * @see Dungeon
	 */
	public boolean hasProperSquares() {
		for (Square s : getSquares()) {
			if (!canHaveAsSquareAt(s, getPositionOfSquare(s)))
				return false;
		}
		return true;
	}

	/**
	 * Check whether this dungeon has a square a the given position relative to its origin.
	 * 
	 * @param position
	 * 		The position to look at.
	 * 
	 * @see Dungeon
	 */
	public boolean hasSquareAt(Point position) {
		return getSquareAt(position) != null;
	}

	/**
	 * Check whether this dungeon has the given square as one of its squares.
	 * 
	 * @param square
	 * 		The square to check for.
	 * 
	 * @see Dungeon
	 */
	public abstract boolean hasAsSquare(Square square);

	/**
	 * Add the given square to this dungeon at the given position relative to the origin of this dungeon,
	 * and make sure that the obstacles in the given directions are destroyed (if possible).
	 * 
	 * @param square
	 * 		The square to add to this dungeon.
	 * @param position
	 * 		The position to add the given square to relative to the origin of this dungeon.
	 * @param destroyObstacles
	 * 		An array of directions in which obstacles should destroy
	 * 
	 * @see Dungeon
	 */
	public abstract void addAsSquareAt(Square square, Point position, Direction... destroyObstacles);

	/**
	 * Remove the square at the given position from this dungeon if it is present, and update its surroundings.
	 *
	 * @param square
	 * 		The square to remove from this dungeon
	 * 
	 * @see Dungeon
	 */
	public abstract void removeAsSquareAt(Point position);

	/**
	 * Swap the square at the given position in this dungeon with the given square.
	 * 
	 * @param position
	 * 		The position of the square in this dungeon to swap
	 * @param other
	 * 		The square to swap the square at he given position in this dungeon with.
	 * 
	 * @see Dungeon
	 */
	public abstract void swapSquareAt(Point position, Square other);

	/**
	 * Remove the given square from this dungeon if it is present.
	 * 
	 * @param square
	 * 		The square to remove from this dungeon
	 * 
	 * @see Dungeon
	 */
	public void removeAsSquare(Square square) {
		if (square != null) {
			Point pos = getPositionOfSquare(square);
			removeAsSquareAt(pos);
		}
	}

	/**
	 * Check whether a wall is allowed in the given direction for this dungeon.
	 * 
	 * @param position
	 * 		The position where the wall is next to.
	 * @param direction
	 * 		The direction of the wall relative to the given position
	 * 
	 * @see Dungeon
	 */
	public boolean canHaveWallAt(Point position, Direction direction) {
		return true;
	}

	/**
	 * Build or replace a door next to the given square in the given direction.
	 * 
	 * @param square
	 * 		The square to build the door next to.
	 * @param direction
	 * 		The direction in which to build the door.
	 * @param isOpen
	 * 		Whether the new door is open.
	 * 
	 * @see Dungeon
	 */
	public void buildDoor(Square square, Direction direction, Boolean isOpen) {
		if (square != null)
			square.buildDoorAt(direction, isOpen);
		// might fail, if no door is allowed (total implementation)
	}

	/**
	 * Build or replace a wall next to the given square in the given direction.
	 * 
	 * @param square
	 * 		The square to build the wall next to.
	 * @param direction
	 * 		The direction in which to build the wall.
	 * 
	 * @see Dungeon
	 */
	public void buildWall(Square square, Direction direction) {
		if (square != null && canHaveWallAt(getPositionOfSquare(square), direction))
			square.buildWallAt(direction);
	}

	/**
	 * Build or replace a door next to the square at the given position in the given direction.
	 * 
	 * @param position
	 * 		The position of the square to build the door next to.
	 * @param direction
	 * 		The direction in which to build the door.
	 * @param isOpen
	 * 		Whether the build door is open.
	 * 
	 * @see Dungeon
	 */
	public void buildDoor(Point position, Direction direction, Boolean isOpen) {
		buildDoor(getSquareAt(position), direction, isOpen);
	}

	/**
	 * Build or replace a wall next to the square at the given position in the given direction.
	 * 
	 * @param position
	 * 		The position of the square to build the wall next to.
	 * @param direction
	 * 		The direction in which to build the wall.
	 * 
	 * @see Dungeon
	 */
	public void buildWall(Point position, Direction direction) {
		buildWall(getSquareAt(position), direction);
	}

	/**
	 * Destroy an obstacle in the given direction of the given square.
	 * 
	 * @param square
	 * 		The square to destroy the obstacle from.
	 * @param direction
	 * 		The direction in which to destroy an obstacle
	 * 
	 * @see Dungeon
	 */
	public void destroyObstacle(Square square, Direction direction) {
		if (square != null)
			square.destroyObstacleAt(direction);
	}

	/**
	 * Destroy an obstacle in the given direction of the square with the given position.
	 * 
	 * @param position
	 * 		The position of the square to destroy the obstacle from.
	 * @param direction
	 * 		The direction in which to destroy an obstacle
	 * 
	 * @see Dungeon
	 */
	public void destroyObstacle(Point position, Direction direction) {
		destroyObstacle(getSquareAt(position), direction);
	}

	/**
	 * If possible, let the given square collapse.
	 * 
	 * @param square
	 * 		The square to collapse
	 * 
	 * @see Dungeon
	 */
	public void collapse(Square square) {
		assert getRootDungeon().hasAsSquare(square);

		// redirect to root
		if (getParentDungeon() != null)
			getRootDungeon().collapse(square);
		if (!square.canCollapse())
			return;

		Square above = square.getNeighborAt(Direction.UP);
		Point pos = getPositionOfSquare(square);

		removeAsSquare(square);
		removeAsSquare(above);
		addAsSquareAt(above, pos); // temperature updates

		// 60% next square collapses (if possible)
		Random rand = new Random(System.currentTimeMillis());
		if (rand.nextFloat() > 0.6f && above.getNeighborAt(Direction.DOWN) != null)
			collapse(above.getNeighborAt(Direction.DOWN));
	}

	/**
	 * Convenience method for collapse(square). Make the given position collapse if possible
	 * 
	 * @param position
	 * 		The position to collapse
	 * @effect If this dungeon has a square at the given position, then try to collapse it.
	 * 		| if getSquareAt(position) != null
	 * 		|	then collapse(getSquareAt(position))
	 */
	public void collapse(Point position) {
		Square s = getSquareAt(position);
		if(s!= null)
			collapse(s);
	}

	/**
	 * Returns an iterator that iterates over all the squares in this dungeon from front(y)
	 * to back, left(x) to right and bottom(z) to top.
	 */
	public abstract SquareIterator iterator();

	
	
	/*****************************************************************
	 * TERMINATION
	 *****************************************************************/

	/**
	 * Check whether this dungeon is terminated.
	 */
	@Basic
	public boolean isTerminated() {
		return isTerminated;
	}

	/**
	 * Terminate this dungeon.
	 * 
	 * @see Dungeon
	 */
	public abstract void terminate();

	/**
	 * Set the termination state of this dungeon to the given termination state
	 * 
	 * @param isTerminated
	 * 		Whether this dungeon is terminated
	 * @post The termination state of this dungeon is the given state.
	 * 		| new.isTerminated() == isTerminated
	 */
	protected void setTerminated(boolean isTerminated) {
		this.isTerminated = isTerminated;
	}

	/**
	 * Whether this dungeon is terminated (when it is set to true, it can never be changed)
	 */
	private boolean isTerminated;

	
	
	/*****************************************************************
	 * PARENT DUNGEON 
	 *****************************************************************/

	/**
	 * Get the parent dungeon of this dungeon.
	 */
	@Basic
	public CompositeDungeon getParentDungeon() {
		return parentDungeon;
	}

	/**
	 * Set the given dungeon to be the parent of this dungeon.
	 * 
	 * @param parentDungeon
	 * 		A composite dungeon.
	 * 
	 * @see Dungeon
	 * @note a similar construction is used in Square for its dungeon management
	 */
	@Raw
	public void setParentDungeon(CompositeDungeon parentDungeon) throws IllegalDungeonException {
		// NOTE: canHaveAsParentDungeon is not used, because that method does not allow null values,
		// but we want to be able to set the parent of this dungeon to null if it is removed from it, etc...
		if (parentDungeon != null && !parentDungeon.hasAsSubDungeon(this))
			throw new IllegalDungeonException(parentDungeon);
		if (parentDungeon == null && getParentDungeon() != null && getParentDungeon().hasAsSubDungeon(this))
			throw new IllegalDungeonException(parentDungeon);
		this.parentDungeon = parentDungeon;
	}

	/**
	 * Check whether this square can have the given dungeon as its dungeon
	 * 
	 * @param dungeon
	 * 		The dungeon to check
	 * 
	 * @see Dungeon
	 */
	@Raw
	public boolean canHaveAsParentDungeon(CompositeDungeon parentDungeon) {
		return ((parentDungeon == null) || parentDungeon.canHaveAsSubDungeon(this));
	}

	/**
	 * Check whether this dungeon has a proper parent dungeon
	 * 
	 * @see Dungeon
	 */
	@Raw
	public boolean hasProperParentDungeon() {
		return (canHaveAsParentDungeon(getParentDungeon()) && (getParentDungeon() == null || getParentDungeon().hasAsSubDungeon(this)));
	}

	/**
	 * Retrieve the root dungeon of this dungeon.
	 * 
	 * @see Dungeon
	 */
	public Dungeon getRootDungeon() {
		if (getParentDungeon() != null)
			return getParentDungeon().getRootDungeon();
		else
			return this;
	}

	/**
	 * Check whether this dungeon is equal to the given dungeon or is a direct
	 * or indirect parent of the given dungeon.
	 * 
	 * @see Dungeon
	 */
	@Raw
	public boolean equalsOrIsDirectOrIndirectParentOf(@Raw Dungeon dungeon) {
		return ((this == dungeon) || ((dungeon != null) && (equalsOrIsDirectOrIndirectParentOf(dungeon.getParentDungeon()))));
	}

	/**
	 * Get the position of this dungeon relative to the origin of its root.
	 * 
	 * @see Dungeon
	 */
	public Point getPositionInRoot() {
		if (getParentDungeon() != null)
			return getParentDungeon().getPositionInRoot().add(getParentDungeon().getPositionOfSubDungeon(this));
		return Point.ORIGIN;
	}

	private CompositeDungeon parentDungeon;

	
	
	/*****************************************************************
	 * SUB DUNGEONS (introduced here instead of CompositeDungeon, to not violate LSP)
	 *****************************************************************/

	/**
	 * Get all the levels and shafts that are contained within this dungeon.
	 * 
	 * @see Dungeon
	 */
	@Basic
	public abstract List<SquareDungeon> getLevelsAndShafts();

	/**
	 * Retrieve the square dungeon that contains the given position.
	 * 
	 * @param position
	 * 		Some position relative to the origin of this dungeon.
	 * 
	 * @see Dungeon
	 */
	public abstract SquareDungeon getDungeonContainingPosition(Point position);

}
