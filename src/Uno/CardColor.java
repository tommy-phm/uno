package Uno;

import java.awt.*;

/**
 * Enum for the card colors
 *
 * @author Tyrus Karmesin
 * @bugs none.
 */
public enum CardColor {
    BLACK(0),
    RED(0),
    YELLOW(13),
    GREEN(26),
    BLUE(39);

    private int indexOffset;

    // Simple constructor
    private CardColor(int indexOffset) {
        this.indexOffset = indexOffset;
    }

    public int getOffset() {
        return indexOffset;
    }
}
