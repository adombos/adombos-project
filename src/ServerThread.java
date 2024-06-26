import java.net.*; 
import java.io.*; 

/**
 * The ServerThread class extends Thread and represents a thread that handles communication 
 * with a specific client on the server side. Each ServerThread
 * instance is associated with a single client connection and manages the sending and receiving
 * of messages between the server and that client.
 */
public class ServerThread extends Thread {
    private Socket socket = null; 
    private Server server = null; 
    private int ID = -1; 
    private BufferedReader streamIn = null; 
    private BufferedWriter streamOut = null; 
    private boolean isRunning = true; 
    private String username; 

    public ServerThread(Server _server, Socket _socket, String _username) {
        super(); 
        server = _server; 
        socket = _socket; 
        ID = socket.getPort(); 
        username = _username; 
    }

    // Sends message to the output stream 
    public void send(String msg) {
        try {
            streamOut.write(msg); 
            streamOut.newLine(); 
            streamOut.flush(); 
            System.out.println("Successfully sent message: " + msg); 
        } catch (IOException ioe) {
            System.out.println("Error when sending message"); 
            server.remove(ID); 
            stopThread(); 
        }
    }

    public int getID() {
        return ID; 
    }

    public String getUsername() {
        return username; 
    }

    // Read message from the input stream 
    public void run() {
        System.out.println(String.format("Server thread %d is currently running", ID)); 
        
        while (isRunning) {
            try {
                String input = streamIn.readLine(); 
                System.out.println("Received message: " + input); 
                server.handle(ID, input); 
            } catch (IOException ioe) {
                System.out.println("Error when reading message"); 
                server.remove(ID);
                stopThread(); 
            }
        }
    }

    public void open() throws IOException {
        streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        streamOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); 
    }

    public void close() throws IOException {
        if (socket != null) {
            socket.close(); 
        } if (streamIn != null) {
            streamIn.close(); 
        } if (streamOut != null) {
            streamOut.close(); 
        }
    }

    public void stopThread() {
        isRunning = false;

        try { 
            if (socket != null) {
                socket.close(); 
            }
        } catch (IOException ioe) {
            System.out.println("Error when closing socket");
        }
    }
}
