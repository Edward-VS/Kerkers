package dungeons;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import be.kuleuven.cs.som.annotate.Basic;
import dungeons.util.Direction;
import dungeons.util.Point;
import dungeons.util.SquareIterator;

/**
 * A SquareDungeons is a Dungeon that contain references of squares (in a direct way <-> CompositeDungeon). All squares have
 * as position relative to the origin of the SquareDungeon they are stored in.
 * 
 * The total memory that is used to store squares in a SquareDungeon is O(n) where n is the amount of effective square.
 * 
 * @see Documentation of Dungeon is also valid.
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

public class SquareDungeon extends Dungeon{

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
	public SquareDungeon(Point maximumDimensions) throws IllegalMaximumDimensionsException {
		super(maximumDimensions);
		squares = new TreeMap<Point, Square>();
	}
	
	
	/*****************************************************************
	 * SQUARE MANAGEMENT - TOTAL
	 *****************************************************************/
	
	/**
	 * Retrieve all the squares that belong to this dungeon in a direct or indirect way.
	 */
	@Override @Basic
	public Collection<Square> getSquares() {
		return squares.values();
	}

	/**
	 * Get the square at the given position relative to the origin of this dungeon.
	 * 
	 * @param position
	 * 		A position relative to the origin of this dungeon.
	 * 
	 * @see Dungeon
	 */
	@Override @Basic
	public Square getSquareAt(Point position) {
		// get() does an equality check, not a reference check
		return squares.get(position);
	}

	/**
	 * Get the number of squares that are currently directly or indirectly in this dungeon.
	 */
	@Override @Basic
	public int getNbSquares() {
		return squares.size();
	}

	/**
	 * Get the position of the given square relative to the origin of this dungeon.
	 * 
	 * @param square
	 * 		The square to get the position of.
	 */
	@Override @Basic
	public Point getPositionOfSquare(Square square) {
		for(Map.Entry<Point, Square> e: squares.entrySet()){
			if(e.getValue() == square)
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
	 * @see Dungeon
	 */
	@Override
	public boolean hasAsSquare(Square square) {
		return squares.containsValue(square);
	}

	/**
	 * Add the given square to this dungeon at the given position relative to the origin of this dungeon,
	 * and make sure that the obstacles in the given directions are destroyed (if possible).
	 * 
	 * @see Dungeon
	 */
	@Override
	public void addAsSquareAt(Square square, Point position, Direction... destroyObstacles) {
		if(!canHaveAsSquareAt(square, position) && square != null)
			return;
		
		// TODO the replaced square must be removed properly
		
		squares.put(position, square);
		
		Point posInRoot = position.add(getPositionInRoot()); // position of square in root in my root
		Dungeon root = getRootDungeon(); // my root
		
		//register neighbors relative to my root (the square might have neighbors that are not stored in this
		for(Direction d:Direction.values()){
			Square n = root.getSquareInDirectionOfPosition(posInRoot, d);
			if(n != null)
				square.registerNeighbor(n, d);
		}
		
		square.fixObstacles(destroyObstacles);
		// TODO whenever a neighbor is registered, obstacels must be updated (registerNeighbor() may not be a raw method
	}

	/**
	 * Remove the square at the given position from this dungeon if it is present.
	 * 
	 * @see Dungeon
	 */
	@Override
	public void removeAsSquareAt(Point position) {
		if(position != null){
			Square removed = squares.remove(position);
			if(removed != null){
				for(Direction d: Direction.values())
					removed.removeNeighbor(d);
			}
		}
	}

	/**
	 * Returns an iterator that iterates over all the squares in this dungeon from front(y)
	 * to back, left(x) to right and bottom(z) to top.
	 * 
	 * @note return type of overwritten method is narrower than 
	 */
	@Override
	public SquareIterator iterator() {
		return new SquareIterator(){
			//TODO doc
			@Override
			public boolean hasNext() {
				return master.hasNext();
			}

			@Override
			public Square next() {
				return master.next().getValue();
			}
			
			@Override
			public SimpleEntry<Point, Square> nextEntry() {
				Map.Entry<Point, Square> e = master.next();
				return new SimpleEntry<Point, Square>(e.getKey(), e.getValue());
			}
			
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
	private TreeMap<Point, Square> squares;
	
	
	
	
	/*****************************************************************
	 * TERMINATION
	 *****************************************************************/
	
	/**
	 * Terminate this dungeon.
	 * 
	 * @see Dungeon
	 */
	public void terminate(){
		isTerminated = true;
		
		TreeMap<Point, Square> oldSquares = new TreeMap<Point, Square>(squares);
		for(Map.Entry<Point, Square> e: oldSquares.entrySet()){
			removeAsSquareAt(e.getKey());
		}
		
		assert getNbSquares() == 0;
		
		CompositeDungeon oldParent = getParentDungeon();
		if(oldParent != null){
			oldParent.removeAsSubDungeon(this);
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
		if(!position.isEqualOrBiggerThanPoint(getMaximumDimensions())
				&& position.isEqualOrBiggerThanValue(0))
			return this;
		return null;
	}

	
}
