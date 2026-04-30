package cp3project;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * PlagiarismGUI — drop-in replacement for PlagiarismDemo.
 * Place this file in your cp3project package alongside the other .java files.
 * Run it instead of PlagiarismDemo; all other classes are unchanged.
 */
public class PlagiarismGUI extends JFrame {

    // ── Palette ─────────────────────────────────────────────────────────────
    private static final Color BG        = new Color(0x0F, 0x11, 0x17);
    private static final Color PANEL     = new Color(0x1A, 0x1D, 0x27);
    private static final Color CARD      = new Color(0x22, 0x26, 0x36);
    private static final Color BORDER_C  = new Color(0x2A, 0x2D, 0x3A);
    private static final Color TEXT_PRI  = new Color(0xE8, 0xEA, 0xF0);
    private static final Color TEXT_SEC  = new Color(0x7C, 0x80, 0xA0);
    private static final Color ACCENT    = new Color(0x5B, 0x8D, 0xFF);
    private static final Color HIGH_C    = new Color(0xFF, 0x5F, 0x6D);
    private static final Color MID_C     = new Color(0xFF, 0xC9, 0x3C);
    private static final Color LOW_C     = new Color(0x43, 0xE9, 0x7B);

    // ── Fonts ────────────────────────────────────────────────────────────────
    private static final Font  FONT_TITLE  = new Font("SansSerif", Font.BOLD,  28);
    private static final Font  FONT_LABEL  = new Font("Monospaced", Font.BOLD, 15);
    private static final Font  FONT_BODY   = new Font("Monospaced", Font.PLAIN, 15);
    private static final Font  FONT_SMALL  = new Font("Monospaced", Font.PLAIN, 13);
    private static final Font  FONT_SCORE  = new Font("Monospaced", Font.BOLD, 15);

    // ── UI components ────────────────────────────────────────────────────────
    private JTextField    pathField;
    private JSpinner      kSpinner;
    private JButton       browseBtn, runBtn;
    private JTable        resultsTable;
    private DefaultTableModel tableModel;
    private JLabel        statusLabel;
    private JPanel        statsPanel;
    private JLabel        statTotal, statHigh, statMod, statLow;
    private JProgressBar  progressBar;

    public PlagiarismGUI() {
        super("Plagiarism Detection System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1050, 780);
        setMinimumSize(new Dimension(860, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        buildHeader();
        buildCenter();
        buildFooter();

        setVisible(true);
    }

    // ── Header ───────────────────────────────────────────────────────────────
    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(PANEL);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_C),
            new EmptyBorder(18, 24, 18, 24)
        ));

