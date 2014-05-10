package dungeons;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;
import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.util.Direction;
import dungeons.util.Point;
import dungeons.util.SquareIterator;

/**
 * A dungeon is a structure that contains squares in a direct or indirect manner. All squares have a zero-bases 3D position relative
 * to the origin of the dungeon they directly or indirectly belong to.
 * 
 * The coordinate system can be described as 'x=0':left, WEST; 'y=0':front, SOUTH; 'z=0':bottom, DOWN.
 * 
 * The content of a dungeon must always respect the maximum dimensions (it fits entirely inside of it, given its position),
 * and can never have a smaller position than (0,0,0).
 * 
 * Note that maximum dimension represent a 'size', and are thereby in an exclusive representation. This is different from the
 * zero-bases position of squares. E.g. if the maximum dimensions of a dungeon are (3,1,1), then a
 * square with position (2,0,0) is legal, but a square with position (3,0,0) is not.
 * 
 * All dungeons may have a maximum of one parent dungeon, as explained in the documentation of CompositeDungeon.
 *
 * Adding and removing a square from a dungeon takes a complexity of O(log(n)), although computing the new environment (neighbors,
 * obstacles,temperature,...) of the added/removed square might be more time demanding. Note that the new surroundings of the square will be relative
 * to the root of the dungeon hierarchy it belong to (and are in accordance with the documentation of Square). E.g. two squares that are directly stored
 * in tow different SquareDungeons, may have each other as neighbor, if their position relative to a common CompositeDungeon lie side by side.
 * 
 * All squares know nothing about the dungeon (or dungeon hierarchy) they belong to, nor do they store there own position. In addition,
 * they ARE aware of their surrounding neighbors and the obstacles between them in the dungeon hierarchy they belong to.
 * 
 * TODO exceptions must be more descriptive
 * TODO methods build/destroy obstacles make no sense at this moment
 * 
 * @invar A dungeon must always have maximum dimensions that are smaller or equal to the maximum allowed dimensions (5000,5000,5000),
 * 		bigger than (0,0,0), in compliance with the dimensions of its parent dungeon (if any) and legal in any other way.
 *		| hasProperMaximumDimensions()
 * @invar All the squares in a dungeon must be in a legal state.
 * 		| hasProperSquares()
 * @invar The parent of a dungeon (if any) must be in a legal state, and the association between its parent dungeon must be
 * 		properly set. 
 * 		| hasProperParentDungeon()
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 */

public abstract class Dungeon implements Iterable<Square>{

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
	public Dungeon(Point maximumDimensions) throws IllegalMaximumDimensionsException {
		setMaximumDimensions(maximumDimensions); // throws exception
	}

	

	/*****************************************************************
	 * DIMENSIONS - DEFENSIVE
	 *****************************************************************/

	/**
	 * Get the maximum dimensions of this dungeon.
	 * 
	 * @note No elements can be added that are outside or on these boundaries
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
	 * @return Else if this dungeon has a parent, and this dungeon with the given maximum dimensions overlaps
	 * 		with any other sub-dungeon of the parent of this dungeon, then false.
	 *		| if getParentDungeon() != null
	 *		|	&& getParentDungeon().overlapsWithOtherSubDungeon(this, maximumDimensions,
	 *									getParentDungeon().getPositionOfSubDungeon(this)))
	 *		|	then result == false
	 *		Else true.
	 */
	@Raw
	public boolean canHaveAsMaximumDimensions(Point maximumDimensions) {
		if(maximumDimensions == null)
			return false;
		if(!maximumDimensions.isEqualOrSmallerThanValue(ABSOLUTE_MAXIMUM_DIMENSIONS))
			return false;
		if(maximumDimensions.isEqualOrSmallerThanValue(0)) // TODO Dimensions must be bigger or equal than (1,1,1)
			return false;
		if(getParentDungeon() != null && getParentDungeon().overlapsWithOtherSubDungeon(this, maximumDimensions, getParentDungeon().getPositionOfSubDungeon(this)))
			return false;
		return true;
	}

	/**
	 * Check whether this dungeon has proper maximum dimensions.
	 * 
	 * @return True if the current maximum dimensions of this dungeon are legal.
	 * 		| result == canHaveAsMaximumDimensions(getMaximumDimensions())
	 */
	public boolean hasProperMaximumDimensions(){
		return canHaveAsMaximumDimensions(getMaximumDimensions());
	}
	
