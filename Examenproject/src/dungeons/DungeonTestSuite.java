package dungeons;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@RunWith(Suite.class)
@Suite.SuiteClasses({
	DungeonTest.class,
	CompositeDungeonTest.class,
	SquareDungeonTest.class,
	ShaftTest.class,
	SquareTest.class
})
public class DungeonTestSuite {
}
