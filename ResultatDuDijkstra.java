public class ResultatDuDijkstra {

    private final double[] distanceDepuisSource;
    private final int[] predecesseur;

    public ResultatDuDijkstra(double[] distanceDepuisSource, int[] predecesseur) {
        this.distanceDepuisSource = distanceDepuisSource;
        this.predecesseur = predecesseur;
    }

    public double[] getDistanceDepuisSource() {
        return distanceDepuisSource;
    }

    public int[] getPredecesseur() {
        return predecesseur;
    }
}
