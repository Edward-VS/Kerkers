/**
 * The package for a roleplaying game.
 */
package dungeons;

import be.kuleuven.cs.som.annotate.*;


/**
 * A class for the creation of objects of Teleportationsquares. A square has 6 directions. 
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
 * @invar The Teleportationsquare must have a valid targetsquare.
 * 		|hasProperTarget() == true
 */
public class TeleportationSquare extends Square {
	
	/**
	 * Standard constructor for a teleportationsquare.
	 * 
	 * @param square. The square where this square teleports to.
	 * @post The temperature is set to a default value.
	 * 		|new.getTemperature() == DEFAULT_TEMPERATURE
	 * @post The newly created square is surrounded by walls.
	 * 		|for each direction in Direction.values(): 
	 * 		|	new.hasWall(direction) == true
	 * @post The square is set as the target for the teleportation.
	 * 		|new.getTarget() == square
	 * @throws IllegalArgumentException [MUST] The target isn't valid
	 * 		|!isValidTarget(square)
	 */
	public TeleportationSquare(Square square){
		super();
		if(!this.isValidTarget(square)){
			throw new IllegalArgumentException("This is not a valid target!");
		}
		this.target = square;
	}
	
	/**
	 * Method to check if the suggested target is valid.
	 * 
	 * @param target The target to check.
	 * @return If this square is the same as the target, or the target isn't in a dungeon, or the target is a rock,
	 * 		then the target isn't valid.
	 * 		|if((this == target) || (target.getDungeon() == null) || (target.notAlwaysSurroundedByWalls() == false))
	 * 		|	then return false
	 * 		Else return true.
	 * 		|else return == true
	 */
	public boolean isValidTarget(Square target){
		if((this == target) || (target.getDungeon() == null) || (target.notAlwaysSurroundedByWalls() == false)){
			return false;
		}
		else{
			return true;
		}
	}
	
	/**
	 * Inspector to check if the target is valid.
	 * 
	 * @effect Returns true if the current target is valid.
	 * 		|isValidTarget(this.getTarget())
	 */
	public boolean hasProperTarget(){
		return this.isValidTarget(this.getTarget());
	}
	
	/**
	 * Inspector to return the target.
	 */
	@Basic
	public Square getTarget(){
		return target;
	}
	
	/**
	 * Method to set a new target.
	 * 
	 * @param target The target.
	 * @post If the target is valid, then the target is changed to the given one.
	 * 		|if(isValidtarget(target)
	 * 		|	then new.getTarget() == target
	 * @throws IllegalArgumentException [MUST] The target isn't valid
	 * 		|!isValidTarget(square)
	 */
	public void setTarget(Square target){
		if(this.isValidTarget(target)){
			this.target = target;
		}
		else {
			throw new IllegalArgumentException("This is not a valid target!");
		}
	}
	
	/**
	 * Variable to store the target.
	 */
	private Square target;
}
