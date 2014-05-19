package dungeons.dungeon;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.util.Point;

/**
 * A Unit Test for the AbstractDungeon. Most methods from Dungeon are tested in Unit tests SquareDungeonTest and CompositeDungeonTest
 * (instead of here). Where needed, methods are tested in both test classes (this is the case for methods that are overwritten in
 * the sub classes of Dungeon).
 * 
 * Two test setups where designed to try and cover all the functionalities of all the different kinds of dungeons.
 * - The first setup focuses on the aspects of dungeon hierarchies, and is implemented in CompositeDungeonTest. This includes methods to do
 * 		with SubDungeons, ParentDugneons and all the methods that were overwritten in CompositeDungeon.
 * - The second setup focuses on the aspects of square management, and is implemented in SquareDungeonTest. This includes methods to do with
 * 		obstacles, neighbors and temperature of squares relative to the dungeon they belong in.
 * (Some aspects might be present in both setups).
 * 
 * A very straightforward unit test is implemented here that test the basic functionalities of the dimensions of dungeons.
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 */
public class AbstractDungeonTest {
	
	private AbstractDungeon dungeon_dim500;
	
	@Before
	public void setUp() throws Exception {
		dungeon_dim500 = new Level(new Point(500,500,1));
	}
	
	@Test
	public void testDungeonConstructor() throws IllegalMaximumDimensionsException{
		Point dim = new Point(100,200,300);
		AbstractDungeon d = new CompositeDungeon(dim);
		assertEquals(d.getMaximumDimensions(), dim);
		// Note that most association in this program are partial, so they should
		// not be initialized during construction. MaximumDimensions it's though.
	}
	
	/*************************************************************
	 * DIMENSION TESTS
	 **************************************************************/
	
	@Test
	public void testCanHaveAsMaximumDimensions(){
		// test some difference dimensions + boundaries
		Point goodDim = new Point(500,500,1);
		Point tooLargeDim = new Point(500,5001,1);
		Point negativeDim = new Point(-500,-500,1);
		Point origin = new Point(0,0,0);
		Point goodButSmall = new Point (1,1,1);
		
		assertTrue(dungeon_dim500.canHaveAsMaximumDimensions(goodDim));
		assertFalse(dungeon_dim500.canHaveAsMaximumDimensions(tooLargeDim)); // To large maximum dimensions
		assertFalse(dungeon_dim500.canHaveAsMaximumDimensions(null)); // no dimensions at all
		assertFalse(dungeon_dim500.canHaveAsMaximumDimensions(negativeDim)); // negative dimension
		assertFalse(dungeon_dim500.canHaveAsMaximumDimensions(origin)); // no squares are contained...
		assertTrue(dungeon_dim500.canHaveAsMaximumDimensions(goodButSmall)); // one one square is contained -> legal!
	}
	
	@Test
	public void testCanAcceptAsNewMaximumDimensions(){
		// the size of a dungeon can only be enlarged
		Point smallerDim = new Point(300,500,1);
		Point largetDim = new Point(500,600,1);
		assertTrue(dungeon_dim500.canAcceptAsNewMaximumDimensions(largetDim)); // larger maximum dimension
		assertFalse(dungeon_dim500.canAcceptAsNewMaximumDimensions(smallerDim)); // smaller maximum dimensions
	}

	@Test
	public void testChangeMaximumDimensions_legal() throws IllegalMaximumDimensionsException{
		Point legalDim = new Point(501,501,1);
		dungeon_dim500.changeMaximumDimensions(legalDim);
		assertEquals(legalDim, dungeon_dim500.getMaximumDimensions());
	}
	
	@Test (expected = IllegalMaximumDimensionsException.class)
	public void testChangeMaximumDimensions_illegalTooSmall() throws IllegalMaximumDimensionsException{
		// Test to check if exception triggers properly, when the size of a dungeon is changed to something illegal
		Point tooSmallDim = new Point(499,500,500);
		dungeon_dim500.changeMaximumDimensions(tooSmallDim);
	}
	
	
	/*************************************************************
	 * SQUARE MANAGEMENT TESTS - TOTOAAL
	 **************************************************************/
	// see SquareDungeonTest for non-abstract methods that where not overwritten in subclasses
	//		or CompositeDungeonTest && SquareDungeonTest for all the other methods
	
	/*************************************************************
	 * PARENT DUNGEON TESTS
	 **************************************************************/
	// see CompositeDungeonTest
	
}

