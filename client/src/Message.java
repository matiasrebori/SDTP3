import org.json.simple.*;
import org.json.simple.parser.*;
public class Message {
	Integer status;
	String statusDetail;
	Integer operation;
	String message;
	
	public Message() {
		
	}
	
	/**
	 * @param status estado del mensaje: 0 transaccion exitosa; -1 transanccion indeterminada; >=1 tira un codigo de error 
	 * @param statusDetail ok si no existe error, detalle del error si existe
	 * @param operation 1 ver clientes conectados, 2 iniciar llamada, 3 conversar, 4 terminar llamada
	 * @param message mensaje enviado entre clientes
	 */
	public Message(Integer status, String statusDetail, Integer operation, String message) {
		this.status = status;
		this.statusDetail = statusDetail;
		this.operation = operation;
		this.message = message;
	}

	/**
	 * Convierte un objeto Message a un String en formato JSON
	 * @param Mensaje 
	 * @return String JSON
	 */
	@SuppressWarnings("unchecked")
	public String toJSON( ) {
		JSONObject obj = new JSONObject();
		obj.put("status", getStatus() );
		obj.put("statusDetail", getStatusDetail() );
		obj.put("operation", getOperation() );
		obj.put("message", getMessage() );

		return obj.toJSONString();
	}

	/**
	 * Convierto un String en notacion JSON a un objeto Message
	 * @param String 
	 * @return Objeto Message
	 * @throws ParseException
	 */
	public void toMessage(String str) {

		try {
			//parsear
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(str.trim());
			JSONObject jsonObject = (JSONObject) obj;
			//extraer variables
			Long aux = (Long)jsonObject.get("status");
			Integer status = new Integer( aux.intValue() );
			String statusDetail = (String)jsonObject.get("statusDetail");
			aux = (Long)jsonObject.get("operation");
			Integer operation = new Integer( aux.intValue() );
			String message = (String)jsonObject.get("message");
			//asignar variables
			this.status = status;
			this.statusDetail = statusDetail;
			this.operation = operation;
			this.message = message;

		}catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void createMessage(String word) {
		if( word.equals("conectar") ) {
			status = 0;
			statusDetail = "ok";
			operation = 2;
			message = word;
		}else {
			status = 0;
			statusDetail = "ok";
			operation = 3;
			message = word;
		}
	}

	public void createMessage(String word , Integer operation ) {
		status = 0;
		statusDetail = "ok";
		operation = operation;
		message = word;

	}

	public void createMessage(Integer operation ) {
		status = 0;
		statusDetail = "ok";
		operation = operation;
		message = "";

	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStatusDetail() {
		return statusDetail;
	}

	public void setStatusDetail(String statusDetail) {
		this.statusDetail = statusDetail;
	}

	public Integer getOperation() {
		return operation;
	}

	public void setOperation(Integer operation) {
		this.operation = operation;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public static void main(String[] args) throws ParseException {
		Message m = new Message( (Integer)1, "ok", (Integer)3 ,"Hola skere como estas");
		String s = m.toJSON();
		System.out.println(s);
		Message me = new Message();
		me.toMessage(s);
		System.out.println(  me.getStatus() + " " + me.getStatusDetail()   + " " + me.getOperation()  + " " + me.getMessage() );
		me.createMessage("conectar");
		System.out.println(  me.getStatus() + " " + me.getStatusDetail()   + " " + me.getOperation()  + " " + me.getMessage() );
	}

}
