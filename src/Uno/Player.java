package Uno;

/**
 * Class covering a player, contains methods for the hand and total points
 *
 * @author Tyrus Karmesin
 * @bugs These methods apparently got handled elsewhere, could be removed.
 */
public class Player {
    private int points;
    private Deck hand;

    // Default constructor that sets points to zero and makes an empty hand
    public Player() {
        points = 0;
        hand = new Deck();
    }

    // Plays a card from the hand, checks whether the play won the game
    public boolean playCard(int index, Deck target) {
        target.addCard(hand.play(index));
        if (hand.size() == 0) {
            return true;
        }
        return false;
    }

    // Draws a card from the given deck, keeps the hand sorted
    public void drawCard(Deck source) {
        hand.addCard(source.draw());
        hand.sort();
    }

    // Adds the points to a player after winning a game
    public void addPoints(int newPoints) {
        points += newPoints;
    }

    // Returns the points
    public int getPoints() {
        return points;
    }

    // Sets the points, not sure that this is needed
    public void setPoints(int points) {
        this.points = points;
    }

    // Returns the hand
    public Deck getHand() {
        return hand;
    }
}
