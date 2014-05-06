package dungeons;

import java.util.EnumMap;
import java.util.Map;

import dungeons.obstacle.Door;
import dungeons.obstacle.Obstacle;
import dungeons.obstacle.Wall;
import dungeons.util.Direction;
import dungeons.util.Point;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;
/**
 * TODO invar concerning neighbors
 * 
 * @author Edward
 *
 */
public class Squareaaaa implements Comparable<Squareaaaa> {

	//TODO Doors MUST have a square at both sides! - this has concequenses when it commes to adding and removeing squares from a dungeon
	// removing a square from a dungeon will check all the neighbors of that square, remove
	
	//TODO destroying walls needs to take in account neighbors... only walls can be destroyed where there is no niehgbor...
	// squares at outside of dungeon always have walls 
	
	
	
	private EnumMap<Direction, Obstacle> obstaclesMap;
	//private EnumMap<Direction, Square> connectedSquares;
	
	/**
	 * A standard constructor.
	 * This constructor doesn't have any arguments. The temperature will be set to a default value. 
	 * 
	 * @post The temperature is set to a default value.
	 * 		|new.getTemperature() == DEFAULT_TEMPERATURE
	 */
	public Squareaaaa() {
		this.temperature = DEFAULT_TEMPERATURE;
		this.neighbors = new EnumMap<Direction, Square>(Direction.class);
		this.obstaclesMap = new EnumMap<Direction, Obstacle>(Direction.class);
	}

	/**
	 * A temperature constructor.
	 * This constructor has a temperature as argument.
	 * 
	 * @param temp The given temperature.
	 * @post The temperature is set to the given value.
	 * 		|new.getTemperature() == temp
	 */
	public Square(int temp){
		this.temperature = temp;
		this.neighbors = new EnumMap<Direction, Square>(Direction.class);
		this.obstaclesMap = new EnumMap<Direction, Obstacle>(Direction.class);
	}
	
	/* TEMPERATURE (Defensive) */

	/**
	 * Inspector to check the temperature.
	 */
	@Basic
	public float getTemperature(){
		return temperature;
	}
	
	/**
	 * Inspector to check the default temperature.
	 */
	@Basic
	@Immutable
	public static int getDefaultTemperature(){
		return DEFAULT_TEMPERATURE;
	}
	
	/**
	 * Inspector to check the maximum temperature.
	 */
	@Basic
	public static int getMaxTemperature(){
		return max_temperature;
	}
	
	/**
	 * Inspector to check the minimum temperature.
	 */
	@Basic
	public static int getMinTemperature(){
		return min_temperature;
	}
	
	/**
	 * Inspector to check the unit of heat damage for each step.
	 */
	@Basic
	public static int getUnitHeatDamage(){
		return unitHeatDamage;
	}
	
	/**
	 * Inspector to check the step for heat damage.
	 */
	@Basic
	public static int getHeatDamageStep(){
		return heatDamageStep;
	}
	
	/**
	 * Inspector to check the limit from where the damage starts to count.
	 */
	@Basic
	public static int getHeatDamageAbove(){
		return heatDamageAbove;
	}
	
	/**
	 * Checker that tells if the temperature is valid.
	 * @param temp The temperature to test.
	 * @return Returns true if the temperature is between the two boundary temperatures.
	 * 		|return == (temp < this.getMaxTemperature() && temp < this.getMinTemperature())
	 */
	public static boolean isValidTemperature(int temp){
		return (temp < Square.getMaxTemperature() && temp > Square.getMinTemperature());
	}
	
	/**
	 * A method to calculate the damage due too temperature.
	 * 
	 * @return If the temperature is too hot, there will be a certain points damage for each specified step of degrees above a certain temperature.
	 * 		|If(this.getTemperature() > this.getHeatDamageAbove()) => return == floor((this.getTemperature()-this.getHeatDamageAbove)/this.getHeatDamageStep)*this.getUnitHeatDamage()
	 * @return If the temperature is too cold, there will be 1 point damage for each 10° below -5°.
	 * 		|If(this.getTemperature() < -5) => return == floor((abs(this.getTemperature())-5)/10)
	 * @return If the temperature is between the boundaries there will be no damage.
	 * 		|If(this.getTemperature() > this.getHeatDamageAbove() && this.getTemperature() < -5) => return == 0
	 */
	public int calculateTemperatureDamage(){
		if(this.getTemperature() > Square.getHeatDamageAbove()){
			return (int) ((this.getTemperature()-Square.getHeatDamageAbove())/Square.getHeatDamageStep()*Square.getUnitHeatDamage());
		}
		if(this.getTemperature() < -5){
			return (int) ((Math.abs(this.getTemperature())-5)/10);
		}
		else{
			return 0;
		}
	}
	
