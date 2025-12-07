
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("=== PROJET GRAPHES — THEME 2 ===");
        System.out.println("1 - Approche 1 HO1 SIMPLE (plus proche voisin, sans capacité)");
        System.out.println("2 - Approche 1 HO1 REALISTE (camion 10 t, retours, fractionnement)");
        System.out.println("3 - Approche 2 HO1 SIMPLE (MST + DFS + shortcutting, sans capacité)");
        System.out.println("4 - Approche 2 HO1 REALISTE (MST + capacités, découpage en tournées)");
        System.out.println("5 - Approche 1 HO2/HO3 SIMPLE (graphe orienté, sans capacité)");
        System.out.println("6 - Approche 1 HO2/HO3 REALISTE (graphe orienté, avec capacité)");
        System.out.print("Votre choix : ");

        int choix = sc.nextInt();
        System.out.println();

        String fichierGrapheVille = "graphe_points.txt";         // HO1, ville réelle
        String fichierGrapheComplet = "graphe_complet_mst.txt";  // HO1, graphe complet pour MST
        String fichierGrapheHO2HO3 = "graphe_ho2_ho3.txt";       // HO2/HO3, graphe orienté

        try {
            // graphe HO1 approche1
            Graphe grapheVille = Graphe.chargerDepuisFichier(fichierGrapheVille);
            Sommet depotVille = grapheVille.trouverDepot();
            ArrayList<Sommet> pointsVille = grapheVille.trouverPointsCollecte();

            if (depotVille == null || pointsVille.isEmpty()) {
                System.out.println("Erreur : graphe réel mal défini (pas de dépôt ou pas de points de collecte).");
                return;
            }

            // graphe complet HO1 approche 2
            Graphe grapheComplet = null;
            Sommet depotComplet = null;
            ArrayList<Sommet> pointsComplet = null;
            if (choix == 3 || choix == 4) {
                grapheComplet = Graphe.chargerDepuisFichier(fichierGrapheComplet);
                depotComplet = grapheComplet.trouverDepot();
                pointsComplet = grapheComplet.trouverPointsCollecte();

                if (depotComplet == null || pointsComplet.isEmpty()) {
                    System.out.println("Erreur : graphe complet MST mal défini.");
                    return;
                }
            }

            // graphe HO2/HO3 approche 1
            GraphesHO2HO3 grapheHO2HO3 = null;
            Sommet depotHO2HO3 = null;
            ArrayList<Sommet> pointsHO2HO3 = null;
            if (choix == 5 || choix == 6) {
                grapheHO2HO3 = GraphesHO2HO3.chargerDepuisFichier(fichierGrapheHO2HO3);
                depotHO2HO3 = grapheHO2HO3.trouverDepot();
                pointsHO2HO3 = grapheHO2HO3.trouverPointsCollecte();

                if (depotHO2HO3 == null || pointsHO2HO3.isEmpty()) {
                    System.out.println("Erreur : graphe HO2/HO3 mal défini (pas de dépôt ou pas de points de collecte).");
                    return;
                }
            }

            // approche 1 HO1 simple
            if (choix == 1) {
                System.out.println("=== APPROCHE 1 SIMPLE (HO1) ===\n");
                Approche1Simple a1 = new Approche1Simple(grapheVille, depotVille, pointsVille);
                a1.executerApproche1();
            }

            // approche 1 HO1 realiste
            else if (choix == 2) {
                System.out.println("=== APPROCHE 1 REALISTE (HO1) ===\n");

                Random random = new Random();
                System.out.println("Quantités initiales de déchets :");
                for (Sommet p : pointsVille) {
                    int q = random.nextInt(8); // 0..7
                    p.setQuantiteDechets(q);
                    System.out.println(" - " + p.getId() + " : " + q + " t");
                }
                System.out.println();

                double capaciteCamion = 10.0;
                AlgorithmeCollecte algo = new AlgorithmeCollecte(grapheVille, depotVille, pointsVille, capaciteCamion);
                algo.executerCollecte();
            }

            // approche 2 HO1 simple
            else if (choix == 3) {
                System.out.println("=== APPROCHE 2 SIMPLE (HO1, MST) ===\n");
                Approche2MSTSimple a2 = new Approche2MSTSimple(grapheComplet, depotComplet, pointsComplet);
                a2.executerApproche2Simple();
            }

            // approche 2 HO1 realiste
            else if (choix == 4) {
                System.out.println("=== APPROCHE 2 REALISTE (HO1, MST + capacités) ===\n");

                //  initialisation quantités
                Random random = new Random();
                for (Sommet p : pointsComplet) {
                    int q = random.nextInt(8); // 0..7
                    p.setQuantiteDechets(q);
                }

                double capaciteCamion = 10.0;
                Approche2MSTRealiste a2r =
                        new Approche2MSTRealiste(grapheComplet, depotComplet, pointsComplet);
                a2r.executerApproche2Realiste(capaciteCamion);
            }

            // approche 1 HO2/HO3 simple
            else if (choix == 5) {
                System.out.println("=== APPROCHE 1 SIMPLE (HO2/HO3, graphe orienté) ===\n");
                Approche1HO2HO3Simple a1h = new Approche1HO2HO3Simple(grapheHO2HO3, depotHO2HO3, pointsHO2HO3);
                a1h.executerApproche1();
            }

            // approche 1 HO2/HO3 realiste
            else if (choix == 6) {
                System.out.println("=== APPROCHE 1 REALISTE (HO2/HO3, graphe orienté + capacités) ===\n");

                Random random = new Random();
                System.out.println("Quantités initiales de déchets (HO2/HO3) :");
                for (Sommet p : pointsHO2HO3) {
                    int q = random.nextInt(8); // 0..7
                    p.setQuantiteDechets(q);
                    System.out.println(" - " + p.getId() + " : " + q + " t");
                }
                System.out.println();

                double capaciteCamion = 10.0;
                Approche1HO2HO3Realiste a1hr =
                        new Approche1HO2HO3Realiste(grapheHO2HO3, depotHO2HO3, pointsHO2HO3, capaciteCamion);
                a1hr.executerCollecte();
            }

            else {
                System.out.println("Choix invalide.");
            }

        } catch (IOException e) {
            System.out.println("Erreur lors du chargement d'un fichier : " + e.getMessage());
        }

        sc.close();
    }
}



