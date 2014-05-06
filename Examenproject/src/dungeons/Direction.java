/**
 * The package for a roleplaying game.
 */
package dungeons;

/**
 * Enum with the different directions.
 * 
 * @author Christof Vermeersch & Edward Van Sieleghem
 */
public enum Direction {
	NORTH, WEST, SOUTH, EAST, UP, DOWN;
	
	// TODO Controleren van specificatie.
	/**
	 * Method to calculate the opposite direction.
	 * 
	 * @return If this direction is NORTH, then the opposite direction will be SOUTH
	 * 		|If(this == Direction.NORTH) => return == Direction.SOUTH
	 * @return If this direction is WEST, then the opposite direction will be EAST
	 * 		|If(this == Direction.WEST) => return == Direction.EAST
	 * @return If this direction is SOUTH, then the opposite direction will be NORTH
	 * 		|If(this == Direction.SOUTH) => return == Direction.NORTH	
	 * @return If this direction is EAST, then the opposite direction will be WEST
	 * 		|If(this == Direction.EAST) => return == Direction.WEST
	 * @return If this direction is UP, then the opposite direction will be DOWN
	 * 		|If(this == Direction.UP) => return == Direction.DOWN 
	 * @return If this direction is DOWN, then the opposite direction will be UP
	 * 		|If(this == Direction.DOWN) => return == Direction.UP	 
	 */
	public Direction oppositeDirection(){
		Direction thisDirection = this;
		Direction oppositeDirection = null;
		switch(thisDirection){
		case NORTH: 
			oppositeDirection = SOUTH;
			break;
		case WEST:
			oppositeDirection = EAST;
			break;
		case SOUTH:
			oppositeDirection = NORTH;
			break;
		case EAST:
			oppositeDirection = WEST;
			break;
		case UP:
			oppositeDirection = DOWN;
			break;
		case DOWN:
			oppositeDirection = UP;
			break;
		}
		return oppositeDirection;
	}
}
