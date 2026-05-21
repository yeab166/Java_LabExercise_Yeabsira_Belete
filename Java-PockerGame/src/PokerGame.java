import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PokerGame {
    public enum Phase {
        READY,
        DEAL,
        SHOWDOWN
    }

    private final Deck deck;
    private final Player human;
    private final Player cpu;
    private int pot;
    private Phase phase;
    private String status;

    public PokerGame() {
        this.deck = new Deck();
        this.human = new Player("You", 100);
        this.cpu = new Player("Dealer", 100);
        reset();
    }

    public Player getHuman() {
        return human;
    }

    public Player getCpu() {
        return cpu;
    }

    public int getPot() {
        return pot;
    }

    public Phase getPhase() {
        return phase;
    }

    public String getStatus() {
        return status;
    }

    public void reset() {
        human.clearHand();
        cpu.clearHand();
        deck.reset();
        pot = 0;
        phase = Phase.READY;
        human.adjustBankroll(100 - human.getBankroll());
        cpu.adjustBankroll(100 - cpu.getBankroll());
        status = "Click Deal to begin a new hand.";
    }

    public boolean canStartRound() {
        return phase == Phase.READY && human.getBankroll() >= 10;
    }

    public void startRound() {
        if (!canStartRound()) {
            status = "Not enough bankroll to start a new round.";
            return;
        }

        human.clearHand();
        cpu.clearHand();
        deck.reset();
        pot = 20;
        human.adjustBankroll(-10);
        cpu.adjustBankroll(-10);

        for (int i = 0; i < 5; i++) {
            human.receiveCard(deck.deal());
            cpu.receiveCard(deck.deal());
        }

        phase = Phase.DEAL;
        status = "Select cards to discard, then press Draw.";
    }

    public void draw(List<Integer> discardIndices) {
        if (phase != Phase.DEAL) {
            status = "You need to deal cards first.";
            return;
        }

        human.replaceCards(discardIndices, deck);
        cpu.replaceCards(getCpuDiscardIndices(), deck);
        phase = Phase.SHOWDOWN;
        settleRound();
    }

    private void settleRound() {
        PokerHandEvaluator.HandValue humanValue = PokerHandEvaluator.evaluate(human.getHand());
        PokerHandEvaluator.HandValue cpuValue = PokerHandEvaluator.evaluate(cpu.getHand());
        int result = humanValue.compareTo(cpuValue);
        if (result > 0) {
            human.adjustBankroll(pot);
            status = String.format("You win with %s! Dealer has %s.", humanValue.getName(), cpuValue.getName());
        } else if (result < 0) {
            cpu.adjustBankroll(pot);
            status = String.format("Dealer wins with %s. You had %s.", cpuValue.getName(), humanValue.getName());
        } else {
            int share = pot / 2;
            human.adjustBankroll(share);
            cpu.adjustBankroll(pot - share);
            status = String.format("Tie: both have %s. Pot split.", humanValue.getName());
        }
        pot = 0;

        if (human.getBankroll() < 10) {
            status += " Game over — reset to start again.";
        }
    }

    private List<Integer> getCpuDiscardIndices() {
        PokerHandEvaluator.HandValue value = PokerHandEvaluator.evaluate(cpu.getHand());
        int rankType = value.getRank();
        List<Card> hand = new ArrayList<Card>(cpu.getHand());
        Map<Integer, List<Integer>> rankPositions = new HashMap<Integer, List<Integer>>();
        for (int i = 0; i < hand.size(); i++) {
            int rank = hand.get(i).getValue();
            rankPositions.computeIfAbsent(rank, k -> new ArrayList<Integer>()).add(i);
        }

        List<Integer> keepRanks = new ArrayList<Integer>();
        if (rankType >= 5) {
            return new ArrayList<Integer>();
        }
        if (rankType == 4 || rankType == 8 || rankType == 7 || rankType == 6) {
            return new ArrayList<Integer>();
        }
        if (rankType == 3) {
            keepRanks.add(value.getTieBreakers()[0]);
            keepRanks.add(value.getTieBreakers()[1]);
        } else if (rankType == 2) {
            keepRanks.add(value.getTieBreakers()[0]);
        } else {
            List<Integer> sortedRanks = new ArrayList<Integer>();
            for (Card card : hand) {
                sortedRanks.add(card.getValue());
            }
            Collections.sort(sortedRanks, Collections.reverseOrder());
            int first = sortedRanks.get(0);
            int second = sortedRanks.get(1);
            keepRanks.add(first);
            if (second != first) {
                keepRanks.add(second);
            }
        }

        List<Integer> discard = new ArrayList<Integer>();
        for (int i = 0; i < hand.size(); i++) {
            if (!keepRanks.contains(hand.get(i).getValue())) {
                discard.add(i);
            }
        }
        return discard;
    }
}
