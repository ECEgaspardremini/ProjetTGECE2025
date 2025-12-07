public class Secteur {

    private String nom;      // nom du secteur
    private int quantite;    // quantité en tonnes utilisé en H2

    //constructeur simple pour h1 juste secteur avec son nom et pas de quantité
    public Secteur(String nom) {
        this.nom = nom;
        this.quantite = 0;
    }

    // constructeur complet pour H2 avec nom secteur + sa quantité
    public Secteur(String nom, int quantite) {
        this.nom = nom;
        this.quantite = quantite;
    }

    // getters et setters
    public String getNom() {
        return nom;
    }
    public int getQuantite() {
        return quantite;
    }

    //mettre a jour la quantité
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    //méthodes

    @Override
    public String toString() {
        return nom;
    }

    //deux secteurs sont identiques quand ils ont le meme nom utiles pour les list
    @Override
    public boolean equals(Object o) {

        // même objet donc égal
        if (this == o) return true;

        // pas le même type donc pas egal
        if (!(o instanceof Secteur)) return false;

        Secteur autre = (Secteur) o;
        return nom != null && nom.equals(autre.nom);
    }

    @Override
    public int hashCode() {
        return nom != null ? nom.hashCode() : 0;
    }
}
