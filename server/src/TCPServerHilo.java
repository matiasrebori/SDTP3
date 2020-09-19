import java.net.*;
import java.io.*;

public class TCPServerHilo extends Thread{
	TCPMultiServer server;
	Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	BufferedReader stdIn;
	Message msg;

	String user1;
	
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

	public void sendMessage(String user, PrintWriter salida, Integer op) {
		//convierto la palabra en objeto Message

		msg.createMessage(user,op);
		//enviamos el objeto convertido a String en notacion JSON
		salida.println(msg.toJSON()) ;
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

	public String readMessage(BufferedReader e) throws IOException {
		String word =  e.readLine();
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
	String g;
	public void communication() throws IOException
	{
		String inputLine, outputLine;
		Integer operation,operation2;
		Integer bandera;
		
		while (true) {
			//leemos mensaje del cliente, viene en notacion JSON , lo guardamos en msg y extraemos el mensaje
			inputLine = readMessage();
			//g=inputLine;
			operation = msg.getOperation();

			if (operation.equals(4)) {
				break;
			} else if( operation.equals(2) ) {
				conectarllamada();
				//iniciarllamada();
				//conect();

			} else if(operation.equals(5)){
				conectar(inputLine);
			}else if(operation.equals(6)){
				connectToUse(inputLine);
			}else if(operation.equals(3))
			sendMessage(inputLine);
		}
	}
	
	public void setUser() throws IOException {
		sendMessage("Ingrese su usuario");
		int index = server.hilosClientes.indexOf(this);
		System.out.println("Index de este hilo en hilosClientes:" + index);
		String user = readMessage();
		user1=user;
		server.usuarios.add( index , user );
		System.out.println("Usuario:" + user + " a�adido en index:" + index);
		sendMessage("Usuario A�adido");
		//printMessage( server.usuarios.get(0) );
	}
	
	public void connectToUse(String user) throws IOException {

		Integer index = server.usuarios.indexOf(user);
		PrintWriter temp  = out;
		out = server.hilosClientes.get(index).out;
		server.hilosClientes.get(index).out = temp;
	
	}

	public void conectarllamada() throws IOException {

		sendMessage("ingrese el nombre del otro cliente");
		String user = readMessage();
		Integer index = server.usuarios.indexOf(user);
		PrintWriter temp;
		temp= server.hilosClientes.get(index).out;
		sendMessage(user1,temp,5);

	}
	public void conectar(String user) throws IOException {

		sendMessage("llamada de: "+user+"\n desea recibir la llamada? y/n");
		String c = readMessage();
		if(c.equals("y")) {
			Integer index = server.usuarios.indexOf(user);
			PrintWriter temp;
			temp = server.hilosClientes.get(index).out;
			sendMessage(user1, temp, 6);
		}
		else{
			sendMessage("llamada rechazada");
		}

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

