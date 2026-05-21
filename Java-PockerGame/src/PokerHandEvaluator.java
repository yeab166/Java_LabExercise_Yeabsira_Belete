import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PokerHandEvaluator {
    public static class HandValue implements Comparable<HandValue> {
        private final int rank;
        private final int[] tieBreakers;

        public HandValue(int rank, int[] tieBreakers) {
            this.rank = rank;
            this.tieBreakers = tieBreakers;
        }

        public int getRank() {
            return rank;
        }

        public int[] getTieBreakers() {
            return tieBreakers;
        }

        public String getName() {
            switch (rank) {
                case 9: return "Straight Flush";
                case 8: return "Four of a Kind";
                case 7: return "Full House";
                case 6: return "Flush";
                case 5: return "Straight";
                case 4: return "Three of a Kind";
                case 3: return "Two Pair";
                case 2: return "One Pair";
                default: return "High Card";
            }
        }

        @Override
        public int compareTo(HandValue other) {
            if (rank != other.rank) {
                return Integer.compare(rank, other.rank);
            }
            for (int i = 0; i < tieBreakers.length && i < other.tieBreakers.length; i++) {
                if (tieBreakers[i] != other.tieBreakers[i]) {
                    return Integer.compare(tieBreakers[i], other.tieBreakers[i]);
                }
            }
            return 0;
        }
    }

    public static HandValue evaluate(List<Card> hand) {
        if (hand == null || hand.size() != 5) {
            throw new IllegalArgumentException("Hand must contain exactly 5 cards.");
        }

        List<Integer> ranks = new ArrayList<Integer>();
        Map<Card.Suit, Integer> suitCount = new HashMap<Card.Suit, Integer>();
        Map<Integer, Integer> rankCount = new HashMap<Integer, Integer>();
        for (Card card : hand) {
            int value = card.getValue();
            ranks.add(value);
            suitCount.put(card.getSuit(), suitCount.getOrDefault(card.getSuit(), 0) + 1);
            rankCount.put(value, rankCount.getOrDefault(value, 0) + 1);
        }

        Collections.sort(ranks, Collections.reverseOrder());
        boolean flush = suitCount.size() == 1;
        boolean straight = isStraight(ranks);
        int highStraightValue = straight ? getStraightHighValue(ranks) : 0;

        List<Map.Entry<Integer, Integer>> grouped = new ArrayList<Map.Entry<Integer, Integer>>(rankCount.entrySet());
        Collections.sort(grouped, (a, b) -> {
            if (!a.getValue().equals(b.getValue())) {
                return b.getValue() - a.getValue();
            }
            return b.getKey() - a.getKey();
        });

        int primaryRank = grouped.get(0).getKey();
        int primaryCount = grouped.get(0).getValue();
        int secondaryRank = grouped.size() > 1 ? grouped.get(1).getKey() : 0;
        int secondaryCount = grouped.size() > 1 ? grouped.get(1).getValue() : 0;

        if (straight && flush) {
            return new HandValue(9, new int[] {highStraightValue});
        }
        if (primaryCount == 4) {
            int kicker = getKicker(rankCount, primaryRank);
            return new HandValue(8, new int[] {primaryRank, kicker});
        }
        if (primaryCount == 3 && secondaryCount == 2) {
            return new HandValue(7, new int[] {primaryRank, secondaryRank});
        }
        if (flush) {
            return new HandValue(6, toArray(ranks));
        }
        if (straight) {
            return new HandValue(5, new int[] {highStraightValue});
        }
        if (primaryCount == 3) {
            int[] kickers = getKickers(rankCount, new int[] {primaryRank});
            return new HandValue(4, join(new int[] {primaryRank}, kickers));
        }
        if (primaryCount == 2 && secondaryCount == 2) {
            int kicker = getKicker(rankCount, primaryRank, secondaryRank);
            return new HandValue(3, new int[] {Math.max(primaryRank, secondaryRank), Math.min(primaryRank, secondaryRank), kicker});
        }
        if (primaryCount == 2) {
            int[] kickers = getKickers(rankCount, new int[] {primaryRank});
            return new HandValue(2, join(new int[] {primaryRank}, kickers));
        }
        return new HandValue(1, toArray(ranks));
    }

    public static int compareHands(List<Card> first, List<Card> second) {
        return evaluate(first).compareTo(evaluate(second));
    }

    private static boolean isStraight(List<Integer> ranks) {
        List<Integer> sorted = new ArrayList<Integer>(ranks);
        Collections.sort(sorted);
        boolean normal = true;
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i) != sorted.get(i - 1) + 1) {
                normal = false;
                break;
            }
        }
        boolean wheel = sorted.get(0) == 2 && sorted.get(1) == 3 && sorted.get(2) == 4 && sorted.get(3) == 5 && sorted.get(4) == 14;
        return normal || wheel;
    }

    private static int getStraightHighValue(List<Integer> ranks) {
        List<Integer> descending = new ArrayList<Integer>(ranks);
        Collections.sort(descending, Collections.reverseOrder());
        if (descending.equals(java.util.Arrays.asList(14, 5, 4, 3, 2))) {
            return 5;
        }
        return descending.get(0);
    }

    private static int[] toArray(List<Integer> values) {
        int[] array = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            array[i] = values.get(i);
        }
        return array;
    }

    private static int getKicker(Map<Integer, Integer> rankCount, int excludeRank) {
        int kicker = 0;
        for (int rank : rankCount.keySet()) {
            if (rank != excludeRank && rank > kicker) {
                kicker = rank;
            }
        }
        return kicker;
    }

    private static int getKicker(Map<Integer, Integer> rankCount, int excludeRank1, int excludeRank2) {
        int kicker = 0;
        for (int rank : rankCount.keySet()) {
            if (rank != excludeRank1 && rank != excludeRank2 && rank > kicker) {
                kicker = rank;
            }
        }
        return kicker;
    }

    private static int[] getKickers(Map<Integer, Integer> rankCount, int[] excludes) {
        List<Integer> kickers = new ArrayList<Integer>();
        for (int rank : rankCount.keySet()) {
            boolean excluded = false;
            for (int value : excludes) {
                if (rank == value) {
                    excluded = true;
                    break;
                }
            }
            if (!excluded) {
                for (int i = 0; i < rankCount.get(rank); i++) {
                    kickers.add(rank);
                }
            }
        }
        Collections.sort(kickers, Collections.reverseOrder());
        int[] result = new int[kickers.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = kickers.get(i);
        }
        return result;
    }

    private static int[] join(int[] first, int[] second) {
        int[] combined = new int[first.length + second.length];
        System.arraycopy(first, 0, combined, 0, first.length);
        System.arraycopy(second, 0, combined, first.length, second.length);
        return combined;
    }
}
