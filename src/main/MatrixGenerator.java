package main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public class MatrixGenerator
{
    public static final int SEED1 = 1;
    public static final int SEED2 = 2;
    public static final int EMPTY_ROW_FRACTION = 1;

    public static final String MATRIX1_NAME = "src/inS1.txt";
    public static final String MATRIX2_NAME = "src/inS2.txt";
    public static final int SIZE = 16;

    private final int emptyRowFraction;
    private final int size;
    private final String emptyRow;
    private final Random rnd;
    private final String file;

    public MatrixGenerator(int seed, int emptyRowFraction, String file, int size)
    {
        this.emptyRowFraction = emptyRowFraction;
        this.size = size;
        this.file = file;
        rnd = new Random(seed);
        emptyRow = Collections.nCopies(size, "0").stream().collect(Collectors.joining(" "));
    }

    public static void main(String args[])
    {
        try
        {
            new MatrixGenerator(SEED1, EMPTY_ROW_FRACTION, MATRIX1_NAME, SIZE).generate();
            new MatrixGenerator(SEED2, EMPTY_ROW_FRACTION, MATRIX2_NAME, SIZE).generate();
            testPerformance();
        }
        catch (IOException e)
        {
            System.out.println("Fail to generate matrix file: " + e);
        }
    }

    private static void testPerformance()
    {

    System.out.println("Starting loading dense matrices");
    Matrix m1 = new DenseMatrix(MATRIX1_NAME);
    System.out.println("1 loaded");
    Matrix m2 = new DenseMatrix(MATRIX2_NAME);
    System.out.println("2 loaded");
    long start = System.currentTimeMillis();
    m1.mul(m2).toString();
    System.out.println("Dense Matrix time: " +(System.currentTimeMillis() - start));

//    System.out.println("Starting loading sparse matrices");
//     m1 = new SparseMatrix(MATRIX1_NAME);
//    System.out.println("1 loaded");
//     m2 = new SparseMatrix(MATRIX2_NAME);
//    System.out.println("2 loaded");
//     start = System.currentTimeMillis();
//    m1.mul(m2);
//    System.out.println("Sparse Matrix time: " +(System.currentTimeMillis() - start));

    }

    public void generate() throws IOException
    {
        PrintWriter out = new PrintWriter(new FileWriter(file));
        for (int i = 0; i < size; i++)
        {
            if (rnd.nextInt(emptyRowFraction) == 0)
                out.println(generateRow());
            else
                out.println(emptyRow);
        }
        out.close();
    }

    private String generateRow()
    {
        return rnd.ints(0, emptyRowFraction).limit(size).mapToObj(r -> (r == 0) ? "" + rnd.nextInt(10) : "0")
                .collect(Collectors.joining(" "));
    }
}