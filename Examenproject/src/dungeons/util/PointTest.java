package dungeons.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PointTest {

	private Point first, second, third, fourth, fourthDuplicate; // for comparison
	private Point point_5_6_7; // for other checks
	
	@Before
	public void setUp() throws Exception {
		first = new Point();
		second = new Point(0,1,0); // 'one to the back'
		third = new Point(1,1,0); // 'one the the back and one to the right'
		fourth = new Point(1,1,1); // 'one the the back, one to the right and one up'
		fourthDuplicate = new Point(1,1,1);
		
		point_5_6_7 = new Point(5,6,7);
	}

	@Test
	public void testPointConstructor(){
		Point p = new Point(1, 2, 3);
		assertEquals(1, p.getX());
		assertEquals(2, p.getY());
		assertEquals(3, p.getZ());
	}

	
	/******************************************
	 * MANIPULATIONS & COMPARISON
	 ******************************************/
	
	@Test
	public void testIsEqualOrSmallerThanValue(){
		// systematic check against a range of values
		for(int i = -5; i < 10; i++){
			if(i>=7)
				assertTrue(point_5_6_7.isEqualOrSmallerThanValue(i));
			else
				assertFalse(point_5_6_7.isEqualOrSmallerThanValue(i));
		}
	}
	
	@Test
	public void testIsEqualOrBiggerThanValue(){
		// systematic check against a range of values
		for(int i = -5; i < 10; i++){
			if(i<=5)
				assertTrue(point_5_6_7.isEqualOrBiggerThanValue(i));
			else
				assertFalse(point_5_6_7.isEqualOrBiggerThanValue(i));
		}
	}
	
	@Test
	public void testIsEqualOrBiggerThanPoint(){
		// boundary check
		Point equal = new Point(5, 6, 7);
		Point bigger = new Point(5, 6, 8);
		Point smaller = new Point(4, 6, 7);
		
		assertTrue(point_5_6_7.isEqualOrBiggerThanPoint(equal));
		assertTrue(point_5_6_7.isEqualOrBiggerThanPoint(smaller));
		assertFalse(point_5_6_7.isEqualOrBiggerThanPoint(bigger));
	}
	
	@Test
	public void testIsEqualOrSmallerThanPoint(){
		// boundary check
		Point equal = new Point(5, 6, 7);
		Point bigger = new Point(5, 6, 8);
		Point smaller = new Point(4, 6, 7);
		
		assertTrue(point_5_6_7.isEqualOrSmallerThanPoint(equal));
		assertFalse(point_5_6_7.isEqualOrSmallerThanPoint(smaller));
		assertTrue(point_5_6_7.isEqualOrSmallerThanPoint(bigger));
	}
	
	@Test
	public void testOveralp(){
		Point s1 = new Point(0,0,0);
		Point e1 = new Point(2,2,2);
		
		// check a (2,2,2) cube that sits at the origin against 4 other cubes, that do, or do not overlap
		Point s2 = new Point(1,1,1);
		Point e2 = new Point(3,3,3);
		assertTrue(Point.overlap(s1, e1, s2, e2));
		
		s2 = new Point(1,1,0);
		e2 = new Point(2,2,3);
		assertTrue(Point.overlap(s1, e1, s2, e2));
		
		s2 = new Point(2,2,2);
		e2 = new Point(4,4,4);
		assertFalse(Point.overlap(s1, e1, s2, e2));
		
		s2 = new Point(0,0,0);
		e2 = new Point(1,1,1);
		assertTrue(Point.overlap(s1, e1, s2, e2));
		
	}
	
	@Test
	public void testSize(){
		assertEquals(5*6*7, point_5_6_7.size());
	}
	
	@Test
	public void testAdd(){
		// Sample test
		assertEquals(new Point(3,2,1), (new Point(1,2,0)).add(new Point(2,0,1)));
	}
	
	@Test
	public void testSubtract(){
		// Sample test
		assertEquals(new Point(3,2,1), (new Point(5,2,3)).subtract(new Point(2,0,2)));
	}
	
	@Test
	public void testCompareTo(){
		assertTrue(first.compareTo(second) < 0);
		assertTrue(first.compareTo(third) < 0);
		assertTrue(first.compareTo(fourth) < 0);
		assertTrue(second.compareTo(first) > 0);
		assertTrue(third.compareTo(second) > 0);
		assertTrue(fourth.compareTo(third) > 0);
		assertTrue(fourth.compareTo(fourth) == 0);
	}
	
	@Test
	public void testEquals(){
		assertTrue(fourth.equals(fourthDuplicate));
		assertTrue(first.equals(first));
		assertFalse(first.equals(fourth));
		assertFalse(first.equals(new Object()));
	}

}
