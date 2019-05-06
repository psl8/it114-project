import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    public static void main(String[] args ) {
        ArrayList<ChatHandler>handlers = new ArrayList<ChatHandler>();
        try {
            ServerSocket s = new ServerSocket(3000);
            for (;;) {
                Socket incoming = s.accept();
                new ChatHandler(incoming, handlers).start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

class ChatHandler extends Thread{
    private Socket incoming;
    ArrayList<ChatHandler>handlers;
    String name;
    ObjectInputStream in;
    ObjectOutputStream out;

    public ChatHandler(Socket i, ArrayList<ChatHandler>h) {
        incoming = i;
        handlers = h;
        handlers.add(this);
    }

    public void run() {
        try {
            in = new ObjectInputStream(incoming.getInputStream());
            out = new ObjectOutputStream(incoming.getOutputStream());

            boolean done = false;
            while (!done) {
                DataObject objIn = (DataObject)in.readObject();
                if (objIn == null) {
                    done = true;
                } else {
                    switch (objIn.getType()) {
                        case DISCONNECT:
                            done = true;
                            objIn.setMessage(name);
                            for (ChatHandler h : handlers) {
                                h.out.writeObject(objIn);
                            }
                            break;
                        case CONNECT:
                            ArrayList<String> names = new ArrayList<String>();
                            for (ChatHandler h : handlers) {
                                names.add(h.name);
                            }
                            DataObject listUpdate = new DataObject(MessageType.LIST);
                            listUpdate.setList(names);
                            out.writeObject(listUpdate);
                            name = objIn.getMessage();
                            for (ChatHandler h : handlers) {
                                h.out.writeObject(objIn);
                            }
                            break;
                        case PUBLIC:
                            String temp = objIn.getMessage();
                            objIn.setMessage(name + ": " + temp);
                            for (ChatHandler h : handlers) {
                                h.out.writeObject(objIn);
                            }
                            if (objIn.getMessage().trim().equals("BYE")) {
                                done = true;
                            }
                            break;
                        case PRIVATE:
                            String temp2 = objIn.getMessage();
                            objIn.setMessage(name + ": " + temp2);
                            out.writeObject(objIn);
                            for (ChatHandler h : handlers) {
                                if (h.name.equals(objIn.getDestination()))
                                    h.out.writeObject(objIn);
                            }
                            if (objIn.getMessage().trim().equals("BYE")) {
                                done = true;
                            }
                            break;
                    }
                }
            }
            incoming.close();
        } catch (IOException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        } finally {
            handlers.remove(this);
        }
    }
}
