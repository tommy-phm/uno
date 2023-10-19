package Uno;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import static Uno.CardColor.BLACK;
import static Uno.CardColor.RED;

/**
 * Uno client that display and connect to a server
 * Can display GUI and send moves to server
 * @author Tommy Pham
 * @bugs lobby is sometimes laggy.
 */
public class Client extends JPanel implements ActionListener {
    private JPanel panel = new JPanel();
    private String IP;
    private int PORT;
    private String NAME;
    private JTextField input1, input2, input3;
    private ServerThread server_thread;

    private final int width, height;
    private final Image[] imgCards;

    private byte id, totalPlayers, current;
    private int colorMenu = 0;
    private Deck deck = new Deck();
    private byte[] cardCount = new byte[4];
    private Card top = Card.cardFromIndex(0);
    private String[] usernames = new String[4];
    private int[] points = new int[4];
    private boolean roation;
    private boolean[] ready = new boolean[4];

    /**
     * Constructor for Client
     * First ask for server info
     * Then display game
     */
    public Client() throws IOException {
        usernames[0] = "";
        usernames[1] = "";
        usernames[2] = "";
        usernames[3] = "";

        panel.setPreferredSize(new Dimension(200, 100));
        panel.setBackground(Color.decode("#3498db"));

        JLabel ipText = new JLabel("Ip Addresss", SwingConstants.CENTER);
        ipText.setForeground(Color.black);
        panel.add(ipText);
        input1 = new JTextField("localhost");
        panel.add(input1);

        JLabel portText = new JLabel("Port", SwingConstants.CENTER);
        portText.setForeground(Color.black);
        panel.add(portText);
        input2= new JTextField(String.valueOf(1234));
        panel.add(input2);

        JLabel usernameText = new JLabel("Username", SwingConstants.CENTER);
        usernameText.setForeground(Color.black);
        panel.add(usernameText);
        input3= new JTextField("player");
        panel.add(input3);

        JButton button = new JButton("Submit");
        button.addActionListener(this);
        panel.add(button);

        panel.setLayout(new GridLayout(4, 2));
        add(panel, BorderLayout.CENTER);

        IP = input1.getText();
        PORT = Integer.parseInt(input2.getText());
        NAME =  input3.getText();

        width = 1920; height = 1080; //For testing
        setPreferredSize(new Dimension(width, height));
        addMouseListener(new MouseHandler());
        imgCards = new Display().addImage();

    }

    /**
     * Connect to server
     */
    private void startClient() {
        try {
            Socket socket = new Socket(IP, PORT);
            Thread.sleep(1000);
            server_thread = new ServerThread(this, socket);
            Thread server = new Thread(server_thread);
            server.start();
            server_thread.newAction("5 "+NAME);
            remove(panel);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Monitor mouse press and sent coordinates to play()
     */
    private class MouseHandler extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            play(e.getX(), e.getY());
        }
    }

    /**
     * Update Client with info from server
     * @param data info from server
     */
    public void update(String data){
        //System.out.println(current+"> " + data);
        String[] chuck = data.split(" ");
        switch(chuck[0]){
            case "0":
                //System.out.println("["+id+"] Sync");
                current = Byte.parseByte(chuck[1]);
                top = Card.cardFromIndex(Integer.parseInt(chuck[2]));
                cardCount[0] = Byte.parseByte(chuck[3]);
                cardCount[1] = Byte.parseByte(chuck[4]);
                cardCount[2] = Byte.parseByte(chuck[5]);
                cardCount[3] = Byte.parseByte(chuck[6]);
                colorMenu = Integer.parseInt(chuck[7]);
                points[0] = Integer.parseInt(chuck[8]);
                points[1] = Integer.parseInt(chuck[9]);
                points[2] = Integer.parseInt(chuck[10]);
                points[3] = Integer.parseInt(chuck[11]);
                roation = Boolean.parseBoolean(chuck[12]);
                break;
            case "1":
                System.out.println("["+id+"] Valid Move. Remove Card NO."+chuck[1]);
                deck.play(Integer.parseInt(chuck[1]));
                break;
            case "2":
                deck.addCard(Card.cardFromIndex(Integer.parseInt(chuck[1])));
                System.out.println("["+id+"] Card Added "+deck.getCard(deck.size()-1).getColor()+" "+deck.getCard(deck.size()-1).getType());
                deck.sort();
                break;
            case "3":
                System.out.println("["+id+"] Force Play Card "+Card.cardFromIndex(Integer.parseInt(chuck[1])).getColor()+" "+Card.cardFromIndex(Integer.parseInt(chuck[1])).getType());
                server_thread.newAction("0 "+chuck[1]);
                break;
            case "4":
                deck = new Deck();
                for (int i = 1; i < 8; i++)
                    deck.addCard(Card.cardFromIndex(Integer.parseInt(chuck[i])));
                id = Byte.parseByte(chuck[8]);
                totalPlayers = Byte.parseByte(chuck[9]);
                top = Card.cardFromIndex(Integer.parseInt(chuck[10]));
                for (byte i = 0; i < totalPlayers; i++)
                    cardCount[i] = 7;
                usernames[0] = chuck[11];
                usernames[1] = chuck[12];
                usernames[2] = chuck[13];
                usernames[3] = chuck[14];
                System.out.println("["+id+"] Deck added.");
                break;
            case "5":
                id = Byte.parseByte(chuck[1]);
                usernames[0] = chuck[2];
                usernames[1] = chuck[3];
                usernames[2] = chuck[4];
                usernames[3] = chuck[5];
                ready[0] = Boolean.parseBoolean(chuck[6]);
                ready[1] = Boolean.parseBoolean(chuck[7]);
                ready[2] = Boolean.parseBoolean(chuck[8]);
                ready[3] = Boolean.parseBoolean(chuck[9]);
                totalPlayers = Byte.parseByte(chuck[10]);
                //System.out.println("["+id+"] Update Lobby ");
                break;
        }
    }

