package dungeons.exception;

import be.kuleuven.cs.som.annotate.Basic;
import dungeons.Dungeon;

/**
 * A class signaling an attempt of giving a composite dungeon an illegal sub dungeon.
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 */
public class IllegalSubDungeonException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Initialize a new illegal sub dungeon exception involving the given dungeon.
	 * 
	 * @param subDungeon
	 * 		The illegal sub dungeon
	 * @post The illegal sub dungeon  associated with this exception is the given sub dungeon.
	 * 		| new.getSubDungeon() == dimensions 
	 */
	public IllegalSubDungeonException(Dungeon subDungeon){
		this.subDungeon = subDungeon;
	}
	
	/**
	 * Get the illegal sub dungeon associated with this exception.
	 */
	@Basic
	public Dungeon getSubDugneon(){
		return subDungeon;
	}

	/*
	 * The illegal sub dungeon associated with this exception 
	 */
	private Dungeon subDungeon;
	
}
