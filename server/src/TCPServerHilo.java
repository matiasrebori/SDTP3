import java.net.*;
import java.io.*;

import java.util.logging.*; //INCLUIMOS LA LIBRERIA CORRESPONDIENTE A LOS LOGS


public class TCPServerHilo extends Thread{
	TCPMultiServer server;
	Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	BufferedReader stdIn;
	Message msg;

	String user1; //hilo del cliente actual
	String user2 = null; //hilo del cliente al que se conecta
	Boolean disponible; //true si no esta en una llamada , false de lo contrario

	//LOG OBJECTS
	private final static Logger logger;

	static {
		logger = Logger.getLogger("LogTCPServerHilo");
	}

	// re realiza la conexion con el cliente.
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
		//String en formato JSON recibido
		String word =  in.readLine();
		//Convertir de string JSON a objeto Message
		msg.toMessage(word);
		return msg.getMessage();
	}

	//cambia el estado de variable de clase
	public void setDisponible(boolean b){
		this.disponible=b;
	}
	//devuelve si esta conectado o no
	public boolean getDisponible() {
		return this.disponible;
	}

	public void communication() throws IOException
	{
		String inputLine;
		Integer operation;

		boolean disp;
		while (true) {
			//leemos mensaje del cliente, viene en notacion JSON , lo guardamos en msg y extraemos el mensaje
			try {
				//se lee el string JSON
				inputLine = readMessage();
				//se extrae la operacion del msg
				operation = msg.getOperation();
				//verificar si no esta conectado con otro cliente
				disp = getDisponible();

				//operacion listar usuarios
				if (operation.equals(1) && disp) {
					listUsers();
				//realizar llamada
				} else if (operation.equals(2) && disp) {
					conectarllamada();
				//recibir llamada
				} else if (operation.equals(5)) {
					conectar(inputLine);
				//conecta los clientes
				} else if (operation.equals(6)) {
					connectToUser(inputLine);
				//desconecta los clientes
				} else if (operation.equals(4) && !disp) {
					desconectarLlamada();
				//si es true esta en llamada y conversan por medio de la op 3
				} else if (!disp)
					sendMessage(user1 + ": " + inputLine);
			//si ocurre un error en la lectura de algun mensaje recibido se cierra el hilo
			}catch(IOException e1){
				disp = getDisponible();
				//si en el error esta conectado , desconectamos
				if (!disp) {
					desconectarLlamada();
				}
				//se remueve el cliente de las variables de servidor
				server.hilosClientes.remove(this);
				server.usuarios.remove(user1);
				//cierra las variables de clase
				close();
				//se interrunpe el hilo
				Thread.currentThread().interrupt();
				System.out.println("Usuario " + user1 +" se ha desconectado");
				break;
			}
		}
	}
	// envia lista de usuarios
	public void listUsers() {
	String lista="";
		sendMessage("Lista de Usuarios conectados");

		for (String i : server.usuarios) {
			if(!i.equals(""))
				lista = "* "+i+"\n"+lista;
		}

		sendMessage(lista);
	}
	//agrega nombre de usuario al hilo
	public void setUser() throws IOException {
		sendMessage("Ingrese su usuario");
		String user;
		boolean existe=true;
		do {
			user = readMessage();
			//true si el nombre de usuario ya existe
			existe = server.usuarios.contains(user);
			if (!existe) {
				int index = server.hilosClientes.indexOf(this);
				System.out.println("Index de este hilo en hilosClientes:" + index);
				user1 = user;
				server.usuarios.add(index, user);
				System.out.println("Usuario:" + user + " añadido en index:" + index);
				sendMessage("Usuario Añadido");
				disponible = true;
				sendMessage("Lista de comandos:\n" +
						"1) 'lista' para ver a los usuarios conectados\n" +
						"2) 'llamar' si desea realizar una llamada a un usuario conectado\n" +
						"3) 'terminar' si desea finalizar una llamada\n");
			} else {
				sendMessage("el usuario " + user + " ya existe, ingrese otro nombre");
			}
		}while(existe);
	}
	//envia peticion de llamada al cliente 2
	public void conectarllamada() throws IOException {

		sendMessage("ingrese el nombre del otro cliente");
		String user = readMessage();
		//verificamos si existe el cliente
		boolean existe= server.usuarios.contains(user);
		//si no te llamas a vos mismo y existe el cliente 2
		if(!user.equals(user1) && existe) {
			Integer index = server.usuarios.indexOf(user);
			PrintWriter temp;
			boolean disp;
			disp = server.hilosClientes.get(index).getDisponible();
			if (disp) {
				user2 = user;
				temp = server.hilosClientes.get(index).out;
				sendMessage("llamando a " + user + "...");
				sendMessage(user1, temp, 5);
			} else {
				sendMessage("el usuario: " + user + " actualmente esta en una llamada");
			}
		}else if(user.equals(user1)){

			sendMessage("no puede llamarse a si mismo" ); // para que no se llame a si mismo
		}else{
			sendMessage("el usuario al que quiere llamar no existe");// para que no llame a alguien que no existe
		}
	}
	//recibe peticion de llamada. acepta o rechaza la llamada , ejecuta el cliente 2
	// se realiza el registro de eventos de llamadas
	public void conectar(String user) throws IOException {

		sendMessage("llamada de "+user+"\n desea recibir la llamada? y/n");

		boolean bandera = true;
		Integer index = server.usuarios.indexOf(user);
		PrintWriter temp;
		temp = server.hilosClientes.get(index).out;
		do {
			String c = readMessage();
			if (c.equals("y")) {

				user2 = user;
				sendMessage(user1, temp, 6);
				bandera = false;

				LogManager.getLogManager().reset(); // RESETEAR TODOS LOS LOG MANAGER QUE EXISTAN
				logger.setLevel(Level.ALL); //PASAR TODOS LOS NIVELS DE EVENTOS AL LOG

				ConsoleHandler consoleHandler = new ConsoleHandler(); // Creamos un manejador de logs para consola
				consoleHandler.setLevel(Level.INFO);				// seteamos el nivel para dicho manejador
				logger.addHandler(consoleHandler); 					// agreamos el manejador a nuestro logger

				try {
					FileHandler fileHandler = new FileHandler("logLlamadas.log", true);
					fileHandler.setFormatter(new SimpleFormatter());
					logger.addHandler(fileHandler);
				} catch (IOException e) {
					//imprime en consola que hubo un error al crear el archivo de registro
					logger.log(Level.SEVERE, "Archivo log no funiciona", e);
				}
				//escribe en el archivo
				logger.log(Level.INFO, " [USUARIO "+ user1+", IP "+ clientSocket.getInetAddress().toString().substring(1)+
						", PUERTO "+clientSocket.getPort() + "] INICIÓ LLAMADA CON [USUARIO "+user2+", IP "+
						server.hilosClientes.get(index).clientSocket.getInetAddress().toString().substring(1)+", PUERTO "+
						server.hilosClientes.get(index).clientSocket.getPort()+"]"); //log de conexion establecida
			//si rechaza la llamda
			} else if (c.equals("n")) {
				sendMessage("llamada rechazada");
				sendMessage(user1+" a rechazado tu llamada",temp,108);
				bandera = false;
			} else{
				sendMessage("por favor ingrese una opción valida: y/n");
			}
		}while( bandera  );

	}
	//conecta a los clientes
	public void connectToUser(String user) throws IOException {

		Integer index = server.usuarios.indexOf(user);
		setDisponible(false);// marcar como ocupado el hilo
		server.hilosClientes.get(index).setDisponible(false);// marcar como ocupado el otro hilo
		sendMessage("conectado con "+user);
		sendMessage("conectado con "+user1,server.hilosClientes.get(index).out,108);
		PrintWriter temp  = out;
		out = server.hilosClientes.get(index).out;
		server.hilosClientes.get(index).out = temp;
	
	}
	//desconecta los clientes
	public void desconectarLlamada() throws IOException {

		Integer index = server.usuarios.indexOf(user2);
		PrintWriter temp  = server.hilosClientes.get(index).out;	
		server.hilosClientes.get(index).out = out;
		out = temp;
		setDisponible(true);// marcar como desocupado el hilo
		server.hilosClientes.get(index).setDisponible(true);// marcar como desocupado el otro hilo
		sendMessage("Llamada finalizada con "+user2);
		sendMessage("Llamada finalizada con "+user1,server.hilosClientes.get(index).out,108);
		user2 = null;
	
	}
	


	public void run() {
		try {
			//agrega usuario
			setUser();
			//comunicacion
			communication();

		} catch (IOException e1) {
			System.out.println(" error en metodo run ");
		}
	}
	
}

