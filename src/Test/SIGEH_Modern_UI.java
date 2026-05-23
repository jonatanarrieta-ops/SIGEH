package Test;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * SIGEH — Sistema Integral de Gestión Hospitalaria
 * Clase principal: contiene el main y la ventana raíz con login + dashboard.
 *
 * Estructura del proyecto (todas en el paquete Test):
 *   SIGEH_Modern_UI.java   ← esta clase (main aquí)
 *   UIUtils.java           ← paleta de colores, fuentes y componentes reutilizables
 *   DataRepository.java    ← capa de datos en memoria (Singleton)
 *   Paciente.java          ← modelo
 *   Cita.java              ← modelo
 *   Factura.java           ← modelo
 *   HistoriaClinica.java   ← modelo
 *   DashboardPanel.java    ← módulo dashboard
 *   PacientesPanel.java    ← módulo pacientes
 *   CitasPanel.java        ← módulo citas
 *   FacturacionPanel.java  ← módulo facturación
 *   HistoriaClinicaPanel.java ← módulo historia clínica
 *   ReportesPanel.java     ← módulo reportes
 */
public class SIGEH_Modern_UI extends JFrame {

    // ── Navegación principal ──────────────────────────────────────────────────
    private CardLayout cardLayout;
    private JPanel     panelPrincipal;

    // ── Navegación de módulos ─────────────────────────────────────────────────
    private CardLayout layoutModulos;
    private JPanel     panelModulos;

    // ── Campos de login ───────────────────────────────────────────────────────
    private JTextField     campoUsuario;
    private JPasswordField campoClave;

    // ── Barra lateral ─────────────────────────────────────────────────────────
    private JButton btnActivo = null; // botón de menú actualmente seleccionado

    // ── Credenciales (demo) ───────────────────────────────────────────────────
    private static final String USUARIO = "admin";
    private static final String CLAVE   = "123456";

    // ── Constructor ───────────────────────────────────────────────────────────
    public SIGEH_Modern_UI() {
        configurarVentana();
        inicializar();
    }

    private void configurarVentana() {
        setTitle("SIGEH — Sistema Integral de Gestión Hospitalaria");
        setSize(1200, 700);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Ícono de la ventana (si existe el recurso)
        try {
            ImageIcon ico = new ImageIcon(getClass().getResource("/resources/logo.png"));
            setIconImage(ico.getImage());
        } catch (Exception ignored) {}
    }

