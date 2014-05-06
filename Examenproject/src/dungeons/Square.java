/**
 * The package for a roleplaying game.
 */
package dungeons;

import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.ArrayList;

import dungeons.obstacle.Door;
import dungeons.obstacle.Obstacle;
import dungeons.obstacle.Wall;
import dungeons.util.Direction;
import be.kuleuven.cs.som.annotate.*;

/**
 * Opmerkingen Edward:
 * - meer uitleg bij klasse invariaten
 * - klasse omschrijving
 * - extra methoden:
 * 		builDoorAt(Direction dir, booelan isOpen), setDoorOpen(Direciton dir, boolean isOpen) -> reset eventueel temperatuur,
 * 		fixObstacles() lijst met destroyObstacels meenemen
 * 		registerNeighbor() mag niet Raw zijn (fixt obstacels zelf), computeGroup() moet tilles behorende tot een groep vinden (zie opdracht)
 * 		destroyObstaclesAt() ->kan temperatuur updaten
 * - formele speceficatie meot leesbaar zijn...
 * - temperatuur moet float zijn...
 * - methode canCollapse
 * - set temperature also computes the group
 */


/**
 * A class for the creation of objects of Squares.
 * 
 * @author Christof Vermeersch & Edward Van Sieleghem
 * @invar The temperature must be between valid boundaries.
 * 		|isValidTemperature(this.getTemperature())
 * @invar The square can maximum have 6 boundaries.
 * 		|obstacleMap.size() <= 6
 * @invar The obstacles must be valid.
 * 		|hasProperObstacles() ==  true
 * @invar The neighbors must be valid.
 * 		|hasProperNeighbors() == true
 */
public class Square {

