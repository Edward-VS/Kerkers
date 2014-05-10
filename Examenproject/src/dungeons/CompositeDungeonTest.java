package dungeons;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.exception.IllegalSubDungeonAtPositionException;
import dungeons.exception.IllegalSubDungeonException;
import dungeons.util.Direction;
import dungeons.util.Point;
import dungeons.util.SquareIterator;

public class CompositeDungeonTest {

	private CompositeDungeon cmpDun_lvl0, cmpDun_lvl1;
	private SquareDungeon sqrDun_lvl1, sqrDun_lvl2;
	private Square sqr_1, sqr_2, sqr_3;
	
	@Before
	public void setUp() throws Exception {
		
		// We build a test setup of sub-dungeons with a couple of squares in them.

		cmpDun_lvl0 = new CompositeDungeon(new Point(3,3,3));
		cmpDun_lvl1 = new CompositeDungeon(new Point(3,3,1));
		sqrDun_lvl1 = new SquareDungeon(new Point(3,3,1));
		sqrDun_lvl2 = new SquareDungeon(new Point(2,2,1));
		sqr_1 = new Square(63); // To make the test more dynamic, set a random temperature
		sqr_2 = new Square();
		sqr_3 = new Square();
		
		cmpDun_lvl0.addAsSubDungeonAt(cmpDun_lvl1, new Point(0,0,0));
		cmpDun_lvl0.addAsSubDungeonAt(sqrDun_lvl1, new Point(0,0,1));
		cmpDun_lvl1.addAsSubDungeonAt(sqrDun_lvl2, new Point(0,0,0));
		
		sqrDun_lvl2.addAsSquareAt(sqr_1, new Point(0,0,0));
		sqrDun_lvl2.addAsSquareAt(sqr_2, new Point(1,0,0), Direction.WEST);
		sqrDun_lvl1.addAsSquareAt(sqr_3, new Point(0,0,0), Direction.DOWN);
		// the 3 added square are connected.
		
		// NOTE: since we add the sub dungeons before adding any squares to them, adding squares to those subdungeons will
		// already take neighbors in account that are located in the root, so the parameter destroyObstacels, of addSquareAt will work.
		// If these action would be executed the other way around, that the added sub dungeons be have walls at their edges, so
		// sqr_1 and sqr_3 woul not be connected.
	}
	
	
	
	/*****************************************************************
	 * DIMENSIONS
	 *****************************************************************/
	
	@Test
	public void testOverlapsWithOtherSubDungeon() throws IllegalMaximumDimensionsException{
		// we check at various positions if a 1x1x1 sub dungeons overlaps with any other.
		CompositeDungeon c1 = new CompositeDungeon(new Point(1,1,1));
		assertTrue(cmpDun_lvl0.overlapsWithOtherSubDungeon(c1, new Point(1,1,1), new Point(0,0,0)));
		assertFalse(cmpDun_lvl0.overlapsWithOtherSubDungeon(c1, new Point(1,1,1), new Point(2,2,2)));
		assertTrue(cmpDun_lvl1.overlapsWithOtherSubDungeon(c1, new Point(1,1,1), new Point(0,0,0)));
		assertFalse(cmpDun_lvl1.overlapsWithOtherSubDungeon(c1, new Point(1,1,1), new Point(2,2,0)));
		
		assertTrue(cmpDun_lvl0.overlapsWithOtherSubDungeon(cmpDun_lvl1, new Point(3,3,2), new Point(0,0,0))); // (see next test)
	}
	
	@Test
	public void testCanHaveAsMaximumDimensions(){
		// NOTE this test supplements the test in DungeonTest
		
		assertFalse(cmpDun_lvl1.canHaveAsMaximumDimensions(new Point(3,3,2)));
	}
	
	
	/*****************************************************************
	 * SQUARE MANAGEMENT
	 *****************************************************************/
	
	@Test
	public void testGetSquares(){
		// the root of the hierarchy contains all squares, so also sqr_1
		assertTrue(cmpDun_lvl0.getSquares().contains(sqr_1)); 
		assertFalse(cmpDun_lvl0.getSquares().contains(new Square()));
		// this branch of the hierarchy also contains sqr_1
		assertTrue(cmpDun_lvl1.getSquares().contains(sqr_1));
		// this branch of the hierarchy does not contain any squares
		assertFalse(sqrDun_lvl1.getSquares().contains(sqr_1));
	}
	
