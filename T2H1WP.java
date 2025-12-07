import java.util.*;


 // Theme 3 H1


public class T3H1WP {

    
     // Applique l'algorithme de Welsh & Powell sur le graphe donné
     // return une map Secteur: numéro de jour (1, 2, 3,etc)
     
    public Map<Secteur, Integer> colorierWelshPowell(GrapheSecteurs g) {

        // résultat : pour chaque secteur on stock sa couleur( jour)
        Map<Secteur, Integer> couleur = new HashMap<>();
        //On récupère tous les secteurs du graphe
        List<Secteur> secteurs = new ArrayList<>(g.getTousSecteurs());

        // on trie les secteurs par degré décroissant comme dans wesh & powell
        //on colorie d'abord ceux qui ont le plus de voisins donc + grand degré
        secteurs.sort((s1, s2) -> g.getDegre(s2) - g.getDegre(s1));

        int currentColor = 1; // on commence par la premiere couleur

        //Tant qu'il reste des secteurs non coloriés
        for (Secteur s : secteurs) {

            // Si s est déjà colorié, on le saute
            if (couleur.containsKey(s)) {
                continue;
            }

            // On donne la couleur actuelle au secteur s
            couleur.put(s, currentColor);

            // Puis on essaie de colorier le plus de secteurs possible
            // avec cette même couleur 
            for (Secteur t : secteurs) {

                // On ignore les secteurs déjà coloriés
                if (couleur.containsKey(t)) {
                    continue;
                }

                // On vérifie si t est compatible avec la couleur courante
                if (estCompatibleAvecCouleur(g, t, couleur, currentColor)) {
                    couleur.put(t, currentColor);
                }
            }

            // Quand on ne peut plus utiliser currentColor, on passe à la suivante
            currentColor++;
        }

        return couleur;
    }

    // *boolean qui renvoie true si le secteur t peut recevoir la couleur currentColor pour cela aucun voisin ne doit avoir la bonne couleur
    
    private boolean estCompatibleAvecCouleur(GrapheSecteurs g,
                                             Secteur t,
                                             Map<Secteur, Integer> couleur,
                                             int currentColor) {

        for (Secteur voisin : g.getVoisins(t)) {
            Integer cVoisin = couleur.get(voisin);
            if (cVoisin != null && cVoisin == currentColor) {
                // Au moins un voisin a déjà cette couleur = pas compatible
                return false;
            }
        }
        return true;
    }
}
