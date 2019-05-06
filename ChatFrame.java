import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class ChatFrame extends Frame {
    public ChatFrame() {
        setSize(500, 500);
        setTitle("Chat Frame");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        add(new ChatPanel(this), BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        new ChatFrame();
    }
}

class ChatPanel extends Panel implements ActionListener, Runnable {
    TextArea ta;
    TextField tf;
    Button connect, disconnect;
    Thread thread;
    Socket s;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    DataObject d1,d2;
    String username;
    boolean connected;
    ChatFrame frame;
    java.awt.List list;

    public ChatPanel(ChatFrame frame) {
        setLayout(new BorderLayout());
        tf = new TextField();
        tf.addActionListener(this);
        ta = new TextArea();
        add(tf, BorderLayout.NORTH);
        Panel p1 = new Panel();
        p1.setLayout(new BorderLayout());
        p1.add(ta, BorderLayout.CENTER);
        list = new java.awt.List();
        p1.add(list, BorderLayout.WEST);
        add(p1, BorderLayout.CENTER);
        connect = new Button("Connect");
        connect.addActionListener(this);
        disconnect = new Button("Disconnect");
        disconnect.setEnabled(false);
        disconnect.addActionListener(this);
        Panel p2 = new Panel();
        p2.add(connect);
        p2.add(disconnect);
        add(p2, BorderLayout.SOUTH);

        this.frame = frame;
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            if (ae.getSource() == connect) {
                if (!connected) {
                    s = new Socket("127.0.0.1", 3000);
                    oos = new ObjectOutputStream(s.getOutputStream());
                    ois = new ObjectInputStream(s.getInputStream());
                    d1 = new DataObject(MessageType.CONNECT);
                    d1.setMessage(frame.getTitle());
                    oos.writeObject(d1);
                    thread = new Thread(this);
                    thread.start();
                    connected = true;
                    connect.setEnabled(false);
                    disconnect.setEnabled(true);
                    System.out.println("Connected!!!");
                    if (list.getItemCount() != 0) list.removeAll();
                }
            } else if (ae.getSource() == disconnect) {
                if (connected) {
                    d1 = new DataObject(MessageType.DISCONNECT);
                    oos.writeObject(d1);
                    connected = false;
                    connect.setEnabled(true);
                    disconnect.setEnabled(false);
                }
            } else if (ae.getSource() == tf) {
                if (connected) {
                    String temp = tf.getText();
                    if (list.getSelectedItem() == null) {
                        d1 = new DataObject(MessageType.PUBLIC);
                    } else {
                        d1 = new DataObject(MessageType.PRIVATE);
                        d1.setDestination(list.getSelectedItem());
                    }
                    d1.setMessage(temp);
                    oos.writeObject(d1);
                    tf.setText("");
                } else {
                    String temp = tf.getText();
                    frame.setTitle(temp);
                    tf.setText("");
                }
            }
        } catch (UnknownHostException uhe) {
            System.out.println(uhe.getMessage());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public void run() {
        while (connected) {
            try {
                d2 = (DataObject)ois.readObject();
                switch (d2.getType()) {
                    case CONNECT:
                        ta.append(d2.getMessage() + " has connected\n");
                        list.add(d2.getMessage());
                        break;
                    case DISCONNECT:
                        ta.append(d2.getMessage() + " has disconnected\n");
                        list.remove(d2.getMessage());
                        break;
                    case PUBLIC:
                    case PRIVATE:
                        ta.append(d2.getMessage() + "\n");
                        break;
                    case LIST:
                        for (String name : d2.getList()) {
                            list.add(name);
                        }
                }

            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            } catch (ClassNotFoundException cnfe) {
                System.out.println(cnfe.getMessage());
            }
        }
    }
}
