import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class Klawisze implements KeyListener{

	public Klawisze(Main game){
		game.addKeyListener(this);
	}
	
	public class Key{
		private boolean pressed=false;
		
		public boolean isPressed(){
			return pressed;
		}
		
		public void toggle(boolean isPressed){
			pressed=isPressed;
		}
	}
	
	public Key up = new Key();
	public Key down = new Key();
	public Key left = new Key();
	public Key right = new Key();
	public Key enter = new Key();
	public Key esc = new Key();
	public Key space = new Key();
	
	public void keyPressed(KeyEvent e) {
		toggleKey(e.getKeyCode(), true);		
		if(e.getKeyCode()==KeyEvent.VK_F1){
			if(Main.info==true)Main.info=false;
			else Main.info=true;
		}
		if(Main.menuRunning){
			if(e.getKeyCode()==KeyEvent.VK_UP)Main.menuItem--;
			if(e.getKeyCode()==KeyEvent.VK_DOWN)Main.menuItem++;
			if(e.getKeyCode()==KeyEvent.VK_W)Main.menuItem--;
			if(e.getKeyCode()==KeyEvent.VK_S)Main.menuItem++;
			if(Main.menuItem<0)Main.menuItem=0;
			if(Main.menuItem>3)Main.menuItem=3;
			if(e.getKeyCode()==KeyEvent.VK_ENTER)Main.start=true;
			if(e.getKeyCode()==KeyEvent.VK_SPACE){
				Main.start=true;
			}
			if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
				Main.ServerUp=false;
				System.exit(0);
			}
		}
		if(Main.gameRunning){
			if(e.getKeyCode()==KeyEvent.VK_SPACE){
				Main.placeBomb=true;
			}
		}
		if(!Main.menuRunning && !Main.gameRunning){
			if(e.getKeyCode()==KeyEvent.VK_ESCAPE || e.getKeyCode()==KeyEvent.VK_ENTER || e.getKeyCode()==KeyEvent.VK_SPACE)System.exit(0);
		}
	}

	public void keyReleased(KeyEvent e) {
		toggleKey(e.getKeyCode(), false);
	}

	public void keyTyped(KeyEvent e) {
	}
	
	public void toggleKey(int keyCode, boolean isPressed){
		if(keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP){up.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN){down.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT){left.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT){right.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_ENTER){enter.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_ESCAPE){esc.toggle(isPressed);}
		if(keyCode == KeyEvent.VK_SPACE){space.toggle(isPressed);}
	}

}
