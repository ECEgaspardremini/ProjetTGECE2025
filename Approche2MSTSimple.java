import java.util.ArrayList;
import java.util.Arrays;


public class Approche2MSTSimple {

    private Graphe graphe;
    private Sommet depot;
    private ArrayList<Sommet> pointsCollecte;

    // sommets utilisés par l'approche 2
    private ArrayList<Sommet> sommetsTSP;
    // Matrice des distances du graphe complet
    private int[][] matriceDistances;
    // indice du sommet sommetsTSP[i] dans la liste globale du Graphe
    private int[] indiceGlobal;

    public Approche2MSTSimple(Graphe graphe, Sommet depot, ArrayList<Sommet> pointsCollecte) {
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


     //distance entre deux indices locaux dans sommetsTSP

    private int distanceEntreLocal(int iLocal, int jLocal) {
        int ig = indiceGlobal[iLocal];
        int jg = indiceGlobal[jLocal];
        return matriceDistances[ig][jg];
    }


    private int[] calculerMSTPrim() {
        int n = sommetsTSP.size();
        int[] parent = new int[n];
        int[] cle = new int[n];
        boolean[] dansMST = new boolean[n];
         // valeur considerer comme infini donc comme si pas relié
        int INF = 1_000_000_000;

        Arrays.fill(cle, INF);
        Arrays.fill(dansMST, false);
        Arrays.fill(parent, -1);

        // Racine = dépôt à l'indice 0 dans sommetsTSP
        cle[0] = 0;

        for (int cpt = 0; cpt < n - 1; cpt++) {
            // recherche le sommet non dans le MST avec la plus petite clé
            int u = -1;
            int min = INF;
            for (int i = 0; i < n; i++) {
                if (!dansMST[i] && cle[i] < min) {
                    min = cle[i];
                    u = i;
                }
            }
            if (u == -1) {
                break;
            }

            dansMST[u] = true;

            // relax des voisin
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


    private ArrayList<int[]> calculerOrdrePrimSurMST(int[] parent) {
        int n = parent.length;

        // Construire les voisins du MST à partir de parent[]
        @SuppressWarnings("unchecked")
        ArrayList<Integer>[] voisinsMST = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            voisinsMST[i] = new ArrayList<>();
        }
        for (int i = 0; i < n; i++) {
            if (parent[i] != -1) {
                int p = parent[i];
                voisinsMST[p].add(i);
                voisinsMST[i].add(p);
            }
        }

        ArrayList<int[]> ordre = new ArrayList<>();
        boolean[] dansArbre = new boolean[n];
        int INF = 1_000_000_000;

        dansArbre[0] = true;

        // On doit ajouter n-1 arêtes
        for (int k = 0; k < n - 1; k++) {
            int bestU = -1;
            int bestV = -1;
            int bestW = INF;

            // rechercher l'arête MST u-v avec u dans l'arbre et v hors de l'arbre
            for (int u = 0; u < n; u++) {
                if (!dansArbre[u]) continue;
                for (int v : voisinsMST[u]) {
                    if (dansArbre[v]) continue;
                    int w = distanceEntreLocal(u, v);
                    if (w < bestW) {
                        bestW = w;
                        bestU = u;
                        bestV = v;
                    }
                }
            }

            if (bestV == -1) {
                break; // ne devrait pas arriver
            }

            ordre.add(new int[]{bestU, bestV});
            dansArbre[bestV] = true;
        }

        return ordre;
    }


    private void dfsMST(int u, boolean[] visite, ArrayList<Integer> parcours, ArrayList<Integer>[] voisins) {
        visite[u] = true;
        parcours.add(u);

        for (int v : voisins[u]) {
            if (!visite[v]) {
                dfsMST(v, visite, parcours, voisins);
                // On revient à u
                parcours.add(u);
            }
        }
    }


    public void executerApproche2Simple() {
        System.out.println("===== APPROCHE 2 SIMPLE (MST + DFS + shortcutting) =====");

        // 1) infos sur les sommets TSP
        System.out.print("Sommets pris en compte (TSP) : ");
        for (int i = 0; i < sommetsTSP.size(); i++) {
            System.out.print(sommetsTSP.get(i).getId());
            if (i < sommetsTSP.size() - 1) System.out.print(", ");
        }
        System.out.println("\n");

        // 2.1) calcul du MST
        int[] parent = calculerMSTPrim();
        int poidsMST = calculerPoidsMST(parent);

        // 2.2) affichage dans un ordre cohérent avec Prim
        System.out.println("Arbre couvrant de poids minimum (MST) – ordre de Prim :");
        ArrayList<int[]> ordrePrim = calculerOrdrePrimSurMST(parent);
        int etape = 1;
        for (int[] arete : ordrePrim) {
            int u = arete[0];
            int v = arete[1];
            Sommet sPere = sommetsTSP.get(u);
            Sommet sFils = sommetsTSP.get(v);
            int d = distanceEntreLocal(u, v);
            System.out.println(" Étape " + etape + " : " +
                    sPere.getId() + " -- " + sFils.getId() + " (" + d + " m)");
            etape++;
        }
        System.out.println("Poids total du MST : " + poidsMST + " m\n");

        // 3) construction du graphe du MST en termes de voisins pour DFS
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

        // 4) Parcours DFS  sur le MST
        // parcours BRUT = (allers/retours)
        boolean[] visite = new boolean[n];
        ArrayList<Integer> parcoursBrut = new ArrayList<>();
        dfsMST(0, visite, parcoursBrut, voisins); // 0 = dépôt

        System.out.println("Parcours préfixe sur le MST (avec allers/retours) :");
        for (int i = 0; i < parcoursBrut.size(); i++) {
            System.out.print(sommetsTSP.get(parcoursBrut.get(i)).getId());
            if (i < parcoursBrut.size() - 1) System.out.print(" -> ");
        }
        System.out.println();

        // 5) distance brute (parcours "avant shortcutting")
        int distanceBrute = 0;
        for (int i = 0; i < parcoursBrut.size() - 1; i++) {
            int u = parcoursBrut.get(i);
            int v = parcoursBrut.get(i + 1);
            int d = distanceEntreLocal(u, v);
            distanceBrute += d;
        }
        System.out.println("Distance si on parcourt toutes les arêtes du MST avec allers/retours : " +
                distanceBrute + " m");
        System.out.println("Théoriquement : 2 × poids(MST) = " + (2 * poidsMST) + " m\n");

        // 6) Shortcutting : on garde chaque sommet la première fois où il apparaît
        ArrayList<Integer> parcoursFinalIndices = new ArrayList<>();
        boolean[] dejaVisite = new boolean[n];

        for (int idx : parcoursBrut) {
            if (!dejaVisite[idx]) {
                parcoursFinalIndices.add(idx);
                dejaVisite[idx] = true;
            }
        }
        // ajoute le retour au dépôt (indice 0) si ce n'est pas déjà le cas
        if (parcoursFinalIndices.get(parcoursFinalIndices.size() - 1) != 0) {
            parcoursFinalIndices.add(0);
        }

        System.out.println("Parcours final après shortcutting :");
        for (int i = 0; i < parcoursFinalIndices.size(); i++) {
            System.out.print(sommetsTSP.get(parcoursFinalIndices.get(i)).getId());
            if (i < parcoursFinalIndices.size() - 1) System.out.print(" -> ");
        }
        System.out.println();

        // 7) détail des déplacements et distance totale de la tournée finale
        int distanceTotale = 0;
        System.out.println("Détails des déplacements de la tournée finale :");
        int cumul = 0;
        for (int i = 0; i < parcoursFinalIndices.size() - 1; i++) {
            int u = parcoursFinalIndices.get(i);
            int v = parcoursFinalIndices.get(i + 1);
            Sommet sa = sommetsTSP.get(u);
            Sommet sb = sommetsTSP.get(v);
            int d = distanceEntreLocal(u, v);
            distanceTotale += d;
            cumul += d;
            System.out.println("De " + sa.getId() +
                    " vers " + sb.getId() + " : " + d + " m [total : " + cumul + " m]");
        }

        System.out.println("\nDistance totale de la tournée (approche 2 simple) : " +
                distanceTotale + " m");

        System.out.println(
                "\n(À titre de comparaison sur ce petit exemple : " +
                        "on peut trouver à la main une tournée encore plus courte :");
        System.out.println(" D -> P1 -> P4 -> P3 -> P5 -> P2 -> P6 -> D (5400 m) )");


        System.out.println("===== FIN APPROCHE 2 SIMPLE =====\n");
    }
}