    /**
     * Execute move base on mouse click and location
     * @param x horizontal coordinate
     * @param y vertical coordinate
     */
    private void play(int x, int y){
        int size = deck.size(), cardWidth = 100, cardHeight = (int)(cardWidth * 1.5);

        int offset;
        if(size < 16)
            offset = 50;
        else
            offset = (750 - cardWidth) / (size-1);
        int xMin = width/2 - (((size - 1) * offset + cardWidth) / 2);
        int xMax = xMin + (size - 1)*offset + cardWidth;
        int yMin = (int)(height * 0.9 - cardHeight/2);
        int yMax = (int)(height * 0.9 - cardHeight/2) + cardHeight;

        int deckX = width/2 - cardWidth;
        int deckY = (height - cardHeight)/2;

        if (xMin < x && x < xMax && yMin < y && y < yMax && colorMenu != id) {
            int index = (x - xMin) / offset;
            if (index >= size)
                index = size - 1;
                server_thread.newAction("0 "+index);
        } else if (deckX < x && x < deckX + cardWidth && deckY < y && y < deckY + cardHeight && colorMenu != id) {
            server_thread.newAction("1 ");
        } else if (colorMenu == id) {
            if (width / 2 + 110 < x && x < width / 2 + 140
                    && height / 2 - 70 < y && y < height / 2 + 75) {
                if (height / 2 - 40 > y) {
                    //System.out.println("The color red has been chosen");
                    server_thread.newAction("2 R");
                } else if (height / 2 > y) {
                    //System.out.println("The color blue has been chosen");
                    server_thread.newAction("2 B");
                } else if (height / 2 + 40 > y) {
                    //System.out.println("The color green has been chosen");
                    server_thread.newAction("2 G");
                } else {
                    //System.out.println("The color yellow has been chosen");
                    server_thread.newAction("2 Y");
                }
                colorMenu = 0;//should remove
                current = 0;//should remove
            }
        } else if(width/2-90 < x && x < width/2+90  && height/5+30+totalPlayers*25 < y && y < height/5+45+totalPlayers*25+10) {
            if(ready[id - 1])
                server_thread.newAction("4 ");
            else
                server_thread.newAction("3 ");
            }
    }

    /**
     * Setup GUI, display wallpaper and display cards.
     * @param g graphically component
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D canvas = (Graphics2D) g;
        canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        Display.displayWallpaper(canvas, width, height);
        display:
        if(current == 0) {
            if(totalPlayers != 0) {
                for(int i = 0; i < totalPlayers; i++){
                    //System.out.println(i+" "+points[i]);
                    if(points[i] >= 500 ){
                        Display.displayWinner(canvas, width, height, usernames[i]);
                        break display;
                    }
                }
                Display.displayLobby(canvas, width, height, totalPlayers, usernames, ready, id);
            }
        }
        else{
            Display.displayHUD(canvas, imgCards, width, height, current, totalPlayers, usernames, cardCount, points, roation);
            if(colorMenu == id)
                Display.cardMenu(canvas, width, height);
            Display.displayCard(canvas, imgCards, width, height, id, totalPlayers, deck, top.getIndex(), cardCount);
        }

        repaint();
    }

    /**
     * Send message to server
     * @param e event name
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        IP = input1.getText();
        PORT = Integer.parseInt(input2.getText());
        NAME =  input3.getText();
        startClient();
    }

    /**
     * Initialize Client for UNO
     */
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Uno");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.add(new Client());
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