	/**
	 * Check whether the given maximum dimension are legal as new maximum dimensions.
	 * 
	 * @param maximumDimensions
	 *		A point that represents the new maximum dimensions.
	 * @return True if the given maximum dimensions are legal. Also all coordinates
	 *		must be bigger or equal to the coordinates of the current maximum dimensions.
	 *		| result == canHaveAsMaximumDimensions(maximumDimensions)
	 *		| 		&& maximumDimensions.isEqualOrBiggerThanPoint(getMaximumDimensions())
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
	 * @post The maximum dimensions of this dungeon are the given maximum dimensions.
	 *		| new.getmaximumDimensions() == maximumDimensions
	 * @throws IllegalMaximumDimensionsException(maximumDimensions)
	 *		[MUST] The given new maximum dimensions are not legal for this dungeon
	 *		possibly because smaller than the current dimensions.
	 *		| !canAcceptAsNewMaximumDimensions(maximumDimensions)
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
	 * @post The maximum dimensions of this dungeon are the given maximum dimensions.
	 *		| new.getmaximumDimensions() == maximumDimensions
	 * @throws IllegalMaximumDimensionsException(maximumDimnesions)
	 *		[MUST] The given new maximum dimensions are not legal for this dungeon.
	 *		| !canAcceptAsMaximumDimensions()
	 */
	@Model @Raw
	protected void setMaximumDimensions(Point maximumDimensions) throws IllegalMaximumDimensionsException {
		if (!canHaveAsMaximumDimensions(maximumDimensions))
			throw new IllegalMaximumDimensionsException(maximumDimensions);
		this.maximumDimensions = maximumDimensions;
	}
	
	/**
	 * @invar Exclusive representation of 3 integers. E.g. (20,20,20) means that the maximum (zero-based) coordinate of a square is (19,19,19)
	 */
	private Point maximumDimensions;
	
	/**
	 * All dimensions (x,y,z) must have a maximum that is smaller or equal to this.
	 */
	public static final int ABSOLUTE_MAXIMUM_DIMENSIONS = 5000;
	
	
	
	
	
	
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
	 * @pre The given position must not be null
	 * 		| position != null
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
	 * @pre The given direction must not be a null reference
	 * 		| direction != null
	 * @pre The given position must not be a null reference.
	 * 		| position != null
	 * @return The square that is located next to the given position in the given direction
	 * 		or null if no such square exists.
	 * 		| result == getSquareAt(direction.getPointInThisDirectionOf(position))
	 */
	public Square getSquareInDirectionOfPosition(Point position, Direction direction){
		return getSquareAt(direction.getPointInThisDirectionOf(position));
	}
	
	/**
	 * Check whether the given square is valid for this dungeon.
	 * 
	 * @param square
	 * 		The square to check
	 * @return False if the given square is terminated
	 * 		| if square != null && square.isTerminated()
	 * 		|	then result == false
	 * @return Else true if and only if the given square is not null or it is null
	 * 		and this dungeon is terminated.
	 * 		| else result == ( square != null || isTerminated() )
	 */
	public boolean canHaveAsSquare(Square square){
		if(square != null && (square.isTerminated() || isTerminated()))
			return false;
		return ( square != null || isTerminated() );
	}
	
	/**
	 * Check whether the given square can be placed on the given position in this dungeon.
	 * 
	 * @param square
	 * 		The square to check.
	 * @param position
	 * 		The proposed position of the given square.
	 * @pre If the given square belongs to the root of this dungeon, than the given position
	 * 		is the position of the square relative to the root of this dungeon.
	 * 		| if getRootDungeon().hasAsSquare(square)
	 * 		|	then getRootDungeon().getPositionOfSquare(square).equals(position.add(getPositionInRoot()))
	 * @return If the given square is not legal in this dungeon then false
	 * 		| if !canHaveAsSquare(square)
	 * 		|	then result == false
	 * @return Else if the given position is smaller than zero then false
	 * 		| else if !position.isEqualOrBiggerThanValue(0)
	 * 		|	then result == false
	 * @return Else true if and only if the given position is smaller than the maximum dimensions
	 *  	| else result == position.isSmallerThan(maximumDimensions)
	 */
	public boolean canHaveAsSquareAt(Square square, Point position){
		// With the current setup (unidirectional association between Square and Dungeon) this is the best we can do to avoid squares being added
		// in multiple dungeons at once.
		assert !getRootDungeon().hasAsSquare(square) || getRootDungeon().getPositionOfSquare(square).equals(position.add(getPositionInRoot()));
		
		if(!canHaveAsSquare(square))
			return false;
		if(!position.isEqualOrBiggerThanValue(0))
			return false;
		if(!position.isSmallerThan(maximumDimensions))
			return false;
		return true;
	}
	
