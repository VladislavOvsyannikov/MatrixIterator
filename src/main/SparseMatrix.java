package main;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class SparseMatrix implements Matrix{
    public int size;
    public HashMap<Integer,Row> map;

    public SparseMatrix(String file){
        readSparse(file);
    }

    public SparseMatrix(int size){
        this.map = new HashMap<>();
        this.size = size;
    }

    public Matrix mul(Matrix x){
        if (x instanceof SparseMatrix) return mulSparseSparse((SparseMatrix) x);
        else return mulSparseDense((DenseMatrix) x);
    }

    public SparseMatrix mulSparseSparse(SparseMatrix other){
        transSparce(other.map);
        SparseMatrix res = new SparseMatrix(size);
        Iterator<HashMap.Entry<Integer, Row>> iterMap1 = this.map.entrySet().iterator();
        while (iterMap1.hasNext()) {
            HashMap.Entry<Integer, Row> entry1 = iterMap1.next();
            Row row1 = entry1.getValue();
            Row resRow = new Row();
            Iterator<HashMap.Entry<Integer, Row>> iterMap2 = other.map.entrySet().iterator();
            while (iterMap2.hasNext()) {
                HashMap.Entry<Integer, Row> entry2 = iterMap2.next();
                Row row2 = entry2.getValue();
                if (!row1.mapRow.isEmpty() && !row2.mapRow.isEmpty()) {
                    Iterator<HashMap.Entry<Integer, Integer>> iterRow1 = row1.mapRow.entrySet().iterator();
                    int r = 0;
                    while (iterRow1.hasNext()) {
                        HashMap.Entry<Integer, Integer> entry3 = iterRow1.next();
                        int j = entry3.getKey();
                        if (row2.mapRow.get(j)!=null){
                            r = r + entry3.getValue()*row2.mapRow.get(j);
                            resRow.mapRow.put(entry2.getKey(),r);
                        }
                    }
                }
            }
            res.map.put(entry1.getKey(), resRow);
        }
        return res;
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

    public SparseMatrix mulSparseDense(DenseMatrix other){
        SparseMatrix res = new SparseMatrix(size);
        transDense(other.matrix);
        Iterator<HashMap.Entry<Integer, Row>> iterMap1 = this.map.entrySet().iterator();
        while (iterMap1.hasNext()) {
            HashMap.Entry<Integer, Row> entry1 = iterMap1.next();
            Row row1 = entry1.getValue();
            Row resRow = new Row();
            if (!row1.mapRow.isEmpty()){
                for (int k = 0; k < size; k++) {
                    Iterator<HashMap.Entry<Integer, Integer>> iterRow1 = row1.mapRow.entrySet().iterator();
                    int r = 0;
                    while (iterRow1.hasNext()) {
                        HashMap.Entry<Integer, Integer> entry3 = iterRow1.next();
                        int j = entry3.getKey();
                        r = r + other.matrix[k][j-1]*row1.mapRow.get(j);
                        resRow.mapRow.put(k+1,r);
                    }
                }
            }
            res.map.put(entry1.getKey(), resRow);
        }
        return res;
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

    public void readSparse(String file){
        this.size=getSize(file);
        Scanner in = null;
        try {
            in = new Scanner(new File(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.map = new HashMap<>();
        for (int i = 1; i <= size; i++) {
            Row row = new Row();
            for (int j = 1; j <= size; j++) {
                int a = in.nextInt();
                if (a != 0) {
                    row.mapRow.put(j,a);
                }
            }
            map.put(i,row);
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
        for (int i = 1; i <= size; i++) {
            Row row = map.get(i);
            for (int j = 1; j <= size; j++) {
                if (row.mapRow.get(j)==null) out.print(0+"  "); else out.print(row.mapRow.get(j)+"  ");
                if (j==size) out.println();
            }
        }
        out.close();
        return null;
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
}