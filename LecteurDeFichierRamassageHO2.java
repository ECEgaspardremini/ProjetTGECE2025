import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LecteurDeFichierRamassageHO2 {
    public static InstanceRamassage lireInstanceDepuisFichierHO2(String nomFichier) throws IOException {

        GrapheRoutier graphe = null;
        Integer idDepot = null;
        String nomEncomU = null;
        String nomEncomV = null;
        double positionDepuisU = 0.0;

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
                        if (l.isEmpty() || l.startsWith("#")) {
                            continue;
                        }
                        String[] p = l.split("\\s+");
                        String nomU = p[0];
                        String nomV = p[1];
                        double longueur = Double.parseDouble(p[2]);
                        graphe.ajouterArcOriente(nomU, nomV, longueur);
                        compteur++;
                    }

                } else if (morceaux[0].equalsIgnoreCase("ENCOMBRANT")) {
                    nomEncomU = morceaux[1];
                    nomEncomV = morceaux[2];
                    positionDepuisU = Double.parseDouble(morceaux[3]);
                }
            }
        }

        if (graphe == null) {
            throw new IllegalStateException("Section NODES manquante");
        }
        if (idDepot == null) {
            throw new IllegalStateException("DEPOT non défini");
        }
        if (nomEncomU == null || nomEncomV == null) {
            throw new IllegalStateException("ENCOMBRANT non défini");
        }

        Integer idU = graphe.getIdSommet(nomEncomU);
        Integer idV = graphe.getIdSommet(nomEncomV);
        if (idU == null || idV == null) {
            throw new IllegalArgumentException(
                    "Sommet de l'encombrant inconnu : " + nomEncomU + " ou " + nomEncomV
            );
        }

        double longueurRue = graphe.obtenirLongueurArete(idU, idV); // longueur de l'arc u -> v

        if (positionDepuisU < 0 || positionDepuisU > longueurRue) {
            throw new IllegalArgumentException(
                    "Position de l'encombrant hors de la rue (0.." + longueurRue + ")"
            );
        }

        EncombrantSurRue encombrant = new EncombrantSurRue(idU, idV, positionDepuisU, longueurRue);
        return new InstanceRamassage(graphe, idDepot, encombrant);
    }
}