	/**
	 * Check if all the squares of this dungeon are in a legal state, at a legal position.
	 * 
	 * @return True if for each square, it is legal at its position.
	 * 		| for each s in getSquares():
	 * 		|	( canHaveAsSquareAtPosition(s, getPositionOfSquare(s)) )
	 */
	public boolean hasProperSquares(){
		for (Square s : getSquares()){
			if(!canHaveAsSquareAt(s, getPositionOfSquare(s)))
				return false;
		}
		return true;
	}
	
	/**
	 * Check whether this dungeon has a square a the given position relative to its origin.
	 * 
	 * @param position
	 * 		The position to look at.
	 * @pre The given position is not null
	 * 		| position != null
	 * @return True if a square exists at the given position
	 * 		| result == ( getSquareAt(position) != null )
	 */
	public boolean hasSquareAt(Point position){
		return getSquareAt(position) != null;
	}
	
	/**
	 * Check whether this dungeon has the given square as one of its squares.
	 * 
	 * @param square
	 * 		The square to check for.
	 * @result True if some square in this dungeon is equal to the given square.
	 * 		| result ==
	 * 		|	( for some s in getSquares():
	 * 		|		s == square )
	 */
	public abstract boolean hasAsSquare(Square square);
	
	/**
	 * Add the given square to this dungeon at the given position relative to the origin of this dungeon,
	 * and make sure that the obstacles in the given directions are destroyed (if possible).
	 * TODO
	 * @param square
	 * 		The square to add to this dungeon.
	 * @param position
	 * 		The position to add the given square to relative to the origin of this dungeon.
	 * @param destroyObstacles
	 * 		An array of directions in which obstacles should destroy
	 * @pre If the given square belongs to the root of this dungeon, than the given position
	 * 		is the position of the square relative to the root of this dungeon.
	 * 		| if getRootDungeon().hasAsSquare(square)
	 * 		|	then getRootDungeon().getPositionOfSquare(square).equals(position.add(getPositionInRoot()))
	 * @effect If the given square is legal for the given position, and a square already exists at that position,
	 * 		then that square is now removed from this dungeon without fixing obstacles of its neighbors	
	 * 		| if this.canHaveAsSquareAt(square, position) && this.hasSquareAt(position)
	 * 		|	then removeSquareAtPositionWithoutFixingNeighbors(Point position)
	 * @post If the given square is legal for the given position, the square at the given position is
	 * 		now the given square. Additionally, if the given position did not contain a square before,
	 * 		the square count of this dungeon is now incremented with 1.
	 * 		| if this.canHaveAsSquareAt(square, position)
	 * 		|	then new.getSquareAt(position) == square
	 * 		| 	&& if this.hasSquareAt(position)
	 * 		|		then new.getNbSquares() == this.getNbSquares()+1
	 * @post If the given square is legal for the given position, the given square has neighbors
	 * 		in all directions there is a neighbor at in its dungeon
	 * 		| if this.canHaveAsSquareAt(square, position)
	 * 		|	then for each d in Direction.values(): 
	 * 		|			(new square).getDungeon().getSquareInDirectionOfPosition(position, d)
	 * 		|			== (new square).getNeighborAtDirection(d)
	 * @post If the given square is legal for the given position, the given square will certainly have
	 * 		walls at boundaries where it doesn't have a neighbor
	 * 		| if this.canHaveAsSquareAt(square, position)
	 * 		|	then for each d in Direction.values():
	 * 		|			if !(new square).hasNeighborInDirection(d)
	 * 		|			then (new square).hasWall(d)
	 * @post If the given square is legal for the given position, all inconsistencies between new obstacles
	 * 		of its new neighbors and itself are fixed
	 * 		| if this.canHaveAsSquareAt(square, position)
	 * 		|	then for each d in Direction.values():
	 * 		|		if (new square).hasNeighborInDirection(d) then	
	 * 		|			if destroyObstacles.contains(d)
	 * 		|			then !(new square).hasObstacle(d) 
	 * 		|				&& !(new square).getNeighborAt(d).hasObstacle(d.getInvers())
	 * 		|			else if ( (this square).hasDoor(d) || (new square).getNeighborAt(d).hasDoor(d.getInvers()) )
	 * 		|			then (new square).hasDoor(d)
	 * 		|				&& (new square).getObstacle(d) == (new square).getNeighborAt(d).getObstacle(d.getInvers())
	 * 		|			else if ( (this square).hasWall(d) || (new square).getNeighborAt(d).hasWall(d.getInvers()) )
	 * 		|			then (new square).hasWall(d)
	 * 		|				&& (new square).getObstacle(d) == (new square).getNeighborAt(d).getObstacle(d.getInvers())
	 * 		|			else (new square).getObstacle(d) == (this square).getObstacle(d)
	 * 		|				&& (this square).getObstacle(d) == (new square).getNeighborAt(d).getObstacle(d.getInvers())
	 * @post If the given square is legal for the given position, the temperature of the group this
	 * 		square now belong to is set to the proper value
	 * 		| if this.canHaveAsSquareAt(square, position)
	 * 		|	then for each s in (new square).getConnected():
	 * 		|			s.getTemperature() == (new square).getTemperature()
	 * @note The method is left in a non-raw state!
	 */
	public abstract void addAsSquareAt(Square square, Point position, Direction... destroyObstacles);
	
