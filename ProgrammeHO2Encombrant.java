import java.util.List;

public class ProgrammeHO2Encombrant {

    public static void executerCalculAllerRetourHO2(String nomFichier) {
        try {
            InstanceRamassage instance =
                    LecteurDeFichierRamassageHO2.lireInstanceDepuisFichierHO2(nomFichier);

            GrapheRoutier graphe = instance.getGraphe();
            int idDepot = instance.getIdDepot();
            EncombrantSurRue encombrant = instance.getEncombrant();

            int u = encombrant.getSommetU(); // arc u -> v
            int v = encombrant.getSommetV();
            double positionDepuisU = encombrant.getPositionDepuisU();
            double longueurRue = encombrant.getLongueurRue();

            // Aller : DEPOT -> ... -> u -> point (sur l'arc u -> v)
            ResultatDuDijkstra resDepuisDepot = Dijkstra.calculerPlusCourtsChemins(graphe, idDepot);
            double[] distDepot = resDepuisDepot.getDistanceDepuisSource();

            if (Double.isInfinite(distDepot[u])) {
                System.out.println("Impossible d'atteindre l'encombrant (sommet u inaccessible).");
                return;
            }

            double distanceAller = distDepot[u] + positionDepuisU;

            List<Integer> cheminDepotVersU =
                    ProgrammeHO1Encombrant.reconstruireChemin(
                            resDepuisDepot.getPredecesseur(), idDepot, u);

            // Retour : point -> v -> ... -> DEPOT
            ResultatDuDijkstra resDepuisV = Dijkstra.calculerPlusCourtsChemins(graphe, v);
            double[] distDepuisV = resDepuisV.getDistanceDepuisSource();

            if (Double.isInfinite(distDepuisV[idDepot])) {
                System.out.println("Impossible de revenir au dépôt depuis la fin de la rue (sommet v).");
                return;
            }

            double distanceRetour = (longueurRue - positionDepuisU) + distDepuisV[idDepot];

            List<Integer> cheminVVersDepot =
                    ProgrammeHO1Encombrant.reconstruireChemin(
                            resDepuisV.getPredecesseur(), v, idDepot);

            double distanceTotale = distanceAller + distanceRetour;

            // -------- AFFICHAGE --------
            System.out.println(" HO2 - Collecte d'un encombrant (rues orientées) \n");
            System.out.println("Dépôt : " + graphe.getNomSommet(idDepot));
            System.out.println("Encombrant sur l'arc orienté : " +
                    graphe.getNomSommet(u) + " -> " + graphe.getNomSommet(v));
            System.out.println("Position de l'encombrant : " + positionDepuisU +
                    " m depuis " + graphe.getNomSommet(u) + " (longueur de l'arc : " + longueurRue + " m)\n");

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

        } catch (Exception e) {
            System.err.println("Erreur dans ProgrammeHO2Encombrant : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
