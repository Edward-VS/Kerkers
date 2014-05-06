package dungeons.obstacle;

public class Door extends Obstacle{

	private boolean isOpen;
	
	public void setOpen(boolean isOpen){
		this.isOpen = isOpen;
	}
	
	public boolean isOpen(){
		return isOpen;
	}
	
}
