package it.unina.uninaswap.view;

import it.unina.uninaswap.model.enums.TipoAnnuncio;
import it.unina.uninaswap.model.enums.TipoCategoria;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.CategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.EnumMap;

public class ReportView extends JPanel {

    private final JTabbedPane tabs = new JTabbedPane();

    // ====== CARD LAYOUT PER OGNI TAB ======
    private final CardLayout venditeCards = new CardLayout();
    private final JPanel venditeRoot = new JPanel(venditeCards);

    private final CardLayout acquistiCards = new CardLayout();
    private final JPanel acquistiRoot = new JPanel(acquistiCards);

    // ===== VENDITE =====
    private final JTable tableVenditeCategoria = new JTable();
    private final JTable tableVenditeTipologia = new JTable();

    private final JLabel lblTotArrivateVendite = new JLabel("Totale offerte arrivate: 0");
    private final JLabel lblTotAccettateVendite = new JLabel("Totale accettate: 0");
    private final JLabel lblMediaVendite = new JLabel("Media: -");
    private final JLabel lblMinVendite = new JLabel("Min: -");
    private final JLabel lblMaxVendite = new JLabel("Max: -");

    private final ChartPanel chartVenditeCategoria = new ChartPanel(null);
    private final ChartPanel chartVenditeTipologia = new ChartPanel(null);

    // ===== ACQUISTI =====
    private final JTable tableAcquistiCategoria = new JTable();
    private final JTable tableAcquistiTipologia = new JTable();

    private final JLabel lblTotInviateAcquisti = new JLabel("Totale offerte inviate: 0");
    private final JLabel lblTotAccettateAcquisti = new JLabel("Totale accettate: 0");
    private final JLabel lblMediaAcquisti = new JLabel("Media: -");
    private final JLabel lblMinAcquisti = new JLabel("Min: -");
    private final JLabel lblMaxAcquisti = new JLabel("Max: -");

    private final ChartPanel chartAcquistiCategoria = new ChartPanel(null);
    private final ChartPanel chartAcquistiTipologia = new ChartPanel(null);

    public ReportView() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Report", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // setup tabelle
        setupTable(tableVenditeCategoria, "Categoria", "Offerte arrivate", "Accettate");
        setupTable(tableVenditeTipologia, "Tipologia", "Offerte arrivate", "Accettate");
        setupTable(tableAcquistiCategoria, "Categoria", "Offerte inviate", "Accettate");
        setupTable(tableAcquistiTipologia, "Tipologia", "Offerte inviate", "Accettate");

        // costruzione tab con card
        venditeRoot.add(buildVenditeSummaryCard(), "SUMMARY");
        venditeRoot.add(buildVenditeChartsCard(), "CHARTS");

        acquistiRoot.add(buildAcquistiSummaryCard(), "SUMMARY");
        acquistiRoot.add(buildAcquistiChartsCard(), "CHARTS");

        tabs.addTab("Vendite", venditeRoot);
        tabs.addTab("Acquisti", acquistiRoot);

        add(tabs, BorderLayout.CENTER);

