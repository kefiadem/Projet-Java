public interface Connectable {
    void connecter(String reseau) throws robotException;
    void deconnecter();
    void envoyerDonnees(String donnees) throws robotException;
}
