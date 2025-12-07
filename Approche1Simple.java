import java.util.ArrayList;

public class Approche1Simple {

    private Graphe graphe;
    private Sommet depot;
    private ArrayList<Sommet> pointsCollecte;
    private int[][] matriceDistances;

    public Approche1Simple(Graphe graphe, Sommet depot, ArrayList<Sommet> pointsCollecte) {
        this.graphe = graphe;
        this.depot = depot;
        this.pointsCollecte = pointsCollecte;
        this.matriceDistances = graphe.construireMatriceDistances();
    }

    //  distance du plus court chemin entre deux sommets avec dijkstra
    private int distance(Sommet a, Sommet b) {
        int ia = graphe.getIndiceSommet(a);
        int ib = graphe.getIndiceSommet(b);
        int[] dist = graphe.dijkstra(ia, matriceDistances);
        return dist[ib];
    }

    // distance totale d'un objet Chemin en sommant les arcs du chemin
    private int distanceChemin(Chemin chemin) {
        int total = 0;
        ArrayList<Sommet> liste = chemin.getSommets();
        for (int i = 0; i < liste.size() - 1; i++) {
            int ia = graphe.getIndiceSommet(liste.get(i));
            int ib = graphe.getIndiceSommet(liste.get(i + 1));
            total += matriceDistances[ia][ib];
        }
        return total;
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
        ArrayList<Sommet> ordrePoints = new ArrayList<>();
        ordrePoints.add(depot);

        Sommet position = depot;
        int distanceTotale = 0;

        ArrayList<Sommet> cheminGlobal = new ArrayList<>();
        cheminGlobal.add(depot); // on part du dépôt

        System.out.println("--- APPROCHE 1 : PLUS PROCHE VOISIN (version simple) ---\n");

        int numTrajet = 1;


        while (!nonVisites.isEmpty()) {

            Sommet prochain = choisirPlusProche(position, nonVisites);

            // plus court chemin détaillé entre position et prochain
            Chemin chemin = graphe.calculerPlusCourtChemin(position, prochain, matriceDistances);
            int d = distanceChemin(chemin);

            // Afichage détaillé du trajet
            System.out.print("Trajet " + numTrajet + " : "
                    + position.getId() + " -> " + prochain.getId()
                    + " (" + d + " m), chemin : ");
            afficherChemin(chemin.getSommets());
            System.out.println();

            distanceTotale += d;
            numTrajet++;


            position = prochain;
            ordrePoints.add(position);
            nonVisites.remove(position);

            //  ajout au chemin global sans répéter le sommet de départ
            ArrayList<Sommet> listeChemin = chemin.getSommets();
            for (int i = 1; i < listeChemin.size(); i++) {
                cheminGlobal.add(listeChemin.get(i));
            }
        }

        // retour final au dépôt
        Chemin cheminRetour = graphe.calculerPlusCourtChemin(position, depot, matriceDistances);
        int dRetour = distanceChemin(cheminRetour);
        System.out.print("\nTrajet retour : "
                + position.getId() + " -> " + depot.getId()
                + " (" + dRetour + " m), chemin : ");
        afficherChemin(cheminRetour.getSommets());
        System.out.println();

        distanceTotale += dRetour;
        ordrePoints.add(depot);

        // ajout du retour au chemin global
        ArrayList<Sommet> listeRetour = cheminRetour.getSommets();
        for (int i = 1; i < listeRetour.size(); i++) {
            cheminGlobal.add(listeRetour.get(i));
        }

        // Résumé
        System.out.println("\nOrdre de visite (points de collecte) :");
        for (Sommet s : ordrePoints) {
            System.out.print(s.getId() + " ");
        }

        System.out.println("\nDistance totale = " + distanceTotale + " m");

        System.out.print("Chemin global sur le graphe (avec intersections) : ");
        afficherChemin(cheminGlobal);
        System.out.println();

        System.out.println("------\n");
    }


    private void afficherChemin(ArrayList<Sommet> liste) {
        for (int i = 0; i < liste.size(); i++) {
            System.out.print(liste.get(i).getId());
            if (i < liste.size() - 1) {
                System.out.print(" -> ");
            }
        }
    }
}
