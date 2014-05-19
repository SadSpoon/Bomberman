import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Client extends Thread{
	private DatagramSocket socket;
 	String message;
 	int port,index;
 	InetAddress host;
 	public double x,y;
 	String[] dane;
 	public long ping;
 	long picTime;
 	boolean win=false;
 	
 	public boolean transmissionTimeout = false;
 	
	public Client(String Host, int Port){
		
		this.port=Port;
		
		try {
			socket = new DatagramSocket(port);
			this.host=InetAddress.getByName(Host);
		}
		catch (SocketException e) {System.out.println("Wyst�pi� problem z socketem na porcie "+Port);} 
		catch (UnknownHostException e) {System.out.println("Nie odnaleziono hosta: "+Host);}
	}
	
	public void run()
	{
		try{
			
			byte[] packetData = new byte[1024];
			DatagramPacket packet = new DatagramPacket(packetData, packetData.length);
			String input;
			
			while(!transmissionTimeout){
				
				try{
					socket.setSoTimeout(5000);
					socket.receive(packet);
					input = new String(packet.getData());
					System.out.println("Received packet: "+input+" ");
					dane=input.split(" ");
					
					
				}catch(SocketTimeoutException x){
					System.out.println("Wystapil blad przy otrzymywaniu pakietu od serwera");
					transmissionTimeout = true;
				}


				

				switch (Integer.parseInt(dane[0])){
				case 1:
					break;
				case 2:
					int temp =1;
					for(int i=0;i<15;i++){
						   for(int j=0;j<11;j++){
								   if(Integer.parseInt(dane[temp])==0 || Integer.parseInt(dane[temp])==1)Main.plansza[i][j]=Integer.parseInt(dane[temp]);
								   if(Integer.parseInt(dane[temp])==2){
									   Main.plansza[i][j]=1;
									   Main.bonusy[i][j]=2;
								   }
								   if(Integer.parseInt(dane[temp])==3){
									   Main.plansza[i][j]=1;
									   Main.bonusy[i][j]=3;
								   }
								   temp++;
						   }
					}
					   Main.plansza[0][0]=0;
					   Main.plansza[0][1]=0;
					   Main.plansza[1][0]=0;
					   
					   Main.plansza[13][0]=0;
					   Main.plansza[14][1]=0;
					   Main.plansza[14][0]=0;
					   
					   Main.plansza[0][9]=0;
					   Main.plansza[0][10]=0;
					   Main.plansza[1][10]=0;
					   
					   Main.plansza[13][10]=0;
					   Main.plansza[14][10]=0;
					   Main.plansza[14][9]=0;
					   
					   Main.bonusy[0][0]=0;
					   Main.bonusy[0][1]=0;
					   Main.bonusy[1][0]=0;
					   
					   Main.bonusy[13][0]=0;
					   Main.bonusy[14][1]=0;
					   Main.bonusy[14][0]=0;
					   
					   Main.bonusy[0][9]=0;
					   Main.bonusy[0][10]=0;
					   Main.bonusy[1][10]=0;
					   
					   Main.bonusy[13][10]=0;
					   Main.bonusy[14][10]=0;
					   Main.bonusy[14][9]=0;
					   for(int i=1;i<=13;i+=2){
							  for(int j=1;j<=9;j+=2){
								  Main.plansza[i][j]=4;
							  }
						   }
					   sendData("3 -100 -100 0 0 0 0 0 0 0 0 0".getBytes());
					break;
				case 3:
					if(Main.polaczono==false)Main.polaczono=true;
					//wspolrzedne przeciwnika
					x = Integer.parseInt(dane[1]);
					y = Integer.parseInt(dane[2]);
					//zmiana w mapie jesli 1
					if(Integer.parseInt(dane[3])==1){
						Main.boombMoc[Integer.parseInt(dane[5])][Integer.parseInt(dane[6])]=Integer.parseInt(dane[4]);
						Main.plansza[Integer.parseInt(dane[5])][Integer.parseInt(dane[6])]=12;
						Main.boombmap[Integer.parseInt(dane[5])][Integer.parseInt(dane[6])]=System.nanoTime();//w przyszlosci -Ping
					}
					picTime	= Long.parseLong(dane[7]);
					ping = (System.nanoTime() - Long.parseLong(dane[8]))/2000000;
					break;
				case 4:
					win = true;
					break;
				}
			}
		}
		catch(Exception d){d.printStackTrace();}
	}
	
	public void sendData(byte[] data){
		DatagramPacket packet = new DatagramPacket(data, data.length, host, port);
		try {
			System.out.println("Sending data: "+new String(packet.getData())+" ");
			socket.send(packet);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}