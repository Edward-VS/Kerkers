package dungeons.dungeon;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import dungeons.exception.IllegalDungeonAtPositionException;
import dungeons.exception.IllegalDungeonException;
import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.square.Square;
import dungeons.util.Direction;
import dungeons.util.Point;
import dungeons.util.SquareIterator;

/**
 * A unit test for SquareDungeon. All overwritten methods from Dungeon are tested here. Also some methods that were implemented in AbstractDungeon
 * but not overwritten in SquareDungeon are tested here.
 * 
 * A test setup is defined for checking different functionalities concerning square management.
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 *
 */
public class SquareDungeonTest {

	private SquareDungeon sqrDun, trmDun;
	private Square square_1_0_0, trmSqr;
	
	/**
	 * The test setup contains a dungeon that is filled with squares.
	 */
	@Before
	public void setUp() throws Exception {
		// Test dungeon: |-_ walls ; ] door
		// All squares are given a number to make it easier to read the tests. Square 2 is explicitly stored, so tests that
		// require a square reference can use this one.
		//  _ _ 
		// |7 8|_    
		// |4|5|6|
		//  - -  
		// |1 2]3|
		//  - - -
		sqrDun = new Level(new Point(3,3,1));
		sqrDun.addAsSquareAt(new Square(13), new Point(0,0,0)); // 1
		square_1_0_0 = new Square(); /* 2 */
		sqrDun.addAsSquareAt(square_1_0_0, new Point(1,0,0), Direction.WEST); // 2
		sqrDun.addAsSquareAt(new Square(), new Point(2,0,0)); // 3
		sqrDun.addAsSquareAt(new Square(-5), new Point(0,1,0)); // 4
		sqrDun.addAsSquareAt(new Square(), new Point(1,1,0)); // 5
		sqrDun.addAsSquareAt(new Square(), new Point(2,1,0), Direction.SOUTH); // 6
		sqrDun.addAsSquareAt(new Square(), new Point(0,2,0), Direction.SOUTH); // 7
		sqrDun.addAsSquareAt(new Square(), new Point(1,2,0), Direction.SOUTH, Direction.WEST); // 8
		sqrDun.buildDoor(new Point(1,0,0), Direction.EAST, true); // an open door between 2 and 3
		// Note that some random temperatures were set for more conclusive tests
		
		// some terminated structures
		trmSqr = new Square();
		trmSqr.terminate();
		trmDun = new Level(new Point(3,3,1));
		trmDun.terminate();
	}
	
	
	/*************************************************************
	 * SQUARE MANAGEMENT TESTS - TOTOAAL
	 **************************************************************/
	
	@Test
	public void testGetSquares(){
		// Trivial test
		assertTrue(sqrDun.getSquares().contains(square_1_0_0));
		// The list of squares can never contain a null reference
		assertFalse(sqrDun.getSquares().contains(null));
	}
	
	@Test
	public void testGetSquareAt(){
		assertSame(square_1_0_0, sqrDun.getSquareAt(new Point(1,0,0)));
		// try to get a square where there is none
		assertNull(sqrDun.getSquareAt(new Point(2,2,0)));
	}
	
	@Test
	public void testGetNbSquares(){
		assertEquals(8, sqrDun.getNbSquares());
	}
	
	/**
	 * @note Test for implementation in AbstractDungeon (tested method not overwritten in SquareDungeon)
	 */
	@Test
	public void testGetSquareInDirectionOfPosition(){
		// look at square 1, and test if its neighbors are correct.
		assertSame(square_1_0_0, sqrDun.getSquareInDirectionOfPosition(new Point(0,0,0), Direction.EAST));
		assertNull(sqrDun.getSquareInDirectionOfPosition(new Point(0,0,0), Direction.SOUTH));
		assertNotNull(sqrDun.getSquareInDirectionOfPosition(new Point(0,0,0), Direction.NORTH));
	}
	
	@Test
	public void testGetPositionOfSquare(){
		assertEquals(new Point(1,0,0), sqrDun.getPositionOfSquare(square_1_0_0));
	}
	
