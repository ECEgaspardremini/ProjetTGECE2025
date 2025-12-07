public class EncombrantOrienteHO2 {

    // Nom du sommet de départ de l'arc (U dans U -> V)
    private final String nomSommetDepart;

    // Nom du sommet d'arrivée de l'arc (V dans U -> V)
    private final String nomSommetArrivee;

    // Position (distance en mètres depuis le sommet de départ U)
    private final double positionDepuisDepart;

    public EncombrantOrienteHO2(String nomSommetDepart,
                                String nomSommetArrivee,
                                double positionDepuisDepart) {
        this.nomSommetDepart = nomSommetDepart;
        this.nomSommetArrivee = nomSommetArrivee;
        this.positionDepuisDepart = positionDepuisDepart;
    }

    public String getNomSommetDepart() {
        return nomSommetDepart;
    }

    public String getNomSommetArrivee() {
        return nomSommetArrivee;
    }

    public double getPositionDepuisDepart() {
        return positionDepuisDepart;
    }
}
