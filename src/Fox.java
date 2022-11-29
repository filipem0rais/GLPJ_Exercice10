import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * Un modèle élémentaire de renard (fox).
 * Un renard vieillit, se déplace, mange des lapins (rabbits) et meurt.
 * 
 * @author David J. Barnes et Michael Kolling
 * @version 2008.03.30
 */
public class Fox
{
    // Caractéristiques partagées par tous les renards (champs statistiques).
    
    // L'âge à partir duquel un renard peut se reproduire
    private static final int BREEDING_AGE = 10;
    // L'âge maximal d'un renard
    private static final int MAX_AGE = 150;
    // La probabilité de reproduction d'un renard
    private static final double BREEDING_PROBABILITY = 0.35;
    // La taille maximale d'une portée
    private static final int MAX_LITTER_SIZE = 5;
    // La valeur nutritive d'un lapin. C'est le nombre de pas qu'un renard 
    // peut réaliser avant de devoir manger.
    private static final int RABBIT_FOOD_VALUE = 7;
    // Un générateur de nombres aléatoires commun pour contrôler les reproductions.
    private static final Random rand = Randomizer.getRandom();
    
    // Caractéristiques individuelles (champs d'instance).

    // L'âge du renard.
    private int age;
    // Le renard est vivant ou mort.
    private boolean alive;
    // La position du renard.
    private Location location;
    // Le terrain occupé.
    private Field field;
    // La réserve alimentaire du renard, augmentée lorsque le renard mange des lapins.
    private int foodLevel;

    /**
     * Crée un renard. Un renard peut être créé comme nouveau-né (âge nul et non affamé)
     * ou avec un âge et une réserve alimentaire aléatoires.
     * 
     * @param randomAge Si true, le renard aura un âge et un niveau de faim aléatoires.
     * @param field Le terrain actuellement occupé.
     * @param location L'emplacement sur le terrain.
     */
    public Fox(boolean randomAge, Field field, Location location)
    {
        age = 0;
        alive = true;
        this.field = field;
        setLocation(location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(RABBIT_FOOD_VALUE);
        }
        else {
            // laisser l'âge à zéro
            foodLevel = RABBIT_FOOD_VALUE;
        }
    }
    
    /**
     * Ce que fait le renard la plupart du temps : il chasse des lapins.
     * Durant cette chasse, il peut se reproduire, mourir de faim
     * ou mourir de vieillesse.
     * @param field Le terrain actuellement occupé.
     * @param newFoxes Une liste à laquelle ajouter les nouveau-nés.
     */
    public void hunt(List<Fox> newFoxes)
    {
        incrementAge();
        incrementHunger();
        if(alive) {
            giveBirth(newFoxes);            
            // Se déplacer vers une source de nourriture si on en trouve une.
            Location newLocation = findFood(location);
            if(newLocation == null) { 
                // pas de nourriture trouvée - se déplacer dans un emplacement libre.
                newLocation = field.freeAdjacentLocation(location);
            }
            // Voir s'il était possible de se déplacer.
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
     * Vérifier si le renard est mort ou vivant.
     * @return True si le renard est toujours vivant.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Renvoie la position du renard.
     * @return La position du renard.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Place le renard à la nouvelle position dans le champ donné.
     * @param newLocation La nouvelle position du renard.
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
     * Augmente l'âge. Ceci pourrait entraîner la mort du renard.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Rend ce renard plus affamé. Peut provoquer la mort.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Indique au renard de chercher des lapins dans les positions adjacentes.
     * Seul le premier lapin vivant est mangé.
     * @param location Position actuelle du renard.
     * @return La position où la nourriture a été trouvée, null si rien trouvé.
     */
    private Location findFood(Location location)
    {
        List<Location> adjacent = field.adjacentLocations(location);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) { 
                    rabbit.setDead();
                    foodLevel = RABBIT_FOOD_VALUE;
                    // Retirer le lapin mort du terrain.
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Vérifie si oui ou non ce renard doit donner naissance à ce pas.
     * Les naissances auront lieu dans les emplacements adjacents libres.
     * @param newFoxes Une liste pour ajouter les renards nouveau-nés.
     */
    private void giveBirth(List<Fox> newFoxes)
    {
        // Les renards naissent dans les positions adjacentes.
        // Obtenir une liste des positions adjacentes libres.
        List<Location> free = field.getFreeAdjacentLocations(location);
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Fox young = new Fox(false, field, loc);
            newFoxes.add(young);
        }
    }
        
    /**
     * Générer un nombre représentant le nombre de naissances,
     * s'il peut se reproduire.
     * @return Le nombre de naissances (peut être égal à zéro).
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
     * Un renard peut se reproduire s'il a atteint l'âge adulte.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * Indique que le renard est mort.
     * Il est retiré du terrain.
     */
    private void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
}
