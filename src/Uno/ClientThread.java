package Uno;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * ClientThread Class
 * Handle the Server communication to and from the clients.
 * @author Tommy Pham
 * @bugs none.
 */
public class ClientThread implements Runnable{
    private Server server;
    private Socket client;
    private int id;
    private PrintWriter out;

    /**
     * Return object for socket
     * @return out
     */
    public PrintWriter getWriter() {
        return out;
    }

    /**
     * Construct object
     * @param server ip address
     * @param client socket
     * @param id client id
     */
    public ClientThread(Server server, Socket client, int id) {
        this.server = server;
        this.client = client;
        this.id = id+1;
    }

    /**
     * Handle network in and out
     */
    public void run() {
        try {
            this.out = new PrintWriter(client.getOutputStream(), false);
            Scanner in = new Scanner(client.getInputStream());
            while (!client.isClosed()) {
                if (in.hasNextLine())
                    server.action(id, in.nextLine());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send deck info to client
     * @param deck deck object
     * @param id client id
     * @param top card on top
     * @param numPlayers number of total clients
     * @param usernames client's username
     */
    public void addDeck(Deck deck, int id, int top, int numPlayers, String[] usernames){
        String output = "4 "
                + deck.getCard(0).getIndex() + " "
                + deck.getCard(1).getIndex() + " "
                + deck.getCard(2).getIndex() + " "
                + deck.getCard(3).getIndex() + " "
                + deck.getCard(4).getIndex() + " "
                + deck.getCard(5).getIndex() + " "
                + deck.getCard(6).getIndex() + " "
                + id + " "
                + numPlayers + " "
                + top + " "
                + usernames[0] + " "
                + usernames[1] + " "
                + usernames[2] + " "
                + usernames[3];
        PrintWriter cout = this.getWriter();
        if (cout != null) {
            cout.write(output + "\r\n");
            cout.flush();
        }
    }

    /**
     * Send lobby information
     * @param usernames all client's username
     * @param ready all client's ready status
     * @param numPlayers total number of clients
     */
    public void setLobby(String[] usernames, Boolean[] ready, int numPlayers){
        String output = "5 "
                + id +" "
                + usernames[0] +" "
                + usernames[1] +" "
                + usernames[2] +" "
                + usernames[3] +" "
                +ready[0]+" "
                +ready[1]+" "
                +ready[2]+" "
                +ready[3]+" "
                +numPlayers;
        PrintWriter cout = this.getWriter();
        if (cout != null) {
            cout.write(output + "\r\n");
            cout.flush();
        }
    }

    /**
     * Send message to client
     * @param output String to send
     */
    public void message(String output) {
        try{
            PrintWriter cout = this.getWriter();
            if (cout != null) {
                cout.write(output + "\r\n");
                cout.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
