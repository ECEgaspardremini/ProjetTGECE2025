import java.util.ArrayList;

public class Approche1HO2HO3Simple {

    private GraphesHO2HO3 graphe;
    private Sommet depot;
    private ArrayList<Sommet> pointsCollecte;
    private int[][] matriceDistances;

    public Approche1HO2HO3Simple(GraphesHO2HO3 graphe, Sommet depot, ArrayList<Sommet> pointsCollecte) {
        this.graphe = graphe;
        this.depot = depot;
        this.pointsCollecte = pointsCollecte;
        this.matriceDistances = graphe.construireMatriceDistances();
    }

    // distance plus court chemin entre a et b
    private int distance(Sommet a, Sommet b) {
        int ia = graphe.getIndiceSommet(a);
        int ib = graphe.getIndiceSommet(b);
        int[] dist = graphe.dijkstra(ia, matriceDistances);
        return dist[ib];
    }


    private Sommet choisirPlusProche(Sommet position, ArrayList<Sommet> nonVisites) {
        Sommet meilleur = null;
        int meilleureDist = Integer.MAX_VALUE;

        for (Sommet p : nonVisites) {
            int d = distance(position, p);
            if (d < meilleureDist) {
                meilleureDist = d;
                meilleur = p;
            }
        }
        return meilleur;
    }

    public void executerApproche1() {

        ArrayList<Sommet> nonVisites = new ArrayList<>(pointsCollecte);
        ArrayList<Sommet> ordre = new ArrayList<>();
        ordre.add(depot);

        Sommet position = depot;
        int distanceTotale = 0;

        // chemin détaillé avec intersections
        ArrayList<Sommet> cheminGlobal = new ArrayList<>();
        cheminGlobal.add(depot);

        System.out.println("--- APPROCHE 1 (HO2/HO3) : plus proche voisin (version simple) ---");

        while (!nonVisites.isEmpty()) {

            Sommet prochain = choisirPlusProche(position, nonVisites);
            if (prochain == null) {
                System.out.println("ATTENTION : plus aucun point atteignable depuis " + position.getId());
                break;
            }

            int d = distance(position, prochain);
            System.out.println("De " + position.getId() + " → " + prochain.getId() + " (" + d + " m)");

            distanceTotale += d;

            // on récupère le chemin complet entre position et prochain
            Chemin chemin = graphe.calculerPlusCourtChemin(position, prochain, matriceDistances);
            for (int i = 1; i < chemin.getSommets().size(); i++) {
                cheminGlobal.add(chemin.getSommets().get(i));
            }

            position = prochain;
            ordre.add(position);
            nonVisites.remove(position);
        }

        // retour au dépôt
        int dRetour = distance(position, depot);
        System.out.println("Retour final " + position.getId() + " -> " + depot.getId() + " (" + dRetour + " m)");
        distanceTotale += dRetour;
        ordre.add(depot);

        Chemin cheminRetour = graphe.calculerPlusCourtChemin(position, depot, matriceDistances);
        for (int i = 1; i < cheminRetour.getSommets().size(); i++) {
            cheminGlobal.add(cheminRetour.getSommets().get(i));
        }

        System.out.println("\nOrdre de visite (dépôt + points de collecte) :");
        for (Sommet s : ordre) {
            System.out.print(s.getId() + " ");
        }
        System.out.println("\nDistance totale = " + distanceTotale + " m");

        System.out.print("Détail du chemin global : ");
        for (int i = 0; i < cheminGlobal.size(); i++) {
            System.out.print(cheminGlobal.get(i).getId());
            if (i < cheminGlobal.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
        System.out.println("------\n");
    }
}
