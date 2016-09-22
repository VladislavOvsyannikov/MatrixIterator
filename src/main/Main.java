package main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

public class Main {
    public static void main(String[] args){
        Matrix m1 = new SparseMatrix("src/inD1.txt");
        Matrix m2 = new DenseMatrix("src/inD2.txt");
        m1.mul(m2).toString();
//        int seed = 10;
//        int[][] m = new int[seed][seed];

//        for (int i = 0; i < seed; i++) {
//            for (int j = 0; j < seed; j++) {
//                Random rnd2 = new Random();
//                Random rnd = new Random();
//                int b = rnd2.nextInt(200);
//                for (int k = 0; k<b;k++){
//                    k=k+j;
//                    if (k>=seed) {k=k-seed;i=i+1;}
//                    m[i][k]=0;
//                }
//                j = j+b;
//                int a = rnd.nextInt(8)+1;
//                if (j<seed&&i<seed) {m[i][j] = a;}
//            }
//        }
//
//        for (int i = 0; i < seed; i++) {
//            for (int j = 0; j < seed; j++) {
//                Random rnd = new Random();
//                int a = rnd.nextInt(8)+1;
//                if (a!=0) {m[i][j] = a;}
//            }
//        }
//
//        PrintWriter out = null;
//        try {
//            out = new PrintWriter("src/inD2.txt");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        for (int i = 0; i < seed; i++) {
//            for (int j = 0; j < seed; j++) {
//                out.print(m[i][j]+"   ");
//                if (j==seed-1) out.println();
//            }
//        }
//        out.close();
    }
}
