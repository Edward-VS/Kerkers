package dungeons;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import dungeons.dungeon.CompositeDungeonTest;
import dungeons.dungeon.AbstractDungeonTest;
import dungeons.dungeon.LevelTest;
import dungeons.dungeon.MagicCompositeDungeonTest;
import dungeons.dungeon.MagicDungeonHelperTest;
import dungeons.dungeon.ShaftTest;
import dungeons.dungeon.SquareDungeonTest;
import dungeons.obstacle.ObstacleTest;
import dungeons.obstacle.WallTest;
import dungeons.square.SquareTest;
import dungeons.util.DirectionTest;
import dungeons.util.PointTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// Utility
	DirectionTest.class,
	PointTest.class,
	
	// Dungeons 
	AbstractDungeonTest.class,
	CompositeDungeonTest.class,
	SquareDungeonTest.class,
	ShaftTest.class,
	LevelTest.class,
	MagicCompositeDungeonTest.class,
	MagicDungeonHelperTest.class,
	
	// Squares and obstacles
	SquareTest.class,
	WallTest.class,
	ObstacleTest.class
})

public class DungeonTestSuite {
}
