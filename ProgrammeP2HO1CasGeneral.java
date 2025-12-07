import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProgrammeP2HO1CasGeneral {

    //Trouve les sommets de degré impair
    private static List<Integer> trouverSommetImpairs(GrapheRoutier graphe) {
        List<Integer> impairs = new ArrayList<>();
        int n = graphe.getNbSommets();
        for (int u = 0; u < n; u++) {
            int deg = graphe.getVoisins(u).size();
            if (deg % 2 != 0) impairs.add(u);
        }
        return impairs;
    }

    // Calcule la somme des longueurs de toutes les rues (une fois chaque EDGES).
    private static double calculerSommeToutesRues(GrapheRoutier graphe) {
        double somme = 0.0;
        int n = graphe.getNbSommets();
        boolean[][] dejaVu = new boolean[n][n];

        for (int u = 0; u < n; u++) {
            for (Arete a : graphe.getVoisins(u)) {
                int v = a.getSommetVoisin();
                if (!dejaVu[u][v]) {
                    dejaVu[u][v] = true;
                    dejaVu[v][u] = true;
                    somme += a.getLongueur();
                }
            }
        }
        return somme;
    }

    // Recherche de la meilleure appariement des sommets impairs

    private static double meilleurCoutAppariement;
    private static double[][] distImpairs;
    private static int nbImpairs;

    private static void backtrackingAppariement(boolean[] pris, double coutActuel) {
        int i = -1;
        for (int k = 0; k < nbImpairs; k++) {
            if (!pris[k]) {
                i = k;
                break;
            }
        }

        // Si tous pris -> on a une appariement complet
        if (i == -1) {
            if (coutActuel < meilleurCoutAppariement) {
                meilleurCoutAppariement = coutActuel;
            }
            return;
        }

        pris[i] = true;
        for (int j = i + 1; j < nbImpairs; j++) {
            if (!pris[j]) {
                pris[j] = true;
                double nouveauCout = coutActuel + distImpairs[i][j];
                if (nouveauCout < meilleurCoutAppariement) {
                    backtrackingAppariement(pris, nouveauCout);
                }
                pris[j] = false;
            }
        }
        pris[i] = false;
    }

    public static void executerCasGeneral(String nomFichier) {
        try {
            LecteurDeFichierP2HO1.InstanceP2HO1 instance =
                    LecteurDeFichierP2HO1.lireInstance(nomFichier);
            GrapheRoutier graphe = instance.getGraphe();

            double sommeRues = calculerSommeToutesRues(graphe);

            List<Integer> impairs = trouverSommetImpairs(graphe);
            if (impairs.isEmpty()) {
                System.out.println("Tous les sommets sont déjà pairs : cas eulérien (utiliser Cas 1).");
                return;
            }

            nbImpairs = impairs.size();
            if (nbImpairs % 2 != 0) {
                System.out.println("Erreur : nombre de sommets impairs impair (ne devrait pas arriver).");
                return;
            }

            // On calcule les plus courts chemins entre tous les sommets impairs
            distImpairs = new double[nbImpairs][nbImpairs];
            for (int i = 0; i < nbImpairs; i++) {
                int idSommet = impairs.get(i);
                ResultatDuDijkstra res = Dijkstra.calculerPlusCourtsChemins(graphe, idSommet);
                double[] dist = res.getDistanceDepuisSource();
                for (int j = 0; j < nbImpairs; j++) {
                    distImpairs[i][j] = dist[impairs.get(j)];
                }
            }

            meilleurCoutAppariement = Double.POSITIVE_INFINITY;
            boolean[] pris = new boolean[nbImpairs];
            Arrays.fill(pris, false);
            backtrackingAppariement(pris, 0.0);

            double longueurTotalePostier = sommeRues + meilleurCoutAppariement;

            System.out.println(" P2 / HO1 / Cas 3 : Cas général (Postier chinois)");
            System.out.println("Nombre de sommets impairs : " + nbImpairs);
            System.out.println("Somme de toutes les rues (une fois chaque) : " + sommeRues + " m");
            System.out.println("Coût minimal pour dupliquer certaines rues (appariement impairs) : "
                    + meilleurCoutAppariement + " m");
            System.out.println("Longueur minimale d'une tournée qui passe au moins une fois par chaque rue : "
                    + longueurTotalePostier + " m");
            System.out.println("\n");

        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
