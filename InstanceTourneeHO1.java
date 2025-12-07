import java.util.List;

public class InstanceTourneeHO1 {

    private final GrapheRoutier graphe;
    private final int idDepot;
    private final int[] idsCarrefoursEncombrants;
    private final List<EncombrantPourTournee> encombrantsPourTournee;

    public InstanceTourneeHO1(GrapheRoutier graphe,
                              int idDepot,
                              int[] idsCarrefoursEncombrants,
                              List<EncombrantPourTournee> encombrantsPourTournee) {
        this.graphe = graphe;
        this.idDepot = idDepot;
        this.idsCarrefoursEncombrants = idsCarrefoursEncombrants;
        this.encombrantsPourTournee = encombrantsPourTournee;
    }

    public GrapheRoutier getGraphe() {
        return graphe;
    }

    public int getIdDepot() {
        return idDepot;
    }

    public int[] getIdsCarrefoursEncombrants() {
        return idsCarrefoursEncombrants;
    }

    public List<EncombrantPourTournee> getEncombrantsPourTournee() {
        return encombrantsPourTournee;
    }
}
