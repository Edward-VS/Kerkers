/**
 * The package for a roleplaying game.
 */
package dungeons.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit test for Direction.
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
	
	@Test
	public void testGetPointInThisDirectionOf() {
		assertEquals(new Point(1,3,3), Direction.NORTH.getPointInThisDirectionOf(new Point(1,2,3)));
		assertEquals(new Point(1,1,3), Direction.SOUTH.getPointInThisDirectionOf(new Point(1,2,3)));
		assertEquals(new Point(2,2,3), Direction.EAST.getPointInThisDirectionOf(new Point(1,2,3)));
		assertEquals(new Point(0,2,3), Direction.WEST.getPointInThisDirectionOf(new Point(1,2,3)));
		assertEquals(new Point(1,2,4), Direction.UP.getPointInThisDirectionOf(new Point(1,2,3)));
		assertEquals(new Point(1,2,2), Direction.DOWN.getPointInThisDirectionOf(new Point(1,2,3)));
	}

}
