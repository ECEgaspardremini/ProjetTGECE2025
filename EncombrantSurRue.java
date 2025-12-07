public class EncombrantSurRue {

    private final int sommetU;
    private final int sommetV;
    private final double positionDepuisU;
    private final double longueurRue;

    // true si l'encombrant est sur une "rue double sens une seule voie" (HO3 type HO1)
    private final boolean surAreteDoubleSensUneVoie;

    // Constructeur utilisé par HO1 / HO2
    public EncombrantSurRue(int sommetU, int sommetV, double positionDepuisU, double longueurRue) {
        this(sommetU, sommetV, positionDepuisU, longueurRue, false);
    }

    // Constructeur complet pour HO3
    public EncombrantSurRue(int sommetU, int sommetV, double positionDepuisU,
                            double longueurRue, boolean surAreteDoubleSensUneVoie) {
        this.sommetU = sommetU;
        this.sommetV = sommetV;
        this.positionDepuisU = positionDepuisU;
        this.longueurRue = longueurRue;
        this.surAreteDoubleSensUneVoie = surAreteDoubleSensUneVoie;
    }

    public int getSommetU() {
        return sommetU;
    }

    public int getSommetV() {
        return sommetV;
    }

    public double getPositionDepuisU() {
        return positionDepuisU;
    }

    public double getLongueurRue() {
        return longueurRue;
    }

    public boolean isSurAreteDoubleSensUneVoie() {
        return surAreteDoubleSensUneVoie;
    }
}
