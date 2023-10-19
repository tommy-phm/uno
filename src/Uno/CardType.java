package Uno;

/**
 * Enum for the types of cards
 *
 * @author Tyrus Karmesin
 * @bugs none.
 */
public enum CardType {
    ZERO(0, "0", 0),
    ONE(1, "1", 1),
    TWO(2, "2", 2),
    THREE(3, "3", 3),
    FOUR(4, "4", 4),
    FIVE(5, "5", 5),
    SIX(6, "6", 6),
    SEVEN(7, "7", 7),
    EIGHT(8, "8", 8),
    NINE(9, "9", 9),
    SKIP(20, "Skip", 10),
    REVERSE(20, "Reverse", 11),
    DRAWTWO(20, "+2", 12),
    WILD(50, "Wild", 52),
    WILDDRAWFOUR(50, "Wild +4", 53);

    private int points;
    private String string;
    private int index;

    // Simple constructor
    CardType(int points, String string, int index) {
        this.points = points;
        this.string = string;
        this.index = index;
    }

    // returns the points you get for the card type
    public int getPoints() {
        return points;
    }

    // Returns a string with the card type, for displaying
    public int getIndex() {
        return index;
    }

    // Returns a string with the card type, for displaying
    public String toString() {
        return string;
    }
}
