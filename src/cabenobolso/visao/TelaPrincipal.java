package cabenobolso.visao;

import cabenobolso.controle.PlanejamentoControle;
import cabenobolso.modelo.Despesa;
import cabenobolso.modelo.Participante;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TelaPrincipal extends JFrame {

    private final PlanejamentoControle controle;

    // Layout com CardLayout para alternar a área central
    private final CardLayout cardLayout;
    private final JPanel panelConteudoCentral;

    // Botões da Sidebar
    private JButton btnNavParticipantes, btnNavDespesas, btnNavResumo;

    // Componentes - Participantes
    private JTextField txtCodigo, txtNome, txtOrcamento;
    private JTable tabelaParticipantes;
    private DefaultTableModel modelParticipantes;

    // Componentes - Despesas
    private JTextField txtDescricaoDespesa, txtValorDespesa;
    private JComboBox<String> cbTipoDespesa;
    private JList<Participante> listParticipantesDespesa;
    private DefaultListModel<Participante> listModelParticipantes;
    private JTable tabelaDespesas;
    private DefaultTableModel modelDespesas;
    private Despesa despesaEmEdicao = null;

    // Componentes - Resumo & Métricas
    private JTable tabelaResumo;
    private DefaultTableModel modelResumo;
    private JLabel lblValorCustoTotal, lblValorQtdParticipantes, lblValorQtdDespesas;

    // Paleta de Cores (Dark Mode Modern)
    private final Color BG_APP = new Color(20, 21, 26);          // #14151A
    private final Color BG_SIDEBAR = new Color(27, 28, 34);      // #1B1C22
    private final Color BG_CARD = new Color(32, 34, 42);         // #20222A
    private final Color BG_INPUT = new Color(22, 23, 29);        // #16171D
    
    private final Color COLOR_ACCENT = new Color(235, 77, 75);     // Vermelho Coral
    private final Color COLOR_PRIMARY = new Color(52, 152, 219);    // Azul
    private final Color COLOR_TEXT_WHITE = new Color(240, 242, 245);
    private final Color COLOR_TEXT_MUTED = new Color(140, 145, 160);

    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private final Font FONT_BASE = new Font("Segoe UI", Font.PLAIN, 13);

    public TelaPrincipal() {
        controle = new PlanejamentoControle();

        setTitle("Cabe no bolso - Planejador de Despesas Compartilhadas");
        setSize(1050, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Painel Principal
        JPanel panelMain = new JPanel(new BorderLayout());
        panelMain.setBackground(BG_APP);

        cardLayout = new CardLayout();
        panelConteudoCentral = new JPanel(cardLayout);
        panelConteudoCentral.setBackground(BG_APP);

        panelConteudoCentral.add(criarPainelParticipantes(), "PARTICIPANTES");
        panelConteudoCentral.add(criarPainelDespesas(), "DESPESAS");
        panelConteudoCentral.add(criarPainelResumo(), "RESUMO");

        panelMain.add(criarSidebar(), BorderLayout.WEST);
        panelMain.add(panelConteudoCentral, BorderLayout.CENTER);

        add(panelMain);
    }

    // --- CONSTRUÇÃO DA SIDEBAR LATERAL ---
    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        // Logo
        JLabel lblLogo = new JLabel("CABE NO BOLSO", SwingConstants.LEFT);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(COLOR_ACCENT);
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Despesas Compartilhadas");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_TEXT_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(lblLogo);
        sidebar.add(lblSub);
        sidebar.add(Box.createRigidArea(new Dimension(0, 35)));

        // Navegação
        btnNavParticipantes = criarBotaoSidebar("👥  Participantes", true);
        btnNavDespesas = criarBotaoSidebar("💳  Despesas", false);
        btnNavResumo = criarBotaoSidebar("📊  Resumo / Status", false);

        btnNavParticipantes.addActionListener(e -> navegarPara("PARTICIPANTES", btnNavParticipantes));
        btnNavDespesas.addActionListener(e -> navegarPara("DESPESAS", btnNavDespesas));
        btnNavResumo.addActionListener(e -> navegarPara("RESUMO", btnNavResumo));

        sidebar.add(btnNavParticipantes);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnNavDespesas);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnNavResumo);

        sidebar.add(Box.createVerticalGlue());

        // Rodapé de Arquivo
        JLabel lblArquivo = new JLabel("GESTÃO DE ARQUIVO");
        lblArquivo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblArquivo.setForeground(COLOR_TEXT_MUTED);
        lblArquivo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSalvar = criarBotaoSidebar("💾  Salvar Planejamento", false);
        btnSalvar.addActionListener(e -> salvarPlanejamento());

        JButton btnCarregar = criarBotaoSidebar("📂  Carregar Planejamento", false);
        btnCarregar.addActionListener(e -> carregarPlanejamento());

        JButton btnNovo = criarBotaoSidebar("✨  Novo Planejamento", false);
        btnNovo.addActionListener(e -> {
            controle.novoPlanejamento();
            limparFormularioParticipante();
            limparFormularioDespesa();
            atualizarTodasTelas();
            JOptionPane.showMessageDialog(this, "Novo planejamento iniciado!");
        });

        sidebar.add(lblArquivo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnNovo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebar.add(btnSalvar);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebar.add(btnCarregar);

        return sidebar;
    }

    private void navegarPara(String cardName, JButton btnAtivo) {
        cardLayout.show(panelConteudoCentral, cardName);
        resetarBotoesSidebar();
        btnAtivo.setBackground(COLOR_ACCENT);
        btnAtivo.setForeground(COLOR_TEXT_WHITE);
    }

    private void resetarBotoesSidebar() {
        for (JButton btn : new JButton[]{btnNavParticipantes, btnNavDespesas, btnNavResumo}) {
            btn.setBackground(BG_SIDEBAR);
            btn.setForeground(COLOR_TEXT_MUTED);
        }
    }

    private JButton criarBotaoSidebar(String texto, boolean ativo) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                super.paintComponent(g2);
                g2.dispose();
            }
        };

        // Força o uso da fonte Segoe UI Emoji para renderizar os ícones corretamente no Windows
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));

        btn.setForeground(ativo ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
        btn.setBackground(ativo ? COLOR_ACCENT : BG_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(200, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(8, 12, 8, 12));
        return btn;
    }

    // --- PAINEL DE PARTICIPANTES ---
    private JPanel criarPainelParticipantes() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_APP);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pForm = criarCardPanel("Cadastrar / Editar Participante");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        pForm.add(criarLabel("Código:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtCodigo = criarTextField(8);
        pForm.add(txtCodigo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        pForm.add(criarLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNome = criarTextField(25);
        pForm.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        pForm.add(criarLabel("Orçamento (R$):"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtOrcamento = criarTextField(12);
        pForm.add(txtOrcamento, gbc);

        JPanel pBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pBotoes.setOpaque(false);
        JButton btnSalvarParticipante = criarBotaoAcao("Salvar Participante", COLOR_ACCENT);
        btnSalvarParticipante.addActionListener(e -> salvarParticipante());

        JButton btnLimpar = criarBotaoAcao("Limpar Campos", BG_CARD);
        btnLimpar.addActionListener(e -> limparFormularioParticipante());

        pBotoes.add(btnSalvarParticipante);
        pBotoes.add(btnLimpar);

        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        pForm.add(pBotoes, gbc);

        modelParticipantes = new DefaultTableModel(new String[]{"Código", "Nome", "Orçamento (R$)"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaParticipantes = new JTable(modelParticipantes);
        estilizarTabelaDark(tabelaParticipantes);

        tabelaParticipantes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tabelaParticipantes.getSelectedRow();
                if (row >= 0 && row < controle.getParticipantes().size()) {
                    Participante p = controle.getParticipantes().get(row);
                    txtCodigo.setText(String.valueOf(p.getCodigo()));
                    txtCodigo.setEditable(false);
                    txtNome.setText(p.getNome());
                    txtOrcamento.setText(String.valueOf(p.getOrcamento()).replace(".", ","));
                }
            }
        });

        JPanel pCentro = new JPanel(new BorderLayout(10, 10));
        pCentro.setOpaque(false);
        pCentro.add(criarScrollPaneDark(tabelaParticipantes), BorderLayout.CENTER);

        JButton btnRemoverParticipante = criarBotaoAcao("Remover Participante Selecionado", new Color(180, 50, 50));
        btnRemoverParticipante.addActionListener(e -> removerParticipante());
        pCentro.add(btnRemoverParticipante, BorderLayout.SOUTH);

        panel.add(pForm, BorderLayout.NORTH);
        panel.add(pCentro, BorderLayout.CENTER);

        return panel;
    }

    // --- PAINEL DE DESPESAS ---
    private JPanel criarPainelDespesas() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_APP);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pForm = criarCardPanel("Cadastrar / Editar Despesa");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        pForm.add(criarLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtDescricaoDespesa = criarTextField(25);
        pForm.add(txtDescricaoDespesa, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        pForm.add(criarLabel("Valor (R$):"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtValorDespesa = criarTextField(12);
        pForm.add(txtValorDespesa, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        pForm.add(criarLabel("Tipo:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        cbTipoDespesa = new JComboBox<>(new String[]{"Fixa", "Por Pessoa"});
        cbTipoDespesa.setFont(FONT_BASE);
        cbTipoDespesa.setBackground(BG_INPUT);
        cbTipoDespesa.setForeground(COLOR_TEXT_WHITE);
        pForm.add(cbTipoDespesa, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.NORTHEAST;
        pForm.add(criarLabel("Participantes Vinculados:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        listModelParticipantes = new DefaultListModel<>();
        listParticipantesDespesa = new JList<>(listModelParticipantes);
        listParticipantesDespesa.setFont(FONT_BASE);
        listParticipantesDespesa.setBackground(BG_INPUT);
        listParticipantesDespesa.setForeground(COLOR_TEXT_WHITE);
        listParticipantesDespesa.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollList = criarScrollPaneDark(listParticipantesDespesa);
        scrollList.setPreferredSize(new Dimension(220, 80));

        JLabel lblDica = new JLabel("Dica: Mantenha pressionada a tecla Ctrl para selecionar múltiplos participantes.", SwingConstants.LEFT);
        lblDica.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblDica.setForeground(COLOR_TEXT_MUTED);

        JPanel pListaComDica = new JPanel(new BorderLayout(2, 4));
        pListaComDica.setOpaque(false);
        pListaComDica.add(scrollList, BorderLayout.CENTER);
        pListaComDica.add(lblDica, BorderLayout.SOUTH);

        pForm.add(pListaComDica, gbc);

        JPanel pBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pBotoes.setOpaque(false);
        JButton btnSalvarDespesa = criarBotaoAcao("Salvar Despesa", COLOR_ACCENT);
        btnSalvarDespesa.addActionListener(e -> salvarDespesa());

        JButton btnLimparD = criarBotaoAcao("Limpar Campos", BG_CARD);
        btnLimparD.addActionListener(e -> limparFormularioDespesa());

        pBotoes.add(btnSalvarDespesa);
        pBotoes.add(btnLimparD);

        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        pForm.add(pBotoes, gbc);

        modelDespesas = new DefaultTableModel(new String[]{"Descrição", "Tipo", "Valor (R$)", "Participantes"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaDespesas = new JTable(modelDespesas);
        estilizarTabelaDark(tabelaDespesas);

        tabelaDespesas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tabelaDespesas.getSelectedRow();
                if (row >= 0 && row < controle.getDespesas().size()) {
                    despesaEmEdicao = controle.getDespesas().get(row);
                    txtDescricaoDespesa.setText(despesaEmEdicao.getDescricao());
                    txtValorDespesa.setText(String.valueOf(despesaEmEdicao.getValor()).replace(".", ","));
                    cbTipoDespesa.setSelectedItem((despesaEmEdicao instanceof cabenobolso.modelo.DespesaFixa) ? "Fixa" : "Por Pessoa");

                    listParticipantesDespesa.clearSelection();
                    List<Integer> indices = new ArrayList<>();
                    for (int i = 0; i < listModelParticipantes.size(); i++) {
                        Participante p = listModelParticipantes.get(i);
                        if (despesaEmEdicao.getParticipantes().contains(p)) {
                            indices.add(i);
                        }
                    }
                    int[] indArray = indices.stream().mapToInt(i -> i).toArray();
                    listParticipantesDespesa.setSelectedIndices(indArray);
                }
            }
        });

        JPanel pCentro = new JPanel(new BorderLayout(10, 10));
        pCentro.setOpaque(false);
        pCentro.add(criarScrollPaneDark(tabelaDespesas), BorderLayout.CENTER);

        JButton btnRemoverDespesa = criarBotaoAcao("Excluir Despesa Selecionada", new Color(180, 50, 50));
        btnRemoverDespesa.addActionListener(e -> removerDespesa());
        pCentro.add(btnRemoverDespesa, BorderLayout.SOUTH);

        panel.add(pForm, BorderLayout.NORTH);
        panel.add(pCentro, BorderLayout.CENTER);

        return panel;
    }

    // --- PAINEL DE RESUMO ---
    private JPanel criarPainelResumo() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_APP);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pCards = new JPanel(new GridLayout(1, 3, 15, 0));
        pCards.setOpaque(false);

        lblValorCustoTotal = new JLabel("R$ 0,00");
        lblValorQtdParticipantes = new JLabel("0");
        lblValorQtdDespesas = new JLabel("0");

        pCards.add(criarMiniCardMétrica("Custo Total", lblValorCustoTotal, COLOR_ACCENT));
        pCards.add(criarMiniCardMétrica("Participantes", lblValorQtdParticipantes, COLOR_PRIMARY));
        pCards.add(criarMiniCardMétrica("Total de Despesas", lblValorQtdDespesas, new Color(155, 89, 182)));

        modelResumo = new DefaultTableModel(new String[]{"Código", "Participante", "Gasto Total (R$)", "Orçamento (R$)", "Situação"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaResumo = new JTable(modelResumo);
        estilizarTabelaDark(tabelaResumo);

        tabelaResumo.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String text = (value != null) ? value.toString() : "";

                if (!isSelected) {
                    if (text.startsWith("Excedido")) {
                        c.setBackground(new Color(60, 25, 25));
                        c.setForeground(new Color(255, 110, 110));
                        setFont(FONT_BOLD);
                    } else {
                        c.setBackground(new Color(25, 50, 30));
                        c.setForeground(new Color(110, 255, 140));
                        setFont(FONT_BASE);
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        panel.add(pCards, BorderLayout.NORTH);
        panel.add(criarScrollPaneDark(tabelaResumo), BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarMiniCardMétrica(String titulo, JLabel lblValor, Color corDestaque) {
        JPanel card = new JPanel(new BorderLayout(5, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTit = new JLabel(titulo.toUpperCase());
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTit.setForeground(COLOR_TEXT_MUTED);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValor.setForeground(corDestaque);

        card.add(lblTit, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    // --- MÉTODOS AUXILIARES DE ESTILIZAÇÃO ---
    private JPanel criarCardPanel(String titulo) {
        JPanel p = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), titulo, 0, 0, FONT_TITLE, COLOR_TEXT_WHITE));
        return p;
    }

    private JLabel criarLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(COLOR_TEXT_WHITE);
        return lbl;
    }

    private JTextField criarTextField(int columns) {
        JTextField txt = new JTextField(columns);
        txt.setFont(FONT_BASE);
        txt.setBackground(BG_INPUT);
        txt.setForeground(COLOR_TEXT_WHITE);
        txt.setCaretColor(COLOR_TEXT_WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 52, 65), 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return txt;
    }

    private JButton criarBotaoAcao(String texto, Color bg) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(COLOR_TEXT_WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    private void estilizarTabelaDark(JTable tabela) {
        tabela.setRowHeight(28);
        tabela.setFont(FONT_BASE);
        tabela.setBackground(BG_CARD);
        tabela.setForeground(COLOR_TEXT_WHITE);
        tabela.setSelectionBackground(new Color(60, 65, 80));
        tabela.setSelectionForeground(COLOR_TEXT_WHITE);
        tabela.setGridColor(new Color(45, 48, 60));

        tabela.getTableHeader().setFont(FONT_BOLD);
        tabela.getTableHeader().setBackground(BG_SIDEBAR);
        tabela.getTableHeader().setForeground(COLOR_TEXT_MUTED);
        tabela.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
    }

    private JScrollPane criarScrollPaneDark(Component view) {
        JScrollPane scroll = new JScrollPane(view);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(45, 48, 60), 1));
        return scroll;
    }

    // --- AÇÕES DE PERSISTÊNCIA E DADOS ---
    private void salvarPlanejamento() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controle.salvar(fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Planejamento salvo com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void carregarPlanejamento() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controle.carregar(fc.getSelectedFile().getAbsolutePath());
                limparFormularioParticipante();
                limparFormularioDespesa();
                atualizarTodasTelas();
                JOptionPane.showMessageDialog(this, "Planejamento carregado com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void salvarParticipante() {
        try {
            String codStr = txtCodigo.getText().trim();
            String nome = txtNome.getText().trim();
            String orcStr = txtOrcamento.getText().trim();

            if (codStr.isEmpty() || nome.isEmpty() || orcStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos do participante.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int codigo = Integer.parseInt(codStr);
            double orcamento = Double.parseDouble(orcStr.replace(",", "."));

            Participante existente = null;
            for (Participante p : controle.getParticipantes()) {
                if (p.getCodigo() == codigo) {
                    existente = p;
                    break;
                }
            }

            if (existente != null) {
                controle.atualizarParticipante(existente, nome, orcamento);
                JOptionPane.showMessageDialog(this, "Participante atualizado com sucesso!");
            } else {
                controle.adicionarParticipante(codigo, nome, orcamento);
                JOptionPane.showMessageDialog(this, "Participante adicionado com sucesso!");
            }

            limparFormularioParticipante();
            atualizarTodasTelas();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Código deve ser inteiro positivo e Orçamento deve ser numérico.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            String msg = (ex.getMessage() != null && !ex.getMessage().trim().isEmpty()) ? ex.getMessage() : ex.toString();
            JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removerParticipante() {
        int row = tabelaParticipantes.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um participante na tabela para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Participante p = controle.getParticipantes().get(row);
            controle.removerParticipante(p);
            limparFormularioParticipante();
            atualizarTodasTelas();
            JOptionPane.showMessageDialog(this, "Participante removido com sucesso!");
        } catch (Exception ex) {
            String msg = (ex.getMessage() != null && !ex.getMessage().trim().isEmpty()) ? ex.getMessage() : ex.toString();
            JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarDespesa() {
        try {
            String desc = txtDescricaoDespesa.getText().trim();
            String valorStr = txtValorDespesa.getText().trim();

            if (desc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "A descrição da despesa não pode estar vazia.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (valorStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o valor da despesa.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double valor = Double.parseDouble(valorStr.replace(",", "."));
            String tipo = (String) cbTipoDespesa.getSelectedItem();
            List<Participante> selecionados = listParticipantesDespesa.getSelectedValuesList();

            if (selecionados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione pelo menos um participante para a despesa.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (despesaEmEdicao != null) {
                controle.removerDespesa(despesaEmEdicao);
            }

            if ("Fixa".equals(tipo)) {
                controle.adicionarDespesaFixa(desc, valor, selecionados);
            } else {
                controle.adicionarDespesaPorPessoa(desc, valor, selecionados);
            }

            limparFormularioDespesa();
            atualizarTodasTelas();
            JOptionPane.showMessageDialog(this, "Despesa salva com sucesso!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite um valor numérico válido para a despesa.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            String msg = (ex.getMessage() != null && !ex.getMessage().trim().isEmpty()) ? ex.getMessage() : ex.toString();
            JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removerDespesa() {
        int row = tabelaDespesas.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma despesa na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Despesa d = controle.getDespesas().get(row);
            controle.removerDespesa(d);
            limparFormularioDespesa();
            atualizarTodasTelas();
            JOptionPane.showMessageDialog(this, "Despesa excluída com sucesso!");
        } catch (Exception ex) {
            String msg = (ex.getMessage() != null && !ex.getMessage().trim().isEmpty()) ? ex.getMessage() : ex.toString();
            JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormularioParticipante() {
        txtCodigo.setText("");
        txtCodigo.setEditable(true);
        txtNome.setText("");
        txtOrcamento.setText("");
        tabelaParticipantes.clearSelection();
    }

    private void limparFormularioDespesa() {
        txtDescricaoDespesa.setText("");
        txtValorDespesa.setText("");
        cbTipoDespesa.setSelectedIndex(0);
        listParticipantesDespesa.clearSelection();
        tabelaDespesas.clearSelection();
        despesaEmEdicao = null;
    }

    private void atualizarTodasTelas() {
        modelParticipantes.setRowCount(0);
        listModelParticipantes.clear();
        for (Participante p : controle.getParticipantes()) {
            modelParticipantes.addRow(new Object[]{p.getCodigo(), p.getNome(), String.format("R$ %.2f", p.getOrcamento())});
            listModelParticipantes.addElement(p);
        }

        modelDespesas.setRowCount(0);
        for (Despesa d : controle.getDespesas()) {
            String tipo = (d instanceof cabenobolso.modelo.DespesaFixa) ? "Fixa" : "Por Pessoa";
            modelDespesas.addRow(new Object[]{d.getDescricao(), tipo, String.format("R$ %.2f", d.getValor()), d.getParticipantes().size() + " participante(s)"});
        }

        modelResumo.setRowCount(0);
        Map<Participante, Double> totais = controle.getTotaisIndividuais();
        for (Map.Entry<Participante, Double> entry : totais.entrySet()) {
            Participante p = entry.getKey();
            double gasto = entry.getValue();
            double orcamento = p.getOrcamento();
            String situacao = (gasto <= orcamento) ? "Dentro do limite" : String.format("Excedido em R$ %.2f", (gasto - orcamento));

            modelResumo.addRow(new Object[]{p.getCodigo(), p.getNome(), String.format("R$ %.2f", gasto), String.format("R$ %.2f", orcamento), situacao});
        }

        lblValorCustoTotal.setText(String.format("R$ %.2f", controle.getCustoTotal()));
        lblValorQtdParticipantes.setText(String.valueOf(controle.getParticipantes().size()));
        lblValorQtdDespesas.setText(String.valueOf(controle.getDespesas().size()));
    }
}