import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        // scanner pour tout le programme
        Scanner sc = new Scanner(System.in);

        // menu
        System.out.println("1. Thème 3 H1 WP");
        System.out.println("2. Thème 3 H2 WP + capa random");
        System.out.println("3. Quitter");
        System.out.print("choix : ");

        String choix = sc.nextLine().trim();

        switch (choix) {
            case "1":
                // Lance le theme 3 H1
                lancerTheme3H1DepuisFichier(sc);
                break;

            case "2":
                // Lancer Thème 3 H2
                lancerTheme3H2DepuisFichier(sc);
                break;
                //arrete le programme
            case "3":
                System.out.println("fin");
                break;
            // on met un default si aucun des choiw ou un choix different est mit
            default:
                System.out.println("choix non valide");
        }

        sc.close();
    }

    // H1


    public static void lancerTheme3H1DepuisFichier(Scanner sc) {

        // Demander le fichier à l'utilisateur
        System.out.print("Chemin du fichier : ");
        String chemin = sc.nextLine().trim();

        // Nom par défaut
        if (chemin.isEmpty()) {
            chemin = "theme3_h1_monGraphe.txt";
        }

        try {
            // Charger le graphe des secteurs
            GrapheSecteurs g = GrapheSecteurs.chargerDepuisFichier(chemin);

            // Appliquer Welsh&Powell
            T3H1WP planif = new T3H1WP();
            Map<Secteur, Integer> resultat = planif.colorierWelshPowell(g);

            // Affichage
            System.out.println("\nResultat theme 3 H1 :");

            // On met les paires (Secteur, jour) dans une liste
            List<Map.Entry<Secteur, Integer>> listeResultat = new ArrayList<>(resultat.entrySet());

            // on tri dabord par jour(valeur) puis par nom de secteur
            Collections.sort(
                    listeResultat,
                    new Comparator<Map.Entry<Secteur, Integer>>() {
                        @Override
                        public int compare(Map.Entry<Secteur, Integer> e1, Map.Entry<Secteur, Integer> e2) {
                            int cmpJour = e1.getValue().compareTo(e2.getValue());
                            if (cmpJour != 0) {
                                return cmpJour;
                            }
                            return e1.getKey().getNom().compareTo(e2.getKey().getNom());
                        }
                    }
            );

            // Affichage final
            for (Map.Entry<Secteur, Integer> e : listeResultat) {
                System.out.println(
                        "secteur " + e.getKey().getNom()
                                + "  : jour (couleur) " + e.getValue()
                );
            }

        } catch (IOException e) {
            System.out.println("Erreur  : " + e.getMessage());
        }
    }

    //H2

    public static void lancerTheme3H2DepuisFichier(Scanner sc) {

        //Demander le fichier
        System.out.print("Chemin du fichier  : ");
        String chemin = sc.nextLine().trim();
        if (chemin.isEmpty()) {
            // On utiliuse le meme fichier que H1 si besoin
            chemin = "theme3_h1_monGraphe.txt";
        }

        try {
            //Charger le graphe
            GrapheSecteurs g = GrapheSecteurs.chargerDepuisFichier(chemin);

            //On demande la qapacité C et le nombre de cmaions N
            System.out.print("Capacité du camion en tonne C = ");
            int capaciteCamion = Integer.parseInt(sc.nextLine().trim());

            System.out.print("nbr de camions dispo par jour N = ");
            int nbCamions = Integer.parseInt(sc.nextLine().trim());

            int capaciteJour = capaciteCamion * nbCamions;

            // On génère des quantités aléatoire pour chaque secteur
            Random rand = new Random();
            int qMin = 3;   // 3 tonnes minimum
            int qMax = 12;  // 12 tonnes maximum

            System.out.println("\nquantités :");
            // Récupérer tous les secteurs et on les tri par noms
            List<Secteur> listeSecteurs = new ArrayList<>(g.getTousSecteurs());
            Collections.sort(listeSecteurs, new Comparator<Secteur>() {
                @Override
                public int compare(Secteur s1, Secteur s2) {
                    return s1.getNom().compareTo(s2.getNom());
                }
            });


            for (Secteur s : listeSecteurs) {
                int q = qMin + rand.nextInt(qMax - qMin + 1); // entre qMin et qMax
                s.setQuantite(q);
                System.out.println("  Secteur " + s.getNom() + " : q = " + q + " tonnes");
            }

            // On lance H2 (WP et tri par capacité)
            T3H2 planif = new T3H2();
            Map<Secteur, Integer> resultat = planif.planifier(g, capaciteCamion, nbCamions);

            System.out.println("\n résultat Thème 3 H2");
            System.out.println("Capacité journalière max : " + capaciteJour);

            // Comme pour H1 on trie
            List<Map.Entry<Secteur, Integer>> listeResultat = new ArrayList<>(resultat.entrySet());
            Collections.sort(
                    listeResultat,
                    new Comparator<Map.Entry<Secteur, Integer>>() {
                        @Override
                        public int compare(Map.Entry<Secteur, Integer> e1, Map.Entry<Secteur, Integer> e2) {
                            int cmpJour = e1.getValue().compareTo(e2.getValue());
                            if (cmpJour != 0) {
                                return cmpJour;
                            }
                            return e1.getKey().getNom().compareTo(e2.getKey().getNom());
                        }
                    }
            );
            for (Map.Entry<Secteur, Integer> e : listeResultat) {
                System.out.println(
                        "Secteur " + e.getKey().getNom()
                                + " (q=" + e.getKey().getQuantite() + ")"
                                + " : Jour " + e.getValue()
                );
            }



        } catch (IOException e) {
            System.out.println("Erreur lors du chargement du fichier du graphe : " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Entrée numérique invalide pour C ou N.");
        }
    }
}
