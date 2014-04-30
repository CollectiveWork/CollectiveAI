package ga;


import ga.windows.MainWindow;
import org.ejml.simple.SimpleMatrix;

import javax.swing.*;

/**
 * Created by AndreiMadalin on 3/12/14.
 */
public abstract class GeneticAlgorithm {
    protected int m; // dimensiunea unui cromozom
    protected int n; // dimensiunea populatiei
    protected int it; // numarul de iteratii
    protected double uc; // probabilitatea de crossover
    protected double um; // probabilitatea de mutatie
    protected int geneSize;
    protected double low, high;
    protected boolean elitism;
    protected boolean maximize;
    protected String type;
    protected SimpleMatrix population; // populatia de cromozomi
    protected SimpleMatrix fitness_population; // fitnesul fiecarui cromozom
    protected SimpleMatrix normalized_population; // fitnesul fiecarui cromozom, normalizat
    protected SimpleMatrix cumulative_population; //
    protected String best_chromosome = "";


    protected JTextArea output = null;
    protected MainWindow window = null;

    public GeneticAlgorithm() {

    }

    /**
     * @param m  // dimensiunea unui cromozom
     * @param n  // dimensiunea populatiei
     * @param it // numarul de iteratii
     * @param uc // probabilitatea de crossover
     * @param um // probabilitatea de mutatie
     */
    public GeneticAlgorithm(int m, int n, int it, double uc, double um, boolean elitism, int geneSize) {
        this.m = m;
        this.n = n;
        this.it = it;
        this.uc = uc;
        this.um = um;
        this.elitism = elitism;
        this.geneSize = geneSize;
        this.type = "binary";
    }

    public GeneticAlgorithm(int m, int n, int it, double uc, double um, boolean elitism, double low, double high) {
        this.m = m;
        this.n = n;
        this.it = it;
        this.uc = uc;
        this.um = um;
        this.elitism = elitism;
        this.low = low;
        this.high = high;
        this.type = "real";
    }

    public void setOutput(JTextArea output) {
        this.output = output;
    }

    public void setWindow(MainWindow window) {
        this.window = window;
    }

    /**
     * @param maximize           set this true if you want the GA to maximize the fitness function ( set it false if you want to minimize it )
     * @param crossoverAlgorithm the crossover algorithm
     * @return the population at the end of iterations
     * @throws Exception
     */
    public SimpleMatrix start(boolean maximize, String crossoverAlgorithm) throws Exception {
        population = init();
        this.maximize = maximize;
        String tmp;

        SimpleMatrix new_population;
        SimpleMatrix all_population = new SimpleMatrix(population.numRows(), population.numCols() * 2);
        int i = 1;
        double highest_mutation = .333;
        double lowest_mutation = .015;
        int num_changes = it / 100 / 2;
        double step_change = (highest_mutation - lowest_mutation) / num_changes;

        do {
            if (i % 100 == 0) {
                um += (i < it / 2) ? -step_change : step_change;
            }

            fitness_population = getPopulationFitness(population);
            sortPopulationByFitness(population, fitness_population);
            normalized_population = getNormalizedFitnes();
            cumulative_population = getCumulativeFitnes();

            if (i % 10 == 0){
                tmp = best_chromosome;
                best_chromosome = getBinaryCromosom(getFittest());
                if(tmp != best_chromosome)
                    window.draw_panel.setChromosome(best_chromosome);
                System.out.println("Iteration " + i + ". Fittest fitness: " + getFitness(getFittest()) + " " + best_chromosome);

            }

            new_population = Population.rouletteWheelSelection(population, cumulative_population);

            switch (crossoverAlgorithm) {
                case "singlePointCrossover":
                    new_population = GeneticOperations.singlePointCrossover(type, new_population, uc);
                    break;
                case "doublePointCrossover":
                    new_population = GeneticOperations.doublePontCrossover(type, new_population, uc);
                    break;
                case "multiPointCrossover":
                    new_population = GeneticOperations.multiPointCrossover(type, new_population, uc);
                    break;
                default:
                    throw new Exception("Unrecognised crossover algorithm");
            }

            if (type.equals("binary"))
                new_population = GeneticOperations.mutation(new_population, um);
            else
                new_population = GeneticOperations.mutation(new_population, um, high - low);


            if (elitism) {
                all_population.insertIntoThis(0, 0, population);
                all_population.insertIntoThis(0, population.numCols(), new_population);
                sortPopulationByFitness(all_population, getPopulationFitness(all_population));

                population = all_population.extractMatrix(0, population.numRows(), 0, population.numCols());
            } else {
                population = new_population;
            }
            i++;
            //System.out.println(binaryToString(getFittest()));
        } while (i <= it);

        // fix all the values after last mutaion
        fitness_population = getPopulationFitness(population);
        sortPopulationByFitness(population, fitness_population);
        normalized_population = getNormalizedFitnes();
        cumulative_population = getCumulativeFitnes();

        return population;
    }

