import java.util.ArrayList;
import java.util.Arrays;


public class Approche2MSTRealiste {

    private Graphe graphe;
    private Sommet depot;
    private ArrayList<Sommet> pointsCollecte;

    private ArrayList<Sommet> sommetsTSP;
    private int[][] matriceDistances;
    private int[] indiceGlobal;

    public Approche2MSTRealiste(Graphe graphe, Sommet depot, ArrayList<Sommet> pointsCollecte) {
        this.graphe = graphe;
        this.depot = depot;
        this.pointsCollecte = pointsCollecte;

        this.sommetsTSP = new ArrayList<>();
        this.sommetsTSP.add(depot);
        this.sommetsTSP.addAll(pointsCollecte);

        this.matriceDistances = graphe.construireMatriceDistances();

        int n = sommetsTSP.size();
        this.indiceGlobal = new int[n];
        for (int i = 0; i < n; i++) {
            Sommet s = sommetsTSP.get(i);
            indiceGlobal[i] = graphe.getIndiceSommet(s);
        }
    }



    private int distanceEntreLocal(int iLocal, int jLocal) {
        int ig = indiceGlobal[iLocal];
        int jg = indiceGlobal[jLocal];
        return matriceDistances[ig][jg];
    }

    private int distanceEntre(Sommet a, Sommet b) {
        int iaLocal = -1;
        int ibLocal = -1;

        for (int i = 0; i < sommetsTSP.size(); i++) {
            if (sommetsTSP.get(i) == a) iaLocal = i;
            if (sommetsTSP.get(i) == b) ibLocal = i;
        }
        if (iaLocal == -1 || ibLocal == -1) return 0;
        return distanceEntreLocal(iaLocal, ibLocal);
    }

    private int[] calculerMSTPrim() {
        int n = sommetsTSP.size();
        int[] parent = new int[n];
        int[] cle = new int[n];
        boolean[] dansMST = new boolean[n];

        int INF = 1_000_000_000;
        Arrays.fill(cle, INF);
        Arrays.fill(dansMST, false);
        Arrays.fill(parent, -1);

        // racine = dépôt (indice 0)
        cle[0] = 0;

        for (int cpt = 0; cpt < n - 1; cpt++) {
            int u = -1;
            int min = INF;
            for (int i = 0; i < n; i++) {
                if (!dansMST[i] && cle[i] < min) {
                    min = cle[i];
                    u = i;
                }
            }
            if (u == -1) break;

            dansMST[u] = true;

            for (int v = 0; v < n; v++) {
                int poidsUV = distanceEntreLocal(u, v);
                if (!dansMST[v] && poidsUV < cle[v]) {
                    cle[v] = poidsUV;
                    parent[v] = u;
                }
            }
        }

        return parent;
    }

    private int calculerPoidsMST(int[] parent) {
        int n = parent.length;
        int poidsTotal = 0;

        for (int i = 0; i < n; i++) {
            if (parent[i] != -1) {
                int p = parent[i];
                int poids = distanceEntreLocal(i, p);
                poidsTotal += poids;
            }
        }

        return poidsTotal;
    }

    private void dfsMST(int u, boolean[] visite, ArrayList<Integer> parcours, ArrayList<Integer>[] voisins) {
        visite[u] = true;
        parcours.add(u);

        for (int v : voisins[u]) {
            if (!visite[v]) {
                dfsMST(v, visite, parcours, voisins);
                parcours.add(u);
            }
        }
    }


    private ArrayList<Sommet> calculerOrdreVisiteEtAfficherMST() {
        System.out.println("===== APPROCHE 2 REALISTE (MST + capacités) =====");

        // 1) Infos sommets
        System.out.print("Sommets pris en compte (TSP) : ");
        for (int i = 0; i < sommetsTSP.size(); i++) {
            System.out.print(sommetsTSP.get(i).getId());
            if (i < sommetsTSP.size() - 1) System.out.print(", ");
        }
        System.out.println("\n");

        // 2) MST
        int[] parent = calculerMSTPrim();
        int poidsMST = calculerPoidsMST(parent);

        System.out.println("Arbre couvrant de poids minimum (MST) :");
        for (int i = 0; i < parent.length; i++) {
            if (parent[i] != -1) {
                Sommet fils = sommetsTSP.get(i);
                Sommet pere = sommetsTSP.get(parent[i]);
                int d = distanceEntreLocal(i, parent[i]);
                System.out.println(" - " + pere.getId() + " -- " + fils.getId() + " (" + d + " m)");
            }
        }
        System.out.println("Poids total du MST : " + poidsMST + " m\n");

        // 3) Graphe de voisins du MST
        int n = sommetsTSP.size();
        @SuppressWarnings("unchecked")
        ArrayList<Integer>[] voisins = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            voisins[i] = new ArrayList<>();
        }
        for (int i = 0; i < n; i++) {
            if (parent[i] != -1) {
                int p = parent[i];
                voisins[p].add(i);
                voisins[i].add(p);
            }
        }

        // 4) DFS sur MST
        boolean[] visite = new boolean[n];
        ArrayList<Integer> parcoursBrut = new ArrayList<>();

        dfsMST(0, visite, parcoursBrut, voisins);

        System.out.println("Parcours préfixe sur le MST (avec allers/retours) :");
        for (int i = 0; i < parcoursBrut.size(); i++) {
            System.out.print(sommetsTSP.get(parcoursBrut.get(i)).getId());
            if (i < parcoursBrut.size() - 1) System.out.print(" -> ");
        }
        System.out.println();

