import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LecteurDeFichierTourneeHO3 {

    public static InstanceTourneeHO3 lireInstanceTourneeHO3(String nomFichier) throws IOException {

        GrapheRoutier graphe = null;
        Integer idDepot = null;
        List<EncombrantPourTourneeHO3> listeEncombrants = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(nomFichier))) {
            String ligne;

            while ((ligne = br.readLine()) != null) {
                ligne = ligne.trim();
                if (ligne.isEmpty() || ligne.startsWith("#")) continue;

                String[] morceaux = ligne.split("\\s+");

                if (morceaux[0].equalsIgnoreCase("NODES")) {
                    int nb = Integer.parseInt(morceaux[1]);
                    graphe = new GrapheRoutier();
                    for (int i = 0; i < nb; i++) {
                        String nomSommet = br.readLine();
                        if (nomSommet == null) {
                            throw new IOException("Fichier tronqué dans la section NODES");
                        }
                        nomSommet = nomSommet.trim();
                        if (nomSommet.isEmpty()) {
                            i--;
                            continue;
                        }
                        graphe.ajouterSommet(nomSommet);
                    }

                } else if (morceaux[0].equalsIgnoreCase("DEPOT")) {
                    if (graphe == null) {
                        throw new IllegalStateException("NODES doit apparaître avant DEPOT");
                    }
                    String nomDepot = morceaux[1];
                    Integer id = graphe.getIdSommet(nomDepot);
                    if (id == null) {
                        throw new IllegalArgumentException("DEPOT inconnu : " + nomDepot);
                    }
                    idDepot = id;

                } else if (morceaux[0].equalsIgnoreCase("EDGES")) {
                    if (graphe == null) {
                        throw new IllegalStateException("NODES doit apparaître avant EDGES");
                    }
                    int nbEdges = Integer.parseInt(morceaux[1]);
                    int compteur = 0;
                    while (compteur < nbEdges) {
                        String l = br.readLine();
                        if (l == null) {
                            throw new IOException("Fichier tronqué dans la section EDGES");
                        }
                        l = l.trim();
                        if (l.isEmpty() || l.startsWith("#")) continue;
                        String[] p = l.split("\\s+");
                        String nomU = p[0];
                        String nomV = p[1];
                        double longueur = Double.parseDouble(p[2]);
                        graphe.ajouterAreteNonOriente(nomU, nomV, longueur);
                        compteur++;
                    }

                } else if (morceaux[0].equalsIgnoreCase("ARCS")) {
                    if (graphe == null) {
                        throw new IllegalStateException("NODES doit apparaître avant ARCS");
                    }
                    int nbArcs = Integer.parseInt(morceaux[1]);
                    int compteur = 0;
                    while (compteur < nbArcs) {
                        String l = br.readLine();
                        if (l == null) {
                            throw new IOException("Fichier tronqué dans la section ARCS");
                        }
                        l = l.trim();
                        if (l.isEmpty() || l.startsWith("#")) continue;
                        String[] p = l.split("\\s+");
                        String nomU = p[0];
                        String nomV = p[1];
                        double longueur = Double.parseDouble(p[2]);
                        graphe.ajouterArcOriente(nomU, nomV, longueur);
                        compteur++;
                    }

                } else if (morceaux[0].equalsIgnoreCase("ENCOMBRANT")) {
                    if (graphe == null) {
                        throw new IllegalStateException("NODES doit apparaître avant ENCOMBRANT");
                    }
                    String nomU = morceaux[1];
                    String nomV = morceaux[2];
                    double position = Double.parseDouble(morceaux[3]);

                    Integer idU = graphe.getIdSommet(nomU);
                    Integer idV = graphe.getIdSommet(nomV);
                    if (idU == null || idV == null) {
                        throw new IllegalArgumentException("Sommet inconnu dans ENCOMBRANT : "
                                + nomU + " ou " + nomV);
                    }

                    // On regarde si (U,V) est une "rue double sens une seule voie"
                    boolean estDoubleSensUneSeuleVoie = graphe.estAreteDoubleSensUneVoie(idU, idV);

                    // Si ce n'est pas le cas, on considérera que c'est un encombrant
                    // sur un arc orienté U -> V (comme en HO2).
                    listeEncombrants.add(
                            new EncombrantPourTourneeHO3(nomU, nomV, position, estDoubleSensUneSeuleVoie)
                    );
                }
            }
        }

        if (graphe == null) {
            throw new IllegalStateException("Section NODES manquante");
        }
        if (idDepot == null) {
            throw new IllegalStateException("DEPOT non défini");
        }
        if (listeEncombrants.isEmpty()) {
            throw new IllegalStateException("Aucun ENCOMBRANT défini pour cette tournée HO3.");
        }

        return new InstanceTourneeHO3(graphe, idDepot, listeEncombrants);
    }
}
