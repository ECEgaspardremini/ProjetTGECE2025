public class Camion {

    private double capaciteMax;
    private double chargeActuelle;
    private Sommet positionActuelle;

    public Camion(double capaciteMax, Sommet positionInitiale) {
        this.capaciteMax = capaciteMax;
        this.positionActuelle = positionInitiale;
        this.chargeActuelle = 0.0;
    }


    public double getCapaciteMax() {
        return capaciteMax;
    }


    public double getChargeActuelle() {
        return chargeActuelle;
    }
    public void setChargeActuelle(double chargeActuelle) {
        this.chargeActuelle = chargeActuelle;
    }



    public Sommet getPositionActuelle() {
        return positionActuelle;
    }

    public void setPositionActuelle(Sommet positionActuelle) {
        this.positionActuelle = positionActuelle;
    }

    public void viderAuDepot() {
        this.chargeActuelle = 0.0;
    }

    public double getPlaceDisponible() {
        return capaciteMax - chargeActuelle;
    }


    public boolean estPlein() {
        return chargeActuelle >= capaciteMax - 1e-9;
    }
}
