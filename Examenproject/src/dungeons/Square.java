/**
 * The package for a roleplaying game.
 */
package dungeons;

import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.ArrayList;

import be.kuleuven.cs.som.annotate.*;
import dungeons.util.*;
import dungeons.obstacle.*;

/**
 * A class for the creation of objects of Squares.
 * 
 * @author Christof Vermeersch & Edward Van Sieleghem
 * @invar The square can maximum have 6 boundaries.
 * 		|obstacleMap.size() <= 6
 * @invar The obstacles must be valid.
 * 		|hasProperObstacles() ==  true
 * @invar The neighbors must be valid.
 * 		|hasProperNeighbors() == true
 * @invar The temperature must be the same as the other squares of its group.
 * 		Also the temperature must be between valid boundaries.
 * 		|hasProperTemperature() == true
 */
public class Square {

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
	public Square() {
		this.temperature = DEFAULT_TEMPERATURE;
		this.neighborsMap = new EnumMap<Direction, Square>(Direction.class);
		this.obstaclesMap = new EnumMap<Direction, Obstacle>(Direction.class);
		for (Direction d : Direction.values()) {
			this.buildWallAt(d);
		}
	}

	/**
	 * A temperature constructor.
	 * This constructor has a temperature as argument.
	 * 
	 * @param temp The given temperature.
	 * @post The temperature is set to the given value.
	 * 		|new.getTemperature() == temp
	 * @post The newly created square is surrounded by walls.
	 * 		|for each direction in Direction.values(): 
	 * 		|	new.hasWall(direction) == true
	 */
	public Square(int temp) {
		this.temperature = temp;
		this.neighborsMap = new EnumMap<Direction, Square>(Direction.class);
		this.obstaclesMap = new EnumMap<Direction, Obstacle>(Direction.class);
		for (Direction d : Direction.values()) {
			this.buildWallAt(d);
		}
	}

	/* TERMINATION */

	/**
	 * Method to check if the square is terminated.
	 */
	@Basic
	public boolean isTerminated() {
		return isTerminated;
	}

	/**
	 * Terminator
	 * 
	 * @post The square is set terminated.
	 * 		|new.isTerminated() == true
	 * @post The square is surrounded by walls. 
	 * 		|for each direction in Direction.values(): new.hasWall(direction) == true
	 * @post At the place where the square had neighbors, the neighbors will also have walls.
	 * 		|for each direction in Direction.values(): 
	 * 		|	if(old.getNeighborAt(direction != null) 
	 * 		|		then (new.(old.getNeighborAt(direction))).hasWall(direction) == true
	 * @post The square doesn't have any neighbors left.
	 * 		|new.getNeighborsMap().isEmpty() == true
	 */
	public void terminate() {
		for (Direction d : Direction.values()) {
			this.buildWallAt(d);
			this.removeNeighbor(d);
		}
		isTerminated = true;
	}

	/**
	 * Variable to store if the square is terminated. True if so.
	 */
	private boolean isTerminated;

	/* TEMPERATURE (Defensive) */

	/**
	 * Inspector to check the temperature.
	 */
	@Basic
	public int getTemperature() {
		return temperature;
	}

	/**
	 * Inspector to check the default temperature.
	 */
	@Basic
	@Immutable
	public static int getDefaultTemperature() {
		return DEFAULT_TEMPERATURE;
	}

	/**
	 * Inspector to check the maximum temperature.
	 */
	@Basic
	public static int getMaxTemperature() {
		return max_temperature;
	}

	/**
	 * Inspector to check the minimum temperature.
	 */
	@Basic
	public static int getMinTemperature() {
		return min_temperature;
	}

	/**
	 * Inspector to check the unit of heat damage for each step.
	 */
	@Basic
	public static int getUnitHeatDamage() {
		return unitHeatDamage;
	}

	/**
	 * Inspector to check the step for heat damage.
	 */
	@Basic
	public static int getHeatDamageStep() {
		return heatDamageStep;
	}

	/**
	 * Inspector to check the limit from where the damage starts to count.
	 */
	@Basic
	public static int getHeatDamageAbove() {
		return heatDamageAbove;
	}

