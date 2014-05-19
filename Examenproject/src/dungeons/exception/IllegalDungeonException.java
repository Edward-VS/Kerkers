package dungeons.exception;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import dungeons.dungeon.Dungeon;

/**
 * A class signaling an attempt of an illegal transaction with a dungeon.
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 */
public class IllegalDungeonException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Initialize a new illegal dungeon exception involving the given dungeon.
	 * 
	 * @param dungeon
	 * 		The illegal dungeon
	 * @post The illegal dungeon associated with this exception is the given dungeon.
	 * 		| new.getDungeon() == dungeon
	 */
	public IllegalDungeonException(Dungeon dungeon){
		this.dungeon = dungeon;
	}
	
	/**
	 * Get the illegal dungeon associated with this exception.
	 */
	@Basic @Immutable
	public Dungeon getDugneon(){
		return dungeon;
	}

	/**
	 * The illegal sub dungeon associated with this exception. (may be a non-effective dungeon)
	 */
	private Dungeon dungeon;
	
}