	/**
	 * @note Test for implementation in AbstractDungeon (tested method not overwritten in SquareDungeon)
	 */
	@Test
	public void testCanHaveAsSquare(){
		Square legalSquare = new Square();
		
		assertTrue(sqrDun.canHaveAsSquare(legalSquare));
		assertFalse(sqrDun.canHaveAsSquare(null));
		assertFalse(sqrDun.canHaveAsSquare(trmSqr));
		assertFalse(trmDun.canHaveAsSquare(legalSquare));
	}
	
	/**
	 * @note Test for implementation in AbstractDungeon (tested method not overwritten in SquareDungeon)
	 * @see CompositeDungeonTest
	 */
	@Test
	public void testCanHaveAsSquareAt(){
		Square legalSquare = new Square();
		
		// existing squares can be replaced
		assertTrue(sqrDun.canHaveAsSquareAt(legalSquare, new Point(0,0,0)));
		// squares can not be added outside of the dungeon
		assertFalse(sqrDun.canHaveAsSquareAt(legalSquare, new Point(3,0,0)));
		// a square can be located a the position it is located at
		assertTrue(sqrDun.canHaveAsSquareAt(square_1_0_0, new Point(1,0,0)));
		// square that are already in a dungeon should not be added at a different position of their own
		assertFalse(sqrDun.canHaveAsSquareAt(square_1_0_0, new Point(0,0,0)));
	}
	
	/**
	 * @note Test for implementation in AbstractDungeon (tested method not overwritten in SquareDungeon)
	 */
	@Test
	public void testHasSquareAt(){
		assertTrue(sqrDun.hasSquareAt(new Point(0,0,0)));
		// not-populated square inside the dungeon
		assertFalse(sqrDun.hasSquareAt(new Point(2,2,0)));
		// square outside the dungeon
		assertFalse(sqrDun.hasSquareAt(new Point(3,3,0)));
	}
	
	@Test
	public void testHasAsSquare(){
		assertTrue(sqrDun.hasAsSquare(square_1_0_0));
		assertFalse(sqrDun.hasAsSquare(new Square()));
	}
	
	@Test
	public void testAddAsSquareAt(){
		// test conditions that should have been met after setUp() like neighbors, obstacles,...
		// We test the surroundings of 'square 2'
		
		// Neighbor checks
		assertSame(sqrDun.getSquareAt(new Point(0,0,0)), square_1_0_0.getNeighborAt(Direction.WEST)); /*1*/
		assertSame(sqrDun.getSquareAt(new Point(2,0,0)), square_1_0_0.getNeighborAt(Direction.EAST)); /*3*/
		assertSame(null, square_1_0_0.getNeighborAt(Direction.SOUTH)); // outside of the dungeon
		// Neighbor bidirectionality
		assertSame(square_1_0_0, sqrDun.getSquareAt(new Point(0,0,0)).getNeighborAt(Direction.EAST)); /*1*/
		assertSame(square_1_0_0, sqrDun.getSquareAt(new Point(2,0,0)).getNeighborAt(Direction.WEST)); /*3*/
		// Obstacles checks
		assertTrue(square_1_0_0.hasDoor(Direction.EAST)); /*3*/
		assertTrue(square_1_0_0.hasWall(Direction.NORTH)); /*5*/
		assertTrue(square_1_0_0.hasWall(Direction.SOUTH)); // at the edge of the dungeon
		assertFalse(square_1_0_0.hasWall(Direction.WEST)); /*1*/
		// Obstacles bidirectionality
		assertSame(square_1_0_0.getObstacleAt(Direction.EAST), sqrDun.getSquareAt(new Point(2,0,0)).getObstacleAt(Direction.WEST) /* 3 */);
		assertSame(square_1_0_0.getObstacleAt(Direction.NORTH), sqrDun.getSquareAt(new Point(1,1,0)).getObstacleAt(Direction.SOUTH) /* 5 */);
		// Temperature check (Note that the door between 2 and 3 is open)
		assertEquals(sqrDun.getSquareAt(new Point(2,1,0) /* 6 */).getTemperature(), square_1_0_0.getTemperature(), 0.00001f);
		assertEquals(sqrDun.getSquareAt(new Point(2,0,0) /* 3 */).getTemperature(), square_1_0_0.getTemperature(), 0.00001f);
		assertEquals(sqrDun.getSquareAt(new Point(0,0,0) /* 1 */).getTemperature(), square_1_0_0.getTemperature(), 0.00001f);
		// Dungeon of square check
		assertSame(sqrDun, square_1_0_0.getDungeon());
		
		// ------ REPLACEING SQUARE ------- //
		float oldTemp = square_1_0_0.getTemperature();
		Square s = new Square();
		sqrDun.addAsSquareAt(s, new Point(1,0,0));
		// removed square has no neighbors, and walls in all directions.
		for(Direction d: Direction.values()){
			assertTrue(square_1_0_0.hasWall(d));
			assertNull(square_1_0_0.getNeighborAt(d));
		}
		// temperature has not changed of removed square.
		assertEquals(oldTemp, square_1_0_0.getTemperature(), 0.0001f);
		// old neighbor of square does not have it as neighbor anymore
		assertNotSame(square_1_0_0, sqrDun.getSquareAt(new Point(0,0,0) /* 1 */).getNeighborAt(Direction.EAST));
		assertSame(s, sqrDun.getSquareAt(new Point(0,0,0) /* 1*/).getNeighborAt(Direction.EAST));
		// there is still a door east of the new square
		assertTrue(s.hasDoor(Direction.EAST));
		// removed square has no more dungeon, but added square does
		assertNull(square_1_0_0.getDungeon());
		assertSame(sqrDun, s.getDungeon());
	}
	
