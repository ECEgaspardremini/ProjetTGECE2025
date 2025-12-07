import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        boolean continuer = true;

        while (continuer) {

            System.out.println("\nmenu principal : ");
            System.out.println("1) Thème 1 : Ramassage aux pied des habitations");
            System.out.println("2) Thème 2 : Collecte des points de collecte");
            System.out.println("3) Thème 3 : Planification des jour de passage");
            System.out.println("0 - Quitter");
            System.out.print("Votre choix : ");

            int choix = lireEntier(scanner);

            switch (choix) {
                case 1:
                    menuTheme1(scanner);
                    break;
                case 2:
                    menuTheme2(scanner);
                    break;
                case 3:
                    menuTheme3(scanner);
                    break;
                case 0:
                    System.out.println("Au revoir !");
                    continuer = false;
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        }

        scanner.close();
    }


    //thème 1
    private static void menuTheme1(Scanner scanner) {

        System.out.println("\nthème 1 :");
        System.out.println("1) Problématique 1 : Encombrant");
        System.out.println("2) Problématique 2 : Poubelles");
        System.out.print("votre choix : ");

        int choixProb = lireEntier(scanner);
        System.out.println();

        switch (choixProb) {
            case 1:
                theme1Problematique1(scanner);
                break;
            case 2:
                theme1Problematique2(scanner);
                break;
            default:
                System.out.println("Choix invalide.");
        }
    }

    // thème 1 problèmatique 1

    private static void theme1Problematique1(Scanner scanner) {

        System.out.println("Problématique 1 : Encombrants");
        System.out.println("1)H1 : Un seul ramassage");
        System.out.println("2)H2 : Tournée TSP");
        System.out.print("Votre choix : ");
        int hyp = lireEntier(scanner);

        int ho = demanderHO(scanner);
        System.out.println();

        if (hyp == 1) {
            if (ho == 1)
                ProgrammeHO1Encombrant.executerCalculAllerRetour("commune_village_HO1.txt");
            else if (ho == 2)
                ProgrammeHO2Encombrant.executerCalculAllerRetourHO2("commune_village_HO2.txt");
            else if (ho == 3)
                ProgrammeHO3Encombrant.executerCalculAllerRetourHO3("commune_village_HO3.txt");
        }
        else if (hyp == 2) {
            if (ho == 1)
                ProgrammeHO1Tournee.executerTourneeHO1("commune_village_HO1_tournee.txt");
            else if (ho == 2)
                ProgrammeHO2Tournee.executerTourneeHO2("commune_village_HO2_tournee.txt");
            else if (ho == 3)
                ProgrammeHO3Tournee.executerTourneeHO3("commune_village_HO3_tournee.txt");
        }
    }

    // thème 1 problématique 2

    private static void theme1Problematique2(Scanner scanner) {

        System.out.println("problématique 2 : poubelles");
        System.out.println("1) Cas idéal (tous pairs)");
        System.out.println("2) Deux sommets impairs");
        System.out.println("3 Cas général");
        System.out.print("Votre choix : ");
        int cas = lireEntier(scanner);

        int ho = demanderHO(scanner);
        System.out.println();

        if (cas == 1 && ho == 1)
            ProgrammeP2HO1TousPairs.executerTousPairs("village_P2_cas1_HO1.txt");
        else if (cas == 1 && ho == 2)
            ProgrammeP2HO2Cas1.executerCas1HO2("village_P2_cas1_HO2.txt");
        else if (cas == 2 && ho == 1)
            ProgrammeP2HO1DeuxImpairs.executerDeuxImpairs("village_P2_cas2_HO1.txt");
        else if (cas == 2 && ho == 2)
            ProgrammeP2HO2Cas2.executerCas2HO2("village_P2_cas2_HO2.txt");
        else if (cas == 3 && ho == 1)
            ProgrammeP2HO1CasGeneral.executerCasGeneral("village_P2_cas3_HO1.txt");
        else if (cas == 3 && ho == 2)
            ProgrammeP2HO2Cas3.executerCas3HO2("village_P2_cas3_HO2.txt");
        else
            System.out.println("Ce cas n'est pas encore implémenter pour HO3.");
    }


    //thème 2
    private static void menuTheme2(Scanner sc) {

        System.out.println("=== THÈME 2 ===");
        System.out.println("1) Approche 1 HO1 simple");
        System.out.println("2) Approche 1 HO1 réaliste");
        System.out.println("3) Approche 2 HO1 simple (MST)");
        System.out.println("4) Approche 2 HO1 réaliste (MST + capacité)");
        System.out.println("5) Approche 1 HO2/HO3 simple");
        System.out.println("6) Approche 1 HO2/HO3 réaliste");
        System.out.print("Votre choix : ");

        int choix = lireEntier(sc);
        System.out.println();

        String fichierVille = "graphe_points.txt";
        String fichierMST = "graphe_complet_mst.txt";
        String fichierHO = "graphe_ho2_ho3.txt";

        try {

            Graphe gv = null, gMST = null;
            GraphesHO2HO3 gHO = null;
            Sommet depot;
            ArrayList<Sommet> points;

            // HO1
            if (choix <= 4) {
                gv = Graphe.chargerDepuisFichier(fichierVille);
                depot = gv.trouverDepot();
                points = gv.trouverPointsCollecte();

                if (depot == null) {
                    System.out.println("ERREUR : dépôt manquant.");
                    return;
                }
            }

            // Approches MST
            if (choix == 3 || choix == 4) {
                gMST = Graphe.chargerDepuisFichier(fichierMST);
            }

            // HO2/HO3
            if (choix == 5 || choix == 6) {
                gHO = GraphesHO2HO3.chargerDepuisFichier(fichierHO);
            }

            switch (choix) {

                case 1:
                    System.out.println("Approche 1 HO1 simple");
                    new Approche1Simple(gv, gv.trouverDepot(), gv.trouverPointsCollecte()).executerApproche1();
                    break;

                case 2:
                    System.out.println("Approche 1 HO1 réaliste");
                    Random r = new Random();
                    for (Sommet p : gv.trouverPointsCollecte())
                        p.setQuantiteDechets(r.nextInt(8));
                    new AlgorithmeCollecte(gv, gv.trouverDepot(), gv.trouverPointsCollecte(), 10).executerCollecte();
                    break;

                case 3:
                    System.out.println("Approche 2 HO1 simple (MST)");
                    new Approche2MSTSimple(gMST, gMST.trouverDepot(), gMST.trouverPointsCollecte()).executerApproche2Simple();
                    break;

                case 4:
                    System.out.println("Approche 2 HO1 réaliste");
                    new Approche2MSTRealiste(gMST, gMST.trouverDepot(), gMST.trouverPointsCollecte())
                            .executerApproche2Realiste(10);
                    break;

                case 5:
                    System.out.println("Approche HO2/HO3 simple");
                    new Approche1HO2HO3Simple(gHO, gHO.trouverDepot(), gHO.trouverPointsCollecte())
                            .executerApproche1();
                    break;

                case 6:
                    System.out.println("Approche HO2/HO3 réaliste");
                    new Approche1HO2HO3Realiste(gHO, gHO.trouverDepot(), gHO.trouverPointsCollecte(), 10)
                            .executerCollecte();
                    break;

                default:
                    System.out.println("Choix invalide.");
            }

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    //theme 3
    private static void menuTheme3(Scanner sc) {

        System.out.println("\ntheme 3");
        System.out.println("1)WP (H1)");
        System.out.println("2)WP + capacité (H2)");
        System.out.print("Votre choix : ");

        String choix = sc.nextLine().trim();

        switch (choix) {
            case "1":
                theme3H1(sc);
                break;
            case "2":
                theme3H2(sc);
                break;
            default:
                System.out.println("Choix invalide.");
        }
    }

    //H1

    private static void theme3H1(Scanner sc) {

        System.out.print("Chemin fichier secteurs : ");
        String f = sc.nextLine().trim();
        if (f.isEmpty()) f = "theme3_h1_monGraphe.txt";

        try {
            GrapheSecteurs g = GrapheSecteurs.chargerDepuisFichier(f);
            Map<Secteur,Integer> res = new T3H1WP().colorierWelshPowell(g);

            System.out.println("\nRésultat WP :");
            res.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEach(e -> System.out.println("Secteur " +
                            e.getKey().getNom() + " → Jour " + e.getValue()));

        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // ------------------------------- H2 ------------------------------

    private static void theme3H2(Scanner sc) {

        System.out.print("Chemin fichier secteurs : ");
        String f = sc.nextLine().trim();
        if (f.isEmpty()) f = "theme3_h1_monGraphe.txt";

        try {

            GrapheSecteurs g = GrapheSecteurs.chargerDepuisFichier(f);

            System.out.print("Capacité camion : ");
            int C = Integer.parseInt(sc.nextLine().trim());

            System.out.print("nombre de camions : ");
            int N = Integer.parseInt(sc.nextLine().trim());

            for (Secteur s : g.getTousSecteurs())
                s.setQuantite(3 + new Random().nextInt(10));

            Map<Secteur,Integer> res = new T3H2().planifier(g,C,N);

            System.out.println("\nRésultat H2 :");
            res.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEach(e -> System.out.println(
                            "Secteur " + e.getKey().getNom() +
                                    " (q=" + e.getKey().getQuantite() + ")" +
                                    " → Jour " + e.getValue()));

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // utilitaires

    private static int lireEntier(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.print("Entrez un entier : ");
            sc.next();
        }
        int n = sc.nextInt();
        sc.nextLine();
        return n;
    }

    private static int demanderHO(Scanner sc) {
        System.out.println("\nChoix HO :");
        System.out.println("1)HO1");
        System.out.println("2) HO2");
        System.out.println("3) HO3");
        System.out.print("Votre choix : ");
        return lireEntier(sc);
    }
}
