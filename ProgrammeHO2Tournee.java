import java.util.*;

public class ProgrammeHO2Tournee {

    // encodage d'un arc orienté (u, v) dans un long pour pouvoir le mettre dans un Set.
    private static long encodageArc(int u, int v) {
        return (((long) u) << 32) | (v & 0xffffffffL);
    }

    // Reconstruit le chemin du sommet source au sommet cible à partir du tableau de prédécesseurs.
    private static List<Integer> reconstruireChemin(int[] predecesseur, int idSource, int idCible) {
        List<Integer> chemin = new ArrayList<>();
        int courant = idCible;
        while (courant != -1) {
            chemin.add(courant);
            if (courant == idSource) break;
            courant = predecesseur[courant];
        }
        Collections.reverse(chemin);
        return chemin;
    }

    public static void executerTourneeHO2(String nomFichier) {

        try {
            // Lecture du fichier (graphe + dépôt + liste des encombrants sur arcs orientés)
            InstanceTourneeHO2 instance = LecteurDeFichierTourneeHO2.lireInstanceTourneeHO2(nomFichier);
            GrapheRoutier graphe = instance.getGraphe();
            int idDepot = instance.getIdDepot();
            List<EncombrantOrienteHO2> encombrants = instance.getListeEncombrants();
            int k = encombrants.size(); // nombre d'encombrants

            System.out.println("===== HO2 - Tournée de ramassage (Hypothèse 2) =====");
            System.out.println("Nombre d'encombrants à ramasser : " + k);
            System.out.println("Dépôt : " + graphe.getNomSommet(idDepot));

            // Pour chaque encombrant, on récupère l'id de U (sommet de départ de l'arc U->V) et l'id de V (sommet d'arrivée)
            int[] idsDepartEncombrants = new int[k];
            int[] idsArriveeEncombrants = new int[k];

            for (int i = 0; i < k; i++) {
                EncombrantOrienteHO2 e = encombrants.get(i);
                Integer idU = graphe.getIdSommet(e.getNomSommetDepart());
                Integer idV = graphe.getIdSommet(e.getNomSommetArrivee());
                if (idU == null || idV == null) {
                    throw new IllegalArgumentException("Sommet inconnu pour un encombrant : "
                            + e.getNomSommetDepart() + " ou " + e.getNomSommetArrivee());
                }
                idsDepartEncombrants[i] = idU;
                idsArriveeEncombrants[i] = idV;
            }

            System.out.print("Encombrants sur les arcs : ");
            for (int i = 0; i < k; i++) {
                EncombrantOrienteHO2 e = encombrants.get(i);
                System.out.print(e.getNomSommetDepart() + "->" + e.getNomSommetArrivee() + "  ");
            }
            System.out.println("\n");

            // On fait Dijkstra pour : le dépôt tous les sommets U (départ des arcs des encombrants) tous les sommets V (arrivée des arcs des encombrants)
            Set<Integer> ensembleSources = new HashSet<>();
            ensembleSources.add(idDepot);
            for (int id : idsDepartEncombrants) {
                ensembleSources.add(id);
            }
            for (int id : idsArriveeEncombrants) {
                ensembleSources.add(id);
            }

            // On fait Dijkstra depuis chaque source "importante"
            Map<Integer, ResultatDuDijkstra> dijkstraDepuis = new HashMap<>();
            for (int source : ensembleSources) {
                ResultatDuDijkstra res = Dijkstra.calculerPlusCourtsChemins(graphe, source);
                dijkstraDepuis.put(source, res);
            }

            //  Construction matrice des distances TSP point 0 : dépôt, points 1..k : sommets de départ des encombrants (idsDepartEncombrants)
            int nbPoints = k + 1;
            int[] points = new int[nbPoints];
            points[0] = idDepot;
            for (int i = 0; i < k; i++) {
                points[i + 1] = idsDepartEncombrants[i];
            }

            // Matrice des distances "entre points" pour le TSP
            double[][] distPoints = new double[nbPoints][nbPoints];
            for (int i = 0; i < nbPoints; i++) {
                int source = points[i];
                ResultatDuDijkstra res = dijkstraDepuis.get(source);
                double[] distDepuisSource = res.getDistanceDepuisSource();
                for (int j = 0; j < nbPoints; j++) {
                    int cible = points[j];
                    distPoints[i][j] = distDepuisSource[cible];
                }
            }

            // Vérification : chaque sommet de départ d'encombrant doit être accessible depuis le dépôt, et le dépôt doit être accessible depuis chaque sommet (pour le retour).
            for (int i = 1; i < nbPoints; i++) {
                if (Double.isInfinite(distPoints[0][i]) || Double.isInfinite(distPoints[i][0])) {
                    System.out.println("Le sommet de départ d'un encombrant ("
                            + graphe.getNomSommet(points[i])
                            + ") n'est pas accessible depuis le dépôt ou retour impossible.");
                    return;
                }
            }

            // TSP par programmation dynamique sur les encombrants
            // On numérote les encombrants 0..k-1.
            // DP[mask][i] = coût minimal pour partir du dépôt, visiter l'ensemble 'mask' d'encombrant et finir sur l'encombrant i (i index 0..k-1).
            int maxMask = 1 << k;
            double[][] dp = new double[maxMask][k];
            int[][] pred = new int[maxMask][k];

            // Initialisation
            for (int mask = 0; mask < maxMask; mask++) {
                for (int i = 0; i < k; i++) {
                    dp[mask][i] = Double.POSITIVE_INFINITY;
                    pred[mask][i] = -1;
                }
            }

            // Départ : du dépôt (point 0) vers chaque encombrant i (point i+1)
            for (int i = 0; i < k; i++) {
                int mask = 1 << i;
                dp[mask][i] = distPoints[0][i + 1];
            }

            // Transitions du TSP
            for (int mask = 0; mask < maxMask; mask++) {
                for (int last = 0; last < k; last++) {
                    if ((mask & (1 << last)) == 0) continue; // si l'encombrant "last" n'est pas dans mask, on saute
                    double coutActuel = dp[mask][last];
                    if (Double.isInfinite(coutActuel)) continue;

                    // On essaie d'ajouter un nouvel encombrant "nxt"
                    for (int nxt = 0; nxt < k; nxt++) {
                        if ((mask & (1 << nxt)) != 0) continue; // déjà visité
                        int nouveauMask = mask | (1 << nxt);
                        // last -> nxt : on passe de point (last+1) à point (nxt+1)
                        double coutTransition = distPoints[last + 1][nxt + 1];
                        double nouveauCout = coutActuel + coutTransition;
                        if (nouveauCout < dp[nouveauMask][nxt]) {
                            dp[nouveauMask][nxt] = nouveauCout;
                            pred[nouveauMask][nxt] = last;
                        }
                    }
                }
            }

            // Fermeture de la boucle : retour au dépôt
            int fullMask = maxMask - 1;
            double meilleurCout = Double.POSITIVE_INFINITY;
            int dernierEncombrant = -1;

            for (int last = 0; last < k; last++) {
                double coutTour = dp[fullMask][last] + distPoints[last + 1][0];
                if (coutTour < meilleurCout) {
                    meilleurCout = coutTour;
                    dernierEncombrant = last;
                }
            }

            if (Double.isInfinite(meilleurCout) || dernierEncombrant == -1) {
                System.out.println("Impossible de construire une tournée couvrant tous les encombrants.");
                return;
            }

            //Reconstruction de l'ordre des encombrants visités (indices 0..k-1)
            List<Integer> ordreEncombrants = new ArrayList<>();
            int mask = fullMask;
            int current = dernierEncombrant;
            while (current != -1) {
                ordreEncombrants.add(current);
                int prevIndex = pred[mask][current];
                mask = mask & ~(1 << current);
                current = prevIndex;
            }
            Collections.reverse(ordreEncombrants);

            // Reconstruction du chemin global (liste de sommets)
            List<Integer> cheminGlobal = new ArrayList<>();
            int sommetCourant = idDepot;

            cheminGlobal.add(idDepot);  // on commence au dépôt

            // On enchaîne dépôt -> U1 -> U2 -> ... selon l'ordre trouvé par le TSP
            for (int idx = 0; idx < ordreEncombrants.size(); idx++) {
                int encombrantIndex = ordreEncombrants.get(idx);
                int idDepart = idsDepartEncombrants[encombrantIndex];

                // On va de "sommetCourant" à "idDepart" en suivant les prédécesseurs du Dijkstra
                ResultatDuDijkstra resSource = dijkstraDepuis.get(sommetCourant);
                if (resSource == null) {
                    throw new IllegalStateException("Pas de Dijkstra pré-calculé pour la source " + sommetCourant);
                }

                List<Integer> segment = reconstruireChemin(
                        resSource.getPredecesseur(), sommetCourant, idDepart);

                // On évite de répéter le sommet de départ
                if (!cheminGlobal.isEmpty() && !segment.isEmpty()) {
                    segment.remove(0);
                }
                cheminGlobal.addAll(segment);
                sommetCourant = idDepart;
            }

            // Retour du dernier sommet au dépôt
            if (!ordreEncombrants.isEmpty()) {
                ResultatDuDijkstra resSource = dijkstraDepuis.get(sommetCourant);
                List<Integer> segmentRetour = reconstruireChemin(
                        resSource.getPredecesseur(), sommetCourant, idDepot);
                if (!segmentRetour.isEmpty()) {
                    segmentRetour.remove(0);
                }
                cheminGlobal.addAll(segmentRetour);
            }

            // On regarde maintenant quelles arêtes orientées sont réellement parcourues
            // dans le cheminGlobal (pour savoir si un arc U->V est traversé naturellement).
            Set<Long> arcsParcourus = new HashSet<>();
            for (int i = 0; i + 1 < cheminGlobal.size(); i++) {
                int u = cheminGlobal.get(i);
                int v = cheminGlobal.get(i + 1);
                long key = encodageArc(u, v);
                arcsParcourus.add(key);
            }

            // Calcul du coût des boucles locales et préparation des annotations
            double coutBouclesLocales = 0.0;

            // Pour l'affichage, on veut savoir, pour chaque sommet U, quels encombrants
            // nécessitent une boucle locale à partir de U.
            Map<Integer, List<EncombrantOrienteHO2>> bouclesParSommet = new HashMap<>();

            for (int i = 0; i < k; i++) {
                EncombrantOrienteHO2 e = encombrants.get(i);
                int idU = graphe.getIdSommet(e.getNomSommetDepart());
                int idV = graphe.getIdSommet(e.getNomSommetArrivee());

                long keyUV = encodageArc(idU, idV);

                // Si la tournée traverse naturellement l'arc U->V, l'encombrant est ramassé
                if (arcsParcourus.contains(keyUV)) {
                    continue; // pas besoin de boucle locale
                }

                // Sinon, on ajoute une boucle locale orientée :
                // U -> V (plus court chemin)
                // puis V -> U (plus court chemin)
                ResultatDuDijkstra resDepuisU = dijkstraDepuis.get(idU);
                ResultatDuDijkstra resDepuisV = dijkstraDepuis.get(idV);

                if (resDepuisU == null || resDepuisV == null) {
                    throw new IllegalStateException("Dijkstra manquant pour U ou V dans une boucle locale.");
                }

                double[] distDepuisU = resDepuisU.getDistanceDepuisSource();
                double[] distDepuisV = resDepuisV.getDistanceDepuisSource();

                double dUV = distDepuisU[idV];
                double dVU = distDepuisV[idU];

                if (Double.isInfinite(dUV) || Double.isInfinite(dVU)) {
                    System.out.println("Attention : impossible de faire une boucle locale pour l'encombrant sur "
                            + e.getNomSommetDepart() + "->" + e.getNomSommetArrivee()
                            + " (chemin orienté manquant).");
                    continue;
                }

                coutBouclesLocales += dUV + dVU;

                // On enregistre cette boucle locale pour l'affichage au niveau du sommet U
                bouclesParSommet
                        .computeIfAbsent(idU, k2 -> new ArrayList<>())
                        .add(e);
            }

            double distanceTotaleAvecBoucles = meilleurCout + coutBouclesLocales;

            // Affichage des résultats

            System.out.println("Ordre optimal de visite des encombrants (par arc U->V) :");
            for (int idx : ordreEncombrants) {
                EncombrantOrienteHO2 e = encombrants.get(idx);
                System.out.print(e.getNomSommetDepart() + "->" + e.getNomSommetArrivee() + "  ");
            }
            System.out.println("\n");

            System.out.println("Chemin complet (avec dépôt au début et à la fin) :");

            for (int id : cheminGlobal) {
                String nomSommet = graphe.getNomSommet(id);
                System.out.print(nomSommet);

                // Si ce sommet est le sommet de départ d'un ou plusieurs encombrants
                // qui nécessitent une boucle locale, on détaille ces boucles ici.
                List<EncombrantOrienteHO2> boucles = bouclesParSommet.get(id);
                if (boucles != null && !boucles.isEmpty()) {
                    for (EncombrantOrienteHO2 e : boucles) {
                        int idU = graphe.getIdSommet(e.getNomSommetDepart());
                        int idV = graphe.getIdSommet(e.getNomSommetArrivee());

                        ResultatDuDijkstra resDepuisU = dijkstraDepuis.get(idU);
                        ResultatDuDijkstra resDepuisV = dijkstraDepuis.get(idV);

                        // Chemin aller U -> V
                        List<Integer> aller = reconstruireChemin(
                                resDepuisU.getPredecesseur(), idU, idV);
                        // Chemin retour V -> U
                        List<Integer> retour = reconstruireChemin(
                                resDepuisV.getPredecesseur(), idV, idU);
                        if (!retour.isEmpty()) {
                            // On enlève V pour éviter de le dupliquer
                            retour.remove(0);
                        }

                        System.out.print(" [boucle locale orientée pour "
                                + e.getNomSommetDepart() + "->" + e.getNomSommetArrivee() + " : ");

                        // Affichage détaillé du chemin de la boucle : U -> ... -> V -> ... -> U
                        boolean premier = true;
                        for (int s : aller) {
                            if (!premier) System.out.print(" -> ");
                            System.out.print(graphe.getNomSommet(s));
                            premier = false;
                        }
                        for (int s : retour) {
                            if (!premier) System.out.print(" -> ");
                            System.out.print(graphe.getNomSommet(s));
                            premier = false;
                        }
                        System.out.print(" ]");
                    }

                    //on enlève ces boucles pour éviter de les réafficher
                    bouclesParSommet.remove(id);
                }

                System.out.print(" -> ");
            }
            System.out.println("(fin)");

            System.out.println("\nDistance minimale de la tournée (sans les boucles locales) : "
                    + meilleurCout + " m");
            System.out.println("Surcoût dû aux boucles locales (U->V->...->U) : "
                    + coutBouclesLocales + " m");
            System.out.println("Distance totale de la tournée (avec les boucles locales) : "
                    + distanceTotaleAvecBoucles + " m");
            System.out.println("\n");

        } catch (Exception e) {
            System.err.println("Erreur dans ProgrammeHO2Tournee : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
