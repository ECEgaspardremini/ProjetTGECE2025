import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ProgrammeP2HO2Cas2 {

    private static void calculerDegres(GrapheRoutier g, int[] degIn, int[] degOut) {
        int n = g.getNbSommets();
        for (int u = 0; u < n; u++) {
            for (Arete a : g.getVoisins(u)) {
                int v = a.getSommetVoisin();
                degOut[u]++;
                degIn[v]++;
            }
        }
    }

    // Hierholzer orienté (comme dans le cas 1).
    private static List<Integer> hierholzerCircuit(GrapheRoutier g, int source) {
        int n = g.getNbSommets();

        List<List<Integer>> indicesVoisins = new ArrayList<>();
        for (int u = 0; u < n; u++) {
            List<Integer> list = new ArrayList<>();
            int taille = g.getVoisins(u).size();
            for (int i = 0; i < taille; i++) {
                list.add(i);
            }
            indicesVoisins.add(list);
        }

        Stack<Integer> pile = new Stack<>();
        List<Integer> circuit = new ArrayList<>();
        pile.push(source);

        while (!pile.isEmpty()) {
            int u = pile.peek();
            List<Integer> liste = indicesVoisins.get(u);

            if (liste.isEmpty()) {
                circuit.add(u);
                pile.pop();
            } else {
                int idx = liste.remove(liste.size() - 1);
                Arete a = g.getVoisins(u).get(idx);
                int v = a.getSommetVoisin();
                pile.push(v);
            }
        }

        List<Integer> resultat = new ArrayList<>();
        for (int i = circuit.size() - 1; i >= 0; i--) {
            resultat.add(circuit.get(i));
        }
        return resultat;
    }

    public static void executerCas2HO2(String nomFichier) {
        try {
            // ⚠️ Ici on lit bien un fichier HO2 (ARCS)
            LecteurDeFichierP2HO2.InstanceP2HO2 instance =
                    LecteurDeFichierP2HO2.lireInstance(nomFichier);
            GrapheRoutier g = instance.getGraphe();

            int n = g.getNbSommets();
            int[] degIn = new int[n];
            int[] degOut = new int[n];
            calculerDegres(g, degIn, degOut);

            int s = -1; // départ
            int t = -1; // arrivée

            for (int v = 0; v < n; v++) {
                if (degOut[v] == degIn[v] + 1) s = v;
                else if (degIn[v] == degOut[v] + 1) t = v;
            }

            if (s == -1 || t == -1) {
                System.out.println("Le graphe ne vérifie pas les conditions d'un chemin eulérien orienté (2 sommets déséquilibrés).");
                return;
            }

            System.out.println("Sommet de départ (s) : " + g.getNomSommet(s));
            System.out.println("Sommet d'arrivée (t) : " + g.getNomSommet(t));

            // On ajoute un arc artificiel t -> s (coût 0) pour transformer en cas 1
            g.ajouterArcOriente(g.getNomSommet(t), g.getNomSommet(s), 0.0);

            // Circuit eulérien sur ce graphe modifié
            List<Integer> circuit = hierholzerCircuit(g, s);

            // On cherche l’endroit où l’on passe de t à s (arc artificiel).
            int indexArc = -1;
            for (int i = 0; i + 1 < circuit.size(); i++) {
                if (circuit.get(i) == t && circuit.get(i + 1) == s) {
                    indexArc = i;      // index de t
                    break;
                }
            }

            if (indexArc == -1) {
                System.out.println("Impossible de retrouver l'arc artificiel t->s dans le circuit.");
                return;
            }

            // Construire le chemin linéaire s -> ... -> t
            List<Integer> chemin = new ArrayList<>();

            // 1) de s = circuit[indexArc+1] jusqu'à la fin du circuit
            for (int i = indexArc + 1; i < circuit.size(); i++) {
                chemin.add(circuit.get(i));
            }
            // 2) puis de circuit[1] jusqu'à t = circuit[indexArc]
            for (int i = 1; i <= indexArc; i++) {
                chemin.add(circuit.get(i));
            }

            // Calcul de la longueur en ignorant l'arc artificiel (t->s, coût 0)
            double longueurTotale = 0.0;
            for (int i = 0; i + 1 < chemin.size(); i++) {
                int u = chemin.get(i);
                int v = chemin.get(i + 1);
                longueurTotale += g.getLongueurArete(u, v); // 0 pour l'arc t->s s'il était encore présent
            }

            System.out.println(" P2 / HO2 / Cas 2 : Deux sommets déséquilibrés (chemin eulérien orienté) ");
            System.out.println("Chemin eulérien :");
            for (int i = 0; i < chemin.size(); i++) {
                int id = chemin.get(i);
                System.out.print(g.getNomSommet(id));
                if (i + 1 < chemin.size()) System.out.print(" -> ");
            }
            System.out.println();
            System.out.println("Longueur totale de la tournée : " + longueurTotale + " m");
            System.out.println("\n");

        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
