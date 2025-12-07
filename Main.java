import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        boolean continuer = true;
        while (continuer) {

            System.out.println("MENU PRINCIPAL");
            System.out.println("1 - Thème 1 : Optimiser le ramassage aux pieds des habitations");
            System.out.println("2 - Thème 2 : Optimiser les ramassages des points de collecte");
            System.out.println("3 - Thème 3 : Planifier les jours de passage");
            System.out.println("0 - Quitter");
            System.out.print("Votre choix de thème : ");

            int choixTheme = lireEntier(scanner);

            if (choixTheme == 0) {
                System.out.println("Au revoir !");
                continuer = false;
                break;
            }

            switch (choixTheme) {
                case 1:
                    gererTheme1(scanner);
                    break;
                case 2:
                    System.out.println("\nThème 2 : pas encore implémenté dans ce prototype.\n");
                    break;
                case 3:
                    System.out.println("\nThème 3 : pas encore implémenté dans ce prototype.\n");
                    break;
                default:
                    System.out.println("\nChoix de thème invalide.\n");
            }
        }

        scanner.close();
    }

    // THEME 1

    private static void gererTheme1(Scanner scanner) {
        System.out.println("\nThème 1 : Optimiser le ramassage aux pieds des habitations");
        System.out.println("1 - Problématique 1 : Organiser la collecte des encombrants");
        System.out.println("2 - Problématique 2 : Organiser la collecte des poubelles aux pieds des habitations");
        System.out.print("Votre choix de problématique : ");

        int choixProblematique = lireEntier(scanner);
        System.out.println();

        switch (choixProblematique) {
            case 1:
                gererProblematique1Theme1(scanner);
                break;
            case 2:
                gererProblematique2Theme1(scanner);
                break;
            default:
                System.out.println("Problématique inconnue pour le Thème 1.\n");
        }
    }

    //  PROBLÉMATIQUE 1 (ENCOMBRANTS)

    private static void gererProblematique1Theme1(Scanner scanner) {

        System.out.println(" Problématique 1 : Organiser la collecte des encombrants");
        System.out.println("   Hypothèses :");
        System.out.println("   1 - Hypothèse 1 : Un seul ramassage");
        System.out.println("   2 - Hypothèse 2 : Tournée (TSP)");
        System.out.print("Votre choix : ");

        int hypProblematique = lireEntier(scanner);
        System.out.println();

        int choixHO = demanderHypotheseOrientation(scanner);
        System.out.println();

        // Hypothèse 1
        if (hypProblematique == 1) {

            if (choixHO == 1) {
                String nomFichier = "commune_village_HO1.txt";
                ProgrammeHO1Encombrant.executerCalculAllerRetour(nomFichier);

            } else if (choixHO == 2) {
                String nomFichier = "commune_village_HO2.txt";
                ProgrammeHO2Encombrant.executerCalculAllerRetourHO2(nomFichier);

            } else if (choixHO == 3) {
                String nomFichier = "commune_village_HO3.txt";
                ProgrammeHO3Encombrant.executerCalculAllerRetourHO3(nomFichier);

            } else {
                System.out.println("Numéro HO inconnu.");
            }

        }
        //  Hypothèse 2
        else if (hypProblematique == 2) {

            System.out.println("Problématique 1 / Hypothèse 2 : tournée TSP");

            if (choixHO == 1) {
                String nomFichier = "commune_village_HO1_tournee.txt";
                ProgrammeHO1Tournee.executerTourneeHO1(nomFichier);

            } else if (choixHO == 2) {
                String nomFichier = "commune_village_HO2_tournee.txt";
                ProgrammeHO2Tournee.executerTourneeHO2(nomFichier);

            } else if (choixHO == 3) {
                String nomFichier = "commune_village_HO3_tournee.txt";
                ProgrammeHO3Tournee.executerTourneeHO3(nomFichier);

            } else {
                System.out.println("Numéro HO inconnu.");
            }
        }
        else {
            System.out.println("Hypothèse inconnue.");
        }
    }

    //  PROBLÉMATIQUE 2 (POUBELLES)

    private static void gererProblematique2Theme1(Scanner scanner) {

        System.out.println(" Problématique 2 : Collecte des poubelles aux pieds des habitations");
        System.out.println("   1 - Cas idéal : tous sommets pairs");
        System.out.println("   2 - Deux sommets impairs");
        System.out.println("   3 - Cas général");
        System.out.print("Votre choix de cas : ");

        int casProblematique = lireEntier(scanner);
        System.out.println();

        int choixHO = demanderHypotheseOrientation(scanner);
        System.out.println();

        System.out.println(" Exécution Problématique 2 / HO" + choixHO);

        String fichier;

        //  Cas 1
        if (casProblematique == 1) {
            if (choixHO == 1) {
                fichier = "village_P2_cas1_HO1.txt";
                ProgrammeP2HO1TousPairs.executerTousPairs(fichier);
            } else if (choixHO == 2) {
                fichier = "village_P2_cas1_HO2.txt";
                ProgrammeP2HO2Cas1.executerCas1HO2(fichier);
            } else if (choixHO == 3) {
                System.out.println("Cas 1 / HO3 pas encore implémenté.\n");
            } else {
                System.out.println("HO inconnu pour le cas 1.\n");
            }
        }

        //  Cas 2
        else if (casProblematique == 2) {
            if (choixHO == 1) {
                fichier = "village_P2_cas2_HO1.txt";
                ProgrammeP2HO1DeuxImpairs.executerDeuxImpairs(fichier);
            } else if (choixHO == 2) {
                fichier = "village_P2_cas2_HO2.txt";
                ProgrammeP2HO2Cas2.executerCas2HO2(fichier);
            } else if (choixHO == 3) {
                System.out.println("Cas 2 / HO3 pas encore implémenté.\n");
            } else {
                System.out.println("HO inconnu pour le cas 2.\n");
            }
        }

        //  Cas 3
        else if (casProblematique == 3) {
            if (choixHO == 1) {
                fichier = "village_P2_cas3_HO1.txt";
                ProgrammeP2HO1CasGeneral.executerCasGeneral(fichier);
            } else if (choixHO == 2) {
                fichier = "village_P2_cas3_HO2.txt";
                ProgrammeP2HO2Cas3.executerCas3HO2(fichier);
            } else if (choixHO == 3) {
                System.out.println("Cas 3 / HO3 pas encore implémenté.\n");
            } else {
                System.out.println("HO inconnu pour le cas 3.\n");
            }
        }

        else {
            System.out.println("Numéro de cas inconnu.\n");
        }
    }

    //  CHOIX HO

    private static int demanderHypotheseOrientation(Scanner scanner) {
        System.out.println("Choix de l'hypothèse HO :");
        System.out.println("1 - HO1 : Double sens");
        System.out.println("2 - HO2 : Sens uniques possibles");
        System.out.println("3 - HO3 : Mélange double sens + sens uniques");
        System.out.print("Votre choix : ");
        return lireEntier(scanner);
    }

    //  LECTURE ENTIER

    private static int lireEntier(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.print("Veuillez entrer un nombre entier : ");
            scanner.next();
        }
        int valeur = scanner.nextInt();
        scanner.nextLine();
        return valeur;
    }
}
