public class EncombrantPourTourneeHO3 {

    // Nom sommet départ rue/arc
    private final String nomU;

    // Nom sommet arrivée rue/arc
    private final String nomV;

    // Position le long de l'arête depuis U
    private final double positionDepuisU;

    // True si (U,V) est une "rue double sens une seule voie" (HO1),
    // False si c'est un arc orienté (HO2).
    private final boolean estDoubleSensUneSeuleVoie;

    public EncombrantPourTourneeHO3(String nomU,
                                    String nomV,
                                    double positionDepuisU,
                                    boolean estDoubleSensUneSeuleVoie) {
        this.nomU = nomU;
        this.nomV = nomV;
        this.positionDepuisU = positionDepuisU;
        this.estDoubleSensUneSeuleVoie = estDoubleSensUneSeuleVoie;
    }

    public String getNomU() {
        return nomU;
    }

    public String getNomV() {
        return nomV;
    }

    public double getPositionDepuisU() {
        return positionDepuisU;
    }

    public boolean isDoubleSensUneSeuleVoie() {
        return estDoubleSensUneSeuleVoie;
    }
}