        // Title block
        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("PLAGIARISM DETECTOR");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRI);
        JLabel sub = new JLabel("Jaccard Similarity · k-Shingling · Merge Sort");
        sub.setFont(FONT_SMALL);
        sub.setForeground(TEXT_SEC);
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(2));
        titleBlock.add(sub);

        // Controls row
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        // Folder path field
        pathField = styledTextField(36);
        pathField.setToolTipText("Path to folder containing .txt documents");

        browseBtn = styledButton("BROWSE", ACCENT);
        browseBtn.addActionListener(e -> browse());

        // k-spinner
        JLabel kLabel = new JLabel("k =");
        kLabel.setFont(FONT_LABEL);
        kLabel.setForeground(TEXT_SEC);
        kSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        styleSpinner(kSpinner);

        runBtn = styledButton("▶  RUN", new Color(0x43, 0xE9, 0x7B));
        runBtn.setForeground(BG);
        runBtn.addActionListener(e -> run());

        controls.add(pathField);
        controls.add(browseBtn);
        controls.add(Box.createHorizontalStrut(8));
        controls.add(kLabel);
        controls.add(kSpinner);
        controls.add(Box.createHorizontalStrut(8));
        controls.add(runBtn);

        header.add(titleBlock, BorderLayout.WEST);
        header.add(controls,   BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    // ── Center ───────────────────────────────────────────────────────────────
    private void buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(16, 20, 0, 20));

        // Stats row
        statsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        statsPanel.setOpaque(false);
        statTotal = new JLabel("—"); statHigh = new JLabel("—");
        statMod   = new JLabel("—"); statLow  = new JLabel("—");
        statsPanel.add(statCard("TOTAL PAIRS",    statTotal, TEXT_SEC));
        statsPanel.add(statCard("HIGH  (≥ 0.70)", statHigh,  HIGH_C));
        statsPanel.add(statCard("MODERATE",        statMod,   MID_C));
        statsPanel.add(statCard("LOW  (< 0.30)",   statLow,   LOW_C));

        // Table
        String[] cols = { "#", "Document 1", "Document 2", "Score", "Level" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        resultsTable = new JTable(tableModel);
        styleTable(resultsTable);

        JScrollPane scroll = new JScrollPane(resultsTable);
        scroll.setBackground(PANEL);
        scroll.getViewport().setBackground(PANEL);
        scroll.setBorder(new LineBorder(BORDER_C, 1));

        center.add(statsPanel, BorderLayout.NORTH);
        center.add(scroll,     BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    // ── Footer ───────────────────────────────────────────────────────────────
    private void buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setBackground(PANEL);
        footer.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER_C),
            new EmptyBorder(8, 20, 8, 20)
        ));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setValue(0);
        progressBar.setBackground(CARD);
        progressBar.setForeground(ACCENT);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(200, 6));

        statusLabel = new JLabel("Ready — choose a folder and press RUN.");
        statusLabel.setFont(FONT_SMALL);
        statusLabel.setForeground(TEXT_SEC);

        footer.add(statusLabel,  BorderLayout.CENTER);
        footer.add(progressBar,  BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
    }

    // ── Actions ──────────────────────────────────────────────────────────────
    private void browse() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Select document folder");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            pathField.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }

    private void run() {
        String folderPath = pathField.getText().trim();
        if (folderPath.isEmpty()) {
            status("⚠  Please enter or browse to a folder path.", TEXT_SEC);
            return;
        }
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            status("✗  Folder not found: " + folderPath, HIGH_C);
            return;
        }

        int k = (int) kSpinner.getValue();
        runBtn.setEnabled(false);
        progressBar.setIndeterminate(true);
        status("Processing…", ACCENT);
        tableModel.setRowCount(0);
        resetStats();

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            List<Merging.DocPair> sortedPairs;
            List<String>          docNames;
            String                errorMsg;

            @Override
            protected Void doInBackground() {
                try {
                    // ── same logic as PlagiarismDemo.main ──
                    List<String[]> allTokens = Reader.read_docs(folderPath);
                    if (allTokens.isEmpty()) {
                        errorMsg = "No .txt documents found in that folder.";
                        return null;
                    }

                    // Collect doc names in the same order Reader gives tokens
                    File[] files = folder.listFiles();
                    docNames = new ArrayList<>();
                    if (files != null) {
                        Arrays.sort(files); // keep consistent ordering
                        for (File f : files)
                            if (f.getName().endsWith(".txt"))
                                docNames.add(f.getName().replace(".txt", ""));
                    }

                    Shingler  shingler  = new Shingler(k);
                    HashTable hashTable = new HashTable(1000);
                    List<int[]> allHashed = new ArrayList<>();
                    for (String[] tokens : allTokens)
                        allHashed.add(shingler.generateHashedShingles(tokens, hashTable));

                    Merging merger = new Merging();
                    List<Merging.DocPair> results = merger.compareAll(allHashed, shingler);
                    Merging.DocPair[] arr = results.toArray(new Merging.DocPair[0]);
                    merger.mergeSort(arr, 0, arr.length - 1);
                    sortedPairs = Arrays.asList(arr);

                } catch (Exception ex) {
                    errorMsg = ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setValue(100);
                runBtn.setEnabled(true);

                if (errorMsg != null) {
                    status("✗  " + errorMsg, HIGH_C);
                    return;
                }
                if (sortedPairs == null || sortedPairs.isEmpty()) {
                    status("No pairs computed — need at least 2 documents.", TEXT_SEC);
                    return;
                }

                // Populate table
                int high = 0, mod = 0, low = 0;
                for (int i = 0; i < sortedPairs.size(); i++) {
                    Merging.DocPair p = sortedPairs.get(i);
                    String name1 = (p.docId1 < docNames.size()) ? docNames.get(p.docId1) : "doc" + p.docId1;
                    String name2 = (p.docId2 < docNames.size()) ? docNames.get(p.docId2) : "doc" + p.docId2;
                    String level;
                    if (p.score >= 0.7) { level = "HIGH";     high++; }
                    else if (p.score >= 0.3) { level = "MODERATE"; mod++;  }
                    else                 { level = "LOW";      low++;  }
                    tableModel.addRow(new Object[]{ i + 1, name1, name2,
                        String.format("%.4f", p.score), level });
                }

                // Update stats
                statTotal.setText(String.valueOf(sortedPairs.size()));
                statHigh.setText(String.valueOf(high));
                statMod.setText(String.valueOf(mod));
                statLow.setText(String.valueOf(low));

                status("✓  Done — " + sortedPairs.size() + " pairs compared (k=" + k + ")", LOW_C);
            }
        };
        worker.execute();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private void status(String msg, Color c) {
        statusLabel.setText(msg);
        statusLabel.setForeground(c);
    }

    private void resetStats() {
        for (JLabel l : new JLabel[]{ statTotal, statHigh, statMod, statLow })
            l.setText("—");
    }

    // ── Styled widgets ───────────────────────────────────────────────────────
    private JTextField styledTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setBackground(CARD);
        tf.setForeground(TEXT_PRI);
        tf.setCaretColor(ACCENT);
        tf.setFont(FONT_BODY);
        tf.setBorder(new CompoundBorder(
            new LineBorder(BORDER_C, 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getBackground();
                if (getModel().isPressed())
                    base = base.darker();
                else if (getModel().isRollover())
                    base = base.brighter();
                g2.setColor(base);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setBackground(bg);
        btn.setForeground(TEXT_PRI);
        btn.setFont(FONT_LABEL);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 16, 7, 16));
        return btn;
    }

    private void styleSpinner(JSpinner s) {
        s.setBackground(CARD);
        s.setForeground(TEXT_PRI);
        JComponent editor = s.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(CARD);
            tf.setForeground(TEXT_PRI);
            tf.setFont(FONT_BODY);
            tf.setCaretColor(ACCENT);
            tf.setBorder(new EmptyBorder(4, 6, 4, 6));
            tf.setHorizontalAlignment(JTextField.CENTER);
        }
        s.setBorder(new LineBorder(BORDER_C, 1));
        s.setPreferredSize(new Dimension(60, 30));
    }

    private void styleTable(JTable t) {
        t.setBackground(PANEL);
        t.setForeground(TEXT_PRI);
        t.setFont(FONT_BODY);
        t.setGridColor(BORDER_C);
        t.setRowHeight(38);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(CARD);
        t.setSelectionForeground(TEXT_PRI);
        t.setIntercellSpacing(new Dimension(0, 1));

        // Header
        JTableHeader h = t.getTableHeader();
        h.setBackground(CARD);
        h.setForeground(TEXT_SEC);
        h.setFont(FONT_LABEL);
        h.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_C));
        h.setReorderingAllowed(false);

        // Column widths
        int[] widths = {40, 210, 210, 90, 100};
        for (int i = 0; i < widths.length; i++)
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Custom cell renderer for level & score columns
        t.getColumnModel().getColumn(3).setCellRenderer(scoreRenderer());
        t.getColumnModel().getColumn(4).setCellRenderer(levelRenderer());

        // Centre # column
        DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
        centre.setHorizontalAlignment(SwingConstants.CENTER);
        centre.setBackground(PANEL);
        centre.setForeground(TEXT_SEC);
        t.getColumnModel().getColumn(0).setCellRenderer(centre);
    }

    private TableCellRenderer scoreRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                setFont(FONT_SCORE);
                double score = Double.parseDouble(v.toString());
                if (score >= 0.7)      setForeground(HIGH_C);
                else if (score >= 0.3) setForeground(MID_C);
                else                   setForeground(LOW_C);
                setBackground(sel ? CARD : PANEL);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        };
    }

    private TableCellRenderer levelRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                setFont(FONT_LABEL);
                String lv = v.toString();
                Color c;
if (lv.equals("HIGH")) {
    c = HIGH_C;
} else if (lv.equals("MODERATE")) {
    c = MID_C;
} else {
    c = LOW_C;
}
                setForeground(c);
                setBackground(sel ? CARD : PANEL);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        };
    }

    private JPanel statCard(String label, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_C, 1),
            new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_SEC);

        valueLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
        valueLabel.setForeground(accent);

        card.add(lbl,        BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // ── Entry point ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(PlagiarismGUI::new);
    }
}
