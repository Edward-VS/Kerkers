package dungeons;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Raw;
import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.exception.IllegalSubDungeonAtPositionException;
import dungeons.exception.IllegalSubDungeonException;
import dungeons.util.Direction;
import dungeons.util.Point;
import dungeons.util.SquareIterator;

/**
 * A CompositeDungeon is a Dungeon that contains other dungeons as its sub dungeons. Composite dungeons can not
 * contain squares in a direct way (they don't store references of squares themselves), but all the squares of
 * its subdungeons also belong to it. So, if you request all squares in a composite dungeon, a collection is returned
 * with all the squares of its sub dungeons.
 * 
 * The position of a square equals the position of its sub dungeon relative to the origin of the composite dungeon,
 * added to the position of the square relative to the origin of the sub dungeon it belong to.
 * 
 * Each sub-dungeon is represented by 'a box' in space given its maximum dimensions and the position it is located at.
 * 'Boxes' of different sub dungeons may never overlap in a CompositeDungeon.
 * 
 * Composite dungeons and their sub dungeons are linked by a partial bidirectional association. This allows for sub-
 * dungeons to know there parent, and thereby know the root dungeon of the hierarchy they belong to.
 * 
 * The reference of a square is not stored in a composite dungeon. But if a square is added to a dungeon that is a 
 * sub dungeon of a composite dungeon, then that square 'belongs to' that composite dungeon (or in general, the root 
 * of the dungeon hierarchy). This means that a square is not restricted to refer to 'neighbors' as squares that belong to the
 * same dungeon where there reference is stored in. Adding and removing squares anywhere in the dungeon hierarchy takes
 * this principle in account. 
 * 
 * Naturally squares can only be added (using the add method in CompositeDungeon) at positions that are contained in a sub
 * dungeon of that CompositeDungeon (since the square reference must be stored somewhere...).
 * 
 * Adding and removing sub dungeons can be done in constant time although computing the new environment of all the squares
 * in the sub dungeon might be more time demanding.
 * 
 * @see Documentation of Dungeon is also valid.
 *
 *
 * @invar A dungeon must always have maximum dimensions that are smaller or equal to the maximum allowed dimensions,
 * 		bigger than (0,0,0), in compliance with the dimensions of its parent dungeon (if any) and legal in any other way.
 *		| hasProperMaximumDimensions()
 * @invar All the squares in a dungeon must be in a legal state.
 * 		| hasProperSquares()
 * @invar The parent of a dungeon (if any) must be in a legal state, and the association between its parent dungeon must be
 * 		properly set. 
 * 		| hasProperParentDungeon()
 * @invar A composite dungeon only contains sub dungeons that entirely fit in it and don't overlap. Also its sub dungeons
 * 		must be in a legal state.
 * 		| hasProperSubDungeons()
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 *
 */
public class CompositeDungeon extends Dungeon{

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
	public CompositeDungeon(Point maximumDimensions) throws IllegalMaximumDimensionsException {
		super(maximumDimensions);
		subDungeons = new HashMap<Point, Dungeon>();
	}
	
	
	/*****************************************************************
	 * DIMENSIONS - DEFENSIVE
	 *****************************************************************/
	