	/**
	 * Checker that tells if the given temperature is valid as start temperature for an isolated square.
	 * @param temp The temperature to test.
	 * @return Returns true if the temperature is between the two boundary temperatures.
	 * 		|return == (temp < this.getMaxTemperature() && temp < this.getMinTemperature())
	 */
	public static boolean isValidTemperature(int temp) {
		return (temp < Square.getMaxTemperature() && temp > Square.getMinTemperature());
	}
	
	/**
	 * Checker that tells if the current temperature is valid.
	 * @return Returns true if the temperature is between the two boundary temperatures and the temperature
	 * 		is the same as in the other squares of the group containing this square.
	 * 		|return == (isValidTemperature(this.getTemperature()) 
	 * 		|			&& (this.getTemperature() == this.calculateUpdateTemperature())
	 */
	public boolean hasProperTemperature(){
		return (isValidTemperature(this.getTemperature()) && (this.getTemperature() == this.calculateUpdateTemperature()));
	}

	/**
	 * A method to calculate the damage due too temperature.
	 * 
	 * @return If the temperature is too hot, 
	 * 		there will be a certain points damage for each specified step of degrees above a certain temperature.
	 * 		|If(this.getTemperature() > this.getHeatDamageAbove()) 
	 * 		|	then return == (((this.getTemperature()-this.getHeatDamageAbove)/this.getHeatDamageStep)*this.getUnitHeatDamage()
	 * @return If the temperature is too cold, 
	 * 		there will be 1 point damage for each 10° below -5°.
	 * 		|If(this.getTemperature() < -5) 
	 * 		|	then return == (abs(this.getTemperature())-5)/10
	 * @return If the temperature is between the boundaries there will be no damage.
	 * 		|If(this.getTemperature() > this.getHeatDamageAbove() && this.getTemperature() < -5) 
	 * 		|	then return == 0
	 */
	public int calculateTemperatureDamage() {
		if (this.getTemperature() > Square.getHeatDamageAbove()) {
			return (this.getTemperature() - Square.getHeatDamageAbove()) / Square.getHeatDamageStep() * Square.getUnitHeatDamage();
		}
		if (this.getTemperature() < -5) {
			return ((Math.abs(this.getTemperature()) - 5) / 10);
		} else {
			return 0;
		}
	}

	/**
	 * Method to calculate the temperature of a group without changing it.
	 * 
	 * @return The temperature of the whole group will be the average of all the squares in the group.
	 * 		|for each square in computeGroup(): 
	 * 		|	return == (for each square in computeGroup():
	 * 		|		 sum = sum + old.square.getTemperature)/calculateGroup().size()
	 */
	public int calculateUpdateTemperature() {
		int sum = 0;
		int average;
		for (Square square : this.computeGroup()) {
			sum = sum + square.getTemperature();
		}
		average = sum / this.computeGroup().size();
		return average;
	}

	/**
	 * Method to set the temperature.
	 * 
	 * @param temp The specified temperature.
	 * @post The current temperature is set to the given one.
	 * 		|new.getTemperature() == temp
	 * @throws IllegalArgumentException [MUST] The given temperature is not valid.
	 * 		|!isValidTemperature(temp) || !new.hasProperTemperature()
	 */
	private void setTemperature(int temp) throws IllegalArgumentException {
		int oldTemperature = this.getTemperature();
		this.temperature =  temp;
		if((!this.hasProperTemperature())||(!Square.isValidTemperature(temp))){
			this.temperature = oldTemperature;
			throw new IllegalArgumentException("The temperature is not valid!");
		}
	}

	/**
	 * Method to set the maximum temperature.
	 * 
	 * @param temp The specified maximum temperature.
	 * @post The current maximum temperature is set to the given one.
	 * 		|new.getMaxTemperature() == temp
	 */
	public static void setMaxTemperature(int temp) {
		Square.max_temperature = temp;
	}

	/**
	 * Method to set the minimum temperature.
	 * 
	 * @param temp The specified minimum temperature.
	 * @post The current minimum temperature is set to the given one.
	 * 		|new.getMinTemperature() == temp
	 */
	public static void setMinTemperature(int temp) {
		Square.min_temperature = temp;
	}

