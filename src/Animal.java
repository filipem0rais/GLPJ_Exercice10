//
// Auteur : Filipe Dias Morais
// Projet : Exercice10
// Date   : 29.11.2022
// 


import java.util.List;
import java.util.Random;

public abstract class Animal {
    private boolean alive;
    private Field field;
    private Location location;
    public static final Random rand = Randomizer.getRandom();
    private int age;

    /**
     * Crée un nouvel animal à une position donnée.
     *
     * @param field    Le terrain où l'animal est créé.
     * @param location La position où l'animal est créé.
     */

    public Animal(Field field, Location location) {
        alive = true;
        this.field = field;
        setLocation(location);
        age = 0;
    }

    public void setLocation(Location newLocation) {
        if (location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    public Location getLocation() {
        return location;
    }

    public Field getField() {
        return field;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive() {
        this.alive = true;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public int getAge() {
        return age;
    }

    public void setDead() {
        alive = false;
        if (location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    public boolean canBreed() {
        return age >= getBreedingAge();
    }

    public abstract void act(List<Animal> newAnimals);

    public void incrementAge() {
        age++;
        if (age > getMaxAge()) {
            setDead();
        }
    }

    protected abstract int getMaxAge();

    protected abstract int getBreedingAge();
    protected abstract double getBreedingProbability();
    protected abstract int getMaxLitterSize();

    public int breed() {
        int births = 0;
        if (canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

}
