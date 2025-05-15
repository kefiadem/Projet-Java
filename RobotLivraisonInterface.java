import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RobotLivraisonInterface extends JFrame {
    
    private robotLivraison robot;
    
    private DrawPanel drawPanel; 
    private JTextArea logArea; 
    private JButton btnDemarrer, btnArreter, btnCharger, btnLivrer, btnDeplacer, btnConnecter, btnDeconnecter, btnEnvoyer, btnRechargerStandard;
    private JTextField txtX, txtY, txtColis, txtDestination, txtReseau, txtDonnees, txtRechargeAmount;
    
    private Timer statusUpdateTimer; 
    private Timer autoSolarChargeTimer; 
    private Timer stationChargeTimer; 

    private final int GRID_CELL_SIZE_FOR_CLICK = 20; 

    private List<SolarStation> solarStations;

    public RobotLivraisonInterface() throws robotException {
        this.setTitle("Robot Livraison Écologique");
        robot = new robotLivraison("R22", 3, 3, 50, 0, false); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5,5));

        solarStations = new ArrayList<>();
        solarStations.add(new SolarStation(5, 5, 2, "Station Shell")); 
        solarStations.add(new SolarStation(15, 18, 2, "Station Ola Energie"));

        logArea = new JTextArea(8, 30); 
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(logArea);

        scroll.setBorder(BorderFactory.createTitledBorder("Journal d'Événements"));

        drawPanel = new DrawPanel(solarStations); 
        drawPanel.setPreferredSize(new Dimension(   500, 500)); 
        add(drawPanel, BorderLayout.CENTER);

        drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int clickedX = e.getX();
                int clickedY = e.getY();
                int gridX = clickedX / GRID_CELL_SIZE_FOR_CLICK;
                int gridY = clickedY / GRID_CELL_SIZE_FOR_CLICK;
                txtX.setText(String.valueOf(gridX));
                txtY.setText(String.valueOf(gridY));
                log("Clic sur la grille: X=" + gridX + ", Y=" + gridY + ". Coordonnées mises à jour.");
            }
        });

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS)); 

        JPanel actionsPanel = new JPanel(new GridLayout(0, 2, 5, 5)); 
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Commandes Robot"));

        btnDemarrer = new JButton("Démarrer");
        btnDemarrer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    robot.demarrer();
                    log("Robot démarré.");
                } catch (robotException ex) { 
                    log("Erreur Robot: " + ex.getMessage());
                } catch (Exception ex) { 
                    log("Erreur inattendue: " + ex.getMessage());
                    ex.printStackTrace();
                }
                updateStatusLabels();
            }
        });

        btnArreter = new JButton("Arrêter"); 
        btnArreter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    robot.arreter();
                    log("Robot arrêté.");
                } catch (robotException ex) { 
                    log("Erreur Robot: " + ex.getMessage());
                } catch (Exception ex) { 
                    log("Erreur inattendue: " + ex.getMessage());
                    ex.printStackTrace();
                }
                updateStatusLabels();
            }
        });
        actionsPanel.add(btnDemarrer); 
        actionsPanel.add(btnArreter);

        txtRechargeAmount = new JTextField("20", 3);
        btnRechargerStandard = new JButton("Recharger Standard"); 
        btnRechargerStandard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int amount = Integer.parseInt(txtRechargeAmount.getText());
                    robot.recharger(amount);
                    log("Recharge standard de " + amount + "%");
                } catch (robotException ex) { 
                    log("Erreur Robot: " + ex.getMessage());
                } catch (NumberFormatException ex) { 
                    log("Erreur Format Nombre: " + ex.getMessage());
                } catch (Exception ex) { 
                    log("Erreur inattendue: " + ex.getMessage());
                    ex.printStackTrace();
                }
                updateStatusLabels();
            }
        });
        JPanel rechargePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        rechargePanel.add(new JLabel("Qté:"));
        rechargePanel.add(txtRechargeAmount);
        rechargePanel.add(btnRechargerStandard);
        actionsPanel.add(rechargePanel);
        
        actionsPanel.add(new JLabel("")); 
        
        txtColis = new JTextField(8); txtDestination = new JTextField(8);
        btnCharger = new JButton("Charger Colis"); 
        btnCharger.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nom = txtColis.getText();
                    String dest = txtDestination.getText();
                    robot.chargerColis(nom, dest);
                    log("Colis chargé: " + nom + " pour " + dest);
                } catch (robotException ex) { 
                    log("Erreur Robot: " + ex.getMessage());
                } catch (Exception ex) { 
                    log("Erreur inattendue: " + ex.getMessage());
                    ex.printStackTrace();
                }
                updateStatusLabels();
            }
        });
        JPanel colisPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        colisPanel.add(new JLabel("Colis:"));
        colisPanel.add(txtColis);
        actionsPanel.add(colisPanel);
        JPanel destPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        destPanel.add(new JLabel("Destination:"));
        destPanel.add(txtDestination);
        destPanel.add(btnCharger);
        actionsPanel.add(destPanel);
        
        txtX = new JTextField(3); txtY = new JTextField(3);
        btnLivrer = new JButton("Livrer Colis"); 
        btnLivrer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int x = Integer.parseInt(txtX.getText());
                    int y = Integer.parseInt(txtY.getText());
                    robot.faireLivraison(x, y);
                    log("Livraison effectuée à ("+x+","+y+").");
                } catch (robotException ex) { 
                    log("Erreur Robot: " + ex.getMessage());
                } catch (NumberFormatException ex) { 
                    log("Erreur Format Nombre: " + ex.getMessage());
                } catch (Exception ex) { 
                    log("Erreur inattendue: " + ex.getMessage());
                    ex.printStackTrace();
                }
                updateStatusLabels();
            }
        });
        JPanel xyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        xyPanel.add(new JLabel("X:"));
        xyPanel.add(txtX);
        xyPanel.add(new JLabel("Y:"));
        xyPanel.add(txtY);
        xyPanel.add(btnLivrer);
        actionsPanel.add(xyPanel);

        btnDeplacer = new JButton("Déplacer Vers"); 
        btnDeplacer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int x = Integer.parseInt(txtX.getText());
                    int y = Integer.parseInt(txtY.getText());
                    robot.deplacer(x, y);
                    log("Déplacé à ("+x+","+y+").");
                } catch (robotException ex) { 
                    log("Erreur Robot: " + ex.getMessage());
                } catch (NumberFormatException ex) { 
                    log("Erreur Format Nombre: " + ex.getMessage());
                } catch (Exception ex) { 
                    log("Erreur inattendue: " + ex.getMessage());
                    ex.printStackTrace();
                }
                updateStatusLabels();
            }
        });
        JPanel deplacerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        deplacerPanel.add(new JLabel("(Utilise X,Y)"));
        deplacerPanel.add(btnDeplacer);
        actionsPanel.add(deplacerPanel); 

        txtReseau = new JTextField(8);
        btnConnecter = new JButton("Connecter"); 
        btnConnecter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String net = txtReseau.getText();
                    robot.connecter(net);
                    log("Connecté à " + net);
                } catch (robotException ex) { 
                    log("Erreur Robot: " + ex.getMessage());
                } catch (Exception ex) { 
                    log("Erreur inattendue: " + ex.getMessage());
                    ex.printStackTrace();
                }
                updateStatusLabels();
            }
        });

        btnDeconnecter = new JButton("Déconnecter"); 
        btnDeconnecter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    robot.deconnecter();
                    log("Déconnecté.");
                } catch (Exception ex) { 
                    log("Erreur pendant la déconnexion: " + ex.getMessage());
                    ex.printStackTrace(); 
                }
                updateStatusLabels();
            }
        });
        JPanel reseauPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        reseauPanel.add(new JLabel("Réseau:"));
        reseauPanel.add(txtReseau);
        reseauPanel.add(btnConnecter);
        actionsPanel.add(reseauPanel);
        actionsPanel.add(btnDeconnecter); 

        txtDonnees = new JTextField(12);
        btnEnvoyer = new JButton("Envoyer Données"); 
        btnEnvoyer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String data = txtDonnees.getText();
                    robot.envoyerDonnees(data);
                    log("Données envoyées: " + data);
                } catch (robotException ex) { 
                    log("Erreur Robot: " + ex.getMessage());
                } catch (Exception ex) { 
                    log("Erreur inattendue: " + ex.getMessage());
                    ex.printStackTrace();
                }
                updateStatusLabels();
            }
        });
        JPanel donneesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        donneesPanel.add(new JLabel("Données:"));
        donneesPanel.add(txtDonnees);
        donneesPanel.add(btnEnvoyer);
        actionsPanel.add(donneesPanel);
        
        controlPanel.add(actionsPanel);
        controlPanel.add(scroll); 

        add(controlPanel, BorderLayout.EAST);
        pack();
        setLocationRelativeTo(null); 
        setVisible(true);

        statusUpdateTimer = new Timer(1000, ev -> updateStatusLabels());
        statusUpdateTimer.start();
        
        autoSolarChargeTimer = new Timer(20000, ev -> {
            if (robot != null && robot.isEnMarche()) { 
                try {
                    boolean chargedBySun = robot.rechargerSolaireAutomatique();
                    if (chargedBySun) {
                        log("Recharge solaire auto (heure): +1%.");
                    }
                } catch (robotException rex) {
                    log("Erreur recharge solaire auto: " + rex.getMessage());
                }
            }
        });
        autoSolarChargeTimer.start();
        
        stationChargeTimer = new Timer(2000, ev -> {
            if (robot != null && robot.isEnMarche()) {
                for (SolarStation station : solarStations) {
                    double distance = Math.sqrt(Math.pow(robot.getX_coord() - station.x, 2) + Math.pow(robot.getY_coord() - station.y, 2));
                    if (distance <= station.radius) { 
                        try {
                            boolean chargedByStation = robot.rechargerViaStation();
                            if (chargedByStation) {
                                log("Recharge via " + station.name + ": +1%.");
                            }
                        } catch (robotException rex) {
                             log("Erreur recharge station: " + rex.getMessage());
                        }
                        break; 
                    }
                }
            }
        });
        stationChargeTimer.start();

        updateStatusLabels(); 
    }

    
    private void updateStatusLabels() {
        if (robot != null) {
            if (drawPanel != null) drawPanel.repaint(); 
        }
    }

    private void log(String msg) {
        if (logArea != null) { 
            logArea.append(LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + " - " + msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength()); 
        } else {
            System.out.println("LOG (logArea not init): " + msg); 
        }
    }

    class DrawPanel extends JPanel {
        private Image img;
        public static final int CELL_SIZE_PX = 20; 
        public static final int ROBOT_IMG_SIZE_PX = 60; 
        private List<SolarStation> stationsToDraw;
        private final int STATUS_INDICATOR_DIAMETER = 10;
        private final int STATUS_INDICATOR_MARGIN = 5;

        public DrawPanel(List<SolarStation> stations) {
            this.stationsToDraw = stations;
            setBackground(Color.WHITE);
            try {
                File imageFile = new File("RobotProject/robot.png"); 
                if (!imageFile.exists()) { 
                    imageFile = new File("robot.png");
                }
                BufferedImage b = ImageIO.read(imageFile);
                img = b.getScaledInstance(ROBOT_IMG_SIZE_PX, ROBOT_IMG_SIZE_PX, Image.SCALE_SMOOTH);
                log("Image robot.png chargée."); 
            } catch (Exception ex) {
                log("Erreur chargement image robot.png: " + ex.getMessage());
                System.err.println("Erreur image: " + ex.getMessage());
                img = null; 
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i < getWidth(); i += 50) g2d.drawLine(i, 0, i, getHeight());
            for (int j = 0; j < getHeight(); j += 50) g2d.drawLine(0, j, getWidth(), j);

            if (stationsToDraw != null) {
                for (SolarStation station : stationsToDraw) {
                    g2d.setColor(Color.YELLOW);
                    int stationDiameterPx = station.radius * 2 * CELL_SIZE_PX;
                    int stationCenterXpx = station.x * CELL_SIZE_PX;
                    int stationCenterYpx = station.y * CELL_SIZE_PX;
                    int topLeftX = stationCenterXpx - (stationDiameterPx / 2);
                    int topLeftY = stationCenterYpx - (stationDiameterPx / 2);
                    g2d.fillOval(topLeftX, topLeftY, stationDiameterPx, stationDiameterPx);
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(station.name, topLeftX, topLeftY - 5); 
                }
            }
            
            if (robot != null) {
                int robotCellPixelX = robot.getX_coord() * CELL_SIZE_PX;
                int robotCellPixelY = robot.getY_coord() * CELL_SIZE_PX;
                
                int robotDrawX = robotCellPixelX + (CELL_SIZE_PX / 2) - (ROBOT_IMG_SIZE_PX / 2);
                int robotDrawY = robotCellPixelY + (CELL_SIZE_PX / 2) - (ROBOT_IMG_SIZE_PX / 2);

                if (img != null) {
                    g2d.drawImage(img, robotDrawX, robotDrawY, this);
                } else {
                    g2d.setColor(Color.BLUE);
                    g2d.fillRect(robotDrawX, robotDrawY, ROBOT_IMG_SIZE_PX / 2, ROBOT_IMG_SIZE_PX / 2); 
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(robot.getId(), robotDrawX, robotDrawY + ROBOT_IMG_SIZE_PX /2 + 10);
                }
                g2d.setColor(Color.BLACK);
                g2d.drawString(robot.getEnergie() + "%", robotDrawX + ROBOT_IMG_SIZE_PX + 5, robotDrawY + ROBOT_IMG_SIZE_PX / 2);
                
                if (robot.isEnMarche()) {
                    g2d.setColor(Color.GREEN);
                } else {
                    g2d.setColor(Color.RED);
                }
                int indicatorX = getWidth() - STATUS_INDICATOR_DIAMETER - STATUS_INDICATOR_MARGIN;
                int indicatorY = STATUS_INDICATOR_MARGIN;
                g2d.fillOval(indicatorX, indicatorY, STATUS_INDICATOR_DIAMETER, STATUS_INDICATOR_DIAMETER);
            }
        }
    }


}