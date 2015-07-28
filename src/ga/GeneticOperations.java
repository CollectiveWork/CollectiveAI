package ga;


import org.ejml.simple.SimpleMatrix;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by AndreiMadalin on 3/12/14.
 */
public class GeneticOperations {

    /**
     * @param parents matrice de forma m x n (m - dimensiunea unui cromozom, n - dimensiunea populatiei)
     * @param uc      probabilitatea de crossover
     * @return
     */
    public static SimpleMatrix singlePointCrossover(String type, SimpleMatrix parents, double uc) {
        return crossover(type, parents, uc, 1);
    }

    public static SimpleMatrix doublePointCrossover(String type, SimpleMatrix parents, double uc) {
        return crossover(type, parents, uc, 2);
    }

    public static SimpleMatrix multiPointCrossover(String type, SimpleMatrix parents, double uc) {
        Random rand = new Random();
        int l = rand.nextInt(parents.numRows() - 3) + 3;

        return crossover(type, parents, uc, l);
    }

    /**
     * @param parents matrice de forma m x n (m - dimensiunea unui cromozom, n - dimensiunea populatiei)
     * @param uc      probabilitatea de crossover
     * @param l       numarul de pozitii de taiere pentru crossover
     * @return
     */
    private static SimpleMatrix crossover(String type, SimpleMatrix parents, double uc, int l) {
        int n = parents.numCols();
        int m = parents.numRows();
        SimpleMatrix pop = new SimpleMatrix(m, n);
        double U;
        Random rand = new Random();


        if (n % 2 == 1) {
            n--;
            pop.insertIntoThis(0, n, parents.extractVector(false, n));
        }
        for (int i = 0; i < n; i += 2) {
            U = rand.nextDouble();
            if (U < uc) {
                switch (type) {
                    case "binary":
                        pop.insertIntoThis(0, i, multiPointBinaryCrossover(parents.extractMatrix(0, m, i, i + 2), l));
                        break;
                    case "real":
                        pop.insertIntoThis(0, i, realCrossover(parents.extractMatrix(0, m, i, i + 2)));
                        break;
                }
            }
        }

        return pop;
    }

    /**
     * @param parents matrice de forma m x n (m - dimensiunea unui cromozom, n - dimensiunea populatiei)
     * @param l       numarul de pozitii de taiere pentru crossover
     * @return
     */
    private static SimpleMatrix multiPointBinaryCrossover(SimpleMatrix parents, int l) {
        int n = parents.numCols(); // dimensiunea populatiti
        int m = parents.numRows(); // dimeansiunea unui cromozom
        SimpleMatrix pop = new SimpleMatrix(parents);
        int positions[] = generateRandom(l, m);
        SimpleMatrix tmpA, tmpB;

        if (l % 2 == 1) {
            l--;

            tmpA = pop.extractMatrix(positions[l], m, 0, 1);
            tmpB = pop.extractMatrix(positions[l], m, 1, 2);

            pop.insertIntoThis(positions[l], 0, tmpB);
            pop.insertIntoThis(positions[l], 1, tmpA);
        }

        for (int i = 0; i < l; i += 2) {
            tmpA = pop.extractMatrix(positions[i], positions[i + 1], 0, 1);
            tmpB = pop.extractMatrix(positions[i], positions[i + 1], 1, 2);

            pop.insertIntoThis(positions[i], 0, tmpB);
            pop.insertIntoThis(positions[i], 1, tmpA);
        }

        return pop;
    }

    private static SimpleMatrix realCrossover(SimpleMatrix parents) {
        int n = parents.numCols();
        int m = parents.numRows();
        SimpleMatrix pop = new SimpleMatrix(m, n);
        double d = 0.25;
        double a[], b[];
        double tempA, tempB;

        a = generateRandom(m, d, new Random());
        b = generateRandom(m, d, new Random());
        for (int j = 0; j < m; j++) {
            tempA = parents.get(j, 0);
            tempB = parents.get(j, 1);
            pop.set(j, 0, tempA * (1 - a[j]) + tempB * a[j]);
            pop.set(j, 1, tempA * (1 - b[j]) + tempB * b[j]);
        }

        return pop;
    }

//    public static SimpleMatrix mutation(String type, SimpleMatrix parents, double um){
//        if(type.equals("binary"))
//            return binaryMutation(parents, um);
//        else
//            return realMutation(parents, um);
//    }

    public static SimpleMatrix mutation(SimpleMatrix parents, double um) {
        Random rand = new Random();
        int n = parents.numCols(); // dimensiunea populatiti
        int m = parents.numRows(); // dimeansiunea unui cromozom

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (rand.nextDouble() < um) {
                    parents.set(j, i, (parents.get(j, i) == 0 ? 1 : 0));
                }
            }
        }

        return parents;
    }

    // TODO de parametrizat k, r ????
    public static SimpleMatrix mutation(SimpleMatrix parents, double um, double domain) {
        Random rand = new Random();
        Random rand2 = new Random();
        Random rand3 = new Random();
        int n = parents.numCols(); // dimensiunea populatiti
        int m = parents.numRows(); // dimeansiunea unui cromozom
        double new_val;
        int s;
        int k = 7; // k apartine {4,5,...,20}
        double r = Math.pow(10, -2); // intervalul de mutatie ( r apartine [Math.pow(10,-6), 0.1] )
        double a;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (rand.nextDouble() < um) {
                    s = Math.round(rand2.nextDouble()) == 0 ? -1 : 1;
                    a = Math.pow(2, -1 * rand3.nextDouble() * k);
                    new_val = parents.get(j, i) + s * (r * domain) * a;
                    parents.set(j, i, new_val);
                }
            }
        }

        return parents;
    }

    private static int[] generateRandom(int l, int m) {
        Random rand = new Random();
        int positions[] = new int[l];
        int i = 0;
        boolean g;
        while (i < l) {
            g = false;
            positions[i] = rand.nextInt(m - 1) + 1;
            for (int j = 0; j < i; j++)
                if (positions[i] == positions[j]) {
                    g = true;
                    break;
                }
            if (!g)
                i++;
        }
        Arrays.sort(positions);
        return positions;
    }

    // metoda de generare a factorului de scalare pentru crossover real
    private static double[] generateRandom(int n, double d, Random rand) {
        double low = -1 * d;
        double high = d + 1;
        double a[] = new double[n];
        for (int i = 0; i < n; i++) {
            a[i] = (high - low) * rand.nextDouble() + low;
        }

        return a;
    }

}
