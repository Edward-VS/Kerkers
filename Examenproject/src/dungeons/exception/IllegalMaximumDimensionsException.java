package dungeons.exception;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import dungeons.util.Point;

/**
 * A class signaling an attempt of giving a dungeons illegal dimensions.
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 */
public class IllegalMaximumDimensionsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Initialize a new illegal maximum dimensions exception involving the given dimensions.
	 * 
	 * @param dimensions
	 * 		The illegal dimensions
	 * @post The illegal dimensions associated with this exception are the given dimensions.
	 * 		| new.getDimensions() == dimensions 
	 */
	public IllegalMaximumDimensionsException(Point dimensions){
		this.dimensions = dimensions;
	}
	
	/**
	 * Get the illegal dimensions associated with this exception.
	 */
	@Basic @Immutable
	public Point getDimensions(){
		return dimensions;
	}

	/*
	 * the illegal dimensions associated with this exception 
	 */
	private Point dimensions;
	
}
