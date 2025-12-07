public class Arete {

    private Sommet sommet1;
    private Sommet sommet2;
    private int distance;

    public Arete(Sommet sommet1, Sommet sommet2, int distance) {
        this.sommet1 = sommet1;
        this.sommet2 = sommet2;
        this.distance = distance;
    }

    public Sommet getSommet1() {
        return sommet1;
    }

    public Sommet getSommet2() {
        return sommet2;
    }

    public int getDistance() {
        return distance;
    }
}
