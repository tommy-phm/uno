package Uno;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

/**
 * Handle GUI Elements
 * Can display wallpaper, hud, and cards
 * @author Tommy Pham
 * @bugs none.
 */
public class Display {

    /**
     * Display wallpaper
     * @param canvas - display component
     * @param width - screen width
     * @param height - screen height
     */
    public static void displayWallpaper(Graphics2D canvas, int width, int height){
        Point center = new Point(width/2, height/2);
        float radius = width;
        float[] dist = { 0f, 1f};
        Color[] colors = { new Color(0, 0, 0, 0), new Color(0, 0, 0, 255)};

        canvas.setColor(Color.decode("#2980b9"));
        canvas.fill(new Ellipse2D.Double(center.getX() - radius, center.getY()
                - radius, radius * 2, radius * 2));

        RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, colors);
        canvas.setPaint(rgp);
        canvas.fill(new Ellipse2D.Double(center.getX() - radius, center.getY() - radius, radius * 2, radius * 2));
    }

    /**
     * Display HUD
     * @param canvas - display component
     * @param imgCards - cards' image
     * @param width - screen width
     * @param height - screen height
     * @param current - id of current player
     * @param totalPlayers - number of total players
     * @param usernames - list of usernames
     * @param cardCount - list of number of cards each player has
     * @param points - list of number of points each player has
     * @param rotation - direction of play
     */
    public static void displayHUD(Graphics2D canvas, Image[] imgCards, int width, int height, byte current, byte totalPlayers, String[] usernames, byte[] cardCount, int[] points, boolean rotation){
        int x = width-168, y = 20;

        canvas.setColor(Color.decode("#2874a6"));
        canvas.fillRect(x-5, y-15, 169, 20);
        canvas.setColor(Color.white);
        canvas.drawString("Usernames     Cards    Score", x, y);
        y = 45;
        for(byte i = 0; i <totalPlayers; i++){
            canvas.setColor(Color.decode("#1b4f72"));
            canvas.fillRect(x-5, y+i*25-15, 169, 20);
            if(current == i+1)
                canvas.setColor(Color.decode("#fdfefe"));
            else
                canvas.setColor(Color.decode("#979a9a"));
            canvas.drawString(usernames[i], x, y+i*25);
            canvas.drawString(String.valueOf(cardCount[i]), x+98-(String.valueOf(cardCount[i]).length()*7/2), y+i*25);
            canvas.drawString(String.valueOf(points[i]), x+143-(String.valueOf(points[i]).length()*7/2), y+i*25);
        }
        if(rotation)
            canvas.drawImage(imgCards[64], width-35, y+totalPlayers*25-10,25, 25, null);
        else
            canvas.drawImage(imgCards[63], width-35, y+totalPlayers*25-10,25, 25, null);
    }

    /**
     * Display wallpaper
     * @param canvas - display component
     * @param width - screen width
     * @param height - screen height
     */
    public static void displayLobby(Graphics2D canvas, int width, int height, byte totalPlayers, String[] usernames, boolean[] ready, int id){
        if(totalPlayers != 0) {
            int x = width / 2 - 90, y = height / 5;
            canvas.setColor(Color.black);
            canvas.fillRect(x, y, 180, 25);
            canvas.setColor(Color.white);
            canvas.drawString("Usernames              Status", x + 10, y + 17);
            y = y + 45;
            for (byte i = 0; i < totalPlayers; i++) {
                if (i % 2 == 0)
                    canvas.setColor(Color.darkGray);
                else
                    canvas.setColor(Color.gray);
                canvas.fillRect(x, y + i * 25 - 20, 180, 25);
                canvas.setColor(Color.white);
                canvas.drawString(usernames[i], x + 10, y + i * 25 - 3);
                if (ready[i]) {
                    canvas.setColor(Color.green);
                    canvas.drawString("READY", x + 117, y + i * 25 - 3);
                } else {
                    canvas.setColor(Color.red);
                    canvas.drawString("NOT READY", x + 102, y + i * 25 - 3);
                }
            }
            if (ready[id - 1]) {

                canvas.setColor(Color.green);
                canvas.fillRect(x, y + totalPlayers * 25 - 15, 180, 25);
                canvas.setColor(Color.white);
                canvas.drawString("READY", x + 70, y + totalPlayers * 25 + 2);
            } else {
                canvas.setColor(Color.red);
                canvas.fillRect(x, y + totalPlayers * 25 - 15, 180, 25);
                canvas.setColor(Color.white);
                canvas.drawString("NOT READY", x + 60, y + totalPlayers * 25 + 2);
            }
        }
    }

    public static void displayWinner(Graphics2D canvas, int width, int height, String usernames){
            canvas.setColor(Color.white);
            canvas.drawString(usernames+" has won!", width / 2 - (2*usernames.length())-19, height / 2-2);
    }

    /**
     * Display cards
     * @param canvas - display component
     * @param imgCards - cards' image
     */
    public static void displayCard(Graphics2D canvas, Image[] imgCards, int width, int height,
                                   byte id, byte totalPlayers, Deck deck, int top, byte[] totalCards){
        final int cardWidth = 100, cardHeight = 150;
        final int deckX = width/2 - cardWidth;
        final int deckY = (height - cardHeight)/2;
        //canvas.drawImage(imgCards[54], width, height, null);
        canvas.drawImage(imgCards[54], deckX, deckY, null);
        canvas.drawImage(imgCards[top],width/2, (height - cardHeight)/2, null);

        displayBottom(canvas, imgCards, width, height, deck);
        if(id+1 <= totalPlayers)
            displayLeft(totalCards[id], canvas, imgCards, width, height);
        else if(id-3 > 0)
            displayLeft(totalCards[id-4], canvas, imgCards, width, height);

        if(id+2 <= totalPlayers)
            displayTop(totalCards[id+1], canvas, imgCards, width, height);
        else if(id-2 > 0)
            displayTop(totalCards[id-3], canvas, imgCards, width, height);

        if(id+3 <= totalPlayers)
            displayRight(totalCards[id+2], canvas, imgCards, width, height);
        else if(id-1 > 0)
            displayRight(totalCards[id-2], canvas, imgCards, width, height);
    }

    /**
     * Display bottom row cards - visible
     * @param canvas - display component
     * @param imgCards - cards' image
     * @param width - screen width
     * @param height - screen height
     */
    private static void displayBottom(Graphics2D canvas, Image[] imgCards, int width, int height, Deck deck){
        final int cardWidth = 100, cardHeight = 150;

        int size = deck.size();

        int offset;
        if(size < 16)
            offset = 50;
        else
            offset = (int)((750 - cardWidth) / (size-1));

        int x = (int)(width/2 - (((size - 1) * offset + cardWidth) / 2));
        int y = (int)(height * 0.9 - cardHeight/2);
        Random rand = new Random();
        for(int i = 0; i < size; i++){
            int rand_int1 = rand.nextInt(52);
            canvas.drawImage(imgCards[deck.getCard(i).getIndex()], x, y, null);
            x = x + offset;
        }
    }

    /**
     * Display top row cards - hidden
     * @param size - player's deck size
     * @param canvas - display component
     * @param imgCards - cards' image
     * @param width - screen width
     * @param height - screen height
     */
    private static void displayTop(int size, Graphics2D canvas, Image[] imgCards, int width, int height){
        final int cardWidth = 100, cardHeight = 150;

        int offset;
        if(size < 16)
            offset = 50;
        else
            offset = (int)((750 - cardWidth) / (size-1));

        int x = (int)(width/2 + (((size - 1) * offset + cardWidth) / 2)  - cardWidth );
        if(size == 1)
            x = x - offset;
        int y = (int)(height * 0.1 - cardHeight/2);
        for(int i = 0; i < size; i++){
            canvas.drawImage(imgCards[54], x, y, null);
            x = x - offset;
        }
    }

    /**
     * Display right column cards - hidden
     * @param size - player's deck size
     * @param canvas - display component
     * @param imgCards - cards' image
     * @param width - screen width
     * @param height - screen height
     */
    private static void displayRight(int size, Graphics2D canvas, Image[] imgCards, int width, int height){
        final int cardWidth = 100, cardHeight = 150;

        int offset;
        if(size < 16)
            offset = 50;
        else
            offset = (int)((750 - cardWidth) / (size-1));

        int x = (int)(width * 0.8 - cardWidth/2);
        int y = (int)(height/2 + (((size - 1) * offset + cardHeight) / 2) - cardHeight);
        for(int i = 0; i < size; i++){
            canvas.drawImage(imgCards[54], x, y, null);
            y = y - offset;
        }
    }

    /**
     * Display left column cards - hidden
     * @param size - player's deck size
     * @param canvas - display component
     * @param imgCards - cards' image
     * @param width - screen width
     * @param height - screen height
     */
    private static void displayLeft(int size, Graphics2D canvas, Image[] imgCards, int width, int height) {
        final int cardWidth = 100, cardHeight = (int) (cardWidth * 1.5);

        int offset;
        if (size < 16)
            offset = 50;
        else
            offset = (int) ((750 - cardWidth) / (size - 1));

        int x = (int) (width * 0.2 - cardWidth / 2);
        int y = (int) (height / 2 - (((size - 1) * offset + cardHeight) / 2));
        for (int i = 0; i < size; i++) {
            canvas.drawImage(imgCards[54], x, y, null);
            y = y + offset;
        }
    }

    /**
     * Color a color menu
     * @param canvas - display component
     * @param width - screen width
     * @param height - screen height
     */
    public static void cardMenu(Graphics2D canvas, int width, int height){
        canvas.setColor(Color.decode("#d72600"));
        canvas.fillRect(width/2+110, height/2-75, 30, 30);
        canvas.setColor(Color.decode("#0956BF"));
        canvas.fillRect(width/2+110, height/2-35, 30, 30);
        canvas.setColor(Color.decode("#379711"));
        canvas.fillRect(width/2+110, height/2+5, 30, 30);
        canvas.setColor(Color.decode("#ECD407"));
        canvas.fillRect(width/2+110, height/2+45, 30, 30);
    }


    /**
     * Import cards' image
     */

    public Image[]  addImage() throws IOException {
        Image[] imgCards = new Image[65];
        for(int i = 0; i < 65; i++) {

            String resource = "/resources/" + i + ".png";
            URL url = getClass().getResource(resource);
            Image image = new ImageIcon(url).getImage();
            imgCards[i] = image;
            try{
                imgCards[i] = ImageIO.read(new File("src/resources/" + i + ".png"));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return imgCards;
    }
}
