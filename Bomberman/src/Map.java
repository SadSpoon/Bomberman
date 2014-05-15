
class Map {

	public int width =  15;
	public int height = 11;
	
	int[][] plansza = new int[15][11]; // wszystkie klocki
	int[][] bonusy = new int[15][11];
	
	String mapaS;

	
	public int tileSize = 50;
	
	public Map(){
		generateMap();
	}
	
	private void generateMap(){
		
		int randomFactor = -1;
		
		for(int i=0; i<width; i++){
			for(int j=0; j<height; j++){
				randomFactor=(int)(Math.random()*100);
			    
				if(randomFactor<=10){
					   plansza[i][j]=0;
					   if(mapaS==null)mapaS = 0+" ";
					   else mapaS = mapaS+0+" ";
					   continue;
			    }
				
			    else if(randomFactor<=80){
					   plansza[i][j]=1;
					   if(mapaS==null)mapaS = 1+" ";
					   else mapaS = mapaS+1+" ";
					   continue;
				}
				
			    else if(randomFactor<=90){
					   plansza[i][j]=1;
					   bonusy[i][j]=2;
					   if(mapaS==null)mapaS = 2+" ";
					   else mapaS = mapaS+2+" ";
					   continue;
				}
			    
			    else if(randomFactor<=100){
					   plansza[i][j]=1;
					   bonusy[i][j]=3;
					   if(mapaS==null)mapaS = 3+" ";
					   else mapaS = mapaS+3+" ";
					   continue;}
			   }
		   }
		   plansza[0][0]=0;
		   plansza[0][1]=0;
		   plansza[1][0]=0;
		   
		   plansza[13][0]=0;
		   plansza[14][1]=0;
		   plansza[14][0]=0;
		   
		   plansza[0][9]=0;
		   plansza[0][10]=0;
		   plansza[1][10]=0;
		   
		   plansza[13][10]=0;
		   plansza[14][10]=0;
		   plansza[14][9]=0;
		   
		   bonusy[0][0]=0;
		   bonusy[0][1]=0;
		   bonusy[1][0]=0;
		   
		   bonusy[13][0]=0;
		   bonusy[14][1]=0;
		   bonusy[14][0]=0;
		   
		   bonusy[0][9]=0;
		   bonusy[0][10]=0;
		   bonusy[1][10]=0;
		   
		   bonusy[13][10]=0;
		   bonusy[14][10]=0;
		   bonusy[14][9]=0;
		   
		   
		    // this needs to be redone for every map
		   for(int i=1;i<=13;i+=2){
			  for(int j=1;j<=9;j+=2){
				  plansza[i][j]=4;
			  }
		   }
		
		 
	}
	
	public void WypiszStanMapy(){
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				System.out.print(plansza[j][i]+" ");
			}
			
			System.out.print("\n");
		}
	}

	

}
