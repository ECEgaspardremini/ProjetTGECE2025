import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LecteurDeFichierTourneeHO1 {

    public static InstanceTourneeHO1 lireInstanceTourneeHO1(String nomFichier) throws IOException {

        GrapheRoutier graphe = null;
        Integer idDepot = null;
        List<Integer> listeCarrefoursEncombrants = new ArrayList<>();
        List<EncombrantPourTournee> listeEncombrantsPourTournee = new ArrayList<>();

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

                } else if (morceaux[0].equalsIgnoreCase("ENCOMBRANT")) {
                    // Format : ENCOMBRANT u v position
                    if (graphe == null) {
                        throw new IllegalStateException("NODES doit apparaître avant ENCOMBRANT");
                    }
                    String nomU = morceaux[1];
                    String nomV = morceaux[2];
                    double position = Double.parseDouble(morceaux[3]);

                    Integer idU = graphe.getIdSommet(nomU);
                    Integer idV = graphe.getIdSommet(nomV);

                    if (idU == null || idV == null) {
                        throw new IllegalArgumentException(
                                "Sommet inconnu dans ENCOMBRANT : " + nomU + " ou " + nomV);
                    }

                    // Longueur totale de la rue
                    double longueurRue = graphe.getLongueurArete(idU, idV);

                    double distanceDepuisU = position;
                    double distanceDepuisV = longueurRue - position;

                    // Carrefour auquel on rattache l'encombrant (le plus proche)
                    int idAssocie;
                    double distanceDepuisCarrefour;
                    if (distanceDepuisU <= distanceDepuisV) {
                        idAssocie = idU;
                        distanceDepuisCarrefour = distanceDepuisU;
                    } else {
                        idAssocie = idV;
                        distanceDepuisCarrefour = distanceDepuisV;
                    }

                    // On ajoute le carrefour associé dans la liste à visiter (si pas déjà présent)
                    if (!listeCarrefoursEncombrants.contains(idAssocie)) {
                        listeCarrefoursEncombrants.add(idAssocie);
                    }

                    // On mémorise l'encombrant pour la tournée (infos pour les boucles locales)
                    listeEncombrantsPourTournee.add(
                            new EncombrantPourTournee(idAssocie, nomU, nomV, distanceDepuisCarrefour)
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

        // On enlève le dépôt des "carrefours clients" s'il y apparaît par accident
        final int depotFinal = idDepot;
        listeCarrefoursEncombrants.removeIf(id -> id == depotFinal);


        if (listeCarrefoursEncombrants.isEmpty()) {
            throw new IllegalStateException("Aucun encombrant défini (aucune ligne ENCOMBRANT).");
        }

        int[] ids = new int[listeCarrefoursEncombrants.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = listeCarrefoursEncombrants.get(i);
        }

        return new InstanceTourneeHO1(graphe, idDepot, ids, listeEncombrantsPourTournee);
    }
}
