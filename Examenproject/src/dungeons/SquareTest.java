/**
 * The package for a roleplaying game.
 */
package dungeons;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import dungeons.obstacle.*;
import dungeons.util.*;


/**
 * Testclass for Square.
 *
 * @author Christof Vermeersch & Edward Van Sieleghem
 */
public class SquareTest {

	private Square testSquare1, testSquare2, testSquare3, testSquare4, testSquare5, testSquare6;
	
	@Before
	public void setUp() {
		/*
		 * 4 different Squares, respectively without, too high, too low and good temperature as parameter.
		 */
		testSquare1 = new Square();
		testSquare6 = new Square();
		testSquare2 = new Square(55);
		testSquare3 = new Square(-55);
		testSquare4 = new Square(15);
		testSquare5 = new Square(15);
		testSquare4.registerNeighbor(testSquare3, Direction.EAST);
		testSquare4.buildDoorAt(Direction.EAST, true);
		testSquare5.buildDoorAt(Direction.NORTH);
		testSquare5.registerNeighbor(testSquare1, Direction.EAST);
	}

	/**
	 * Test method for the constructor of Square without arguments.
	 */
	@Test
	public void testSquare() {
		assertTrue(testSquare1.getTemperature() == Square.getDefaultTemperature());
		Square wallTest = new Square();
		assertTrue(wallTest.hasWall(Direction.NORTH));
		assertTrue(wallTest.hasWall(Direction.SOUTH));
		assertTrue(wallTest.hasWall(Direction.EAST));
		assertTrue(wallTest.hasWall(Direction.WEST));
		assertTrue(wallTest.hasWall(Direction.UP));
		assertTrue(wallTest.hasWall(Direction.DOWN));
	}

	/**
	 * Test method for the constructor of Square with arguments.
	 */
	@Test
	public void testSquareInt() {
		assertTrue(testSquare2.getTemperature() == 55);
	}

	/**
	 * Test method for isValidTemperature.
	 */
	@Test
	public void testIsValidTemperature() {
		/*
		 * Invalid temperature -201 and +5001.
		 */
		assertFalse(Square.isValidTemperature(-201));
		assertFalse(Square.isValidTemperature(5001));
		/*
		 * Valid temperature +1000.
		 */
		assertTrue(Square.isValidTemperature(1000));
	}

	/**
	 * Test method for calculateTemperatureDamage().
	 */
	@Test
	public void testCalculateTemperatureDamage() {
		/*
		 * HeatDamage
		 */
		assertEquals(testSquare2.calculateTemperatureDamage(), 1);
		/*
		 * ColdDamage
		 */
		assertEquals(testSquare3.calculateTemperatureDamage(), 1);
		/*
		 * No damage
		 */
		assertEquals(testSquare6.calculateTemperatureDamage(), 0);
	}

	/**
	 * Test method for setMaxTemperature().
	 */
	@Test
	public void testSetMaxTemperature() {
		Square.setMaxTemperature(1000);
		assertTrue(Square.getMaxTemperature() == 1000);
		Square.setMaxTemperature(5000);
	}

	/**
	 * Test method for setMinTemperature.
	 */
	@Test
	public void testSetMinTemperature() {
		Square.setMinTemperature(-1000);
		assertTrue(Square.getMinTemperature() == -1000);
		Square.setMinTemperature(-2000);
	}

	/**
	 * Test method for setUnitHeatDamage.
	 */
	@Test
	public void testSetUnitHeatDamage() {
		Square.setUnitHeatDamage(10);
		assertTrue(Square.getUnitHeatDamage() == 10);
		Square.setUnitHeatDamage(1);
	}

	/**
	 * Test method for setHeatDamageStep.
	 */
	@Test
	public void testSetHeatDamageStep() {
		Square.setHeatDamageStep(10);
		assertTrue(Square.getHeatDamageStep() == 10);
		Square.setUnitHeatDamage(15);
	}

	/**
	 * Test method for setHeatDamageAbove.
	 */
	@Test
	public void testSetHeatDamageAbove() {
		Square.setHeatDamageAbove(10);
		assertTrue(Square.getHeatDamageAbove() == 10);
		Square.setHeatDamageAbove(35);
	}

