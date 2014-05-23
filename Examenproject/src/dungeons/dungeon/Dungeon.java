package dungeons.dungeon;

import java.util.Collection;
import java.util.List;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Raw;
import dungeons.exception.IllegalDungeonException;
import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.square.Square;
import dungeons.util.Direction;
import dungeons.util.Point;
import dungeons.util.SquareIterator;

/**
 * <p>A dungeon is a structure that contains squares in a direct or indirect manner. All squares have a zero-bases 3D position relative to the
 * origin of the dungeon they directly or indirectly belong to.</p>
 * 
 * <p>The coordinate system can be described as 'x=0':left, WEST; 'y=0':front, SOUTH; 'z=0':bottom, DOWN.</p>
 * 
 * <p>The content of a dungeon must always respect the maximum dimensions (it fits entirely inside of it, given its position), and can never
 * have a smaller position than (0,0,0).</p>
 * 
 * <p>Note that maximum dimension represent a 'size', and are thereby in an exclusive representation. This is different from the zero-bases
 * position of squares. E.g. if the maximum dimensions of a dungeon are (3,1,1), then a square with position (2,0,0) is legal, but a square
 * with position (3,0,0) is not.</p>
 * 
 * <p>All dungeons may have a maximum of one parent dungeon, as explained in the documentation of CompositeDungeon.</p>
 * 
 * <p>Adding and removing a square from a dungeon takes a complexity of O(log(n)), although computing the new environment (neighbors,
 * obstacles,temperature,...) of the added/removed square might be more time demanding. Note that the new surroundings of the square will be
 * relative to the root of the dungeon hierarchy it belong to (and are in accordance with the documentation of Square). E.g. two squares
 * that are directly stored in two different SquareDungeons, may have each other as neighbor, if their position relative to a common
 * CompositeDungeon lie side by side.</p>
 * 
 * <p>Squares have a reference to the dungeon they directly belong to. In addition, they are aware of their surrounding neighbors and the
 * obstacles between them in the dungeon hierarchy they belong to.</p>
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

public interface Dungeon extends Iterable<Square> {

	/*****************************************************************
	 * DIMENSIONS - DEFENSIVE
	 *****************************************************************/

	/**
	 * Get the maximum dimensions of this dungeon.
	 */
	@Basic
	public Point getMaximumDimensions();

	/**
	 * Check whether the given maximum dimensions are valid for this dungeon.
	 * 
	 * @param maximumDimensions
	 *		A point that represents the maximum dimensions.
	 * @return If the given maximum dimensions are null, than false
	 * 		| if maximumDimensions == null
	 * 		|	then result == false
	 * @return Else if the given maximum dimensions are bigger that the maximum allowed dimensions
	 *		then false
	 *		| else if !maximumDimensions.isEqualOrSmallerThanValue(ABSOLUTE_MAXIMUM_DIMENSIONS)
	 *		|	then result == false
	 * @return Else if the given maximum dimensions are smaller or equal to zero, then false
	 *		| else if maximumDimensions.isEqualOrSmallerThanValue(0)
	 *		|	then result == false
	 * @return Else if this dungeon has a parent, and this dungeon with the given maximum dimensions overlaps
	 * 		with any other sub-dungeon of the parent of this dungeon, then false.
	 *		| else if getParentDungeon() != null
	 *		|		  && getParentDungeon().overlapsWithOtherSubDungeon(this, maximumDimensions,
	 *		|							getParentDungeon().getPositionOfSubDungeon(this)))
	 *		|	then result == false
	 *		Else true.
	 */
	@Raw
	public boolean canHaveAsMaximumDimensions(Point maximumDimensions);

	/**
	 * Check whether this dungeon has proper maximum dimensions.
	 * 
	 * @return True if the current maximum dimensions of this dungeon are legal.
	 * 		| result == canHaveAsMaximumDimensions(getMaximumDimensions())
	 */
	public boolean hasProperMaximumDimensions();

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
	public boolean canAcceptAsNewMaximumDimensions(Point maximumDimensions);

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
	 * @note Raw because subclass shaft uses its super method, and goes into a raw sate (until the method is left)
	 */
	public void changeMaximumDimensions(Point maximumDimensions) throws IllegalMaximumDimensionsException;

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
	public Collection<Square> getSquares();

	/**
	 * Get the square at the given position relative to the origin of this dungeon.
	 * 
	 * @param position
	 * 		A position relative to the origin of this dungeon.
	 * @pre The given position is effective
	 * 		| position != null
	 */
	@Basic
	public Square getSquareAt(Point position);

	/**
	 * Get the number of squares that are currently directly or indirectly in this dungeon.
	 */
	@Basic
	public int getNbSquares();

	/**
	 * Get the position of the given square relative to the origin of this dungeon.
	 * 
	 * @param square
	 * 		The square to get the position of.
	 */
	@Basic
	public Point getPositionOfSquare(Square square);

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
	public Square getSquareInDirectionOfPosition(Point position, Direction direction);

	/**
	 * Retrieve the number of positions within the maximum dimensions of this dungeon that do
	 * not contain a square, but that can contain one.
	 * 
	 * @return The number of empty positions in this dungeon
	 * 		| for dungeon in getLevelsAndShafts():
	 * 		| 	result += (d.getMaximumDimensions().size() - d.getSquares().size())
	 */
	public int getEmptrySpace();

	/**
	 * Check whether this dungeon van have the given square as one of its squares.
	 * 
	 * @param square
	 * 		The square to check
	 * @return If this dungeon is terminated, then false.
	 * 		| if isTerminated()
	 * 		|	then result == false
	 * @return Else If the given square is not effective, then false.
	 * 		| else if square == null
	 * 		|	then result == false
	 * @return Else if the given square is terminated, then false.
	 * 		| else if square.isTerminated()
	 * 		|	then result == false
	 * @return Else if this dungeon has the given square as one of its squares, then true if and only if
	 * 		the given square has the dungeon it is directly stored in, as its dungeon.
	 * 		| else if hasAsSquare(square)
	 * 		|	then result == ( getDungeonContainingPositiont(getPositionOfSquare(square)) == square.getDungeon() )
	 */
	@Raw
	public boolean canHaveAsSquare(Square square);

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
	 * @return Else if the given position does not fit in the maximum dimensions, then false.
	 *  	| else if !position.isSmallerThan(maximumDimensions)
	 *  	|	then result == false
	 * @return Else if the given square has a dungeon is belong to, then the given position must be the position
	 * 		of the square in this dungeon.
	 * 		| else if square.getDungeon() != null && getPositionOfSquare(square) != position
	 * 		|	then result == false
	 * @return Else true
	 */
	@Raw
	public boolean canHaveAsSquareAt(Square square, Point position);

	/**
	 * Check if all the squares of this dungeon are in a legal state, at a legal position.
	 * 
	 * @return True if for each square, it is legal at its position.
	 * 		| for each s in getSquares():
	 * 		|	( canHaveAsSquareAtPosition(s, getPositionOfSquare(s)) )
	 */
	@Raw
	public boolean hasProperSquares();

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
	public boolean hasSquareAt(Point position);

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
	public boolean hasAsSquare(Square square);

	/**
	 * Add the given square to this dungeon at the given position relative to the origin of this dungeon,
	 * and make sure that the obstacles in the given directions are destroyed (if possible).
	 * 
	 * @param square
	 * 		The square to add to this dungeon.
	 * @param position
	 * 		The position to add the given square to relative to the origin of this dungeon.
	 * @param destroyObstacles
	 * 		An array of directions in which obstacles should be destroyed (relative to the given position)
	 * @post If the given square is legal for the given position and it was not contained in any other dungeon before,
	 * 		then if a square already exists on the given position position, that square is now removed from this dungeon.
	 * 		| if this.canHaveAsSquareAt(square, position) && this.hasSquareAt(position)
	 * 		|	then !new.hasAsSquare(this.getSquareAt(position))
	 * @post If the given square is legal for the given position and it was not contained in any other dungeon before,
	 * 		then the square at the given position is now the given square. Additionally, if the given position did not contain a square before,
	 * 		the square count of this dungeon is now incremented with 1.
	 * 		| if this.canHaveAsSquareAt(square, position)
	 * 		|	then new.getSquareAt(position) == square
	 * 		| 	&& if this.hasSquareAt(position)
	 * 		|		then new.getNbSquares() == this.getNbSquares()+1
	 * @post If the given square is legal for the given position and it was not contained in any other dungeon before,
	 * 		then the dungeon of the given square is now the SquareDungeon the contains the given position.
	 * 		If a square already populated the position, then that square now has no more dungeon.
	 * 		| if this.canHaveAsSquareAt(square, position)
	 * 		|	then (new square).getDungeon() == getDungeonContainingPosition(position)
	 * 		|		&& if this.hasSquareAt(position)
	 * 		|			then this.getSquareAt(position).getDungeon() == null
	 * @effect If the given square is legal for the given position and it was not contained in any other dungeon before,
	 * 		then the neighbors of the given square are updated with neighboring squares in the root of this dungeon and proper
	 * 		obstacles are build between them.
	 * 		Neighbor associations between old neighbors of the removed square (if any) are also terminated in this process
	 * 		For each direction in destroyObstacles the obstacle between the given square and its new neighbor is removed (if possible).
	 * 		And if this dungeon can not allow a wall in the a specific direction at the given position, then non is placed.
	 * 		| if this.canHaveAsSquareAt(square, position)
	 * 		|	then for each direction in Direction.values():
	 * 		|			if getRootDungeon().getSquareInDirectionOfPosition(position.add(getPositionInRoot()), direction) != null
	 * 		|			then if destroyObstacles.contains(direction) || !canHaveWallAt(position, direction)
	 * 		|				 then square.registerNeighbor(getRootDungeon().getSquareInDirectionOfPosition(position.add(getPositionInRoot()), direction), direction, true)
	 * 		|				 else square.registerNeighbor(getRootDungeon().getSquareInDirectionOfPosition(position.add(getPositionInRoot()), direction), direction, false)
	 * @note The method is left in a non-raw state!
	 */
	public void addAsSquareAt(Square square, Point position, Direction... destroyObstacles);

	/**
	 * Remove the square at the given position from this dungeon if it is present, and update its surroundings.
	 *
	 * @param square
	 * 		The square to remove from this dungeon
	 * @post If the given position contained a square, then it is now removed and the square count is decremented with one
	 * 		| if this.hasSquareAt(position)
	 * 		|	then !new.hasSquareAt(position)
	 * 		|		&& new.getNbSquares() == this.getNbSquares()-1
	 * @post If the given position contained a square, then that square
	 * 		doesn't have any neighbors anymore. Also walls are now located in all directions of the square, and 
	 * 		the previous neighbor of the removed square now don't have that square as their neighbor anymore.
	 * 		| if this.hasSquareAt(position)
	 * 		|	 then for each direction in Direction.values():
	 * 		|			!(new this.getSquareAt(position)).hasNeighborAt(direction)
	 * 		|			&& (new this.getSquareAt(position)).hasWall(direction)
	 * 		|			if getSquareInDirectionOfPosition(position, direction) != null
	 * 		|				then !new.getSquareInDirectionOfPosition(position, direction).hasNeighborAt(direction.oppositeDirection())
	 * 		|				 	 && new.getSquareInDirectionOfPosition(position, direction).hasWall(direction.oppositeDirection())
	 * @post If the given position contained a square, then that square does not have a dungeon anymore
	 * 		| if this.hasSquareAt(position)
	 * 		|	then new.getSquareAt(position).getDungeon() == null
	 * @note The method is left in a non-raw state!
	 */
	public void removeAsSquareAt(Point position);

	/**
	 * Remove the given square from this dungeon if it is present.
	 * 
	 * @param square
	 * 		The square to remove from this dungeon
	 * @effect If the given square is not null, that square is removed.
	 * 		| removeAsSquareAt(getPositionOfSquare(square))
	 */
	public void removeAsSquare(Square square);

	/**
	 * Swap the square at the given position in this dungeon with the given square.
	 * 
	 * @param position
	 * 		The position of the square in this dungeon to swap
	 * @param other
	 * 		The square to swap the square at he given position in this dungeon with.
	 * @post If the given position contains a square and the given square is contained in the root of this dungeon,
	 * 		then the square that was at the given position is now at the position of the other square in the root and the
	 * 		other square is now at the position of the square that was at the given position.
	 * 		| if getRootDungeon().hasAsSquare(other) && hasSquareAt(position)
	 * 		|	then new.getRootDungeon().getSquareAtPosition(this.getRootDungeon().getPositionOfSquare(other)) == this.getSquareAtPosition(position)
	 * 		|		&& new.getSquareAt(position) == other
	 * @effect If the given position contains a square and the given square is contained in the root of this dungeon,
	 * 		then the neighbors of the square at the given position are swapped with the neighbors of the given square.
	 * 		| if getRootDungeon().hasAsSquare(other) && hasSquareAt(position)
	 * 		| then for each direction in Direction.values():
	 * 		|		getSquareAtPosition(position).swapNeighbor(other, direction)
	 * @post If the given position contains a square and the given square is contained in the root of this dungeon,
	 * 		then the dungeon this square at the given position is stored is, is now the dungeon that the given square is stored in, and 
	 * 		the other way around.
	 * 		| if getRootDungeon().hasAsSquare(other) && hasSquareAt(position)
	 * 		| then this.getSquareAt(position).getDungeon() == (new other).getDungeon()
	 * 		|	   && (new this.getSquareAt(position)).getDungeon() == (this other).getDungeon()
	 * @note One argument is a position, the other a square. This is because else swapping of squares would not be possible between different sub-dungeons.
	 */
	public void swapSquareAt(Point position, Square other);

	/**
	 * Check whether a wall is allowed in the given direction for this dungeon.
	 * 
	 * @param position
	 * 		The position where the wall is next to.
	 * @param direction
	 * 		The direction of the wall relative to the given position
	 * @return Always true
	 */
	public boolean canHaveWallAt(Point position, Direction direction);

	/**
	 * Build or replace a door next to the given square in the given direction.
	 * 
	 * @param square
	 * 		The square to build the door next to.
	 * @param direction
	 * 		The direction in which to build the door.
	 * @param isOpen
	 * 		Whether the new door is open.
	 * @effect If the given square and direction are not null, a door might be build or replaced next to the given
	 * 		square in the given direction with the given isOpen state. (no door at the edge of a dungeon)
	 * 		| if square != null
	 * 		| then square.buildDoorAt(direction, isOpen)
	 * @note A temperature update might happen
	 */
	public void buildDoor(Square square, Direction direction, Boolean isOpen);

	/**
	 * Build or replace a wall next to the given square in the given direction.
	 * 
	 * @param square
	 * 		The square to build the wall next to.
	 * @param direction
	 * 		The direction in which to build the wall.
	 * @effect If the given square is not null and a wall can be build in the given direction relative to the position
	 * 		of the given square in this dungeon, then a wall might be build or replaced next to the given square in
	 * 		the given direction.
	 * 		| if (square != null && canHaveWallAt(getPositionOfSquare(square), direction))
	 * 		|	then square.buildWallAt(direction)
	 * @note No temperature update will happen.
	 */
	public void buildWall(Square square, Direction direction);

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
	public void buildDoor(Point position, Direction direction, Boolean isOpen);

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
	public void buildWall(Point position, Direction direction);

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
	public void destroyObstacle(Square square, Direction direction);

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
	public void destroyObstacle(Point position, Direction direction);

	/**
	 * If possible, let the given square collapse.
	 * 
	 * @param square
	 * 		The square to collapse
	 * @pre The given square belong to the root of this dungeon
	 * 		getRootDungeon().hasAsSquare(square)
	 * @effect If this dungeon has a root dungeon, than collapse is executed on that root dungeon
	 * 		| if getParentDungeon() != null
	 * 		|	then getRootDungeon().collapse(square)
	 * @effect Else if the given square can collapse, it is removed from its dungeon, and the square
	 * 		above it now populates the position of the given square.
	 * 		| else if (this square).canCollapse()
	 * 		|	then removeAsSquare(square)
	 * 		|		&& removeAsSquare((this square).getNeighborAt(Direction.UP))
	 * 		|		&& addAsSquareAt((this square).getNeighborAt(Direction.UP), getPositionOfSquare(this square));
	 * @effect If a square collapsed, and the square below the collapsed square can collapse, it has a 60% chance to do so.
	 * 		| if getParentDungeon() == null && (this square).canCollapse()
	 * 		|		&& (new Random(System.currentTimeMillis())).nextFloat() > 0.6f
	 * 		|		&& (this square).getNeighborAt(Direction.DOWN) != null
	 * 		|	then collapse((this square).getNeighborAt(Direction.DOWN))
	 */
	public void collapse(Square square);

	/**
	 * Returns an iterator that iterates over all the squares in this dungeon from front(y)
	 * to back, left(x) to right and bottom(z) to top.
	 */
	public SquareIterator iterator();

	
	
	/*****************************************************************
	 * TERMINATION
	 *****************************************************************/

	/**
	 * Check whether this dungeon is terminated.
	 */
	@Basic
	public boolean isTerminated();

	/**
	 * Terminate this dungeon.
	 * 
	 * @post This dungeon is now in a terminated state
	 * 		| new.isTerminated()
	 * @post All squares that previously belonged to this dungeon, now don't belong to
	 * 		this dungeon anymore.
	 * 		| for each s in this.getSquares():
	 * 		|	!new.hasAsSquare(s)
	 * @effect If this dungeon has a parent dungeon, it is removed from it, and it loses
	 * 		the reference to its parent.
	 * 		| if this.getParentDungeon() != null
	 * 		|	then this.getParentDungeon().removeAsSubDungeon(this)
	 * @note The method is left in a non-raw state!
	 */
	public void terminate();

	
	
	/*****************************************************************
	 * PARENT DUNGEON 
	 *****************************************************************/

	/**
	 * Get the parent dungeon of this dungeon.
	 */
	@Basic
	public CompositeDungeon getParentDungeon();

	/**
	 * Set the given dungeon to be the parent of this dungeon.
	 * 
	 * @param parentDungeon
	 * 		A composite dungeon that is to be the parent of this dungeon.
	 * @post The parent of this dungeon is now the given dungeon
	 *		| new.getParentDungeon() == parentDungeon
	 * @throws IllegalDungeonException(ParentDungeon) [MUST]
	 * 		If the given dungeon is effective, then is does not contain this dungeon as one of its sub-dungeons yet.
	 * 		| (parentDungeon != null) => !parentDungeon.hasAsSubDungeon(this)
	 * 		Else if the given dungeon is not effective, but the current parent dungeon is, then the current parent dungeon still
	 * 		contains this dungeon as one of its sub dungeons. 
	 * 		| (parentDungeon == null && getParentDungeon() != null ) => getParentDungeon().hasAsSubDungeon(this)
	 * @note In an indirect way, a terminated parent dungeon is not allowed. ( hasAsSubDungeon(this)-> can only have subDungeon under
	 * 		specific circumstances.)
	 */
	@Raw
	public void setParentDungeon(CompositeDungeon parentDungeon) throws IllegalDungeonException;

	/**
	 * Check whether this square can have the given dungeon as its dungeon
	 * 
	 * @param dungeon
	 * 		The dungeon to check
	 * @return True if and only if the given dungeon is null, or it can have
	 * 		this square as one of its squares.
	 * @note This method does not check the validity of the bidirectional association. -> see hasProperParentDungeon
	 */
	@Raw
	public boolean canHaveAsParentDungeon(CompositeDungeon parentDungeon);

	/**
	 * Check whether this dungeon has a proper parent dungeon
	 * 
	 * @return True if and only if the parent of this dungeon is legal, and if
	 * 		the parent has this dungeon as one of its sub dungeons.
	 * 		| result == canHaveAsParentDungeon(getParentDungeon())
	 * 		|			&& (getParentDungeon() == null || getParentDungeon().hasAsSubDungeon(this))
	 */
	@Raw
	public boolean hasProperParentDungeon();

	/**
	 * Retrieve the root dungeon of this dungeon.
	 * 
	 * @return If this dungeon has a parent dungeon, than the root dungeon is that dungeon.
	 * 		| if getParentDungeon() != null
	 * 		|	then result == (getParentDungeon().getRootDungeon())
	 * @return Else this dungeon is the root dungeon
	 * 		| else result == this
	 */
	public Dungeon getRootDungeon();

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
	public boolean equalsOrIsDirectOrIndirectParentOf(@Raw Dungeon dungeon);

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
	public Point getPositionInRoot();

	
	
	/*****************************************************************
	 * SUB DUNGEONS (introduced here instead of CompositeDungeon, to not violate LSP)
	 *****************************************************************/

	/**
	 * Get all the levels and shafts that are contained within this dungeon.
	 * 
	 * @return A collection of SquareDungeons that is contained within this dungeon.
	 * 		The returned dungeons have square collections that are mutually disjoint, but that are
	 * 		sub sets of the squares in this dungeon. Their union is the set of squares in this dungeon.
	 * 		(so they for a partition)
	 * 		| for each dungeonOne in result:
	 * 		|	for each dungeonTwo in result:
	 * 		|		if dungeonOne != dungeonTwo
	 * 		|		then for each squareOne in dungeonOne:
	 * 		|				for each squareTwo in dungeonTwo:
	 * 		|					squareOne != squareTwo
	 * 		|				&& hasAsSquare(squareOne)
	 */
	@Basic
	public List<SquareDungeon> getLevelsAndShafts();

	/**
	 * Retrieve the square dungeon that contains the given position.
	 * 
	 * @param position
	 * 		Some position relative to the origin of this dungeon.
	 * @return The dungeon that contains the given position
	 * @see CompositeDungeon
	 */
	public SquareDungeon getDungeonContainingPosition(Point position);

}
