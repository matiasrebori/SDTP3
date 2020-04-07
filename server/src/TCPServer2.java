import java.net.*;
import java.io.*;

public class TCPServer2 {
	ServerSocket serverSocket;
	Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	BufferedReader stdIn;

	/**
	 * Inicia la conexion y parametros requeridos
	 * @param port puerto de escucha
	 * @throws IOException
	 */
	public void start(int port) throws IOException {
		// iniciamos el ServerSocket
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("No se puede abrir el puerto: " + port);
			System.exit(1);
		}
		// iniciamos el ClientSocket
		try {
			clientSocket = serverSocket.accept(); // escucha por una conexion y la acepta, operacion bloqueante
		} catch (IOException e) {
			System.err.println("Fallo el accept().");
			System.exit(1);
		}
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
	public void stop() throws IOException {
		in.close();
		out.close();
		stdIn.close();
		clientSocket.close();
		serverSocket.close();
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
			}
			//leemos por teclado y enviamos al cliente
			outputLine = read();
			sendMessage(outputLine);
		}
	}

	public static void main(String[] args) throws IOException {
		TCPServer2 server = new TCPServer2();
		server.start(6666);
		server.sendMessage("Bienvenido");
		server.communication();
	}
}
