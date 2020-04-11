import java.net.*;
import java.io.*;

public class TCPServerHilo extends Thread{
	String mensaje;
	TCPMultiServer server;
	Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	BufferedReader stdIn;
	
	public TCPServerHilo( Socket socket, TCPMultiServer servidor) throws IOException {
        super("TCPServerHilo");
        clientSocket = socket;
		server = servidor;
		
		// buffer enviamos nosotros
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		// buffer recibimos del cliente
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		// buffer para escribir por teclado
		stdIn = new BufferedReader(new InputStreamReader(System.in));
	}


	/**
	 * cierra todas las variables utilizadas
	 */
	public void close() throws IOException {
		in.close();
		out.close();
		stdIn.close();
		clientSocket.close();
	}

	/**
	 * envia un mensaje al cliente
	 * @param word
	 * 
	 */
	public void sendMessage(String word) {
		out.println(word);
	}

	/**
	 * lee un mensaje enviado por el cliente
	 * @return Mensaje enviado por el cliente
	 * @throws IOException
	 */
	public String readMessage() throws IOException {
		return in.readLine();
	}

	/**
	 * lee el teclado
	 * @return 
	 * @throws IOException
	 */
	public String read() throws IOException {
		return stdIn.readLine();
	}
	
	/**
	 * imprime en la consola el mensaje recibido del cliente
	 * @param word
	 * 
	 */
	public void printMessage(String word) {
		System.out.println("Mensaje recibido: " + word);
	}
	/**
	 * se lee y envia mensajes con el cliente, se mantiene con un while true
	 * @throws IOException
	 */
	public void communication() throws IOException
	{
		String inputLine, outputLine;
		

		
		while (true) {
			//leemos mensaje del cliente
			inputLine = readMessage();
			//imprimimos el mensaje en consola
			printMessage(inputLine);
			if (inputLine.equals("Bye")) {
				break;
			} else if( inputLine.equals("conectar") ) {
				connectToUser();
			}
			//leemos por teclado y enviamos al cliente
			outputLine = read();
			sendMessage(outputLine);
		}
	}
	
	public void setUser() throws IOException {
		sendMessage("Ingrese su usuario");
		int index = server.hilosClientes.indexOf(this);
		System.out.println("Index de este hilo en hilosClientes:" + index);
		String user = readMessage();
		server.usuarios.add( index , user );
		System.out.println("Usuario:" + user + " añadido en index:" + index);
		sendMessage("Usuario Añadido");
		//printMessage( server.usuarios.get(0) );
	}
	
	public void connectToUser() throws IOException {
		sendMessage("ingrese el nombre del otro cliente");
		String user = readMessage();
		int index = server.usuarios.indexOf(user);
		sendMessage("Escriba un mensaje para enviar al otro cliente");
		String word = readMessage();
		server.hilosClientes.get(index).out.println(word);
	}
	
	/**
	 *
	 */
	public void run() {
		try {
			//sendMessage("Bienvenido");
			setUser();
			communication();
			close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
