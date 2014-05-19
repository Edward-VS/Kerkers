package dungeons.dungeon;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import dungeons.exception.IllegalDungeonException;
import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.square.Rock;
import dungeons.square.Square;
import dungeons.util.Point;

/**
 * A unit test for MagicDungeonHelper. The main goal is to test whether the scramble method works as specified.
 * 
 * @author Edward Van Sieleghm & Christof Vermeersch
 */
public class MagicDungeonHelperTest {

	private MagicCompositeDungeon dun;
	private MagicDungeonHelper helper;
	
	/**
	 * We set up a helper class for a magic composite dungeon that has two levels. The upper level contains some rocks,
	 * and the level below that only contains squares.
	 */
	@Before
	public void setUp() throws Exception {
		MagicLevel lvl1 = new MagicLevel(new Point(3,3,1));
		MagicLevel lvl2 = new MagicLevel(new Point(3,3,1));
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				lvl1.addAsSquareAt(new Square(), new Point(i, j, 0));
			}
		}
		lvl2.addAsSquareAt(new Rock(), new Point(1,1,0));
		lvl2.addAsSquareAt(new Rock(), new Point(2,2,0));
		dun = new MagicCompositeDungeon(new Point(3,3,2), lvl1, new Point(0,0,0));
		dun.addAsSubDungeonAt(lvl2, new Point(0,0,1));
		helper = new MagicDungeonHelper(dun);
	}

	/********************************************
	 * MAGIC DUNGEON MANAGEMENT
	 ********************************************/
	
	@Test
	public void testGetMagicDungeon(){
		assertSame(dun, helper.getMagicDungeon());
	}
	
	/**
	 * @note This tests an instance method, but it could just as well have been a static method.
	 */
	@Test
	public void testCanHaveAsMagicDungeon() throws IllegalMaximumDimensionsException{
		// trivial
		assertTrue(helper.canHaveAsMagicDungeon(dun));
		// A helper must always have an effective dungeon
		assertFalse(helper.canHaveAsMagicDungeon(null));
		// A helper must always have a non terminated dungeon
		MagicLevel trm = new MagicLevel();
		trm.terminate();
		assertFalse(helper.canHaveAsMagicDungeon(trm));
	}
	
	@Test 
	public void testSetMagicDungeon_Legal() throws IllegalDungeonException, IllegalMaximumDimensionsException{
		helper.setMagicDungeon(new MagicLevel());
	}
	
	@Test (expected = IllegalDungeonException.class)
	public void testSetMagicDungeon_Illegal() throws IllegalDungeonException{
		helper.setMagicDungeon(null);
	}
	
	/********************************************
	 * SCRAMBLE
	 ********************************************/
	
	/**
	 * @note For a more predictable results the permutation factor is set to 1, and the other factors to 0.
	 * Also because the current test-setup has an odd amount of squares, on square is removed bofore scrambling
	 * to ensure that all squares participate in the permutation.
	 */
	@Test
	public void testScramble_Permutation() {
		// make sure that only permutation will take place
		MagicDungeonHelper.setPermutationFactor(1f);
		MagicDungeonHelper.setFillFactor(0f);
		MagicDungeonHelper.setCollapseFactor(0f);
		dun.removeAsSquareAt(new Point(0,0,0));

		// To ensure a predictable result, we execute more than once
		for(int j = 0; j < 100; j++){
			// we get the current square-setup in an ordered manner.
			ArrayList<Square> before = new ArrayList<Square>();
			for(Square s: dun){ // uses SquareIterator
				before.add(s);
			}
			assertEquals(10, dun.getNbSquares());
			
			helper.scramble();
			
			// we get the square-setup in an ordered manner, after scramble is called.
			ArrayList<Square> after = new ArrayList<Square>();
			for(Square s: dun){ // uses SquareIterator
				after.add(s);
			}
			assertEquals(10, dun.getNbSquares());
			
			// All squares have swapped position
			for(int i = 0; i < dun.getNbSquares(); i++){
				assertNotSame(before.get(i), after.get(i));
				// squares with the same index in 'before' and 'after' have the same position in the dungeon
			}
			
			// A sample of empty position is checked to make sure they are still empty
			assertNull(dun.getSquareAt(new Point(0,0,0))); // the square we removed before this test
			assertNull(dun.getSquareAt(new Point(0,0,1)));
			assertNull(dun.getSquareAt(new Point(2,0,1)));
		}
	}
	
	/**
	 * @note This method is harder to test, since it is not certain that the fill-factor will be reached.
	 */
	@Test
	public void testScramble_Fill() {
		// make sure that only filling wil ltake place
		MagicDungeonHelper.setPermutationFactor(0f);
		MagicDungeonHelper.setFillFactor(1f);
		MagicDungeonHelper.setCollapseFactor(0f);
		
		int prev = dun.getNbSquares();
		
		helper.scramble();
		
		// there are currently more squares than before.
		assertTrue(dun.getNbSquares() > prev);
	}
	
	@Test
	public void testScramble_Collapse() {
		// make sure that only collapseing takes place
		MagicDungeonHelper.setPermutationFactor(0f);
		MagicDungeonHelper.setFillFactor(0f);
		MagicDungeonHelper.setCollapseFactor(1f);

		assertTrue(dun.getSquareAt(new Point(1,1,0)).canCollapse());
		assertTrue(dun.getSquareAt(new Point(2,2,0)).canCollapse());
		
		helper.scramble();
		
		// all collapseable squares are collapsed
		assertNull(dun.getSquareAt(new Point(1,1,1)));
		assertNull(dun.getSquareAt(new Point(2,2,1)));
		assertTrue(dun.getSquareAt(new Point(1,1,0)).canMakeCollapse());
		assertTrue(dun.getSquareAt(new Point(2,2,0)).canMakeCollapse());
	}
	
	@Test
	public void canParticipateInScramble() throws IllegalMaximumDimensionsException{
		// Only MagicDungeons can participate
		assertTrue(MagicDungeonHelper.canParticipateInScramble(new MagicShaft()));
		assertFalse(MagicDungeonHelper.canParticipateInScramble(new Shaft()));
	}
	
	@Test
	public void testIsValidFactor(){
		// We test the entire range of values. Only values between 0 and 1 (inclusive) are allowed.
		for(float f = -1.0f; f < 2.0; f += 0.1){
			if(f >= 0.0f && f <= 1.0f)
				assertTrue(MagicDungeonHelper.isValidFactor(f));
			else
				assertFalse(MagicDungeonHelper.isValidFactor(f));
		}
	}
	
	/**
	 * @note This tests multiple methods concerning factors to do with scrambling.
	 */
	@Test
	public void testSetFactors(){
		MagicDungeonHelper.setPermutationFactor(0.91f);
		MagicDungeonHelper.setFillFactor(0.92f);
		MagicDungeonHelper.setCollapseFactor(0.93f);
		assertEquals(0.91f, MagicDungeonHelper.getPermutationFactor(), 0.0000001f);
		assertEquals(0.92f, MagicDungeonHelper.getFillFactor(), 0.0000001f);
		assertEquals(0.93f, MagicDungeonHelper.getCollapseFactor(), 0.0000001f);
	}

}
