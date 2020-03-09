import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
	Socket socket1,socket2,socket3;
	
	BufferedOutputStream mbos ;
	BufferedReader mbis ;
	BufferedReader mbis2;
	String tiredString=null;

	public Server() {
	}
	
	public static void main(String[] args) {
		Server aServer = new Server();
		aServer.startserver();
	}
	
	
	public void startserver() {
		
		try (ServerSocket serverSocket = new ServerSocket(10222)) {

			System.out.println("Server started at: " + serverSocket);

			while (true) {
				System.out.println("Waiting for a connection...");
				Socket activeSocket = serverSocket.accept();
				System.out.println("Received a connection from " + activeSocket);
				System.out.println("已连接"+activeSocket.toString());
				Runnable runnable = () -> handleClientRequest(activeSocket);
				new Thread(runnable).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handleClientRequest(Socket socket) {
		
		try {
			BufferedReader bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String roleString=null;
			roleString=bis.readLine();
			if(roleString==null)
				return;
			if(roleString.equals("A")) {
				socket1=socket;
				mbis = bis;
				System.out.println("A :"+socket.toString());
				
				String inString=null;
				while((inString=mbis.readLine())!=null) {
					System.out.println("in :"+inString);
					if(mbos!=null) {
						//if(tiredString!=null)
						 	//inString=inString.replace("end", tiredString+",end");
						final String ouString=new String(inString );
						new Thread(()->{
							try {
								
								mbos.write((ouString+"\n").getBytes());
								mbos.flush();
								System.out.println("out:"+ouString);
							} catch (IOException e) {
								mbos=null;
								e.printStackTrace();
							}
						}).start();
						
						

					}
				}
				
			}
			else if(roleString.equals("B")) {
				socket2=socket;
				System.out.println("B :"+socket.toString());
				mbos=new BufferedOutputStream(socket.getOutputStream());
			}
			else if(roleString.equals("A2")){
				System.out.println("A2 :"+socket.toString());
				socket3=socket;
				mbis2 = bis;
				while((tiredString=mbis2.readLine())!=null) {
					System.out.println("tired: "+tiredString);
				}
			}
					
			
		} catch (Exception e) {
			System.out.println("掉线:"+socket.toString());


			e.printStackTrace();
		} 

	}

}
