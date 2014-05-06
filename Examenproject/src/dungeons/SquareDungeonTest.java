package dungeons;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import dungeons.util.Direction;
import dungeons.util.Point;
import dungeons.util.SquareIterator;

/**
 * A unit test for SquareDungeon. All overwritten methods from Dungeon are tested here. Also some methods that were implemented in Dungeon
 * but not overwritten in SquareDungeon are tested here -> indicated with (*#*).
 * 
 * A test setup is defined for checking different functionalities concerning square management.
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 *
 */
public class SquareDungeonTest {

	private SquareDungeon sqrDun, trmDun;
	private Square square_1_0_0, trmSqr;
	
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
		sqrDun = new SquareDungeon(new Point(3,3,1));
		sqrDun.addAsSquareAt(new Square(), new Point(0,0,0)); // 1
		square_1_0_0 = new Square(); /* 2 */
		sqrDun.addAsSquareAt(square_1_0_0, new Point(1,0,0), Direction.WEST); // 2
		sqrDun.addAsSquareAt(new Square(), new Point(2,0,0)); // 3
		sqrDun.addAsSquareAt(new Square(), new Point(0,1,0)); // 4
		sqrDun.addAsSquareAt(new Square(), new Point(1,1,0)); // 5
		sqrDun.addAsSquareAt(new Square(), new Point(2,1,0), Direction.SOUTH); // 6
		sqrDun.addAsSquareAt(new Square(), new Point(0,2,0), Direction.SOUTH); // 7
		sqrDun.addAsSquareAt(new Square(), new Point(1,2,0), Direction.SOUTH, Direction.WEST); // 8
		sqrDun.buildDoor(new Point(1,0,0), Direction.EAST, true); // an open door between 2 and 3
		
		// we set some random temperatures in the rooms of the final dungeon
		sqrDun.getSquareAt(new Point(0,0,0) /*1*/).setTemperature(13);
		sqrDun.getSquareAt(new Point(0,1,0) /*4*/).setTemperature(-5);
		
		// some terminated structures
		trmSqr = new Square();
		trmSqr.terminate();
		trmDun = new SquareDungeon(new Point(3,3,0));
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
	
	// (*#*)
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
	
	// (*#*)
	@Test
	public void testCanHaveAsSquare(){
		Square legalSquare = new Square();
		
		assertTrue(sqrDun.canHaveAsSquare(legalSquare));
		assertFalse(sqrDun.canHaveAsSquare(null));
		assertFalse(sqrDun.canHaveAsSquare(trmSqr));
		assertTrue(trmDun.canHaveAsSquare(null));
		assertFalse(trmDun.canHaveAsSquare(legalSquare));
		
	}
	
	// (*#*)
	@Test
	public void testCanHaveAsSquareAt(){
		Square legalSquare = new Square();
		
		// existing squares can be replaced
		assertTrue(sqrDun.canHaveAsSquareAt(legalSquare, new Point(0,0,0)));
		// squares can not be added outside of the dungeon
		assertFalse(sqrDun.canHaveAsSquareAt(legalSquare, new Point(3,0,0)));
		// a square can be located a the position it is located at
		assertTrue(sqrDun.canHaveAsSquareAt(square_1_0_0, new Point(1,0,0)));
		
		// The following line should throw an assertion error (is -ea in set in VM)
		// A square can not be located at two different location in its root dungeon at the same time
		// sqrDun.canHaveAsSquareAt(square_1_0_0, new Point(2,0,0));
	}
	
	// (*#*)
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
		// We test the surroundings of square 2
		
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
	}
	
	// (*#*)
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
	
	// (*#*)
	@Test
	public void testBuildWall(){
		Square sqr = sqrDun.getSquareAt(new Point (0, 1, 0) /*4 */);
		
		sqrDun.buildWall(new Point (0, 1, 0) /*4 */, Direction.NORTH);

		assertTrue(sqr.hasWall(Direction.NORTH));
		// other square has a wall as well
		assertTrue(sqrDun.getSquareAt(new Point (0,2,0) /* 7 */).hasWall(Direction.SOUTH));
		// no temperature change
	}
	
	// (*#*)
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
		// First square is 1.
		assertSame(s, sqrDun.getSquareAt(new Point(0,0,0)));
		while(it.hasNext()){
			Square next = it.next();
			assertTrue(sqrDun.getPositionOfSquare(next).compareTo(sqrDun.getPositionOfSquare(s)) > 0 );
			s = next;
			// for a visual check, uncomment the following:
			// System.out.println(sqrDun.getPositionOfSquare(s).toString());
		}
		//last square is 6
		assertSame(s, sqrDun.getSquareAt(new Point(2,1,0)));
	}
	
	
	/*************************************************************
	 * TERMINATION TESTS
	 **************************************************************/
	@Test
	public void testTerminate() throws IllegalSubDungeonAtPositionException, IllegalMaximumDimensionsException{
		//first add out square dungeon to a CompositeDungeon for illustrative purposes.
		CompositeDungeon cmpDun = new CompositeDungeon(new Point(3,3,1));
		cmpDun.addAsSubDungeonAt(sqrDun, new Point(0,0,0));
		assertSame(cmpDun, sqrDun.getParentDungeon());
		
		// Now do a termination
		sqrDun.terminate();
		assertTrue(sqrDun.isTerminated());
		// all squares are removed (but not terminated)
		assertEquals(0, sqrDun.getNbSquares());
		// this dungeon is decoupled from its parent
		assertFalse(cmpDun.hasAsSubDungeon(sqrDun));
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
		// Negative coordinated are a no-go
		assertNotSame(sqrDun, sqrDun.getDungeonContainingPosition(new Point(-1,0,0)));
		// Too large coordinated are a no-go
		assertNotSame(sqrDun, sqrDun.getDungeonContainingPosition(sqrDun.getMaximumDimensions()));
	}
	
}