	@Test
	public void testRemoveSquare(){
		sqrDun.removeAsSquare(square_1_0_0);
		
		// the square does not belong to its dungeon anymore
		assertFalse(sqrDun.hasAsSquare(square_1_0_0));
		// walls are build at the outer edges of the dungeon
		assertTrue(sqrDun.getSquareAt(new Point(2, 0, 0) /* 3 */).hasWall(Direction.WEST));
		assertTrue(sqrDun.getSquareAt(new Point(0, 0, 0) /* 1 */).hasWall(Direction.EAST));
		// the square does not have any neighbors anymore, and is surrounded with walls
		for(Direction d: Direction.values()){
			assertTrue(square_1_0_0.hasWall(d));
			assertNull(square_1_0_0.getNeighborAt(d));
		}
		// neighboring squares dont have the square as its  neighbor anymore
		assertNull(sqrDun.getSquareAt(new Point(2, 0, 0) /* 3 */).getNeighborAt(Direction.WEST));
		assertNull(sqrDun.getSquareAt(new Point(2, 0, 0) /* 1 */).getNeighborAt(Direction.EAST));
		//removed square has no more dungeon
		assertNull(square_1_0_0.getDungeon());
	}
	
	@Test
	public void testSwapSquareAt(){
		// swapping 2 with 5. New obstacles will be as such that doors are privileged above walls
		// 						and walls are privileged above openings. Result:
		//  _ _ 		 _ _
		// |7 8|  	 	|7 8|
		//      -		   - -
		// |4|5|6|	->	|4|2]6|
		//  - -  		 - -
		// |1 2]3|		|1|5]3|
		//  - - -		 - - -
		Square s5 = sqrDun.getSquareAt(new Point(1,1,0));
		sqrDun.swapSquareAt(new Point(1,1,0), square_1_0_0);
		// the squares have a new position
		assertSame(square_1_0_0, sqrDun.getSquareAt(new Point(1,1,0)));
		assertSame(s5, sqrDun.getSquareAt(new Point(1,0,0)));
		// the obstacles are updated
		// - new walls
		assertTrue(square_1_0_0.hasWall(Direction.WEST));
		assertTrue(s5.hasWall(Direction.NORTH));
		// - bidirectional door between 2 and 6
		assertTrue(square_1_0_0.hasDoor(Direction.EAST));
		assertTrue(sqrDun.getSquareAt(new Point(2,1,0)).hasDoor(Direction.WEST));
		// - bidirectional door between 5 and 3
		assertTrue(s5.hasDoor(Direction.EAST));
		assertTrue(sqrDun.getSquareAt(new Point(2,0,0)).hasDoor(Direction.WEST));
	}
	
