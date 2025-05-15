public abstract class robotConnecte extends robot implements Connectable {
    protected Boolean connecte;
    protected String reseauConnecte;
    public robotConnecte(String id, int x, int y, int energie, int heuresUtilisation, Boolean enMarche) throws robotException {
        super(id, x, y, energie, heuresUtilisation, enMarche);
        this.connecte = false;
        this.reseauConnecte = null;
    }
    
    @Override 
    public void connecter(String reseau) throws robotException {
        if (connecte) {
            throw new robotException("Le robot est déjà connecté à un réseau.");
        } 
        if (verifierEnergie(5)){
            this.reseauConnecte = reseau;
            this.connecte = true;
            consommerEnergie(5);
            ajouterHistorique("Robot connecté au réseau " + reseau);
        }
    }

    @Override
    public void deconnecter() {
        if (connecte) {
            this.reseauConnecte = null;
            this.connecte = false;
            ajouterHistorique("Robot déconnecté du réseau " + reseauConnecte);
        } else {
            System.out.println("Le robot n'est pas connecté à un réseau.");
        }
    }

    @Override
    public void envoyerDonnees(String donnees) throws robotException {
        if (!connecte) {
            throw new robotException("Le robot n'est pas connecté à un réseau.");
        } else if (verifierEnergie(3)) {
            consommerEnergie(3);
            ajouterHistorique("Données envoyées: " + donnees);
        } else {
            throw new robotException("Energie insuffisante pour envoyer des données.");
        }
    }
    





}