 import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
 
/** Animating image frames. Each frame has its own file */
@SuppressWarnings("serial")
public class Main extends JPanel {
   // Named-constants
   static JFrame frame;
   
   static final int CANVAS_WIDTH = 850;
   static final int CANVAS_HEIGHT = 650;
   public static final String TITLE = "Bomberman Pawszy v2 -- dev unstable";
   
   public enum ViewStates {
	   
	   InGame, InMenu, Paused, Connecting, Server
	   
   }
   
   ViewStates GameState;
   
   public static boolean isPainted = false;
   
   public static boolean gameRunning = false; // InGame
   public static boolean menuRunning = true; // InMenu
   public static boolean start = false; // ta sama funkcjonalnosc, co gameRunning
   public static boolean pause = false; // Paused
   public static boolean polaczono = false; // InGame?
   public static boolean ServerUp = false; // Server
   public static boolean ClientConnecting = false; // Connecting
   public static boolean info = false;
   public static boolean sendMap=false;
   
   
   public static boolean placeBomb;
   
   public Postac player1;
   public Postac player2;
   
   int[] ktorySegment = new int[2];
   
   static int[][] bonusy = new int[15][11];
   long[][] firemap = new long[15][11]; //mapa dzieki ktorej maze plomienie
   static long[][] boombmap = new long[15][11]; // na podstawie tej tablicy wybuchaja bomby
   static int[][] boombMoc = new int[15][11];
   static int[][] plansza = new int[15][11]; // wszystkie klocki
   static boolean[][] playerBoombs = new boolean[15][11];
   int[][] tempPlansza=plansza;
   static String mapaS;
   
   // 0 - puste
   // 1 - cegla
   // 2 - moc
   // 3 - bomba
   // 4 - sciana
   // 5 - Ogien srodek
   // 6 - ogien pion
   // 7 - ogien poziom
   // 8 - ogien gora
   // 9 - ogien dol
   //10 - ogien prawo
   //11 - ogien lewo
   //12 - bomba1
   //13 - bomba2
   
   //15	- ogien gora prawo
   //16 - ogien gora lewo
   //17 - ogien dol prawo
   //18 - ogien dol lewo

   //20 - player
   
   Image Sciana = LoadImage("SzaraSciana.png"); 
   Image CeglaMoc = LoadImage("ceglaMoc.png"); 
   Image CeglaBom = LoadImage("ceglaBom.png"); 
   Image Cegla = LoadImage("cegla3.png"); 
   Image OgienGora = LoadImage("ogienGora.png"); 
   Image OgienDol = LoadImage("ogienDol.png"); 
   Image OgienPrawo = LoadImage("ogienPrawo.png"); 
   Image OgienLewo = LoadImage("ogienLewo.png"); 
   Image OgienSrodek = LoadImage("ogienSrodek.png"); 
   Image OgienPoziomo = LoadImage("ogienPoziom.png"); 
   Image OgienPionowo = LoadImage("ogienPion.png"); 
   Image Bomba = LoadImage("Bomba.png"); 
   Image bBomb = LoadImage("bBomb.png");
   Image bMoc = LoadImage("bMoc.png");
   Image Lose = LoadImage("Lose.png");
   Image Win = LoadImage("Win.png");
   Image Player1 = LoadImage("player1.png");
   Image Player2 = LoadImage("player2.png");
   Image Widmo = LoadImage("widmo.png");
   Image oGL = LoadImage("oGL.png");
   Image oGP = LoadImage("oGP.png");
   Image oDL = LoadImage("oDL.png");
   Image oDP = LoadImage("oDP.png");
   
   int tempLoading=0;
   long czasWybuchuBomby = (long) (1000000000*1.7); //1,7s
   long czasCzyszczeniaPlomieni = (long) (1000000000*0.5); //0,5s
   long czas;
   int bombaSiec = 0; //wysylanie info o podlozonych bombach do przeciwnika;
   
   public Server server;
   public Client client;
   
   boolean isServer;
   
   public int port = 54321; 
   public String host = "localhost";
   int ping;
   
   public static int menuItem = 0;
   
   public final static long OPTIMAL_TIME = 1000000000 / 60; //ilosc generowanych FPSów 
   long lastFpsTime;
   int fps; //licznik FPSow
   int AktualneFPSy = 0;
   static double delta;   
   public static double speed=4;
   
