import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * Une simulation élémentaire proies-prédateurs, fondée sur un terrain
 * contenant des lapins (rabbits) et des renards (foxes).
 *
 * @author David J. Barnes et Michael Kolling
 * @version 2008.03.30
 */
public class Simulator {
    // Les constantes représentant les informations de configuration pour la simulation.
    // La largeur par défaut pour la grille.
    private static final int DEFAULT_WIDTH = 50;
    // La profondeur par défaut de la grille.
    private static final int DEFAULT_DEPTH = 50;
    // La probabilité qu'un renard soit créé à une position donnée sur la grille.
    private static final double FOX_CREATION_PROBABILITY = 0.02;
    // La probabilité qu'un lapin soit créé dans une position de la grille
    private static final double RABBIT_CREATION_PROBABILITY = 0.08;

    // Répertorie les animaux du terrain. Il existe des listes séparées pour simplifier l'itération.
    // private List<Animal> rabbits;
    //   private List<Animal> foxes;
    // L'état courant du terrain.
    private List<Animal> animals;

    private Field field;
    // Le pas actuel de la simulation.
    private int step;
    // Une représentation graphique de la simulation.
    private SimulatorView view;

    /**
     * Construit un terrain de simulation avec une taille donnée.
     */
    public Simulator() {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Construit un terrain de simulation avec une taille donnée.
     *
     * @param depth La profondeur du terrain. Doit être supérieure à zéro.
     * @param width La largeur du terrain. Doit être supérieure à zéro.
     */
    public Simulator(int depth, int width) {
        if (width <= 0 || depth <= 0) {
            System.out.println("Les dimensions doivent être supérieures à zéro.");
            System.out.println("Valeurs par défaut utilisées.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        //  rabbits = new ArrayList<Animal>();
        //  foxes = new ArrayList<Animal>();
        animals = new ArrayList<Animal>();
        field = new Field(depth, width);

        // Créer une vue de l'état de chaque position du terrain.
        view = new SimulatorView(depth, width);
        view.setColor(Rabbit.class, Color.orange);
        view.setColor(Fox.class, Color.blue);

        // Définir un point de départ valide.
        reset();
    }

    /**
     * Exécute la simulation à partir de son état courant pour une période assez longue,
     * par exemple 500 pas.
     */
    public void runLongSimulation() {
        simulate(500);
    }

    /**
     * Exécute la simulation à partir de son état courant pour un nombre de pas donné.
     * Arrête avant si elle n'est plus viable.
     *
     * @param numSteps Le nombre de pas à réaliser.
     */
    public void simulate(int numSteps) {
        for (int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
        }
    }

    /**
     * Exécute la simulation à partir de son état courant pour un pas.
     * Parcourt tout le terrain en mettant à jour l'état de chaque
     * renard et de chaque lapin.
     */
    public void simulateOneStep() {
        step++;

        // Créer une nouvelle liste pour stocker les animaux qui vont naître.
        List<Animal> newAnimals = new ArrayList<Animal>();
        // Parcourir tous les animaux vivants.
        for (Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals);
            if (!animal.isAlive()) {
                it.remove();
            }
        }

        /*
        // Fournir de l'espace pour les lapins nouveau-nés.
        List<Animal> newRabbits = new ArrayList<Animal>();
        // Faire agir les lapins.
        for(Iterator<Animal> it = rabbits.iterator(); it.hasNext(); ) {
            Animal rabbit = it.next();
            rabbit.act(newRabbits);
            if(! rabbit.isAlive()) {
                it.remove();
            }
        }

        // Fournir de l'espace par les renards nouveau-nés.
        List<Animal> newFoxes = new ArrayList<Animal>();
        // Faire agir les renards.
        for(Iterator<Animal> it = foxes.iterator(); it.hasNext(); ) {
            Animal fox = it.next();
            fox.act(newFoxes);
            if(! fox.isAlive()) {
                it.remove();
            }
        }
        */
        // ajouter les nouveau-nés aux listes principales
        //  rabbits.addAll(newRabbits);
        //   foxes.addAll(newFoxes);

        animals.addAll(newAnimals);

        view.showStatus(step, field);
    }

    /**
     * Réinitialise la simulation.
     */
    public void reset() {
        step = 0;
        //   rabbits.clear();
        //  foxes.clear();

        animals.clear();
        populate();

        // Montrer l'état de départ.
        view.showStatus(step, field);
    }

    /**
     * Peuple le terrain avec des renards et des lapins.
     */
    private void populate() {
        Random rand = Randomizer.getRandom();
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                if (rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fox fox = new Fox(true, field, location);
                    // foxes.add(fox);
                    animals.add(fox);
                } else if (rand.nextDouble() <= RABBIT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Rabbit rabbit = new Rabbit(true, field, location);
                    // rabbits.add(rabbit);
                    animals.add(rabbit);
                }
                // else laisser la position vide.
            }
        }
    }
}
