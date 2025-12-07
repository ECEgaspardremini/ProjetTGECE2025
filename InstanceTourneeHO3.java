import java.util.List;

public class InstanceTourneeHO3 {

    private final GrapheRoutier graphe;
    private final int idDepot;
    private final List<EncombrantPourTourneeHO3> listeEncombrants;

    public InstanceTourneeHO3(GrapheRoutier graphe,
                              int idDepot,
                              List<EncombrantPourTourneeHO3> listeEncombrants) {
        this.graphe = graphe;
        this.idDepot = idDepot;
        this.listeEncombrants = listeEncombrants;
    }

    public GrapheRoutier getGraphe() {
        return graphe;
    }

    public int getIdDepot() {
        return idDepot;
    }

    public List<EncombrantPourTourneeHO3> getListeEncombrants() {
        return listeEncombrants;
    }
}
