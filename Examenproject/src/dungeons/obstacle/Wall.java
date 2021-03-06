/**
 * The package for a roleplaying game.
 */
package dungeons.obstacle;

import dungeons.square.Square;
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
	 * @return If the square is null then return false. Else return true.
	 * 		|if(square == null)
	 * 		|	then return == false
	 * 		|else
	 * 		|	return == true
	 */
	public boolean canBeAnObstacleAt(Square square, Direction dir) {
		if(square == null){
			return false;
		}
		else{
			return true;
		}
	}
	
	/**
	 * Method that indicates if the wall is moveable through.
	 * 
	 * @return Returns false for a wall.
	 * 		|result == false
	 */
	@Override
	public boolean canMoveThrough(){
		return false;
	}
}
