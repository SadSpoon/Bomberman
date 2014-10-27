import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Server extends Thread{
	private DatagramSocket socket;
 	String message;
 	int port,index;
 	public double x,y;
 	String[] dane;
 	public long ping;
 	long picTime;
 	InetAddress IP;
 	boolean win = false;
	Server(int Port){
		this.port=Port;
		try {
			socket = new DatagramSocket(Port);
		}
		catch (SocketException e) {e.printStackTrace();} 
	}
	
	public void run()
	{
		try{
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			while(true){
				socket.receive(packet);
				String input = new String(packet.getData());
				//System.out.println("Recived: "+input);
				if(IP==null){
					IP = packet.getAddress();
				}
				if(packet!=null){
					dane=input.split(" ");
					switch (Integer.parseInt(dane[0])){
					case 1:
						sendData(("2 "+Main.mapaS).getBytes());
						Main.sendMap=true;
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
						   sendData("3 0 0 0 0 0 0 0 0".getBytes());
						break;
					case 3:
						if(Main.polaczono==false)
							{
								Main.polaczono=true;
								Main.sendMap=false;
							}
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
						win=true;
						break;
					}
				}

			}
		}
		catch(IOException e){}
	}
	
	public void sendData(byte[] data){
		DatagramPacket packet;
		try {
			packet = new DatagramPacket(data, data.length, IP, port+1);
			socket.send(packet);
			//System.out.println("Sending data: "+new String(packet.getData())+" ");
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}