	/**
	 * Check whether the given dungeon overlaps with any other sub-dungeon in this composite dungeon
	 * if it would have the given maximum dimensions and position.
	 * 
	 * @param subDungeon
	 * 		A dungeon that might be a sub-dungeon of this dungeon.
	 * @param maximumDimensions
	 * 		The proposed maximum dimensions of the given dungeon 
	 * @param position
	 * 		The proposed position of the given dungeon
	 * @pre The given dungeon is an effective dungeon
	 * 		| dungeon != null
	 * @pre The given maximumDimensions and position are effective, and positive
	 * 		| maximumDimensions != null && maximumDimensions.isEqualOrBiggerThanValue(1)
	 * 		| && position != null && position.isEqualOrBiggerThanValue(0)
	 * @pre The given dungeon belong to this dungeon, or to no dungeon at all
	 * 		| dungeon.getParentDugneon() == this || dungeon.getParentDugneon() == null
	 * @return True if the given dungeon with the given dimensions and position overlaps with at least one
	 * 		sub-dungeon in this composite dungeon that is not the dungeon itself.
	 * 		| result ==
	 * 		| 	for some d in getSubDungeons():
	 * 		|		d != dungeon
	 * 		|		&& Point.overlap( getPositionOfSubDungeon(d), getPositionOfSubDungeon(d).add(d.getMaximumDimensions()),
	 * 		|			position, position.add(maximumDimensions) )
	 */
	public boolean overlapsWithOtherSubDungeon(Dungeon dungeon, Point maximumDimensions, Point position){
		for (Map.Entry<Point, Dungeon> e: subDungeons.entrySet()){
			if(e.getValue() != dungeon){
				Point otherMax = e.getValue().getMaximumDimensions();
				Point otherPos = e.getKey();
				
				if(Point.overlap(otherPos, otherPos.add(otherMax), position, position.add(maximumDimensions)))
					return true;
			}
		}
		return false;
	}
	
	
	
	
	/*****************************************************************
	 * SQUARE MANAGEMENT - TOTAL
	 *****************************************************************/

	/**
	 * Retrieve all the squares that belong to this dungeon in a direct or indirect way.
	 */
	@Override @Basic
	public Collection<Square> getSquares() {
		ArrayList<Square> res = new ArrayList<Square>();
		for (Dungeon d: subDungeons.values()){
			res.addAll(d.getSquares());
		}
		return res;
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
		for (Map.Entry<Point, Dungeon>e: subDungeons.entrySet()){
			if(position.isEqualOrBiggerThanPoint(e.getKey())){
				Square s = e.getValue().getSquareAt(position.subtract(e.getKey()));
				if(s!=null)
					return s;
			}
		}
		return null;
	}

	/**
	 * Get the number of squares that are currently directly or indirectly in this dungeon.
	 */
	@Override @Basic
	public int getNbSquares() {
		int res = 0;
		for (Dungeon d: subDungeons.values()){
			res += d.getNbSquares();
		}
		return res;
	}

