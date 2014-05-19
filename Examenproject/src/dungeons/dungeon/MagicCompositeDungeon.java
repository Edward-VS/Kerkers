package dungeons.dungeon;

import java.util.ArrayList;
import java.util.List;

import dungeons.exception.IllegalDungeonException;
import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.util.Point;
import dungeons.util.SquareIterator;

/**
 * A magical composite dungeon, is a composite dungeon that is also a magic dungeon. 
 * 
 * @see Documentation MagicDungeon and CompositeDungeon
 * 
 * @invar A dungeon must always have maximum dimensions that are smaller or equal to the maximum allowed dimensions,
 * 		bigger than (0,0,0), in compliance with the dimensions of its parent dungeon (if any) and legal in any other way.
 *		| hasProperMaximumDimensions()
 * @invar All the squares in a dungeon must be in a legal state.
 * 		| hasProperSquares()
 * @invar The parent of a dungeon (if any) must be in a legal state, and the association between its parent dungeon must be
 * 		properly set. 
 * 		| hasProperParentDungeon()
 * @invar A composite dungeon only contains sub dungeons that entirely fit in it and don't overlap. Also its sub dungeons
 * 		must be in a legal state. A magical composite dungeon has at least one sub-dungeon that is also a magic dungeon.
 * 		| hasProperSubDungeons()
 * 
 * @author Edward Van Sieleghem & Christof Vermeersch
 *
 */
public class MagicCompositeDungeon extends CompositeDungeon implements MagicDungeon{

	/**
	 * 
	 * @param maximumDimensions
	 *		The dimensions outside of which no squares can be added.
	 * @param magicSubDungeon
	 * 		A magic subDungeon to add to this composite dungeon
	 * @param position
	 * 		The position to add the new magic subDungeon to.
	 * @throws IllegalMaximumDimensionsException
	 * 		[MUST] The given maximum dimensions are not legal
	 * 		| !canHaveAsMaximumDimensions(maximumDimensions)
	 * @throws IllegalDungeonAtPositionException(subDungeon, position) [MUST]
	 * 		The given sub dungeon can not be placed on the given position.
	 * 		| !canHaveAsSubDungeonAt(subDungeon, position)
	 * @throws IllegalDungeonException(subDungeon) [MUST]
	 * 		The given sub dungeon belong to another parent dungeon.
	 * 		| subDungeon.getParentDungeon() != null 
	 */
	public MagicCompositeDungeon(Point maximumDimensions, MagicDungeon magicSubDungeon, Point position) throws IllegalMaximumDimensionsException, IllegalDungeonException {
		super(maximumDimensions); // throws exception
		// dimensions are properly set up, so we can add a sub dungeon as if construction is already completed.
		addAsSubDungeonAt(magicSubDungeon, position); // throws exception
	}
	
	/**
	 * Construct a new composite dungeon with maximum dimensions (1,1,1). (it can contain one square)
	 * 
	 * @effect A new dungeon is constructed with maximum dimensions (1,1,1)
	 * 		| super()
	 */
	public MagicCompositeDungeon() throws IllegalMaximumDimensionsException {
		super();
	}

	
	
	/*********************************************
	 * MAGICAL
	 *********************************************/
	
	/**
	 * Scramble the squares and obstacles in this dungeon.
	 * 
	 * @see MagicDungeon
	 */
	@Override
	public void scramble() throws IllegalStateException {
		try {
			(new MagicDungeonHelper(this)).scramble();
		} catch (IllegalDungeonException e) {
			throw new IllegalStateException("This dugneon is termianted");
		}
	}
	
	/**
	 * Retrieve all the positions of actual squares that can be swapped during a scramble
	 * 
	 * @return A collection a positions of actual squares that can be scrambled
	 * 		| for each position in result:
	 * 		|	hasSquareAt(position)
	 * 		|	&& MagicDungeonHelper.canParticipateInScramble(getDungeonContainingPosition(position))
	 */
	public List<Point> getSwappablePositions(){
		List<Point> res = new ArrayList<Point>();
		for(Dungeon d:getSubDungeons()){
			if(MagicDungeonHelper.canParticipateInScramble(d)){
				SquareIterator it = d.iterator();
				while(it.hasNext())
					res.add(it.nextEntry().getKey().add(getPositionOfSubDungeon(d)));
			}
		}
		return res;
	}
	
	
	
	/*********************************************
	 * SUB DUNGEON MANAGEMENT
	 *********************************************/
	
	/**
	 * Get the collection of all sub dungeon of this dungeon that can take part in a scramble
	 * 
	 * @return A collection of all dungeons that can take part in a scramble
	 * 		| for each dungeon is getSubDungeons():
	 * 		|	if MagicDungeonHelper.canParticipateInScramble(dungeon)
	 * 		|	then result.contains(dungeon)
	 * 		|	else !result.contains(dungeon)
	 */
	public List<Dungeon> getSubDungeonsThatCanParticipateInScramble(){
		ArrayList<Dungeon> res = new ArrayList<Dungeon>();
		for(Dungeon d:getSubDungeons()){
			if(MagicDungeonHelper.canParticipateInScramble(d))
				res.add(d);
		}
		return res;
	}
	
	/**
	 * Check whether the given sub dungeon may be removed from this dungeon
	 * 
	 * @param subDugneon
	 * 		The sub dungeon to remove.
	 * @return If the given dungeon does not belong to this composite dungeon, then false.
	 * 		| if hasAsSubDungeon(subDungeon)
	 * 		|	then result == false
	 * @return Else if the given dungeon is the last dungeon that can take part in a scramble,
	 * 		and this dungeon is not in a terminated state, then false.
	 * 		| else if !isTerminated()
	 * 		|			&& getSubDungeonsThatCanParticipateInScramble().size()==1
	 * 		|			&& MagicDungeonHelper.canParticipateInScramble(subDungeon)
	 * 		|	then result == false
	 * @return Else true
	 * 		| else result == true
	 */
	@Override
	public boolean canRemoveAsSubDungeon(Dungeon subDungeon) {
		if(!super.canRemoveAsSubDungeon(subDungeon))
			return false;
		if(!isTerminated() && getSubDungeonsThatCanParticipateInScramble().size()==1 && MagicDungeonHelper.canParticipateInScramble(subDungeon))
			return false;
		return true;
	}

	/**
	 * Check whether all the sub dungeons of this dungeon are in a legal state, at a legal position.
	 * 
	 * @return False if not all sub dungeons comply with the conditions imposed in a super-type of this MagicCompositeDungeon.
	 * 		| if !super.hasProperSubDungeons()
	 * 		|	then result == false
	 * @return Else if this dungeon is not terminated and has no sub dungeons that can participate in a scramble, then false.
	 * 		| else if getSubDungeonsThatCanParticipateInScramble().size() == 0
	 * 		|	then result == false
	 * @return Else true
	 * 		| else result == true
	 */
	@Override
	public boolean hasProperSubDungeons(){
		if(!super.hasProperSubDungeons())
			return false;
		if(!isTerminated() && getSubDungeonsThatCanParticipateInScramble().size() == 0)
			return false;
		return true;
	}

}
