package raf.edu.rs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ServerThread implements Runnable {
	private Socket clientSocket;

	public ServerThread(Socket socket) {
		this.clientSocket = socket;
	}

	@Override
	public void run() {
		BufferedReader in;
		PrintWriter out;

		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

			String input;

			if (!Server.getConnectedClients().containsKey(clientSocket) && !clientSocket.isClosed()
					&& clientSocket.isConnected()) {
				// Birsemo konzolu trenutnog klijenta
				out.println("clear");

				// Ispisujemo da mora de se prijavi i uputstvo za prijavu
				out.println("Welcome to our server :)");
				out.println("Please login in, in order to chat with others!");
				out.println("All that you need is to type: /login YourNameHere");
			}

			// Loopujemo sve dok klijent ne posalje Disconnect ili ne prekine/izgubi vezu sa serverom
			while (true) {
				input = in.readLine();

				if (input == null)
					break;

				if (input.equalsIgnoreCase("disconnect") || input.equalsIgnoreCase("exit")
						|| input.equalsIgnoreCase("quit"))
					break;

				// Prijava klijenta
				if (!Server.getConnectedClients().containsKey(clientSocket)) {
					// Ako klijent salje nesta sto nije poruka za prijavu, onda ne prosledjujemo
					// drugim povezanim klijentima i preskacemo ciklus u petlji
					// Takodje saljemo mu poruku da mora da se prijavi da bi mogao/la da razgovara
					// sa drugima
					if (!SimpleTokenizer.hasToken(input, "/login")) {
						// Birsemo konzolu trenutnog klijenta
						out.println("clear");

						// Ispisujemo da mora de se prijavi i uputstvo za prijavu
						out.println("Please login in, in order to chat with others!");
						out.println("All that you need is to type: /login YouNameHere");
						continue;
					}

					// Uzimamo ime klijenta
					String name = SimpleTokenizer.getValue("/login", input);

					// Proveravamo da li je manje od 3 karaktera ili vece od 32 karaktera
					// Ako je nesta od ta 2 slucaja saljemo poruku da promeni ime
					if (name.length() < 3 || name.length() > 32) {
						out.println("The name can not be shorter than 3 or longer then 32 characters!");
						continue;
					}

					// Ako su svi uslovi za prijavu zadovoljeni, dodajemo klijenta u listu povezanih
					// i prijavljenih klijenata
					Server.getConnectedClients().put(clientSocket, name);

					// Brisemo sadrzaj konzole klijenta i prosledjujemo mu poruku da se uspesno prijavio/la
					out.println("clear");
					out.println("You have successsfully logged in!");

					// Saljemo svakom povezanom klijentu poruku da se klijent povezao i prijavio
					Server.getConnectedClients().forEach((client, n) -> {
						// Ako je trenutni klijent kroz kojeg loop-ujemo povezan i ako nije trenutni
						// klijent koji se prijavio saljemo poruku
						if (!client.isClosed() && client.isConnected() && clientSocket != client) {
							PrintWriter currentClientOut = null;

							try {
								currentClientOut = new PrintWriter(new OutputStreamWriter(client.getOutputStream()),
										true);

								if (currentClientOut != null)
									currentClientOut.println("Client " + Server.getConnectedClients().get(clientSocket)
											+ " has connected and logged in.");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});

					System.out.println("Client " + Server.getConnectedClients().get(clientSocket) + " has logged in.");
					continue;
				}

				// Proveravamo da li je korisnik poslao praznu poruku
				// Ako jeste, onda preskacemo sledece radnje i ciklus u petlji
				if (input.length() <= 0)
					continue;

				// Proveravamo ako klijent pokusava da se prijavi, a vec je prjavljen
				if (SimpleTokenizer.hasToken(input, "/login")) {
					out.println("You are already logged in!");
					continue;
				}

				// Ako korisnik zahteva da obrise ekran
				// Saljemo mu komandu clear
				if (SimpleTokenizer.hasToken("/clear", input)) {
					out.println("clear");
					continue;
				}

				// Formatiramo poruku
				String message = "[" + dateFormat.format(Calendar.getInstance().getTime()) + "] "
						+ Server.getConnectedClients().get(clientSocket) + ": " + input;

				// Ispisujemo poruku u konzoli servera
				System.out.println(message);

				// Saljemo svakom povezanom klijentu poruku koju je klijent prosledio
				Server.getConnectedClients().forEach((client, name) -> {
					// Ako je trenutni klijent kroz kojeg loop-ujemo povezan i ako nije prekinuo
					// vezu, saljemo poruku koju je klijent prosledio
					if (!client.isClosed() && client.isConnected()) {
						PrintWriter currentClientOut = null;

						try {
							currentClientOut = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);

							if (currentClientOut != null)
								currentClientOut.println(message);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}

			// Saljemo svakom povezanom klijentu poruku da je klijent prekinuo vezu ako je
			// jos povezan na server i ako je ukucao Disconnect
			if (!clientSocket.isClosed() && clientSocket.isConnected()) {
				Server.getConnectedClients().forEach((client, name) -> {
					// Ako je trenutni klijent kroz kojeg loop-ujemo povezan i ako nije trenutni
					// klijent koji gasi vezu saljemo poruku
					if (!client.isClosed() && client.isConnected() && !client.equals(clientSocket)) {
						PrintWriter currentClientOut = null;

						try {
							currentClientOut = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);

							if (currentClientOut != null)
								currentClientOut.println("Client " + Server.getConnectedClients().get(clientSocket)
										+ " has diconnected.");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}

			// Ispisujemo poruku u konzoli servera da se klijent odjavio/la
			if (Server.getConnectedClients().containsKey(clientSocket)) {
				System.out.println("Client " + Server.getConnectedClients().get(clientSocket) + " has logged out.");
			}

			// Uklanjamo klijenta iz liste povezanih klijenata
			Server.getConnectedClients().remove(clientSocket);

			// Zatvaramo vezu
			if (clientSocket != null)
				clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
