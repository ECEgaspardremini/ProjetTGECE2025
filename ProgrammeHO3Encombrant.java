import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProgrammeHO3Encombrant {

    // Reconstruit le chemin de idSource à idCible à partir du tableau des prédecesseurs
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

    public static void executerCalculAllerRetourHO3(String nomFichier) {

        try {
            InstanceRamassage instance =
                    LecteurDeFichierRamassageHO3.lireInstanceDepuisFichierHO3(nomFichier);

            GrapheRoutier graphe = instance.getGraphe();
            int idDepot = instance.getIdDepot();
            EncombrantSurRue encombrant = instance.getEncombrant();

            int u = encombrant.getSommetU();
            int v = encombrant.getSommetV();
            double positionDepuisU = encombrant.getPositionDepuisU();
            double longueurRue = encombrant.getLongueurRue();

            boolean estDoubleSensUneVoie = encombrant.isSurAreteDoubleSensUneVoie();

            // CAS "VOIE ORIENTÉE / DOUBLE-VOIE"

            if (!estDoubleSensUneVoie) {

                // Aller : DEPOT -> ... -> u -> point encombrant
                ResultatDuDijkstra resDepuisDepot = Dijkstra.calculerPlusCourtsChemins(graphe, idDepot);
                double[] distDepuisDepot = resDepuisDepot.getDistanceDepuisSource();

                if (Double.isInfinite(distDepuisDepot[u])) {
                    System.out.println("Impossible d'atteindre l'encombrant (sommet u inaccessible).");
                    return;
                }

                double distanceAller = distDepuisDepot[u] + positionDepuisU;
                List<Integer> cheminDepotVersU =
                        reconstruireChemin(resDepuisDepot.getPredecesseur(), idDepot, u);

                // Retour : point -> v -> ... -> DEPOT
                ResultatDuDijkstra resDepuisV = Dijkstra.calculerPlusCourtsChemins(graphe, v);
                double[] distDepuisV = resDepuisV.getDistanceDepuisSource();

                if (Double.isInfinite(distDepuisV[idDepot])) {
                    System.out.println("Impossible de revenir au dépôt depuis le sommet v.");
                    return;
                }

                double distanceRetour = (longueurRue - positionDepuisU) + distDepuisV[idDepot];
                List<Integer> cheminVVersDepot =
                        reconstruireChemin(resDepuisV.getPredecesseur(), v, idDepot);

                double distanceTotale = distanceAller + distanceRetour;

                System.out.println("===== HO3 - Encombrant sur une voie orientée / double-voie =====\n");
                System.out.println("Dépôt : " + graphe.getNomSommet(idDepot));
                System.out.println("Encombrant sur l’arc : "
                        + graphe.getNomSommet(u) + " -> " + graphe.getNomSommet(v));
                System.out.println("Position : " + positionDepuisU + " m depuis "
                        + graphe.getNomSommet(u) + " (longueur de la rue : " + longueurRue + " m)\n");

                System.out.print("Chemin aller (DEPOT -> ... -> u -> point) : ");
                for (int id : cheminDepotVersU) {
                    System.out.print(graphe.getNomSommet(id) + " -> ");
                }
                System.out.println("POINT_ENCOMBRANT");

                System.out.print("Chemin retour (point -> v -> ... -> DEPOT) : POINT_ENCOMBRANT -> ");
                for (int id : cheminVVersDepot) {
                    System.out.print(graphe.getNomSommet(id) + " -> ");
                }
                System.out.println("(arrivée dépôt)");

                System.out.println();
                System.out.println("Distance aller : " + distanceAller + " m");
                System.out.println("Distance retour : " + distanceRetour + " m");
                System.out.println("Distance totale aller-retour : " + distanceTotale + " m");
                System.out.println("\n");

            }

            // CAS "RUE DOUBLE-SENS UNE SEULE VOIE" (HO1 interne dans HO3)

            else {

                // Dijkstra depuis le dépôt
                ResultatDuDijkstra resDepuisDepot = Dijkstra.calculerPlusCourtsChemins(graphe, idDepot);
                double[] distDepot = resDepuisDepot.getDistanceDepuisSource();

                if (Double.isInfinite(distDepot[u]) && Double.isInfinite(distDepot[v])) {
                    System.out.println("Impossible d'atteindre la rue de l'encombrant depuis le dépôt.");
                    return;
                }

                // Dijkstra depuis u et depuis v (pour revenir au dépôt)
                ResultatDuDijkstra resDepuisU = Dijkstra.calculerPlusCourtsChemins(graphe, u);
                double[] distDepuisU = resDepuisU.getDistanceDepuisSource();

                ResultatDuDijkstra resDepuisV = Dijkstra.calculerPlusCourtsChemins(graphe, v);
                double[] distDepuisV = resDepuisV.getDistanceDepuisSource();

                if (Double.isInfinite(distDepuisU[idDepot]) && Double.isInfinite(distDepuisV[idDepot])) {
                    System.out.println("Impossible de revenir au dépôt depuis la rue de l'encombrant.");
                    return;
                }

                // Distances dépôt <-> extrémités
                double distDepotU = distDepot[u];
                double distDepotV = distDepot[v];
                double distUDepot = distDepuisU[idDepot];
                double distVDepot = distDepuisV[idDepot];

                // Distances sur la rue entre extrémités et point encombrant
                double p = positionDepuisU;                 // distance A -> ENCOMBRANT
                double q = longueurRue - positionDepuisU;   // distance B -> ENCOMBRANT

                // test 4 scénarios :
                // 1) entrée A, sortie A (boucle locale)
                // 2) entrée B, sortie B (boucle locale)
                // 3) entrée A, sortie B (on traverse toute la rue)
                // 4) entrée B, sortie A (on traverse toute la rue)

                double meilleurTotal = Double.POSITIVE_INFINITY;
                double meilleurAller = 0.0;
                double meilleurRetour = 0.0;
                double distanceRueAller = 0.0;
                double distanceRueRetour = 0.0;
                int entree = -1; // 0 = u, 1 = v
                int sortie = -1; // 0 = u, 1 = v

                //  1) Entrée A, sortie A
                if (!Double.isInfinite(distDepotU) && !Double.isInfinite(distUDepot)) {
                    double aller = distDepotU + p;   // dépôt -> ... -> A -> ENCOMBRANT
                    double retour = p + distUDepot;  // ENCOMBRANT -> A -> ... -> dépôt
                    double total = aller + retour;

                    if (total < meilleurTotal) {
                        meilleurTotal = total;
                        meilleurAller = aller;
                        meilleurRetour = retour;
                        distanceRueAller = p;
                        distanceRueRetour = p;
                        entree = 0;
                        sortie = 0;
                    }
                }

                //  2) Entrée B, sortie B
                if (!Double.isInfinite(distDepotV) && !Double.isInfinite(distVDepot)) {
                    double aller = distDepotV + q;
                    double retour = q + distVDepot;
                    double total = aller + retour;

                    if (total < meilleurTotal) {
                        meilleurTotal = total;
                        meilleurAller = aller;
                        meilleurRetour = retour;
                        distanceRueAller = q;
                        distanceRueRetour = q;
                        entree = 1;
                        sortie = 1;
                    }
                }

                //  3) Entrée A, sortie B (on traverse toute la rue)
                if (!Double.isInfinite(distDepotU) && !Double.isInfinite(distVDepot)) {
                    double aller = distDepotU + p;      // dépôt -> ... -> A -> ENCOMBRANT
                    double retour = q + distVDepot;     // ENCOMBRANT -> B -> ... -> dépôt
                    double total = aller + retour;

                    if (total < meilleurTotal) {
                        meilleurTotal = total;
                        meilleurAller = aller;
                        meilleurRetour = retour;
                        distanceRueAller = p;
                        distanceRueRetour = q;
                        entree = 0;
                        sortie = 1;
                    }
                }

                //  4) Entrée B, sortie A (on traverse toute la rue)
                if (!Double.isInfinite(distDepotV) && !Double.isInfinite(distUDepot)) {
                    double aller = distDepotV + q;
                    double retour = p + distUDepot;
                    double total = aller + retour;

                    if (total < meilleurTotal) {
                        meilleurTotal = total;
                        meilleurAller = aller;
                        meilleurRetour = retour;
                        distanceRueAller = q;
                        distanceRueRetour = p;
                        entree = 1;
                        sortie = 0;
                    }
                }

                if (entree == -1 || sortie == -1 || Double.isInfinite(meilleurTotal)) {
                    System.out.println("Aucun scénario valide pour rejoindre et quitter la rue.");
                    return;
                }

                int sommetEntree = (entree == 0) ? u : v;
                int sommetSortie = (sortie == 0) ? u : v;

                // Chemin du dépôt vers l'entrée
                List<Integer> cheminDepotVersEntree =
                        reconstruireChemin(resDepuisDepot.getPredecesseur(), idDepot, sommetEntree);

                // Chemin de la sortie vers le dépôt
                ResultatDuDijkstra resRetour = (sortie == 0) ? resDepuisU : resDepuisV;
                List<Integer> cheminSortieVersDepot =
                        reconstruireChemin(resRetour.getPredecesseur(), sommetSortie, idDepot);

                System.out.println("===== HO3 - Encombrant sur une rue double-sens une seule voie =====\n");
                System.out.println("Dépôt : " + graphe.getNomSommet(idDepot));
                System.out.println("Rue simple double-sens entre : "
                        + graphe.getNomSommet(u) + " et " + graphe.getNomSommet(v));
                System.out.println("Position : " + positionDepuisU + " m depuis "
                        + graphe.getNomSommet(u) + " (longueur totale : " + longueurRue + " m)\n");

                System.out.println("Itinéraire choisi : entrée par "
                        + graphe.getNomSommet(sommetEntree)
                        + ", sortie par " + graphe.getNomSommet(sommetSortie));

                // 1) Chemin jusqu'à l'entrée
                System.out.print("Chemin du dépôt jusqu'à l'entrée de la rue : ");
                for (int id : cheminDepotVersEntree) {
                    System.out.print(graphe.getNomSommet(id) + " -> ");
                }
                System.out.println(graphe.getNomSommet(sommetEntree));

                // 2) Description du tronçon sur la rue
                System.out.println("Parcours sur la rue : ");
                if (sommetEntree == u && sommetSortie == u) {
                    System.out.println("   " + graphe.getNomSommet(u)
                            + " -> ENCOMBRANT -> " + graphe.getNomSommet(u)
                            + "  (boucle locale de " + (distanceRueAller + distanceRueRetour) + " m)");
                } else if (sommetEntree == v && sommetSortie == v) {
                    System.out.println("   " + graphe.getNomSommet(v)
                            + " -> ENCOMBRANT -> " + graphe.getNomSommet(v)
                            + "  (boucle locale de " + (distanceRueAller + distanceRueRetour) + " m)");
                } else if (sommetEntree == u && sommetSortie == v) {
                    System.out.println("   " + graphe.getNomSommet(u)
                            + " -> ENCOMBRANT -> " + graphe.getNomSommet(v)
                            + "  (traversée complète de la rue, "
                            + (distanceRueAller + distanceRueRetour) + " m)");
                } else { // entrée v, sortie u
                    System.out.println("   " + graphe.getNomSommet(v)
                            + " -> ENCOMBRANT -> " + graphe.getNomSommet(u)
                            + "  (traversée complète de la rue, "
                            + (distanceRueAller + distanceRueRetour) + " m)");
                }

                // 3) Chemin de la sortie vers le dépôt
                System.out.print("Chemin de la sortie de la rue jusqu'au dépôt : "
                        + graphe.getNomSommet(sommetSortie) + " -> ");
                for (int id : cheminSortieVersDepot) {
                    System.out.print(graphe.getNomSommet(id) + " -> ");
                }
                System.out.println("(arrivée dépôt)");

                System.out.println();
                System.out.println("Distance aller (jusqu'au point encombrant) : " + meilleurAller + " m");
                System.out.println("Distance retour (depuis le point encombrant) : " + meilleurRetour + " m");
                System.out.println("Distance sur la rue (aller + retour) : "
                        + (distanceRueAller + distanceRueRetour) + " m");
                System.out.println("Distance totale aller-retour : " + meilleurTotal + " m");
                System.out.println("\n");
            }

        } catch (Exception e) {
            System.err.println("Erreur dans ProgrammeHO3Encombrant : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
