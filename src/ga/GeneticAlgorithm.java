package ga;


import org.ejml.simple.SimpleMatrix;

import java.math.BigInteger;

/**
 * Created by AndreiMadalin on 3/12/14.
 */
public abstract class GeneticAlgorithm {
    int m; // dimensiunea unui cromozom
    int n; // dimensiunea populatiei
    int it; // numarul de iteratii
    double uc; // probabilitatea de crossover
    double um; // probabilitatea de mutatie
    double vm; // variatia mutatiei
    int geneSize;
    double low, high;
    boolean elitism;
    boolean maximize;
    String type;
    SimpleMatrix population; // populatia de cromozomi
    SimpleMatrix fitness_population; // fitnesul fiecarui cromozom
    SimpleMatrix normalized_population; // fitnesul fiecarui cromozom, normalizat
    SimpleMatrix cumulative_population; //
    int id;
    SimpleMatrix bestPopulation;
    RSA rsa_tmp;
    public GeneticAlgorithm() {

    }

    /**
     * @param m  // dimensiunea unui cromozom
     * @param n  // dimensiunea populatiei
     * @param it // numarul de iteratii
     * @param uc // probabilitatea de crossover
     * @param um // probabilitatea de mutatie
     */
    public GeneticAlgorithm(int m, int n, int it, double uc, double um, boolean elitism, int geneSize, int id, SimpleMatrix bestPopulation,RSA rsa_tmp) {
        this.m = m;
        this.n = n;
        this.it = it;
        this.uc = uc;
        this.um = um;

        this.elitism = elitism;
        this.geneSize = geneSize;
        this.type = "binary";
        this.id=id;
        this.bestPopulation=bestPopulation;
        this.rsa_tmp=rsa_tmp;
    }

    public GeneticAlgorithm(int m, int n, int it, double uc, double um, boolean elitism, double low, double high, int id, SimpleMatrix bestPopulation,RSA rsa_tmp) {
        this.m = m;
        this.n = n;
        this.it = it;
        this.uc = uc;
        this.um = um;

        this.elitism = elitism;
        this.low = low;
        this.high = high;
        this.type = "real";
        this.id=id;
        this.bestPopulation=bestPopulation;
        this.rsa_tmp=rsa_tmp;
    }

    /**
     * @param maximize           set this true if you want the GA to maximize the fitness function ( set it false if you want to minimize it )
     * @param crossoverAlgorithm the crossover algorithm
     * @return the population at the end of iterations
     * @throws Exception
     */
    synchronized public void start(boolean maximize, String crossoverAlgorithm) throws Exception {
        population = init();
        this.maximize = maximize;

        SimpleMatrix new_population;
        SimpleMatrix all_population = new SimpleMatrix(population.numRows(), population.numCols() * 2);

        int max = 6;
        int nr = 0;
        vm = um / max;
        int i = 0;

        double lum = 0;

        SimpleMatrix last_fittest = null;
        int count_fit = 0;


        do {
            if (i == 0) {
                last_fittest = getFittest();
            }

            if (i % 100 == 0) {

                nr++;

                if (um == .99) um = lum;

                if (getFitness(getFittest()) == (getFitness(last_fittest))) count_fit++;

                if (count_fit == 5) {

                    lum = um;
                    um = .99;
                    count_fit = 0;
                } else {
                    um += nr <= (max / 2) ? -vm : vm;
                }
                System.out.println("id:"+id+"\t"+i + " " + getFitness(getFittest()));

                last_fittest = getFittest();
                if (nr == max) nr = 0;
            }
            fitness_population = getPopulationFitness(population);
            sortPopulationByFitness(population, fitness_population);
            normalized_population = getNormalizedFitnes();
            cumulative_population = getCumulativeFitnes();

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

            population = makePopulationPrime(population);

            if (elitism) {
                all_population.insertIntoThis(0, 0, population);
                all_population.insertIntoThis(0, population.numCols(), new_population);
                sortPopulationByFitness(all_population, getPopulationFitness(all_population));

                population = all_population.extractMatrix(0, population.numRows(), 0, population.numCols());
            } else {
                population = new_population;
            }

            if(bestPopulation!=null&&i%100==0){

                all_population.insertIntoThis(0, 0, population);
                all_population.insertIntoThis(0, population.numCols(), bestPopulation);
                sortPopulationByFitness(all_population, getPopulationFitness(all_population));

                population = all_population.extractMatrix(0, population.numRows(), 0, population.numCols());
                bestPopulation.set(population);
            }

            i++;
            //System.out.println(binaryToString(getFittest()));
        } while (true);

        // fix all the values after last mutaion
//        fitness_population = getPopulationFitness(population);
//        sortPopulationByFitness(population, fitness_population);
//        normalized_population = getNormalizedFitnes();
//        cumulative_population = getCumulativeFitnes();
//
//        return population;
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

    private SimpleMatrix makePopulationPrime(SimpleMatrix population){
        int m = population.numRows();
        int n = population.numCols();
        SimpleMatrix tmp;
        for (int i = 0; i < n; i++) {
            tmp = population.extractVector(false, i);
            String string_chromosome = getBinaryCromosom(tmp);
            if(tmp.numRows() < 2*geneSize)
                System.out.println("ok");
            population.insertIntoThis(0, i, makeItPrime(tmp));
        }

        return population;
    }


    public SimpleMatrix makeItPrime(SimpleMatrix chromosome){
        String string_chromosome = getBinaryCromosom(chromosome);
        if(string_chromosome.length() < 2 * geneSize)
            System.out.println("ok");
        String string_p = string_chromosome.substring(0, geneSize);
        String string_q = string_chromosome.substring(geneSize, 2*geneSize);

        SimpleMatrix p = BigInteger2SimpleMatrix(new BigInteger(string_p, 2).nextProbablePrime(), geneSize);
        SimpleMatrix q = BigInteger2SimpleMatrix(new BigInteger(string_q, 2).nextProbablePrime(), geneSize);

        chromosome.insertIntoThis(0,0, p);
        chromosome.insertIntoThis(geneSize, 0, q);

        return chromosome;
    }


    public SimpleMatrix BigInteger2SimpleMatrix(BigInteger number, int size){
        String binary_number = number.toString(2);
        SimpleMatrix binary = new SimpleMatrix(size, 1);
        int i,j;

        for (i = size - 1, j = binary_number.length() - 1; j >= 0 && i >= 0; i--, j--) {
            binary.set(i, 0, binary_number.charAt(j) == '0' ? 0d : 1d);
        }

        if(binary_number.length() > size){
            binary = BigInteger2SimpleMatrix(new BigInteger(getBinaryCromosom(binary), 2).nextProbablePrime(), size);
        }

        return binary;
    }
}
