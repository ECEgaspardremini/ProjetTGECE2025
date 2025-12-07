import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GrapheSecteurs {


    private Map<Secteur, List<Secteur>> adj;

//constructeurs
    public GrapheSecteurs() {
        this.adj = new HashMap<>();
    }

   // méthodes

    //ajoute un secteur au graphe s’il n’existe pas deja
    public void ajouterSecteur(Secteur s) {
        adj.putIfAbsent(s, new ArrayList<>());
    }

    // ajoute une arête non orientée entre a et b.
     //On ajoute b dans la liste des voisins de a,
     // et a dans la liste des voisins de b.
    public void ajouterVoisinage(Secteur a, Secteur b) {

        // on regarde si les 2 sommets sont présent
        adj.putIfAbsent(a, new ArrayList<>());
        adj.putIfAbsent(b, new ArrayList<>());

        // on ne duplique pas les voisins
        if (!adj.get(a).contains(b)) {
            adj.get(a).add(b);
        }
        if (!adj.get(b).contains(a)) {
            adj.get(b).add(a);
        }
    }

    //graphe

    //Renvoie la liste des voisins d’un secteur si il n'y a pas de secteurs on renvoi une liste vide
    public List<Secteur> getVoisins(Secteur s) {
        return adj.getOrDefault(s, new ArrayList<>());
    }

    //renvoi l'ensemble des secteurs
    public Set<Secteur> getTousSecteurs() {
        return adj.keySet();
    }

    // on prend le degré du secteur on envoi les voisins
    public int getDegre(Secteur s) {
        return getVoisins(s).size();
    }


    //lecture de fichier
    //Format :
     //   ligne 1 : nombre de sommets
    //    ligne 2 : nom des sommets separe par des epspaces
     //  Ligne 3 nombre d'arête
    //   Lignes suivantes : nom1 nom2 les autres données sont ignorés
    public static GrapheSecteurs chargerDepuisFichier(String cheminFichier) throws IOException {

        GrapheSecteurs g = new GrapheSecteurs();

        // Permet de retrouver l’objet Secteur à partir de son nom
        Map<String, Secteur> mapNomSecteur = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(cheminFichier))) {

            String ligne;

            // nombre de sommets et exceptions
            ligne = lireLigneNonVide(br);
            if (ligne == null) {
                throw new IOException("Fichier vide ou invalide");
            }

            int nbSommets = Integer.parseInt(ligne.trim());

            //liste des noms et de sommets
            ligne = lireLigneNonVide(br);
            if (ligne == null) {
                throw new IOException("Fichier invalide ");
            }

            String[] noms = ligne.trim().split("\\s+");
            if (noms.length < nbSommets) {
                throw new IOException("Nombre de noms inférieur au nombre de sommets annoncé.");
            }

            // Création des secteurs et ajout dans le graphe
            for (int i = 0; i < nbSommets; i++) {
                String nom = noms[i];
                Secteur s = new Secteur(nom);
                mapNomSecteur.put(nom, s);
                g.ajouterSecteur(s);
            }

            //nombre d'arretes
            ligne = lireLigneNonVide(br);
            if (ligne == null) {
                throw new IOException("Fichier invalide (nb d'arêtes manquant).");
            }

            int nbAretes = Integer.parseInt(ligne.trim());

            // lignes d'aretes
            for (int i = 0; i < nbAretes; i++) {

                ligne = lireLigneNonVide(br);
                if (ligne == null) {
                    throw new IOException("Nb d'arêtes supérieur au nb de lignes disponibles.");
                }

                String[] parts = ligne.trim().split("\\s+");
                if (parts.length < 2) {
                    // si la ligne est mal formée, on la saute (ou on pourrait lever une erreur)
                    continue;
                }

                String nom1 = parts[0];
                String nom2 = parts[1];

                Secteur s1 = mapNomSecteur.get(nom1);
                Secteur s2 = mapNomSecteur.get(nom2);

                if (s1 != null && s2 != null) {
                    g.ajouterVoisinage(s1, s2);
                }
            }
        }

        return g;
    }


     // lit la prochaine ligne du fichier
     // ignore les lignes vides
     // ignore les lignes qui commencent par #
     // Renvoie null si on arrive à la fin du fichier.

    static String lireLigneNonVide(BufferedReader br) throws IOException {
        String ligne;
        while ((ligne = br.readLine()) != null) {
            ligne = ligne.trim();
            if (ligne.isEmpty()) continue;      // lignes vides
            if (ligne.startsWith("#")) continue; // commentaires
            return ligne;
        }
        return null;
    }
}
