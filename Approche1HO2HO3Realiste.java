import java.util.ArrayList;

public class Approche1HO2HO3Realiste {

    private GraphesHO2HO3 graphe;
    private Sommet depot;
    private ArrayList<Sommet> pointsCollecte;
    private Camion camion;
    private int[][] matriceDistances;

    public Approche1HO2HO3Realiste(GraphesHO2HO3 graphe, Sommet depot,
                                   ArrayList<Sommet> pointsCollecte, double capaciteCamion) {
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
        int ia = graphe.getIndiceSommet(a);
        int ib = graphe.getIndiceSommet(b);
        int[] dist = graphe.dijkstra(ia, matriceDistances);
        return dist[ib];
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


    private String cheminToStringDetaille(Chemin chemin) {
        StringBuilder sb = new StringBuilder();
        int totalLocal = 0;
        for (int i = 0; i < chemin.getSommets().size() - 1; i++) {
            Sommet a = chemin.getSommets().get(i);
            Sommet b = chemin.getSommets().get(i + 1);
            int d = graphe.distanceDirecteEntre(a, b);
            totalLocal += d;
            sb.append(a.getId()).append(" -> ").append(b.getId())
                    .append(" (").append(d).append(" m)");
            if (i < chemin.getSommets().size() - 2) {
                sb.append(", ");
            }
        }
        sb.append(" [total ").append(totalLocal).append(" m]");
        return sb.toString();
    }

    public void executerCollecte() {
        double distanceTotale = 0.0;
        int numeroTournee = 0;

        System.out.println("===== APPROCHE 1 REALISTE (HO2/HO3) =====");


        while (existePointNonVide()) {


            if (camion.getPositionActuelle() == depot && camion.getChargeActuelle() == 0.0) {
                numeroTournee++;
                System.out.println();
                System.out.println("========================================");
                System.out.println("--- DÉBUT DE LA TOURNÉE " + numeroTournee + " --");
                System.out.println("Position de départ : " + depot.getId() +
                        ",  Charge camion : " + camion.getChargeActuelle() +
                        " / " + camion.getCapaciteMax() + " t");
                System.out.println("========================================");
                System.out.println();
            }

            double distanceTournee = 0.0;

            Sommet prochainPoint = choisirPointLePlusProche(camion.getPositionActuelle());
            if (prochainPoint == null) {
                System.out.println("Plus aucun point atteignable, arrêt.");
                break;
            }


            Chemin cheminAller = graphe.calculerPlusCourtChemin(
                    camion.getPositionActuelle(), prochainPoint, matriceDistances);
            int dAller = cheminAller.getDistanceTotale();
            distanceTournee += dAller;
            distanceTotale += dAller;
            camion.setPositionActuelle(prochainPoint);

            System.out.println("  - Déplacement vers " + prochainPoint.getId() +
                    " (" + dAller + " m)");
            System.out.println("    Chemin emprunté : " + cheminToStringDetaille(cheminAller));
            System.out.println("    Distance tournée = " + distanceTournee +
                    " m,  Distance totale = " + distanceTotale + " m");


            double quantitePoint = prochainPoint.getQuantiteDechets();
            double placeDisponible = camion.getPlaceDisponible();

            if (quantitePoint <= placeDisponible + 1e-9) {

                camion.setChargeActuelle(camion.getChargeActuelle() + quantitePoint);
                prochainPoint.setQuantiteDechets(0.0);
                System.out.println("    Action : point " + prochainPoint.getId() + " VIDÉ (+" +
                        quantitePoint + " t) → charge camion = " +
                        camion.getChargeActuelle() + " t");
            } else {

                double pris = placeDisponible;
                camion.setChargeActuelle(camion.getCapaciteMax());
                prochainPoint.setQuantiteDechets(quantitePoint - pris);
                System.out.println("    Action : point " + prochainPoint.getId() +
                        " PARTIELLEMENT VIDÉ (+" + pris + " t), reste " +
                        prochainPoint.getQuantiteDechets() +
                        " t sur le point → camion PLEIN (" +
                        camion.getChargeActuelle() + " t)");
            }


            if (camion.estPlein() && existePointNonVide()) {
                Chemin cheminRetour = graphe.calculerPlusCourtChemin(
                        camion.getPositionActuelle(), depot, matriceDistances);
                int dRetour = cheminRetour.getDistanceTotale();
                distanceTournee += dRetour;
                distanceTotale += dRetour;
                camion.setPositionActuelle(depot);
                camion.viderAuDepot();

                System.out.println();
                System.out.println("  → Camion PLEIN : retour au dépôt");
                System.out.println("    Chemin retour : " + cheminToStringDetaille(cheminRetour));
                System.out.println("    Distance tournée = " + distanceTournee +
                        " m,  Distance totale = " + distanceTotale + " m");
                System.out.println("    Action : vidage du camion au dépôt.");
                System.out.println("--- FIN DE LA TOURNÉE " + numeroTournee +
                        " (distance = " + distanceTournee + " m) ---");

                continue;
            }


            if (!existePointNonVide()) {
                if (camion.getPositionActuelle() != depot) {
                    Chemin cheminRetourFinal = graphe.calculerPlusCourtChemin(
                            camion.getPositionActuelle(), depot, matriceDistances);
                    int dRetourFinal = cheminRetourFinal.getDistanceTotale();
                    distanceTournee += dRetourFinal;
                    distanceTotale += dRetourFinal;
                    camion.setPositionActuelle(depot);
                    camion.viderAuDepot();

                    System.out.println();
                    System.out.println("  → Tous les points sont vidés : retour final au dépôt");
                    System.out.println("    Chemin retour : " + cheminToStringDetaille(cheminRetourFinal));
                    System.out.println("    Distance tournée = " + distanceTournee +
                            " m,  Distance totale = " + distanceTotale + " m");
                }
                System.out.println("--- FIN DE LA TOURNÉE " + numeroTournee +
                        " (distance = " + distanceTournee + " m) ---");
                break;
            }


        }

        System.out.println();
        System.out.println("========================================");
        System.out.println("---- COLLECTE TERMINÉE (APPROCHE 1 REALISTE HO2/HO3) ----");
        System.out.println("Distance totale parcourue : " + distanceTotale + " m");
        System.out.println("Nombre de tournées        : " + numeroTournee);
        System.out.println();
        System.out.println("Quantités finales sur les points de collecte :");
        for (Sommet p : pointsCollecte) {
            System.out.println(" - " + p.getId() + " : " + p.getQuantiteDechets() + " t");
        }
        System.out.println("========================================\n");
    }
}