	/**
	 * @note Test for implementation in AbstractDungeon (tested method not overwritten in SquareDungeon)
	 */
	@Test
	public void testBuildDoor(){
		Square sqr = sqrDun.getSquareAt(new Point (0, 1, 0) /*4 */);
		
		sqrDun.buildDoor(new Point (0, 1, 0) /*4 */, Direction.NORTH, true);
		// same effect : sqrDun.buildDoor(sqr, Direction.NORTH, true);
		assertTrue(sqr.hasDoor(Direction.NORTH));
		
		// other square has a door as well
		assertTrue(sqrDun.getSquareAt(new Point (0,2,0) /* 7 */).hasDoor(Direction.SOUTH));
		
		// open door -> temperature changed
		assertEquals(sqrDun.getSquareAt(new Point(0,2,0) /* 7 */).getTemperature(), sqr.getTemperature(), 0.00001f);
		assertEquals(sqrDun.getSquareAt(new Point(1,2,0) /* 8 */).getTemperature(), sqr.getTemperature(), 0.00001f);
	}
	
	/**
	 * @note Test for implementation in AbstractDungeon (tested method not overwritten in SquareDungeon)
	 */
	@Test
	public void testBuildWall(){
		Square sqr = sqrDun.getSquareAt(new Point (0, 1, 0) /*4 */);
		
		sqrDun.buildWall(new Point (0, 1, 0) /*4 */, Direction.NORTH);

		assertTrue(sqr.hasWall(Direction.NORTH));
		// other square has a wall as well
		assertTrue(sqrDun.getSquareAt(new Point (0,2,0) /* 7 */).hasWall(Direction.SOUTH));
		// no temperature change
	}
	
	/**
	 * @note Test for implementation in AbstractDungeon (tested method not overwritten in SquareDungeon)
	 */
	@Test
	public void testDestroyObstacle(){
		Square sqr = sqrDun.getSquareAt(new Point (0, 0, 0) /* 1 */);
		// try to destroy an outer wall -> wont happen
		sqrDun.destroyObstacle(new Point(0,0,0), Direction.WEST);
		assertTrue(sqr.hasWall(Direction.WEST));
		
		// try to destroy inner wall
		sqrDun.destroyObstacle(new Point(0,0,0), Direction.NORTH);
		assertFalse(sqr.hasWall(Direction.NORTH));
		// temperature is equalized 
		assertEquals(sqrDun.getSquareAt(new Point(1,0,0) /* 4 */).getTemperature(), sqr.getTemperature(), 0.00001f);
		assertEquals(sqrDun.getSquareAt(new Point(1,1,0) /* 5 */).getTemperature(), sqr.getTemperature(), 0.00001f);
	}
	
	@Test
	public void testIterator(){
		// iterate of the squares of a SquareDungeon
		SquareIterator it = sqrDun.iterator();
		Square s = it.next();
		// First square is '1'.
		assertSame(s, sqrDun.getSquareAt(new Point(0,0,0)));
		while(it.hasNext()){
			Square next = it.next();
			// the iterated squares follow the correct natural order
			assertTrue(sqrDun.getPositionOfSquare(next).compareTo(sqrDun.getPositionOfSquare(s)) > 0 );
			s = next;
		}
	}
	
