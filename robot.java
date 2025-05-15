import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public abstract class robot {

    protected String id ;
    protected int x ,y;
    protected int energie;
    protected int heuresUtilisation;
    protected Boolean enMarche ;
    protected ArrayList<String> historiqueActions = new ArrayList<String>();
    
    public robot(String id, int x, int y, int energie, int heuresUtilisation, Boolean enMarche) throws robotException {
        this.id = id;
        this.x = x;
        this.y = y;
        if (energie < 0 || energie > 100) {
            throw new robotException("L'énergie doit être comprise entre 0 et 100.");
        }
        else {
            this.energie = energie;
        }
        this.heuresUtilisation = heuresUtilisation;
        this.enMarche = enMarche;
        ajouterHistorique("Robot créé");
    }
    
    public void ajouterHistorique(String action) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm:ss", Locale.FRENCH);
        String formattedDate = now.format(formatter);
        historiqueActions.add(formattedDate +" "+action);
    }
    
    public Boolean verifierEnergie(int energieRequise)throws energieInsuffisanteException {
        if (energieRequise > this.energie) { 
            throw new energieInsuffisanteException("Energie insuffisante pour effectuer l'action.");
        }
        else {
            return true;
        }
        
    }
    
    public Boolean verfierMaintenance() throws maintenanceException {
        if (heuresUtilisation > 100) {
            throw new maintenanceException("Le robot nécessite une maintenance.");
        }
        else {
            return true;
        }
    }
    
    public void demarrer() throws robotException {
        if (enMarche) {
            throw new robotException("Le robot est déjà démarré.");
        }
        if (verifierEnergie(10)) { 
            enMarche = true;
            ajouterHistorique("Robot démarré");
        } else {
            throw new robotException("Energie insuffisante pour démarrer le robot.");
        }
    }
    
    public void arreter() throws robotException { 
        if (!enMarche) {
            throw new robotException("Le robot est déjà arrêté.");
        }
        enMarche = false;
        ajouterHistorique("Robot arrêté");
    }
    
    public void consommerEnergie (int quantite) throws robotException {
        if (!enMarche) {
            throw new robotException("Le robot doit être démarré pour consommer de l'énergie.");
        }
        if (quantite < 0) {
             throw new robotException("La quantité d'énergie à consommer ne peut pas être négative.");
        }
        if (quantite > this.energie) { 
            throw new energieInsuffisanteException("Energie insuffisante pour consommer cette quantité (" + quantite + "). Actuelle: " + this.energie);
        } else {
            this.energie -= quantite;
            ajouterHistorique("Energie consommée: " + quantite + ". Restante: " + this.energie);
        }
    }
    
    public void recharger(int quantite) throws robotException {
        if (!enMarche) {
            throw new robotException("Le robot doit être démarré pour être rechargé");
        }
        if (quantite < 0) {
            throw new robotException("La quantité de recharge doit être positive.");
        } else if (this.energie + quantite > 100) {
            this.energie = 100;
            ajouterHistorique("Energie rechargée à 100%  ");
        } else {
            this.energie += quantite;
            ajouterHistorique("Energie rechargée: " + quantite + " Énergie actuelle: " + this.energie);
        }
    }

    public boolean rechargerSolaireAutomatique() throws robotException {
        if (!enMarche) { 
            return false; 
        }
        LocalTime currentTime = LocalTime.now(); 
        int currentHour = currentTime.getHour();
        boolean charged = false;

        if (currentHour >= 8 && currentHour < 16) { 
            if (this.energie < 100) {
                this.energie += 1;
                if (this.energie > 100) { 
                    this.energie = 100;
                }
                ajouterHistorique("Recharge solaire auto (heure): +1%. Énergie: " + this.energie + "%");
                charged = true;
            }
        } 
        return charged; 
    }

    public boolean rechargerViaStation() throws robotException {
         if (!enMarche) {
            return false;
        }
        boolean charged = false;
        if (this.energie < 100) {
            this.energie += 1;
            if (this.energie > 100) { 
                this.energie = 100;
            }
            ajouterHistorique("Recharge via station: +1%. Énergie: " + this.energie + "%");
            charged = true;
        } 
        return charged;
    }

    public abstract void deplacer  (int x, int y) throws robotException;
    
    public abstract void effectuerTache () throws robotException;
    
    public String getHistoriqueActions() {
        String historique = "Affichage de l'historique des actions du robot " + id + " :\n";
        for (String action : historiqueActions) {
            historique += action + "\n";
        }
        return historique;
        
    }

    @Override
    public String toString() {
        return "Robot Industriel [ ID : "+ id + ", Position : (" + x + "," + y + "), Energie : " + energie
                + "%, Heures : " + heuresUtilisation +", État: " + (enMarche ? "En Marche" : "Arrêté") + "]";
    }

    public int getX_coord() { 
        return x;
    }
    public int getY_coord() { 
        return y;
    }
    public int getEnergie() {
        return energie;
    }
    public int getHeuresUtilisation() {
        return heuresUtilisation;
    }
    public Boolean isEnMarche() {
        return enMarche;
    }
    public String getId() {
        return id;
    }

}

