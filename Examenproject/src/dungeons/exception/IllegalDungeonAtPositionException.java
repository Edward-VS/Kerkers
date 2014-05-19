package dungeons.exception;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import dungeons.dungeon.Dungeon;
import dungeons.util.Point;

/**
 * A class signaling an attempt of an illegal transaction with a dungeon at a specific position.
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 */
public class IllegalDungeonAtPositionException extends IllegalDungeonException {

	private static final long serialVersionUID = 1L;

	/**
	 * Initialize a new illegal dungeon exception involving the given dungeon at the given position.
	 * 
	 * @param dungeon
	 * 		The illegal dungeon
	 * @param position
	 * 		The illegal position of the given dungeon
	 * @post The illegal dungeon associated with this exception is the given dungeon.
	 * 		| new.getSubDungeon() == dimensions
	 * @post The illegal position associated with this exception is the given position.
	 * 		| new.getPosition() == position
	 */
	public IllegalDungeonAtPositionException(Dungeon dungeon, Point position) {
		super(dungeon);
		this.position = position;
	}
	
	/**
	 * Get the illegal position associated with this exception.
	 */
	@Basic @Immutable
	public Point getPosition(){
		return position;
	}

	/**
	 * The illegal position associated with this exception 
	 */
	private Point position;

}