	/**
	 * Method to set new unit for heat damage.
	 * 
	 * @param unit The new unit for heat damage.
	 * @post The unit will be update too the new one.
	 * 		|new.getUnitHeatDamage() == unit
	 */
	public static void setUnitHeatDamage(int unit) {
		Square.unitHeatDamage = unit;
	}

	/**
	 * Method to set new step for heat damage.
	 * 
	 * @param step The new step for heat damage.
	 * @post The step will be update too the new one.
	 * 		|new.getHeatDamageStep() == step
	 */
	public static void setHeatDamageStep(int step) {
		Square.heatDamageStep = step;
	}

	/**
	 * Method to set new limit for heat damage.
	 * 
	 * @param limit The new limit for heat damage.
	 * @post The limit will be update too the new one.
	 * 		|new.getHeatDamageAbove() == limit
	 */
	public static void setHeatDamageAbove(int limit) {
		Square.heatDamageAbove = limit;
	}

	/**
	 * Method to set the temperature of a group.
	 * 
	 * @post The temperature of the whole group will be set to the average of all the squares in the group.
	 * 		|for each square in this.computeGroup(): 
	 * 		|	square.getTemperature() == this.calculateUpdateTemperature()
	 */
	public void updateTemperature() {
		int average = this.calculateUpdateTemperature();
		for (Square square : this.computeGroup()) {
			square.setTemperature(average);
		}
	}

	/**
	 * Variable to store the temperature.
	 */
	private int temperature;

	/**
	 * Variable to store the default temperature.
	 */
	private static final int DEFAULT_TEMPERATURE = 20;

	/**
	 * Variable to store the maximum temperature.
	 */
	private static int max_temperature = 5000;

	/**
	 * Variable to store the minimum temperature.
	 */
	private static int min_temperature = -200;

	/**
	 * Variable to store unit of heat damage for each step.
	 */
	private static int unitHeatDamage = 1;

	/**
	 * Variable to store the step for heat damage.
	 */
	private static int heatDamageStep = 15;

	/**
	 * Variable to store the limit from where the damage starts to count.
	 */
	private static int heatDamageAbove = 35;

	/* BOUNDARIES (Total) */

	/**
	 * Inspector to retrieve all the obstacles of a square.
	 */
	@Basic
	public EnumMap<Direction, Obstacle> getObstaclesMap() {
		return obstaclesMap;
	}

	/**
	 * Inspector to retrieve number of obstacles.
	 * 
	 * @return Returns the number of obstacles for this Square.
	 * 		|return == obstaclesMap.size()
	 */
	public int getNbOfObstacles() {
		return obstaclesMap.size();
	}

	/**
	 * Inspector to retrieve obstacle in given direction.
	 * 
	 * @param dir The selected direction.
	 * @return The obstacle in the given direction.
	 * 		|return == obstaclesMap.get(dir)
	 */
	public Obstacle getObstacleAt(Direction dir) {
		return obstaclesMap.get(dir);
	}

