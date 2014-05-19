package dungeons.dungeon;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import be.kuleuven.cs.som.annotate.Basic;
import dungeons.exception.IllegalDungeonException;
import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.square.Square;
import dungeons.util.Direction;
import dungeons.util.Point;
import dungeons.util.SquareIterator;

/**
 * <p>A SquareDungeons is a Dungeon that contain references of squares (in a direct way <-> CompositeDungeon). All squares have
 * as position relative to the origin of the SquareDungeon they are stored in.</p>
 * 
 * <p>The total memory that is used to store squares in a SquareDungeon is O(n) where n is the amount of effective square.</p>
 * 
 * @see Documentation of AbstractDungeon is also valid.
 * 
 * @invar A SquareDungeon must always have maximum dimensions that are smaller or equal to the maximum allowed dimensions,
 * 		bigger than (0,0,0), in compliance with the dimensions of its parent dungeon (if any) and legal in any other way.
 *		| hasProperMaximumDimensions()
 * @invar All the squares in a SquareDungeon must be in a legal state.
 * 		| hasProperSquares()
 * @invar The parent of a SquareDungeon (if any) must be in a legal state, and the association between its parent dungeon must be
 * 		properly set. 
 * 		| hasProperParentDungeon()
 *
 * @author Edward Van Sieleghem & Christof Vermeersch
 */

public abstract class SquareDungeon extends AbstractDungeon {

	/**
	 * Construct a new dungeon with the given maximum dimensions.
	 * 
	 * @param maximumDimensions
	 *		The dimensions outside of which no squares can be added.
	 * @post The maximum dimensions of this dungeon are set to the given maximum dimensions
	 * 		| new.getMaximumDimensions() == maximumDimensions
	 * @throws IllegalArgumentException
	 * 		[MUST] The given maximum dimensions are not legal
	 * 		| !canHaveAsMaximumDimensions(maximumDimensions)
	 */
	protected SquareDungeon(Point maximumDimensions) throws IllegalMaximumDimensionsException {
		super(maximumDimensions);
		squares = new TreeMap<Point, Square>();
	}
	
	/**
	 * Construct a new square dungeon with maximum dimensions (1,1,1). (it can contain one square)
	 * 
	 * @effect A new dungeon is constructed with maximum dimensions (1,1,1)
	 * 		| this(Point.CUBE)
	 */
	protected SquareDungeon() throws IllegalMaximumDimensionsException {
		this(Point.CUBE);
	}

	/*****************************************************************
	 * SQUARE MANAGEMENT - TOTAL
	 *****************************************************************/

	/**
	 * Retrieve all the squares that belong to this dungeon in a direct or indirect way.
	 */
	@Override
	@Basic
	public Collection<Square> getSquares() {
		return squares.values();
	}

	/**
	 * Get the square at the given position relative to the origin of this dungeon.
	 * 
	 * @param position
	 * 		A position relative to the origin of this dungeon.
	 * 
	 * @see AbstractDungeon
	 */
	@Override
	@Basic
	public Square getSquareAt(Point position) {
		// get() does an equality check, not a reference check
		return squares.get(position);
	}

	/**
	 * Get the number of squares that are currently directly or indirectly in this dungeon.
	 */
	@Override
	@Basic
	public int getNbSquares() {
		return squares.size();
	}

	/**
	 * Get the position of the given square relative to the origin of this dungeon.
	 * 
	 * @param square
	 * 		The square to get the position of.
	 */
	@Override
	@Basic
	public Point getPositionOfSquare(Square square) {
		for (Map.Entry<Point, Square> e : squares.entrySet()) {
			if (e.getValue() == square)
				return e.getKey();
		}
		return null;
	}
	
	/**
	 * Check whether this dungeon has the given square as one of its squares.
	 * 
	 * @param square
	 * 		The square to check for.
	 * 
	 * @see AbstractDungeon
	 */
	@Override
	public boolean hasAsSquare(Square square) {
		return squares.containsValue(square);
	}

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
	 * @see AbstractDungeon

	 */
	@Override
	public void addAsSquareAt(Square square, Point position, Direction... destroyObstacles) {
		if (!canHaveAsSquareAt(square, position) && square != null)
			return;
		
		Square removed = squares.put(position, square);
		
		// Neighbors of squares are according to the root dungeon, so we get some information of our root.
		Point posInRoot = position.add(getPositionInRoot());
		Dungeon root = getRootDungeon();
		
		for (Direction d : Direction.values()) {
			boolean destroyObstacle = Arrays.asList(destroyObstacles).contains(d) || !canHaveWallAt(position, d);

			Square n = root.getSquareInDirectionOfPosition(posInRoot, d);
			if (n != null){
				square.registerNeighbor(n, d, destroyObstacle);
				// neighbor of replaced obstacle is also updated...
			}
		}
		
		try {
			square.setDungeon(this);
			if(removed != null)
				removed.setDungeon(null);
		} catch (IllegalDungeonException e) {
			// never happens
			assert false;
		}
	}
	
