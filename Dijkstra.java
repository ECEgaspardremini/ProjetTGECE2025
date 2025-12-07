import java.util.*;

public class Dijkstra {

    private static class SommetAvecDistance {
        int idSommet;
        double distance;

        SommetAvecDistance(int idSommet, double distance) {
            this.idSommet = idSommet;
            this.distance = distance;
        }
    }

    public static ResultatDuDijkstra calculerPlusCourtsChemins(GrapheRoutier graphe, int idSource) {

        int n = graphe.getNbSommets();
        double[] dist = new double[n];
        int[] pred = new int[n];
        boolean[] visite = new boolean[n];

        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(pred, -1);
        dist[idSource] = 0.0;

        PriorityQueue<SommetAvecDistance> filePrioritaire =
                new PriorityQueue<>(Comparator.comparingDouble(s -> s.distance));

        filePrioritaire.add(new SommetAvecDistance(idSource, 0.0));

        while (!filePrioritaire.isEmpty()) {
            SommetAvecDistance courant = filePrioritaire.poll();
            int u = courant.idSommet;
            if (visite[u]) continue;
            visite[u] = true;

            for (Arete a : graphe.getVoisins(u)) {
                int v = a.getSommetVoisin();
                double w = a.getLongueur();
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    pred[v] = u;
                    filePrioritaire.add(new SommetAvecDistance(v, dist[v]));
                }
            }
        }

        return new ResultatDuDijkstra(dist, pred);
    }
}