    private SimpleMatrix init() {
        if (type.equals("binary"))
            return Population.init(m, n);
        else
            return Population.init(m, n, low, high);
    }

    private SimpleMatrix getPopulationFitness(SimpleMatrix population) {
        if (type.equals("binary"))
            return getBinaryPopulationFitness(population);
        else
            return getRealPopulationFitness(population);
    }


    private SimpleMatrix getBinaryPopulationFitness(SimpleMatrix population) {
        int n = population.numCols();
        int m = population.numRows();
        SimpleMatrix fitness = new SimpleMatrix(1, n);

        for (int i = 0; i < n; i++) {
            fitness.set(i, fitness(convertChromosome(population.extractVector(false, i))));
        }

        return fitness;

    }

    private SimpleMatrix getRealPopulationFitness(SimpleMatrix population) {
        int n = population.numCols();
        SimpleMatrix fitness = new SimpleMatrix(1, n);

        for (int i = 0; i < n; i++) {
            fitness.set(i, fitness(population.extractVector(false, i)));
        }

        return fitness;
    }

    private SimpleMatrix getNormalizedFitnes() {
        return fitness_population.divide(fitness_population.elementSum());
    }

    private SimpleMatrix getCumulativeFitnes() {
        int n = population.numCols();
        SimpleMatrix c_fitnes = new SimpleMatrix(1, n);
        double sum = 0;

        for (int i = 0; i < n; i++) {
            sum += normalized_population.get(i);
            c_fitnes.set(i, sum);
        }

        return c_fitnes;
    }

    private void sortPopulationByFitness(SimpleMatrix population, SimpleMatrix fitness_population) {
        boolean s;
        int n = fitness_population.numCols();
        double temp1;
        SimpleMatrix temp2;
        do {
            s = false;
            for (int i = 0; i < n - 1; i++) {
                if (maximize ? fitness_population.get(i) < fitness_population.get(i + 1) : fitness_population.get(i) > fitness_population.get(i + 1)) {
                    temp1 = fitness_population.get(i);
                    fitness_population.set(i, fitness_population.get(i + 1));
                    fitness_population.set(i + 1, temp1);

                    temp2 = population.extractVector(false, i);
                    population.insertIntoThis(0, i, population.extractVector(false, i + 1));
                    population.insertIntoThis(0, i + 1, temp2);
                    s = true;
                }
            }
        } while (s);
    }

    public SimpleMatrix getFittest() throws Exception {
        if (maximize)
            return population.extractVector(false, population.numCols() - 1);
        else
            return population.extractVector(false, 0);
    }


    /**
     * converts a binary genes to real genes
     */
    protected SimpleMatrix convertChromosome(SimpleMatrix chromosome) {
        return getParamsFromCromozom(getBinaryCromosom(chromosome));
    }

    /**
     * @param cromosom a SimpleMatrix with a single column
     * @return string representation of binary cromosom
     */
    public String getBinaryCromosom(SimpleMatrix cromosom) {
        StringBuilder s = new StringBuilder();
        int m = cromosom.numRows();
        for (int i = 0; i < m; i++) {
            s.append(cromosom.get(i) == 0 ? '0' : '1');
        }

        return s.toString();
    }

    /**
     * @param cromozom a string of 0s and 1s ( binary representation of a cromosom )
     * @return a SimpleMatrix with genes converted to int
     */
    public SimpleMatrix getParamsFromCromozom(String cromozom) {
        int n = cromozom.length() / geneSize;
        SimpleMatrix params = new SimpleMatrix(n, 1);
        for (int i = 0; i < n; i++)
            params.set(i, Integer.parseInt(cromozom.substring(i * geneSize, (i + 1) * geneSize), 2));

        return params;
    }

    protected abstract double fitness(SimpleMatrix chromosome);

    public double getFitness(SimpleMatrix chromosome) {
        if (type.equals("binary"))
            return fitness(convertChromosome(chromosome));
        else
            return fitness(chromosome);
    }

    public String binaryToString(SimpleMatrix chromosome) {
        String s = getBinaryCromosom(chromosome);
        String[] ss = s.split("(?<=\\G.{8})");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ss.length; i++) {
            sb.append((char) Integer.parseInt(ss[i], 2));
        }
        return sb.toString();
    }
}
