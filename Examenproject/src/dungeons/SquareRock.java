/**
 * The package for a roleplaying game.
 */
package dungeons;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;


/**
 * A class for the creation of objects of Rocks. A rock has 6 directions. 
 * In each direction there can be an obstacle.
 * There is also a specific temperature for the square.
 * 
 * @author Christof Vermeersch & Edward Van Sieleghem
 * @invar The square can maximum have 6 boundaries.
 * 		|getNbOfObstacles() <= 6
 * @invar The obstacles must be valid.
 * 		|hasProperObstacles() ==  true
 * @invar The neighbors must be valid.
 * 		|hasProperNeighbors() == true
 * @invar The temperature must be the same as the other squares of its group.
 * 		Also the temperature must be between valid boundaries.
 * 		|hasProperTemperature() == true
 */
public class SquareRock extends Square {

	/**
	 * A standard constructor.
	 * This constructor doesn't have any arguments. The temperature will be set to a default value. 
	 * 
	 * @post The temperature is set to a default value.
	 * 		|new.getTemperature() == DEFAULT_TEMPERATURE
	 * @post The newly created square is surrounded by walls.
	 * 		|for each direction in Direction.values(): 
	 * 		|	new.hasWall(direction) == true
	 */
	public SquareRock(){
		super();
	}
	
	/**
	 * Inspector to tell if the square can sometimes not be surrounded by walls.
	 * This is a property of a Rock.
	 * 
	 * @return Returns false for a rock, because it must always be surrounded by walls.
	 * 		|result == false
	 */
	@Basic
	@Immutable
	public boolean notAlwaysSurroundedByWalls(){
		return this.NOTALWAYSSURROUNDEDBYWALLS;
	}
	
	
	/**
	 * Variable to store if this square must be surrounded by wall at all time or not.
	 * This is a property of a rock.
	 */
	private final boolean NOTALWAYSSURROUNDEDBYWALLS = false;
}
