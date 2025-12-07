import java.util.ArrayList;

public class Chemin {

    private ArrayList<Sommet> sommets;
    private int distanceTotale;

    public Chemin(ArrayList<Sommet> sommets, int distanceTotale) {
        this.sommets = sommets;
        this.distanceTotale = distanceTotale;
    }

    public ArrayList<Sommet> getSommets() {
        return sommets;
    }

    public int getDistanceTotale() {
        return distanceTotale;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < sommets.size(); i++) {
            sb.append(sommets.get(i).getId());
            if (i < sommets.size() - 1) {
                sb.append(" -> ");
            }
        }
        sb.append(" (").append(distanceTotale).append(" m)");
        return sb.toString();
    }

    public String toStringDetaille(Graphe graphe, int[][] matriceDistances) {
        StringBuilder sb = new StringBuilder();

        if (sommets.isEmpty()) {
            return "(chemin vide)";
        }

        int total = 0;


        for (int i = 0; i < sommets.size() - 1; i++) {
            Sommet a = sommets.get(i);
            Sommet b = sommets.get(i + 1);

            int ia = graphe.getIndiceSommet(a);
            int ib = graphe.getIndiceSommet(b);
            int d = matriceDistances[ia][ib];

            total += d;

            sb.append(a.getId())
                    .append(" -> ")
                    .append(b.getId())
                    .append(" (")
                    .append(d)
                    .append(" m)");

            if (i < sommets.size() - 2) {
                sb.append(", ");
            }
        }

        sb.append(" [total ").append(total).append(" m]");
        return sb.toString();
    }
}
