import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ProgrammeP2HO1DeuxImpairs {

    private static class RueEulerienne {
        int u, v;
        boolean utilisee;
        RueEulerienne(int u, int v) { this.u = u; this.v = v; this.utilisee = false; }
        int autreExtremite(int x) { return (x == u) ? v : u; }
    }

    private static List<RueEulerienne> construireRues(GrapheRoutier graphe) {
        List<RueEulerienne> rues = new ArrayList<>();
        int n = graphe.getNbSommets();
        boolean[][] dejaVue = new boolean[n][n];

        for (int u = 0; u < n; u++) {
            for (Arete a : graphe.getVoisins(u)) {
                int v = a.getSommetVoisin();
                if (!dejaVue[u][v]) {
                    dejaVue[u][v] = true;
                    dejaVue[v][u] = true;
                    if (u < v) rues.add(new RueEulerienne(u, v));
                    else rues.add(new RueEulerienne(v, u));
                }
            }
        }
        return rues;
    }

    private static List<Integer> trouverSommetImpairs(GrapheRoutier graphe) {
        List<Integer> impairs = new ArrayList<>();
        int n = graphe.getNbSommets();
        for (int u = 0; u < n; u++) {
            int deg = graphe.getVoisins(u).size();
            if (deg % 2 != 0) impairs.add(u);
        }
        return impairs;
    }

    public static void executerDeuxImpairs(String nomFichier) {
        try {
            LecteurDeFichierP2HO1.InstanceP2HO1 instance =
                    LecteurDeFichierP2HO1.lireInstance(nomFichier);
            GrapheRoutier graphe = instance.getGraphe();

            List<Integer> impairs = trouverSommetImpairs(graphe);
            if (impairs.size() != 2) {
                System.out.println("Ce cas nécessite exactement 2 sommets de degré impair.");
                System.out.println("Ici, on en trouve : " + impairs.size());
                return;
            }

            int depart = impairs.get(0); // on peut aussi choisir le dépôt si on veut adapter
            System.out.println("Sommet de départ (impair) : " + graphe.getNomSommet(depart));
            System.out.println("Sommet d'arrivée (impair) : " + graphe.getNomSommet(impairs.get(1)));

            List<RueEulerienne> rues = construireRues(graphe);
            int nbRues = rues.size();

            List<List<Integer>> adjRues = new ArrayList<>();
            for (int i = 0; i < graphe.getNbSommets(); i++) {
                adjRues.add(new ArrayList<>());
            }
            for (int i = 0; i < nbRues; i++) {
                RueEulerienne r = rues.get(i);
                adjRues.get(r.u).add(i);
                adjRues.get(r.v).add(i);
            }

            Stack<Integer> pile = new Stack<>();
            List<Integer> chemin = new ArrayList<>();
            pile.push(depart);

            while (!pile.isEmpty()) {
                int u = pile.peek();
                List<Integer> listeRues = adjRues.get(u);

                int idxRueNonUtilisee = -1;
                while (!listeRues.isEmpty()) {
                    int idRue = listeRues.remove(listeRues.size() - 1);
                    if (!rues.get(idRue).utilisee) {
                        idxRueNonUtilisee = idRue;
                        break;
                    }
                }

                if (idxRueNonUtilisee == -1) {
                    chemin.add(u);
                    pile.pop();
                } else {
                    RueEulerienne r = rues.get(idxRueNonUtilisee);
                    r.utilisee = true;
                    int v = r.autreExtremite(u);
                    pile.push(v);
                }
            }

            System.out.println("P2 / HO1 / Cas 2 : Deux sommets impairs");
            System.out.println("Chemin eulérien (on parcourt chaque rue une fois) :");
            for (int i = chemin.size() - 1; i >= 0; i--) {
                int id = chemin.get(i);
                System.out.print(graphe.getNomSommet(id));
                if (i > 0) System.out.print(" -> ");
            }
            System.out.println("\n\n");

        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
