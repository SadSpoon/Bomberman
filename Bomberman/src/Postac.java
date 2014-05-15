
public class Postac {
	
	public double x,y,lastX,lastY;
	public int bomby = 1;
	public int moc = 1;
	public boolean killed=false;
	public int ID;
	/*private String[] imgFilenames;
	private Image[] imgFrames;
	private int currentFrame = 0; // current frame number
	private int frameRate = 5;*/  
	//aktualna klatka
	public Postac(int KtoryGracz){
		
		this.ID = KtoryGracz;
		
		switch(KtoryGracz){
			case 1:
				x=50;
				y=50;	
				lastX=x;
				lastY=y;
				break;
			case 2:
				x=750;
				y=550;	
				lastX=x;
				lastY=y;
		}
	}

}