	/**
	 * Remove the square at the given position from this dungeon if it is present, and update its surroundings.
	 *
	 * @param square
	 * 		The square to remove from this dungeon
	 * @post If the given position is not null, then the square on that position is not effective (anymore),
	 * 		| if ( position != null )
	 * 		|	then !new.hasSquareAtPosition(position)
	 * @post If this dungeon had a square at the given position, then that square has no neighbors anymore.
	 * 		Also walls are build at the edges where the square was removed.
	 * 		| if this.hasSquareAt(position)
	 * 		|	 then for each d in Direction.values():
	 * 		|		(new this.getSquareAt(position)).getNeighborInDirection(d) == null 
	 * 		|		&& (new this.getSquareAt(position)).hasWall(d)
	 * @post If this dungeon had a square at the given position, then all the previous neighbors of that
	 * 		square now don't have that square as their neighbor anymore.
	 * 		| if this.hasSquareAt(position)
	 * 		|	then for n in (this this.getSquareAt(position)).getNeighbors():
	 * 		|		!n.hasAsNeighbor(this.getSquareAt(position))
	 * 
	 * @note The method is left in a non-raw state!
	 */
	public abstract void removeAsSquareAt(Point position);
	
	/**
	 * Remove the given square from this dungeon if it is present.
	 * 
	 * @param square
	 * 		The square to remove from this dungeon
	 * @effect If the given square is not null, that square is removed.
	 * 		| removeAsSquareAt(getPositionOfSquare(square))
	 */
	public void removeAsSquare(Square square){
		if(square != null){
			Point pos = getPositionOfSquare(square);
			removeAsSquareAt(pos);
		}
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
	 * @post If the given square or direction is null, nothing happens.
	 * @effect If the given square and direction are not null, a door might be build or replaced next to the given
	 * 		square in the given direction.
	 * 		| (square != null) => square.buildDoorAt(direction, isOpen)
	 * @note A temperature update might happen
	 */
	public void buildDoor(Square square, Direction direction, Boolean isOpen){
		if(square != null)
			square.buildDoorAt(direction, isOpen);
			//might fail, if no door is allowed (total implementation)
	}
	
	/**
	 * Build or replace a wall next to the given square in the given direction.
	 * 
	 * @param square
	 * 		The square to build the wall next to.
	 * @param direction
	 * 		The direction in which to build the wall.
	 * @post If the given square is null, nothing happens.
	 * @effect If the given square is not null, a wall is build or replaced next to the given square in the given direction.
	 * 		| (square != null) => square.buildWallAt(direction)
	 * @note No temperature update will happen.
	 */
	public void buildWall(Square square, Direction direction){
		if(square != null)
			square.buildWallAt(direction);
		//TODO Does not work?
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
	 * @effect Build or replace a door next to the square at the given position in the given direction.
	 * 		| buildDoor(getSquareAt(position), direction, isOpen);
	 */
	public void buildDoor(Point position, Direction direction, Boolean isOpen){
		buildDoor(getSquareAt(position), direction, isOpen);
	}
	
	/**
	 * Build or replace a wall next to the square at the given position in the given direction.
	 * 
	 * @param position
	 * 		The position of the square to build the wall next to.
	 * @param direction
	 * 		The direction in which to build the wall.
	 * @effect Build or replace a wall next to the square at the given position in the given direction.
	 * 		| buildDoor(getSquareAt(position), direction);
	 */
	public void buildWall(Point position, Direction direction){
		buildWall(getSquareAt(position), direction);
	}
	
	/**
	 * Destroy an obstacle in the given direction of the given square.
	 * 
	 * @param square
	 * 		The square to destroy the obstacle from.
	 * @param direction
	 * 		The direction in which to destroy an obstacle
	 * @effect If the given square is not null, and an obstacle was present in the given direction relative to the given square,
	 * 		and destroying it was possible, then it is now destroyed.
	 * 		| square != null => square.destroyObstacleAt(direction)
	 */
	public void destroyObstacle(Square square, Direction direction){
		if(square != null)
			square.destroyObstacleAt(direction);
	}
	
	/**
	 * Destroy an obstacle in the given direction of the square with the given position.
	 * 
	 * @param position
	 * 		The position of the square to destroy the obstacle from.
	 * @param direction
	 * 		The direction in which to destroy an obstacle
	 * @effect Destroy an obstacle in the given direction of the square with the given position.
	 * 		| destroyObstacle(getSquareAt(position), direction)
	 */
	public void destroyObstacle(Point position, Direction direction){
		destroyObstacle(getSquareAt(position), direction);
	}
	
	/**
	 * If possible, let the given square collapse.
	 * TODO doc
	 * @param square
	 * 		The square to collapse
	 * @pre the given square belong to the root of this dungeon
	 * 		getRootDungeon().hasAsSquare(square)
	 * @effect If this dungeon has a root dungeon, than collapse is executed on that root dungeon
	 * 		| if getParentDungeon() != null
	 * 		|	then getRootDungeon().collapse(square)
	 * @effect else if the given square can collapse, it is removed from its dungeon, and the square
	 * 		above it now populates the position of the given square.
	 * 		| else if (this square).canCollapse()
	 * 		|	then removeAsSquare(square)
	 * 		|		&& removeAsSquare((this square).getNeighborAt(Direction.UP))
	 * 		|		&& addAsSquareAt((this square).getNeighborAt(Direction.UP), getPositionOfSquare(this square));
	 * @effect If a square collapsed, and the square below the collapsed square can collapse, it has a 60% chance to do so.
	 * 		| if getParentDungeon() == null && square.canCollapse()
	 * 		|		&& (new Random(System.currentTimeMillis())).nextFloat() > 0.6f
	 * 		|		&& (this square).getNeighborAt(Direction.DOWN) != null
	 * 		|	then collapse((this square).getNeighborAt(Direction.DOWN))
	 */
	public void collapse(Square square){
		assert getRootDungeon().hasAsSquare(square);
		
		if(getParentDungeon() != null)
			getRootDungeon().collapse(square);
		if(!square.canCollapse())
			return;
		
		Square above = square.getNeighborAt(Direction.UP);
		Point pos = getPositionOfSquare(square);
		
		removeAsSquare(square);
		removeAsSquare(above);
		addAsSquareAt(above, pos); // temperature updates
		
		Random rand = new Random(System.currentTimeMillis());
		if(rand.nextFloat() > 0.6f && above.getNeighborAt(Direction.DOWN) != null)
			collapse(above.getNeighborAt(Direction.DOWN));
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
	public boolean isTerminated(){
		return isTerminated;
	}
	
	/**
	 * Terminate this dungeon.
	 * 
	 * @post This dungeon is now in a terminated state
	 * 		| new.isTerminated()
	 * @post All squares that previously belonged to this dungeon, now don't belong to
	 * 		this dungeon anymore.
	 * 		| for each s in this.getSquares():
	 * 		|	!hasAsSquare(s)
	 * @effect If this dungeon has a parent dungeon, it is removed from it, and it loses
	 * 		the reference to its parent.
	 * 		| if this.getParentDungeon() != null
	 * 		|	then this.getParentDungeon().removeAsSubDungeon(this)
	 * @note 'removing from a dungeon' != 'terminating'
	 * @note The method is left in a non-raw state!
	 */
	public abstract void terminate();
	
	protected boolean isTerminated;
	
	
	
	
	
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
	 * @pre If the given composite dungeon is not null, then it must have this dungeon as one
	 * 		of its sub-dungeons.
	 * 		| (parentDungeon != null) => parentDungeon.hasAsSubDungeon(this)
	 * @pre If the given composite dungeon is null, and the current parent dungeon is not null, then
	 * 		the current parent dungeon may not have this dungeon as one of its sub-dungeons.
	 * 		| (parentDungeon == null && getParentDungeon() != null ) => !getParentDungeon().hasAsSubDungeon(this)
	 * @note This method being nominal will not prevent the management of sub dungeons to be defensive... 
	 * @note In an indirect way, a terminated parent dungeon is not allowed. ( hasAsSubDungeon(this)-> can only have subDungeon under
	 * 		specific circumstances.
	 */
	@Raw
	public void setParentDungeon(CompositeDungeon parentDungeon){
		assert (parentDungeon == null) || parentDungeon.hasAsSubDungeon(this);
		assert (parentDungeon != null) || (getParentDungeon() == null ) || !getParentDungeon().hasAsSubDungeon(this);
		this.parentDungeon = parentDungeon;
	}
	
	/**
	 * Check whether this square can have the given dungeon as its dungeon
	 * 
	 * @param dungeon
	 * 		The dungeon to check
	 * @return True if and only if the given dungeon is null, or it can have
	 * 		this square as one of its squares. 
	 */
	public boolean canHaveAsParentDungeon(CompositeDungeon parentDungeon){
		return ( (parentDungeon == null) || parentDungeon.canHaveAsSubDungeon(this) );
	}

	/**
	 * Check whether this dungeon has a proper parent dungeon
	 * 
	 * @return True if and only if the parent of this dungeon is legal, and if
	 * 		the parent has this dungeon as one of its sub dungeons.
	 * 		| result == canHaveAsParentDungeon(getParentDungeon())
	 * 		|			&& (getParentDungeon() == null || getParentDungeon().hasAsSubDungeon(this))
	 */
	public boolean hasProperParentDungeon(){
		return (canHaveAsParentDungeon(getParentDungeon()) && (getParentDungeon() == null || getParentDungeon().hasAsSubDungeon(this)) );
	}
	
	/**
	 * Retrieve the root dungeon of this dungeon.
	 * 
	 * @return If this dungeon has a parent dungeon, than the root dungeon is that dungeon.
	 * 		| if getParentDungeon() != null
	 * 		|	then result == (getParentDungeon().getRootDungeon())
	 * @return Else this dungeon is the root dungeon
	 * 		| else result == this
	 */
	public Dungeon getRootDungeon(){
		if(getParentDungeon() != null)
			return getParentDungeon().getRootDungeon();
		else return this;
	}
	
	/**
	 * Check whether this dungeon is equal to the given dungeon or is a direct
	 * or indirect parent of the given dungeon.
	 * 
	 * @param dungeon
	 *        The dungeon to check.
	 * @return True if the given dungeon is equal to this dungeon or
	 *         if the given dungeon is effective and this dungeon equals or is
	 *         the direct or indirect parent of the parent of the given dungeon
	 *         False otherwise.
	 *       | result == (this == dungeon) || 
	 *       |            ( dungeon != null && 
	 *       |              equalsOrIsDirectOrIndirectParentOf(
	 *       |                        dungeon.getParentDungeon()) )  
	 */
	@Raw
	public boolean equalsOrIsDirectOrIndirectParentOf(@Raw Dungeon dungeon){
		return ((this == dungeon) || 
			    ( (dungeon != null) && 
			    	  (equalsOrIsDirectOrIndirectParentOf(dungeon.getParentDungeon()))
			    	) );
	}
	
	/**
	 * Get the position of this dungeon relative to the origin of its root.
	 * 
	 * @return If this dungeon has a parent, then return the position of that parent relative to its root
	 * 		added to the position of this dungeon relative to its parent.
	 * 		| if getParentDungeon() != null 
	 * 		|	then result == getParentDungeon().getPosistionInRoot().add(getParentDungeon().getPositionOfSubDungeon(this))
	 * 		Else return the origin
	 * 		| else result == Point.ORIGIN
	 */
	public Point getPositionInRoot(){
		if(getParentDungeon() != null)
			return getParentDungeon().getPositionInRoot().add(getParentDungeon().getPositionOfSubDungeon(this));
		return Point.ORIGIN;
	}
	
	private CompositeDungeon parentDungeon;
	
	
	
	
	/*****************************************************************
	 * SUB DUNGEONS (introduced her instead of CompositeDungeon, to not violate LSP)
	 *****************************************************************/
	
	/**
	 * Get all the levels and shafts that are contained within this dungeon.
	 */
	@Basic
	public abstract List<SquareDungeon> getLevelsAndShafts();
	
	/**
	 * Retrieve the square dungeon that contains the given position.
	 * 
	 * @param position
	 * 		Some position relative to the origin of this dungeon.
	 */
	public abstract SquareDungeon getDungeonContainingPosition(Point position);
	
}

