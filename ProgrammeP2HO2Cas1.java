import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ProgrammeP2HO2Cas1 {

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

    private static boolean estEulerienOriente(int[] degIn, int[] degOut) {
        if (degIn.length != degOut.length) return false;
        for (int i = 0; i < degIn.length; i++) {
            if (degIn[i] != degOut[i]) return false;
        }
        return true;
    }

    //Hierholzer orienté : circuit eulérien sur arcs
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

    public static void executerCas1HO2(String nomFichier) {
        try {
            LecteurDeFichierP2HO2.InstanceP2HO2 instance =
                    LecteurDeFichierP2HO2.lireInstance(nomFichier);

            GrapheRoutier g = instance.getGraphe();
            int depot = instance.getIdDepot();
            int n = g.getNbSommets();

            int[] degIn = new int[n];
            int[] degOut = new int[n];
            calculerDegres(g, degIn, degOut);

            if (!estEulerienOriente(degIn, degOut)) {
                System.out.println("Le graphe n'est pas eulérien orienté (degIn != degOut).");
                return;
            }

            List<Integer> circuit = hierholzerCircuit(g, depot);

            double longueurTotale = 0.0;
            for (int i = 0; i + 1 < circuit.size(); i++) {
                int u = circuit.get(i);
                int v = circuit.get(i + 1);
                longueurTotale += g.getLongueurArete(u, v);
            }

            System.out.println(" P2 / HO2 / Cas 1 : Circuit eulérien orienté ");
            System.out.println("Circuit :");
            for (int i = 0; i < circuit.size(); i++) {
                int id = circuit.get(i);
                System.out.print(g.getNomSommet(id));
                if (i + 1 < circuit.size()) System.out.print(" -> ");
            }
            System.out.println();
            System.out.println("Longueur totale : " + longueurTotale + " m");
            System.out.println("\n");

        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
