/**
 * 
 */
package dungeons;

/**
 * @author Christof
 *
 */
public class Rock extends Square {

	/**
	 * Check whether this square can collapse.
	 * 
	 * @return Always false
	 */
	@Override
	public boolean canCollapse(){
		return false;
	}
	
	/**
	 * Check whether this square can make a square bolow it collapse.
	 * 
	 * @return Always false
	 */
	@Override
	public boolean canMakeCollapse(){
		return true;
	}
}
