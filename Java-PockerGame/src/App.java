import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class App extends JFrame {
    private final PokerGame game;
    private final JToggleButton[] playerCardButtons;
    private final JLabel[] cpuCardLabels;
    private final JLabel bankrollLabel;
    private final JLabel potLabel;
    private final JLabel statusLabel;
    private final JButton dealButton;
    private final JButton drawButton;
    private final JButton resetButton;

    public App() {
        game = new PokerGame();
        playerCardButtons = new JToggleButton[5];
        cpuCardLabels = new JLabel[5];

        setTitle("Java Poker Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(700, 520));
        setLayout(new BorderLayout(8, 8));

        bankrollLabel = new JLabel();
        potLabel = new JLabel();
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 14f));

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        topPanel.add(bankrollLabel);
        topPanel.add(potLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 8, 8));

        JPanel cpuPanel = new JPanel(new BorderLayout(4, 4));
        JLabel cpuTitle = new JLabel("Dealer Hand", SwingConstants.CENTER);
        cpuTitle.setFont(cpuTitle.getFont().deriveFont(Font.BOLD, 16f));
        cpuPanel.add(cpuTitle, BorderLayout.NORTH);
        JPanel cpuCardsPanel = new JPanel(new GridLayout(1, 5, 8, 8));
        for (int i = 0; i < cpuCardLabels.length; i++) {
            cpuCardLabels[i] = new JLabel("🂠", SwingConstants.CENTER);
            cpuCardLabels[i].setOpaque(true);
            cpuCardLabels[i].setBackground(Color.DARK_GRAY);
            cpuCardLabels[i].setForeground(Color.WHITE);
            cpuCardLabels[i].setFont(new Font("SansSerif", Font.BOLD, 28));
            cpuCardLabels[i].setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));
            cpuCardsPanel.add(cpuCardLabels[i]);
        }
        cpuPanel.add(cpuCardsPanel, BorderLayout.CENTER);
        centerPanel.add(cpuPanel);

        JPanel playerPanel = new JPanel(new BorderLayout(4, 4));
        JLabel playerTitle = new JLabel("Your Hand", SwingConstants.CENTER);
        playerTitle.setFont(playerTitle.getFont().deriveFont(Font.BOLD, 16f));
        playerPanel.add(playerTitle, BorderLayout.NORTH);
        JPanel playerCardsPanel = new JPanel(new GridLayout(1, 5, 8, 8));
        for (int i = 0; i < playerCardButtons.length; i++) {
            playerCardButtons[i] = new JToggleButton("—");
            playerCardButtons[i].setFont(new Font("SansSerif", Font.BOLD, 26));
            playerCardButtons[i].setEnabled(false);
            playerCardsPanel.add(playerCardButtons[i]);
        }
        playerPanel.add(playerCardsPanel, BorderLayout.CENTER);
        centerPanel.add(playerPanel);

        add(centerPanel, BorderLayout.CENTER);

        dealButton = new JButton("Deal");
        drawButton = new JButton("Draw");
        resetButton = new JButton("Reset Game");

        dealButton.addActionListener(this::onDeal);
        drawButton.addActionListener(this::onDraw);
        resetButton.addActionListener(e -> {
            game.reset();
            updateUi();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(dealButton);
        buttonPanel.add(drawButton);
        buttonPanel.add(resetButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(statusLabel);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(buttonPanel);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        updateUi();
        setVisible(true);
    }

    private void onDeal(ActionEvent event) {
        game.startRound();
        updateUi();
    }

    private void onDraw(ActionEvent event) {
        List<Integer> discards = new ArrayList<Integer>();
        for (int i = 0; i < playerCardButtons.length; i++) {
            if (playerCardButtons[i].isSelected()) {
                discards.add(i);
            }
        }
        game.draw(discards);
        updateUi();
    }

    private void updateUi() {
        bankrollLabel.setText("Your Bankroll: $" + game.getHuman().getBankroll());
        potLabel.setText("Pot: $" + game.getPot());
        statusLabel.setText(game.getStatus());

        boolean canDeal = game.canStartRound();
        boolean canDraw = game.getPhase() == PokerGame.Phase.DEAL;

        dealButton.setEnabled(canDeal);
        drawButton.setEnabled(canDraw);

        for (int i = 0; i < playerCardButtons.length; i++) {
            Card card = null;
            if (game.getHuman().getHand().size() > i) {
                card = game.getHuman().getHand().get(i);
            }
            if (card != null) {
                playerCardButtons[i].setText(card.toString());
            } else {
                playerCardButtons[i].setText("—");
            }
            playerCardButtons[i].setEnabled(canDraw);
            playerCardButtons[i].setSelected(false);
            playerCardButtons[i].setBackground(canDraw && playerCardButtons[i].isSelected() ? Color.YELLOW : null);
        }

        for (int i = 0; i < cpuCardLabels.length; i++) {
            if (game.getPhase() == PokerGame.Phase.DEAL) {
                cpuCardLabels[i].setText("🂠");
                cpuCardLabels[i].setBackground(Color.DARK_GRAY);
            } else {
                List<Card> cpuHand = game.getCpu().getHand();
                if (cpuHand.size() > i) {
                    cpuCardLabels[i].setText(cpuHand.get(i).toString());
                } else {
                    cpuCardLabels[i].setText("—");
                }
                cpuCardLabels[i].setBackground(Color.BLACK);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App());
    }
}
