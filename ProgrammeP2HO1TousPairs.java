import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ProgrammeP2HO1TousPairs {

    // Représente une "rue" unique pour l'Euler (u, v) non orientée
    private static class RueEulerienne {
        int u;
        int v;
        boolean utilisee;

        RueEulerienne(int u, int v) {
            this.u = u;
            this.v = v;
            this.utilisee = false;
        }

        int autreExtremite(int x) {
            return (x == u) ? v : u;
        }
    }

    //Construit la liste des rues (non orientées) à partir du graphe HO1, en évitant de doubler les arêtes u-v / v-u.

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
                    if (u < v) {
                        rues.add(new RueEulerienne(u, v));
                    } else {
                        rues.add(new RueEulerienne(v, u));
                    }
                }
            }
        }
        return rues;
    }

    // Vérifie que tous les degrés sont pairs.
    private static boolean tousDegresPairs(GrapheRoutier graphe) {
        int n = graphe.getNbSommets();
        for (int u = 0; u < n; u++) {
            int deg = graphe.getVoisins(u).size();
            if (deg % 2 != 0) {
                return false;
            }
        }
        return true;
    }

    // Calcule un circuit eulérien avec Hierholzer, en partant du dépôt.
    public static void executerTousPairs(String nomFichier) {
        try {
            LecteurDeFichierP2HO1.InstanceP2HO1 instance =
                    LecteurDeFichierP2HO1.lireInstance(nomFichier);

            GrapheRoutier graphe = instance.getGraphe();
            int idDepot = instance.getIdDepot();

            if (!tousDegresPairs(graphe)) {
                System.out.println("Le graphe n'est pas eulérien : tous les degrés ne sont pas pairs.");
                return;
            }

            List<RueEulerienne> rues = construireRues(graphe);
            int nbRues = rues.size();

            // Pour chaque sommet : liste des indices de rues incidentes
            List<List<Integer>> adjRues = new ArrayList<>();
            for (int i = 0; i < graphe.getNbSommets(); i++) {
                adjRues.add(new ArrayList<>());
            }
            for (int i = 0; i < nbRues; i++) {
                RueEulerienne r = rues.get(i);
                adjRues.get(r.u).add(i);
                adjRues.get(r.v).add(i);
            }

            // Hierholzer : pile pour le parcours
            Stack<Integer> pile = new Stack<>();
            List<Integer> circuit = new ArrayList<>();
            pile.push(idDepot);

            while (!pile.isEmpty()) {
                int u = pile.peek();
                List<Integer> listeRues = adjRues.get(u);

                // Cherche une rue encore non utilisée
                int idxRueNonUtilisee = -1;
                while (!listeRues.isEmpty()) {
                    int idRue = listeRues.remove(listeRues.size() - 1);
                    if (!rues.get(idRue).utilisee) {
                        idxRueNonUtilisee = idRue;
                        break;
                    }
                }

                if (idxRueNonUtilisee == -1) {
                    // Plus de rue à partir de u -> on remonte
                    circuit.add(u);
                    pile.pop();
                } else {
                    RueEulerienne r = rues.get(idxRueNonUtilisee);
                    r.utilisee = true;
                    int v = r.autreExtremite(u);
                    pile.push(v);
                }
            }

            System.out.println(" P2 / HO1 / Cas 1 : Tous les sommets pairs");
            System.out.println("Circuit eulérien (ordre des carrefours) :");
            for (int i = circuit.size() - 1; i >= 0; i--) {
                int id = circuit.get(i);
                System.out.print(graphe.getNomSommet(id));
                if (i > 0) System.out.print(" -> ");
            }
            System.out.println("\n(Chaque rue est parcourue exactement une fois.)");
            System.out.println("\n");

        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
