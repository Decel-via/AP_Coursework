package server_groupchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {	
	private ServerSocket serverSocket;
	
	public Server(ServerSocket serverSocket) { 	//this is my constructor for the server
		this.serverSocket = serverSocket;
	}
	
	public void startServer() {  // this will keep the server running 
		
		try {
		
			while (!serverSocket.isClosed()) { // while server isn't closed
				
				Socket socket = serverSocket.accept(); // keep server running until client connects
				System.out.println("A new client has connected");
				ClientHandler clientHandler = new ClientHandler(socket); //new client created 
				
				Thread thread = new Thread(clientHandler); // each client is given a thread that runs independently 
				thread.start(); // starts thread
			}
			
		} catch (IOException e) {

		}
	}
	public void closeServerSocket() {  //closes socket if not in use
		try {
			if (serverSocket != null) { // serverSocket must not be null because if u is then we get a nullpointer exception if we close
				serverSocket.close(); // closes server socket
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	public static void main(String[] args) throws IOException {
		
		ServerSocket serverSocket = new ServerSocket( port: 1234);
		Server server = new Server(serverSocket);
		server.StartServer();
	}
	}

}