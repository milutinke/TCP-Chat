package raf.edu.rs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
	private static HashMap<Socket, String> connectedClients;
	
	static {
		connectedClients = new HashMap<Socket, String>();
	}
	
	public Server() throws IOException {
		@SuppressWarnings("resource")
		ServerSocket server = new ServerSocket(Settings.PORT);
		System.out.println("Server has started on the port: " + Settings.PORT);

		while (true) {
			new Thread(new ServerThread(server.accept())).start();
		}
	}

	public static void main(String[] args) throws IOException {
		new Server();
	}

	public static HashMap<Socket, String> getConnectedClients() {
		return connectedClients;
	}
}
