package dungeons;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.util.Direction;
import dungeons.util.Point;

public class ShaftTest {

	private Shaft shaft;
	
	@Before
	public void setUp() throws Exception {
		shaft = new Shaft(new Point(5, 1, 1), Direction.EAST);
	}

	/**********************************************************
	 * DIRECTION
	 **********************************************************/
	
	@Test
	public void testGetDirection(){
		// Trivial...
		assertEquals(Direction.EAST, shaft.getDirection());
	}
	
	@Test
	public void testIsValidDirection(){
		assertTrue(Shaft.isValidDirection(Direction.EAST));
		assertFalse(Shaft.isValidDirection(Direction.WEST));
		assertFalse(Shaft.isValidDirection(null));
	}
	
	@Test
	public void testCanHaveAsDirectionForMaximumDimensions(){
		assertTrue(shaft.canHaveAsDirectionForMaximumDimensions(Direction.NORTH, new Point(1,5,1)));
		assertFalse(shaft.canHaveAsDirectionForMaximumDimensions(Direction.EAST, new Point(1,5,1)));
		assertFalse(shaft.canHaveAsDirectionForMaximumDimensions(null, new Point(1,5,1)));
		assertFalse(shaft.canHaveAsDirectionForMaximumDimensions(Direction.NORTH, null));
		
	}
	
	/**********************************************************
	 * MAXIMUM DIMENSIONS
	 **********************************************************/
	
	@Test
	public void testCanAcceptAsNewMaximumDimensions(){
		assertTrue(shaft.canAcceptAsNewMaximumDimensions(new Point(6,1,1)));
		assertFalse(shaft.canAcceptAsNewMaximumDimensions(new Point(6,2,1)));
		assertFalse(shaft.canAcceptAsNewMaximumDimensions(new Point(1,6,1)));
		assertFalse(shaft.canAcceptAsNewMaximumDimensions(null));
		// Also see DungeonTest for tests that are not Shaft specific
	}
	
	@Test
	public void testChangeMaximumDimensions_legal() throws IllegalMaximumDimensionsException{
		//TODO
		/*Point pos = shaft.getMaximumDimensions();
		shaft.changeMaximumDimensions(new Point(6,1,1));
		while(shaft.getMaximumDimensions().isEqualOrBiggerThanPoint(pos)){
			assertTrue(shaft.hasSquareAt(pos));
			pos = shaft.getDirection().getPointInThisDirectionOf(pos);
		}*/
	}
	
	@Test (expected = IllegalMaximumDimensionsException.class)
	public void testChangeMaximumDimensions_illegal() throws IllegalMaximumDimensionsException{
		shaft.changeMaximumDimensions(new Point(6,2,1));
	}
	
	@Test
	public void testExtend(){
		//TODO
	}
	
	/**********************************************************
	 * SQUARE MANAGEMENT
	 **********************************************************/

	@Test
	public void canHAveAsSquare(){
		assertTrue(shaft.canHaveAsSquare(new Square()));
		assertFalse(shaft.canHaveAsSquare(null));
		assertFalse(shaft.canHaveAsSquare(new Rock()));
	}
	
}
