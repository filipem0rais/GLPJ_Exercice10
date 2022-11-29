import java.util.List;
import java.util.Random;

/**
 * Un modèle simple de lapin (rabbit).
 * Un lapin vieillit, se déplace, se reproduit et meurt.
 *
 * @author David J. Barnes et Michael Kolling
 * @version 2008.03.30
 */
public class Rabbit extends Animal {
    // Caractéristiques partagées par tous les lapins (champs statiques).

    // L'âge à partir duquel un lapin peut se reproduire.
    private static final int BREEDING_AGE = 5;
    // L'âge maximal d'un lapin.
    private static final int MAX_AGE = 40;
    // La probabilité de reproduction d'un lapin.
    private static final double BREEDING_PROBABILITY = 0.15;
    // La taille maximale d'une portée.
    private static final int MAX_LITTER_SIZE = 4;
    // Un générateur de nombres aléatoires commun pour contrôler les reproductions.

    // Caractéristiques individuelles (champs d'instance).

    // L'âge du lapin.
    //  Private int age ;


    /**
     * Crée un lapin. Un lapin peut être créé comme nouveau-né (âge nul)
     * ou avec un âge aléatoire.
     *
     * @param randomAge Si true, le lapin aura un âge aléatoire.
     * @param field     Le terrain actuellement occupé.
     * @param location  L'emplacement sur le terrain.
     */
    public Rabbit(boolean randomAge, Field field, Location location) {
        super(field, location);
        super.setField(field);
        super.setLocation(location);
        if (randomAge) {
            setAge(rand.nextInt(MAX_AGE));
        }
    }

    /**
     * Ce que fait un lapin la plupart du temps : il se déplace.
     * Il peut se reproduire ou mourir de vieillesse.
     *
     * @param newRabbits Une liste à laquelle ajouter les nouveau-nés.
     */
    public void act(List<Animal> newRabbits) {
        incrementAge();
        if (isAlive()) {
            giveBirth(newRabbits);
            // Essaie de passer à un nouvel emplacement.
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if (newLocation != null) {
                setLocation(newLocation);
            } else {
                // Surpopulation.
                setDead();
            }
        }
    }


    /**
     * Vérifie si ce lapin va donner naissance.
     * Les nouveau-nés seront placés dans des sites adjacents libres.
     *
     * @param newRabbits Une liste pour ajouter les nouveau-nés.
     */
    private void giveBirth(List<Animal> newRabbits) {
        // Nouveau-nés placés dans des sites adjacents.
        // Obtenir une liste des emplacements adjacents libres.
        List<Location> free = getField().getFreeAdjacentLocations(getLocation());
        int births = breed();
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Rabbit young = new Rabbit(false, getField(), loc);
            newRabbits.add(young);
        }
    }




    /**
     * Un lapin peut se reproduire s'il a atteint l'âge adulte.
     *
     * @return true si le lapin peut se reproduire, false autrement.
     */
    public int getBreedingAge() {
        return BREEDING_AGE;
    }

    public int getMaxAge() {
        return MAX_AGE;
    }

    public int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }

    public double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }
}