    private void inicializar() {
        cardLayout     = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);
        panelPrincipal.add(construirLogin(),     "login");
        panelPrincipal.add(construirDashboard(), "dashboard");
        add(panelPrincipal);
        cardLayout.show(panelPrincipal, "login");
    }

    // ════════════════════════════════════════════════════════════════════════════
    // PANTALLA DE LOGIN
    // ════════════════════════════════════════════════════════════════════════════
    private JPanel construirLogin() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(bannerIzquierdo(), BorderLayout.WEST);
        panel.add(formularioLogin(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel bannerIzquierdo() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(UIUtils.AZUL_OSCURO);
        banner.setPreferredSize(new Dimension(440, 700));

        // Degradado visual
        JPanel gradiente = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(13, 71, 161),
                    getWidth(), getHeight(), new Color(25, 118, 210));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradiente.setLayout(new GridBagLayout());
        gradiente.setOpaque(false);

        // Contenido centrado en el banner
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        // Logo (con fallback)
        JLabel lblLogo;
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/resources/logo.png"));
            Image img = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            lblLogo = new JLabel(new ImageIcon(img));
        } catch (Exception e) {
            lblLogo = UIUtils.label("🏥", new Font("Segoe UI Emoji", Font.PLAIN, 80), Color.WHITE);
        }
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNombre = UIUtils.labelCentrado("SIGEH", new Font("Segoe UI", Font.BOLD, 42), Color.WHITE);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = UIUtils.labelCentrado("Sistema Integral de", UIUtils.F_NORMAL, new Color(187, 222, 251));
        JLabel lblSub2 = UIUtils.labelCentrado("Gestión Hospitalaria", UIUtils.F_NORMAL, new Color(187, 222, 251));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSub2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 60));
        sep.setMaximumSize(new Dimension(200, 1));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblVer = UIUtils.labelCentrado("v2.0 — SoftHealth Solutions", UIUtils.F_PEQUEÑA, new Color(144, 202, 249));
        lblVer.setAlignmentX(Component.CENTER_ALIGNMENT);

        contenido.add(lblLogo);
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(lblNombre);
        contenido.add(Box.createVerticalStrut(8));
        contenido.add(lblSub);
        contenido.add(lblSub2);
        contenido.add(Box.createVerticalStrut(20));
        contenido.add(sep);
        contenido.add(Box.createVerticalStrut(12));
        contenido.add(lblVer);

        gradiente.add(contenido);
        banner.add(gradiente, BorderLayout.CENTER);
        return banner;
    }

    private JPanel formularioLogin() {
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(UIUtils.GRIS_FONDO);

        // Tarjeta blanca
        JPanel tarjeta = new JPanel(null);
        tarjeta.setPreferredSize(new Dimension(380, 420));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.GRIS_BORDE),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        // Franja superior azul de la tarjeta
        JPanel franjaTop = new JPanel();
        franjaTop.setBackground(UIUtils.AZUL_OSCURO);
        franjaTop.setBounds(0, 0, 380, 8);
        tarjeta.add(franjaTop);

        JLabel lblTitulo = UIUtils.label("Iniciar Sesión", new Font("Segoe UI", Font.BOLD, 24), UIUtils.AZUL_OSCURO);
        lblTitulo.setBounds(40, 36, 300, 36);

        JLabel lblSub = UIUtils.label("Ingrese sus credenciales para continuar", UIUtils.F_PEQUEÑA, UIUtils.TEXTO_GRIS);
        lblSub.setBounds(40, 72, 300, 20);

        // Campo usuario
        JLabel lblUser = UIUtils.label("Usuario", UIUtils.F_NORMAL, UIUtils.TEXTO_OSCURO);
        lblUser.setBounds(40, 112, 200, 20);
        campoUsuario = UIUtils.campo();
        campoUsuario.setBounds(40, 134, 300, 38);

        // Campo clave
        JLabel lblPass = UIUtils.label("Contraseña", UIUtils.F_NORMAL, UIUtils.TEXTO_OSCURO);
        lblPass.setBounds(40, 182, 200, 20);
        campoClave = UIUtils.campoPass();
        campoClave.setBounds(40, 204, 300, 38);
        campoClave.addActionListener(e -> validarLogin());

        // Hint de credenciales
        JLabel lblHint = UIUtils.label("Demo: admin / 123456", UIUtils.F_PEQUEÑA, UIUtils.TEXTO_GRIS);
        lblHint.setBounds(40, 250, 300, 18);

        // Botón ingresar
        JButton btnIngresar = UIUtils.botonPrimario("Ingresar al Sistema");
        btnIngresar.setBounds(40, 276, 300, 44);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.addActionListener(e -> validarLogin());

        // Pie de la tarjeta
        JLabel lblPie = UIUtils.label("© 2026 SoftHealth Solutions S.A.S.", UIUtils.F_PEQUEÑA, UIUtils.TEXTO_GRIS);
        lblPie.setBounds(80, 370, 300, 18);

        tarjeta.add(lblTitulo);
        tarjeta.add(lblSub);
        tarjeta.add(lblUser);
        tarjeta.add(campoUsuario);
        tarjeta.add(lblPass);
        tarjeta.add(campoClave);
        tarjeta.add(lblHint);
        tarjeta.add(btnIngresar);
        tarjeta.add(lblPie);

        fondo.add(tarjeta);
        return fondo;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // DASHBOARD / VENTANA PRINCIPAL
    // ════════════════════════════════════════════════════════════════════════════
    private JPanel construirDashboard() {
        JPanel panel = new JPanel(new BorderLayout());

        // Módulos disponibles
        layoutModulos = new CardLayout();
        panelModulos  = new JPanel(layoutModulos);
        panelModulos.setOpaque(true);
        panelModulos.setBackground(UIUtils.GRIS_FONDO);

        DashboardPanel        dashboard = new DashboardPanel();
        PacientesPanel        pacientes = new PacientesPanel();
        CitasPanel            citas     = new CitasPanel();
        FacturacionPanel      factura   = new FacturacionPanel();
        HistoriaClinicaPanel  historia  = new HistoriaClinicaPanel();
        ReportesPanel         reportes  = new ReportesPanel();

        panelModulos.add(dashboard, "dashboard");
        panelModulos.add(pacientes, "pacientes");
        panelModulos.add(citas,     "citas");
        panelModulos.add(factura,   "facturacion");
        panelModulos.add(historia,  "historia");
        panelModulos.add(reportes,  "reportes");

        panel.add(construirSidebar(dashboard, pacientes, citas, factura, historia, reportes), BorderLayout.WEST);
        panel.add(panelModulos, BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirSidebar(DashboardPanel dashboard, PacientesPanel pacientes,
                                    CitasPanel citas, FacturacionPanel factura,
                                    HistoriaClinicaPanel historia, ReportesPanel reportes) {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(13, 71, 161),
                    0, getHeight(), new Color(10, 55, 130));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setPreferredSize(new Dimension(230, 700));
        sidebar.setMinimumSize(new Dimension(230, 0));
        sidebar.setMaximumSize(new Dimension(230, Integer.MAX_VALUE));
        sidebar.setLayout(new BorderLayout());

        // Logo superior
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel lblLogo = UIUtils.labelCentrado("SIGEH", new Font("Segoe UI", Font.BOLD, 24), Color.WHITE);
        JLabel lblSub  = UIUtils.labelCentrado("Sistema Hospitalario", new Font("Segoe UI", Font.PLAIN, 10), new Color(187, 222, 251));
        JPanel logoTxt = new JPanel(new GridLayout(2, 1, 0, 2));
        logoTxt.setOpaque(false);
        logoTxt.add(lblLogo);
        logoTxt.add(lblSub);
        logoPanel.add(logoTxt, BorderLayout.CENTER);

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 40));

        // Menú de navegación
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(true);
        menuPanel.setBackground(new Color(13, 71, 161));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        // Items del menú: [texto, cardId, tipo de ícono]
        Object[][] items = {
            {"Dashboard",        "dashboard",   MenuIcon.Tipo.DASHBOARD},
            {"Pacientes",        "pacientes",   MenuIcon.Tipo.PACIENTES},
            {"Gestion de Citas", "citas",        MenuIcon.Tipo.CITAS},
            {"Historia Clinica", "historia",     MenuIcon.Tipo.HISTORIA},
            {"Facturacion",      "facturacion",  MenuIcon.Tipo.FACTURACION},
            {"Reportes",         "reportes",     MenuIcon.Tipo.REPORTES},
        };

        JButton primero = null;
        for (Object[] item : items) {
            JButton btn = crearItemMenu((String)item[0], (MenuIcon.Tipo)item[2]);
            String cardId = (String)item[1];
            btn.addActionListener(e -> {
                layoutModulos.show(panelModulos, cardId);
                if (cardId.equals("dashboard")) dashboard.actualizar();
                seleccionarMenu(btn);
            });
            menuPanel.add(btn);
            if (primero == null) primero = btn;
        }
        // Seleccionar el primero por defecto
        if (primero != null) seleccionarMenu(primero);

        // Panel inferior con usuario y salir
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(true);
        bottomPanel.setBackground(new Color(13, 71, 161));
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(255, 255, 255, 40));
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JPanel userInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        userInfo.setOpaque(false);
        userInfo.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        userInfo.add(UIUtils.label("👤 Administrador", UIUtils.F_PEQUEÑA, Color.WHITE));
        userInfo.add(UIUtils.label("admin@sigeh.co",   UIUtils.F_PEQUEÑA, new Color(187, 222, 251)));
        userInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JButton btnSalir = crearItemMenu("Cerrar sesion", MenuIcon.Tipo.CERRAR_SESION);
        btnSalir.addActionListener(e -> cerrarSesion());

        bottomPanel.add(sep2);
        bottomPanel.add(userInfo);
        bottomPanel.add(btnSalir);

        // Ensamblaje del sidebar
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(logoPanel, BorderLayout.NORTH);
        topSection.add(sep,       BorderLayout.CENTER);

        sidebar.add(topSection,  BorderLayout.NORTH);
        sidebar.add(menuPanel,   BorderLayout.CENTER);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);
        return sidebar;
    }

    // Colores solidos del sidebar (sin alpha — Swing no soporta transparencia en botones)
    private static final Color SIDEBAR_NORMAL = new Color(13,  71, 161);
    private static final Color SIDEBAR_HOVER  = new Color(25,  90, 185);
    private static final Color SIDEBAR_ACTIVO = new Color(21, 101, 192);

    private JButton crearItemMenu(String texto, MenuIcon.Tipo tipoIcono) {
        Color colorIcono = new Color(200, 220, 255);
        JButton btn = new JButton("  " + texto,
                new MenuIcon(tipoIcono, 18, colorIcono));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setIconTextGap(10);
        btn.setFont(UIUtils.F_NORMAL);
        btn.setForeground(colorIcono);
        btn.setBackground(SIDEBAR_NORMAL);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 14));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != btnActivo) btn.setBackground(SIDEBAR_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                if (btn != btnActivo) btn.setBackground(SIDEBAR_NORMAL);
            }
        });
        return btn;
    }

    private void seleccionarMenu(JButton btn) {
        Color colorNormal = new Color(200, 220, 255);
        if (btnActivo != null) {
            btnActivo.setBackground(SIDEBAR_NORMAL);
            btnActivo.setForeground(colorNormal);
            btnActivo.setFont(UIUtils.F_NORMAL);
            // Restaurar ícono con color normal
            if (btnActivo.getIcon() instanceof MenuIcon mi) {
                btnActivo.setIcon(new MenuIcon(mi.getTipo(), 18, colorNormal));
            }
        }
        btnActivo = btn;
        btn.setBackground(SIDEBAR_ACTIVO);
        btn.setForeground(Color.WHITE);
        btn.setFont(UIUtils.F_BOTON);
        // Ícono activo en blanco puro
        if (btn.getIcon() instanceof MenuIcon mi) {
            btn.setIcon(new MenuIcon(mi.getTipo(), 18, Color.WHITE));
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    // LÓGICA DE AUTENTICACIÓN
    // ════════════════════════════════════════════════════════════════════════════
    private void validarLogin() {
        String usuario = campoUsuario.getText().trim();
        String clave   = new String(campoClave.getPassword());

        if (usuario.isEmpty() || clave.isEmpty()) {
            UIUtils.alerta(this, "Por favor ingrese usuario y contraseña.");
            return;
        }
        if (USUARIO.equals(usuario) && CLAVE.equals(clave)) {
            campoClave.setText("");
            cardLayout.show(panelPrincipal, "dashboard");
        } else {
            UIUtils.alerta(this, "Usuario o contraseña incorrectos.\n\nCredenciales de demo: admin / 123456");
            campoClave.setText("");
            campoUsuario.requestFocus();
        }
    }

    private void cerrarSesion() {
        if (UIUtils.confirmar(this, "¿Desea cerrar sesión?")) {
            campoUsuario.setText("");
            campoClave.setText("");
            cardLayout.show(panelPrincipal, "login");
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    // MAIN — Punto de entrada de la aplicación
    // ════════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        // Look and Feel nativo del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, usa el L&F por defecto de Java
        }

        // Ajustes globales de fuente para mejor legibilidad
        Font fuenteBase = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("Button.font",       fuenteBase);
        UIManager.put("Label.font",        fuenteBase);
        UIManager.put("TextField.font",    fuenteBase);
        UIManager.put("ComboBox.font",     fuenteBase);
        UIManager.put("Table.font",        fuenteBase);
        UIManager.put("TableHeader.font",  new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("OptionPane.messageFont", fuenteBase);

        SwingUtilities.invokeLater(() -> new SIGEH_Modern_UI().setVisible(true));
    }
}