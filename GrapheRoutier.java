import java.util.*;

public class GrapheRoutier {

    private int nbSommets;
    private final Map<String, Integer> nomVersId;
    private final List<String> idVersNom;
    private final List<List<Arete>> adjacence;

    // Pour HO3 mémoriser quelles paires (u,v) sont des "rues double sens une seule voie"
    private final Set<String> aretesDoubleSensUneVoie;

    public GrapheRoutier() {
        this.nbSommets = 0;
        this.nomVersId = new HashMap<>();
        this.idVersNom = new ArrayList<>();
        this.adjacence = new ArrayList<>();
        this.aretesDoubleSensUneVoie = new HashSet<>();
    }

    public int ajouterSommet(String nomSommet) {
        int id = nbSommets;
        nbSommets++;
        nomVersId.put(nomSommet, id);
        idVersNom.add(nomSommet);
        adjacence.add(new ArrayList<>());
        return id;
    }

    // Rue à double sens (HO1 ou "double sens une seule voie" HO3)
    public void ajouterAreteNonOriente(String nomU, String nomV, double longueur) {
        Integer u = nomVersId.get(nomU);
        Integer v = nomVersId.get(nomV);
        if (u == null || v == null) {
            throw new IllegalArgumentException("Sommet inconnu : " + nomU + " ou " + nomV);
        }
        adjacence.get(u).add(new Arete(v, longueur));
        adjacence.get(v).add(new Arete(u, longueur)); // deux sens pour Dijkstra

        //paire (u, v) correspond à une "rue double sens une seule voie"
        aretesDoubleSensUneVoie.add(cleAreteNonOriente(u, v));
    }

    // Rue orientée (HO2 / HO3)
    public void ajouterArcOriente(String nomU, String nomV, double longueur) {
        Integer u = nomVersId.get(nomU);
        Integer v = nomVersId.get(nomV);
        if (u == null || v == null) {
            throw new IllegalArgumentException("Sommet inconnu : " + nomU + " ou " + nomV);
        }
        adjacence.get(u).add(new Arete(v, longueur)); // sens unique u -> v
    }

    public double obtenirLongueurArete(int u, int v) {
        for (Arete a : adjacence.get(u)) {
            if (a.getSommetVoisin() == v) {
                return a.getLongueur();
            }
        }
        throw new IllegalArgumentException(
                "Aucune arête/arc direct entre " + getNomSommet(u) + " et " + getNomSommet(v)
        );
    }

    private String cleAreteNonOriente(int u, int v) {
        return (u < v) ? (u + "-" + v) : (v + "-" + u);
    }

    /** Vrai si (u, v) correspond à une "rue double sens une seule voie" (déclarée via EDGES). */
    public boolean estAreteDoubleSensUneVoie(int u, int v) {
        return aretesDoubleSensUneVoie.contains(cleAreteNonOriente(u, v));
    }

    public int getNbSommets() {
        return nbSommets;
    }

    public String getNomSommet(int id) {
        return idVersNom.get(id);
    }

    public Integer getIdSommet(String nom) {
        return nomVersId.get(nom);
    }

    public List<Arete> getVoisins(int idSommet) {
        return adjacence.get(idSommet);
    }

    public double getLongueurArete(int idU, int idV) {
        // On parcourt la liste des voisins de idU pour trouver idV
        for (Arete a : getVoisins(idU)) {
            if (a.getSommetVoisin() == idV) {
                return a.getLongueur();
            }
        }
        // Si on trouve pas, on regarde dans l'autre sens
        for (Arete a : getVoisins(idV)) {
            if (a.getSommetVoisin() == idU) {
                return a.getLongueur();
            }
        }
        throw new IllegalArgumentException(
                "Aucune arête entre " + getNomSommet(idU) + " et " + getNomSommet(idV));
    }

}
