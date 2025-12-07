public class EncombrantPourTournee {

    private final int idCarrefourAssocie;      // carrefour le plus proche de l'encombrant
    private final String nomU;                 // extrémité 1
    private final String nomV;                 // extrémité 2
    private final double distanceDepuisCarrefour; // distance (m) du carrefour à l'encombrant

    public EncombrantPourTournee(int idCarrefourAssocie,
                                 String nomU,
                                 String nomV,
                                 double distanceDepuisCarrefour) {
        this.idCarrefourAssocie = idCarrefourAssocie;
        this.nomU = nomU;
        this.nomV = nomV;
        this.distanceDepuisCarrefour = distanceDepuisCarrefour;
    }

    public int getIdCarrefourAssocie() {
        return idCarrefourAssocie;
    }

    public String getNomU() {
        return nomU;
    }

    public String getNomV() {
        return nomV;
    }

    public double getDistanceDepuisCarrefour() {
        return distanceDepuisCarrefour;
    }
}
