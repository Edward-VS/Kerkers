/**
 * The package for a roleplaying game.	
 */
package dungeons.obstacle;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import dungeons.square.Rock;
import dungeons.square.Square;
import dungeons.util.Direction;

/**
 * A testclass for the class of Doors.
 * 
 * @author Christof Vermeersch & Edward Van Sieleghem
 */
public class DoorTest {
	
	private Square square1, square2, squareTerminated, square3, square4, rock;
	private Door door;
	
	/**
	 * Method to build the testsuite.
	 */
	@Before
	public void setUp(){
		square1 = new Square();
		square2 = new Square();
		square3 = new Square();
		square4 = null;
		square1.registerNeighbor(square2, Direction.NORTH);
		rock = new Rock();
		squareTerminated = new Square();
		squareTerminated.terminate();
		door = new Door();
	}
	
	/**
	 * Test method for canBeAnObstacleAt()
	 */
	@Test
	public void testCanBeAnObstacleAt() {
		//Test a null reference, terminated square, rock and square without neigbors.
		assertFalse(door.canBeAnObstacleAt(square4, Direction.NORTH));
		assertFalse(door.canBeAnObstacleAt(squareTerminated, Direction.NORTH));	
		assertFalse(door.canBeAnObstacleAt(square3, Direction.NORTH));
		assertFalse(door.canBeAnObstacleAt(rock, Direction.NORTH));
		//Test a valid reference.
		assertTrue(door.canBeAnObstacleAt(square1, Direction.NORTH));
	}

	/**
	 * Test method for Door.
	 */
	@Test
	public void testDoor() {
		Door doorClosed = new Door();
		assertFalse(doorClosed.isOpen());
	}

	/**
	 * Test method for Door(boolean).
	 */
	@Test
	public void testDoorBoolean() {
		Door doorOpen = new Door(true);
		Door doorClosed = new Door(false);
		assertTrue(doorOpen.isOpen());
		assertFalse(doorClosed.isOpen());
	}

	/**
	 * Test method for SetOpen(boolean).
	 */
	@Test
	public void testSetOpenBoolean() {
		Door doorOpen = new Door(true);
		Door doorClosed = new Door(false);
		doorOpen.setOpen(false);
		doorClosed.setOpen(true);
		assertTrue(doorClosed.isOpen());
		assertFalse(doorOpen.isOpen());
	}

	/**
	 * Test method for SetOpen.
	 */
	@Test
	public void testSetOpen() {
		door.setOpen();
		assertTrue(door.isOpen());
	}

}
