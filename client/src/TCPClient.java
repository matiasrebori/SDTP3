import java.io.*;
import java.net.*;

public class TCPClient  implements Runnable{
	Socket serverSocket;
	PrintWriter out;
	BufferedReader in;
	BufferedReader stdIn;
	
	int cortar=0; 

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
		// buffer para escribir por teclado
		stdIn = new BufferedReader(new InputStreamReader(System.in));
	}

	/**
	 * cierra todas las variables utilizadas
	 */
	public void stop() throws IOException {
		out.close();
		in.close();
		stdIn.close();
		serverSocket.close();
	}

	/**
	 * envia mensaje al servidor
	 * @param word
	 */
	public void sendMessage(String word) {
		out.println(word);
	}

	/**
	 * lee mensaje enviado por el servidor
	 * @return mensaje del servidor
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
	 * imprime en la consola el mensaje recibido del servidor
	 * @param word
	 * 
	 */
	public void printMessage(String word) {
		System.out.println("Servidor: " + word);
	}
	/**
	 * se lee y envia mensajes con el servidor
	 * @throws IOException
	 */
	public void communication() throws IOException
	{
		String /*fromServer,*/fromUser;
		
		Thread mihilo=new Thread(this); // hilo que se encarga de escuchar
		mihilo.start();
		
		
		
		//cuidado con la linea de abajo xd
		while ( /*( fromServer = readMessage() ) != null*/cortar==0) {
			/*printMessage(fromServer);
			if (fromServer.equals("Bye")) {
				break;
			}
			// leer teclado*/
			fromUser = read();
			// enviamos al servidor
			sendMessage(fromUser);

			
		}
	}

	public static void main(String[] args) throws IOException {
		TCPClient client = new TCPClient();
		client.start("181.126.221.195", 6789);    //181.121.86.143	 181.126.221.195
		client.communication();
		
		client.stop();
	}

	@Override
	public void run() {
		String fromServer/*,fromUser*/;
		//cuidado con la linea de abajo xd
		try {
			while ( /*( fromServer = readMessage() ) != null*/true) {
				fromServer = readMessage();
				printMessage(fromServer);
				if (fromServer.equals("Bye")) {
					
					cortar=1;
					break;
				}
				/* leer teclado
				fromUser = read();
				// enviamos al servidor
				sendMessage(fromUser);
				*/
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
