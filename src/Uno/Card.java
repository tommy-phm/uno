package Uno;

import static Uno.CardColor.*;
import static Uno.CardType.*;

/**
 * Class for handling both special and normal cards
 *
 * @author Tyrus Karmesin
 * @bugs none.
 */
public class Card {
    private CardColor color;
    private CardType type;

    // Creates a card from color and type
    public Card(CardColor color, CardType type) {
        this.color = color;
        this.type = type;
    }

    // Creates a card from the index
    public static Card cardFromIndex(int index) {
        if (index < 0 || index > 62 || index == 54) {
            return null;
        }

        CardColor color;
        CardType type;

        // set the color
        if (index < 13 || index == 55 || index == 59) {
            color = RED;
        }
        else if (index < 26 || index == 56 || index == 60) {
            color = YELLOW;
        }
        else if (index < 39 || index == 57 || index == 61) {
            color = GREEN;
        }
        else if (index < 52 || index == 58 || index == 62) {
            color = BLUE;
        }
        else {
            color = BLACK;
        }

        // set wildcard types
        if (index == 52 || (index > 54 && index < 59)) {
            type = WILD;
            return new Card(color, type);
        }
        if (index == 53 || index > 58) {
            type = WILDDRAWFOUR;
            return new Card(color, type);
        }

        // set types
        index = index % 13;
        switch (index) {
            case 0:
                type = ZERO;
                break;
            case 1:
                type = ONE;
                break;
            case 2:
                type = TWO;
                break;
            case 3:
                type = THREE;
                break;
            case 4:
                type = FOUR;
                break;
            case 5:
                type = FIVE;
                break;
            case 6:
                type = SIX;
                break;
            case 7:
                type = SEVEN;
                break;
            case 8:
                type = EIGHT;
                break;
            case 9:
                type = NINE;
                break;
            case 10:
                type = SKIP;
                break;
            case 11:
                type = REVERSE;
                break;
            case 12:
                type = DRAWTWO;
                break;
            default:
                return null;
        }
        return new Card(color, type);
    }

    // returns the color
    public CardColor getColor() {
        return color;
    }

    // sets the color
    public void setColor(CardColor color) {
        this.color = color;
    }

    // returns the type of card
    public CardType getType() {
        return type;
    }

    // returns the index of the card for sorting and finding its image
    public int getIndex() {
        if (type == WILD) {
            int colorOffset = color.getOffset()/13;
            if (color == BLACK) {
                return 52;
            }
            return 55 + colorOffset;
        }
        if (type == WILDDRAWFOUR) {
            int colorOffset = color.getOffset()/13;
            if (color == BLACK) {
                return 53;
            }
            return 59 + colorOffset;
        }
        return color.getOffset() + type.getIndex();
    }

    // returns the points given for the card
    public int points() {
        return this.type.getPoints();
    }

    // prints out the card as a string
    public String toString() {
        return color.toString() + " " + type.toString();
    }
}
