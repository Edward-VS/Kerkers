/**
 * The package for a roleplaying game.
 */
package dungeons.obstacle;

import dungeons.Square;
import dungeons.util.Direction;
import be.kuleuven.cs.som.annotate.*;

/**
 * A class for the creation of a door, which is an extention of the class Obstacle.
 * 
 * @author Christof Vermeersch & Edward Van Sieleghem
 */
public class Door extends Obstacle {
	
	/**
	 * Constructor to create a door.
	 * The door will be standard closed.
	 * This constructor doesn't require any arguments.
	 * 
	 * @post The door will be open.
	 * 		|new.isOpen() == false
	 */
	public Door(){
		this.isOpen = false;
	}
	
	/**
	 * Constructor to create a door.
	 * This constructor has an argument to determine if the door is open or not.
	 * 
	 * @param isOpen Variable to determine if the door is open.
	 * @post The door will be open if isOpen is true.
	 * 		|new.isOpen() == isOpen
	 */
	public Door(Boolean isOpen){
		this.isOpen = isOpen;
	}
	
	/**
	 * Basic inspector to check if the door is open.
	 */
	@Basic
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * Method to change the door moveability.
	 * 
	 * @param isOpen The value to set the door open (true) or closed (false).
	 * @post The door will be set to the value of isOpen.
	 * 		|new.isOpen() == isOpen
	 */
	public void setOpen(Boolean isOpen) {
		this.isOpen = isOpen;
	}
	
	/**
	 * Method to change the door moveability.
	 * 
	 * @post The door will be set open.
	 * 		|new.isOpen() == true
	 */
	public void setOpen() {
		this.isOpen = true;
	}
	
	/**
	 * Method to check if the door can be an obstacle of a square at a given direction.
	 * 
	 * @param square The square where the obstacle is needed.
	 * @param dir Direction in which the obstacle is needed.
	 * @return Returns true if there is a neighbor at the given direction.
	 * 		|return == square.hasNeighborAt(dir)
	 */
	public boolean canBeAnObstacleAt(Square square, Direction dir) {
		return square.hasNeighborAt(dir);
	}
	
	/**
	 * Variable to store the moveability of the door.
	 */
	private boolean isOpen;

}
