import java.io.*;
import java.util.*;

public class DataObject implements Serializable{
    private String username;
	private String message;
    MessageType type;
    private ArrayList<String> list;
    private String destination;

	DataObject(String username){
		message = "";
        this.username = username;
        type = MessageType.PUBLIC;
	}

	DataObject(String username, MessageType type){
		message = "";
        this.username = username;
        this.type = type;
	}

	public String getMessage(){
		return message;
	}

	public void setMessage(String inMessage){
		message = inMessage;
	}

	public String getUsername(){
		return username;
	}

	public void setDestination(String dest){
		destination = dest;
	}

	public String getDestination(){
		return destination;
	}

    public void setList(ArrayList<String> list) {
        this.list = list;
    }

    public ArrayList<String> getList() {
        return list;
    }
}

enum MessageType {
    CONNECT, DISCONNECT, PUBLIC, PRIVATE, LIST,
}
