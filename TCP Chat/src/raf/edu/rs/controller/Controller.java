package raf.edu.rs.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import raf.edu.rs.components.MainComponent;

public class Controller {
	private static Controller instance;
	private Socket client = null;
	private boolean closed = false;

	private Controller() {
	}

	public static Controller getInstance() {
		if (instance == null)
			instance = new Controller();

		return instance;
	}

	public void showWindow() {
		MainComponent.show();
	}

	public void connect(String ip, int port) {
		if (client != null)
			return;

		try {
			client = new Socket(ip, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(String text) {
		if (client == null)
			return;

		try {
			PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
			out.println(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void receiveWelcomeMessage() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

			if (in.readLine().equalsIgnoreCase("clear"))
				MainComponent.getTextArea().clear();

			MainComponent.getTextArea().appendText(in.readLine() + "\n");
			MainComponent.getTextArea().appendText(in.readLine() + "\n");
			MainComponent.getTextArea().appendText(in.readLine() + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void listen() {
		if(client == null)
			return;
		
		new Thread() {
			@SuppressWarnings("unused")
			@Override
			public void run() {
				while (!isClosed() && client != null && client.isConnected() && !client.isClosed()) {	
					if(closed)
						return;
					
					try {
						BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
						
						if(in == null)
							break;
						
						String message = in.readLine();
						
						if(message == null)
							break;

						if (message.equalsIgnoreCase("clear")) {
							MainComponent.getTextArea().clear();
							continue;
						}

						if (message.equalsIgnoreCase("disconnect") || message.equalsIgnoreCase("exit") || message.equalsIgnoreCase("quit")) {
							break;
						} else {
							MainComponent.getTextArea().appendText(message + "\n");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void close() {
		if (client == null || closed)
			return;

		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}
}
