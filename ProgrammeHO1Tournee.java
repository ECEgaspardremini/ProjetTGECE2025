import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProgrammeHO1Tournee {

    // Reconstruit le chemin de idSource à idCible à partir des prédécesseurs
    private static List<Integer> reconstruireChemin(int[] predecesseur, int idSource, int idCible) {
        List<Integer> chemin = new ArrayList<>();
        int courant = idCible;
        while (courant != -1) {
            chemin.add(courant);
            if (courant == idSource) break;
            courant = predecesseur[courant];
        }
        Collections.reverse(chemin);
        return chemin;
    }

    public static void executerTourneeHO1(String nomFichier) {

        try {
            // Lire l'instance (graphe + dépôt + carrefours + encombrants)
            InstanceTourneeHO1 instance = LecteurDeFichierTourneeHO1.lireInstanceTourneeHO1(nomFichier);
            GrapheRoutier graphe = instance.getGraphe();
            int idDepot = instance.getIdDepot();
            int[] carrefours = instance.getIdsCarrefoursEncombrants();
            List<EncombrantPourTournee> encombrants = instance.getEncombrantsPourTournee();
            int k = carrefours.length; // nombre de carrefours à visiter

            System.out.println("===== HO1 - Tournée de ramassage (Hypothèse 2) =====");
            System.out.println("Nombre de carrefours avec encombrants à desservir : " + k);
            System.out.println("Dépôt : " + graphe.getNomSommet(idDepot));
            System.out.print("Carrefours concernés : ");
            for (int id : carrefours) {
                System.out.print(graphe.getNomSommet(id) + " ");
            }
            System.out.println("\n");

            // Calculer les plus courts chemins depuis chaque point important (dépôt + carrefours)
            int nbPoints = k + 1; // 0 = dépôt, 1..k = carrefours
            int[] points = new int[nbPoints];
            points[0] = idDepot;
            for (int i = 0; i < k; i++) {
                points[i + 1] = carrefours[i];
            }

            ResultatDuDijkstra[] resultats = new ResultatDuDijkstra[nbPoints];
            for (int i = 0; i < nbPoints; i++) {
                resultats[i] = Dijkstra.calculerPlusCourtsChemins(graphe, points[i]);
            }

            // matrice des distances entre (dépôt + carrefours)
            double[][] dist = new double[nbPoints][nbPoints];
            for (int i = 0; i < nbPoints; i++) {
                double[] d = resultats[i].getDistanceDepuisSource();
                for (int j = 0; j < nbPoints; j++) {
                    dist[i][j] = d[points[j]];
                }
            }

            // Vérifi connexité
            for (int i = 1; i < nbPoints; i++) {
                if (Double.isInfinite(dist[0][i]) || Double.isInfinite(dist[i][0])) {
                    System.out.println("Le carrefour " + graphe.getNomSommet(points[i]) +
                            " n'est pas accessible depuis le dépôt (ou inversement).");
                    return;
                }
            }

            // Résoudre TSP sur les carrefours (dépôt = point 0)
            int maxMask = 1 << k;
            double[][] dp = new double[maxMask][k];
            int[][] pred = new int[maxMask][k];

            for (int mask = 0; mask < maxMask; mask++) {
                for (int i = 0; i < k; i++) {
                    dp[mask][i] = Double.POSITIVE_INFINITY;
                    pred[mask][i] = -1;
                }
            }

            // Départ : du dépôt vers chaque carrefour
            for (int i = 0; i < k; i++) {
                int mask = 1 << i;
                dp[mask][i] = dist[0][i + 1]; // 0 = dépôt, i+1 = carrefour i
            }

            // Transition
            for (int mask = 0; mask < maxMask; mask++) {
                for (int last = 0; last < k; last++) {
                    if ((mask & (1 << last)) == 0) continue;
                    double coutActuel = dp[mask][last];
                    if (Double.isInfinite(coutActuel)) continue;

                    for (int nxt = 0; nxt < k; nxt++) {
                        if ((mask & (1 << nxt)) != 0) continue; // déjà visité
                        int nouveauMask = mask | (1 << nxt);
                        double nouveauCout = coutActuel + dist[last + 1][nxt + 1];
                        if (nouveauCout < dp[nouveauMask][nxt]) {
                            dp[nouveauMask][nxt] = nouveauCout;
                            pred[nouveauMask][nxt] = last;
                        }
                    }
                }
            }

            // Revenir au dépôt : ferme la boucle
            int fullMask = maxMask - 1;
            double meilleurCout = Double.POSITIVE_INFINITY;
            int dernierPoint = -1;

            for (int last = 0; last < k; last++) {
                double coutTour = dp[fullMask][last] + dist[last + 1][0]; // retour au dépôt
                if (coutTour < meilleurCout) {
                    meilleurCout = coutTour;
                    dernierPoint = last;
                }
            }

            if (Double.isInfinite(meilleurCout) || dernierPoint == -1) {
                System.out.println("Impossible de construire une tournée couvrant tous les carrefours.");
                return;
            }

            // Reconstruction de l'ordre de visite des carrefours (indices 0..k-1)
            List<Integer> ordrePoints = new ArrayList<>();
            int mask = fullMask;
            int current = dernierPoint;
            while (current != -1) {
                ordrePoints.add(current);
                int prev = pred[mask][current];
                mask = mask & ~(1 << current);
                current = prev;
            }
            Collections.reverse(ordrePoints);

            // Reconstruire le chemin détaillé dans le graphe (en sommets)
            List<Integer> cheminGlobal = new ArrayList<>();
            int sommetCourant = idDepot;

            if (!ordrePoints.isEmpty()) {
                // Dépôt -> premier carrefour
                int idx = ordrePoints.get(0);
                int idPremier = carrefours[idx];
                List<Integer> segment = reconstruireChemin(
                        resultats[0].getPredecesseur(), idDepot, idPremier);
                cheminGlobal.addAll(segment);
                sommetCourant = idPremier;

                // Carrefours suivants
                for (int t = 1; t < ordrePoints.size(); t++) {
                    int idxSuivant = ordrePoints.get(t);
                    int idSuivant = carrefours[idxSuivant];

                    int indicePointSource = -1;
                    for (int p = 0; p < nbPoints; p++) {
                        if (points[p] == sommetCourant) {
                            indicePointSource = p;
                            break;
                        }
                    }
                    if (indicePointSource == -1) {
                        throw new IllegalStateException("Point source introuvable pour la reconstruction.");
                    }

                    List<Integer> seg = reconstruireChemin(
                            resultats[indicePointSource].getPredecesseur(),
                            sommetCourant, idSuivant);
                    if (!cheminGlobal.isEmpty()) {
                        seg.remove(0); // por pas dupliquer le sommet de départ
                    }
                    cheminGlobal.addAll(seg);
                    sommetCourant = idSuivant;
                }

                // Retour au dépôt
                int indicePointSource = -1;
                for (int p = 0; p < nbPoints; p++) {
                    if (points[p] == sommetCourant) {
                        indicePointSource = p;
                        break;
                    }
                }
                if (indicePointSource == -1) {
                    throw new IllegalStateException("Point source introuvable pour le retour.");
                }

                List<Integer> segmentRetour = reconstruireChemin(
                        resultats[indicePointSource].getPredecesseur(),
                        sommetCourant, idDepot);
                if (!cheminGlobal.isEmpty()) {
                    segmentRetour.remove(0);
                }
                cheminGlobal.addAll(segmentRetour);
            }

            // arêtes réellement parcourues dans le cheminGlobal
            Set<Long> aretesParcourues = new HashSet<>();
            for (int i = 0; i + 1 < cheminGlobal.size(); i++) {
                int u = cheminGlobal.get(i);
                int v = cheminGlobal.get(i + 1);
                int a = Math.min(u, v);
                int b = Math.max(u, v);
                long key = (((long) a) << 32) | (b & 0xffffffffL);
                aretesParcourues.add(key);
            }

            // Calcul du coût boucles locales
            double coutBouclesLocales = 0.0;
            Map<Integer, List<EncombrantPourTournee>> encombrantsParCarrefour = new HashMap<>();

            for (EncombrantPourTournee e : encombrants) {
                int idAssocie = e.getIdCarrefourAssocie();
                int idU = graphe.getIdSommet(e.getNomU());
                int idV = graphe.getIdSommet(e.getNomV());
                int a = Math.min(idU, idV);
                int b = Math.max(idU, idV);
                long key = (((long) a) << 32) | (b & 0xffffffffL);

                if (aretesParcourues.contains(key)) {
                    // L'arête u-v est parcourue : pas besoin de boucle locale
                    continue;
                }

                // Sinon, boucle locale
                coutBouclesLocales += 2.0 * e.getDistanceDepuisCarrefour();
                encombrantsParCarrefour
                        .computeIfAbsent(idAssocie, k2 -> new ArrayList<>())
                        .add(e);
            }

            double distanceTotaleAvecBoucles = meilleurCout + coutBouclesLocales;

            // résultats
            System.out.println("Ordre optimal de visite des carrefours (sans le dépôt) :");
            for (int idxPoint : ordrePoints) {
                System.out.print(graphe.getNomSommet(carrefours[idxPoint]) + " ");
            }
            System.out.println("\n");

            System.out.println("Chemin complet (avec dépôt au début et à la fin) :");
            for (int id : cheminGlobal) {
                System.out.print(graphe.getNomSommet(id));

                List<EncombrantPourTournee> loc = encombrantsParCarrefour.get(id);
                if (loc != null && !loc.isEmpty()) {
                    System.out.print(" (boucle locale sur ");
                    for (int i = 0; i < loc.size(); i++) {
                        EncombrantPourTournee e = loc.get(i);
                        System.out.print(e.getNomU() + "-" + e.getNomV());
                        if (i < loc.size() - 1) {
                            System.out.print(", ");
                        }
                    }
                    System.out.print(")");
                }

                System.out.print(" -> ");
            }
            System.out.println("(fin)");

            System.out.println("\nDistance minimale de la tournée (sans les boucles locales) : "
                    + meilleurCout + " m");
            System.out.println("Surcoût dû aux boucles locales vers les encombrants : "
                    + coutBouclesLocales + " m");
            System.out.println("Distance totale de la tournée (avec boucles locales) : "
                    + distanceTotaleAvecBoucles + " m");
            System.out.println("\n");

        } catch (Exception e) {
            System.err.println("Erreur dans ProgrammeHO1Tournee : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