        int distanceBrute = 0;
        for (int i = 0; i < parcoursBrut.size() - 1; i++) {
            int u = parcoursBrut.get(i);
            int v = parcoursBrut.get(i + 1);
            distanceBrute += distanceEntreLocal(u, v);
        }
        System.out.println("Distance si on parcourt toutes les arêtes du MST avec allers/retours : " +
                distanceBrute + " m");
        System.out.println("Théoriquement : 2 × poids(MST) = " + (2 * poidsMST) + " m\n");

        // 5) Shortcutting
        ArrayList<Integer> parcoursFinalIndices = new ArrayList<>();
        boolean[] dejaVisite = new boolean[n];

        for (int idx : parcoursBrut) {
            if (!dejaVisite[idx]) {
                parcoursFinalIndices.add(idx);
                dejaVisite[idx] = true;
            }
        }
        // retour au dépôt
        if (parcoursFinalIndices.get(parcoursFinalIndices.size() - 1) != 0) {
            parcoursFinalIndices.add(0);
        }

        System.out.println("Parcours final après shortcutting (ordre de base sans découpage) :");
        for (int i = 0; i < parcoursFinalIndices.size(); i++) {
            System.out.print(sommetsTSP.get(parcoursFinalIndices.get(i)).getId());
            if (i < parcoursFinalIndices.size() - 1) System.out.print(" -> ");
        }
        System.out.println("\n");

        // Convertir en liste de Sommets
        ArrayList<Sommet> ordre = new ArrayList<>();
        for (int idx : parcoursFinalIndices) {
            ordre.add(sommetsTSP.get(idx));
        }
        return ordre;
    }

    // methode de base  venersion réaliste avec capacités

    public void executerApproche2Realiste(double capaciteCamion) {
        //  quantités points colectes initialisées dans le main
        System.out.println("Quantités initiales de déchets (graphe complet) :");
        for (Sommet p : pointsCollecte) {
            System.out.println(" - " + p.getId() + " : " + p.getQuantiteDechets() + " t");
        }
        System.out.println("Capacité du camion : " + capaciteCamion + " t\n");

        // 1) on calcule l'ordre de visite (MST + DFS + shortcutting) et on affiche MST + parcours
        ArrayList<Sommet> ordre = calculerOrdreVisiteEtAfficherMST();

        // 2) on découpe en tournées sans fractionner les points
        ArrayList<ArrayList<Sommet>> tournees = new ArrayList<>();
        ArrayList<Sommet> tourneeCourante = new ArrayList<>();
        double chargeCourante = 0.0;

        // on commence toujours par D
        tourneeCourante.add(depot);

        // on parcourt l'ordre sauf le dernier sommet qui est D (retour final)
        for (int i = 0; i < ordre.size() - 1; i++) {
            Sommet s = ordre.get(i);
            if (s == depot) {
                continue; // on ne traite pas D comme point de collecte
            }

            double q = s.getQuantiteDechets();

            if (chargeCourante + q <= capaciteCamion + 1e-9) {
                // On peut ajouter ce point dans la tournée courante
                tourneeCourante.add(s);
                chargeCourante += q;
            } else {
                // On ferme la tournée courante en revenant au dépôt
                tourneeCourante.add(depot);
                tournees.add(tourneeCourante);

                // On commence une nouvelle tournée
                tourneeCourante = new ArrayList<>();
                tourneeCourante.add(depot);
                tourneeCourante.add(s);
                chargeCourante = q;
            }
        }

        // On ferme la dernière tournée
        tourneeCourante.add(depot);
        tournees.add(tourneeCourante);

        // 3) On affiche toutes les tournées avec charges et distances
        int distanceTotale = 0;
        System.out.println("Découpage en tournées (sans fractionner les points) :\n");

        for (int t = 0; t < tournees.size(); t++) {
            ArrayList<Sommet> tList = tournees.get(t);

            // Calcul de la charge de la tournée
            double chargeTournee = 0.0;
            for (Sommet s : tList) {
                if (s != depot) {
                    chargeTournee += s.getQuantiteDechets();
                }
            }

            System.out.print("Tournée " + (t + 1) + " : ");
            for (int i = 0; i < tList.size(); i++) {
                System.out.print(tList.get(i).getId());
                if (i < tList.size() - 1) System.out.print(" -> ");
            }
            System.out.println("    (charge = " + chargeTournee + " t)");

            // Calcul des distances
            int distanceTournee = 0;
            int cumul = 0;
            for (int i = 0; i < tList.size() - 1; i++) {
                Sommet a = tList.get(i);
                Sommet b = tList.get(i + 1);
                int d = distanceEntre(a, b);
                distanceTournee += d;
                cumul += d;
                System.out.println("  De " + a.getId() + " vers " + b.getId() + " : " +
                        d + " m [total : " + cumul + " m]");
            }

            distanceTotale += distanceTournee;
            System.out.println("  Distance de la tournée " + (t + 1) + " : " +
                    distanceTournee + " m\n");
        }

        System.out.println("Distance totale toutes tournées (approche 2 réaliste) : " +
                distanceTotale + " m");
        System.out.println("Nombre de tournées : " + tournees.size());
        System.out.println("===== FIN APPROCHE 2 REALISTE =====\n");
    }
}


