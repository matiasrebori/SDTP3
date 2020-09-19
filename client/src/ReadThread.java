import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ReadThread extends Thread{
	TCPClient client;
	BufferedReader in;
	PrintWriter out;
	
	public ReadThread( TCPClient client ) {
		this.client = client;
		in = client.in;
		out=client.out;
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
	Message msg1 = new Message();
	public void sendMessage1(String user,Integer op) {
		//convierto la palabra en objeto Message

		msg1.createMessage(user,op);
		//enviamos el objeto convertido a String en notacion JSON
		out.println( msg1.toJSON() ) ;
	}
	public void run() {
		String fromServer;
		Message msg = new Message();
		Integer op;
		try {
			while ( true ) {
				//leer del buffer
				fromServer = readMessage();
				//convertir de String notacion JSON a objeto Message
				msg.toMessage(fromServer);
				//extraer el mesaje para imprimir al cliente
				fromServer= msg.getMessage();
				op= msg.getOperation();
				if(op!=5 && op!=6) {
					//imprimir en pantalla
					printMessage(fromServer);
				}else{
					//printMessage("ola");
					sendMessage1(fromServer,op);
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
