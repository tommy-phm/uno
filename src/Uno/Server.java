package Uno;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import static Uno.CardColor.*;
import static Uno.CardType.WILD;
import static Uno.CardType.WILDDRAWFOUR;

/**
 * Uno server that display and connect to clients
 * Initialize a server to handle client communication and game logic.
 *
 * @author Tommy Pham && Tyrus Karmesin
 * @bugs Cannot be exited out of, must be killed manually.
 */
public class Server extends JFrame implements ActionListener {
    private JPanel panel = new JPanel();
    private JTextField input1;
    private ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    private int numPlayers, current, next, step, colorMenu;
    private Deck[] decks = new Deck[2];
    private Card drawnCard = null;
    private Player[] players = new Player[4];
    private String[] usernames = new String[4];
    private Boolean[] ready = new Boolean[4];
    private int[] points = new int[4];
    private boolean gameOver = false;
    private int gamePoints = 500;

    /**
     * Initialize Server
     */
    public Server() {
        super("Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(200,100));
        panel.setBackground(Color.decode("#3498db"));
        JLabel portText = new JLabel("Port", SwingConstants.CENTER);
        portText.setForeground(Color.black);
        panel.add(portText);
        input1 = new JTextField("1234");
        panel.add(input1);

        JButton exit = new JButton("Exit");
        exit.setActionCommand("Exit");
        exit.addActionListener(this);
        panel.add(exit);

        JButton start = new JButton("Start");
        start.setActionCommand("Start");
        start.addActionListener(this);
        panel.add(start);

        panel.setLayout(new GridLayout(2, 2));
        add(panel, BorderLayout.CENTER);

        pack();
    }

    /**
     * Either start or exit serer/
     * @param e event name
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
            case "Start":
                ServerSocket server_socket = null;
                try {
                    server_socket = new ServerSocket(Integer.parseInt(input1.getText()));
                    /* Begin accepting clients */
                    System.out.println("[Server] Socket Created.");
                    for (int i = 0; i < 4; i++) {
                        usernames[i] = "Player_"+(i+1);
                        players[i] = new Player();
                        ready[i] = false;
                    }
                    acceptClients(server_socket);
                } catch (IOException E) {
                    E.printStackTrace();
                }
                break;
            case "Exit":
                System.exit(0);
        }
    }

    /**
     * Accepts clients for server
     * @param server_socket object of server
     */
    private void acceptClients(ServerSocket server_socket) {
        while (true) {
            try {
                Socket client = server_socket.accept();
                System.out.println("["+(numPlayers+1)+"] New Player: "
                        + client.getRemoteSocketAddress());
                ClientThread client_thread =
                        new ClientThread(this, client, clients.size());
                Thread new_client = new Thread(client_thread);
                new_client.start();
                clients.add(client_thread);
                numPlayers++;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Starts a game.
     * Generates a draw, discard deck and a play deck for each player.
     */
    private void initialize(){
        for (int z = 0; z < 4; z++)
            players[z] = new Player();
        current = 1;
        next = 2;
        step = 1;
        colorMenu = 0;

        decks[0] = new Deck();
        decks[0].completeDeck();
        decks[0].shuffle();

        decks[1] = new Deck();
        Card drawnCard = decks[0].draw();
        // prevent the first card from being a wildcard
        while (drawnCard.getColor() == BLACK) {
            decks[0].addCard(drawnCard);
            decks[0].shuffle();
            drawnCard = decks[0].draw();
        }
        decks[1].addCard(drawnCard);

        for(int i = 0; i < numPlayers; i++) {
            for(int j = 0; j < 7; j++)
                players[i].getHand().addCard(decks[0].draw());
            players[i].getHand().sort();
            clients.get(i).addDeck(players[i].getHand(), i+1, decks[1].getCard(decks[1].size()-1).getIndex(), numPlayers, usernames);
            ready[i] = false;
        }
        for(int i = 0; i < numPlayers; i++)
            clients.get(i).setLobby(usernames, ready, numPlayers);
        try{
            TimeUnit.MILLISECONDS.sleep(500);
        }catch(Exception e){
            e.printStackTrace();
        }
        sync();
        System.out.println("[Server] Round Started.");
    }

    /**
     * Set turn for current and next
     * Set step
     */
    private void turnIncrement() {
        current += step - 1;
        current = ((current + numPlayers) % numPlayers) + 1;
        if (step == 2 || step == -2) {
            step /= 2;
        }
        next = current + step - 1;
        next = ((next + numPlayers) % numPlayers) + 1;
        System.out.println("-------------Player ["+ current +"]'s turn -------------");
        System.out.print("Points: " + points[0]);
        for (int i = 1; i < numPlayers; i++) {
            System.out.print(", " + points[i]);
        }
        System.out.print("\n");
        /*
        System.out.println("hands:");
        System.out.println(players[0].getHand());
        System.out.println(players[1].getHand());
        System.out.println(players[2].getHand());
        */
    }

    /**
     * Apply special card effect
     */
    private void cardEffect(Uno.CardType type) {
        switch (type) {
            case ZERO:
            case ONE:
            case TWO:
            case THREE:
            case FOUR:
            case FIVE:
            case SIX:
            case SEVEN:
            case EIGHT:
            case NINE:
            case WILD:
                return;
            case SKIP:
                step *= 2;
                return;
            case REVERSE:
                // in a two player game, reverse acts as skip
                if (numPlayers == 2) {
                    step *= 2;
                }
                else {
                    step *= -1;
                }
                return;
            case WILDDRAWFOUR:
                drawCard(next);
                drawCard(next);
            case DRAWTWO:
                drawCard(next);
                drawCard(next);
                step *= 2;
        }
    }

    // Draws a card, reshuffling if needed.
    private Card drawCard(int playerNum){
        if(decks[0].size() == 0) {
            System.out.println("Draw deck is empty, shuffling");
            decks[0] = new Deck(decks[1]);
            decks[1] = new Deck();
            Card drawnCard = decks[0].draw();
            decks[0].shuffle();
            decks[1].addCard(drawnCard);
        }
        Card drawnCard = decks[0].draw();
        CardType drawnType = drawnCard.getType();
        //if wild card has color then remove it
        if (drawnType == WILD || drawnType == WILDDRAWFOUR) {
            drawnCard.setColor(BLACK);
        }
        Deck hand = players[playerNum - 1].getHand();

        hand.addCard(drawnCard);
        System.out.println("Player [" + (playerNum) + "] drew: " + hand.getCard(hand.size() - 1));
        hand.sort();
        clients.get(playerNum-1).message("2 "+drawnCard.getIndex()); // Tell client to add card
        sync();
        return drawnCard;
    }

    /**
     * Execute a move for a player.
     * @param id player's id
     * @param action move id
     */
    public void action(int id, String action) throws InterruptedException {
        if (gameOver) {
            return;
        }
        //System.out.println(id+" > "+action);
        String[] chuck = action.split(" ");
        switch(chuck[0]){
            case"0":
                if(current != id)
                    return;
                Deck hand = players[current-1].getHand();
                int index = Integer.parseInt(chuck[1]);
                System.out.println("Player ["+ id +"] Selected Card NO." + index + ", " + hand.getCard(index));
                if (hand.verify(hand.getCard(index), decks[1].getCard(decks[1].size()-1))) {
                    clients.get(current-1).message("1 "+index); //Tell Client move is valid and to remove card
                    if (hand.getCard(index).getColor() == BLACK) {
                        if (hand.getCard(index).getType() == WILD || hand.getCard(index).getType() == WILDDRAWFOUR) {
                            colorMenu = current;
                        }
                    }
                    cardEffect(hand.getCard(index).getType());
                    if (players[current - 1].playCard(index, decks[1])) {
                        System.out.println("Player " + current + " Wins!");
                        for (int i = 0; i < numPlayers; i++) {
                            points[current - 1] += players[i].getHand().getTotalValue();
                        }
                        current = 0;
                        sync();
                        return;
                    }
                    if (colorMenu == 0) {
                        turnIncrement();
                    }
                }
                sync();
                break;
            case "1":
                if(current!= id)
                    return;
                drawnCard = drawCard(current);
                if (drawnCard == null) {
                    System.out.println("Deck is empty, shuffling");
                }
                else{
                    //players[current - 1].getHand().addCard(drawnCard);
                    //players[current - 1].getHand().sort();
                    if (!players[current - 1].getHand().verify(drawnCard,decks[1].getCard(decks[1].size() - 1))) {
                        System.out.println("> not playable");
                        players[current - 1].getHand().sort();
                        turnIncrement();
                    }else{
                        int playIndex =players[current - 1].getHand().search(drawnCard.getIndex());
                        System.out.println("> playable NO."+playIndex);
                        try{
                            TimeUnit.MILLISECONDS.sleep(500);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        clients.get(current-1).message("3 "+playIndex); //Client is force to play
                    }
                    drawnCard = null;
                }
                sync();
                break;
            case "2":
                if(current!= id)
                    return;
                //set color of top card
                int deckEnd = decks[1].size()-1;
                CardColor color = null;
                switch(chuck[1]){
                    case "R":
                        System.out.println("["+id+"] The color red has been chosen");
                        color = RED;
                        break;
                    case "B":
                        System.out.println("["+id+"] The color blue has been chosen");
                        color = BLUE;
                        break;
                    case "G":
                        System.out.println("["+id+"] The color green has been chosen");
                        color = GREEN;
                        break;
                    case "Y":
                        System.out.println("["+id+"] The color yellow has been chosen");
                        color = YELLOW;
                        break;
                }
                decks[1].setCard(deckEnd, new Card(color, decks[1].getCard(deckEnd).getType()));
                colorMenu = 0;
                turnIncrement();
                sync();
                break;
            case "3":
                ready[id-1] = true;
                System.out.println("["+id+"] set ready status: true");
                for(int i = 0; i < numPlayers; i++)
                    clients.get(i).setLobby(usernames, ready, numPlayers);
                for(byte i = 0; i < numPlayers; i++){
                    if(!ready[i])
                        return;
                }
                initialize();
                break;
            case "4":
                System.out.println("["+id+"] set ready status: false");
                ready[id-1] = false;
                for(int i = 0; i < numPlayers; i++)
                    clients.get(i).setLobby(usernames, ready, numPlayers);
                break;
            case "5":
                usernames[id-1] = chuck[1];
                System.out.println("["+id+"] set username: "+chuck[1]);
                for(int i = 0; i < numPlayers; i++)
                    clients.get(i).setLobby(usernames, ready, numPlayers);
                break;
        }
    }

    /**
     * Sync game data with all clients
     */
    private void sync(){
        String input = "0 "+current+" "+decks[1].getCard(decks[1].size()-1).getIndex()+" "+players[0].getHand().size()+" "+players[1].getHand().size()+" "+players[2].getHand().size()+" "+players[3].getHand().size()+" "+colorMenu
                +" "+points[0]+" "+points[1]+" "+points[2]+" "+points[3]+" "+(step < 0);
        for (ClientThread c : clients) {
            PrintWriter cout = c.getWriter();
            if (cout != null) {
                cout.write(input + "\r\n");
                cout.flush();
            }
        }
    }

    /**
     * Open GUI and start server
     */
    public static void main(String[] args) {
        Server window = new Server();
        EventQueue.invokeLater(() -> window.setVisible(true));
    }
}