	/**
	 * A standard constructor.
	 * This constructor doesn't have any arguments. The temperature will be set to a default value. 
	 * 
	 * @post The temperature is set to a default value.
	 * 		|new.getTemperature() == DEFAULT_TEMPERATURE
	 * @post The newly created square is surrounded by walls.
	 * 		|for each direction in Direction.values(): new.hasWall(direction) == true
	 */
	public Square() {
		this.temperature = DEFAULT_TEMPERATURE;
		this.neighborsMap = new EnumMap<Direction, Square>(Direction.class);
		this.obstaclesMap = new EnumMap<Direction, Obstacle>(Direction.class);
		for(Direction d: Direction.values()){
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
	 * 		|for each direction in Direction.values(): new.hasWall(direction) == true
	 */
	public Square(int temp){
		this.temperature = temp;
		this.neighborsMap = new EnumMap<Direction, Square>(Direction.class);
		this.obstaclesMap = new EnumMap<Direction, Obstacle>(Direction.class);
		for(Direction d: Direction.values()){
			this.buildWallAt(d);
		}
	}
	
	/* TERMINATION */
	
	/**
	 * Method to check if the square is terminated.
	 */
	@Basic
	public boolean isTerminated(){
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
	 * 		|for each direction in Direction.values(): if(old.getNeighborAt(direction != null) => (new.(old.getNeighborAt(direction))).hasWall(direction) == true
	 * @post The square doesn't have any neighbors left.
	 * 		|new.getNeighborsMap().isEmpty() == true
	 */
	public void terminate(){
		for(Direction d: Direction.values()){
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
	public int getTemperature(){
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
			return (this.getTemperature()-Square.getHeatDamageAbove())/Square.getHeatDamageStep()*Square.getUnitHeatDamage();
		}
		if(this.getTemperature() < -5){
			return ((Math.abs(this.getTemperature())-5)/10);
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
	
	// TODO Afwerken
	/**
	 * Method to set the temperature of a group.
	 */
	public void updateTemperature(){
		
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
	
	// TODO Wat te doen met dir? Is er een controle nodig?
	
	/**
	 * Inspector to retrieve all the obstacles of a square.
	 */
	@Basic
	public EnumMap<Direction, Obstacle> getObstaclesMap(){
		return obstaclesMap;
	}
	
	/**
	 * Inspector to retrieve number of obstacles.
	 * 
	 * @return Returns the number of obstacles for this Square.
	 * 		|return == obstaclesMap.size()
	 */
	public int getNbOfObstacles(){
		return obstaclesMap.size();
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
	
	//TODO Afwerken en inwerken in buildwall, builddoor en destroy obstacle
	
	/**
	 * Checker that controls if the obstacles now are possible for this square.
	 */
	public boolean hasProperObstacles(){
		return true;
	}
	
	/**
	 * Checker that checks if given obstacle is possible in certain direction.
	 */
	public boolean canHaveAsObstacleAt(){
		return false;
	}
	
	/**
	 * Method to build walls at given direction.
	 *
	 * @param dir The direction in which you want to add a wall.
	 * @post The wall will be set at the given direction.
	 * 		|new.hasWall(dir) == true
	 * @post If the square has a neighbor at the given direction, then a wall will also be set in the adjecant square at opposite direction.
	 * 		|If(getNeighbor(dir) != null) => getNeighborAt(dir).hasWallAt(dir.oppositeDirection())
	 */
	public void buildWallAt(Direction dir){
		Obstacle wall = new Wall();
		this.setObstacleAt(dir, wall);
	}
	
	/**
	 * Method to build doors at given direction.
	 *
	 * @param dir The direction in which you want to add a wall.
	 * @post The door will be set at the given direction.
	 * 		|new.hasDoor(dir) == true
	 * @post If the square has a neighbor at the given direction, then a door will also be set in the adjecant square at opposite direction.
	 * 		|If(getNeighbor(dir) != null) => getNeighborAt(dir).hasWallAt(dir.oppositeDirection()

	 */
	public void buildDoorAt(Direction dir){
		Obstacle door = new Door();
		this.setObstacleAt(dir, door);
	}
	
	//TODO doc
	public void buildDoorAt(Direction dir, boolean isOpen){
		buildDoorAt(dir);
		setDoorOpen(dir, isOpen);
	}
	
	//TODO
	public void destroyObstacleAt(Direction dir){
		// might update tempatature 
	}
	
	//TODO doc
	public void setDoorOpen(Direction dir, boolean isOpen){
		//might update temp
		//TODO
	}
	
	public boolean canCollapse(){
		return false;
	}
	
	/**
	 * Method to set obstacle at given direction.
	 * 
	 * @param dir The direction in which you want to add an obstacle.
	 * @param obstacle The obstacle you want to add.
	 * @post The obstacle will be set at the given direction.
	 * 		|new.getObstacleAt(dir) == obstacle
	 * @post If the square has a neighbor at the given direction, then the obstacle will also be set in the adjecant square at opposite direction.
	 * 		|If(getNeighbor(dir) != null) => getNeighborAt(dir).getObstacleAt(dir.oppositeDirection()) == obstacle
	 */
	private void setObstacleAt(Direction dir, Obstacle obstacle){
		this.obstaclesMap.put(dir, obstacle);
		if(this.getNeighborAt(dir) != null){
			this.getNeighborAt(dir).obstaclesMap.put(Direction.getInverse(dir), obstacle);
		}
	}
	
	//TODO Afwerken
	/**
	 * Fix broken obstacles.
	 */
	public void fixObstacles(Direction[] destroyObstacles){
		
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
	public EnumMap<Direction, Square> getNeighborsMap(){
		return neighborsMap;
	}
	
	/**
	 * Inspector to receive the number of neighbors.
	 * 
	 * @return Returns the number of squares connected to this one.
	 * 		|return == NeighborsMap.size()
	 */
	public int getNbNeighbors(){
		return neighborsMap.size();
	}
	
	/**
	 * Inspector to retrieve neighbors in given direction.
	 * 
	 * @param dir The selected direction.
	 * @return The neighbor in the given direction.
	 * 		|return == NeighborsMap.get(dir)
	 */
	public Square getNeighborAt(Direction dir){
		return neighborsMap.get(dir);
	}
	
	/**
	 * Checker to control if this square has a given square as neighbor.
	 * 
	 * @param square The square that could be connected to this one.
	 * @return Returns true if the square is connected to this one.
	 * 		|result == (for some Neighbor in getNeighborsMap(): Neighbor == square)
	 */
	public boolean hasAsNeighbor(Square square){
		for(Square s: neighborsMap.values()){
			if(square == s){
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
	public boolean hasAsNeighborAt(Direction dir, Square square){
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
	public boolean hasProperNeighbors(){
		for(Entry<Direction, Square> e: neighborsMap.entrySet()){
				if(!canHaveAsNeighbor(e.getValue()) || !e.getValue().hasAsNeighborAt(Direction.getInverse(e.getKey()), this)){
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
	* 		|isTerminated() => ( result == ( square == null ) )
	* @return Else if the given square is not null, true if the given square is not terminated.
	*		|(!isTerminated() && square != null ) => ( result == !square.isTerminated() )
	* @return Else always true
	* 		|(!isTerminated() && square == null ) => ( result == true )
	* @note If a neighbor is accepted by this method, it is automatically accepted in all directions.
	* Method 'canHaveAsNeighborAt(Direction)' is not needed.
	*/
	public boolean canHaveAsNeighbor(Square square){
	//TODO maybe check is they are in the same dungeon? see 'PROBLEM' in Dungeon
		if(isTerminated){
			return square == null;
		}
		else
		{
			if(square != null)
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
	@Model @Raw
	private void setNeighborAt(Direction dir, Square square) throws IllegalArgumentException{
		if(!canHaveAsNeighbor(square)){
			throw new IllegalArgumentException("The given square is not a valid square to connect!");
		}
		else{
			if(square == null){
				this.neighborsMap.remove(dir);
			}
			else{
				this.neighborsMap.put(dir, square);
			}
		}		
	}
	
	/**
	 * Method to copy all the neighbors from a given square to this one.
	 * 
	 * @param other The other square from which you want to copy.
	 * @post If the given square is not null, all the neighbors will be copied to this one. The directions will be the same.
	 * 		|if(square != null) => (for each direction in Direction.values(): new.getNeighborAt(direction) == other.getNeighborAt(direction)
	 * @note This is annotated raw because the temperature is not updated.
	 */
	@Raw
	public void copyNeighbors(Square other){
		if(other != null){
			this.neighborsMap = other.neighborsMap;
		}
	}
	
	/**
	* Register the given square as a neighbor of this square in the given direction.
	* 
	* @param square The square to register as a neighbor
	* @param direction The direction of the given square relative to this square.
	* @post If the new neighbor is a legal neighbor, this square has the given square as its neighbor in the given direction. Also the given square has this square as its neighbor in the inverse of the given direction.
	*	   |if(this.canHaveAsNeighbor(square) && square.canHaveAsNeighbor(this))	=> (new.getNeighborAt(direction) == square && (new.square).getNeighborAt(Direction.oppositeDirection()) == this )
	* @note Annotated raw because this method does not update temperature.
	*/
	@Raw
	public void registerNeighbor(Square square, Direction direction){
		if(this.canHaveAsNeighbor(square) && square.canHaveAsNeighbor(this)){
			try{
				this.setNeighborAt(direction, square);
				square.setNeighborAt(Direction.getInverse(direction), this);
			}
			catch(IllegalArgumentException e){
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
	* 		|if(this.canHaveAsNeighbor(null) && (this.getNeighborAt(direction).canHaveAsNeighbor(null)) => (new.getNeighborAt(direction) == null) && ((new.this).getNeighborAt(direction)).getNeighborAt(direction.oppositeDirection() == null )
	*/
	public void removeNeighbor(Direction direction){
		Square other = getNeighborAt(direction);
		if(other != null){
			if(this.canHaveAsNeighbor(null) && other.canHaveAsNeighbor(null)){
				try{
					setNeighborAt(direction, null);
					other.setNeighborAt(Direction.getInverse(direction), null);
				}
				catch(IllegalArgumentException e){
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
	 * Method to destroy a wall between two squares.
	 * 
	 * @param dir The direction in which the wall must be destroyed seen from this square.
	 * @post The wall at the given direction will be destroyed.
	 * 		|new.getObstacleAt(dir) == null
	 * @post If there is a neighbor, then the wall at the adjecant square at the given direction will also be destroyed.
	 * 		|(new.getNeighborAt(dir)).getObstacleAt(dir.oppositeDirection) == null
	 * @throws IllegalArgumentException [MUST] There is no wall at the given position
	 * 		|!(old.getObstacleAt(dir) instanceof Wall)
	 */
	public void destroyWallAt(Direction dir) throws IllegalArgumentException{
		if(this.hasWall(dir)){
			this.setObstacleAt(dir, null);
			if(this.getNeighborAt(dir) != null){
				this.getNeighborAt(dir).setObstacleAt(Direction.getInverse(dir), null);
			}
		}
		else{
			throw new IllegalArgumentException("There is no wall at that direcetion!");
		}
	}
	
	/**
	 * Method that checks if you can move in a certain direction.
	 * 
	 * @param dir The direction to move in.
	 * @return Returns true if there is neighbor in that direction and there is no wall between them or the door is open.
	 * 		|result == ((this.getNeighborAt(dir) != null) && (!this.hasWallAt(dir)) && (if(this.hasDoorAt(dir)) => getObstacleAt(dir).isOpen())
	 */
	public boolean canMove(Direction dir){
		if(this.getNeighborAt(dir) != null){
			if(this.hasWall(dir)){
				return false;
			}
			if(this.hasDoor(dir)){
				return ((Door)this.getObstacleAt(dir)).isOpen();
			}
			else{
				return true;
			}
		}
		else{
			return false;
		}
	}
	
	// TODO Afwerken
	/**
	 * Method to compute a group of moveable squares.
	 * 
	 * 
	 */
	public ArrayList<Square> computeGroup(){
		return null;
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
		if(this.getNeighborAt(dir) != null){
			this.temperature = (this.getTemperature()+this.getNeighborAt(dir).getTemperature())/2;
			this.getNeighborAt(dir).setTemperature(this.getTemperature()); 
		}
		else{
			throw new IllegalArgumentException("There is no square to merge in that direction!");
		}
	}
	
	// TODO Afwerken
	/**
	 * Method to merge a group of squares with another group of squares.
	 * 
	 * 
	 */
	public void mergeWithGroup(Direction dir){
		
	}

}
