import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class WriteThread extends Thread{
	TCPClient client;
	PrintWriter out;
	BufferedReader stdIn;

	/**
	 * @param TCPClient client
	 */
	public WriteThread( TCPClient client ) {
		super();
		this.client = client;
		out = client.out;
		// buffer para escribir por teclado
		stdIn = new BufferedReader(new InputStreamReader(System.in));
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
	 * envia mensaje al servidor
	 * @param word
	 */
	public void sendMessage(String word) {
		out.println(word);
	}
	
	public void run()
	{
		Message msg = new Message();
		String fromUser = null;		
		while ( true ) {
			try {
				// leer teclado
				fromUser = read();
				//crear objeto Message a partir de lo que dice el cliente
				msg.createMessage(fromUser);
				//convertir objeto a string notacion JSON
				fromUser = msg.toJSON();
				// enviamos al servidor
				sendMessage(fromUser);
				/*
				//if usuario escribe Bye se envio al servidor y cerramos los hilos Write, Read a traves de TCPClient
				if (fromUser.equals("Bye")) {
					stdIn.close();
					client.stop();
					break;
					
				}*/
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			
		}
	}

	
}