        // default
        venditeCards.show(venditeRoot, "SUMMARY");
        acquistiCards.show(acquistiRoot, "SUMMARY");
    }

    // =========================================================
    //  COSTRUZIONE CARDS - VENDITE
    // =========================================================
    private JPanel buildVenditeSummaryCard() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        // TOP: riepilogo + stats
        JPanel top = buildTopSummaryPanel(
                "Riepilogo vendite",
                lblTotArrivateVendite,
                lblTotAccettateVendite,
                lblMediaVendite,
                lblMinVendite,
                lblMaxVendite
        );
        p.add(top, BorderLayout.NORTH);

        // CENTER: tabelle in TAB (così su schermi piccoli si vede bene)
        JTabbedPane tabTabelle = new JTabbedPane();
        JScrollPane spCat = new JScrollPane(tableVenditeCategoria);
        spCat.setBorder(new EmptyBorder(6, 6, 6, 6));
        JScrollPane spTipo = new JScrollPane(tableVenditeTipologia);
        spTipo.setBorder(new EmptyBorder(6, 6, 6, 6));

        tabTabelle.addTab("Per categoria", spCat);
        tabTabelle.addTab("Per tipologia", spTipo);

        JPanel tablesWrap = new JPanel(new BorderLayout());
        tablesWrap.setBorder(new TitledBorder("Tabelle"));
        tablesWrap.add(tabTabelle, BorderLayout.CENTER);

        p.add(tablesWrap, BorderLayout.CENTER);

        // BOTTOM: bottone grafici
        JButton btnMostraGrafici = new JButton("Mostra grafici");
        btnMostraGrafici.addActionListener(e -> venditeCards.show(venditeRoot, "CHARTS"));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnMostraGrafici);
        p.add(bottom, BorderLayout.SOUTH);

        return p;
    }

    private JPanel buildVenditeChartsCard() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        // header con back
        JLabel lbl = new JLabel("Grafici vendite");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));

        JButton btnBack = new JButton("Indietro");
        btnBack.addActionListener(e -> venditeCards.show(venditeRoot, "SUMMARY"));

        JPanel header = new JPanel(new BorderLayout());
        header.add(lbl, BorderLayout.WEST);
        header.add(btnBack, BorderLayout.EAST);
        p.add(header, BorderLayout.NORTH);

        // grafici in TAB (categoria / tipologia)
        JTabbedPane tabCharts = new JTabbedPane();

        chartVenditeCategoria.setBorder(new EmptyBorder(6, 6, 6, 6));
        chartVenditeTipologia.setBorder(new EmptyBorder(6, 6, 6, 6));

        tabCharts.addTab("Per categoria", chartVenditeCategoria);
        tabCharts.addTab("Per tipologia", chartVenditeTipologia);

        JPanel chartsWrap = new JPanel(new BorderLayout());
        chartsWrap.setBorder(new TitledBorder("Grafici"));
        chartsWrap.add(tabCharts, BorderLayout.CENTER);

        p.add(chartsWrap, BorderLayout.CENTER);

        return p;
    }

    // =========================================================
    //  COSTRUZIONE CARDS - ACQUISTI
    // =========================================================
    private JPanel buildAcquistiSummaryCard() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel top = buildTopSummaryPanel(
                "Riepilogo acquisti",
                lblTotInviateAcquisti,
                lblTotAccettateAcquisti,
                lblMediaAcquisti,
                lblMinAcquisti,
                lblMaxAcquisti
        );
        p.add(top, BorderLayout.NORTH);

        JTabbedPane tabTabelle = new JTabbedPane();
        JScrollPane spCat = new JScrollPane(tableAcquistiCategoria);
        spCat.setBorder(new EmptyBorder(6, 6, 6, 6));
        JScrollPane spTipo = new JScrollPane(tableAcquistiTipologia);
        spTipo.setBorder(new EmptyBorder(6, 6, 6, 6));

        tabTabelle.addTab("Per categoria", spCat);
        tabTabelle.addTab("Per tipologia", spTipo);

        JPanel tablesWrap = new JPanel(new BorderLayout());
        tablesWrap.setBorder(new TitledBorder("Tabelle"));
        tablesWrap.add(tabTabelle, BorderLayout.CENTER);

        p.add(tablesWrap, BorderLayout.CENTER);

        JButton btnMostraGrafici = new JButton("Mostra grafici");
        btnMostraGrafici.addActionListener(e -> acquistiCards.show(acquistiRoot, "CHARTS"));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnMostraGrafici);
        p.add(bottom, BorderLayout.SOUTH);

        return p;
    }

    private JPanel buildAcquistiChartsCard() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JLabel lbl = new JLabel("Grafici acquisti");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));

        JButton btnBack = new JButton("Indietro");
        btnBack.addActionListener(e -> acquistiCards.show(acquistiRoot, "SUMMARY"));

        JPanel header = new JPanel(new BorderLayout());
        header.add(lbl, BorderLayout.WEST);
        header.add(btnBack, BorderLayout.EAST);
        p.add(header, BorderLayout.NORTH);

        JTabbedPane tabCharts = new JTabbedPane();

        chartAcquistiCategoria.setBorder(new EmptyBorder(6, 6, 6, 6));
        chartAcquistiTipologia.setBorder(new EmptyBorder(6, 6, 6, 6));

        tabCharts.addTab("Per categoria", chartAcquistiCategoria);
        tabCharts.addTab("Per tipologia", chartAcquistiTipologia);

        JPanel chartsWrap = new JPanel(new BorderLayout());
        chartsWrap.setBorder(new TitledBorder("Grafici"));
        chartsWrap.add(tabCharts, BorderLayout.CENTER);

        p.add(chartsWrap, BorderLayout.CENTER);

        return p;
    }

    // =========================================================
    //  COMPONENTI COMUNI
    // =========================================================
    private JPanel buildTopSummaryPanel(String title,
                                        JLabel lblTot1, JLabel lblTot2,
                                        JLabel lblMedia, JLabel lblMin, JLabel lblMax) {
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(new TitledBorder(title));

        JPanel totals = new JPanel(new GridLayout(0, 1, 0, 5));
        totals.add(lblTot1);
        totals.add(lblTot2);

        JPanel stats = new JPanel(new GridLayout(1, 3, 10, 0));
        stats.setBorder(new TitledBorder("Statistiche importo (solo Vendita)"));
        stats.add(lblMedia);
        stats.add(lblMin);
        stats.add(lblMax);

        JPanel topInner = new JPanel(new BorderLayout(10, 10));
        topInner.add(totals, BorderLayout.NORTH);
        topInner.add(stats, BorderLayout.SOUTH);

        top.add(topInner, BorderLayout.CENTER);
        return top;
    }

    private void setupTable(JTable table, String c1, String c2, String c3) {
        DefaultTableModel model = new DefaultTableModel(new Object[]{c1, c2, c3}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table.setModel(model);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
    }

    // =========================================================
    // API per Controller
    // =========================================================
    public void setData(ReportData data) {
        if (data == null) return;

        // ---------- VENDITE totals ----------
        lblTotArrivateVendite.setText("Totale offerte arrivate: " + data.venditePerCategoria.totaleArrivate);
        lblTotAccettateVendite.setText("Totale accettate: " + data.venditePerCategoria.totaleAccettate);

        lblMediaVendite.setText("Media: " + formatMoney(data.mediaVendite));
        lblMinVendite.setText("Min: " + formatMoney(data.minVendite));
        lblMaxVendite.setText("Max: " + formatMoney(data.maxVendite));

        // Tabelle vendite: categoria
        DefaultTableModel mvCat = (DefaultTableModel) tableVenditeCategoria.getModel();
        mvCat.setRowCount(0);
        for (TipoCategoria cat : TipoCategoria.values()) {
            mvCat.addRow(new Object[]{
                    cat.toString(),
                    data.venditePerCategoria.getArrivate(cat),
                    data.venditePerCategoria.getAccettate(cat)
            });
        }
        mvCat.addRow(new Object[]{"Totale", data.venditePerCategoria.totaleArrivate, data.venditePerCategoria.totaleAccettate});

        // Tabelle vendite: tipologia
        DefaultTableModel mvTipo = (DefaultTableModel) tableVenditeTipologia.getModel();
        mvTipo.setRowCount(0);
        for (TipoAnnuncio t : TipoAnnuncio.values()) {
            mvTipo.addRow(new Object[]{
                    t.toString(),
                    data.venditePerTipologia.getArrivate(t),
                    data.venditePerTipologia.getAccettate(t)
            });
        }
        mvTipo.addRow(new Object[]{"Totale", data.venditePerTipologia.totaleArrivate, data.venditePerTipologia.totaleAccettate});

        // ---------- ACQUISTI totals ----------
        lblTotInviateAcquisti.setText("Totale offerte inviate: " + data.acquistiPerCategoria.totaleArrivate);
        lblTotAccettateAcquisti.setText("Totale accettate: " + data.acquistiPerCategoria.totaleAccettate);

        lblMediaAcquisti.setText("Media: " + formatMoney(data.mediaAcquisti));
        lblMinAcquisti.setText("Min: " + formatMoney(data.minAcquisti));
        lblMaxAcquisti.setText("Max: " + formatMoney(data.maxAcquisti));

        // Tabelle acquisti: categoria
        DefaultTableModel maCat = (DefaultTableModel) tableAcquistiCategoria.getModel();
        maCat.setRowCount(0);
        for (TipoCategoria cat : TipoCategoria.values()) {
            maCat.addRow(new Object[]{
                    cat.toString(),
                    data.acquistiPerCategoria.getArrivate(cat),
                    data.acquistiPerCategoria.getAccettate(cat)
            });
        }
        maCat.addRow(new Object[]{"Totale", data.acquistiPerCategoria.totaleArrivate, data.acquistiPerCategoria.totaleAccettate});

        // Tabelle acquisti: tipologia
        DefaultTableModel maTipo = (DefaultTableModel) tableAcquistiTipologia.getModel();
        maTipo.setRowCount(0);
        for (TipoAnnuncio t : TipoAnnuncio.values()) {
            maTipo.addRow(new Object[]{
                    t.toString(),
                    data.acquistiPerTipologia.getArrivate(t),
                    data.acquistiPerTipologia.getAccettate(t)
            });
        }
        maTipo.addRow(new Object[]{"Totale", data.acquistiPerTipologia.totaleArrivate, data.acquistiPerTipologia.totaleAccettate});
    }

    // ======= Charts setter (con fix asse Y a interi + range 0..N) =======
    public void setVenditeChartCategoria(JFreeChart chart) {
        styleChartToIntegers(chart);
        chartVenditeCategoria.setChart(chart);
    }

    public void setVenditeChartTipologia(JFreeChart chart) {
        styleChartToIntegers(chart);
        chartVenditeTipologia.setChart(chart);
    }

    public void setAcquistiChartCategoria(JFreeChart chart) {
        styleChartToIntegers(chart);
        chartAcquistiCategoria.setChart(chart);
    }

    public void setAcquistiChartTipologia(JFreeChart chart) {
        styleChartToIntegers(chart);
        chartAcquistiTipologia.setChart(chart);
    }

    private void styleChartToIntegers(JFreeChart chart) {
        if (chart == null) return;
        if (!(chart.getPlot() instanceof CategoryPlot plot)) return;
        if (!(plot.getRangeAxis() instanceof NumberAxis axis)) return;

        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        axis.setAutoRangeIncludesZero(true);

        // evita range "strani" quando i valori sono tutti 0
        double max = findMaxValue(plot.getDataset());
        double upper = Math.max(1, max);   // almeno 1, così vedi 0 e 1
        axis.setRange(0, upper);
    }

    private double findMaxValue(CategoryDataset ds) {
        if (ds == null) return 0;
        double max = 0;
        for (int r = 0; r < ds.getRowCount(); r++) {
            for (int c = 0; c < ds.getColumnCount(); c++) {
                Number v = ds.getValue(r, c);
                if (v != null) max = Math.max(max, v.doubleValue());
            }
        }
        return max;
    }

    private String formatMoney(BigDecimal v) {
        if (v == null) return "-";
        return v.toPlainString() + " €";
    }

    // =========================================================
    // DTOs
    // =========================================================
    public static class SectionByCategoria {
        private final EnumMap<TipoCategoria, Integer> arrivate = new EnumMap<>(TipoCategoria.class);
        private final EnumMap<TipoCategoria, Integer> accettate = new EnumMap<>(TipoCategoria.class);
        public int totaleArrivate = 0;
        public int totaleAccettate = 0;

        public void putArrivate(TipoCategoria c, int v) { arrivate.put(c, v); }
        public void putAccettate(TipoCategoria c, int v) { accettate.put(c, v); }

        public int getArrivate(TipoCategoria c) { return arrivate.getOrDefault(c, 0); }
        public int getAccettate(TipoCategoria c) { return accettate.getOrDefault(c, 0); }

        public void computeTotals() {
            totaleArrivate = 0;
            totaleAccettate = 0;
            for (TipoCategoria c : TipoCategoria.values()) {
                totaleArrivate += getArrivate(c);
                totaleAccettate += getAccettate(c);
            }
        }
    }

    public static class SectionByTipologia {
        private final EnumMap<TipoAnnuncio, Integer> arrivate = new EnumMap<>(TipoAnnuncio.class);
        private final EnumMap<TipoAnnuncio, Integer> accettate = new EnumMap<>(TipoAnnuncio.class);
        public int totaleArrivate = 0;
        public int totaleAccettate = 0;

        public void putArrivate(TipoAnnuncio t, int v) { arrivate.put(t, v); }
        public void putAccettate(TipoAnnuncio t, int v) { accettate.put(t, v); }

        public int getArrivate(TipoAnnuncio t) { return arrivate.getOrDefault(t, 0); }
        public int getAccettate(TipoAnnuncio t) { return accettate.getOrDefault(t, 0); }

        public void computeTotals() {
            totaleArrivate = 0;
            totaleAccettate = 0;
            for (TipoAnnuncio t : TipoAnnuncio.values()) {
                totaleArrivate += getArrivate(t);
                totaleAccettate += getAccettate(t);
            }
        }
    }

    public static class ReportData {
        public SectionByCategoria venditePerCategoria = new SectionByCategoria();
        public SectionByTipologia venditePerTipologia = new SectionByTipologia();

        public SectionByCategoria acquistiPerCategoria = new SectionByCategoria();
        public SectionByTipologia acquistiPerTipologia = new SectionByTipologia();

        // stats (solo tipologia Vendita)
        public BigDecimal mediaVendite, minVendite, maxVendite;     // io venditore
        public BigDecimal mediaAcquisti, minAcquisti, maxAcquisti;  // io acquirente
    }
}
