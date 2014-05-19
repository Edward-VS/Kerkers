package dungeons.dungeon;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.util.Point;

/**
 * Unit test for testing methods in Level that were overwritten of newly implemented.
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 */
public class LevelTest {

	Level lvl;
	
	/**
	 * Setup a Level that will be used in the rest of the unit test.
	 */
	@Before
	public void setUp() throws Exception {
		lvl = new Level(new Point(5,5,1));
	}

	@Test (expected = IllegalMaximumDimensionsException.class)
	public void testLevel_Illegal() throws IllegalMaximumDimensionsException{
		new Level (new Point(2,2,2));
	}
	
	/***********************************************************
	 * DIMENSIONS
	 ***********************************************************/
	
	/**
	 * @see AbstractDungeonTest
	 */
	@Test
	public void testCanHaveAsMaximumDimensions() {
		// the maximum dimensions must have a z-coordinate of 1
		assertTrue(lvl.canHaveAsMaximumDimensions(new Point(6,6,1)));
		assertFalse(lvl.canHaveAsMaximumDimensions(new Point(5,5,2)));
	}

	@Test
	public void testEnlarge() throws IllegalMaximumDimensionsException {
		lvl.enlarge(3, 5);
		assertEquals(new Point(8,10,1), lvl.getMaximumDimensions());
	}
	
}