	@Test
	public void testGetSquareAt(){
		// getting squares in a dungeon relative to its origin
		assertSame(sqr_1, cmpDun_lvl0.getSquareAt(new Point(0,0,0)));
		assertSame(sqr_3, cmpDun_lvl0.getSquareAt(new Point(0,0,1)));
	}
	
	@Test
	public void tetGetNbSquares(){
		assertEquals(3, cmpDun_lvl0.getNbSquares()); 
		assertEquals(1, sqrDun_lvl1.getNbSquares());
		// note that '3' in cmpDun_lvl0 also include the '1' from sqrDun_lvl1
	}
	
	@Test
	public void testGetPositionOfSquare(){
		assertEquals(new Point(0,0,0), cmpDun_lvl0.getPositionOfSquare(sqr_1));
		assertEquals(new Point(0,0,1), cmpDun_lvl0.getPositionOfSquare(sqr_3));
		assertEquals(new Point(0,0,0), sqrDun_lvl1.getPositionOfSquare(sqr_3));
		// two last result are valid because the returned position is relative to the
		// origin of the dungeon the square was searched in.
	}
	
	@Test
	public void testCanHaveAsSquareAt(){
		// NOTE: The implementation of this method is overwritten from Dungeon.
		// For more extensive tests, look in SquareDungeonTest
		
		// A square can only be located in a conpositeDungeon where there is a sub-dungeon that can contain it.
		Square s = new Square();
		assertTrue(cmpDun_lvl0.canHaveAsSquareAt(s, new Point(0,0,0)));
		assertTrue(cmpDun_lvl0.canHaveAsSquareAt(s, new Point(2,2,1)));
		assertFalse(cmpDun_lvl0.canHaveAsSquareAt(s, new Point(2,2,0)));
		
		// The following line should throw an assertion error (is -ea in set in VM)
		// A square can not be located at two different location in its root dungeon at the same time
		// cmpDun_lvl0.canHaveAsSquareAt(sqr_3, new Point(0,0,0));
		// The following line will run properly tough
		assertTrue(cmpDun_lvl0.canHaveAsSquareAt(sqr_3, new Point(0,0,1)));
	}
	
	@Test
	public void testAddAsSquareAt(){
		Square s = new Square();
		// test adding a square from a composite dungeon at an invalid position
		cmpDun_lvl0.addAsSquareAt(s, new Point(2,2,0));
		assertFalse(cmpDun_lvl0.hasSquareAt(new Point(2,2,0)));
		
		// test adding a square from a composite dungeon at a valid position
		cmpDun_lvl0.addAsSquareAt(s, new Point(0,1,1)); // using add of composite dungeon
		cmpDun_lvl0.hasSquareAt(new Point(0,1,1));
		sqrDun_lvl1.hasSquareAt(new Point(0,1,0)); // where the square is actually added
		
		// test condition that should have been met after setUp()
		// note that those squares where added using the add method of a SquareDugneon instead of CompositeDungeon (effect should be the same...)
		
		// Neighbor checks
		assertSame(sqr_1, sqr_2.getNeighborAt(Direction.WEST));
		assertSame(sqr_1, sqr_3.getNeighborAt(Direction.DOWN));
		assertSame(s, sqr_3.getNeighborAt(Direction.NORTH));
		assertSame(null, sqr_1.getNeighborAt(Direction.SOUTH)); // outside of the dungeon
		
		// Neighbor bidirectionality
		assertSame(sqr_2, sqr_1.getNeighborAt(Direction.EAST));
		assertSame(sqr_3, sqr_1.getNeighborAt(Direction.UP));
		
		// Obstacles checks
		assertFalse(sqr_1.hasWall(Direction.EAST));			
		assertFalse(sqr_1.hasWall(Direction.UP));
		assertTrue(sqr_1.hasWall(Direction.SOUTH)); // at the edge of the dungeon
		assertTrue(sqr_3.hasWall(Direction.NORTH)); // Towards the newly added square
				
		// Obstacles bidirectionality
		assertSame(sqr_3.getObstacleAt(Direction.NORTH), s.getObstacleAt(Direction.SOUTH));
		
		// Temperature check (Note that the door between 2 and 3 is open)
		assertEquals(sqr_1.getTemperature(), sqr_2.getTemperature(), 0.00001f);
		assertEquals(sqr_1.getTemperature(), sqr_3.getTemperature(), 0.00001f);
		assertNotEquals(sqr_3.getTemperature(), s.getTemperature(), 0.00001f);
		
	}
	
