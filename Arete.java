
public class Arete {

    private final int sommetVoisin;
    private final double longueur;

    public Arete(int sommetVoisin, double longueur) {
        this.sommetVoisin = sommetVoisin;
        this.longueur = longueur;
    }

    public int getSommetVoisin() {
        return sommetVoisin;
    }

    public double getLongueur() {
        return longueur;
    }
}
