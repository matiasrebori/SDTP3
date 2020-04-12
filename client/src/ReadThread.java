import java.io.BufferedReader;
import java.io.IOException;

public class ReadThread extends Thread{
	TCPClient client;
	BufferedReader in;
	
	public ReadThread( TCPClient client ) {
		this.client = client;
		in = client.in;
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
	 * imprime en la consola el mensaje recibido del servidor
	 * @param word
	 * 
	 */
	public void printMessage(String word) {
		System.out.println("Servidor: " + word);
	}
	
	/**
	 *Este hilo se cierra si el cliente escribe bye en WriteThread y se maneja en la clase TCPClient
	 */
	public void run() {
		String fromServer;
		try {
			while ( true ) {
				//leer del buffer
				fromServer = readMessage();
				//imprimir en pantalla
				printMessage(fromServer);
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
