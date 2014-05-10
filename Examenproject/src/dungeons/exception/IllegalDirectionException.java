package dungeons.exception;

import be.kuleuven.cs.som.annotate.Basic;
import dungeons.util.Direction;

/**
 * A class signaling an attempt of using an illegal direction (eg of a shaft).
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 */
public class IllegalDirectionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Initialize a new illegal direction exception involving the given direction.
	 * 
	 * @param direction
	 * 		The illegal direction
	 * @post The illegal direction associated with this exception is the given direction.
	 * 		| new.getDirection() == direction 
	 */
	public IllegalDirectionException(Direction direction){
		this.direction = direction;
	}
	
	/**
	 * Get the illegal direction associated with this exception.
	 */
	@Basic
	public Direction getDirection(){
		return direction;
	}

	/*
	 * The illegal direction associated with this exception 
	 */
	private Direction direction;
	
}
