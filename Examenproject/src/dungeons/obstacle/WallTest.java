/**
 * The package for a roleplaying game.
 */
package dungeons.obstacle;

import static org.junit.Assert.*;

import org.junit.Test;

import dungeons.square.Square;
import dungeons.util.Direction;

/**
 * A testclass for the class of walls.
 * 
 * @author Christof Vermeersch & Edward Van Sieleghem
 */
public class WallTest {

	/**
	 * Test method for canBeAnObstacleAt().
	 */
	@Test
	public void testCanBeAnObstacleAt() {
		Square square1 = null;
		Square square2 = new Square();
		Wall wall = new Wall();
		//Test on null reference.
		assertFalse(wall.canBeAnObstacleAt(square1, Direction.NORTH));
		//Test on normal reference.
		assertTrue(wall.canBeAnObstacleAt(square2, Direction.NORTH));
	}
}