	/**
	 * Get the position of the given square relative to the origin of this dungeon.
	 * 
	 * @param square
	 * 		The square to get the position of.
	 */
	@Override @Basic
	public Point getPositionOfSquare(Square square) {
		for (Map.Entry<Point, Dungeon>e: subDungeons.entrySet()){
			Point p = e.getValue().getPositionOfSquare(square);
			if(p!=null)
				return p.add(e.getKey());
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
		for (Dungeon d: subDungeons.values()){
			boolean hasIt = d.hasAsSquare(square);
			if(hasIt)
				return true;
		}
		return false;
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
	@Override @Raw
	public boolean canHaveAsSquareAt(Square square, Point position){
		if(!super.canHaveAsSquareAt(square, position))
			return false;
		if(getDungeonContainingPosition(position) == null)
			return false;
		return true;
	}
	
	/**
	 * Add the given square to this dungeon at the given position relative to the origin of this dungeon,
	 * and make sure that the obstacles in the given directions are destroyed (if possible).
	 * 
	 * @see Dungeon
	 * 
	 * @note This method just redirects to a leaf dungeon where the square should actually be added. That leaf
	 * 		dungeon will then take information of its root about neighbors for the added square.
	 */
	@Override
	public void addAsSquareAt(Square square, Point position, Direction... destroyObstacles) {
		//NOTE: no method is called to check whether the square CAN actually be places on the given position. This is not
		// necessary, because addAsSquareAt in SquareDungeon does all the required tests and if no valid squareDungeon is found to add 
		// the square to in the first place, the square will just not be added (total implementation)
		for(Map.Entry<Point, Dungeon> e: subDungeons.entrySet()){
			Dungeon subDungeon = e.getValue();
			Point subDunPos = e.getKey();
			if(position.isSmallerThan(subDungeon.getMaximumDimensions().add(subDunPos)) && position.isEqualOrBiggerThanPoint(subDunPos)){
				subDungeon.addAsSquareAt(square, position.subtract(subDunPos), destroyObstacles);
				break;
			}
			// this would be the place to add an exception that no square was added (not necessary).
		}
		square.setDungeon(this, position);
	}

	/**
	 * Remove the square at the given position from this dungeon if it is present.
	 * 
	 * @see Dungeon
	 * 
	 * @note Similar construction to addAsSquareAt()
	 */
	@Override
	public void removeAsSquareAt(Point position) {
		for(Map.Entry<Point, Dungeon> e: subDungeons.entrySet()){
			Dungeon subDungeon = e.getValue();
			Point subDunPos = e.getKey();
			if(position.isSmallerThan(subDungeon.getMaximumDimensions().add(subDunPos)) && position.isEqualOrBiggerThanPoint(subDunPos)){
				subDungeon.removeAsSquareAt(position.subtract(subDunPos));
				break;
			}
			// this would be the place to add an exception to note the user that no square was removed (not necessary).
		}
	}

	
	
	
	/*****************************************************************
	 * TERMINATION
	 *****************************************************************/
	
	/**
	 * Terminate this dungeon.
	 * 
	 * @see Dungeon
	 */
	@Override
	public void terminate() {
		// NOTE: not very efficient, but termination is not a real requirement of the project assignment.
		isTerminated = true;
		
		HashMap<Point, Dungeon> oldSubDungeons = new HashMap<Point, Dungeon>(subDungeons);
		for(Dungeon d: oldSubDungeons.values()){
			removeAsSubDungeon(d);
		}
		
		assert getNbSubDungeons() == 0;
		
		if(getParentDungeon() != null){
			getParentDungeon().removeAsSubDungeon(this);
		}
		
		// When this method is left, all invariant are met
		assert hasProperSquares();
		assert hasProperParentDungeon();
		assert hasProperSubDungeons();
		
	}

	/**
	 * Returns an iterator that iterates over all the squares in this dungeon from front(y)
	 * to back, left(x) to right and bottom(z) to top (relative to the coordinate system of this dungeon).
	 * 
	 * @return A new iterator
	 */
	@Override
	public SquareIterator iterator() {
		return new CompositeDungeon.Iterator(); 
	}
	
	/**
	 * An iterator that iterates over the square of a CompositeDungeon.
	 * 
	 * Each sub-dungeon will have a similar iterator, but it is not said that ALL the elements of one sub
	 * dungeon must come before those of ALL other sub dungeons in the iteration over this dungeon.
	 * It is certain though, that the next element in iteration will be one of the front elements of the iterators
	 * of the sub-dungeons. As a result, this iterator 'grabs' all the elements at the front of the sub-dungeon-iterators,
	 * and return the smallest (using compareTo() of the position of squares relative to the origin of this dungeon.)
	 */
	public class Iterator implements SquareIterator {
		//TODO doc
		// Iterators of sub-dungeons
		SquareIterator[] its;
		// Positions of sub-dungeons
		Point[] subPos;
		// the current next entries of the different iterators (must be arraylist instead of array because of 'generic' error.)
		ArrayList<SimpleEntry<Point, Square>> nexts;
		
		public Iterator(){
			int i = 0;
			int nbSubDun = getNbSubDungeons();
			
			its = new SquareIterator[nbSubDun];
			subPos = new Point[nbSubDun];
			nexts = new ArrayList<SimpleEntry<Point, Square>>(nbSubDun);
			
			for(Map.Entry<Point, Dungeon> e:subDungeons.entrySet()){
				SquareIterator it = e.getValue().iterator();
				its[i] = it;
				subPos[i] = e.getKey();
				if(it.hasNext())
					nexts.add(it.nextEntry());
				else
					nexts.add(null); // this line must be here, since we use 'add'...
				i++;
			}
		}
		
		@Override
		public boolean hasNext() {
			for(SquareIterator it:its){
				if(it.hasNext())
					return true;
			}
			return false;
		}

		@Override
		public Square next() {
			return nextEntry().getValue();
		}
		
		/**
		 * 
		 * @note When next() is called on an iterator, the value must be stored in this iterator if it is not used as
		 * the return value. Else it gets lost, and the iteration is incomplete.
		 */
		@Override
		public SimpleEntry<Point, Square> nextEntry() {
			SimpleEntry<Point, Square> bestEntry = null;
			int bestIndex = -1;
			int i = 0;
			for(SquareIterator it: its){
				// complete the nexts list (if needed) with your next entry if you have one.
				if(nexts.get(i) == null && it.hasNext()){
					SimpleEntry<Point, Square> next = it.nextEntry();
					nexts.set(i, new SimpleEntry<Point, Square>(next.getKey().add(subPos[i]), next.getValue()));
				}
				// Do you turn out to have a next entry?
				if(nexts.get(i) != null){
					if(bestEntry == null || nexts.get(i).getKey().compareTo(bestEntry.getKey()) > 0){
						bestEntry = nexts.get(i);
						bestIndex = i;
					}
				}
				i++;
			}
			if(bestIndex != -1){
				// the value that is returned should not be considered a 'next value' of any iterator anymore.
				nexts.set(bestIndex, null);
				return bestEntry;
			}
			throw new NoSuchElementException();
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	
	
	/*****************************************************************
	 * SUB DUNGEONS - DEFENSIVE
	 *****************************************************************/
	
	/**
	 * Get a list of sub-dungeons that are in this dungeon.
	 */
	@Basic
	public Collection<Dungeon> getSubDungeons(){
		return subDungeons.values();
	}
	
	/**
	 * Retrieve the sub dungeon at the given position (if any)
	 * 
	 * @param position
	 * 		The position relative to the origin of this dungeon to look at
	 */
	@Basic
	public Dungeon getSubDungeonAt(Point position){
		// 'get()' check for hash equality -> hashCode() in Point must be overwritten
		return subDungeons.get(position);
	}
	
	/**
	 * Get the number of sub dungeons in this dungeon
	 */
	@Basic
	public int getNbSubDungeons(){
		return subDungeons.size();
	}
	
	/**
	 * Retrieve the position of the given sub dungeon relative to the origin of this dungeon.
	 * 
	 * @param subDungeon
	 * 		The sub dungeon to look for
	 */
	@Basic
	public Point getPositionOfSubDungeon(Dungeon subDungeon){
		for(Map.Entry<Point, Dungeon> e: subDungeons.entrySet()){
			if(e.getValue() == subDungeon)
				return e.getKey();
		}
		return null;
	}
	
	/**
	 * Check whether this dungeon contains the given sub dungeon as one of its sub dungeons.
	 * 
	 * @param dungeon
	 * 		The dungeon to look for.
	 * @return True if at least one sub dungeon of this dungeon equals the given dungeon.
	 * 		| result ==
	 * 		|	for some dungeon in getSubDungeons():
	 * 		|		dungeon == subDungeon
	 */
	public boolean hasAsSubDungeon(Dungeon subDungeon){
		return subDungeons.containsValue(subDungeon);
	}
	
	/**
	 * Check whether the given dungeon is a valid sub dungeon for this composite dungeon.
	 * 
	 * @param dungeon
	 * 		The dungeon to check
	 * @return False if the given dungeon is effective and this dungeon is terminated or
	 * 		the given dungeon is terminated or this dungeon is a direct or indirect parent
	 * 		of the given dungeon.
	 * 		| if dungeon != null && ( dungeon.isTerminated() || isTerminated() || dungeon.equalsOrIsDirectOrIndirectParentOf(this)) )
	 * 		|	then result == false
	 * @return Else true if and only if the given dungeon is not effective or is effective
	 * 		while this dungeon is terminated.
	 * 		| else result == ( dungeon != null || isTerminated() )
	 */
	public boolean canHaveAsSubDungeon(Dungeon dungeon){
		if(dungeon != null && ( dungeon.isTerminated() || isTerminated() || dungeon.equalsOrIsDirectOrIndirectParentOf(this)) )
			return false;
		if( dungeon == null && !isTerminated() )
			return false;
		return true;
	}

	/**
	 * Check whether the given dungeon can be placed on the given position in this dungeon.
	 * 
	 * @param dungeon
	 * 		The dungeon to check.
	 * @param position
	 * 		The proposed position of the given dungeon.
	 * @return If the given square is not legal in this dungeon then false
	 * 		| if !canHaveAsSquare(square)
	 * 		|	then result == false
	 * @return Else if the given position is smaller than zero then false
	 * 		| else if !position.isEqualOrBiggerThanValue(0)
	 * 		|	then result == false
	 * @return Else if the given sub-dungeon with its maximum dimensions does not fit in this dungeon at
	 * 		the given position (it is too large), then false.
	 *  	| else if !position.add(subDungeon.getMaximumDimensions()).isEqualOrSmallerThanPoint(getMaximumDimensions())
	 *  	|	then result == false
	 * @return Else true if and only if each sub dungeon in this composite dungeon that does not
	 * 		equal the given dungeon, does not overlap the given sub dungeon
	 * 		| else result == !overlapsWithOtherSubDungeon(subDungeon, subDungeon.getMaximumDimensions())
	 * @note association related conditions are tested in hasProperSubDungeon(), not in canHaveAsSubDungeonAt()
	 */
	public boolean canHaveAsSubDungeonAt(Dungeon subDungeon, Point position){
		if(!canHaveAsSubDungeon(subDungeon))
			return false;
		if(!position.isEqualOrBiggerThanValue(0))
			return false;
		if(!position.add(subDungeon.getMaximumDimensions()).isEqualOrSmallerThanPoint(getMaximumDimensions()))
			return false;
		if(overlapsWithOtherSubDungeon(subDungeon, subDungeon.getMaximumDimensions(), position))
			return false;
		return true;
	}
	
	/**
	 * Check whether all the sub dungeons of this dungeon are in a legal state, at a legal position.
	 * 
	 * @return True if for each sub dungeon, it is legal at its position.
	 * 		| for each dungeon in getSubDungeons():
	 * 		|	( canHaveAsSquareAtPosition(dungeon, getPositionOfDungeon(dungeon))
	 * 		|		&& dungeon.getParentDungeon() == this )
	 */
	public boolean hasProperSubDungeons(){
		for (Dungeon dungeon : getSubDungeons()){
			if(!canHaveAsSubDungeonAt(dungeon, getPositionOfSubDungeon(dungeon)))
				return false;
			if(dungeon.getParentDungeon() != this)
				return false;
		}
		return true;
	}

	/**
	 * Add the given sub dungeon at the given position in this dungeon
	 * 
	 * @param subDungeon
	 * 		The sub dungeon to add to this composite dungeon
	 * @param position
	 * 		The position where the origin of the sub dungeon will be placed relative to the origin
	 * 		of this dungeon.
	 * @post This dungeon contains the given sub dungeon
	 * 		| new.hasAsSubDungeon(subDungeon)
	 * @post The given sub dungeon refers to this composite dungeon as its
	 * 		parent dungeon
	 * 		| (new subDungeon).getParentDungeon() == this
	 * @post All the squares in the given sub dungeon contain proper neighbors
	 * 		| for each square in subDungeon.getSquares():
	 * 		|	for each direction in Direction.values():
	 * 		|		getRootDungeon().getSquareInDirectionOfPosition(position.add(getPositionInRoot())
	 * 		|						.add(subDungeon.getPositionOfSquare(square)), direction)
	 * 		|			== square.getNeighborAt(direction)
	 * @throws IllegalSubDungeonAtPositionException(subDungeon, position) [MUST]
	 * 		The given sub dungeon can not be placed on the given position.
	 * 		| !canHaveAsSubDungeonAt(subDungeon, position)
	 * @throws IllegalSubDungeonException(subDungeon) [MUST]
	 * 		The given sub dungeon belong to another parent dungeon.
	 * 		| subDungeon.getParentDungeon() != null
	 */
	public void addAsSubDungeonAt(Dungeon subDungeon, Point position) throws IllegalSubDungeonAtPositionException, IllegalSubDungeonException{
		if(!canHaveAsSubDungeonAt(subDungeon, position))
			throw new IllegalSubDungeonAtPositionException(subDungeon, position);
		if(subDungeon.getParentDungeon() != null)
			throw new IllegalSubDungeonException(subDungeon);
		
		subDungeons.put(position, subDungeon);
		subDungeon.setParentDungeon(this);
		
		// See code comment of addAsSquareAt() in SquareDungeon about invariants
		
		
		// Neighbors of squares are according to the root dungeon, so we get some information of our root.
		Point posInRoot = position.add(getPositionInRoot()); // position of square in root in my root
		Dungeon root = getRootDungeon(); // my root
		
		Point maxDim = subDungeon.getMaximumDimensions();
		
		// register new neighbors for squares that are at the exterior of the added sub dungeon.
		SquareIterator it = subDungeon.iterator();
		while(it.hasNext()){
			SimpleEntry<Point, Square> entry= it.nextEntry();
			Square square = entry.getValue();
			Point pos = entry.getKey();
			Point myPosInRoot = pos.add(posInRoot);
			
			// The new sub dungeon was its own root before it was added -> new neighbors should be registered instead of replaced.
			
			// you are as much to the WEST (left) as possible in your sub dungeon
			if(pos.getX() == 0)
				//this may try to register null as neighbor, but that will do nothing.
				square.registerNeighbor(root.getSquareInDirectionOfPosition(myPosInRoot, Direction.WEST),  Direction.WEST);
			if(pos.getX()+1 == maxDim.getX())
				square.registerNeighbor(root.getSquareInDirectionOfPosition(myPosInRoot, Direction.EAST),  Direction.EAST);
			if(pos.getY() == 0)
				square.registerNeighbor(root.getSquareInDirectionOfPosition(myPosInRoot, Direction.SOUTH),  Direction.SOUTH);
			if(pos.getY()+1 == maxDim.getY())
				square.registerNeighbor(root.getSquareInDirectionOfPosition(myPosInRoot, Direction.NORTH),  Direction.NORTH);
			if(pos.getZ() == 0)
				square.registerNeighbor(root.getSquareInDirectionOfPosition(myPosInRoot, Direction.DOWN),  Direction.DOWN);
			if(pos.getZ()+1 == maxDim.getZ())
				square.registerNeighbor(root.getSquareInDirectionOfPosition(myPosInRoot, Direction.UP),  Direction.UP);
			//Note: temperature does not need and update since walls are build between new neighbors
		}
	}
	
	/**
	 * Remove the given sub dungeon form this dungeon.
	 * [Convenience method for removeAsSubDungeon(Point)]
	 * 
	 * @param subDungeon
	 * 		The subDungeon to remove
	 * @effect Remove the sub-dungeon at the position of the given sub-dungeon from this dungeon.
	 * 		| removeAsSubDungeonAt(getPositionOfSubDungeon(subDungeon))
	 * @throws IllegalArgumentException
	 * 		[MUST] The given dungeon is not a sub-dungeon of this dungeon.
	 * 		| !hasAsSubDungeon(subDungeon)
	 */
	public void removeAsSubDungeon(Dungeon subDungeon) throws IllegalArgumentException{
		removeAsSubDungeonAt(getPositionOfSubDungeon(subDungeon));
	}
	
	/**
	 * Remove the sub-dungeon at the given position from this dungeon
	 * 
	 * @param position
	 * 		The position of the sub-dungeon to remove.
	 * @post The subDungeon at the given position is not effective anymore
	 * 		| !new.hasAsSubDungeon(this.getSubDungeonAt(position))
	 * @post All the neighbor association between squares in the sub dungeon at the given position,
	 * 		and squares of the root of this dungeon are broken. This means that squares in the removed
	 * 		sub dungeon can only have neighbors that are also in that sub dungeon.
	 * 		| for each rootSquare in getRootDungeon().getSquares():
	 * 		|	for each subSquare in this.getSubDungeonAt(position).getSquares():
	 * 		|		if rootSquare != subSquare
	 * 		|		then !rootSquare.hasAsNeighbor(subSquare)
	 * @post The sub dungeon at the given position has no parent dungeon anymore. As an effect it 
	 * 		becomes root.
	 * 		| (new this.getSubDungeonAt(position)).getParentDungeon() == null
	 * @throws IllegamArgumentException
	 * 		[MUST] This dungeon has no sub dungeon at the given position.
	 * 		| getSubDungeonAt(position) == null
	 * @note This method perform better than removeAsSubDungeon(Dungeon)
	 */
	public void removeAsSubDungeonAt(Point position) throws IllegalArgumentException{
		Dungeon subDungeon = subDungeons.get(position);
		if(subDungeon == null)
			throw new IllegalArgumentException("The given position does not contain a sub dungeon");
		// Should also check if i can have a non effective sub dungeon at the given position?
		
		// See code comment of addAsSquareAt() in SquareDungeon about invariants
		
		subDungeons.remove(position);
		subDungeon.setParentDungeon(null);
		
		Point maxDim = subDungeon.getMaximumDimensions();
		
		SquareIterator it = subDungeon.iterator();
		while(it.hasNext()){
			SimpleEntry<Point, Square> entry= it.nextEntry();
			Square square = entry.getValue();
			Point pos = entry.getKey();
			
			// We remove neighbors that are at the exterior of the sub dungeon. 
			
			// you are as much to the WEST (left) as possible
			if(pos.getX() == 0)
				square.removeNeighbor(Direction.WEST);
			// you are as much to the EAST (right) as possible
			if(pos.getX()+1 == maxDim.getX())
				square.removeNeighbor(Direction.EAST);
			// you are as much to the SOUTH (front) as possible
			if(pos.getY() == 0)
				square.removeNeighbor(Direction.SOUTH);
			// you are as much to the NORTH (back) as possible
			if(pos.getY()+1 == maxDim.getY())
				square.removeNeighbor(Direction.NORTH);
			// you are as much DOWN as possible
			if(pos.getZ() == 0)
				square.removeNeighbor(Direction.DOWN);
			// you are as much to the NORTH (back) as possible
			if(pos.getZ()+1 == maxDim.getZ())
				square.removeNeighbor(Direction.UP);
		}
	}
	
	
	
	/**
	 * Get all the levels and shafts that are contained within this dungeon.
	 */
	@Override
	public List<SquareDungeon> getLevelsAndShafts() {
		ArrayList<SquareDungeon> res = new ArrayList<SquareDungeon>();
		for (Dungeon d: subDungeons.values()){
			res.addAll(d.getLevelsAndShafts());
		}
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
		for(Map.Entry<Point, Dungeon> e: subDungeons.entrySet()){
			Dungeon subDungeon = e.getValue();
			Point subDunPos = e.getKey();
			if(position.isSmallerThan(subDungeon.getMaximumDimensions().add(subDunPos)) && position.isEqualOrBiggerThanPoint(subDunPos)){
				SquareDungeon res = subDungeon.getDungeonContainingPosition(position.subtract(subDunPos));
				if(res != null)
					return res;
			}
		}
		return null;
	}

	/**
	 *  @invar Each sub dungeon is stored in a key-value pair where the key is its position relative the origin of this dungeon.
	 *  	The hash structure makes it possible to find, add, and remove sub dungeons at a specified position in constant time,
	 *		but searching the position of a sub dungeon takes linear time.
	 */
	private HashMap<Point, Dungeon> subDungeons;

}
