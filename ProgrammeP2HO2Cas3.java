import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProgrammeP2HO2Cas3 {

    private static void calculerDegres(GrapheRoutier g, int[] degIn, int[] degOut) {
        int n = g.getNbSommets();
        for (int u = 0; u < n; u++) {
            for (Arete a : g.getVoisins(u)) {
                int v = a.getSommetVoisin();
                degOut[u]++;
                degIn[v]++;
            }
        }
    }

    // Somme des longueurs de tous les arcs
    private static double sommeTousArcs(GrapheRoutier g) {
        double somme = 0.0;
        int n = g.getNbSommets();
        for (int u = 0; u < n; u++) {
            for (Arete a : g.getVoisins(u)) {
                somme += a.getLongueur();
            }
        }
        return somme;
    }

    // backtracking pour l'appariement deficit[i] -> surplus[j]

    private static double[][] cout;   // cout[i][j] = plus court chemin de deficit[i] vers surplus[j]
    private static int M;             // nombre total d'unités (copies)
    private static double meilleurCout;
    private static boolean[] surplusPris;

    private static void backtrack(int i, double coutActuel) {
        if (i == M) {
            if (coutActuel < meilleurCout) meilleurCout = coutActuel;
            return;
        }

        if (coutActuel >= meilleurCout) return; // pruning

        for (int j = 0; j < M; j++) {
            if (!surplusPris[j]) {
                surplusPris[j] = true;
                backtrack(i + 1, coutActuel + cout[i][j]);
                surplusPris[j] = false;
            }
        }
    }

    public static void executerCas3HO2(String nomFichier) {
        try {
            LecteurDeFichierP2HO2.InstanceP2HO2 instance =
                    LecteurDeFichierP2HO2.lireInstance(nomFichier);
            GrapheRoutier g = instance.getGraphe();

            int n = g.getNbSommets();
            int[] degIn = new int[n];
            int[] degOut = new int[n];

            calculerDegres(g, degIn, degOut);

            double sommeArcs = sommeTousArcs(g);

            // Construire les listes "déficit" (delta < 0) et "surplus" (delta > 0)
            List<Integer> deficits = new ArrayList<>();
            List<Integer> surplus = new ArrayList<>();

            for (int v = 0; v < n; v++) {
                int delta = degOut[v] - degIn[v];
                if (delta < 0) {
                    for (int k = 0; k < -delta; k++) deficits.add(v);
                } else if (delta > 0) {
                    for (int k = 0; k < delta; k++) surplus.add(v);
                }
            }

            if (deficits.isEmpty() && surplus.isEmpty()) {
                System.out.println("Tous les sommets sont déjà équilibrés (cas eulérien) : utilisez plutôt le Cas 1.");
                return;
            }

            if (deficits.size() != surplus.size()) {
                System.out.println("Erreur : le total des deltas ne s’équilibre pas (deficits != surplus).");
                return;
            }

            M = deficits.size();
            cout = new double[M][M];

            // Plus courts chemins de chaque déficit vers chaque surplus
            for (int i = 0; i < M; i++) {
                int s = deficits.get(i);
                ResultatDuDijkstra res = Dijkstra.calculerPlusCourtsChemins(g, s);
                double[] dist = res.getDistanceDepuisSource();
                for (int j = 0; j < M; j++) {
                    int t = surplus.get(j);
                    cout[i][j] = dist[t];
                }
            }

            meilleurCout = Double.POSITIVE_INFINITY;
            surplusPris = new boolean[M];
            Arrays.fill(surplusPris, false);
            backtrack(0, 0.0);

            double longueurTotale = sommeArcs + meilleurCout;

            System.out.println(" P2 / HO2 / Cas 3 : Cas général (Postier chinois dirigé simplifié) ");
            System.out.println("Nombre total d'unités de déséquilibre : " + M);
            System.out.println("Somme de tous les arcs (une fois chacun) : " + sommeArcs + " m");
            System.out.println("Coût minimal pour rééquilibrer les degrés (ajout de chemins) : "
                    + meilleurCout + " m");
            System.out.println("Longueur minimale approximative d'une tournée couvrant tous les arcs : "
                    + longueurTotale + " m");
            System.out.println("\n");

        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
