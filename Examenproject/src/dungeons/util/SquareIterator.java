package dungeons.util;

import java.util.AbstractMap;
import java.util.Iterator;

import dungeons.Square;

public interface SquareIterator extends Iterator<Square>{

	/**
	 * Returns the position associated with the next square-position pair in the iteration
	 * 
	 * @return The position associated with the next square-position pair in the iteration
	 * @throws NoSuchElementException
	 * 		The are no more elements in the iteration.
	 */
	public AbstractMap.SimpleEntry<Point, Square> nextEntry();
	
}
