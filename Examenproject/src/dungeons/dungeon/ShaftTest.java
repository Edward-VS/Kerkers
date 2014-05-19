package dungeons.dungeon;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.square.Rock;
import dungeons.square.Square;
import dungeons.util.Direction;
import dungeons.util.Point;

/**
 * Unit test for testing methods in shaft that were overwritten of newly implemented.
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 */
public class ShaftTest {

	private Shaft shaft;
	
	/**
	 * Setup a shaft that will be inspected and modified in all the tests of this unit.
	 */
	@Before
	public void setUp() throws Exception {
		// shaft with direction NORTH
		shaft = new Shaft(new Point(1, 3, 1));
	}

	@Test
	public void testShaft(){
		// the newly created shaft is filled up with squares
		assertNotNull(shaft.getSquareAt(new Point(0,0,0)));
		assertNotNull(shaft.getSquareAt(new Point(0,1,0)));
		assertNotNull(shaft.getSquareAt(new Point(0,2,0)));
		assertNull(shaft.getSquareAt(new Point(0,3,0)));
		
		// the newly created shaft has no internal walls (not doors because we didn't build any)
		assertTrue(shaft.getSquareAt(new Point(0,0,0)).hasWall(Direction.SOUTH));
		assertFalse(shaft.getSquareAt(new Point(0,0,0)).hasWall(Direction.NORTH));
		assertFalse(shaft.getSquareAt(new Point(0,1,0)).hasWall(Direction.NORTH));
		assertTrue(shaft.getSquareAt(new Point(0,2,0)).hasWall(Direction.NORTH));
	}
	
	/**********************************************************
	 * MAXIMUM DIMENSIONS
	 **********************************************************/
	
	@Test
	public void getDirection(){
		assertSame(Direction.NORTH, shaft.getDirection());
	}
	
	/**
	 * @see AbstractDungeonTest
	 */
	@Test
	public void testCanHaveAsMaximumDimensions(){
		// dimension in a distinctive direction
		assertTrue(shaft.canHaveAsMaximumDimensions(new Point(1,2,1)));
		// a square sized dungeon
		assertTrue(shaft.canHaveAsMaximumDimensions(new Point(1,1,1)));
		// dimension in two directions
		assertFalse(shaft.canHaveAsMaximumDimensions(new Point(2,2,1)));
	}
	
	/**
	 * @note This test in included to study indirect effects of the method canHaveAsMaximumDimensions (overwritten in shaft)
	 * 		being called by canAccespAsNewMaxiumDimensions (not overwritten in shaft)
	 */
	@Test
	public void testCanAcceptAsNewMaximumDimensions(){
		// the same dimensions
		assertTrue(shaft.canAcceptAsNewMaximumDimensions(new Point(1,3,1)));
		// larger dimensions in proper direction
		assertTrue(shaft.canAcceptAsNewMaximumDimensions(new Point(1,4,1)));
		// larger dimensions in two directions
		assertFalse(shaft.canAcceptAsNewMaximumDimensions(new Point(2,4,1)));
		// small dimensions in proper direction
		assertFalse(shaft.canAcceptAsNewMaximumDimensions(new Point(1,2,1)));
	}
	
	@Test
	public void testHasProperSquares(){
		assertTrue(shaft.hasProperSquares());
	}
	
	@Test
	public void testParseDirectionFromMaximumDimensions(){
		assertSame(Direction.EAST, Shaft.parseDirectionFromMaximumDimensions(new Point(2,1,1)));
		assertSame(Direction.NORTH, Shaft.parseDirectionFromMaximumDimensions(new Point(1,2,1)));
		assertSame(Direction.UP, Shaft.parseDirectionFromMaximumDimensions(new Point(1,1,2)));
		assertSame(Shaft.DEFAULT_DIRECTION, Shaft.parseDirectionFromMaximumDimensions(Point.CUBE));
		assertNull(Shaft.parseDirectionFromMaximumDimensions(new Point(2,2,1)));
		assertNull(Shaft.parseDirectionFromMaximumDimensions(new Point(1,-2,1)));
	}
	
	/**
	 * @see AbstractDungeonTest
	 */
	@Test
	public void testChangeMaximumDimensions_legal() throws IllegalMaximumDimensionsException{
		shaft.changeMaximumDimensions(new Point(1,4,1));

		assertNotNull(shaft.getSquareAt(new Point(0,0,0)));
		assertNotNull(shaft.getSquareAt(new Point(0,1,0)));
		assertNotNull(shaft.getSquareAt(new Point(0,2,0)));
		assertNotNull(shaft.getSquareAt(new Point(0,3,0)));
		assertNull(shaft.getSquareAt(new Point(0,4,0)));
	}
	

	@Test (expected = IllegalMaximumDimensionsException.class)
	public void testChangeMaximumDimensions_Illegal() throws IllegalMaximumDimensionsException{
		// New maximum dimensions that cannot be parsed into a direction
		shaft.changeMaximumDimensions(new Point(2,4,1));
	}
	
	@Test
	public void testExtend() throws IllegalMaximumDimensionsException{
		// Extend the shaft with 2 (=> new squares are created)
		shaft.extend(2);
		assertEquals(new Point(1,5,1), shaft.getMaximumDimensions());
		assertNotNull(shaft.getSquareAt(new Point(0,4,0)));
		
		// test for single-square shaft with the (mutable) default direction
		Shaft smallShaft = new Shaft();
		smallShaft.extend(2);
		assertSame(Shaft.DEFAULT_DIRECTION, smallShaft.getDirection());
		assertEquals(3, smallShaft.getMaximumDimensions().size());
	}
	
	/**********************************************************
	 * SQUARE MANAGEMENT
	 **********************************************************/

	/**
	 * @see SquareDungeonTest
	 */
	@Test
	public void canHaveAsSquare(){
		assertTrue(shaft.canHaveAsSquare(new Square()));
		assertFalse(shaft.canHaveAsSquare(null));
		assertFalse(shaft.canHaveAsSquare(new Rock()));
	}
	
	/**
	 * @see SquareDungeonTest
	 */
	@Test
	public void removeAsSquareAt(){
		// Removing a square from a non-terminated shaft will do nothing
		shaft.removeAsSquareAt(Point.ORIGIN);
		assertNotNull(shaft.getSquareAt(Point.ORIGIN));
		
		// terminating the shaft will remove all squares
		shaft.terminate();
		assertNull(shaft.getSquareAt(Point.ORIGIN));
	}
	
}