   static Color CiemnyZielony = new Color(25, 140, 25);
   
   public Klawisze input;
   
   public Main(){
	   	setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
	   	setFocusable(true);	 
	   	input = new Klawisze(this);
	   	Sound sound = new Sound();
	   	
	   	GameState = ViewStates.InGame;
	   	
        Thread loop = new Thread()
        {
           public void run()
           {
        	  Menu();
           }
        };
        loop.start();
   }
   
   public void Menu(){
	   isPainted=false;
	   
	   while (menuRunning)
	   {
			UpdateMenu();
			repaint();
			try{Thread.sleep(OPTIMAL_TIME/1000000);}catch (Exception e) {}
	   }
   }
   
   public void Loading(){
	   ServerUp=true;
	   isPainted=false;
	   while(ServerUp){
		   repaint();
		   try{Thread.sleep(OPTIMAL_TIME/1000000);}catch (Exception e) {}
		   if(isServer&&sendMap){
			   server.sendData(("2 "+Main.mapaS+" ").getBytes());
		   }
		   if(!isServer) client.sendData("1 ".getBytes());
		   if(polaczono){
			   sendMap=false;
			   gameLoop();
			   break;
		   }
	   }
   }
   
   public void Host(){
	   System.out.println("TO JEST SERVER!");
	   server = new Server(port);
	   server.start();
	   isServer=true;
	   GenerujCegly();
	   Loading();
   }
   
   public void Join(){
	   host = JOptionPane.showInputDialog(this, "Wprowadz IP serwera gry");
	   if(host == null)	Menu();
	   
	   
	   client = new Client(host, port);
	   client.start();
	   client.sendData("1 0 0 0 0 0 0 0 0 ".getBytes());
	   Loading();
   }
   