	@Test
	public void testRemoveAsSquareAt(){
		// test removing a square where there is none (nothing happens)
		cmpDun_lvl0.removeAsSquareAt(new Point(1,1,0));
		
		// test remove a square where there actually is one
		cmpDun_lvl0.removeAsSquareAt(new Point(0,0,0));
		
		
		// the square does not belong to any dungeon anymore
		assertFalse(cmpDun_lvl0.hasSquareAt(new Point(0,0,0)));
		assertFalse(cmpDun_lvl1.hasSquareAt(new Point(0,0,0)));

		// walls are build at the outer edges of the dungeon
		assertTrue(sqr_2.hasWall(Direction.WEST));
		assertTrue(sqr_3.hasWall(Direction.DOWN));
		
		// the square does not have any neighbors anymore, and is surrounded with walls
		for(Direction d: Direction.values()){
			assertTrue(sqr_1.hasWall(d));
			assertNull(sqr_1.getNeighborAt(d));
		}
		
		// neighboring squares don't have the square as its  neighbor anymore
		assertNull(sqr_2.getNeighborAt(Direction.WEST));
		assertNull(sqr_3.getNeighborAt(Direction.DOWN));
	}
	
	@Test 
	public void testIterator() {
		//we add some squares, so iteration is tested more extensively
		cmpDun_lvl0.addAsSquareAt(new Square(), new Point(0,1,0));
		cmpDun_lvl0.addAsSquareAt(new Square(), new Point(0,1,1));
		cmpDun_lvl0.addAsSquareAt(new Square(), new Point(1,2,1));
		Square lastInIteration = new Square(); // The left most, and back most square
		cmpDun_lvl0.addAsSquareAt(lastInIteration, new Point(2,1,1));
		
		int i = 1;
		SquareIterator it = cmpDun_lvl0.iterator();
		Square s = it.next();
		// the first square in iteration
		assertSame(s, cmpDun_lvl0.getSquareAt(new Point(0,0,0)));
		while(it.hasNext()){
			i++;
			Square next = it.next();
			// for a visual check, uncomment the following:
			// System.out.println(cmpDun_lvl0.getPositionOfSquare(next).toString());
			assertTrue(cmpDun_lvl0.getPositionOfSquare(next).compareTo(cmpDun_lvl0.getPositionOfSquare(s)) > 0 );
			s = next;
		}
		// the last square in iteration;
		assertSame(s, lastInIteration);
		// the same number of squares was iterated as there are are in comDun_lvl0 
		assertEquals(cmpDun_lvl0.getNbSquares(), i);
	}
	
	@Test
	public void collapse() throws IllegalMaximumDimensionsException, IllegalSubDungeonAtPositionException, IllegalSubDungeonException{
		// This method requires a more extensive setup than the current
		CompositeDungeon main = new CompositeDungeon(new Point(1,1,3));
		
		// 3 square dungeons on top of each other, with at the top a rock, and below normal squares.
		SquareDungeon topDun = new SquareDungeon(new Point(1,1,1));
		Square topSqr = new Rock();
		topDun.addAsSquareAt(topSqr, new Point(0,0,0));
		main.addAsSubDungeonAt(topDun, new Point(0,0,2));
		
		SquareDungeon midDun = new SquareDungeon(new Point(1,1,1));
		Square midSqr = new Square();
		midDun.addAsSquareAt(midSqr, new Point(0,0,0));
		main.addAsSubDungeonAt(midDun, new Point(0,0,1));
		
		SquareDungeon bottomDun = new SquareDungeon(new Point(1,1,1));
		Square bottomSqr = new Square();
		bottomDun.addAsSquareAt(bottomSqr, new Point(0,0,0));
		main.addAsSubDungeonAt(bottomDun, new Point(0,0,0));
		
		// The top square has a 100% chance to drop
		assertTrue(midSqr.canCollapse());
		assertSame(topSqr, main.getSquareAt(new Point(0,0,2)));
		//this can trigger a cascade-> hard to test
		main.collapse(midSqr);
		
		if(main.getSquareAt(new Point(0,0,1)) == topSqr){
			// no cascade
			assertSame(topSqr, main.getSquareAt(new Point(0,0,1)));
		}else{
			// cascade
			assertFalse(main.hasSquareAt(new Point(0,0,1)));
			assertSame(topSqr, main.getSquareAt(new Point(0,0,0)));
		}
		assertFalse(main.hasSquareAt(new Point(0,0,2))); // 100% chance that the top square has fallen down
	}
	
	
	
