
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class TCPMultiServer {

	//variables compartidas
	boolean listening = true;
	List<TCPServerHilo> hilosClientes; //almacenar los hilos (no se utiliza en el ejemplo, se deja para que el alumno lo utilice)
	List<String> usuarios; //almacenar una lista de usuarios (no se utiliza, se deja para que el alumno lo utilice)

    public void ejecutar() throws IOException {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(17015);
        } catch (IOException e) {
            System.err.println("No se puede abrir el puerto: 17015.");
            System.exit(1);
        }
        System.out.println("Puerto abierto: 17015.");

        while (listening) {
        	
        	TCPServerHilo hilo = new TCPServerHilo(serverSocket.accept(), this);
            hilosClientes.add(hilo);
            //se crea un nuevo elemento en el array de usuarios cada vez que se inicia un hilo TCPServerHilo
            usuarios.add("");
            hilo.start();
            
           
        }

        serverSocket.close();
    }
    
    public static void main(String[] args) throws IOException {
    	
    	TCPMultiServer tms = new TCPMultiServer();
    	
    	tms.hilosClientes = new ArrayList<TCPServerHilo>();
    	tms.usuarios = new ArrayList<String>();
    	
    	tms.ejecutar();
    	
    }
}

