package dungeons.dungeon;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dungeons.exception.IllegalDungeonException;
import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.square.Square;
import dungeons.util.Point;

/**
 * A unit test for MagicComposteDungeon. The main goal is to test whether the invariant concerning sub dungeons
 * is fulfilled at all times (a MagicCompositeDungeon must always have at least one magical sub dungeon)
 * 
 * @author Edward Van Sieleghm & Christof Vermeersch
 */
public class MagicCompositeDungeonTest {

	private MagicCompositeDungeon cmpDun_lvl0;
	private MagicLevel sqrDun_lvl1_1;
	private SquareDungeon sqrDun_lvl1_2;
	
	/**
	 * A dungeon hierarchy is set up, with 2 squares.
	 */
	@Before
	public void setUp() throws Exception {
		sqrDun_lvl1_1 = new MagicLevel(new Point(3,3,1));
		sqrDun_lvl1_2 = new Level(new Point(3,3,1));
		cmpDun_lvl0 = new MagicCompositeDungeon(new Point(3,3,3), sqrDun_lvl1_1, new Point(0,0,0));
		
		cmpDun_lvl0.addAsSubDungeonAt(sqrDun_lvl1_2, new Point(0,0,1));
		
		sqrDun_lvl1_1.addAsSquareAt(new Square(), new Point(0,0,0));// swappable from comDun_lvl0
		sqrDun_lvl1_2.addAsSquareAt(new Square(), new Point(0,0,0)); // not swappable from comDun_lvl0
	}

	@Test (expected = IllegalDungeonException.class)
	public void testMagicCompositeDungeon_IllegalConstruction() throws IllegalMaximumDimensionsException, IllegalDungeonException{
		// try to add a too large sub dungeon
		MagicLevel lvl = new MagicLevel(new Point(4,4,1));
		new MagicCompositeDungeon(new Point(3,3,3), lvl, new Point(0,0,0));
	}
	
	
	/*********************************************
	 * SUB DUNGEON MANAGEMENT
	 *********************************************/
	
	/**
	 * @see CompositeDungeonTest
	 */
	@Test
	public void testCanRemoveAsSubDungeon() {
		// we CAN remove the non-magic dungeon, we CANT remove the magic dungeon (from cmpDun_lvl0)
		assertTrue(cmpDun_lvl0.canRemoveAsSubDungeon(sqrDun_lvl1_2));
		assertFalse(cmpDun_lvl0.canRemoveAsSubDungeon(sqrDun_lvl1_1));
		
		// extra test: termination will not throw an exception
		cmpDun_lvl0.terminate();
		assertTrue(cmpDun_lvl0.isTerminated());
	}

	@Test
	public void testGetSubDungeonsThatCanParticipateInScramble() {
		// only sqrDun_lvl1_1 can participate (since it is a magic dungeon)
		List<Dungeon> canParticipate = cmpDun_lvl0.getSubDungeonsThatCanParticipateInScramble();
		assertEquals(1, canParticipate.size());
		assertTrue(canParticipate.contains(sqrDun_lvl1_1));
	}

	
	
	/*********************************************
	 * MAGICAL
	 *********************************************/
	
	@Test
	public void testGetSwappablePositions(){
		// only one square can be swapped
		List<Point> swappable = cmpDun_lvl0.getSwappablePositions();
		assertEquals(1, swappable.size());
		assertTrue(swappable.contains(new Point(0,0,0)));
	}
	
	/**
	 * See MagicDungeonHelperTest
	 */
	@Test
	public void testScramble() {
		// not implemented here
	}
	
}
