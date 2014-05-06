/**
 * The package for a roleplaying game.
 */
package dungeons.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Testclass for Direction
 * 
 * @author Christof Vermeersch & Edward Van Sieleghem
 */
public class DirectionTest {

	/**
	 * Test method for oppositeDirection().
	 */
	@Test
	public void testOppositeDirection() {
		Direction northDirection = Direction.NORTH;
		Direction southDirection = Direction.SOUTH;
		Direction westDirection = Direction.WEST;
		Direction eastDirection = Direction.EAST;
		Direction upDirection = Direction.UP;
		Direction downDirection = Direction.DOWN;
		assertEquals(northDirection.oppositeDirection(), Direction.SOUTH);
		assertEquals(southDirection.oppositeDirection(), Direction.NORTH);
		assertEquals(westDirection.oppositeDirection(), Direction.EAST);
		assertEquals(eastDirection.oppositeDirection(), Direction.WEST);
		assertEquals(upDirection.oppositeDirection(), Direction.DOWN);
		assertEquals(downDirection.oppositeDirection(), Direction.UP);
	}

}