   public void gameLoop()
   { 
	  ServerUp = false;
	  isPainted=false;
      long lastLoopTime = System.nanoTime();
      menuRunning=false;
	  gameRunning=true;
	  player1 = new Postac(1);
	  player2 = new Postac(2);
      // keep looping round til the game ends
      while (gameRunning)
      {
    	 
	         // work out how long its been since the last update, this
	         // will be used to calculate how far the entities should
	         // move this loop
	         long now = System.nanoTime();
	         long updateLength = now - lastLoopTime;
	         lastLoopTime = now;
	         delta = updateLength / ((double)OPTIMAL_TIME);
	
	         // update the frame counter
	         lastFpsTime += updateLength;
	         fps++;
	         
	         // update our FPS counter if a second has passed since
	         // we last recorded
	         
	         // update the game logic
	         Update();
	         
	         // draw everyting
	         repaint();
			   if (lastFpsTime >= 1000000000)
		         {
			       //WypiszStanMapy();
				   lastFpsTime = 0;
				   AktualneFPSy = fps;
				   fps = 0;
				   if(isServer)ping = (int)server.ping;
				   else ping = (int)client.ping;
		         }
			   
	         // we want each frame to take 10 milliseconds, to do this
	         // we've recorded when we started the frame. We add 10 milliseconds
	         // to this and then factor in the current time to give 
	         // us our final value to wait for
	         // remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
	         try{Thread.sleep( (lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000 );}catch (Exception e) {}
    	  
      }
   }
   public void Update(){
	   
	   czas = System.nanoTime();
	   if(isServer){
		   if(input.up.isPressed())player1.y-=1*speed*delta;
		   if(input.down.isPressed())player1.y+=1*speed*delta;
		   if(input.left.isPressed())player1.x-=1*speed*delta;
		   if(input.right.isPressed())player1.x+=1*speed*delta;
		   
		   PrzyporzadkowaniePlayera(player1);
		   ktorySegment();
		   SprawdzKolizje(player1);
		   if(placeBomb)placeBomb(player1);
		   kaboom(player1);
		   clear();
		   sprawdzPerki(player2);
		   
		   if(player1.x<50)player1.x=50;
		   if(player1.y<50)player1.y=50;
		   if(player1.x>CANVAS_WIDTH-100)player1.x=CANVAS_WIDTH-100;
		   if(player1.y>CANVAS_HEIGHT-100)player1.y=CANVAS_HEIGHT-100;
		   
			try{
				server.sendData(("3 "+Integer.toString((int)(player1.x))+" "+Integer.toString((int)(player1.y))+" "+bombaSiec+" "+player1.moc+" "+ktorySegment[0]+" "+ktorySegment[1]+" "+czas+" "+server.picTime+" ").getBytes());
			}catch(Exception e){}
			
			   player2.x=server.x;
			   player2.y=server.y;
	   }
	   else{
		   if(input.up.isPressed())player2.y-=1*speed*delta;
		   if(input.down.isPressed())player2.y+=1*speed*delta;
		   if(input.left.isPressed())player2.x-=1*speed*delta;
		   if(input.right.isPressed())player2.x+=1*speed*delta;
		   
		   PrzyporzadkowaniePlayera(player2);
		   ktorySegment();
		   SprawdzKolizje(player2);

		   if(placeBomb)placeBomb(player2);
		   kaboom(player2);
		   clear();
		   sprawdzPerki(player1);
		   
		   if(player2.x<50)player2.x=50;
		   if(player2.y<50)player2.y=50;
		   if(player2.x>CANVAS_WIDTH-100)player2.x=CANVAS_WIDTH-100;
		   if(player2.y>CANVAS_HEIGHT-100)player2.y=CANVAS_HEIGHT-100;
		   
			try{
				client.sendData(("3 "+Integer.toString((int)(player2.x))+" "+Integer.toString((int)(player2.y))+" "+bombaSiec+" "+player2.moc+" "+ktorySegment[0]+" "+ktorySegment[1]+" "+czas+" "+client.picTime+" ").getBytes()); //segment odwrotnie
			}catch(Exception e){}
			
		   player1.x=client.x;
		   player1.y=client.y;
	   }
	   bombaSiec=0;
	   if(input.esc.isPressed()){
		   gameRunning=false;
		   menuRunning=true;
		   Menu();
	   }
   }
   void placeBomb(Postac player){
	   placeBomb=false;
	   if(player.bomby>0){
		   bombaSiec=1;
		   player.bomby--;
		   playerBoombs[ktorySegment[0]][ktorySegment[1]]=true;
		   plansza[ktorySegment[0]][ktorySegment[1]]=12;
		   boombMoc[ktorySegment[0]][ktorySegment[1]]=player.moc;
		   boombmap[ktorySegment[0]][ktorySegment[1]]=System.nanoTime();
	   }
   }
   //czysci plomienie
   void clear(){
	   for(int i=0;i<11;i++){
		   for(int j=0;j<15;j++){
			   if((plansza[j][i]== 5 || plansza[j][i]== 6 || plansza[j][i]== 7 || plansza[j][i]==8 || plansza[j][i]==9 || plansza[j][i]==10 || plansza[j][i]==11 || plansza[j][i]==15 || plansza[j][i]==16 || plansza[j][i]==17 || plansza[j][i]==18) && System.nanoTime()-firemap[j][i]>=czasCzyszczeniaPlomieni){
				   if(bonusy[j][i]==0)plansza[j][i]=0;
				   if(bonusy[j][i]==2){
					   bonusy[j][i]=0;
					   plansza[j][i]=2;
				   }
				   if(bonusy[j][i]==3){
					   bonusy[j][i]=0;
					   plansza[j][i]=3;
				   }
			   }
		   }
	   }
   }
   void kaboom(Postac player){
	   for(int i=0;i<11;i++){
		   for(int j=0;j<15;j++){
			   if((plansza[j][i]==12) && System.nanoTime()-boombmap[j][i]>=czasWybuchuBomby){
				   if(playerBoombs[j][i]==true){
					   player.bomby++;
					   playerBoombs[j][i]=false;
				   }
				   boombmap[j][i]=0;
				   firemap[j][i]=System.nanoTime();
				   boolean flag = true;
				   
				   if(player.x > 50*j && player.x < 100 + 50*j && player.y > 50*i && player.y < 100 + 50*i){
					   player.killed = true;
				   }
				   
				   plansza[j][i]=5;
				   //LEWO
				   for(int k=1;k<=boombMoc[j][i] && flag;k++){
					   //plomienie przecinajace sie
					   if(j-k>=0 && plansza[j-k][i]==6){
						   plansza[j-k][i]=5;
						   continue;
					   }  
					   if(j-k>=0 && k!=boombMoc[j][i]){
						   if(plansza[j-k][i]==0){
							   plansza[j-k][i]=7;
						   	   firemap[j-k][i]=System.nanoTime();
						   }
						   else if ((plansza[j-k][i]==1) || (plansza[j-k][i]==2) || (plansza[j-k][i]==3) || (plansza[j-k][i]==20)){
							   plansza[j-k][i]=11;
							   firemap[j-k][i]=System.nanoTime();
							   flag=false;
						   }
						   else if (plansza[j-k][i]==5){
							   plansza[j-k][i]=5;
						   }
						   else{
							   flag=false;
						   }
					   }
					   //koniec skrzyzowany
					   if(j-k>=0 && k==boombMoc[j][i] && plansza[j-k][i]==8){
						   plansza[j-k][i]=17;
						   firemap[j-k][i]=System.nanoTime();
					   }
					   else if(j-k>=0 && k==boombMoc[j][i] && plansza[j-k][i]==9){
						   plansza[j-k][i]=15;
						   firemap[j-k][i]=System.nanoTime();
					   }
					   //koniec plomienia
					   else if(j-k>=0 && k==boombMoc[j][i] && plansza[j-k][i]!=12 && plansza[j-k][i]!=4){
						   plansza[j-k][i]=11;
						   firemap[j-k][i]=System.nanoTime();
					   }

					   //BOmba zapalajaca inne bomby
					   if (j-k>=0 && plansza[j-k][i]==12){
						   boombmap[j-k][i]=System.nanoTime()-czasWybuchuBomby;   
					   }
					   //IT'S A PLAYER KILL HIM! 10 px tolerancji
					   if(j-k>=0 && player.x > 10+50*(j-k) && player.x < 90 + 50*(j-k) && player.y > 10+50*i && player.y < 90 + 50*i){
						   player.killed=true;
					   }
				   }
				   flag=true;
				   //PRAWO
				   for(int k=1;k<=boombMoc[j][i] && flag;k++){
					   
					   if(j+k<=14 && plansza[j+k][i]==6){
						   plansza[j+k][i]=5;
						   continue;
					   }
					   
					   if(j+k<=14 && k!=boombMoc[j][i]){
						   if(plansza[j+k][i]==0){
							   plansza[j+k][i]=7;
							   firemap[j+k][i]=System.nanoTime();
						   }
						   else if ((plansza[j+k][i]==1) || (plansza[j+k][i]==2) || (plansza[j+k][i]==3)){
							   plansza[j+k][i]=10;
							   firemap[j+k][i]=System.nanoTime();
							   flag=false;
						   }
						   else{
							   flag=false;
						   }
					   }
					   //koniec skrzyzowany
					   if(j+k<=14 && k==boombMoc[j][i] && plansza[j+k][i]==8){
						   plansza[j+k][i]=18;
						   firemap[j+k][i]=System.nanoTime();
					   }
					   else if(j+k<=14 && k==boombMoc[j][i] && plansza[j+k][i]==9){
						   plansza[j+k][i]=16;
						   firemap[j+k][i]=System.nanoTime();
					   }
					   else if(j+k<=14 && k==boombMoc[j][i] && plansza[j+k][i]!=12 && plansza[j+k][i]!=4){
						   firemap[j+k][i]=System.nanoTime();
						   plansza[j+k][i]=10;
					   }

					   if (j+k<=14 && plansza[j+k][i]==12){
						   boombmap[j+k][i]=System.nanoTime()-czasWybuchuBomby;   
					   }
					   //IT'S A PLAYER KILL HIM!
					   if(j+k<=14 && player.x > 10+50*(j+k) && player.x < 90 + 50*(j+k) && player.y > 10+50*i && player.y < 90 + 50*i){
						   player.killed=true;
					   }
				   }
				   flag=true;
				   //GORA
				   for(int k=1;k<=boombMoc[j][i] && flag;k++){
					   if(i-k>=0 && plansza[j][i-k]==7){
						   plansza[j][i-k]=5;
						   continue;
					   }
					   
					   if(i-k>=0 && k!=boombMoc[j][i]){
						   if(plansza[j][i-k]==0){
							   plansza[j][i-k]=6;
							   firemap[j][i-k]=System.nanoTime();
						   }
						   else if ((plansza[j][i-k]==1) || (plansza[j][i-k]==2) || (plansza[j][i-k]==3) || (plansza[j][i-k]==20)){
							   plansza[j][i-k]=8;
							   firemap[j][i-k]=System.nanoTime();
							   flag=false;
						   }
						   else{
							   flag=false;
						   }
					   }
						 //koniec skrzyzowany
					   if(i-k>=0 && k==boombMoc[j][i] && plansza[j][i-k]==10){
						   plansza[j][i-k]=18;
						   firemap[j][i-k]=System.nanoTime();
					   }
					   else if(i-k>=0 && k==boombMoc[j][i] && plansza[j][i-k]==11){
						   plansza[j][i-k]=17;
						   firemap[j][i-k]=System.nanoTime();
					   }
					   else if(i-k>=0 && k==boombMoc[j][i] && plansza[j][i-k]!=12 && plansza[j][i-k]!=4){
						   plansza[j][i-k]=8;
						   firemap[j][i-k]=System.nanoTime();
					   }

					   if (i-k>=0 && plansza[j][i-k]==12){
						   boombmap[j][i-k]=System.nanoTime()-czasWybuchuBomby;   
					   }
					   //IT'S A PLAYER KILL HIM!
					   if(i-k>=0 && player.x > 10+50*j && player.x < 90 + 50*j && player.y > 10+50*(i-k) && player.y < 90 + 50*(i-k)){
						   player.killed=true;
					   }
				   }
				   flag=true;
				   //DOL
				   for(int k=1;k<=boombMoc[j][i] && flag;k++){
					   
					   if(i+k<=10 && plansza[j][i+k]==7){
						   plansza[j][i+k]=5;
						   continue;
					   }
					   
					   if(i+k<=10 && k!=boombMoc[j][i]){
						   if(plansza[j][i+k]==0){
							   plansza[j][i+k]=6;
							   firemap[j][i+k]=System.nanoTime();
						   }
						   else if ((plansza[j][i+k]==1) || (plansza[j][i+k]==2) || (plansza[j][i+k]==3)){
							   plansza[j][i+k]=9;
							   firemap[j][i+k]=System.nanoTime();
							   flag=false;
						   }
						   else{
							   flag=false;
						   }
					   }
						 //koniec skrzyzowany
					   if(i+k<=10 && k==boombMoc[j][i] && plansza[j][i+k]==10){
						   plansza[j][i+k]=16;
						   firemap[j][i+k]=System.nanoTime();
					   }
					   else if(i+k<=10 && k==boombMoc[j][i] && plansza[j][i+k]==11){
						   plansza[j][i+k]=15;
						   firemap[j][i+k]=System.nanoTime();
					   }
					   else if(i+k<=10 && k==boombMoc[j][i] && plansza[j][i+k]!=12 && plansza[j][i+k]!=4){
						   plansza[j][i+k]=9;
						   firemap[j][i+k]=System.nanoTime();
					   }

					   if (i+k<=10 && plansza[j][i+k]==12){
						   boombmap[j][i+k]=System.nanoTime()-czasWybuchuBomby;   
					   }
					   //IT'S A PLAYER KILL HIM!
					   if(i+k<=10 && player.x > 10+50*j && player.x < 90 + 50*j && player.y > 10+50*(i+k) && player.y < 90 + 50*(i+k)){
						   player.killed=true;
					   }
				   }
			   }
		   }
	   }
   }
   
   void SprawdzKolizje(Postac player){
	   if(ktorySegment[0]!=0)if(player.x<ktorySegment[0]*50+50 && !(plansza[ktorySegment[0]-1][ktorySegment[1]]==0 || plansza[ktorySegment[0]-1][ktorySegment[1]]==2 || plansza[ktorySegment[0]-1][ktorySegment[1]]==3))player.x=ktorySegment[0]*50+50;
	   if(ktorySegment[0]!=14)if(player.x>ktorySegment[0]*50+50 && !(plansza[ktorySegment[0]+1][ktorySegment[1]]==0 || plansza[ktorySegment[0]+1][ktorySegment[1]]==2 || plansza[ktorySegment[0]+1][ktorySegment[1]]==3))player.x=ktorySegment[0]*50+50;
	   
	   if(ktorySegment[1]!=0)if(player.y<ktorySegment[1]*50+50 && !(plansza[ktorySegment[0]][ktorySegment[1]-1]==0 || plansza[ktorySegment[0]][ktorySegment[1]-1]==2 || plansza[ktorySegment[0]][ktorySegment[1]-1]==3))player.y=ktorySegment[1]*50+50;
	   if(ktorySegment[1]!=10)if(player.y>ktorySegment[1]*50+50 && !(plansza[ktorySegment[0]][ktorySegment[1]+1]==0 || plansza[ktorySegment[0]][ktorySegment[1]+1]==2 || plansza[ktorySegment[0]][ktorySegment[1]+1]==3))player.y=ktorySegment[1]*50+50;
	   
	   if(player.x<ktorySegment[0]*50+37 && player.y<ktorySegment[1]*50+37){
		   player.x=ktorySegment[0]*50+37;
		   player.y=ktorySegment[1]*50+37;
	   }
	   if(player.x>ktorySegment[0]*50+63 && player.y>ktorySegment[1]*50+63){
		   player.x=ktorySegment[0]*50+63;
		   player.y=ktorySegment[1]*50+63;
	   }
	   
	   if(player.x<ktorySegment[0]*50+37 && player.y>ktorySegment[1]*50+63){
		   player.x=ktorySegment[0]*50+37;
		   player.y=ktorySegment[1]*50+63;
	   }
	   if(player.x>ktorySegment[0]*50+63 && player.y<ktorySegment[1]*50+37){
		   player.x=ktorySegment[0]*50+63;
		   player.y=ktorySegment[1]*50+37;
	   }
   }
   
   public void ktorySegment(){
	   boolean flag=true;
	   for(int i=0;i<11&&flag;i++){
		   for(int j=0;j<15&&flag;j++){
			   if(plansza[j][i]==20){
				   ktorySegment[0]=j;
				   ktorySegment[1]=i;
			   }
		   }
	   }
   }

   public void UpdateMenu(){
	   if(start){
		   start = false;
		   switch(menuItem){
		   case 0:
		   			Host();
		   			break;

		   case 1:	
		            Join();
		   			break;
		   			
		   case 2:	
		   			break;

		   case 3:	menuRunning=false;
  					gameRunning=false;
			   		System.exit(0);
		   			break;
		   }
	   }
   }
   public void paintComponent(Graphics g){
	   if(gameRunning){
		   g.setColor(CiemnyZielony);
		   g.fill3DRect(-1, -1, CANVAS_WIDTH, CANVAS_HEIGHT, true);
		   isPainted = true;
		   
			   //Ping 
		   if (lastFpsTime >= 1000000000)
	         {
			   //if(isServer)frame.setTitle(TITLE +"		Server		Ping: "+String.valueOf(server.ping)+"		FPS:"+fps);
			   //else frame.setTitle(TITLE +"		Client		Ping: "+String.valueOf(client.ping)+"		FPS:"+fps);
			   
	         }
		   //Mapa
		   for(int i=0;i<CANVAS_WIDTH-99;i+=50){
			   g.drawImage(Sciana, i, 0, null);
			   g.drawImage(Sciana, i, CANVAS_HEIGHT-50, null);
		   }
		   for(int i=0;i<CANVAS_WIDTH-99;i+=50){
			   g.drawImage(Sciana, 0, i, null);
			   g.drawImage(Sciana, CANVAS_WIDTH-50, i, null);
		   }
		   
		   for(int i=0;i<15;i+=1){
			   for(int j=0;j<11; j+=1){
				   switch (tempPlansza[i][j]){
				   case 0: break;
				   case 1: 	g.drawImage(Cegla, 50+50*i, 50+50*j, null);
				   			break;
				   case 2: g.drawImage(bMoc, 50+50*i, 50+50*j, null);
		   					break;

				   case 3: g.drawImage(bBomb, 50+50*i, 50+50*j, null);
		   					break;

				   case 4: g.drawImage(Sciana, 50+50*i, 50+50*j, null);
		   					break;

				   case 5: g.drawImage(OgienSrodek, 50+50*i, 50+50*j, null);
		   					break;

				   case 6: g.drawImage(OgienPionowo, 50+50*i, 50+50*j, null);
		   					break;

				   case 7: g.drawImage(OgienPoziomo, 50+50*i, 50+50*j, null);
		   					break;

				   case 8: g.drawImage(OgienGora, 50+50*i, 50+50*j, null);
		   					break;

				   case 9: g.drawImage(OgienDol, 50+50*i, 50+50*j, null);
		   					break;

				   case 10: g.drawImage(OgienPrawo, 50+50*i, 50+50*j, null);
		   					break;

				   case 11: g.drawImage(OgienLewo, 50+50*i, 50+50*j, null);
		   					break;

				   case 12: g.drawImage(Bomba, 50+50*i, 50+50*j, null);
		   					break;
		   					
				   case 15: g.drawImage(oGP, 50+50*i, 50+50*j, null);
	   					break;
				   case 16: g.drawImage(oGL, 50+50*i, 50+50*j, null);
	   					break;
				   case 17: g.drawImage(oDP, 50+50*i, 50+50*j, null);
	   					break;
				   case 18: g.drawImage(oDL, 50+50*i, 50+50*j, null);
	   					break;
		   			
				   case 20: break;
		   			default: System.err.println("Blad podczas rysowania "+ i +" "+ j +" sprita");
		   					
				   }
			   }
		   }
		   
		   //Rysowanie Pierwszego gracza
		   g.drawImage(Widmo, (int)player1.lastX, (int)player1.lastY, null);
		   g.drawImage(Player1, (int)player1.x, (int)player1.y, null);
		   player1.lastY=player1.y;
		   player1.lastX=player1.x;
		   //Rysowanie drugiego gracza
		   g.drawImage(Widmo, (int)player2.lastX, (int)player2.lastY, null);
		   g.drawImage(Player2, (int)player2.x, (int)player2.y, null);
		   player2.lastY=player2.y;
		   player2.lastX=player2.x;
		   
		   if(isServer){
			   if(player1.killed){
				   g.drawImage(Lose, 0, 0, null);
				   server.sendData("4".getBytes());
				   gameRunning=false;
				   info=false;
			   }
			   if(server.win){	
				   g.drawImage(Win, 0, 0, null);
				   gameRunning=false;
				   info=false;
			   }
		   }
		   else{
			   if(client.win){	
				   g.drawImage(Win, 0, 0, null);
				   gameRunning=false;
				   info=false;
			   }
			   if(player2.killed){	
				   g.drawImage(Lose, 0, 0, null);
				   client.sendData("4".getBytes());
				   gameRunning=false;
				   info=false;
			   }
		   }
		   
	   }
	   if(menuRunning && !ServerUp){
		   g.setColor(Color.black);
		   g.fill3DRect(-1, -1, CANVAS_WIDTH, CANVAS_HEIGHT, true);
  			switch(menuItem){
		   		case 0: 	
		   			g.setColor(Color.red);
		   			g.drawString("Create", 10, 10);
		   			g.setColor(Color.white);
		   			g.drawString("Join", 10, 30);
		   			g.drawString("Credits", 10, 50);
		   			g.drawString("Exit", 10, 70);
		   			break;
		   		case 1: 	
   					g.setColor(Color.white);
   					g.drawString("Create", 10, 10);
   					g.setColor(Color.red);
   					g.drawString("Join", 10, 30);
   					g.setColor(Color.white);
   					g.drawString("Credits", 10, 50);
   					g.drawString("Exit", 10, 70);
   					break;
   					
		   		case 2: 	
		   			g.setColor(Color.white);
   					g.drawString("Create", 10, 10);
   					g.drawString("Join", 10, 30);
   					g.setColor(Color.red);
   					g.drawString("Credits", 10, 50);
   					g.setColor(Color.white);
   					g.drawString("Exit", 10, 70);
   					break;
   					
		   		case 3: 	
		   			g.setColor(Color.white);
   					g.drawString("Create", 10, 10);
   					g.drawString("Join", 10, 30);
   					g.drawString("Credits", 10, 50);
   					g.setColor(Color.red);
   					g.drawString("Exit", 10, 70);
   					break;
		   }
	   }
	   if(menuRunning && ServerUp && !isPainted){
		   g.setColor(Color.black);
		   g.fill3DRect(-1, -1, CANVAS_WIDTH, CANVAS_HEIGHT, true);
		   g.setColor(Color.yellow);
		   if(tempLoading==0)g.drawString("Oczekiwanie na przeciwnika", CANVAS_WIDTH/2, CANVAS_HEIGHT/2);
		   if(tempLoading==1)g.drawString("Oczekiwanie na przeciwnika .", CANVAS_WIDTH/2, CANVAS_HEIGHT/2);
		   if(tempLoading==2)g.drawString("Oczekiwanie na przeciwnika ..", CANVAS_WIDTH/2, CANVAS_HEIGHT/2);
		   if(tempLoading==3)g.drawString("Oczekiwanie na przeciwnika ...", CANVAS_WIDTH/2, CANVAS_HEIGHT/2);
		   tempLoading++;
		   if(tempLoading>3)tempLoading=0;
		   isPainted = false;
	   }
	   if (info)
       {
		   g.setColor(Color.yellow);
		   g.setFont(new Font("Comic Sans", Font.BOLD, 16));
		   g.drawString("Total Memory: "+String.valueOf(Runtime.getRuntime().totalMemory()/(1024*1024))+" MB", 650, 20);
		   g.drawString("Free Memory: "+String.valueOf(Runtime.getRuntime().freeMemory()/(1024*1024))+" MB", 650, 50);
		   if(gameRunning){
			   g.drawString("FPS: "+AktualneFPSy, 650, 80);
			   g.drawString("Ping: "+ping+"ms", 730, 80);			   
		   }	   
       }
   }
   public Image LoadImage(String imgFilePath) {
	      URL imgUrl = getClass().getClassLoader().getResource(imgFilePath);
	      if (imgUrl == null) {
	         System.err.println("Couldn't find file: " + imgFilePath);
	      } else {
	         try {
	            return ImageIO.read(imgUrl);
	         } catch (IOException ex) {
	            ex.printStackTrace();
	         }
	      }
		return null;
	   }
   // ----- zrobione -------
   public void GenerujCegly(){
	   int temp = -1;
	   for(int i=0;i<15;i++){
		   for(int j=0;j<11;j++){
			   temp=(int)(Math.random()*100);
			   if(temp<=10){
				   plansza[i][j]=0;
				   if(mapaS==null)mapaS = 0+" ";
				   else mapaS = mapaS+0+" ";
				   continue;
				   }
			   if(temp<=80){
				   plansza[i][j]=1;
				   if(mapaS==null)mapaS = 1+" ";
				   else mapaS = mapaS+1+" ";
				   continue;}
			   if(temp<=90){
				   plansza[i][j]=1;
				   bonusy[i][j]=2;
				   if(mapaS==null)mapaS = 2+" ";
				   else mapaS = mapaS+2+" ";
				   continue;
			   }
			   if(temp<=100){
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
	   
	   for(int i=1;i<=13;i+=2){
		  for(int j=1;j<=9;j+=2){
			  plansza[i][j]=4;
		  }
	   }
	   //int[][] plansza = new int[15][11]; // wszystkie klocki
   }
   
   // ----- zrobione -------
   public void WypiszStanMapy(){
		  for(int i=0;i<11;i++){
			  for(int j=0;j<15;j++){
				  System.out.print(plansza[j][i]+" ");
			  }
			  System.out.print("\n");
		  }
   }
   //Sprawdza czy przeciwnik nie wzial perka;
   void sprawdzPerki(Postac player){
	   boolean flag=true;
	   for(int i=0;i<11&&flag;i++){
		   for(int j=0;j<15&&flag;j++){
			   if(player.x<j*50+74&&player.y<i*50+74){
				   if(plansza[j][i]==2){
					   plansza[j][i]=0;
				   }
				   if(plansza[j][i]==3){
					   plansza[j][i]=0;
				   }
				   flag=false;
			   }
		   }
	   }
   }
// szuka gracza na mapie po segmentach, kolizja gracza z bonusami ---- todo?   
   public void PrzyporzadkowaniePlayera(Postac player){
	   boolean flag=true;
	   for(int i=0;i<11;i++){
		   for(int j=0;j<15;j++){
			   if(plansza[j][i]==20)plansza[j][i]=0;
		   }
	   }
	   for(int i=0;i<11&&flag;i++){
		   for(int j=0;j<15&&flag;j++){
			   if(player.x<j*50+74&&player.y<i*50+74){
				   if(plansza[j][i]==2){
					   plansza[j][i]=20;
					   player.moc++;
				   }
				   if(plansza[j][i]==3){
					   plansza[j][i]=20;
					   player.bomby++;
				   }
				   if(plansza[j][i]==0){
					   plansza[j][i]=20;
				   }
				   flag=false;
			   }
		   }
	   }
   }
   
   
   public static void main(String[] args) { 
       frame = new JFrame(TITLE);
       frame.setContentPane(new Main());
       frame.pack();
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setLocationRelativeTo(null); // center the application window
       frame.setVisible(true);
       frame.setResizable(false);
       frame.setBackground(Color.black);
       
       System.out.println(TITLE);
       System.out.println();
   }
}