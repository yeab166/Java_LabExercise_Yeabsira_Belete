import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {
    private final String name;
    private int bankroll;
    private final List<Card> hand;

    public Player(String name, int bankroll) {
        this.name = name;
        this.bankroll = bankroll;
        this.hand = new ArrayList<Card>(5);
    }

    public String getName() {
        return name;
    }

    public int getBankroll() {
        return bankroll;
    }

    public void adjustBankroll(int amount) {
        bankroll += amount;
    }

    public List<Card> getHand() {
        return Collections.unmodifiableList(hand);
    }

    public void clearHand() {
        hand.clear();
    }

    public void receiveCard(Card card) {
        if (hand.size() < 5) {
            hand.add(card);
        }
    }

    public void replaceCards(List<Integer> discardIndices, Deck deck) {
        if (discardIndices == null || discardIndices.isEmpty()) {
            return;
        }
        Collections.sort(discardIndices, Collections.reverseOrder());
        for (int index : discardIndices) {
            if (index >= 0 && index < hand.size()) {
                hand.remove(index);
            }
        }
        while (hand.size() < 5 && deck.size() > 0) {
            hand.add(deck.deal());
        }
    }
}