	/*****************************************************************
	 * TERMINATION
	 *****************************************************************/
	
	@Test
	public void testTerminate(){
		cmpDun_lvl1.terminate();
		assertEquals(0, cmpDun_lvl1.getNbSubDungeons());
		assertFalse(cmpDun_lvl0.hasAsSubDungeon(cmpDun_lvl1));
		assertTrue(cmpDun_lvl1.isTerminated());
		// postcondition of removaAsSubDungeon() are also valid, but are not tested here.
	}

	
	
	
	/*****************************************************************
	 * SUB DUNGEONS
	 *****************************************************************/
	
	//NOTE: the following 5 tests are trivial, but give an indication if internal storage works as expected.
	@Test
	public void testGetSubDungeons(){
		assertTrue(cmpDun_lvl0.getSubDungeons().contains(cmpDun_lvl1));
		//only direct sub-dungeons are contained (<-> getSquares() && getLevelsAndShafts())
		assertFalse(cmpDun_lvl0.getSubDungeons().contains(sqrDun_lvl2));
		assertTrue(cmpDun_lvl1.getSubDungeons().contains(sqrDun_lvl2));
	}
	
	@Test
	public void testGetSubDungeonAt(){
		assertSame(cmpDun_lvl1, cmpDun_lvl0.getSubDungeonAt(new Point(0,0,0)));
	}
	
	@Test
	public void testGetNbSubDungeons(){
		assertEquals(2, cmpDun_lvl0.getNbSubDungeons());
		assertEquals(1, cmpDun_lvl1.getNbSubDungeons());
	}
	
	@Test
	public void testGetPositionOfSubDungeon(){
		assertEquals(new Point(0,0,1), cmpDun_lvl0.getPositionOfSubDungeon(sqrDun_lvl1));
		assertEquals(new Point(0,0,0), cmpDun_lvl1.getPositionOfSubDungeon(sqrDun_lvl2));
		assertNull(cmpDun_lvl0.getPositionOfSubDungeon(sqrDun_lvl2));
	}
	
	@Test
	public void testHasAsSubDungeon(){
		assertTrue(cmpDun_lvl0.hasAsSubDungeon(sqrDun_lvl1));
		assertFalse(cmpDun_lvl0.hasAsSubDungeon(sqrDun_lvl2));
	}
	
	//NOTE: non-trivial tests start here
	@Test
	public void testCanHaveAsSubDungeon() throws IllegalMaximumDimensionsException{
		SquareDungeon trmSqrDun = new SquareDungeon(new Point(3,3,1));
		trmSqrDun.terminate(); // trivial termination...
		SquareDungeon validSqrDun = new SquareDungeon(new Point(3,3,1));

		CompositeDungeon trmCmpDun = new CompositeDungeon(new Point(3,3,3));
		trmCmpDun.terminate(); // trivial termination...
		CompositeDungeon validCmpDun = new CompositeDungeon(new Point(3,3,3));
		
		// test concerning termination and effectiveness
		assertTrue(validCmpDun.canHaveAsSubDungeon(validSqrDun));
		assertFalse(validCmpDun.canHaveAsSubDungeon(trmSqrDun));
		assertFalse(validCmpDun.canHaveAsSubDungeon(null));
		assertTrue(trmCmpDun.canHaveAsSubDungeon(null));
		assertFalse(trmCmpDun.canHaveAsSubDungeon(validSqrDun));
		assertFalse(trmCmpDun.canHaveAsSubDungeon(trmSqrDun));
		
		//	test concerning indirect loops, and self referencing
		assertFalse(validCmpDun.canHaveAsSubDungeon(validCmpDun));
		assertFalse(cmpDun_lvl1.canHaveAsSubDungeon(cmpDun_lvl0));
	}
	
