public class Sommet {

    private String id;
    private TypeSommet type;

    private double quantiteDechets;

    public Sommet(String id, TypeSommet type) {
        this.id = id;
        this.type = type;
        this.quantiteDechets = 0.0;
    }

    public String getId() {
        return id;
    }

    public TypeSommet getType() {
        return type;
    }

    public double getQuantiteDechets() {
        return quantiteDechets;
    }

    public void setQuantiteDechets(double quantiteDechets) {
        this.quantiteDechets = quantiteDechets;
    }

    @Override
    public String toString() {
        return id + " (" + type + ", " + quantiteDechets + " t)";
    }
}
