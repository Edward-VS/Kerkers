/**
 * The package for a roleplaying game.
 */
package dungeons.square;

import dungeons.util.Direction;
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
 * 		A group is the square itself because of the walls at all boundaries.
 * 		The temperature must be the the average of the surrounding temperatures.
 * 		|hasProperTemperature() == true 		
 */
public class Rock extends Square {

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
	public Rock(){
		super();
	}
	
	/**
	 * Checker that tells if the current temperature is valid.
	 * 
	 * @return Returns true if the temperature is between the two boundary temperatures and the temperature
	 * 		is the same as in the other squares of the group containing this square.
	 * 		|return == (isValidTemperature(this.getTemperature()) 
	 * 		|			&& (this.getTemperature() == this.calculateUpdateTemperature())
	 */
	public boolean hasProperTemperature(){
		return (isValidTemperature(this.getTemperature()) && (this.getTemperature() == this.calculateUpdateTemperature()));
	}
	
	/**
	 * Method to calculate the temperature of a rock without changing it.
	 * Other methods will then update the temperature.
	 * 
	 * @return Because the group is limited too the square itself the group temperature is no longer a problem.
	 * 		But now the temperature must be the average of the temperature of the surrounding squares.
	 * 		|for each direction in Direction.Values():
	 * 		|	sum = sum + this.getNeighborAt(direction).getTemperature()
	 * 		| 	i=i+1
	 * 		|result == sum / i
	 */
	public int calculateUpdateTemperature() {
		int sum = 0;
		int i =0;
		for(Direction dir: Direction.values()){
			sum = sum + this.getNeighborAt(dir).getTemperature();
			i=i+1;
		}
		return sum/i;
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
	
	/**
	 * Check whether this square can collapse.
	 * 
	 * @return Always false
	 */
	@Override
	public boolean canCollapse(){
		return false;
	}
	
	/**
	 * Check whether this square can make a square bolow it collapse.
	 * 
	 * @return Always false
	 */
	@Override
	public boolean canMakeCollapse(){
		return true;
	}
}
