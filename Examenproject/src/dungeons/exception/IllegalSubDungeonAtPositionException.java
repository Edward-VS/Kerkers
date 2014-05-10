package dungeons.exception;

import be.kuleuven.cs.som.annotate.Basic;
import dungeons.Dungeon;
import dungeons.util.Point;

/**
 * A class signaling an attempt of giving a composite dungeon an illegal sub dungeon at a specific position.
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 */
public class IllegalSubDungeonAtPositionException extends IllegalSubDungeonException {

	private static final long serialVersionUID = 1L;

	/**
	 * Initialize a new illegal sub dungeon exception involving the given dungeon at the given position.
	 * 
	 * @param subDungeon
	 * 		The illegal sub dungeon
	 * @param position
	 * 		The illegal position of the given sub dungeon
	 * @post The illegal sub dungeon associated with this exception is the given sub dungeon.
	 * 		| new.getSubDungeon() == dimensions
	 * @post The illegal position associated with this exception is the given position.
	 * 		| new.getPosition() == position
	 */
	public IllegalSubDungeonAtPositionException(Dungeon subDungeon, Point position) {
		super(subDungeon);
		this.position = position;
	}
	
	/**
	 * Get the illegal position associated with this exception.
	 */
	@Basic
	public Point getPosition(){
		return position;
	}

	/*
	 * The illegal position associated with this exception 
	 */
	private Point position;

}
