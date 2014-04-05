package ga;


import org.ejml.simple.SimpleMatrix;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by AndreiMadalin on 3/12/14.
 */
public class Population {

    /**
     * initializare cu gene binare
     *
     * @param m numarul de vatiabile ale unui cromozom
     * @param n dimensiunea populatiei de cromozomi
     * @return pop matrice de dimensiune m x n
     */
    public static SimpleMatrix init(int m, int n) {
        SimpleMatrix pop = SimpleMatrix.random(m, n, 0, 1, new Random());
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                pop.set(i, j, Math.round(pop.get(i, j)));
            }
        }
        return pop;
    }

    /**
     * initializarea cu variabile reale
     *
     * @param m numarul de vatiabile ale unui cromozom
     * @param n dimensiunea populatiei de cromozomi
     * @return pop matrice de dimensiune m x n
     */
    public static SimpleMatrix init(int m, int n, double low, double high) {
        return SimpleMatrix.random(m, n, low, high, new Random());
    }

    /**
     * @param parent matrice de dimensiuni m x n
     * @param cpop   vector ce cotine valorile cumulate ale functiei de fitness normalizate calculate pentur fiecare individ
     * @return parent matrice de dimensiuni m x n
     */
    public static SimpleMatrix rouletteWheelSelection(SimpleMatrix parent, SimpleMatrix cpop) {
        int m = parent.numRows();
        int n = parent.numCols();
        SimpleMatrix pop = new SimpleMatrix(m, n);
        double U;
        Random rand = new Random();


        for (int i = 0; i < n; i++) {
            U = rand.nextDouble();
            for (int j = 0; j < n; j++) {
                if (((j == 0) ? 0 <= U : cpop.get(j - 1) <= U) && U < cpop.get(j)) {
                    pop.insertIntoThis(0, i, parent.extractVector(false, j));
                    break;
                }
            }
        }

        return pop;
    }
}
