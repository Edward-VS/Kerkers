/**
 * The package for a roleplaying game.
 */
package dungeons.obstacle;

import dungeons.Square;
import dungeons.util.Direction;

/**
 * Class for the creation of walls.
 * 
 * @author Christof Vermeersch & Edward Van Sieleghem
 */
public class Wall extends Obstacle {
	
	/**
	 * Constructor to make a wall.
	 */
	public Wall(){}
	
	/**
	 * Method to check if the wall can be an obstacle of a square at a given direction.
	 * 
	 * @param square The square where the obstacle is needed.
	 * @param dir Direction in which the obstacle is needed.
	 * @return Returns always true.
	 * 		|return == true
	 */
	public boolean canBeAnObstacleAt(Square square, Direction dir) {
		return square.hasNeighborAt(dir);
	}
}
