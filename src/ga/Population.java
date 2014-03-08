package ga;
import org.ejml.*;
import org.ejml.data.Matrix64F;
import org.ejml.simple.SimpleMatrix;

import java.util.Random;

/**
 * Created by  Gabriel, Andrei, Root on 3/8/14.
 */
public class Population {
    SimpleMatrix population;
    private int m,n;
    /**
     *
     * @param m numarul de varabile a unui cromozom
     * @param n dimensiunea populatii de cromozomi
     */
    public Population(int m, int n){
        this.m = m;
        this.n = n;
    }

    public void initBinary(){
        population = SimpleMatrix.random(n,m,0,1,new Random());
    }

    public SimpleMatrix rouletteWheelSelection(SimpleMatrix pop, SimpleMatrix cpop){
        int numRows = pop.numRows();
        int numCols = pop.numCols();
        int U;

        SimpleMatrix parent = new SimpleMatrix(numRows, numCols);
        Random rand = new Random();

        for (int i = 0; i < numRows; i++) {
            U = rand.nextInt(1);
            for (int j = 0; j < numRows; j++) {
                if(cpop.get(i,j - 1) <= U && U < cpop.get(i,j))
                    parent.insertIntoThis(i,0,pop.extractVector(true, j));
            }
        }
        return parent;
    }

}
