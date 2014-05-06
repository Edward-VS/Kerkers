package dungeons.util;

import static org.junit.Assert.*;

import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

public class PointTest {

	private Point first, second, third, fourth, fourthDuplicate; // for comparison
	private Point point_500_600_700; // for size checks
	
	@Before
	public void setUp() throws Exception {
		first = new Point();
		second = new Point(0,1,0); // 'one to the back'
		third = new Point(1,1,0); // 'one the the back and one to the right'
		fourth = new Point(1,1,1); // 'one the the back, one to the right and one up'
		fourthDuplicate = new Point(1,1,1);
		
		point_500_600_700 = new Point(500,600,700);
	}

	@Test
	public void testAdd(){
		assertEquals(new Point(3,2,1), (new Point(1,2,0)).add(new Point(2,0,1)));
	}
	
	@Test
	public void testSubtract(){
		assertEquals(new Point(3,2,1), (new Point(5,2,3)).subtract(new Point(2,0,2)));
	}
	
	@Test
	public void testPointConstructor(){
		Point p = new Point(1, 2, 3);
		assertEquals(1, p.getX());
		assertEquals(2, p.getY());
		assertEquals(3, p.getZ());
	}
	
	@Test
	public void testEquals(){
		assertTrue(fourth.equals(fourthDuplicate));
		assertTrue(first.equals(first));
		assertFalse(first.equals(fourth));
		assertFalse(first.equals(new Object()));
	}
	
	@Test
	public void testIsEqualOrSmallerThanValue(){
		for(int i = -500; i < 1000; i+=50){
			if(i>=700)
				assertTrue(point_500_600_700.isEqualOrSmallerThanValue(i));
			else
				assertFalse(point_500_600_700.isEqualOrSmallerThanValue(i));
		}
	}
	
	@Test
	public void testIsEqualOrBiggerThanValue(){
		for(int i = -500; i < 1000; i+=50){
			if(i<=500)
				assertTrue(point_500_600_700.isEqualOrBiggerThanValue(i));
			else
				assertFalse(point_500_600_700.isEqualOrBiggerThanValue(i));
		}
	}
	
	@Test
	public void testBetween(){
		assertTrue((new Point(1,2,3)).between(1, 4));
		assertFalse((new Point(1,2,3)).between(1, 3));
	}
	
	@Test
	public void testIsEqualOrBiggerThanPoint(){
		Point equal = new Point(500, 600, 700);
		Point bigger = new Point(500, 600, 800);
		Point smaller = new Point(400, 600, 700);
		
		assertTrue(point_500_600_700.isEqualOrBiggerThanPoint(equal));
		assertTrue(point_500_600_700.isEqualOrBiggerThanPoint(smaller));
		assertFalse(point_500_600_700.isEqualOrBiggerThanPoint(bigger));
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
		// bas test....
	}
	
	@Test
	public void testOveralp(){
		Point s1 = new Point(0,0,0);
		Point e1 = new Point(2,2,2);
		
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

}
