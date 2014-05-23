package dungeons;

import java.util.Random;

import dungeons.dungeon.CompositeDungeon;
import dungeons.dungeon.Dungeon;
import dungeons.dungeon.Level;
import dungeons.dungeon.MagicLevel;
import dungeons.dungeon.Shaft;
import dungeons.exception.IllegalDungeonAtPositionException;
import dungeons.exception.IllegalDungeonException;
import dungeons.exception.IllegalMaximumDimensionsException;
import dungeons.square.Square;
import dungeons.square.TeleportationSquare;
import dungeons.util.Direction;
import dungeons.util.Point;

/**
 * A class that is intended as an example of how the dungeons application works.
 * We did not document this class well, because it does not belong to the actual application.
 *  
 * @author Edward Van Sieleghem & Christof Vermeersch
 *
 */
public class DungeonsMain {
	
	/**
	 * A composite dungeon is constructed with 4 square-containing dungeons in it:
	 * - A magic level at the bottom (1)
	 * - A normal level in the middle (2)
	 * - A shaft connecting the two previous dungeons (3)
	 * - A separate Level at the top (4)
	 * All these dungeons are filled with some squares at random positions with random openings.
	 */
	public static void main(String [] args) throws IllegalMaximumDimensionsException, IllegalDungeonAtPositionException, IllegalDungeonException{
		// make a dungeon complex
		CompositeDungeon cmpDun = new CompositeDungeon(new Point(10,10,10));
		MagicLevel mgcLvl = new MagicLevel(new Point(10,10,1));
		Level lvl1 = new Level(new Point(10,10,1));
		Level lvl2 = new Level(new Point(10,10,1));
		Shaft shft = new Shaft(new Point(1,1,6));
		
		// fill the dungeons with squares
		fillLevel(lvl1);
		fillLevel(lvl2);
		fillLevel(mgcLvl);
		assert lvl1.getNbSquares() > 0;
		assert lvl2.getNbSquares() > 0;
		assert mgcLvl.getNbSquares() > 0;
		
		// add the dungeons to a main dungeon
		cmpDun.addAsSubDungeonAt(mgcLvl, new Point(0,0,0));
		cmpDun.addAsSubDungeonAt(shft, new Point(3,3,1));
		cmpDun.addAsSubDungeonAt(lvl1, new Point(0,0,7));
		cmpDun.addAsSubDungeonAt(lvl2, new Point(0,0,9));
		
		// connect lvl2 and mgcLvl via teleportation squares
		cmpDun.addAsSquareAt(new Square(), new Point(0,0,9), Direction.values());
		cmpDun.addAsSquareAt(new Square(), new Point(9,9,0), Direction.values());
		cmpDun.addAsSquareAt(new TeleportationSquare(cmpDun.getSquareAt(new Point(9,9,0))), new Point(9,9,9), Direction.values());
		cmpDun.addAsSquareAt(new TeleportationSquare(cmpDun.getSquareAt(new Point(0,0,9))), new Point(0,0,0), Direction.values());
		
		// scramble the magic dungeon
		// Parameters of scramble can be changed via static methods of MagicDungeonHelper
		//MagicDungeonHelper.setPermutationFactor(1f); //-> for guaranteed permutation
		//MagicDungeonHelper.setFillFactor(1f); //-> for guaranteed filling (as much as it can)
		// The teleporation square that were just added are also scrambled (the one in the magic dungeon)
		// Collapseing squares can not be tested with the given setup
		mgcLvl.scramble();
		
		// connect lvl1 and mgcLvl by ensuring that proper square are present in the respective dungeons
		cmpDun.addAsSquareAt(new Square(), new Point(3,3,0), Direction.values());
		cmpDun.addAsSquareAt(new Square(), new Point(3,3,8), Direction.values());
		cmpDun.destroyObstacle(new Point(3,3,0), Direction.UP);
		cmpDun.buildDoor(new Point(3,3,8), Direction.DOWN, false); //the build door is closed
		
		// a simple representation of the dungeon
		printDungeon(cmpDun);
	}
	
	/**
	 * A method that adds square at empty positions in the given dungeon at a rate of fill_factor.
	 * 
	 * @param d
	 * 		the dungeon to fill
	 * @pre The given dungeon contains no squares.
	 * 		| d.getNbSquare() == 0
	 * @post The given dungeon is fill_factor full
	 * 		| d.getNbSquare() == 0
	 * @note This method is not very efficient, but it works... 
	 */
	public static void fillLevel(Level d){
		Random rand = new Random(System.currentTimeMillis());
		Point maxDim = d.getMaximumDimensions();
		
		int desiredCount = (int) (maxDim.size()*fill_factor);
		int actualCount = 0;
		while(desiredCount > actualCount){
			Point p = maxDim.randomSmallerThanThis(rand);
			if(!d.hasSquareAt(p)){
				d.addAsSquareAt(new Square(rand.nextInt(20)), p, Direction.values()[rand.nextInt(4)]);
				actualCount++;
			}
		}
	}
	
	/**
	 * A convenience method that creates a simple String representation of the given dungeon.
	 * For each z-coordinate, a 2D plane is printed
	 */
	public static void printDungeon(Dungeon d){
		Point maxDim = d.getMaximumDimensions();
		for(int i = 0; i < maxDim.getZ(); i++){
			for(int j = 0; j < maxDim.getX(); j++){
				String resH = " ";
				String resL = "";
				for(int k = 0; k < maxDim.getY(); k++){
					Point p = new Point(j, k, i);
					if(d.hasSquareAt(p)){
						Square s = d.getSquareAt(p);
						if(s.getObstacleAt(Direction.NORTH) != null){
							resH += "- ";
						}else{
							resH += "  ";
						}
						if(s.getObstacleAt(Direction.WEST) != null){
							resL += "| ";
						}else{
							resL += "  ";
						}
						//resL += ""
						
						//if(d.getSquareAt(p) instanceof TeleportationSquare) System.out.print("T");
						//else System.out.print("O");
					}else{
						resH += "- ";
						resL += "|x";
						//System.out.print(" ");
					}
					
				}
				System.out.print(resH + "\n" + resL + "\n");
				//System.out.print("\n");
			}
			System.out.print("_____________________________________________________\n");
		}
	}
	
	/**
	 * The amount of actual positions that are filled in the created dungeons.
	 * @invar Float between 0 and 1 inclusive.
	 */
	public static float fill_factor = 0.6f;
	
}
