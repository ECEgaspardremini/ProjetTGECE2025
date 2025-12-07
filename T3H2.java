import java.util.*;

public class T3H2 {

    // On réutilise la coloration de Welsh-powell faite pour H1
    private T3H1WP planifWP = new T3H1WP();

  
    public Map<Secteur, Integer> planifier(GrapheSecteurs g,
                                           int capaciteCamion,
                                           int nbCamions) {

        // Capacité totale de ramassage par jour
        int capaciteJour = capaciteCamion * nbCamions;

        //coloration du graphe comme H1
        Map<Secteur, Integer> couleurs = planifWP.colorierWelshPowell(g);

        // Regrouper les secteurs par couleur
        Map<Integer, List<Secteur>> secteursParCouleur = new HashMap<>();
        for (Map.Entry<Secteur, Integer> e : couleurs.entrySet()) {
            Secteur s = e.getKey();
            int couleur = e.getValue();

            secteursParCouleur
                    .computeIfAbsent(couleur, k -> new ArrayList<>())
                    .add(s);
        }

        // quelle jour pour quel secteur
        Map<Secteur, Integer> jours = new HashMap<>();

        // On commence à compter les jours à partir de 1
        int jourCourant = 1;

        // On traite les couleurs dans l’ordre croissant seulement pour l'affichage 
        List<Integer> listeCouleurs = new ArrayList<>(secteursParCouleur.keySet());
        Collections.sort(listeCouleurs);
        //Répartition par couleur
        for (Integer couleur : listeCouleurs) {

            
            
            // Liste des secteurs de cette couleur
            List<Secteur> listeSecteurs = secteursParCouleur.get(couleur);

            // On trie les secteurs de cette couleur par quantité décroissante
            // donc on place dabord les plus gros secteurs
            listeSecteurs.sort((s1, s2) ->
                    Integer.compare(s2.getQuantite(), s1.getQuantite()));

            // Chaque bin correspondra à un jour
            List<Integer> charges = new ArrayList<>();          // charge actuelle de chaque jour
            List<List<Secteur>> bins = new ArrayList<>();       // secteurs affectés à chaque jour

            // On place les secteurs un par un
            for (Secteur s : listeSecteurs) {
                int q = s.getQuantite();

                // message si un seul secteur dépasse la capacité d’un jour
                if (q > capaciteJour) {
                    System.out.println("le secteur " + s.getNom()
                            + " a une quantité (" + q + ") supérieure à la capacité journalière ("
                            + capaciteJour + "). il ne peut donc pas etre planifié normalement");
                }

                placerSecteurDansUnJour(s, q, capaciteJour, charges, bins);
            }

            // Maintenant, chaque bin correspond à un jour réel dans le planning
            for (List<Secteur> bin : bins) {
                for (Secteur s : bin) {
                    jours.put(s, jourCourant);
                }
                jourCourant++;
            }
        }

        return jours;
    }
    private void placerSecteurDansUnJour(Secteur s,
                                         int quantite,
                                         int capaciteJour,
                                         List<Integer> charges,
                                         List<List<Secteur>> bins) {

        boolean place = false;
        // On essaie de le mettre dans un jour déjà créé
        for (int i = 0; i < charges.size(); i++) {
            int chargeActuelle = charges.get(i);

            if (chargeActuelle + quantite <= capaciteJour) {
                // On peut ajouter ce secteur à ce jour
                charges.set(i, chargeActuelle + quantite);
                bins.get(i).add(s);
                place = true;
                break;
            }
        }

        
        
        // Si on n’a trouvé aucun jour où ça rentre, on crée un nouveau "bin" (nouveau jour)
        if (!place) {
            charges.add(quantite);

            List<Secteur> nouveauJour = new ArrayList<>();
            nouveauJour.add(s);
            bins.add(nouveauJour);
        }
    }
}
