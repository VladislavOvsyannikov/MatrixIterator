package test;

import main.DenseMatrix;
import main.Matrix;
import main.SparseMatrix;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import static org.junit.Assert.assertTrue;

public class MulTest {

    @Test
    public void mulMatrixTest(){

        Matrix m1 = new DenseMatrix("src/inD1.txt");
        Matrix m2 = new DenseMatrix("src/inD2.txt");

        long startTime = System.nanoTime();
        m1.mul(m2).toString();
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Execution time(ms) " + (estimatedTime / 1000000));

        Scanner out = null;
        Scanner goldResult = null;
        try {
            out = new Scanner(new File("src/out.txt"));
            goldResult = new Scanner(new File("src/goldResult.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (out.hasNextInt()||goldResult.hasNextInt()) {
            int a = out.nextInt();
            int b = goldResult.nextInt();
            assertTrue("Error", a==b);
        }
        goldResult.close();
        out.close();
    }
}