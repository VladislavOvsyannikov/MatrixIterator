package main;

import java.io.*;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Scanner;

public class DenseMatrix implements Matrix {
    public int size;
    public int matrix[][];

    public DenseMatrix(String file){
        readDense(file);
    }

    public DenseMatrix(int size){
        this.matrix = new int[size][size];
        this.size = size;
    }

    public Matrix mul(Matrix x){
        if (x instanceof DenseMatrix) return mulDenseDense((DenseMatrix) x);
        else return mulDenseSparse((SparseMatrix) x);
    }

    public DenseMatrix mulDenseDense(DenseMatrix other){
        transDense(other.matrix);
        DenseMatrix res = new DenseMatrix(size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    res.matrix[i][j] = res.matrix[i][j] + this.matrix[i][k] * other.matrix[j][k];
                }
            }
        }
        return res;
    }

    public DenseMatrix mulDenseSparse(SparseMatrix other){
        DenseMatrix res = new DenseMatrix(size);
        transSparce(other.map);
        for (int i = 0; i < size; i++) {
            Iterator<HashMap.Entry<Integer, Row>> iterMap2 = other.map.entrySet().iterator();
            while (iterMap2.hasNext()) {
                HashMap.Entry<Integer, Row> entry2 = iterMap2.next();
                Row row2 = entry2.getValue();
                if (!row2.mapRow.isEmpty()) {
                    Iterator<HashMap.Entry<Integer, Integer>> iterRow1 = row2.mapRow.entrySet().iterator();
                    while (iterRow1.hasNext()) {
                        HashMap.Entry<Integer, Integer> entry3 = iterRow1.next();
                        int k = entry3.getKey();
                        int j = entry2.getKey();
                        res.matrix[i][j-1] = res.matrix[i][j-1] + this.matrix[i][k-1] * row2.mapRow.get(k);
                    }
                }
            }
        }
        return res;
    }


    public void transDense(int[][] a){
        for (int i=0;i<size-1;i++){
            for (int j=i+1;j<size;j++){
                int b=a[i][j];
                a[i][j]=a[j][i];
                a[j][i]=b;
            }
        }
    }

    public static int getSize(String file){
        int size=0;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String str = in.readLine();
            String[] arr = str.split("\\s+");
            size = arr.length;
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    public void readDense(String file){
        this.size = getSize(file);
        Scanner in = null;
        try {
            in = new Scanner(new File(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.matrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.matrix[i][j] = in.nextInt();
            }
        }
        in.close();
    }

    public String toString(){
        PrintWriter out = null;
        try {
            out = new PrintWriter("src/out.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                out.print(this.matrix[i][j]+"   ");
                if (j==size-1) out.println();
            }
        }
        out.close();
        return null;
    }

    public void transSparce(HashMap<Integer, Row> m){
        HashMap<Integer,Integer> g = new HashMap<>();
        for (int i=1;i<size;i++){
            for (int j=i+1;j<=size;j++) {
                g.put(0,m.get(i).mapRow.get(j));
                m.get(i).mapRow.put(j, m.get(j).mapRow.get(i));
                m.get(j).mapRow.put(i, g.get(0));
            }
        }
    }
}



