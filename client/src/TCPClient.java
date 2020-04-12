import java.io.*;
import java.net.*;

public class TCPClient {
	Socket serverSocket;
	ReadThread hiloEscucha;
	WriteThread hiloEscribe;
	PrintWriter out;
	BufferedReader in;
	
	

	/**
	 * Inicia la conexion y parametros requeridos
	 * @param ip
	 * @param port puerto de escucha del servidor
	 * @throws IOException
	 */
	public void start(String ip, int port) throws IOException {
		// iniciamos el serverSocket
		try {
			serverSocket = new Socket(ip, port);

		} catch (UnknownHostException e) {
			System.err.println("Host desconocido");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Error de I/O en la conexion al host");
			System.exit(1);
		}

		// buffer enviamos nosotros
		out = new PrintWriter(serverSocket.getOutputStream(), true);
		// buffer viene del servidor
		in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

	}
	
	/**
	 * Crea el hilo que escucha y que escribe y le pasa el cliente
	 */
	public void ejecutar() {
		hiloEscucha = new ReadThread(this);
		hiloEscucha.start();
		hiloEscribe = new WriteThread(this);
		hiloEscribe.start();
	
	}

	/**
	 * cierra todas las variables utilizadas
	 */
	public void stop() throws IOException {
		hiloEscucha.stop();
		hiloEscribe.stop();
		out.close();
		in.close();
		serverSocket.close();
		System.exit(0); //termina el progama
	}

	


	public static void main(String[] args) throws IOException {
		TCPClient client = new TCPClient();
		client.start("localhost", 6789);    //181.121.86.143	 181.126.221.195
		client.ejecutar();
		
	}

	

}
