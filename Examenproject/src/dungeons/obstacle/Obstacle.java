/**
 * The package for a roleplaying game.
 */
package dungeons.obstacle;

import dungeons.square.Square;
import dungeons.util.Direction;

/**
 * Class for the creation of obstacles.
 * 
 * @author Christof Vermeersch & Edward Van Sieleghem
 */
public abstract class Obstacle {

	/**
	 * Constructor for an obstacle.
	 */
	public Obstacle(){}
	
	/**
	 * Method to check if the obstacle can be an obstacle of a square at a given direction.
	 * 
	 * @param square The square where the obstacle is needed.
	 * @param dir Direction in which the obstacle is needed.
	 * @return Returns always true.
	 * 		|result == true
	 */
	public boolean canBeAnObstacleAt(Square square, Direction dir) {
		return true;
	}
	
	/**
	 * Method that indicates if the obstacle is moveable through.
	 * 
	 * @return Returns true for a standard obstacle.
	 * 		|result == true
	 */
	public boolean canMoveThrough(){
		return true;
	}
	

}
