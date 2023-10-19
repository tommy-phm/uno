package Uno;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * ServerThread Class
 * Handle the Client communication to and from the server.
 * @author Tommy Pham
 * @bugs none.
 */
public class ServerThread implements Runnable {
    private Socket socket;
    private Client client;
    private final LinkedList<String> actions;

    /**
     * Construct object
     * @param client Client object
     * @param socket server socket
     */
    public ServerThread(Client client, Socket socket) {
        this.socket = socket;
        this.client = client;
        actions = new LinkedList<String>();
    }

    /**
     * Handle network in and out
     */
    public void run() {
        try {
            /* Use Socket object to get input and output stream */
            PrintWriter out_stream =
                    new PrintWriter(socket.getOutputStream(), false);
            InputStream in_stream = socket.getInputStream();
            Scanner in = new Scanner(in_stream);
            while (!socket.isClosed()) {
                /* Get game update from server. */
                if (in_stream.available() > 0) {
                    if (in.hasNextLine())
                        client.update(in.nextLine());
                }
                /* Send actions to the socket server */
                if (!actions.isEmpty()) {
                    String next = null;
                    synchronized (actions) {
                        next = actions.pop();
                    }
                    out_stream.println(next);
                    out_stream.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send action info to server
     * @param  new_message String to send
     */
    public void newAction (String new_message){
        synchronized (actions) {
            actions.push(new_message);
        }
    }
}
