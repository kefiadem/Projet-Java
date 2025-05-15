import java.util.Scanner;

public class robotLivraison extends robotConnecte {
    private String colisActuel;
    private String destination;
    private Boolean enLivaison;
    private static final int ENERGIE_LIVRAISON = 15;
    private static final int ENERGIE_CHARGEMENT = 5;
    
    public robotLivraison(String id, int x, int y, int energie, int heuresUtilisation, Boolean enMarche) throws robotException {
        super(id, x, y, energie, heuresUtilisation, enMarche);
        this.colisActuel = null;
        this.destination = null;
        this.enLivaison = false;
    }
    
    @Override
    public void effectuerTache() throws robotException {
        if (!enMarche) {
            throw new robotException("Le robot doit être démarré pour effectuer une tâche.");
        }
        if (enLivaison){
            Scanner scanner = new Scanner(System.in);
            System.err.println("Donner les coordonnées de la destination (x y): ");
            int destX = scanner.nextInt(); 
            int destY = scanner.nextInt(); 
            faireLivraison(destX, destY);
        }
        else {
            Scanner scanner = new Scanner(System.in);
            System.err.println("Vouelez-vous charger un nouveau colis ? (O/N): ");
            String reponse = scanner.nextLine();
            while (!reponse.equalsIgnoreCase("O") && !reponse.equalsIgnoreCase("N")) {
                System.err.println("Réponse invalide. veuillez saisir (O) pour oui ou (N) pour non: ");
                reponse = scanner.nextLine();
            }
            if (reponse.equalsIgnoreCase("O")) {
                System.err.println("Donner le nom du colis: ");
                String nomcolis = scanner.nextLine();
                System.err.println("Donner la destination du colis: ");
                String dest = scanner.nextLine();
                chargerColis(nomcolis,dest);
            } else {
                ajouterHistorique("En attente de colis (tâche annulée par l'utilisateur)");
            }
        }
    }

    public void chargerColis(String nom,String destination) throws robotException {
        if (!enMarche) {
            throw new robotException("Le robot doit être démarré pour charger un colis.");
        }
        if (!enLivaison && colisActuel == null && verifierEnergie(ENERGIE_CHARGEMENT)) {
                consommerEnergie(ENERGIE_CHARGEMENT);
                enLivaison = true;
                colisActuel = nom;
                this.destination = destination;
                ajouterHistorique("Colis " + nom + " chargé vers " + destination);
            } 
        else {
            if (colisActuel != null) {
                throw new robotException("Le robot a déjà un colis en cours de livraison.");
            } else if (enLivaison) {
                throw new robotException("Le robot est déjà en cours de livraison (sans colis spécifié).");
            } else {
                 throw new robotException("Le robot ne peut pas charger de colis actuellement (énergie insuffisante ou autre statut).");
            }
        }
    }
    
    public void faireLivraison (int xDest, int yDest) throws robotException { 
        if (!enMarche) {
            throw new robotException("Le robot doit être démarré pour faire une livraison.");
        }
        if (!enLivaison || colisActuel == null) {
            throw new robotException("Aucun colis n'est actuellement chargé pour la livraison.");
        }
        if (verifierEnergie(ENERGIE_LIVRAISON)){
            consommerEnergie(ENERGIE_LIVRAISON);
            deplacer(xDest, yDest);
            ajouterHistorique("Livraison terminée à " + this.destination + " (" + xDest + "," + yDest + "). Colis: " + colisActuel);
            colisActuel = null;
            this.destination = null; 
            enLivaison = false;
        } 
    }

    @Override
    public void deplacer(int newX, int newY) throws robotException { 
        if (!enMarche) {
            throw new robotException("Le robot doit être démarré pour se déplacer.");
        }
        double distance = Math.sqrt(Math.pow(newX - this.x, 2) + Math.pow(newY - this.y, 2));
        if (distance > 100 )
        {
            throw new robotException("Le robot ne peut pas se déplacer à cette distance (" + String.format("%.2f", distance) + "). Limite: 100.");
        } 
        int energiereq = (int)(distance*0.3);
        if (verifierEnergie(energiereq) && verfierMaintenance()) {
            this.x = newX;
            this.y = newY;
            consommerEnergie(energiereq); 
            heuresUtilisation += (int)(distance/10);
            ajouterHistorique("Robot déplacé vers (" + this.x + ", " + this.y + ")");
        }
    }


    @Override
    public String toString() {
        return super.toString() + ", Colis : " + (colisActuel != null ? colisActuel : "Aucun") + ", Destination : " + (destination != null ? destination : "Aucune") + ", En Livraison: " + (enLivaison ? "Oui" : "Non");
    }

    public String getColisActuel() {
        return colisActuel;
    }

    public String getDestination() {
        return destination;
    }

    public Boolean isEnLivaison() { 
        return enLivaison;
    }

}