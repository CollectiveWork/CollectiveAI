package ga;


import org.ejml.simple.SimpleMatrix;

/**
 * Created by AndreiMadalin on 3/12/14.
 */
public abstract class GeneticAlgorithm {
    int m; // dimensiunea unui cromozom
    int n; // dimensiunea populatiei
    int it; // numarul de iteratii
    double uc; // probabilitatea de crossover
    double um; // probabilitatea de mutatie
    int geneSize;
    double low, high;
    String type;
    SimpleMatrix population; // populatia de cromozomi
    SimpleMatrix fitness_population; // fitnesul fiecarui cromozom
    SimpleMatrix normalized_population; // fitnesul fiecarui cromozom, normalizat
    SimpleMatrix cumulative_population; //

    public GeneticAlgorithm() {

    }

    /**
     * @param m  // dimensiunea unui cromozom
     * @param n  // dimensiunea populatiei
     * @param it // numarul de iteratii
     * @param uc // probabilitatea de crossover
     * @param um // probabilitatea de mutatie
     */
    public GeneticAlgorithm(int m, int n, int it, double uc, double um, int geneSize) {
        this.m = m;
        this.n = n;
        this.it = it;
        this.uc = uc;
        this.um = um;
        this.geneSize = geneSize;
        this.type = "binary";
    }

    public GeneticAlgorithm(int m, int n, int it, double uc, double um, double low, double high) {
        this.m = m;
        this.n = n;
        this.it = it;
        this.uc = uc;
        this.um = um;
        this.low = low;
        this.high = high;
        this.type = "real";
    }


    public SimpleMatrix start(String crossoverAlgorithm) throws Exception {
        population = init();

        int i = 0;
        do {
            fitness_population = getPopulationFitness(population);
            sortPopulationByFitness();
            normalized_population = getNormalizedFitnes();
            cumulative_population = getCumulativeFitnes();

            population = Population.rouletteWheelSelection(population, cumulative_population);

            switch (crossoverAlgorithm) {
                case "singlePointCrossover":
                    population = GeneticOperations.singlePointCrossover(type, population, uc);
                    break;
                case "doublePointCrossover":
                    population = GeneticOperations.doublePontCrossover(type, population, uc);
                    break;
                case "multiPointCrossover":
                    population = GeneticOperations.multiPointCrossover(type, population, uc);
                    break;
                default:
                    throw new Exception("Unrecognised crossover algorithm");
            }

            if(type.equals("binary"))
                population = GeneticOperations.mutation(population, um);
            else
                population = GeneticOperations.mutation(population, um, high - low);

            i++;
        } while (i < it);

        // fix all the values after last mutaion
        fitness_population = getPopulationFitness(population);
        sortPopulationByFitness();
        normalized_population = getNormalizedFitnes();
        cumulative_population = getCumulativeFitnes();

        return population;
    }

    private SimpleMatrix init(){
        if(type.equals("binary"))
            return Population.init(m, n);
        else
            return Population.init(m, n, low, high);
    }

    private SimpleMatrix getPopulationFitness(SimpleMatrix population){
        if (type.equals("binary"))
            return getBinaryPopulationFitness(population);
        else
            return getRealPopulationFitness(population);
    }


    private SimpleMatrix getBinaryPopulationFitness(SimpleMatrix population){
        int n = population.numCols();
        int m = population.numRows();
        int dim = m / geneSize;
        SimpleMatrix fitness = new SimpleMatrix(1, n);
        String strCromosom;

        for (int i = 0; i < n; i++) {
            strCromosom = getBinaryCromosom(population.extractVector(false, i));
            fitness.set(i, fitness(getParamsFromCromozom(strCromosom)));
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

    private void sortPopulationByFitness() {
        boolean s;
        int n = fitness_population.numCols();
        double temp1;
        SimpleMatrix temp2;
        do {
            s=false;
            for (int i = 0; i < n - 1; i++) {
                if (fitness_population.get(i) > fitness_population.get(i + 1)) {
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

    public SimpleMatrix getFittest(String type) throws Exception {
        if(type.equals("max"))
            return population.extractVector(false, population.numCols()-1);
        if(type.equals("min"))
            return population.extractVector(false, 0);

        throw new Exception("Wrong fittest type");
    }

    public String getBinaryCromosom(SimpleMatrix cromosom) {
        StringBuilder s = new StringBuilder();
        int m = cromosom.numRows();
        for (int i = 0; i < m; i++) {
            s.append(cromosom.get(i) == 0 ? '0' : '1');
        }

        return s.toString();
    }

    public SimpleMatrix getParamsFromCromozom(String cromozom){
        int n = cromozom.length() / geneSize;
        SimpleMatrix params = new SimpleMatrix(n,1);
        for (int i = 0; i < n; i++)
            params.set(i, Integer.parseInt(cromozom.substring(i * geneSize, (i + 1) * geneSize), 2));

        return params;
    }

    protected abstract double fitness(SimpleMatrix cromosom);

    public double getFitness(SimpleMatrix cromosom){
        if(type.equals("binary"))
            return fitness(getParamsFromCromozom(getBinaryCromosom(cromosom)));
        else
            return fitness(cromosom);
    }
}
