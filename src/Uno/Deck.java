package Uno;

import java.util.ArrayList;

import static Uno.CardColor.*;
import static Uno.CardType.*;

/**
 * Handles both the decks and the players' hands
 *
 * @author Tyrus Karmesin
 * @bugs none.
 */
public class Deck {
    ArrayList<Card> deck;

    // Constructor that makes an empty deck
    public Deck() {
        this.deck = new ArrayList<Card>();
    }

    // Copy constructor
    public Deck(Deck deck) {
        this.deck = new ArrayList<Card>();
        for (int i = 0; i < deck.size(); i++) {
            this.deck.add(deck.getCard(i));
        }
    }

    // fills the deck with the full set of cards
    public void completeDeck() {
        // clear the deck
        deck = new ArrayList<Card>();
        for (int i = 0; i < 4; i++) {
            deck.add(new Card(BLACK, WILD));
            deck.add(new Card(BLACK, WILDDRAWFOUR));
        }
        for (CardColor color : CardColor.values()) {
            if (color == BLACK) {
                continue;
            }
            deck.add(new Card(color, ZERO));
            for (int i = 0; i < 2; i++) {
                deck.add(new Card(color, ONE));
                deck.add(new Card(color, TWO));
                deck.add(new Card(color, THREE));
                deck.add(new Card(color, FOUR));
                deck.add(new Card(color, FIVE));
                deck.add(new Card(color, SIX));
                deck.add(new Card(color, SEVEN));
                deck.add(new Card(color, EIGHT));
                deck.add(new Card(color, NINE));
                deck.add(new Card(color, SKIP));
                deck.add(new Card(color, REVERSE));
                deck.add(new Card(color, DRAWTWO));
            }
        }
    }

    // Method for adding a card to the deck
    public void addCard(Card card) {
        deck.add(card);
    }

    // Method to draw a card from a deck
    public Card draw() {
        int deckEnd = deck.size() - 1;
        Card card = deck.get(deckEnd);
        deck.remove(deckEnd);
        return card;
    }

    // Finds whether the hand has any cards of the given color, for finding if a wild draw four can be played
    public boolean hasColor(CardColor color) {
        for (int i = 0; i < deck.size(); i++) {
            CardColor handColor = deck.get(i).getColor();
            if (color == handColor) {
                return true;
            }
        }
        return false;
    }

    // Finds whether a card can be played onto the center deck
    public boolean verify(Card card, Card topCard) {
        CardType type = card.getType();
        CardColor topColor = topCard.getColor();
        if (type == WILD) {
            return true;
        }
        if (type == WILDDRAWFOUR) {
            if (!hasColor(topColor)) {
                return true;
            }
            else {
                return false;
            }
        }
        CardColor color = card.getColor();
        if (color == topColor) {
            return true;
        }
        CardType topType = topCard.getType();
        if (type == topType) {
            return true;
        }
        return false;
    }

    // Method to play a card at a given index from a player's hand
    public Card play(int index) {
        if (index < 0 || index > deck.size() - 1) {
            return null;
        }
        Card card = deck.get(index);
        deck.remove(index);
        return card;
    }

    // Puts the deck in a random order
    public void shuffle() {
        ArrayList<Card> newDeck = new ArrayList<Card>();
        int count = 0;
        for (int i = 0; i < deck.size(); i++) {
            int maximum = deck.size() - count;
            int index = (int)(Math.random() * maximum);
            newDeck.add(deck.get(index));
            deck.remove(index);
        }
        deck = newDeck;
    }

    // Sorts the deck using bubble sort
    public void sort() {
        boolean sorted = false;
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < deck.size() - 1; i++) {
                Card card = deck.get(i);
                Card nextCard = deck.get(i + 1);
                if (card.getIndex() > nextCard.getIndex()) {
                    deck.set(i, nextCard);
                    deck.set(i + 1, card);
                    sorted = false;
                }
            }
        }
    }

    // Gets the total value of the deck, for calculating total points at the end of a game
    public int getTotalValue() {
        int total = 0;
        for (int i = 0; i < deck.size(); i++) {
            Card card = deck.get(i);
            total += card.points();
        }
        return total;
    }

    public String toString () {
        String string = "";
        for (Card card : deck) {
            string = string.concat(card.toString() + "\n");
        }
        return string;
    }

    // returns the location of a card in a deck
    public int search(int index) {
        for (int i = 0; i < deck.size(); i++) {
            if (deck.get(i).getIndex() == index) {
                return i;
            }
        }
        return -1;
    }

    // Get a card at a given index
    public Card getCard(int index) {
        return deck.get(index);
    }

    // Set a card at a given index
    public void setCard(int index, Card card) {
        deck.set(index, card);
    }

    // Get the size of the deck
    public int size() {
        return deck.size();
    }

    // Get the array list of cards
    public ArrayList<Card> getDeck() {
        return deck;
    }
}