	/**
	 * Method to set the temperature.
	 * 
	 * @param temp The specified temperature.
	 * @post The current temperature is set to the given one.
	 * 		|new.getTemperature() == temp
	 * @throws IllegalArgumentException [MUST] The given temperature is not valid.
	 * 		|!isValidTemperature(temp)
	 */
	public void setTemperature(int temp) throws IllegalArgumentException {
		if(isValidTemperature(temp)){
			this.temperature = temp;
		}
		else{
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
	public static void setMaxTemperature(int temp){
		Square.max_temperature = temp;
	}
	
	/**
	 * Method to set the minimum temperature.
	 * 
	 * @param temp The specified minimum temperature.
	 * @post The current minimum temperature is set to the given one.
	 * 		|new.getMinTemperature() == temp
	 */
	public static void setMinTemperature(int temp){
		Square.min_temperature = temp;
	}
	
	/**
	 * Method to set new unit for heat damage.
	 * 
	 * @param unit The new unit for heat damage.
	 * @post The unit will be update too the new one.
	 * 		|new.getUnitHeatDamage() == unit
	 */
	public static void setUnitHeatDamage(int unit){
		Square.unitHeatDamage = unit;
	}
	
	/**
	 * Method to set new step for heat damage.
	 * 
	 * @param step The new step for heat damage.
	 * @post The step will be update too the new one.
	 * 		|new.getHeatDamageStep() == step
	 */
	public static void setHeatDamageStep(int step){
		Square.heatDamageStep = step;
	}
	
	/**
	 * Method to set new limit for heat damage.
	 * 
	 * @param limit The new limit for heat damage.
	 * @post The limit will be update too the new one.
	 * 		|new.getHeatDamageAbove() == limit
	 */
	public static void setHeatDamageAbove(int limit){
		Square.heatDamageAbove = limit;
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
	
	
	
	
	
	// -+-+-+- BIDIRECTION DUNGEON ASSOCIATION -+-+-+- //
	
	public Dungeon getDungeon(){
		return dungeon;
	}
	
	/**
	 * Set the dungeon of this square.
	 * @param d
	 * 		The dungeon this square belongs in, or null if no such dungeon exists.
	 * @pre If the given dungeon is not null, then it must have this square as one
	 * 		of its squares.
	 * 		| (d != null) => d.hasAsSquare(this)
	 * @pre If the given dungeon is null, and the current dungeon is not null, then
	 * 		the current dungeon may not have this square as one of its squares.
	 * 		| (d == null && getDungeon() != null ) => !getDungeon().hasAsSquare(this)
	 * @note In an idirect way, it is ensured that the given dungeon is not terminated ( hasAsSquare(this) )
	 */
	@Raw
	public void setDungeon(Dungeon d){
		assert (d == null) || d.hasAsSquare(this); // these are not O(log(n)) operations, so may not be checked at runtime (in the final version) 
		assert (d != null) || (getDungeon() == null ) || !getDungeon().hasAsSquare(this);
		this.dungeon = d;
	}
	
	/**
	 * CHeck whether this square has a proper dungeon
	 * 
	 * @return True if and only if the dungeon of this square is legal, and if
	 * 		the dungeon has this square as one of its squares.
	 * 		| result == canHaveAsDungeon(getDungeon())
	 * 		|			&& (getDungeon() == null || getDungeon().hasAsSquare(this))
	 */
	public boolean hasProperDungeon(){
		return (canHaveAsDungeon(getDungeon()) && (getDungeon() == null || getDungeon().hasAsSquare(this)) );
	}
	
	/**
	 * Check whether this square can have the given dungeon as its dungeon
	 * 
	 * @param dungeon
	 * 		The dungeon to check
	 * @return True if and only if the given dungeon is null, or it can have
	 * 		this square as one of its squares. 
	 */
	public boolean canHaveAsDungeon(Dungeon dungeon){
		return ( (dungeon == null) || dungeon.canHaveAsSquare(this) );
	}
	
	private Dungeon dungeon;
	
	
	
	
	// -+-+-+- UNIDIRECTINAL OBSTACLES ASSOCIATION -+-+-+- //
	
	
	public boolean hasFloor(){
		return false;
	}
	
	public boolean hasCeiling(){
		return false;
	}
	


	// pre: er is een verbinding
	public void mergeWith(Direction d){
	}
	
	
	// -+-+-+- UNIDIRECTIONAL CONNECTED SQUARE ASSOCIATION -+-+-+- //
	/*public void setConnectedSquare(Square s, Direction d){
		
	}*/
	/*
	public Square getConnectedSquare(Direction d){
		return connectedSquares.get(d);
	}*/
	
	public boolean canMove(Direction d){
		return false;
	}
	
	// -+-+-+- TELEPORATION RULES -+-+-+- //
	public boolean canBeTeleportedTo(){
		return false;
	}
	
	// -+-+-+- COLLAPS -+-+-+- //
	public boolean canCollapse(){
		return false;
	}
	
	public void collapse(){
		// recursive...
		// TODO: update your position in your parent dungeon. + update neihgbors etc... (like addSquare method?)
	}

	@Override
	public int compareTo(Square other) {
		
		return 0;
	}
	
	/* BOUNDARIES (Total) */
	
	// TODO Wat te doen met dir? Is er een controle nodig?
	
	/**
	 * Inspector to retrieve all the obstacles of a square.
	 */
	@Basic
	public EnumMap<Direction, Obstacle> getObstaclesMap(){
		return obstaclesMap;
	}
	
	
	/**
	 * Inspector to retrieve obstacle in given direction.
	 * 
	 * @param dir The selected direction.
	 * @return The obstacle in the given direction.
	 * 		|return == obstaclesMap.get(dir)
	 */
	public Obstacle getObstacleAt(Direction dir){
		return obstaclesMap.get(dir);
	}
	
	
	/**
	 * Inspector to check if a square has a door in a certain direction.
	 * 
	 * @param dir The direction you want to check.
	 * @return Returns true if the square has a door in that direction.
	 * 		|return == (this.getObstacleAt(dir) instanceof Door)
	 */
	public boolean hasDoor(Direction dir){
		if(this.getObstacleAt(dir) instanceof Door){
			return true;
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
	public boolean hasWall(Direction dir){
		if(this.getObstacleAt(dir) instanceof Wall){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Method to set obstacle at given direction.
	 * 
	 * @param dir The direction in which you want to add an obstacle.
	 * @param obstacle The obstacle you want to add.
	 * @post The obstacle will be set at the given direction.
	 * 		|new.getObstacleAt(dir) == obstacle
	 * @post If the square has a connected square at the given direction, then the obstacle will also be set in the adjecant square at opposite direction.
	 * 		|If(getConnectedSquare(dir) != null) => getConnectedSquareAt(dir).getObstacleAt(dir.oppositeDirection(), obstacle)
	 */
	public void setObstacleAt(Direction dir, Obstacle obstacle){
		this.obstaclesMap.put(dir, obstacle);
		if(this.getNeighborAt(dir) != null){
			this.getNeighborAt(dir).obstaclesMap.put(Direction.getInverse(dir), obstacle);
		}
	}
	
	//TODO note: setObstacle sould be private, and should only concern itself about its own obstacle map. BuildWall etc ensures bidirection stuff is done correcly
	

	
	/**
	 * Variable to store the obstacles of a square.
	 */
	//private EnumMap<Direction, Obstacle> obstaclesMap;
	
	/**
	 * Variable to store the connected squares of a square.
	 */
	//private EnumMap<Direction, Square> connectedSquaresMap;

	
	
	
	/*
	public void setConnectedSquares(EnumMap<Direction, Square> connectedSquares){
		this.connectedSquares = connectedSquares;
	}
	*/
	public EnumMap<Direction, Obstacle> getObstacles(){
		return obstaclesMap;
	}
	
	public void setObstacles(EnumMap<Direction, Obstacle> obstacles){
		this.obstaclesMap = obstacles;
	}
	


	public boolean isTerminated(){
		return isTerminated;
	}

	/**
	 * Terminate this square
	 * 
	 * @effect If this square has a dungeon, it is removed from that dungeon, and it loses
	 * 		the reference to it. (it this order - see preconitions setDungeon)
	 * 		| if this.getDungeon() != null
	 * 		|	then getDungeon().removeAsSquare(this)
	 * 		|		&& new.getDungeon() == null
	 * @post This square is now in a terminated state
	 * 		| new.isTerminated()
	 */
	@Raw
	public void terminate(){
		//TODO
		//TODO set dungeon reference to null. Maybe make it so termination is only posible if that dungeon does not contain you anymore. (ot the other way around)
		for(Map.Entry<Direction, Square> n: neighbors.entrySet()){
			buildWall(n.getKey());
			removeNeighbor(n.getKey()); //TODO add convenience methode taht takes neighbor AND direction as argument, and make it private.
		}
		isTerminated = true; //TODO set isTerminated in the end (like here)
	}
	//TODO terminate will remove this square from the map in its parent.
	
	private boolean isTerminated;
	
	

	
	
	
	// -+-+-+-+- OBSTACLES - TOTAL -+-+-+-+- //
	
	// TODO more methods needed... like for all collections

	public Obstacle getObstacle(Direction d){
		return null;
	}
	
	
	/**
	 * 
	 * this method will not update the temperature, other obstacle udpateing mathod will?
	 * No walls at the edge (where no neighbor) are destroyed. 
	 * 
	 * @param d
	 * @note The behavior of this method compared to methods like 
	 */
	@Raw //Raw exit
	private void destroyObstacleWithoutTempUpdate(Direction d){
		
	}
	
	@Raw //Raw exit
	public void destroyObstacles(Direction[] ds){
		for (Direction d : ds){
			destroyObstacleWithoutTempUpdate(d);
		}
		updateTemperature();
	}
	
	
	/**
	 * Fix inconcitencies conserning obstacles and neighbors.
	 * After a new square is added to a dugeon, its neihbors are updated, but some obstacles might be inconsitent. 
	 * e.g. your neighbor has a wall while you have a door etc...
	 * 
	 * @note This method might update temperature, if an open door is inteoduced, or there is an edge without a wall.
	 * 
	 * DOC OF ADD METHOD I DUNGEON:
	 * 
	 * Inconsistencies between obstacles of the given square, and its new neighbors are fixed.
	 * 		Openings between squares are created in the given directions. Note that a square will always
	 * 		have a wall in directions they don't have a square at. If there are openings between squares,
	 * 		the temperature of the group this squares belongs to is updated.
	 * 
	 */
	@Raw //Raw exit and raw entry.
	public void fixObstacles(Direction[] destroyObstacles){
		// information of breakWalls always has privilege above previously existing walls
		boolean updateTemperature = false;
		
		for(Direction d : Direction.values()){
			Obstacle oHere = obstaclesMap.get(d);
			Square n = neighbors.get(d);
			
			if(n == null){
				buildWall(d); // squares at the edge of a dungeon always have walls at there outside. - this will not update temp...
			}else{
				Obstacle oThere = n.getObstacle(Direction.getInverse(d));
				/*if(oHere instanceof Door){
					
				}*/
				//TODO if one of the two is a door, they both become the same door.
				//TODO else if one of the two is a wall, they both become the same wall.
				//TODO else use information of this.
			}
		}
		
		if(updateTemperature){
			updateTemperature();
		}
	}
	
	/**
	 * Compute the group, this square is in.
	 * set the temparature of all cencering squares to the same average...
	 */
	@Model @Raw // Raw entry
	private void updateTemperature() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Build a door in the given direction that is, or is not, open.
	 * 
	 * @param direction
	 * 		The direction to build te door in
	 * @param isOpen
	 * 		Whether the build door must be open
	 * @effect
	 * TODO setObstacle..., bidirectioneel
	 * @post A door-obstacle is now present in the given direction relative to this square
	 * 		| new.getObstacleInDirection(direction) instanceof Door
	 * @post 
	 * @post The new door has the open-state of isOpen
	 * 		| new.getObstacleInDirection(direction).isOpen() == isOpen
	 */
	public void buildDoor(Direction direction, boolean isOpen){
		//TODO
		// canHaveAsObstacleInDirection...
		if(isOpen)
			updateTemperature();
	}
	
	public void buildWall(Direction d){
		// TODO bidirectional... (no temperature update)
	}
	
	public void setDoorOpen(boolean isOpen){
		
	}
	
	@Model
	private void setObstacle(Obstacle obstacle, Direction direction){
		
	}
	
	public boolean canHaveAsObstacleInDirection(Obstacle obstacle, Direction direction){
		return false;
	}
	
	public void destroyObstacle(Direction direction){
		// can destroy obstacele? - total implementation
	}
	
	
	//TODO make the above three methods private, and compose them in a single method (that has no raw exit or entry)
	
	
	
	
	// TODO need method that registers all neighbors, given a (root) directory
	// TODO Adding a square to any dungeon will set the directory of that dungeon to 
	// 1. the root dungeon
	// 2. the leaf dungeon the square is in
	

	
	//TODO setDungeon needs preconditions!!! see page 438
	
	
	// -+-+-+-+- NEIGHBORING SQUARES - TOTAL -+-+-+-+- //
	/**
	 * Retrieve all the squares that are neighbors of this square in an enumMap representation.
	 */
	@Basic
	public EnumMap<Direction, Square> getNeighbors() {
		return neighbors;
	}
	
	/**
	 * Retrieve the neighbor of this square in the specific direction
	 */
	@Basic
	public Square getNeighborAt(Direction direction) {
		return neighbors.get(direction);
	}
	
	/**
	 * Retrieve the number of neighbors this square has
	 */
	@Basic
	public int getNbNeighbors(){
		return neighbors.size();
	}
	
	/**
	 * Check whether this square has the given square as one of its neighbors.
	 * 
	 * @param other
	 * 		The square that might be a neighbor of this square.
	 * @return True if this square contains the given square as one of its neighbors.
	 * 		| result ==
	 * 		| (for some square in getNeighbors():
	 * 		|		square == this)
	 */
	public boolean hasAsNeighbor(Square other){
		for(Square s: neighbors.values()){
			// equal references
			if(other == s)
				return true;
		}
		return false;
	}
	
	/**
	 * Check whether a neighbor exists in the given direction.
	 * 
	 * @param direction
	 * 		The direction to look in.
	 * @return True if a neighbor exists in the specific direction.
	 * 		| result == (getNeighborAt(direction) != null)
	 */
	public boolean hasNeighborInDirection(Direction direction){
		return getNeighborAt(direction) != null;
	}
	
	/**
	 * Check whether the given square is the neighbor in the given direction.
	 * 
	 * @param direction
	 * 		The direction to look in.
	 * @return True if the neighbor in the given direction is the given square.
	 * 		| result == (getNeighborAt(direction) == square)
	 */
	public boolean hasAsNeighborInDirection(Square square, Direction direction){
		return getNeighborAt(direction) == square;
	}
	
	/**
	 * Copy all the neighbors from the given square to this square.
	 * 
	 * @param other
	 * 		The square to copy the neighbors from.
	 * @post If the given square is not null, all the neighbors of this square
	 * 		are now the same as the neighbors of the given square (the respective directions
	 * 		are also the same)
	 * 		| if (other != null)
	 * 		| then ( for each direction in Direction.values():
	 * 		|		new.getNeighborAt(direction) == other.getNeighborAt(direction) )
	 */
	@Raw
	public void copyNeighbors(Square other){
		if(other != null)
			this.neighbors = other.getNeighbors();
	}
	
	
	/**
	 * Register the given square as a neighbor of this square in the given direciton.
	 * 
	 * @param square
	 * 		The square to register as a neighbor
	 * @param direction
	 * 		The direction of the given square relative to this square.
	 * @post If the new neighbor is legal, this square has the given square as its neighbor in the given direction.
	 * 		Also the given square has this square as its neighbor in the inverse of the given direction.
	 * 		| this.canHaveAsNeighbor(square) && (this square).canHaveAsNeighbor(this)		
	 *		| 	=> ( new.getNeighborAt(direction) == square
	 *		|		&& (new square).getNeighborAt(Direction.getInverse(direction)) == this )
	 * @note Raw because this method does not update temperature...
	 */
	@Raw
	public void registerNeighbor(Square square, Direction direction){
		if(this.canHaveAsNeighbor(square) && square.canHaveAsNeighbor(this)){
			try{
				setNeighbor(square, direction);
				square.setNeighbor(this, Direction.getInverse(direction));
			}catch(IllegalArgumentException e){
				// should never happen
				assert false;
			}
		}
	}
	//TODO private?
	
	/**
	 * Remove the neighbor of this square in the given direction, if it can be removed.
	 * 
	 * @param direction
	 * 		The direction of the square to remove as neighbor.
	 * @post If the neighbor can be removed, this square does not have the square at the given direction as its neighbor anymore.
	 * 		Also the respective neighboring square does not have this square as its neighbor anymore.
	 * 		| this.canHaveAsNeighbor(null) && (this n).canHaveAsNeighbor(null)
	 * 		| 	=> ( new.getNeighborAt(direction) == null
	 * 		|		&& (new this.getNeighborAt(direction)).getNeighborAt(Direction.getInverse(direction)) == null )
	 */
	public void removeNeighbor(Direction direction){
		Square n = getNeighborAt(direction);
		if(n != null && this.canHaveAsNeighbor(null) && n.canHaveAsNeighbor(null)){
			try{
				setNeighbor(null, direction);
				n.setNeighbor(null, Direction.getInverse(direction));
			}catch(IllegalArgumentException e){
				// should never happen
				assert false;
			}
		}
		//TODO obstacles are fixed.
	}
	//TODO add canRemoveNeighbor: only if there is a wall between the two squares.
	
	/**
	 * Set the neighbor of this square in the given direction to the given square.
	 * 
	 * @param square
	 * 		The square to set a neighbor
	 * @param direction
	 * 		The direction in which to set the given square as neighbor
	 * @post This square has the given square as its neighbor in the given direction.
	 * 		| new.getNeighborAt(direction) == square
	 * @throws IllegalArgumentException [MUST]
	 * 		The given square is not a valid neighbor
	 * 		| !canHaveAsNeighbor(square)
	 */
	@Model @Raw
	private void setNeighbor(Square square, Direction direction) throws IllegalArgumentException{
		if(!canHaveAsNeighbor(square))
			throw new IllegalArgumentException("The given square is not a valid neighbor");
		if(square == null){
			neighbors.remove(direction);
		}else{
			neighbors.put(direction, square);
		}
	}
	
	/**
	 * Check whether this square has proper neighbors.
	 * 
	 * @return True if for each neighbor of this square in a specific direction, that neighbor
	 * 		also has this square as its neighbor in the inverse of that direction, and that neighbor
	 * 		is legal.
	 * 		| result ==
	 * 		| ( for each neighborEntry in getNeighbors():
	 * 		|	( canHaveAsNeighbor(neighborEntry.getValue()) 
	 * 		|		&& neighborEntry.getValue().hasAsNeighborInDirection(this,
	 * 		|		Direction.getInvers(neighborEntry.getKey()) ) )
	 */
	public boolean hasProperNeighbors(){
		for(Map.Entry<Direction, Square> e: neighbors.entrySet()){
			if(!canHaveAsNeighbor(e.getValue())
					|| !e.getValue().hasAsNeighborInDirection(this, Direction.getInverse(e.getKey())))
				return false;
		}
		return true;
	}
	
	/**
	 * Check whether the given square is valid as a neighbor for this square.
	 * 
	 * @param square
	 * 		The square to check.
	 * @return If this square is terminated, then true if the given square is null.
	 * 		| isTerminated() => ( result == ( square == null ) )
	 * @return Else if the given square is not null, true if the given square is not terminated.
	 * 		| (!isTerminated() && square != null ) => ( result == !square.isTerminated() )
	 * @return Else always true
	 * 		| (!isTerminated() && square == null ) => ( result == true )
	 * @note If a neighbor is accepted by this method, it is automatically accepted in all directions.
	 * 		Method 'canHaveAsNeighborAt(Direction)' is not needed.
	 */
	//TODO can not have null reference if not terminated...
	public boolean canHaveAsNeighbor(Square square){
		//TODO maybe check is they are in the same dungeon? see 'PROBLEM' in Dungeon
		if(isTerminated){
			return square == null;
		}else{
			if(square != null)
				return !square.isTerminated();
			return true;
		}
	}
	
	// TODO represenation invar
	private EnumMap<Direction, Square> neighbors;



	@Override
	public int compareTo(Squareaaaa o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
