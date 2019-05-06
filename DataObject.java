import java.io.*;
import java.util.*;

public class DataObject implements Serializable{
	private String message;
    private MessageType type;
    private ArrayList<String> list;
    private String destination;

	DataObject(MessageType type){
		message = "";
        this.type = type;
	}

	public String getMessage(){
		return message;
	}

	public void setMessage(String inMessage){
		message = inMessage;
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

    public MessageType getType() {
        return type;
    }
}

enum MessageType {
    CONNECT, DISCONNECT, PUBLIC, PRIVATE, LIST,
}
