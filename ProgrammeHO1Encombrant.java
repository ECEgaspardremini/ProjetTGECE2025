import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProgrammeHO1Encombrant {

    //Reconstruit la liste des sommets (id) entre source et cible,avec tableau des prédecesseurs  Dijkstra

    public static List<Integer> reconstruireChemin(int[] predecesseur, int idSource, int idCible) {
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


     // lecture du fichier, Dijkstra, choix du meilleur chemin vers l’encombrant, distance aller-retour.

    public static void executerCalculAllerRetour(String nomFichier) {

        try {
            InstanceRamassage instance = LecteurDeFichierRamassage.lireInstanceDepuisFichier(nomFichier);

            GrapheRoutier graphe = instance.getGraphe();
            int idDepot = instance.getIdDepot();
            EncombrantSurRue encombrant = instance.getEncombrant();

            // Dijkstra depuis dépôt
            ResultatDuDijkstra resultat = Dijkstra.calculerPlusCourtsChemins(graphe, idDepot);

            double[] distances = resultat.getDistanceDepuisSource();
            int[] predecesseur = resultat.getPredecesseur();

            int u = encombrant.getSommetU();
            int v = encombrant.getSommetV();
            double positionDepuisU = encombrant.getPositionDepuisU();
            double longueurRue = encombrant.getLongueurRue();

            double distanceDepotVersU = distances[u];
            double distanceDepotVersV = distances[v];

            double distanceViaU = distanceDepotVersU + positionDepuisU;
            double distanceViaV = distanceDepotVersV + (longueurRue - positionDepuisU);

            boolean passerParU = distanceViaU <= distanceViaV;

            int sommetApproche = passerParU ? u : v;

// Chemin dépôt → carrefour le plus proche
            List<Integer> cheminDeDepotVersSommetApproche =
                    reconstruireChemin(predecesseur, idDepot, sommetApproche);

// boucle locale HO1

// p = distance depuis U, L - p depuis V
            double distanceDepuisU = positionDepuisU;
            double distanceDepuisV = longueurRue - positionDepuisU;

// aller-retour local jusqu'à l’encombrant
            double petiteBoucleLocale = 2.0 * Math.min(distanceDepuisU, distanceDepuisV);

// distance aller-retour réelle
            double distanceAllerRetour =
                    (passerParU ? distanceDepotVersU : distanceDepotVersV)
                            + petiteBoucleLocale;


            System.out.println("RESULTAT DU RAMASSAGE (HO1)\n");

            System.out.println("Centre de traitement (DEPOT) : " + graphe.getNomSommet(idDepot));
            System.out.println("Encombrant placé sur la rue : " +
                    graphe.getNomSommet(u) + " - " + graphe.getNomSommet(v));
            System.out.println("Position de l'encombrant : " + positionDepuisU +
                    " m depuis " + graphe.getNomSommet(u) + " (longueur totale de la rue : " + longueurRue + " m)");
            System.out.println();

            System.out.println("=> Meilleure approche : via le sommet " +
                    graphe.getNomSommet(sommetApproche));
            System.out.println("Chemin aller : ");
            for (int idSommet : cheminDeDepotVersSommetApproche) {
                System.out.print("   " + graphe.getNomSommet(idSommet) + " -> ");
            }
            System.out.println(graphe.getNomSommet(sommetApproche));
            System.out.println();

// boucle locale réelle HO1
            System.out.println("Boucle locale effectuée pour atteindre l’encombrant : ");
            if (distanceDepuisU < distanceDepuisV) {
                System.out.println("   " + graphe.getNomSommet(u)
                        + " -> ENCOMBRANT -> " + graphe.getNomSommet(u)
                        + "   (aller-retour de " + (2 * distanceDepuisU) + " m)");
            } else {
                System.out.println("   " + graphe.getNomSommet(v)
                        + " -> ENCOMBRANT -> " + graphe.getNomSommet(v)
                        + "   (aller-retour de " + (2 * distanceDepuisV) + " m)");
            }

            System.out.println();
            System.out.println("Distance aller-retour totale (dépôt -> encombrant -> dépôt) : "
                    + distanceAllerRetour + " m");



            System.out.println("\n");

        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