	/**
	 * Inspector to check if a square has a door in a certain direction.
	 * 
	 * @param dir The direction you want to check.
	 * @return Returns true if the square has a door in that direction.
	 * 		|return == (this.getObstacleAt(dir) instanceof Door)
	 */
	public boolean hasDoor(Direction dir) {
		if (this.getObstacleAt(dir) instanceof Door) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Inspector to check if a door in a certain direction is open.
	 * 
	 * @param dir The direction in which you want to check the door.
	 * @return If there is a door in that direction, returns true if the door is open.
	 * 		|if(this.hasDoor(dir))
	 * 		|	then return == ((Door)this.getObstacleAt(dir)).isOpen()
	 * @return If there is no door in that direction, returns false.
	 * 		|if(!this.hasDoor(dir))
	 * 		|	then return == false
	 */
	public boolean isOpen(Direction dir){
		if(this.hasDoor(dir)){
			return ((Door)this.getObstacleAt(dir)).isOpen();
		}
		else{
			return false;
		}
	}

	/**
	 * Inspector to check if a square has a wall in a certain direction.
	 * 
	 * @param dir The direction you want to check.
	 * @return Returns true if the square has a wall in that direction.
	 * 		|return == (getObstacleAt(dir) instanceof Wall)
	 */
	public boolean hasWall(Direction dir) {
		if (this.getObstacleAt(dir) instanceof Wall) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method to check if a square and its neighbor has the same obstacle in the given direction, seen from this square.
	 * 
	 * @param dir The direction.
	 * @return Returns true if the obstacle is the same for this and his neighbor in a given direction.
	 * 		This is also important for the moveability.
	 * 		|if(this.hasWall(dir)) 
	 * 		|	then return == this.getNeighborAt(dir).hasWall(dir.oppositeDirection()) 
	 * 		|if(this.hasDoor(dir)) 
	 * 		|	then return == this.getNeighborAt(dir).hasDoor(dir.oppositeDirection())
	 * 		|				&& (this.isOpen(dir) == this.getNeighborAt(dir).isOpen(dir.oppositeDirection())
	 * 		|if(!this.hasDoor(dir) && !this.hasWall(dir)) 
	 * 		|	then return == !this.getNeighborAt(dir).hasDoor(dir.oppositeDirection()) 
	 * 		|				&& !this.getNeighborAt(dir)hasWall(dir.oppositeDirection())
	 */
	public boolean hasSameAdjecantObstacleAt(Direction dir) {
		if (this.hasWall(dir)) {
			return this.getNeighborAt(dir).hasWall(dir.oppositeDirection());
		}
		if (this.hasDoor(dir)) {
			return ((this.getNeighborAt(dir).hasDoor(dir.oppositeDirection())) && (this.isOpen(dir) == this.getNeighborAt(dir).isOpen(dir.oppositeDirection())));
		} else {
			return !this.getNeighborAt(dir).hasDoor(dir.oppositeDirection()) && !this.getNeighborAt(dir).hasWall(dir.oppositeDirection());
		}

	}
	
	// TODO Verbeteren klasse tot hier
	/**
	 * Checker that controls if the obstacles now are possible for this square.
	 * 
	 * @return Returns true if all the obstacles are possible for this square 
	 * 		and if there is an neighbor, the neighbor has the same obstacle with same moveability.
	 * 		|return == (for each direction in Direction.values(): 
	 * 		|	this.canHaveAsObstacleAt(direction, this.getObstacleAt(direction)) 
	 * 		|	&& if(this.getNeighborAt(direction) != null) then this.hasSameAdjecantObstacleAt(direction)) 
	 */
	public boolean hasProperObstacles() {
		for (Direction direction : Direction.values()) {
			if (!this.canHaveAsObstacleAt(direction, this.getObstacleAt(direction))) {
				return false;
			}
			if (this.getNeighborAt(direction) != null) {
				if (!this.hasSameAdjecantObstacleAt(direction)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checker that checks if given obstacle is possible in certain direction.
	 * 
	 * @param dir The direction in which you want to check.
	 * @param obstacle The obstacle you want to check.
	 * @return If the obstacle is a wall, then the obstacle is always possible.
	 * 		|if(obstacle instanceof Wall) 
	 * 		|	then return == true
	 * @return If the obstacle is a door, then the obstacle is only possible if there is a neighbor in that direction.
	 * 		|if(obstacle instanceof Door) 
	 * 		|	then return == this.hasNeighborAt(dir) 
	 * @return If the obstacle is an empty reference, so there is no wall or door, then there must be a neighbor in that direction.
	 * 		|if(obstacle == null) 
	 * 		|	then return == this.hasNeighborAt(dir)
	 */
	public boolean canHaveAsObstacleAt(Direction dir, Obstacle obstacle) {
		if (obstacle instanceof Wall) {
			return true;
		}
		if (obstacle instanceof Door) {
			return this.getNeighborAt(dir) != null;
		}
		if (obstacle == null) {
			return this.getNeighborAt(dir) != null;
		} else {
			return false;
		}
	}

	/**
	 * Method to build walls at given direction.
	 *
	 * @param dir The direction in which you want to add a wall.
	 * @post If it is possible to set a wall, the wall will be set at the given direction.
	 * 		|if(canHaveAsObstacle(dir, new Wall()) then new.hasWall(dir) == true
	 * @post If the square has a neighbor at the given direction and the building of a wall is possible, then a wall will also be set in the adjecant square at opposite direction.
	 * 		|If(getNeighbor(dir) != null && canHaveAsObstacle(dir, new Wall()) then getNeighborAt(dir).hasWallAt(dir.oppositeDirection())
	 */
	public void buildWallAt(Direction dir) {
		Obstacle wall = new Wall();
		if (canHaveAsObstacleAt(dir, wall)) {
			this.setObstacleAt(dir, wall);
		}
	}

	/**
	 * Method to build doors at given direction.
	 *
	 * @param dir The direction in which you want to add a wall.
	 * @post If it is possible to set a door, the door will be set at the given direction.
	 * 		|if(canHaveAsObstacle(dir, new Door()) then new.hasDoor(dir) == true
	 * @post If the square has a neighbor at the given direction and the building of a door is possible, then a door will also be set in the adjecant square at opposite direction.
	 * 		|If(getNeighbor(dir) != null && canHaveAsObstacle(dir, new Door()) then getNeighborAt(dir).hasWallAt(dir.oppositeDirection()

	 */
	public void buildDoorAt(Direction dir) {
		Obstacle door = new Door();
		if (canHaveAsObstacleAt(dir, door)) {
			this.setObstacleAt(dir, door);
		}
	}

	/**
	 * Method to set obstacle at given direction.
	 * 
	 * @param dir The direction in which you want to add an obstacle.
	 * @param obstacle The obstacle you want to add.
	 * @post The obstacle will be set at the given direction.
	 * 		|new.getObstacleAt(dir) == obstacle
	 * @post If the square has a neighbor at the given direction, then the obstacle will also be set in the adjecant square at opposite direction.
	 * 		|If(getNeighbor(dir) != null) then getNeighborAt(dir).getObstacleAt(dir.oppositeDirection()) == obstacle
	 */
	private void setObstacleAt(Direction dir, Obstacle obstacle) {
		this.obstaclesMap.put(dir, obstacle);
		if (this.getNeighborAt(dir) != null) {
			this.getNeighborAt(dir).obstaclesMap.put(dir.oppositeDirection(), obstacle);
		}
	}

	/**
	 * Method to destroy a wall between two squares.
	 * 
	 * @param dir The direction in which the wall must be destroyed seen from this square.
	 * @post If it is possible to destroy a wall and there is a wall at the given direction, the wall at the given direction will be destroyed.
	 * 		|if(canHaveAsObstacleAt(dir, null) && this.hasWall(dir)) then new.getObstacleAt(dir) == null
	 * @post If there is a neighbor and the wall can be destroyed, then the wall at the adjecant square at the given direction will also be destroyed.
	 * 		|if(canHaveAsObstacleAt(dir, null) && this.hasWall(dir)) then (new.getNeighborAt(dir)).getObstacleAt(dir.oppositeDirection) == null
	 */
	public void destroyWallAt(Direction dir) throws IllegalArgumentException {
		if ((this.canHaveAsObstacleAt(dir, null)) && (this.hasWall(dir))) {
			this.setObstacleAt(dir, null);
			if (this.getNeighborAt(dir) != null) {
				this.getNeighborAt(dir).setObstacleAt(dir.oppositeDirection(), null);
			}
		}
	}

	/**
	 * When the obstacles aren't proper then this method will fix them.
	 * 
	 * @post The method will set walls in every direction where there is no neighbor and this square hasn't a proper obstacle.
	 * 		|for each dir in Direction.values(): if(this.getNeighborAt(dir) == null && !canHaveAsObstacleAt(dir,this.getObstacleAt()) then new.hasWallAt(dir)
	 * @post The method will set walls in every direction where the neighbor has walls and this square hasn't a proper obstacle.
	 * 		|for each dir in Direction.values(): if(this.getNeighborAt(dir).hasWallAt(dir.oppositeDirection()) && !canHaveAsObstacleAt(dir,this.getObstacleAt()) then new.hasWallAt(dir) == true
	 * @post The method will set doors in every direction where the neighbor has doors and this square hasn't a proper obstacle.
	 * 		|for each dir in Direction.values(): if(this.getNeighborAt(dir).hasDoorAt(dir.oppositeDirection()) && !canHaveAsObstacleAt(dir,this.getObstacleAt()) then new.hasDoorAt(dir) == true && (Door new.getObstacleAt(dir)).isOpen() == (Door new.getNeighborAt(dir).getObstacleAt(dir.oppositeDirection())).isOpen()
	 * @post The method will destroy doors en walls in every direction where the neighbor hasn't any obstacles and this square hasn't a proper obstacle.
	 * 		|for each dir in Direction.values(): if(this.getNeighborAt(dir).getObstacleAt(dir.oppositeDirection() == null && !canHaveAsObstacleAt(dir,this.getObstacleAt()) then new.getObstacleAt(dir) == null
	 */
	public void fixObstacles() {
		for (Direction dir : Direction.values()) {
			if (!this.canHaveAsObstacleAt(dir, this.getObstacleAt(dir))) {
				if (this.getNeighborAt(dir) == null) {
					this.setObstacleAt(dir, new Wall());
				} else {
					if (this.getNeighborAt(dir).getObstacleAt(dir.oppositeDirection()) == null) {
						this.setObstacleAt(dir, null);
					}
					if (this.getNeighborAt(dir).hasWall(dir.oppositeDirection())) {
						this.setObstacleAt(dir, new Wall());
					}
					if (this.getNeighborAt(dir).hasDoor(dir.oppositeDirection())) {
						this.setObstacleAt(dir, new Door());
						((Door) this.getObstacleAt(dir))
								.setMoveable(((Door) this.getNeighborAt(dir).getObstacleAt(dir.oppositeDirection())).isOpen());
					}
				}
			}
		}
	}

	/**
	 * Variable to store the obstacles of a square.
	 */
	private EnumMap<Direction, Obstacle> obstaclesMap;

	/* neighborS (Total) */

	/**
	 * Inspector to retrieve all the neighbors of a square.
	 */
	@Basic
	public EnumMap<Direction, Square> getNeighborsMap() {
		return neighborsMap;
	}

	/**
	 * Inspector to receive the number of neighbors.
	 * 
	 * @return Returns the number of squares connected to this one.
	 * 		|return == NeighborsMap.size()
	 */
	public int getNbOfNeighbors() {
		return neighborsMap.size();
	}

	/**
	 * Inspector to retrieve neighbors in given direction.
	 * 
	 * @param dir The selected direction.
	 * @return The neighbor in the given direction.
	 * 		|return == NeighborsMap.get(dir)
	 */
	public Square getNeighborAt(Direction dir) {
		return neighborsMap.get(dir);
	}

	/**
	 * Checker to control if this square has a given square as neighbor.
	 * 
	 * @param square The square that could be connected to this one.
	 * @return Returns true if the square is connected to this one.
	 * 		|result == (for some Neighbor in getNeighborsMap(): Neighbor == square)
	 */
	public boolean hasAsNeighbor(Square square) {
		for (Square s : neighborsMap.values()) {
			if (square == s) {
				return true;
			}
		}
		return false;
	}

	/**
	* Check whether a neighbor is connected in the given direction.
	* 
	* @param dir The direction to look in.
	* @param square The square to check.
	* @return True if the neighbor exists in the specific direction.
	*		|result == (getNeighborAt(dir) == square)
	*/
	public boolean hasAsNeighborAt(Direction dir, Square square) {
		return (this.getNeighborAt(dir) == square);
	}

	/**
	* Check whether this square has proper neighbors.
	* 
	* @return True if for each neighbor of this square in a specific direction, that neighbor
	* also has this square as its neighbor in the inverse of that direction, and that neighbor
	* is legal.
	* 		| result == ( for each direction in getNeighborsMap(): (canHaveAsNeighbor(getNeighborsMap().get(direction)) && getNeighborsMap().get(direction).hasAsNeighborAt(direction.oppositeDirection(), this))
	*/
	public boolean hasProperNeighbors() {
		for (Entry<Direction, Square> e : neighborsMap.entrySet()) {
			if (!canHaveAsNeighbor(e.getValue()) || !e.getValue().hasAsNeighborAt(e.getKey().oppositeDirection(), this)) {
				return false;
			}
		}
		return true;
	}

	/**
	* Check whether the given square is valid as a neighbor for this square.
	* 
	* @param square The square to check.
	* @return If this square is terminated, then true if the given square is null.
	* 		|isTerminated() then ( result == ( square == null ) )
	* @return Else if the given square is not null, true if the given square is not terminated.
	*		|(!isTerminated() && square != null ) then ( result == !square.isTerminated() )
	* @return Else always true
	* 		|(!isTerminated() && square == null ) then ( result == true )
	* @note If a neighbor is accepted by this method, it is automatically accepted in all directions.
	* Method 'canHaveAsNeighborAt(Direction)' is not needed.
	*/
	public boolean canHaveAsNeighbor(Square square) {
		if (isTerminated) {
			return square == null;
		} else {
			if (square != null)
				return !square.isTerminated();
			return true;
		}
	}

	/**
	 * Method to set neighbor at given direction.
	 * 
	 * @param dir The direction in which you want to add a neighbor.
	 * @param square The square you want to add.
	 * @post The square will be set connected at the given direction.
	 * 		|new.getNeighborAt(dir) == square
	 * @throws IllegalArgumentException [MUST] The given square is not a valid neighbor.
	 * 		|!canHaveAsNeighbor(square)
	 */
	@Model
	@Raw
	private void setNeighborAt(Direction dir, Square square) throws IllegalArgumentException {
		if (!canHaveAsNeighbor(square)) {
			throw new IllegalArgumentException("The given square is not a valid square to connect!");
		} else {
			if (square == null) {
				this.neighborsMap.remove(dir);
			} else {
				this.neighborsMap.put(dir, square);
			}
		}
	}

	/**
	 * Method to copy all the neighbors from a given square to this one.
	 * 
	 * @param other The other square from which you want to copy.
	 * @post If the given square is not null, all the neighbors will be copied to this one. The directions will be the same.
	 * 		|if(square != null) then (for each direction in Direction.values(): new.getNeighborAt(direction) == other.getNeighborAt(direction)
	 * @note This is annotated raw because the temperature is not updated.
	 */
	@Raw
	public void copyNeighbors(Square other) {
		if (other != null) {
			this.neighborsMap = other.neighborsMap;
		}
	}

	/**
	* Register the given square as a neighbor of this square in the given direction.
	* 
	* @param square The square to register as a neighbor
	* @param direction The direction of the given square relative to this square.
	* @post If the new neighbor is a legal neighbor, this square has the given square as its neighbor in the given direction. Also the given square has this square as its neighbor in the inverse of the given direction.
	*	   |if(this.canHaveAsNeighbor(square) && square.canHaveAsNeighbor(this))	then (new.getNeighborAt(direction) == square && (new.square).getNeighborAt(Direction.oppositeDirection()) == this )
	* @note Annotated raw because this method does not update temperature.
	*/
	@Raw
	public void registerNeighbor(Square square, Direction direction) {
		if (this.canHaveAsNeighbor(square) && square.canHaveAsNeighbor(this)) {
			try {
				this.setNeighborAt(direction, square);
				square.setNeighborAt(direction.oppositeDirection(), this);
			} catch (IllegalArgumentException e) {
				// Should never happen
				assert false;
			}
		}
	}

	/**
	* Remove the neighbor of this square in the given direction, if it can be removed.
	* 
	* @param direction The direction of the square to remove as neighbor.
	* @post If the neighbor can be removed, this square does not have the square at the given direction as its neighbor anymore.
	* 		Also the respective neighboring square does not have this square as its neighbor anymore.
	* 		|if(this.canHaveAsNeighbor(null) && (this.getNeighborAt(direction).canHaveAsNeighbor(null)) then (new.getNeighborAt(direction) == null) && ((new.this).getNeighborAt(direction)).getNeighborAt(direction.oppositeDirection() == null )
	*/
	public void removeNeighbor(Direction direction) {
		Square other = getNeighborAt(direction);
		if (other != null) {
			if (this.canHaveAsNeighbor(null) && other.canHaveAsNeighbor(null)) {
				try {
					setNeighborAt(direction, null);
					other.setNeighborAt(direction.oppositeDirection(), null);
				} catch (IllegalArgumentException e) {
					// Should never happen
					assert false;
				}
			}
		}
	}

	/**
	 * Variable to store the neighbors of a square.
	 */
	private EnumMap<Direction, Square> neighborsMap;

	/* MERGING SQUARES (Defensive) */

	/**
	 * Method that checks if you can move in a certain direction.
	 * 
	 * @param dir The direction to move in.
	 * @return Returns true if there is neighbor in that direction and there is no wall between them or the door is open.
	 * 		|result == ((this.getNeighborAt(dir) != null) && (!this.hasWallAt(dir)) && (if(this.hasDoorAt(dir)) then getObstacleAt(dir).isOpen())
	 */
	public boolean canMove(Direction dir) {
		if (this.getNeighborAt(dir) != null) {
			if (this.hasWall(dir)) {
				return false;
			}
			if (this.hasDoor(dir)) {
				return ((Door) this.getObstacleAt(dir)).isOpen();
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	// TODO recursive method arraylist...
	/**
	 * Method to compute a group of moveable squares.
	 * 
	 * @return Creates a group of moveable squares. The list will contain adjecant squares that are moveable.
	 * 		|return == ...
	 */
	public ArrayList<Square> computeGroup() {
		// invar: The ArrayList contains adjecant squares that are movable.
		// No duplicates
		ArrayList<Square> group = new ArrayList<Square>();
		for (Direction dir : Direction.values()) {
			if (this.canMove(dir)) {
				group.add(this.getNeighborAt(dir));
				group.addAll(this.getNeighborAt(dir).computeGroup());
			}
		}
		return group;
	}

	/**
	 * Method to merge squares in the given direction. This method applies to single squares who are already connected.
	 * 
	 * @param dir The direction in which you want to merge the square.
	 * @post The new temperature will be the average temperature of both squares.
	 * 		|new.getTemperature() == (old.getTemperature()+(old.getNeighborAt(dir)).getTemperature())/2
	 * @post The merged square will have the same temperature as the new temperature of this one.
	 * 		|(new.getNeighborAt(dir)).getTemperature() == new.getTemperature()
	 * @throws IllegalArgumentException [MUST] The direction is not valid if there doesn't exist a neighbor at the given direction.
	 * 		|getNeighborAt(dir) == null
	 */
	public void mergeWith(Direction dir) throws IllegalArgumentException {
		if (this.getNeighborAt(dir) != null) {
			this.temperature = (this.getTemperature() + this.getNeighborAt(dir).getTemperature()) / 2;
			this.getNeighborAt(dir).setTemperature(this.getTemperature());
		} else {
			throw new IllegalArgumentException("There is no square to merge in that direction!");
		}
	}

	/**
	 * Method to merge a group of squares with another group of squares.
	 * 
	 * @param dir The direction of the other group.
	 * @post The temperature of each square in this group will be the average of the squares in the two groups, taken in account the size of the two groups.
	 * 		|for each square in calculateGroup(): square.getTemperature() == (new.computeGroup().size()*old.calculateUpdateTemperature() + new.getNeighborAt(dir).computeGroup().size()*old.getNeighborAt(dir).calculateUpdateTemperature())/(new.calculateGroup().size() + new.getNeighborAt(dir).calculateGroup().size())
	 * @post The temperature of each square in the group of the other square will be the average of the squares in the two groups, taken in account the size of the two groups.
	 * 		|for each square in this.getNeighborAt(dir).computeGroup(): square.getTemperature() == (new.computeGroup().size()*old.calculateUpdateTemperature() + new.getNeighborAt(dir).computeGroup().size()*old.getNeighborAt(dir).calculateUpdateTemperature())/(new.calculateGroup().size() + new.getNeighborAt(dir).calculateGroup().size())
	 * @throws IllegalArgumentException [MUST] The direction is not valid if there doesn't exist a neighbor at the given direction.
	 * 		|getNeighborAt(dir) == null
	 */
	public void mergeWithGroup(Direction dir) {
		if (this.getNeighborAt(dir) != null) {
			this.updateTemperature();
			this.getNeighborAt(dir).updateTemperature();
			int average = (this.getTemperature() * this.computeGroup().size() + this.getNeighborAt(dir).getTemperature()
					* this.getNeighborAt(dir).computeGroup().size())
					/ (this.computeGroup().size() + this.getNeighborAt(dir).computeGroup().size());
			for (Square square : this.computeGroup()) {
				square.setTemperature(average);
			}
			for (Square square : this.getNeighborAt(dir).computeGroup()) {
				square.setTemperature(average);
			}
		}
	}

}
