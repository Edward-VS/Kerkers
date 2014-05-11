/**
 * The package for a roleplaying game.
 */
package dungeons;

import java.util.EnumMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import be.kuleuven.cs.som.annotate.*;
import dungeons.util.*;
import dungeons.obstacle.*;

/**
 * TODO algemeen:
 * 	-pre concdities op argumenten van methoden, of extra condities verwerken in
 * 		de documentatie van methoden -> implementatie moeten rekening kunnen houden met null waarden.
 *  - verwissel argument volgorde van methoden zoals canHaveAsObstacleAt (logischer)
 *  - netter zijn in formele logica: bv. veelal zijn haakjes net achter 'if' overbodig
 *  - kweet nie zeker of calculateUpdateTemperature zal werken in de tests
 *  - Eventuele oplossing voor alle liskov problemen: twee aparte enumMappen, één voor door en één voor wall.
 *  - klasse documentatie
 *  
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

	
	
	/***********************************************************
	 * TERMINATION
	 ***********************************************************/

	/**
	 * Method to check if the square is terminated.
	 */
	@Basic
	public boolean isTerminated() {
		return isTerminated;
	}

	/**
	 * Terminator, to tear down bidirectional association between neighbors.
	 * !NOTE! This terminator lacks in the fact that it is unable the check whether the square
	 * is still present in any Dungeon. If it is still present, but in a terminated state, then
	 * invariants of Dungeon are broken!
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
	 * 		|new.getNeighbors().isEmpty() == true
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

	
	
	
	/***********************************************************
	 * TEMPERATURE (Defensive)
	 ***********************************************************/

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
	 * @param temp
	 * 		The specified temperature.
	 * @post The current temperature is set to the given one.
	 * 		| new.getTemperature() == temp
	 * @throws IllegalArgumentException [MUST] The given temperature is not valid.
	 * 		| !isValidTemperature(temp)
	 */
	private void setTemperature(int temp) throws IllegalArgumentException {
		if(!Square.isValidTemperature(temp))
			throw new IllegalArgumentException("The temperature is not valid!");
		this.temperature = temp;
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
	 * @note This method is annotated raw because it can be used when 
	 * 		the temperature isn't yet in line with the classinvariants concerning the temperature.
	 * @note This method is private because the only use of this method is in the method mergeWith()
	 */
	@Raw
	private void updateTemperature() {
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

	
	
	
	
	/***********************************************************
	 * BOUNDARIES (Total)
	 ***********************************************************/

	/**
	 * Inspector to retrieve all the obstacles of a square.
	 */
	@Basic
	public Collection<Obstacle> getObstacles() {
		return obstaclesMap.values();
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
	 * Method to check if a square and its neighbor have the same obstacle in the given direction, seen from this square.
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
	
	/**
	 * Checker that controls if the obstacles that are set now are possible for this square.
	 * 
	 * @return Returns false if an obstacles is invalid for this square 
	 * 		|for some direction in Direction.values(): 
	 * 		|	 if(!this.canHaveAsObstacle(direction, this.getObstacleAt(direction)) 
	 * 		|		then return == false
	 * @return Returns false if there is a neighbor at given direction and the obstacles between both aren't the same.
	 * 		|for some direction in Direction.values():
	 *		|	if(this.hasNeighborAt(direction))
	 *		|		if(!this.hasSameAdjecantObstacleAt(direction))
	 *		|			then return == false
	 * @return Returns false if there is a neighbor at given direction and there is a wall between both with a different moveability.
	 * 		|for some direction in Direction.values():
	 *		|	if(this.hasNeighborAt(direction))
	 *		|		(if(this.hasDoorAt(direction)) 	
	 * 		|			then this.getObstacleAt(direction).isOpen() == this.getNeighborAt(direction).getObstacleAt(direction.oppositeDirection()).isOpen))
	 */
	public boolean hasProperObstacles() {
		for (Direction direction : Direction.values()) {
			if (!this.canHaveAsObstacleAt(direction, this.getObstacleAt(direction))) {
				return false;
			}
			if(this.hasNeighborAt(direction)){
				if(!this.hasSameAdjecantObstacleAt(direction)){
					return false;
				}
				if(this.hasDoor(direction)){ 	
				 return this.isOpen(direction) == this.getNeighborAt(direction).isOpen(direction);
				}
			}
		}
		return true;
	}

	//TODO
	/**
	 * Checker that checks if given obstacle is possible in certain direction.
	 * 
	 * @param dir The direction in which you want to check.
	 * @param obstacle The obstacle you want to check.
	 * @return The method will return the return of canBeAnObstacleAt of the specific obstacle.
	 * 		|return == obstacle.canBeAnObstacleAt(this, dir)
	 */
	public boolean canHaveAsObstacleAtOld(Direction dir, Obstacle obstacle) {
		//werkt niet
		// Voor gewone square: elk obstakel mag (inclusief null), behalve als je geen neighbor hebt in die richting. Dan mag enkel een door
		// Voor rock: enkel walls mogen 
		// In teminated staat mogen ook enkel walls
		return obstacle.canBeAnObstacleAt(this, dir);
	}

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
	 * 		|if(canHaveAsObstacle(dir, new Wall()) 
	 * 		|	then new.hasWall(dir) == true
	 * @post If the square has a neighbor at the given direction and the building of a wall is possible in both squares, 
	 * 		then a wall will be set in both the adjecant squares at opposite directions.
	 * 		|if(getNeighbor(dir) != null)
	 * 		|	if(getNeighborAt(dir).canHaveAsObstacle(dir.oppositeDirection(), new Wall()) && this.canHaveAsObstacle(dir, new Wall())) 
	 * 		|		then getNeighborAt(dir).hasWallAt(dir.oppositeDirection()) && new.hasWall(dir) == true
	 */
	public void buildWallAt(Direction dir) {
		Obstacle wall = new Wall();
		if (canHaveAsObstacleAt(dir, wall) && this.getNeighborAt(dir) == null) {
			this.setObstacleAt(dir, wall);
		}
		if(this.getNeighborAt(dir) != null){
			if (canHaveAsObstacleAt(dir, wall) && this.getNeighborAt(dir).canHaveAsObstacleAt(dir.oppositeDirection(), wall)) {
				this.setObstacleAt(dir, wall);
				this.getNeighborAt(dir).setObstacleAt(dir.oppositeDirection(), wall);
			}
		}
	}

	/**
	 * Build a closed door at the given direction.
	 *
	 * @param dir
	 * 		The direction to build the door in.
	 * @effect A closed door is build
	 * 		| buildDoorAt(dir, false);
	 */
	public void buildDoorAt(Direction dir) {
		buildDoorAt(dir, false);
	}
	
	/**
	 * Method to build doors at given direction.
	 *
	 * @param dir
	 * 		The direction in which you want to add a wall.
	 * @param isOpen
	 * 		Whether the new door should be open.
	 * @pre The given direction is effective
	 * 		| direction != null
	 * @post If the given direction is effective and this square has a neighbor at the given direction and the building of a door is possible in both squares, 
	 * 		then a door will be set in both squares at the given direction (resp opposite direction).
	 * 		The door has an open-state of isOpen. If the door is open, then temperature is updated.
	 * 		| if getNeighbor(dir) != null
	 * 		|		&& getNeighborAt(dir).canHaveAsObstacle(dir.oppositeDirection(), new Door())
	 * 		|		&& this.canHaveAsObstacle(dir, new Door())) 
	 * 		|	then (new getNeighborAt(dir)).hasDoorAt(dir.oppositeDirection())
	 * 		|		&& new.hasDoorAt(dir)
	 * 		|		&& (new getNeighborAt(dir)).((Door)getObstacleAt(dir.oppositeDirection())).isOpen() == isOpen
	 * 		|		&& new.((Door)getObstacleAt(dir)).isOpen() == isOpen
	 * 		|		if isOpen
	 * 		|			then for each square in new.calculateGroup():
	 * 		|				square.getTemperature() == square.calculateUpdateTemperature()
	 * @note A door can only be build is you have a neighbor in the respective direction.
	 */
	public void buildDoorAt(Direction dir, boolean isOpen){
		Door door = new Door(isOpen);

		if(this.getNeighborAt(dir) != null){
			if (canHaveAsObstacleAt(dir, door) && this.getNeighborAt(dir).canHaveAsObstacleAt(dir.oppositeDirection(), door)) {
				this.setObstacleAt(dir, door);
				this.getNeighborAt(dir).setObstacleAt(dir.oppositeDirection(), door);
			}
		}
		if(isOpen){
			updateTemperature();
		}
	}
	
	/**
	 * Method to open a door at given direction.
	 * 
	 * @param dir The direction in which you want to open the door.
	 * @post If there is a door in that direction, the method opens the door.
	 * 		The temperature will also be recalculated for the newly formed group
	 * 		|if this.hasDoor(dir)
	 * 		|	then new.isOpen() == true
	 * 		|		&& new.getTemperature() == this.calculateUpdateTemperature()
	 * @return If there is no door in that direction, the method does nothing.
	 * 		|if !this.hasDoor(dir)
	 * 		|	then new.isOpen() == false
	 */
	public void setOpen(Direction dir){
		if(this.hasDoor(dir)){
			((Door)this.getObstacleAt(dir)).setOpen();
			this.updateTemperature();
		}
	}

	/**
	 * Method to set obstacle at given direction.
	 * 
	 * @param dir The direction in which you want to add an obstacle.
	 * @param obstacle The obstacle you want to add.
	 * @post If the obstacle isn't a null reference, the obstacle will be set at the given direction.
	 * 		|if(obstacle != null)
	 * 		|	then new.getObstacleAt(dir) == obstacle
	 * @effect If the obstacle is the null reference, the method will destroy the previous obstacle.
	 * 		|if(obstacle == null)
	 * 		|	then destroyObstacle(dir)
	 * @note This method is annotated raw because it doesn't take the obstacle invariant into account.
	 */
	@Raw
	private void setObstacleAt(Direction dir, Obstacle obstacle) {
		if(obstacle != null){
			this.obstaclesMap.put(dir, obstacle);
		}
		else{
			this.destroyObstacleAt(dir);
		}
	}
	
	/**
	 * Method to destroy an obstacle at a given direction.
	 * 
	 * @param dir The given direction.
	 * @post If the obstacle can be destroyed, then this method will remove an obstacle in a given direction from the list with obstacles.
	 * 		|if(this.canHaveAsObstacleAt(dir, null))
	 * 		|	then new.getObstacleAt(dir) == null
	 * @post If the obstacle can be destroyed, and the square has a neighbor in that direction, 
	 * 		then the obstacle of the neighbor at opposite direction will also be destroyed.
	 * 		|if(this.canHaveAsObstacleat(dir, null) && this.getNeighborAt(dir) != null)
	 * 		|	then (new.getNeighborAt(dir)).getObstacleAt(dir.oppositeDirection()) == null
	 * @post After the obstacle has been destroyed, the temperature will be set to the one of the new group that has been formed.
	 * 		|new.getTemperature() == new.calculateUpdateTemperature()
	 */
	public void destroyObstacleAt(Direction dir){
		if(this.canHaveAsObstacleAt(dir, null)){
			obstaclesMap.remove(dir);
			if(this.getNeighborAt(dir) != null){
				this.getNeighborAt(dir).obstaclesMap.remove(dir.oppositeDirection());
			}
			this.updateTemperature();
		}
	}

	/**
	 * Variable to store the obstacles of a square.
	 */
	private EnumMap<Direction, Obstacle> obstaclesMap;

	
	
	
	
	/***********************************************************
	 * NEIGHBORS (total)
	 ***********************************************************/

	/**
	 * Inspector to retrieve all the neighbors of a square.
	 */
	@Basic
	public Collection<Square> getNeighbors() {
		return neighborsMap.values();
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
	 * Inspector to retrieve the direction of a given neighbor.
	 * 
	 * @param neighbor The neighbor to check.
	 * @return If the neighbor is a neighbor of this square then return its direction.
	 * 		|if(this.hasAsNeighborAt(dir, neighbor)
	 * 		|	then result == dir
	 * @return If the neighbor isn't a neighbor of this square then return null as direction.
	 * 		|if(this.hasAsNeigbor(neighbor) == false)
	 * 		|	then result == null
	 */
	public Direction getDirectionOfNeighbor(Square neighbor){
		for(Direction dir: Direction.values()){
			if(this.hasAsNeighborAt(dir, neighbor)){
				return dir;
			}
		}
		return null;
	}
	
	/**
	 * Checker to control if this square has a neighbor in a certain direction.
	 * 
	 * @param dir The direction to check.
	 * @return Returns true if the square has a neighbor in the given direction.
	 * 		|result == (this.getNeighborAt(dir) != null)
	 */
	public boolean hasNeighborAt(Direction dir){
		return (this.getNeighborAt(dir) != null);
	}

	/**
	 * Checker to control if this square has a given square as neighbor.
	 * 
	 * @param square The square that could be connected to this one.
	 * @return Returns true if the square is connected to this one.
	 * 		|result == (for some Neighbor in getNeighbors(): Neighbor == square)
	 */
	public boolean hasAsNeighbor(Square square) {
		for (Square s : getNeighbors()) {
			if (square == s)
				return true;
		}
		return false;
	}

	/**
	* Check whether a neighbor is connected to a given square in the given direction.
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
	* @return True if for each neighbor of this square in a specific direction, that neighbor is legal and has
	* 		also this square as its neighbor in the inverse of that direction.
	* 		|result ==
	* 		|	( for each square in this.getObstacles():
	* 		|		canHaveAsNeighbor(square)
	* 		|		&& square.getNeighborAt(getDirectionOfNeighbor(square).oppositeDirection()) == this )
	*/
	public boolean hasProperNeighbors() {
		for(Square square: this.getNeighbors()){
			if(!this.canHaveAsNeighbor(square))
				return false;
			if(square.getNeighborAt(getDirectionOfNeighbor(square).oppositeDirection()) != this)
				return false;
		}
		return true;
	}

	/**
	* Check whether the given square is valid as a neighbor for this square.
	* 
	* @param square The square to check.
	* @return If this square is terminated, then true if the given square is null.
	* 		|if(isTerminated())
	* 		|	then ( result == ( square == null ) )
	* @return Else if the given square is not null, true if the given square is not terminated.
	*		| else if(!isTerminated() && square != null ))
	*		|	then ( result == !square.isTerminated() )
	* @return Else always true
	* 		| else result == true
	* @note If a neighbor is accepted by this method, it is automatically accepted in all directions.
	* 		Method 'canHaveAsNeighborAt(Direction dir, Square square)' is not needed.
	*/
	public boolean canHaveAsNeighbor(Square square) {
		if (isTerminated)
			return square == null;
		if (square != null)
			return !square.isTerminated();
		return true;
	}

	/**
	 * Method to set neighbor at given direction. This method doesn't take any temperature changes in account and only works unidirectionally.
	 * Another thing to take into account is that the obstacle may be corrupt after setting new neighbors with this method.
	 * 
	 * @param dir
	 * 		The direction in which you want to add a neighbor.
	 * @param square
	 * 		The square you want to add.
	 * @post If this square can have the other square as neighbor, then the square will be set connected at the given direction.
	 * 		|if(canHaveAsNeighbor(square)
	 * 		|	then new.getNeighborAt(dir) == square
	 * @note This method is annotated raw because the temperature is not updated 
	 * 		and the method only works in one direction of the association of neighbors. Also the obstacles between square will not be updated.
	 */
	@Model @Raw
	private void setNeighborAt(Direction dir, Square square){
		if (canHaveAsNeighbor(square)) {
			if (square == null) {
				this.neighborsMap.remove(dir);
			} else {
				this.neighborsMap.put(dir, square);
			}
		}
	}

	/**
	* Remove the neighbor of this square in the given direction, if it can be removed.
	* 
	* @param direction The direction of the square to remove as neighbor.
	* @post If the neighbor can be removed, this square does not have a neighbor anymore.
	* 		Also the respective neighbor doesn't have this square as neighbor anymore.
	* 		|if(this.canHaveAsNeighbor(null) && (this.getNeighborAt(direction).canHaveAsNeighbor(null)) 
	* 		|	then (new.getNeighborAt(direction) == null) && ((new.this).getNeighborAt(direction)).getNeighborAt(direction.oppositeDirection() == null )
	* @post Besides the removal of the neighbors there will also be set a wall at the direction where the connection of neighbors has been broken,
	* 		so that the invariants will be respected.
	* 		|if(new.getNeighborAt(direction) == null) && ((new.(old.getNeighborAt(direction))).getNeighborAt(direction.oppositeDirection()) == null ) 
	* 		|	then (new.hasWallAt(direction) == true) && ((new.(old.getNeighborAt(direction))).hasWallAt(direction.oppositeDirection()) == true)
	*/
	public void removeNeighbor(Direction direction) {
		Square other = getNeighborAt(direction);
		if (other != null) {
			if (this.canHaveAsNeighbor(null) && other.canHaveAsNeighbor(null)) {
				this.setNeighborAt(direction, null);
				other.setNeighborAt(direction.oppositeDirection(), null);
				this.buildWallAt(direction);
				other.buildWallAt(direction.oppositeDirection());
			}
		}
	}
	
	/**
	 * Register a neighbor for this square at the given direction, and remoove the obstacle between this
	 * square and its new square if destroyObstacle is true. 
	 * 
	 * @param square
	 * 		The square to add as neighbor
	 * @param direction
	 * 		The direction in which to add the neighbor
	 * @param destroyObstacle
	 * 		Whether the obstacles between this and the new neighbor should be removed.
	 * @post If the given square and direction are effective, and this square already has a neighbor
	 * 		in the given direction, then that neighbor now has no neighbor in the opposite of the
	 * 		given direction. It also has a wall in that direction now.
	 * 		| if square != null && direction != null && this.hasNeighborAt(direction)
	 * 		|	then !(new this.getNeighborAt(direction)).hasNeighborAt(direction.oppositeDirection())
	 * 		|		&& (new this.getNeighborAt(direction)).hasWall(direction.oppositeDirection())
	 * @post If the given square and direction are effective, and the given square already had a neighbor
	 * 		in the inverse of the given direction, then that neighbor now has no neighbor in the given
	 * 		direction anymore. It also has a wall in that direction now.
	 *		| if square != null && direction != null && (this square).hasNeighborAt(direction.oppositeDirection())
	 * 		|	then !(new (this square).hasNeighborAt(direction.oppositeDirection())).hasNeighborAt(direction)
	 * 		|		&& (new (this square).hasNeighborAt(direction.oppositeDirection())).hasWall(direction)
	 * @post If the given square and direction are effective, then this square has the given square as
	 * 		its neighbor in the given direction. Also the given square has this square as its neighbor in the
	 * 		opposite direction.
	 * 		| if square != null && direction != null
	 * 		|	then new.hasAsNeighborAt(square, direction)
	 * 		|		&& (new square).hasAsNeighborAt(this, direction.oppositeDirection())
	 * @effect If destroyObstacle is true and this square and the new neighbor can have an opening between them,
	 * 		then this square and the given square will have an opening between them.
	 * 		| if destroyObstacle
	 * 		|	&& this.canHaveAsObstacleAt(direction, null)
	 * 		|	&& (this square).canHaveAsObstacleAt(direction.oppositeDirection(), null)
	 * 		|	then destroyObstacleAt(direction)
	 *		Else the following happens:
	 * 		If this or the given square currently has a door in the given direction (resp opposite direction)
	 * 		then a door is build between the new neighbors with the same oppening state.
	 * 		| else if this.hasDoor(direction) || square.hasDoor(direction.oppositeDirection())
	 * 		|			then buildDoorAt(direction)
	 * 		Else if this or the given square has a wall in the given direction (resp opposite direction)
	 * 		then a wall is build between the new neighbors
	 * 		| 		else if this.hasWall(direction) || square.hasWall(direction.oppositeDirection())
	 * 		|			then buildWallAt(direction)
	 * 		Else if this or the given square has an opening in the given direction (resp opposite direction)
	 * 		then an opening is created between the new neighbors
	 * 		|		else if !this.hasObstacleAt(direction) || !square.hasObstacleAt(direction.oppositeDirection())
	 * 		|			then destroyObstacleAt(direction)
	 * @note This square might already have a neighbor in the given direction, or the given square might already
	 * 		have a niehgbor in the opposite direction. These 'external nieghbor' are also handled bij this method
	 * 		(they don't have that neighbor anymore, and a wall is build)
	 */
	public void registerNeighbor(Square square, Direction direction, boolean destroyObstacle){
		if(square == null || direction == null)
			return;
		
		// Check if a neihgbor already exists. If so, break the old association (obstacles are kept)
		// both this square and the given square can already have a neighbor...
		Square oldOne = getNeighborAt(direction);
		if(oldOne != null){
			// NOTE: set neighbor does not break invariant
			// 1. because there is not dungeon to check
			// 2. because this method is called after the squares are already neighbors (see addAsSquareAt)
			oldOne.setNeighborAt(direction.oppositeDirection(), null);
			oldOne.buildWallAt(direction.oppositeDirection()); // no invariants are broken since a wall must be placed in dirctions there is no neighbor
		}
		
		Square oldTwo = square.getNeighborAt(direction.oppositeDirection());
		if(oldTwo != null){
			oldTwo.setNeighborAt(direction, null);
			oldTwo.buildWallAt(direction);
		}
		
		// set the new neighbor association
		setNeighborAt(direction, square);
		square.setNeighborAt(direction.oppositeDirection(), this);
		
		// If the obstacle must be destroyed, then do so. (Only posible if this square and the niehgbor are not Rock)
		if(destroyObstacle && this.canHaveAsObstacleAt(direction, null) && square.canHaveAsObstacleAt(direction.oppositeDirection(), null)){
			destroyObstacleAt(direction);
		}else{
			if( this.hasDoor(direction) || square.hasDoor(direction.oppositeDirection())){
				if(this.hasDoor(direction))
					buildDoorAt(direction, ((Door)this.getObstacleAt(direction)).isOpen()); //TODO get rid of typecast
				else if(square.hasDoor(direction.oppositeDirection()))
					buildDoorAt(direction, ((Door)square.getObstacleAt(direction.oppositeDirection())).isOpen()); //TODO get rid of typecast
			}else if(this.hasWall(direction) || square.hasWall(direction.oppositeDirection()))
				buildWallAt(direction);
			else if(this.getObstacleAt(direction) == null || square.getObstacleAt(direction.oppositeDirection()) == null)
				destroyObstacleAt(direction);
			else {
				// use old obstacle of this
				if(hasDoor(direction))
					buildDoorAt(direction, ((Door)this.getObstacleAt(direction)).isOpen()); // TODO typecast
				else if(hasWall(direction))
					buildWallAt(direction);
				else
					destroyObstacleAt(direction);
			}
		}
	}
	
	/**
	 * Convenience method for registerNeighbor(square, direction, destroyObstacle)
	 * 
	 * @param square
	 * 		The square to add as a neighbor
	 * @param direction
	 * 		The direction to add the given square in as neighbor
	 * @effect The given square is registered as neighbor at the given position, and the wall
	 * 		between the new neighbors is not destroyed
	 * 		| registerNeighbor(square, direction, false)
	 * 		
	 */
	public void registerNeighbor(Square square, Direction direction) {
		registerNeighbor(square, direction, false);
	}

	/**
	 * Check whether this square can collapse.
	 * 
	 * @return True is this square has a rock above it.
	 * 		| result ==  ( getNeighborAt(Direction.UP) != null
	 * 		|			&&  getNeighborAt(Direction.UP) instanceof Rock )
	 */
	public boolean canCollapse(){
		Square upperNeighbor = getNeighborAt(Direction.UP);
		if(upperNeighbor!= null && upperNeighbor.canMakeCollapse() )
			return true;
		return false;
	}
	
	/**
	 * Check whether this square can make a square bolow it collapse.
	 * 
	 * @return Always true
	 */
	public boolean canMakeCollapse(){
		return false;
	}
	
	/**
	 * Variable to store the neighbors of a square.
	 */
	private EnumMap<Direction, Square> neighborsMap;

	
	
	
	
	/***********************************************************
	 * MERGING SQUARES (Defensive)
	 ***********************************************************/

	/**
	 * Method that checks if you can move in a certain direction.
	 * 
	 * @param dir The direction to move in.
	 * @return Returns true if there is neighbor in that direction and there is no wall between them or the door is open.
	 * 		With other words, you can move in that direction.
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
	
	/**
	 * Compute the group to which this square belongs (A 'group' is defined as a set of squares
	 * between which one can move)
	 * 
	 * @return The group to which this square belongs.
	 * 		| for each square in result:
	 * 		|	for some otherSquare in result:
	 * 		|		square.hasAsNeighbor(otherSquare)
	 * 		|		&& square.canMove(getDirectionOfNeighbor(otherSquare))
	 */
	@Raw
	public Collection<Square> computeGroup() {
		// invar: The set contains squares that are connected through moveable obstacles (null and door).
		// 		|for each square1 and square2 in group:
		//		|	if(square1.getNeighborAt(dir) == square2)
		//		|		then square1.canMove(dir)
		// invar: There are no duplicates in the list.
		//		|for each square in group:
		//		|	group.remove(square).contains(square) == false
		Set<Square> group = new HashSet<Square>();
		group.add(this);
		
		ArrayList<Square> mn = movableNeighbors();
		group.addAll(movableNeighbors());
		
		LinkedList<Square> newlyAdded = new LinkedList<Square>(mn);
		
		while(newlyAdded.size() != 0){
			Square n = newlyAdded.pop();
			for(Square s : n.movableNeighbors()){
				boolean added = group.add(s);
				if(added)
					newlyAdded.push(s);
			}
		}

		return group;
	}
	
	/**
	 * Retrieve all the squares to which one can move from this squre, given the current obstacle and
	 * neighbor configuration
	 * 
	 * @return All the squares to which one can move from this squre.
	 * 		| for direction in Direction.values():
	 *		|	if canMove(direction)
	 *		|		then result.add(getNeighborAt(direction))
	 */
	@Model @Raw
	private ArrayList<Square> movableNeighbors(){
		ArrayList<Square> g = new ArrayList<Square>();
		for (Direction d : Direction.values()) {
			if (canMove(d)) {
				g.add(this.getNeighborAt(d));
			}
		}
		return g;
	}

	/**
	 * Method to merge a group of squares with another group of squares.
	 * 
	 * @param dir The direction of the other group.
	 * @post The temperature of each square in this and the other group will be the average of the squares in the two groups, taken in account the size of the two groups. 
	 * 		We can also see this now as one group.
	 * 		|for each square in calculateGroup():
	 * 		|	square.getTemperature() == square.calculateUpdateTemperature()
	 * @throws IllegalArgumentException [MUST] The direction is not valid if there doesn't exist a neighbor at the given direction.
	 * 		|getNeighborAt(dir) == null
	 * @note This method is annotated raw because the method can be used when the temperature isn't updated yet.
	 */
	@Raw
	public void mergeWith(Direction dir) {
		if (this.getNeighborAt(dir) != null) {
			this.updateTemperature();
		}
		else{
			throw new IllegalArgumentException("There is no neighbor at the given direction!");
		}
	}
	
}
