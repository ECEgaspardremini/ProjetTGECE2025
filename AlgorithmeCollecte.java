
import java.util.ArrayList;

public class AlgorithmeCollecte {

    private Graphe graphe;
    private Sommet depot;
    private ArrayList<Sommet> pointsCollecte;
    private Camion camion;
    private int[][] matriceDistances;

    public AlgorithmeCollecte(Graphe graphe, Sommet depot, ArrayList<Sommet> pointsCollecte, double capaciteCamion) {
        this.graphe = graphe;
        this.depot = depot;
        this.pointsCollecte = pointsCollecte;
        this.camion = new Camion(capaciteCamion, depot);
        this.matriceDistances = graphe.construireMatriceDistances();
    }


    private boolean existePointNonVide() {
        for (Sommet p : pointsCollecte) {
            if (p.getQuantiteDechets() > 0.0) {
                return true;
            }
        }
        return false;
    }

    private int calculerDistancePlusCourte(Sommet a, Sommet b) {
        int indiceA = graphe.getIndiceSommet(a);
        int indiceB = graphe.getIndiceSommet(b);
        int[] dist = graphe.dijkstra(indiceA, matriceDistances);
        return dist[indiceB];
    }


    private Sommet choisirPointLePlusProche(Sommet position) {
        Sommet meilleur = null;
        int meilleureDistance = Integer.MAX_VALUE;

        for (Sommet p : pointsCollecte) {
            if (p.getQuantiteDechets() > 0.0) {
                int d = calculerDistancePlusCourte(position, p);
                if (d < meilleureDistance) {
                    meilleureDistance = d;
                    meilleur = p;
                }
            }
        }

        return meilleur;
    }

    public void executerCollecte() {
        double distanceTotale = 0.0;
        double distanceTournee = 0.0;
        int numeroTournee = 0;

        while (existePointNonVide()) {


            if (camion.getPositionActuelle() == depot && camion.getChargeActuelle() == 0.0) {
                numeroTournee++;
                distanceTournee = 0.0;
                System.out.println();
                System.out.println("========================================");
                System.out.println("--- DÉBUT DE LA TOURNÉE " + numeroTournee + " --");
                System.out.println("Position de départ : " + depot.getId() +
                        ",  Charge camion : " + camion.getChargeActuelle() + " / " + camion.getCapaciteMax() + " t");
                System.out.println("========================================");
            }


            Sommet prochainPoint = choisirPointLePlusProche(camion.getPositionActuelle());
            if (prochainPoint == null) {
                break;
            }


            Chemin cheminAller = graphe.calculerPlusCourtChemin(
                    camion.getPositionActuelle(),
                    prochainPoint,
                    matriceDistances
            );
            int distance = cheminAller.getDistanceTotale();
            distanceTotale += distance;
            distanceTournee += distance;
            camion.setPositionActuelle(prochainPoint);

            System.out.println();
            System.out.println("  - Déplacement vers " + prochainPoint.getId() +
                    " (" + distance + " m)");
            System.out.println("    Chemin emprunté : " +
                    cheminAller.toStringDetaille(graphe, matriceDistances));
            System.out.println("    Distance tournée = " + distanceTournee +
                    " m,  Distance totale = " + distanceTotale + " m");

            // Gestion prise des déchets
            double quantitePoint = prochainPoint.getQuantiteDechets();
            double placeDisponible = camion.getPlaceDisponible();

            if (quantitePoint <= placeDisponible + 1e-9) {
                // On peut tout prendre
                camion.setChargeActuelle(camion.getChargeActuelle() + quantitePoint);
                prochainPoint.setQuantiteDechets(0.0);
                System.out.println("    Action : point " + prochainPoint.getId() + " VIDÉ (+" +
                        quantitePoint + " t) → charge camion = " + camion.getChargeActuelle() + " t");
            } else {
                // On ne peut prendre qu'une partie
                double pris = placeDisponible;
                camion.setChargeActuelle(camion.getCapaciteMax());
                prochainPoint.setQuantiteDechets(quantitePoint - pris);
                System.out.println("    Action : point " + prochainPoint.getId() + " PARTIELLEMENT VIDÉ (+" +
                        pris + " t), reste " + prochainPoint.getQuantiteDechets() +
                        " t sur le point → camion PLEIN (" + camion.getChargeActuelle() + " t)");
            }

            // si camion  plein et qu'il reste des points à vider = retour dépôt
            if (camion.estPlein() && existePointNonVide()) {

                Chemin cheminRetour = graphe.calculerPlusCourtChemin(
                        camion.getPositionActuelle(),
                        depot,
                        matriceDistances
                );
                int distRetour = cheminRetour.getDistanceTotale();
                distanceTotale += distRetour;
                distanceTournee += distRetour;
                camion.setPositionActuelle(depot);
                camion.viderAuDepot();

                System.out.println();
                System.out.println("  → Camion PLEIN : retour au dépôt");
                System.out.println("    Chemin retour : " +
                        cheminRetour.toStringDetaille(graphe, matriceDistances));
                System.out.println("    Distance tournée = " + distanceTournee +
                        " m,  Distance totale = " + distanceTotale + " m");
                System.out.println("    Action : vidage du camion au dépôt.");

                System.out.println("--- FIN DE LA TOURNÉE " + numeroTournee +
                        " (distance = " + distanceTournee + " m) ---");
            }

            // tout les points vide = retour depot
            if (!existePointNonVide()) {
                if (camion.getPositionActuelle() != depot) {

                    Chemin cheminFinal = graphe.calculerPlusCourtChemin(
                            camion.getPositionActuelle(),
                            depot,
                            matriceDistances
                    );
                    int distFinal = cheminFinal.getDistanceTotale();
                    distanceTotale += distFinal;
                    distanceTournee += distFinal;
                    camion.setPositionActuelle(depot);
                    camion.viderAuDepot();

                    System.out.println();
                    System.out.println("  → Tous les points sont vidés : retour final au dépôt");
                    System.out.println("    Chemin retour : " +
                            cheminFinal.toStringDetaille(graphe, matriceDistances));
                    System.out.println("    Distance tournée = " + distanceTournee +
                            " m,  Distance totale = " + distanceTotale + " m");
                    System.out.println("--- FIN DE LA TOURNÉE " + numeroTournee +
                            " (distance = " + distanceTournee + " m) ---");
                }
                break;
            }
        }

        System.out.println();
        System.out.println("========================================");
        System.out.println("---- COLLECTE TERMINÉE (APPROCHE 1 REALISTE HO1) ----");
        System.out.println("Distance totale parcourue : " + distanceTotale + " m");
        System.out.println("Nombre de tournées        : " + numeroTournee);
        System.out.println();
        System.out.println("Quantités finales sur les points de collecte :");
        for (Sommet p : pointsCollecte) {
            System.out.println(" - " + p.getId() + " : " + p.getQuantiteDechets() + " t");
        }
        System.out.println("========================================");
    }
}
