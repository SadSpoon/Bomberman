
class Bomb {

	public int ownerID;
	public int range = 1;
	
	public int x;
	public int y;
	public long timeToExplode = (long)(1000000000 * 1.7); 
	
	public boolean exploded = false;
	
	
	public Bomb(int ownerID, int x, int y, int range, long timeToExplode){
		this.ownerID = ownerID;
		this.x = x;
		this.y = y;
		this.range = range;
		this.timeToExplode = timeToExplode;
	}

	public boolean detonateBomb(){
		exploded = true;
		
		// zwroc wlascicielowi bombe ... to trzeba napisac, leniu
		
		// wyczysc komorke zawierajaca bombe, bo przeciez wybuchla i jej nie ma, nie?
		// tutaj musi byc odwolanie sie do obiektu mapy (czyt. stworz klase mapy i jej obiekt, leniu)
		
		
		
		
		
		return exploded;
	}
	
	

}
