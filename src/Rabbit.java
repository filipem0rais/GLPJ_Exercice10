import java.util.List;
import java.util.Random;

/**
 * Un modèle simple de lapin (rabbit).
 * Un lapin vieillit, se déplace, se reproduit et meurt.
 * 
 * @author David J. Barnes et Michael Kolling
 * @version 2008.03.30
 */
public class Rabbit
{
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
    private static final Random rand = Randomizer.getRandom();
    
    // Caractéristiques individuelles (champs d'instance).
    
    // L'âge du lapin.
    private int age;
    // Le lapin est vivant ou mort.
    private boolean alive;
    // La position du lapin.
    private Location location;
    // Le terrain occupé.
    private Field field;

    /**
     * Crée un lapin. Un lapin peut être créé comme nouveau-né (âge nul)
     * ou avec un âge aléatoire.
     * 
     * @param randomAge Si true, le lapin aura un âge aléatoire.
     * @param field Le terrain actuellement occupé.
     * @param location L'emplacement sur le terrain.
     */
    public Rabbit(boolean randomAge, Field field, Location location)
    {
        age = 0;
        alive = true;
        this.field = field;
        setLocation(location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }
    
    /**
     * Ce que fait un lapin la plupart du temps : il se déplace.
     * Il peut se reproduire ou mourir de vieillesse.
     * @param newRabbits Une liste à laquelle ajouter les nouveau-nés.
     */
    public void run(List<Rabbit> newRabbits)
    {
        incrementAge();
        if(alive) {
            giveBirth(newRabbits);            
            // Essaie de passer à un nouvel emplacement.
            Location newLocation = field.freeAdjacentLocation(location);
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Surpopulation.
                setDead();
            }
        }
    }
    
    /**
     * Indique si le lapin est mort.
     * @return true si la lapin est toujours vivant.
     */
    public boolean isAlive()
    {
        return alive;
    }
    
    /**
     * Indique que le lapin est mort.
     * Il est retiré du terrain.
     */
    public void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
    
    /**
     * Renvoie la position du lapin.
     * @return La position du lapin.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Place le lapin à la nouvelle posiiton sur le terrain donné.
     * @param newLocation La nouvelle position du lapin.
     */
    private void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Augmente l'âge.
     * Peut provoquer la mort (de vieillesse).
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Vérifie si ce lapin va donner naissance.
     * Les nouveau-nés seront placés dans des sites adjacents libres.
     * @param newRabbits Une liste pour ajouter les nouveau-nés.
     */
    private void giveBirth(List<Rabbit> newRabbits)
    {
        // Nouveau-nés placés dans des sites adjacents.
        // Obtenir une liste des emplacements adjacents libres.
        List<Location> free = field.getFreeAdjacentLocations(location);
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Rabbit young = new Rabbit(false, field, loc);
            newRabbits.add(young);
        }
    }
        
    /**
     * Génère un nombre de naissances,
     * si le lapin peut se reproduire.
     * @return Le nombre de naissances (peut être nul).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * Un lapin peut se reproduire s'il a atteint l'âge adulte.
     * @return true si le lapin peut se reproduire, false autrement.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
