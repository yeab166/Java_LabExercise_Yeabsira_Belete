import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Deck {
    private final LinkedList<Card> cards;
    private final Random random;

    public Deck() {
        this.cards = new LinkedList<Card>();
        this.random = new Random();
        reset();
    }

    public final void reset() {
        cards.clear();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        shuffle();
    }

    public void shuffle() {
        List<Card> temporary = new ArrayList<Card>(cards);
        Collections.shuffle(temporary, random);
        cards.clear();
        cards.addAll(temporary);
    }

    public Card deal() {
        return cards.pollFirst();
    }

    public int size() {
        return cards.size();
    }
}
