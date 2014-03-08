package ga;

import org.ejml.simple.SimpleMatrix;

import java.util.Random;

/**
 * Created by Gabriel on 3/8/14.
 */
public class GeneticOperations {

    /**
     * @param parents
     * @param uc      probability of crossover ( usually 0.65 - 0
     */
    public static SimpleMatrix crossover(SimpleMatrix parents, int uc) {
        int n = parents.numRows();
        SimpleMatrix pop = new SimpleMatrix(n, parents.numCols());

        Random rnd = new Random();
        double U;

        if (n % 2 == 1) {
            n--;
        }
        int j=0;
        for (int i = 0; i < n ; i += 2) {
           U = rnd.nextDouble();
           if(U<uc){

               SimpleMatrix parents2 = new SimpleMatrix(2,parents.numCols());
               parents2.insertIntoThis(0,0,parents.extractVector(true,i));
               parents2.insertIntoThis(1,0,parents.extractVector(true,i+1));
               pop.insertIntoThis(j, 0, methodSimpleCrossOver(parents2));
               j+=2;
           }

        }


        return pop;
    }

    private static SimpleMatrix methodSimpleCrossOver(SimpleMatrix parents){
       SimpleMatrix pop = parents;


       return pop;
    }
}