	/**
	 * Test method for getObstacleAt().
	 */
	@Test
	public void testGetObstacleAt() {
		//TODO zeker geen instanceof in in een test gebruiken...
		//TODO Christof: Is test van deze getter wel nodig?
		assertTrue(testSquare4.getObstacleAt(Direction.EAST) instanceof Door);
		assertTrue(testSquare4.getObstacleAt(Direction.NORTH) instanceof Wall);
		testSquare4.registerNeighbor(testSquare1, Direction.SOUTH);
		testSquare4.destroyObstacleAt(Direction.SOUTH);
		assertTrue(testSquare4.getObstacleAt(Direction.SOUTH) ==  null);
		testSquare4.buildWallAt(Direction.SOUTH);
	}

	/**
	 * Test method for getNeighborAt().
	 */
	@Test
	public void testGetNeighborAt() {
		assertEquals(testSquare4.getNeighborAt(Direction.NORTH), null);
		assertEquals(testSquare4.getNeighborAt(Direction.EAST), testSquare3);
	}

	/**
	 * Test method for hasDoor.
	 */
	@Test
	public void testHasDoor() {
		assertTrue(testSquare4.hasDoor(Direction.EAST));
	}

	/**
	 * Test method for hasWall.
	 */
	@Test
	public void testHasWall() {
		assertTrue(testSquare4.hasWall(Direction.NORTH));
	}

	/**
	 * Test method for buildWallAt().
	 */
	@Test
	public void testBuildWallAt() {
		testSquare4.registerNeighbor(testSquare1, Direction.WEST);
		testSquare4.buildWallAt(Direction.WEST);
		assertTrue(testSquare1.hasWall(Direction.EAST));
		assertTrue(testSquare4.hasWall(Direction.WEST));
	}
	
	/**
	 * Test method for buildDoorAt().
	 */
	@Test
	public void testBuildDoorAt() {
		testSquare4.registerNeighbor(testSquare1, Direction.WEST);
		testSquare4.buildDoorAt(Direction.WEST);
		assertTrue(testSquare1.hasDoor(Direction.EAST));
		assertTrue(testSquare4.hasDoor(Direction.WEST));
	}

	/**
	 * Test method for registerNeighbor().
	 */
	@Test
	public void testRegisterNeighbor() {
		assertEquals(testSquare4.getNeighborAt(Direction.EAST), testSquare3);
		assertEquals(testSquare3.getNeighborAt(Direction.EAST.oppositeDirection()), testSquare4);
	}

	/**
	 * Test method for mergeWith().
	 */
	@Test
	public void testMergeWith() {
		testSquare4.mergeWith(Direction.EAST);
		assertEquals(testSquare4.getTemperature(),-20);
		assertEquals(testSquare3.getTemperature(),-20);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testMergeWithException() {
		testSquare4.mergeWith(Direction.UP);
		fail("The exception must be thrown");
	}

	/**
	 * Test method for destroyWallAt().
	 */
	@Test
	public void testDestroyWallAt() {
		testSquare4.destroyObstacleAt(Direction.EAST);
		assertEquals(testSquare4.getObstacleAt(Direction.EAST), null);
		assertEquals(testSquare3.getObstacleAt(Direction.EAST.oppositeDirection()), null);
	}
	
	/**
	 * Test method for hasProperNeighbors().
	 */
	@Test
	public void testHasProperNeighbors() {
		assertTrue(testSquare4.hasProperNeighbors());
	}
	
	/**
	 * Test method for canHaveAsNeighbor().
	 */
	@Test
	public void testCanHaveAsNeighbor() {
		Square testSquareTerminated = new Square();
		testSquareTerminated.terminate();
		assertFalse(testSquare4.canHaveAsNeighbor(testSquareTerminated));
		assertTrue(testSquare4.canHaveAsNeighbor(null));
		assertTrue(testSquare4.canHaveAsNeighbor(testSquare1));
	}
	
	/**
	 * Test method for removeNeighbor().
	 */
	@Test
	public void testRemoveNeighbor() {
		testSquare4.removeNeighbor(Direction.EAST);
		assertEquals(testSquare4.getNeighborAt(Direction.EAST), null);
		testSquare4.registerNeighbor(testSquare3, Direction.EAST);
	}
}	

