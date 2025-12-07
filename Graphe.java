import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;


public class Graphe {

    private ArrayList<Sommet> sommets;
    private ArrayList<Arete> aretes;
    private HashMap<String, Integer> indicesParId;

    public Graphe() {
        this.sommets = new ArrayList<>();
        this.aretes = new ArrayList<>();
        this.indicesParId = new HashMap<>();
    }

    public void ajouterSommet(Sommet s) {
        if (indicesParId.containsKey(s.getId())) {
            return;
        }
        int index = sommets.size();
        sommets.add(s);
        indicesParId.put(s.getId(), index);
    }

    public void ajouterArete(String id1, String id2, int distance) {
        Sommet s1 = getSommetParId(id1);
        Sommet s2 = getSommetParId(id2);
        if (s1 == null || s2 == null) {
            System.out.println("Attention : sommet introuvable pour l'arête " + id1 + " - " + id2);
            return;
        }
        Arete a = new Arete(s1, s2, distance);
        aretes.add(a);
    }

    public Sommet getSommetParId(String id) {
        Integer indice = indicesParId.get(id);
        if (indice == null) {
            return null;
        }
        return sommets.get(indice);
    }

    public int getIndiceSommet(Sommet s) {
        Integer indice = indicesParId.get(s.getId());
        if (indice == null) {
            return -1;
        }
        return indice;
    }

    public ArrayList<Sommet> getSommets() {
        return sommets;
    }

    public int getNombreSommets() {
        return sommets.size();
    }


    public int[][] construireMatriceDistances() {
        int n = getNombreSommets();
        int INF = 1_000_000_000;
        int[][] matrice = new int[n][n];

        // initialisation
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    matrice[i][j] = 0;
                } else {
                    matrice[i][j] = INF;
                }
            }
        }

        // ajout des arêtes (graphe non orienté)
        for (Arete a : aretes) {
            int i = getIndiceSommet(a.getSommet1());
            int j = getIndiceSommet(a.getSommet2());
            if (i >= 0 && j >= 0) {
                matrice[i][j] = a.getDistance();
                matrice[j][i] = a.getDistance();
            }
        }

        return matrice;
    }


    public int[] dijkstra(int indiceSource, int[][] matriceDistances) {
        int n = matriceDistances.length;
        int INF = 1_000_000_000;

        int[] dist = new int[n];
        boolean[] visite = new boolean[n];

        for (int i = 0; i < n; i++) {
            dist[i] = INF;
            visite[i] = false;
        }
        dist[indiceSource] = 0;

        for (int c = 0; c < n; c++) {
            // recherche du sommet non visité le plus proche
            int u = -1;
            int min = INF;
            for (int i = 0; i < n; i++) {
                if (!visite[i] && dist[i] < min) {
                    min = dist[i];
                    u = i;
                }
            }

            if (u == -1) {
                break; // plus de sommet accessible
            }

            visite[u] = true;

            // relaxation des voisins
            for (int v = 0; v < n; v++) {
                if (!visite[v] && matriceDistances[u][v] < INF) {
                    int nouvelleDist = dist[u] + matriceDistances[u][v];
                    if (nouvelleDist < dist[v]) {
                        dist[v] = nouvelleDist;
                    }
                }
            }
        }

        return dist;
    }


    public Chemin calculerPlusCourtChemin(Sommet source, Sommet destination, int[][] matriceDistances) {
        int n = matriceDistances.length;
        int INF = 1_000_000_000;

        int indiceSource = getIndiceSommet(source);
        int indiceDestination = getIndiceSommet(destination);

        int[] dist = new int[n];
        boolean[] visite = new boolean[n];
        int[] precedent = new int[n]; // pour reconstruire le chemin

        Arrays.fill(dist, INF);
        Arrays.fill(visite, false);
        Arrays.fill(precedent, -1);

        dist[indiceSource] = 0;

        for (int c = 0; c < n; c++) {
            // recherche du sommet non visité le plus proche
            int u = -1;
            int min = INF;
            for (int i = 0; i < n; i++) {
                if (!visite[i] && dist[i] < min) {
                    min = dist[i];
                    u = i;
                }
            }

            if (u == -1) {
                break;
            }

            visite[u] = true;

            // relax des voisins
            for (int v = 0; v < n; v++) {
                if (!visite[v] && matriceDistances[u][v] < INF) {
                    int nouvelleDist = dist[u] + matriceDistances[u][v];
                    if (nouvelleDist < dist[v]) {
                        dist[v] = nouvelleDist;
                        precedent[v] = u;
                    }
                }
            }
        }

        //  destination est inatteignable
        if (dist[indiceDestination] >= INF) {
            return new Chemin(new ArrayList<Sommet>(), INF);
        }

        // reconstruction du chemin on remonte depuis la destination
        ArrayList<Sommet> cheminSommets = new ArrayList<>();
        int courant = indiceDestination;
        while (courant != -1) {
            Sommet s = sommets.get(courant);
            cheminSommets.add(0, s); // on insère au début pour inverser
            courant = precedent[courant];
        }

        int distanceTotale = dist[indiceDestination];
        return new Chemin(cheminSommets, distanceTotale);
    }



    public static Graphe chargerDepuisFichier(String nomFichier) throws IOException {
        Graphe graphe = new Graphe();

        BufferedReader lecteur = new BufferedReader(new FileReader(nomFichier));
        String ligne;
        boolean dansSommets = false;
        boolean dansAretes = false;

        while ((ligne = lecteur.readLine()) != null) {
            ligne = ligne.trim();
            if (ligne.isEmpty() || ligne.startsWith("#")) {
                continue;
            }

            if (ligne.equalsIgnoreCase("[SOMMETS]")) {
                dansSommets = true;
                dansAretes = false;
                continue;
            }
            if (ligne.equalsIgnoreCase("[ARETES]")) {
                dansSommets = false;
                dansAretes = true;
                continue;
            }

            String[] morceaux = ligne.split(";");
            if (dansSommets) {
                if (morceaux.length < 2) {
                    continue;
                }
                String id = morceaux[0].trim();
                String typeTexte = morceaux[1].trim();

                TypeSommet type;
                if (typeTexte.equalsIgnoreCase("DEPOT")) {
                    type = TypeSommet.DEPOT;
                } else if (typeTexte.equalsIgnoreCase("POINT_COLLECTE")) {
                    type = TypeSommet.POINT_COLLECTE;
                } else {
                    type = TypeSommet.INTERSECTION;
                }

                Sommet s = new Sommet(id, type);
                graphe.ajouterSommet(s);
            } else if (dansAretes) {
                if (morceaux.length < 3) {
                    continue;
                }
                String id1 = morceaux[0].trim();
                String id2 = morceaux[1].trim();
                int distance = Integer.parseInt(morceaux[2].trim());
                graphe.ajouterArete(id1, id2, distance);
            }
        }

        lecteur.close();
        return graphe;
    }


    public Sommet trouverDepot() {
        for (Sommet s : sommets) {
            if (s.getType() == TypeSommet.DEPOT) {
                return s;
            }
        }
        return null;
    }


    public ArrayList<Sommet> trouverPointsCollecte() {
        ArrayList<Sommet> liste = new ArrayList<>();
        for (Sommet s : sommets) {
            if (s.getType() == TypeSommet.POINT_COLLECTE) {
                liste.add(s);
            }
        }
        return liste;
    }
}
