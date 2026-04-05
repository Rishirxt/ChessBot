package ui;

import bot.Bot;
import logic.MoveGenerator;
import logic.MoveHistory;
import model.Board;
import model.Move;
import model.Piece;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ChessUI extends JFrame {
    private static final Color APP_BG = new Color(24, 28, 34);
    private static final Color PANEL_BG = new Color(34, 39, 46);
    private static final Color PANEL_SOFT = new Color(43, 49, 58);
    private static final Color PANEL_EDGE = new Color(68, 76, 89);
    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);
    private static final Color SELECT_COLOR = new Color(87, 198, 255, 120);
    private static final Color LAST_MOVE_COLOR = new Color(246, 222, 120, 130);
    private static final Color HINT_COLOR = new Color(20, 25, 32, 70);
    private static final Color CAPTURE_HINT = new Color(215, 76, 76, 110);
    private static final Color TEXT_PRIMARY = new Color(234, 237, 243);
    private static final Color TEXT_MUTED = new Color(162, 170, 184);
    private static final Color STATUS_PLAYER = new Color(109, 196, 255);
    private static final Color STATUS_BOT = new Color(133, 214, 146);
    private static final Color STATUS_REPLAY = new Color(255, 202, 107);
    private static final Color STATUS_GAMEOVER = new Color(255, 120, 120);

    private Board liveBoard = new Board();
    private MoveHistory history = new MoveHistory(liveBoard);
    private Board viewBoard = liveBoard;

    private int selectedRow = -1;
    private int selectedCol = -1;
    private int viewIndex = -2;
    private Move lastMove;
    private boolean botThinking;
    private boolean gameOver;
    private boolean suppressHistoryEvents;

    private final List<Move> legalMovesForSelected = new ArrayList<>();
    private final List<Piece> capturedByWhite = new ArrayList<>();
    private final List<Piece> capturedByBlack = new ArrayList<>();

    private final ChessSquare[][] squares = new ChessSquare[8][8];
    private final DefaultListModel<String> historyModel = new DefaultListModel<>();

    private JLabel statusLabel;
    private JLabel statusMetaLabel;
    private JLabel capturedWhiteLabel;
    private JLabel capturedBlackLabel;
    private JList<String> historyList;
    private JButton btnFirst;
    private JButton btnPrev;
    private JButton btnNext;
    private JButton btnLast;

    private ListSelectionListener historySelectionListener;

    public ChessUI() {
        setTitle("ChessBot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(APP_BG);
        setLayout(new BorderLayout(0, 0));

        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildSidePanel(), BorderLayout.EAST);

        refreshBoard();
        updateCapturedLabels();
        showPlayerTurn();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(APP_BG);
        center.setBorder(new EmptyBorder(16, 16, 16, 10));

        center.add(buildCapturedPanel("Black captures", false), BorderLayout.NORTH);
        center.add(buildBoardShell(), BorderLayout.CENTER);
        center.add(buildCapturedPanel("White captures", true), BorderLayout.SOUTH);
        return center;
    }

    private JPanel buildCapturedPanel(String title, boolean whiteAtBottom) {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.setBackground(APP_BG);
        panel.setBorder(new EmptyBorder(0, 6, 0, 6));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT_MUTED);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel piecesLabel = new JLabel(" ");
        piecesLabel.setForeground(TEXT_PRIMARY);
        piecesLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(piecesLabel, BorderLayout.CENTER);

        if (whiteAtBottom) {
            capturedBlackLabel = piecesLabel;
        } else {
            capturedWhiteLabel = piecesLabel;
        }
        return panel;
    }

    private JPanel buildBoardShell() {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(PANEL_BG);
        shell.setBorder(new CompoundBorder(
                new LineBorder(PANEL_EDGE, 1, true),
                new EmptyBorder(12, 12, 12, 12)));
        shell.add(buildBoardWithCoords(), BorderLayout.CENTER);
        return shell;
    }

    private JPanel buildBoardWithCoords() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(PANEL_BG);

        JPanel rankPanel = new JPanel(new GridLayout(8, 1));
        rankPanel.setBackground(PANEL_BG);
        rankPanel.setPreferredSize(new Dimension(22, 0));
        for (int row = 0; row < 8; row++) {
            JLabel label = new JLabel(String.valueOf(8 - row), SwingConstants.CENTER);
            label.setForeground(TEXT_MUTED);
            label.setFont(new Font("Consolas", Font.BOLD, 12));
            rankPanel.add(label);
        }

        JPanel filePanel = new JPanel(new GridLayout(1, 8));
        filePanel.setBackground(PANEL_BG);
        filePanel.setPreferredSize(new Dimension(0, 22));
        for (int col = 0; col < 8; col++) {
            JLabel label = new JLabel(String.valueOf((char) ('a' + col)), SwingConstants.CENTER);
            label.setForeground(TEXT_MUTED);
            label.setFont(new Font("Consolas", Font.BOLD, 12));
            filePanel.add(label);
        }

        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        boardPanel.setPreferredSize(new Dimension(560, 560));
        boardPanel.setBorder(new LineBorder(new Color(17, 19, 24), 2));
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessSquare square = new ChessSquare(row, col);
                squares[row][col] = square;
                boardPanel.add(square);
            }
        }

        container.add(rankPanel, BorderLayout.WEST);
        container.add(boardPanel, BorderLayout.CENTER);
        container.add(filePanel, BorderLayout.SOUTH);
        return container;
    }

    private JPanel buildSidePanel() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(APP_BG);
        side.setBorder(new EmptyBorder(16, 6, 16, 16));
        side.setPreferredSize(new Dimension(280, 0));

        side.add(buildHeaderCard());
        side.add(Box.createVerticalStrut(12));
        side.add(buildStatusCard());
        side.add(Box.createVerticalStrut(12));
        side.add(buildHistoryCard());
        side.add(Box.createVerticalStrut(12));
        side.add(buildControlsCard());

        return side;
    }

    private JPanel buildHeaderCard() {
        JPanel panel = cardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("ChessBot");
        title.setForeground(TEXT_PRIMARY);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Play White, review every move, and challenge the engine.");
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitle);
        return panel;
    }

    private JPanel buildStatusCard() {
        JPanel panel = cardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel header = sectionHeader("Status");
        statusLabel = new JLabel("White to move");
        statusLabel.setForeground(STATUS_PLAYER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusMetaLabel = new JLabel("Select a white piece to see its legal moves.");
        statusMetaLabel.setForeground(TEXT_MUTED);
        statusMetaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusMetaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(header);
        panel.add(Box.createVerticalStrut(6));
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(3));
        panel.add(statusMetaLabel);
        return panel;
    }

    private JPanel buildHistoryCard() {
        JPanel panel = cardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(sectionHeader("Move History"));
        panel.add(Box.createVerticalStrut(8));

        historyList = new JList<>(historyModel);
        historyList.setBackground(PANEL_SOFT);
        historyList.setForeground(TEXT_PRIMARY);
        historyList.setFont(new Font("Consolas", Font.PLAIN, 13));
        historyList.setSelectionBackground(new Color(76, 122, 181));
        historyList.setSelectionForeground(Color.WHITE);
        historyList.setFixedCellHeight(26);
        historyList.setCellRenderer(new MoveHistoryCellRenderer());
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        historySelectionListener = e -> {
            if (!e.getValueIsAdjusting() && !suppressHistoryEvents) {
                int selectedIndex = historyList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    navigateTo(selectedIndex);
                }
            }
        };
        historyList.addListSelectionListener(historySelectionListener);

        JScrollPane scrollPane = new JScrollPane(historyList);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setBorder(new LineBorder(PANEL_EDGE));
        scrollPane.setPreferredSize(new Dimension(240, 330));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 330));
        scrollPane.getViewport().setBackground(PANEL_SOFT);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane);
        return panel;
    }

    private JPanel buildControlsCard() {
        JPanel panel = cardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(sectionHeader("Controls"));
        panel.add(Box.createVerticalStrut(8));

        JPanel navPanel = new JPanel(new GridLayout(1, 4, 6, 0));
        navPanel.setBackground(PANEL_BG);
        btnFirst = navButton("|<", "Go to start");
        btnPrev = navButton("<", "Previous move");
        btnNext = navButton(">", "Next move");
        btnLast = navButton(">|", "Go to live board");

        btnFirst.addActionListener(e -> navigateTo(-1));
        btnPrev.addActionListener(e -> {
            int current = (viewIndex == -2) ? history.size() - 1 : viewIndex;
            navigateTo(current - 1);
        });
        btnNext.addActionListener(e -> {
            int current = (viewIndex == -2) ? history.size() - 1 : viewIndex;
            if (current + 1 >= history.size()) {
                navigateLive();
            } else {
                navigateTo(current + 1);
            }
        });
        btnLast.addActionListener(e -> navigateLive());

        navPanel.add(btnFirst);
        navPanel.add(btnPrev);
        navPanel.add(btnNext);
        navPanel.add(btnLast);

        JButton resetButton = new JButton("New Game");
        resetButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        resetButton.setForeground(TEXT_PRIMARY);
        resetButton.setBackground(new Color(73, 120, 178));
        resetButton.setBorder(new CompoundBorder(
                new LineBorder(new Color(92, 143, 204), 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        resetButton.setFocusPainted(false);
        resetButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> resetGame());

        panel.add(navPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(resetButton);
        return panel;
    }

    private JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_BG);
        panel.setBorder(new CompoundBorder(
                new LineBorder(PANEL_EDGE, 1, true),
                new EmptyBorder(12, 12, 12, 12)));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JLabel sectionHeader(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_PRIMARY);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton navButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setFont(new Font("Consolas", Font.BOLD, 14));
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(PANEL_SOFT);
        button.setBorder(new CompoundBorder(
                new LineBorder(PANEL_EDGE, 1, true),
                new EmptyBorder(6, 0, 6, 0)));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void navigateTo(int index) {
        if (index < -1) {
            index = -1;
        }
        if (index >= history.size()) {
            navigateLive();
            return;
        }

        viewIndex = index;
        viewBoard = history.getBoardAt(index);
        lastMove = index >= 0 ? history.get(index).move : null;

        setHistorySelection(index);
        clearSelection();
        refreshBoard();
        updateNavigationButtons();

        if (index < 0) {
            setStatus("Replay", "Initial position", STATUS_REPLAY);
        } else {
            String notation = history.get(index).notation;
            setStatus("Replay", "Move " + (index + 1) + " - " + notation, STATUS_REPLAY);
        }
    }

    private void navigateLive() {
        viewIndex = -2;
        viewBoard = liveBoard;
        lastMove = history.isEmpty() ? null : history.get(history.size() - 1).move;

        setHistorySelection(history.size() - 1);
        clearSelection();
        refreshBoard();
        updateNavigationButtons();

        if (!gameOver) {
            if (botThinking) {
                showBotThinking();
            } else {
                showPlayerTurn();
            }
        }
    }

    private void handleClick(int row, int col) {
        if (botThinking || gameOver) {
            return;
        }
        if (!isLiveView()) {
            navigateLive();
            return;
        }

        Piece clickedPiece = liveBoard.board[row][col];
        if (selectedRow == -1) {
            if (clickedPiece != null && clickedPiece.isWhite) {
                selectSquare(row, col);
            }
            return;
        }

        if (clickedPiece != null && clickedPiece.isWhite) {
            selectSquare(row, col);
            return;
        }

        Move chosenMove = null;
        for (Move move : legalMovesForSelected) {
            if (move.toRow == row && move.toCol == col) {
                chosenMove = move;
                break;
            }
        }

        if (chosenMove == null) {
            clearSelection();
            refreshBoard();
            showPlayerTurn();
            return;
        }

        applyPlayerMove(chosenMove);
    }

    private void selectSquare(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        legalMovesForSelected.clear();
        for (Move move : MoveGenerator.getMoves(liveBoard, true)) {
            if (move.fromRow == row && move.fromCol == col) {
                legalMovesForSelected.add(move);
            }
        }
        refreshBoard();
        Piece piece = liveBoard.board[row][col];
        setStatus("Piece selected", piece.type + " on " + squareName(row, col), STATUS_PLAYER);
    }

    private void applyPlayerMove(Move move) {
        Piece captured = liveBoard.board[move.toRow][move.toCol];
        if (captured != null) {
            capturedByWhite.add(captured);
        }

        liveBoard.makeMove(move);
        lastMove = move;
        history.record(liveBoard, move, true);
        addToHistoryPanel(history.size() - 1, history.get(history.size() - 1).notation);

        clearSelection();
        viewBoard = liveBoard;
        refreshBoard();
        updateCapturedLabels();
        updateNavigationButtons();

        if (checkGameOver(false)) {
            return;
        }

        if (MoveGenerator.inCheck(liveBoard, false)) {
            setStatus("Check", "Black king is under attack", STATUS_REPLAY);
        }

        botThinking = true;
        if (!MoveGenerator.inCheck(liveBoard, false)) {
            showBotThinking();
        }

        SwingWorker<Move, Void> worker = new SwingWorker<>() {
            @Override
            protected Move doInBackground() throws Exception {
                Thread.sleep(450);
                return Bot.getBestMove(liveBoard);
            }

            @Override
            protected void done() {
                try {
                    Move botMove = get();
                    if (botMove != null) {
                        Piece botCaptured = liveBoard.board[botMove.toRow][botMove.toCol];
                        if (botCaptured != null) {
                            capturedByBlack.add(botCaptured);
                        }

                        liveBoard.makeMove(botMove);
                        lastMove = botMove;
                        history.record(liveBoard, botMove, false);
                        addToHistoryPanel(history.size() - 1, history.get(history.size() - 1).notation);
                    }

                    botThinking = false;
                    viewBoard = liveBoard;
                    refreshBoard();
                    updateCapturedLabels();
                    updateNavigationButtons();

                    if (!checkGameOver(true)) {
                        if (MoveGenerator.inCheck(liveBoard, true)) {
                            setStatus("Check", "White king is under attack", STATUS_REPLAY);
                        } else {
                            showPlayerTurn();
                        }
                    }
                } catch (Exception ex) {
                    botThinking = false;
                    setStatus("Error", "Bot move failed: " + ex.getMessage(), STATUS_GAMEOVER);
                }
            }
        };
        worker.execute();
    }

    private boolean checkGameOver(boolean whiteToMove) {
        List<Move> legalMoves = MoveGenerator.getMoves(liveBoard, whiteToMove);
        if (!legalMoves.isEmpty()) {
            return false;
        }

        String message;
        if (MoveGenerator.inCheck(liveBoard, whiteToMove)) {
            message = "Checkmate! " + (whiteToMove ? "Black" : "White") + " wins.";
        } else {
            message = "Stalemate! The game is drawn.";
        }

        gameOver = true;
        clearSelection();
        refreshBoard();
        setStatus("Game Over", message, STATUS_GAMEOVER);
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE));
        return true;
    }

    private void addToHistoryPanel(int historyIndex, String notation) {
        historyModel.addElement(formatHistoryEntry(historyIndex, notation));
        setHistorySelection(historyModel.size() - 1);
        historyList.ensureIndexIsVisible(historyModel.size() - 1);
    }

    private String formatHistoryEntry(int historyIndex, String notation) {
        int moveNumber = (historyIndex / 2) + 1;
        return (historyIndex % 2 == 0)
                ? moveNumber + ". " + notation
                : moveNumber + "... " + notation;
    }

    private void refreshBoard() {
        Board board = viewBoard != null ? viewBoard : liveBoard;

        boolean[][] hintSquares = new boolean[8][8];
        boolean[][] captureSquares = new boolean[8][8];
        if (isLiveView() && selectedRow >= 0) {
            for (Move move : legalMovesForSelected) {
                hintSquares[move.toRow][move.toCol] = true;
                if (board.board[move.toRow][move.toCol] != null) {
                    captureSquares[move.toRow][move.toCol] = true;
                }
            }
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.board[row][col];
                boolean isSelected = isLiveView() && row == selectedRow && col == selectedCol;
                boolean isLastFrom = lastMove != null && row == lastMove.fromRow && col == lastMove.fromCol;
                boolean isLastTo = lastMove != null && row == lastMove.toRow && col == lastMove.toCol;
                squares[row][col].update(piece, isSelected, isLastFrom || isLastTo, hintSquares[row][col], captureSquares[row][col]);
            }
        }
    }

    private void updateCapturedLabels() {
        capturedBlackLabel.setText(piecesToString(capturedByWhite));
        capturedWhiteLabel.setText(piecesToString(capturedByBlack));
    }

    private String piecesToString(List<Piece> pieces) {
        if (pieces.isEmpty()) {
            return " ";
        }

        StringBuilder builder = new StringBuilder("<html>");
        for (Piece piece : pieces) {
            builder.append(piece.getSymbol()).append("&nbsp;");
        }
        builder.append("</html>");
        return builder.toString();
    }

    private void resetGame() {
        liveBoard = new Board();
        history = new MoveHistory(liveBoard);
        viewBoard = liveBoard;
        lastMove = null;
        viewIndex = -2;
        botThinking = false;
        gameOver = false;

        capturedByWhite.clear();
        capturedByBlack.clear();
        legalMovesForSelected.clear();
        selectedRow = -1;
        selectedCol = -1;
        historyModel.clear();

        updateCapturedLabels();
        refreshBoard();
        updateNavigationButtons();
        showPlayerTurn();
    }

    private void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        legalMovesForSelected.clear();
    }

    private void showPlayerTurn() {
        setStatus("Your turn", "White to move", STATUS_PLAYER);
    }

    private void showBotThinking() {
        setStatus("Bot thinking", "Black is choosing a move", STATUS_BOT);
    }

    private void setStatus(String headline, String detail, Color color) {
        statusLabel.setText(headline);
        statusLabel.setForeground(color);
        statusMetaLabel.setText(detail);
    }

    private void setHistorySelection(int index) {
        suppressHistoryEvents = true;
        historyList.setSelectedIndex(index);
        suppressHistoryEvents = false;
    }

    private void updateNavigationButtons() {
        int current = viewIndex == -2 ? history.size() - 1 : viewIndex;
        boolean hasHistory = history.size() > 0;
        btnFirst.setEnabled(hasHistory && current >= 0);
        btnPrev.setEnabled(hasHistory && current >= 0);
        btnNext.setEnabled(hasHistory && viewIndex != -2);
        btnLast.setEnabled(hasHistory && viewIndex != -2);
    }

    private boolean isLiveView() {
        return viewIndex == -2;
    }

    private String squareName(int row, int col) {
        return String.valueOf((char) ('a' + col)) + (8 - row);
    }

    private class ChessSquare extends JPanel {
        private final int row;
        private final int col;
        private Piece piece;
        private boolean selected;
        private boolean lastMove;
        private boolean hint;
        private boolean captureHint;

        ChessSquare(int row, int col) {
            this.row = row;
            this.col = col;
            setPreferredSize(new Dimension(70, 70));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handleClick(row, col);
                }
            });
        }

        void update(Piece piece, boolean selected, boolean lastMove, boolean hint, boolean captureHint) {
            this.piece = piece;
            this.selected = selected;
            this.lastMove = lastMove;
            this.hint = hint;
            this.captureHint = captureHint;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            g2.setColor(((row + col) % 2 == 0) ? LIGHT_SQUARE : DARK_SQUARE);
            g2.fillRect(0, 0, width, height);

            if (lastMove) {
                g2.setColor(LAST_MOVE_COLOR);
                g2.fillRect(0, 0, width, height);
            }
            if (selected) {
                g2.setColor(SELECT_COLOR);
                g2.fillRect(0, 0, width, height);
            }

            if (captureHint) {
                g2.setColor(CAPTURE_HINT);
                g2.setStroke(new BasicStroke(4f));
                g2.drawOval(6, 6, width - 12, height - 12);
            } else if (hint) {
                g2.setColor(HINT_COLOR);
                int dotSize = width / 3;
                g2.fillOval((width - dotSize) / 2, (height - dotSize) / 2, dotSize, dotSize);
            }

            if (piece != null) {
                String symbol = piece.getSymbol();
                g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 48));
                FontMetrics fm = g2.getFontMetrics();
                int x = (width - fm.stringWidth(symbol)) / 2;
                int y = (height - fm.getHeight()) / 2 + fm.getAscent();

                if (piece.isWhite) {
                    g2.setColor(new Color(45, 48, 56));
                    g2.drawString(symbol, x - 1, y);
                    g2.drawString(symbol, x + 1, y);
                    g2.drawString(symbol, x, y - 1);
                    g2.drawString(symbol, x, y + 1);
                    g2.setColor(new Color(248, 250, 252));
                } else {
                    g2.setColor(new Color(18, 20, 26));
                }
                g2.drawString(symbol, x, y);
            }

            g2.dispose();
        }
    }

    private class MoveHistoryCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setFont(new Font("Consolas", Font.PLAIN, 13));
            label.setBorder(new EmptyBorder(4, 8, 4, 8));
            if (isSelected) {
                label.setBackground(new Color(76, 122, 181));
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(index % 2 == 0 ? PANEL_SOFT : PANEL_BG);
                label.setForeground(TEXT_PRIMARY);
            }
            return label;
        }
    }
}