	@Test
	public void testCanHaveAsSubDungeonAt() throws IllegalMaximumDimensionsException{
		// Also see previous test
		
		SquareDungeon validSqrDun = new SquareDungeon(new Point(3,3,1));
		// perfect fit
		assertTrue(cmpDun_lvl0.canHaveAsSubDungeonAt(validSqrDun, new Point(0,0,2)));
		// overlap with sqrDun_lvl1
		assertFalse(cmpDun_lvl0.canHaveAsSubDungeonAt(validSqrDun, new Point(0,0,1)));
		// no fit, because max dimensions of cmpDun_lvl0 are to small in the x direction
		assertFalse(cmpDun_lvl0.canHaveAsSubDungeonAt(validSqrDun, new Point(1,0,2)));
	}
	
	@Test
	public void testHasProperSubDungeons() throws IllegalMaximumDimensionsException{
		// A method that should ALWAYS return true...
		assertTrue(cmpDun_lvl0.hasProperSubDungeons());
		assertTrue(cmpDun_lvl1.hasProperSubDungeons());
		
		CompositeDungeon emptyCmpDun = new CompositeDungeon(new Point(3,3,3));
		assertTrue(emptyCmpDun.hasProperSubDungeons());
	}
	
	
	@Test (expected = IllegalSubDungeonException.class)
	public void testAddAsSubDungeonAt_Illegal_SubDungeonWithParent() throws IllegalSubDungeonAtPositionException, IllegalSubDungeonException{
		//add a sub dungeon that already has a dungeon as its sub dungeon
		cmpDun_lvl0.addAsSubDungeonAt(sqrDun_lvl2, new Point(0,0,2));
	}
	
	@Test (expected = IllegalSubDungeonAtPositionException.class)
	public void testAddAsSubDungeonAt_Illegal_Position() throws IllegalSubDungeonAtPositionException, IllegalMaximumDimensionsException, IllegalSubDungeonException{
		// add a sub dungeon that would overlap with another sub dungeon
		SquareDungeon validSqrDun = new SquareDungeon(new Point(3,3,1));
		cmpDun_lvl0.addAsSubDungeonAt(validSqrDun, new Point(0,0,1));
	}
	
	@Test (expected = IllegalSubDungeonAtPositionException.class)
	public void testAddAsSubDungeonAt_Illegal_SubDungeonLoop() throws IllegalSubDungeonAtPositionException, IllegalMaximumDimensionsException, IllegalSubDungeonException{
		// add a sub dungeon that is your own parent (sizes of both should be the same, for them not to throw the
		// exception for the wrong reason)
		CompositeDungeon cmp1 = new CompositeDungeon(new Point(3,3,3));
		CompositeDungeon cmp2 = new CompositeDungeon(new Point(3,3,3));
		
		cmp1.addAsSubDungeonAt(cmp2, new Point(0,0,0));
		cmp2.addAsSubDungeonAt(cmp1, new Point(0,0,0)); // recursion
	}