	/**
	 * @see This test is an extension of the test testComputeGroup() in SquareTest
	 */
	@Test
	public void testComputeGroupInDungeon() throws IllegalMaximumDimensionsException{
		// Create a ring that is intermittent at one point
		//  _ _ _
		// |  _  |
		// | |_| |
		// |_|_ _|
		SquareDungeon sqrDun = new Level(new Point(3,3,1));
		Square s1 = new Square();
		Square s2 = new Square();
		Square s3 = new Square();
		Square s4 = new Square();
		Square s5 = new Square();
		Square s6 = new Square();
		Square s7 = new Square();
		Square s8 = new Square();
		sqrDun.addAsSquareAt(s1, new Point(0,0,0));
		sqrDun.addAsSquareAt(s2, new Point(0,1,0), Direction.SOUTH);
		sqrDun.addAsSquareAt(s3, new Point(0,2,0), Direction.SOUTH);
		sqrDun.addAsSquareAt(s4, new Point(1,2,0), Direction.WEST);
		sqrDun.addAsSquareAt(s5, new Point(2,2,0), Direction.WEST);
		sqrDun.addAsSquareAt(s6, new Point(2,1,0), Direction.NORTH);
		sqrDun.addAsSquareAt(s7, new Point(2,0,0), Direction.NORTH);
		sqrDun.addAsSquareAt(s8, new Point(1,0,0), Direction.EAST);
		assertTrue(s1.hasWall(Direction.EAST)); // the breakpoint
		
		// The computed group fulfills the expected properties
		Collection<Square> group = s1.computeGroup();
		assertEquals(8, group.size());
		assertTrue(group.contains(s5));
		assertTrue(group.contains(s6));
		
		//we reduce the size of the group
		//sqrDun.buildWall(new Point(2,2,0), Direction.SOUTH);
		//  _ _ _
		// |  _ _|
		// | |_| |
		// |_|_ _|
		s5.buildWallAt(Direction.SOUTH);
		assertTrue(s5.hasWall(Direction.SOUTH));
		assertTrue(s6.hasWall(Direction.NORTH));
		
		group = s1.computeGroup();
		assertEquals(5, group.size());
		assertTrue(group.contains(s5));
		assertFalse(group.contains(s6));
	}
	
	
	
	/*************************************************************
	 * TERMINATION TESTS
	 **************************************************************/
	@Test
	public void testTerminate() throws IllegalDungeonAtPositionException, IllegalMaximumDimensionsException, IllegalDungeonException{
		//first add our square dungeon to a CompositeDungeon for illustrative purposes.
		CompositeDungeon cmpDun = new CompositeDungeon(new Point(3,3,1));
		cmpDun.addAsSubDungeonAt(sqrDun, new Point(0,0,0));
		assertSame(cmpDun, sqrDun.getParentDungeon());
		
		Collection<Square> oldSquares = cmpDun.getSquares();
		
		// Now do a termination
		sqrDun.terminate();
		assertTrue(sqrDun.isTerminated());
		// all squares are removed (but not terminated)
		assertEquals(0, sqrDun.getNbSquares());
		// this dungeon is decoupled from its parent
		assertFalse(cmpDun.hasAsSubDungeon(sqrDun));
		assertNull(sqrDun.getParentDungeon());
		// all squares have no more dungeon
		for(Square s:oldSquares){
			assertNull(s.getDungeon());
		}
	}
	
	
	/*************************************************************
	 * SUB DUNGEONS TESTS
	 **************************************************************/
	@Test
	public void testGetLevelsAndShafts(){
		// The levels and shafts of a SquareDungeon, is only the dungeon itself.
		assertTrue(sqrDun.getLevelsAndShafts().contains(sqrDun));
		assertEquals(1, sqrDun.getLevelsAndShafts().size());
	}
	
	@Test
	public void testGetDungeonContainingPosition(){
		// A square dungeon will only return itself if the given position is actually inside the maximum dimensions
		assertSame(sqrDun, sqrDun.getDungeonContainingPosition(new Point(0,0,0)));
		// Negative coordinates are a no-go
		assertNotSame(sqrDun, sqrDun.getDungeonContainingPosition(new Point(-1,0,0)));
		// Too large coordinates are a no-go
		assertNotSame(sqrDun, sqrDun.getDungeonContainingPosition(sqrDun.getMaximumDimensions()));
	}
	
}
