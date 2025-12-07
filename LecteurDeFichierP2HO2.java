import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LecteurDeFichierP2HO2 {

    // Conteneur : graphe + id du dépôt
    public static class InstanceP2HO2 {
        private final GrapheRoutier graphe;
        private final int idDepot;

        public InstanceP2HO2(GrapheRoutier graphe, int idDepot) {
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

    public static InstanceP2HO2 lireInstance(String nomFichier) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(nomFichier))) {

            GrapheRoutier graphe = new GrapheRoutier();
            String ligne;

            // ===== NODES =====
            ligne = br.readLine();
            if (ligne == null || !ligne.startsWith("NODES")) {
                throw new IOException("Fichier invalide : première ligne doit commencer par 'NODES'.");
            }

            int nbSommets = Integer.parseInt(ligne.split("\\s+")[1]);

            for (int i = 0; i < nbSommets; i++) {
                ligne = br.readLine();
                if (ligne == null || ligne.trim().isEmpty()) {
                    throw new IOException("Nombre de sommets incohérent.");
                }
                String nom = ligne.trim();
                graphe.ajouterSommet(nom);
            }

            // ===== DEPOT =====
            ligne = br.readLine();
            while (ligne != null && ligne.trim().isEmpty()) {
                ligne = br.readLine();
            }
            if (ligne == null || !ligne.startsWith("DEPOT")) {
                throw new IOException("Ligne DEPOT manquante.");
            }

            String[] dep = ligne.split("\\s+");
            if (dep.length < 2) {
                throw new IOException("Format DEPOT incorrect.");
            }
            String nomDepot = dep[1];
            Integer idDepot = graphe.getIdSommet(nomDepot);
            if (idDepot == null) {
                throw new IOException("DEPOT inconnu : " + nomDepot);
            }

            // ===== ARCS (graphe orienté) =====
            ligne = br.readLine();
            while (ligne != null && ligne.trim().isEmpty()) {
                ligne = br.readLine();
            }
            if (ligne == null || !ligne.startsWith("ARCS")) {
                throw new IOException("Ligne ARCS manquante (HO2 = graphe orienté).");
            }

            int nbArcs = Integer.parseInt(ligne.split("\\s+")[1]);

            for (int i = 0; i < nbArcs; i++) {
                ligne = br.readLine();
                if (ligne == null || ligne.trim().isEmpty()) {
                    throw new IOException("Nombre d'arcs incohérent.");
                }
                String[] tokens = ligne.trim().split("\\s+");
                if (tokens.length != 3) {
                    throw new IOException("Format d'arc invalide : " + ligne);
                }

                String nomU = tokens[0];
                String nomV = tokens[1];
                double longueur = Double.parseDouble(tokens[2]);

                // HO2 : rue orientée U -> V
                graphe.ajouterArcOriente(nomU, nomV, longueur);
            }

            return new InstanceP2HO2(graphe, idDepot);
        }
    }
}
