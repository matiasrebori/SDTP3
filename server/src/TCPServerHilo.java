import java.net.*;
import java.io.*;

public class TCPServerHilo extends Thread{
	TCPMultiServer server;
	Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	BufferedReader stdIn;
	Message msg;
	
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
		
		//Mensaje
		msg = new Message();
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
	 * convierte una palabra en objeto Message luego lo pasa a notacion JSON y lo envia al stream del cliente
	 * @param word
	 * 
	 */
	public void sendMessage(String word) {
		//convierto la palabra en objeto Message
		msg.createMessage(word);
		//enviamos el objeto convertido a String en notacion JSON
		out.println( msg.toJSON() ) ;
	}

	/**
	 * lee un String que es el mensaje enviado por el cliente en notacion JSON lo convierte a objeto y guarda en varable de clase msg 
	 * @return String Mensaje enviado por el cliente
	 * @throws IOException
	 */
	public String readMessage() throws IOException {
		String word =  in.readLine();
		msg.toMessage(word);
		return msg.getMessage();
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
		Integer operation;

		
		while (true) {
			//leemos mensaje del cliente, viene en notacion JSON , lo guardamos en msg y extraemos el mensaje
			inputLine = readMessage();
			//extraer codigo de operacion
			operation = msg.getOperation();
			if (operation.equals(4)) {
				break;
			} else if( operation.equals(2) ) {
				connectToUser();
				inputLine = readMessage();
				
			}
			//Enviamos al cliente
			sendMessage(inputLine);
		}
	}
	
	public void setUser() throws IOException {
		sendMessage("Ingrese su usuario");
		int index = server.hilosClientes.indexOf(this);
		System.out.println("Index de este hilo en hilosClientes:" + index);
		String user = readMessage();
		server.usuarios.add( index , user );
		System.out.println("Usuario:" + user + " a�adido en index:" + index);
		sendMessage("Usuario A�adido");
		//printMessage( server.usuarios.get(0) );
	}
	
	public void connectToUser() throws IOException {

		sendMessage("ingrese el nombre del otro cliente");
		String user = readMessage();
		int index = server.usuarios.indexOf(user);
		sendMessage("Escriba un mensaje para enviar al otro cliente");
		PrintWriter temp  = out;
		out = server.hilosClientes.get(index).out;
		server.hilosClientes.get(index).out = temp;
	
	}
	

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