	/**
	 * Swap the square at the given position in this dungeon with the given square.
	 * 
	 * @param position
	 * 		The position of the square in this dungeon to swap
	 * @param other
	 * 		The square to swap the square at he given position in this dungeon with.
	 * 
	 * @see AbstractDungeon
	 */
	@Override
	public void swapSquareAt(Point position, Square other){
		if(!hasSquareAt(position) || other == null)
			return;
		
		Dungeon root = getRootDungeon();
		SquareDungeon dunOfOther = root.getDungeonContainingPosition(root.getPositionOfSquare(other));
		
		if(dunOfOther != null){
			Point posOfOther = dunOfOther.getPositionOfSquare(other);
			Square squareAtPos = getSquareAt(position);
			
			dunOfOther.squares.remove(posOfOther);
			squares.remove(position);
			dunOfOther.squares.put(posOfOther, squareAtPos);
			squares.put(position, other);
			
			for(Direction d: Direction.values()){
				other.swapNeighbor(squareAtPos, d);
			}
			
			try {
				other.setDungeon(this);
				squareAtPos.setDungeon(dunOfOther);
			} catch (IllegalDungeonException e) {
				// will never happen
				assert false;
			}
		}
	}
	
	/**
	 * Remove the square at the given position from this dungeon if it is present.
	 * 
	 * @param position
	 * 		The position to remove the square from
	 * 
	 * @see AbstractDungeon
	 */
	@Override
	public void removeAsSquareAt(Point position) {
		if (position != null) {
			Square removed = squares.remove(position);
			if (removed != null) {
				// Neighbors are removed in all directions.
				for (Direction d : Direction.values()){
					removed.removeNeighbor(d);
				}
				try {
					removed.setDungeon(null);
				} catch (IllegalDungeonException e) {
					// never happens
					assert false;
				}
			}
		}
	}

	/**
	 * Returns an iterator that iterates over all the squares in this dungeon from front(y)
	 * to back, left(x) to right and bottom(z) to top.
	 * 
	 * @note The iterator throws a ConcurrentModificationException when the internal TreeMap is modified
	 * 		at any point in time when the iterator is in use.
	 */
	@Override
	public SquareIterator iterator() {
		return new SquareIterator() {
			/**
			 * Check whether this iterator has a next element
			 */
			@Override
			public boolean hasNext() {
				return master.hasNext();
			}

			/**
			 * Retrieve the next element in iteration.
			 * @return a square that also belong to this dungeon.
			 * 		| hasAsSquare(result)
			 */
			@Override
			public Square next() {
				return master.next().getValue();
			}

			/**
			 * Retrieve the next entry in iteration.
			 * @return A key value pair that hold a square and its position in this dungeon.
			 * 		| getPositionOfSquare(result.getValue()) == result.getKey()
			 */
			@Override
			public SimpleEntry<Point, Square> nextEntry() {
				Map.Entry<Point, Square> e = master.next();
				return new SimpleEntry<Point, Square>(e.getKey(), e.getValue());
			}

			/**
			 * The remove operation is not implemented
			 */
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			private Iterator<Map.Entry<Point, Square>> master = squares.entrySet().iterator();
		};
	}

	/**
	 * @invar Squares are stored in a map entry with their position relative to the origin of this dungeon as key.
	 * 		The ordering of the tree is in accordance to the <code>compareTo()</code> method of Point.
	 * 		The squares map should NEVER contain a null value.
	 */
	protected TreeMap<Point, Square> squares;

	
	
	/*****************************************************************
	 * TERMINATION
	 *****************************************************************/

	/**
	 * Terminate this dungeon.
	 * 
	 * @see AbstractDungeon
	 */
	public void terminate() {
		setTerminated(true);

		TreeMap<Point, Square> oldSquares = new TreeMap<Point, Square>(squares);
		for (Map.Entry<Point, Square> e : oldSquares.entrySet()) {
			removeAsSquareAt(e.getKey());
		}

		assert getNbSquares() == 0;

		if (getParentDungeon() != null) {
			getParentDungeon().removeAsSubDungeon(this);
		}

		// When this method is left, all invariant are met
		assert hasProperSquares();
		assert hasProperParentDungeon();
	}

	/*****************************************************************
	 * SUB DUNGEONS
	 *****************************************************************/

	/**
	 * Get all the levels and shafts that are contained within this dungeon.
	 */
	@Override @Basic
	public List<SquareDungeon> getLevelsAndShafts() {
		ArrayList<SquareDungeon> res = new ArrayList<SquareDungeon>(1);
		res.add(this);
		return res;
	}

	/**
	 * Retrieve the square dungeon that contains the given position.
	 * 
	 * @param position
	 * 		Some position relative to the origin of this dungeon.
	 */
	@Override
	public SquareDungeon getDungeonContainingPosition(Point position) {
		if (!position.isEqualOrBiggerThanPoint(getMaximumDimensions()) && position.isEqualOrBiggerThanValue(0))
			return this;
		return null;
	}

}
