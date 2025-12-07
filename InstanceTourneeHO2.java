import java.util.List;

public class InstanceTourneeHO2 {

    private final GrapheRoutier graphe;
    private final int idDepot;
    private final List<EncombrantOrienteHO2> listeEncombrants;

    public InstanceTourneeHO2(GrapheRoutier graphe,
                              int idDepot,
                              List<EncombrantOrienteHO2> listeEncombrants) {
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

    public List<EncombrantOrienteHO2> getListeEncombrants() {
        return listeEncombrants;
    }
}