	@Test
	public void testAddAsSubDungeonAt_Legal() throws IllegalMaximumDimensionsException, IllegalSubDungeonAtPositionException, IllegalSubDungeonException{
		SquareDungeon sqrDun_new = new SquareDungeon(new Point(3,3,1));
		Square sqr_new = new Square();
		sqrDun_new.addAsSquareAt(sqr_new, new Point(0,0,0));
		
		cmpDun_lvl0.addAsSubDungeonAt(sqrDun_new, new Point(0,0,2));
		assertTrue(cmpDun_lvl0.hasAsSubDungeon(sqrDun_new));
		
		// neighbors are set properly
		assertSame(sqr_3, sqr_new.getNeighborAt(Direction.DOWN));
		assertSame(sqr_new, sqr_3.getNeighborAt(Direction.UP));
		
		// obstacles are set properly
		assertTrue(sqr_new.hasWall(Direction.DOWN));
		assertSame(sqr_new.getObstacleAt(Direction.DOWN), sqr_3.getObstacleAt(Direction.UP));
		
		//note: temperature is not updated
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testRemoveAsSubDungeon_IllegalSubDungeon(){
		cmpDun_lvl0.removeAsSubDungeon(sqrDun_lvl2);
	}
	
	@Test
	public void testRemoveAsSubDungeon_Legal(){
		// remove the sub-dungeon that contained sqr_3
		assertTrue(cmpDun_lvl0.hasAsSubDungeon(sqrDun_lvl1));
		cmpDun_lvl0.removeAsSubDungeon(sqrDun_lvl1);
		assertFalse(cmpDun_lvl0.hasAsSubDungeon(sqrDun_lvl1));
		
		// neighbors are removed.
		assertNull(sqr_3.getNeighborAt(Direction.DOWN));
		assertNull(sqr_1.getNeighborAt(Direction.UP));
		
		// obstacles are properly set
		assertTrue(sqr_1.hasWall(Direction.UP));
		assertTrue(sqr_3.hasWall(Direction.DOWN));
	}
	
	@Test
	public void testGetLevelsAndShafts(){
		assertTrue(cmpDun_lvl0.getLevelsAndShafts().contains(sqrDun_lvl1));
		assertTrue(cmpDun_lvl0.getLevelsAndShafts().contains(sqrDun_lvl2));
	}
	
	@Test
	public void testGetDungeonContainingPosition(){
		// only squareDungeons can be returned here + not all points relative to the origin of cmpDun_lvl0
		// belong to a squareDungeon
		assertSame(sqrDun_lvl2, cmpDun_lvl0.getDungeonContainingPosition(new Point(0,0,0)));
		assertSame(sqrDun_lvl1, cmpDun_lvl0.getDungeonContainingPosition(new Point(0,0,1)));
		assertSame(sqrDun_lvl1, cmpDun_lvl0.getDungeonContainingPosition(new Point(2,2,1)));
		assertNull(cmpDun_lvl0.getDungeonContainingPosition(new Point(0,0,2))); // a height (z) where there is no square dungeon
	}
	
	
	
	
	/*****************************************************************
	 * PARENT DUNGEONS - some of these test are valid for ALL dungeons (not only for CompositeDungeon())
	 *****************************************************************/
	@Test
	public void testGetParentDungeon(){
		assertSame(cmpDun_lvl0, cmpDun_lvl1.getParentDungeon());
		assertSame(cmpDun_lvl0, sqrDun_lvl1.getParentDungeon());
		assertSame(cmpDun_lvl1, sqrDun_lvl2.getParentDungeon());
	}
	
	@Test
	public void testSetParentDungeon(){
		// enable assertion (-ea) to check if preconditions are fulfilled (-> all unit tests pass the assertions...)
	}
	
	@Test
	public void testCanHaveAsParentDungeon(){
		// trivial tests:
		assertTrue(cmpDun_lvl0.canHaveAsParentDungeon(cmpDun_lvl0.getParentDungeon()));
		assertTrue(cmpDun_lvl1.canHaveAsParentDungeon(cmpDun_lvl1.getParentDungeon()));
		
		//non trivial tests:
		// -All dungeons can have null as their parent (since canHaveAsParent() does not check the validity of the
		//	bidirectional association)
		assertTrue(cmpDun_lvl1.canHaveAsParentDungeon(null));
		// -Also see: testCanHaveAsSubDungeon()
	}
	
	@Test
	public void testHasProperParentDungeon(){
		assertTrue(cmpDun_lvl0.hasProperParentDungeon());
		assertTrue(cmpDun_lvl1.hasProperParentDungeon());
		assertTrue(sqrDun_lvl2.hasProperParentDungeon());
	}
	
	@Test
	public void testGetRootDungeon(){
		// dungeon that is the root
		assertSame(cmpDun_lvl0, cmpDun_lvl0.getRootDungeon());
		//dungeons that are not the root
		assertSame(cmpDun_lvl0, cmpDun_lvl1.getRootDungeon());
		assertSame(cmpDun_lvl0, sqrDun_lvl2.getRootDungeon());
	}
	
	@Test
	public void testEqualsOrIsDirectOrIndirectParentOf(){
		assertTrue(cmpDun_lvl0.equalsOrIsDirectOrIndirectParentOf(cmpDun_lvl0)); // trivial
		assertTrue(cmpDun_lvl0.equalsOrIsDirectOrIndirectParentOf(cmpDun_lvl1)); // direct parent
		assertTrue(cmpDun_lvl0.equalsOrIsDirectOrIndirectParentOf(sqrDun_lvl2)); // indirect parent (root)
	}
	
	@Test
	public void testGetPositionInRoot(){
		// test test setup does not really allow proper testing of this method
		assertEquals(new Point(0,0,0), cmpDun_lvl0.getPositionInRoot()); // the root itself
		assertEquals(new Point(0,0,0), cmpDun_lvl1.getPositionInRoot());
		assertEquals(new Point(0,0,1), sqrDun_lvl1.getPositionInRoot());
		assertEquals(new Point(0,0,0), sqrDun_lvl2.getPositionInRoot());
	}
}
