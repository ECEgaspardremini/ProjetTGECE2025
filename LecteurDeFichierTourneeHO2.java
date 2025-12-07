import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LecteurDeFichierTourneeHO2 {
    public static InstanceTourneeHO2 lireInstanceTourneeHO2(String nomFichier) throws IOException {

        GrapheRoutier graphe = null;
        Integer idDepot = null;
        List<EncombrantOrienteHO2> listeEncombrants = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(nomFichier))) {
            String ligne;

            while ((ligne = br.readLine()) != null) {
                ligne = ligne.trim();

                if (ligne.isEmpty() || ligne.startsWith("#")) {
                    continue;
                }

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
                    // DEPOT T
                    if (graphe == null) {
                        throw new IllegalStateException("NODES doit apparaître avant DEPOT");
                    }
                    String nomDepot = morceaux[1];
                    Integer id = graphe.getIdSommet(nomDepot);
                    if (id == null) {
                        throw new IllegalArgumentException("DEPOT inconnu : " + nomDepot);
                    }
                    idDepot = id;

                } else if (morceaux[0].equalsIgnoreCase("ARCS")) {
                    // ARCS nbArcs
                    // nbArcs lignes : u v longueur
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

                        // Méthode ajoutant un arc orienté u -> v
                        graphe.ajouterArcOriente(nomU, nomV, longueur);
                        compteur++;
                    }

                } else if (morceaux[0].equalsIgnoreCase("ENCOMBRANT")) {
                    // ENCOMBRANT u v position
                    if (graphe == null) {
                        throw new IllegalStateException("NODES doit apparaître avant ENCOMBRANT");
                    }
                    String nomU = morceaux[1];
                    String nomV = morceaux[2];
                    double position = Double.parseDouble(morceaux[3]);
                    listeEncombrants.add(new EncombrantOrienteHO2(nomU, nomV, position));
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
            throw new IllegalStateException("Aucun ENCOMBRANT défini pour cette tournée.");
        }

        return new InstanceTourneeHO2(graphe, idDepot, listeEncombrants);
    }
}
