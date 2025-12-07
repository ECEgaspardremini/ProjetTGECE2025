import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LecteurDeFichierP2HO1 {

    public static class InstanceP2HO1 {
        private final GrapheRoutier graphe;
        private final int idDepot;

        public InstanceP2HO1(GrapheRoutier graphe, int idDepot) {
            this.graphe = graphe;
            this.idDepot = idDepot;
        }

        public GrapheRoutier getGraphe() {
            return graphe;
        }

        public int getIdDepot() {
            return idDepot;
        }
    }

    public static InstanceP2HO1 lireInstance(String nomFichier) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(nomFichier))) {

            GrapheRoutier graphe = new GrapheRoutier();
            String ligne;

            // NODES
            ligne = br.readLine();
            if (ligne == null || !ligne.startsWith("NODES")) {
                throw new IOException("Fichier invalide : première ligne doit commencer par 'NODES'.");
            }

            int nbSommets = Integer.parseInt(ligne.split("\\s+")[1]);
            ArrayList<String> noms = new ArrayList<>();

            for (int i = 0; i < nbSommets; i++) {
                ligne = br.readLine();
                if (ligne == null || ligne.trim().isEmpty()) {
                    throw new IOException("Nombre de sommets incohérent.");
                }
                String nom = ligne.trim();
                noms.add(nom);
                graphe.ajouterSommet(nom);
            }

            // DEPOT
            ligne = br.readLine();
            while (ligne != null && ligne.trim().isEmpty()) {
                ligne = br.readLine();
            }
            if (ligne == null || !ligne.startsWith("DEPOT")) {
                throw new IOException("Ligne DEPOT manquante.");
            }
            String[] departs = ligne.split("\\s+");
            if (departs.length < 2) {
                throw new IOException("Format DEPOT incorrect.");
            }
            String nomDepot = departs[1];
            Integer idDepot = graphe.getIdSommet(nomDepot);
            if (idDepot == null) {
                throw new IOException("DEPOT inconnu : " + nomDepot);
            }

            // EDGES
            ligne = br.readLine();
            while (ligne != null && ligne.trim().isEmpty()) {
                ligne = br.readLine();
            }
            if (ligne == null || !ligne.startsWith("EDGES")) {
                throw new IOException("Ligne EDGES manquante.");
            }
            int nbAretes = Integer.parseInt(ligne.split("\\s+")[1]);

            for (int i = 0; i < nbAretes; i++) {
                ligne = br.readLine();
                if (ligne == null || ligne.trim().isEmpty()) {
                    throw new IOException("Nombre d'arêtes incohérent.");
                }
                String[] tokens = ligne.trim().split("\\s+");
                if (tokens.length != 3) {
                    throw new IOException("Format d'arête invalide : " + ligne);
                }
                String nomU = tokens[0];
                String nomV = tokens[1];
                double longueur = Double.parseDouble(tokens[2]);

                // HO1 : rue à double sens, une seule voie
                graphe.ajouterAreteNonOriente(nomU, nomV, longueur);
            }

            return new InstanceP2HO1(graphe, idDepot);
        }
    }
}
