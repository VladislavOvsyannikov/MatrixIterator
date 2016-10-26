package main;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class SparseMatrix implements Matrix{
    public int size;
    public ConcurrentHashMap<Integer,Row> map;

    public SparseMatrix(String file){
        readSparse(file);
    }

    public SparseMatrix(int size){
        this.map = new ConcurrentHashMap<Integer,Row>();
        this.size = size;
    }

    public Matrix mul(Matrix x){
        if (x instanceof SparseMatrix) try {
            return mulSparseSparse((SparseMatrix) x);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        else return mulSparseDense((DenseMatrix) x);
    }

    public SparseMatrix mulSparseSparse(SparseMatrix other) throws InterruptedException {
        transSparce(other.map);
        SparseMatrix result = new SparseMatrix(size);

        Iterator<ConcurrentHashMap.Entry<Integer, Row>> iterMap1 = this.map.entrySet().iterator();

        Thread t1 = new Thread(new MulSS(result.map,this.map,other.map,iterMap1));
        Thread t2 = new Thread(new MulSS(result.map,this.map,other.map,iterMap1));
        Thread t3 = new Thread(new MulSS(result.map,this.map,other.map,iterMap1));
        Thread t4 = new Thread(new MulSS(result.map,this.map,other.map,iterMap1));
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t1.join();
        t2.join();
        t3.join();
        t4.join();

// Iterator<HashMap.Entry<Integer, Row>> iterMap1 = this.map.entrySet().iterator();
// while (iterMap1.hasNext()) {
// HashMap.Entry<Integer, Row> entry1 = iterMap1.next();
// Row row1 = entry1.getValue();
// Row resRow = new Row();
// Iterator<HashMap.Entry<Integer, Row>> iterMap2 = other.map.entrySet().iterator();
// while (iterMap2.hasNext()) {
// HashMap.Entry<Integer, Row> entry2 = iterMap2.next();
// Row row2 = entry2.getValue();
// if (!row1.mapRow.isEmpty() && !row2.mapRow.isEmpty()) {
// Iterator<HashMap.Entry<Integer, Integer>> iterRow1 = row1.mapRow.entrySet().iterator();
// int r = 0;
// while (iterRow1.hasNext()) {
// HashMap.Entry<Integer, Integer> entry3 = iterRow1.next();
// int j = entry3.getKey();
// if (row2.mapRow.get(j)!=null){
// r = r + entry3.getValue()*row2.mapRow.get(j);
// resRow.mapRow.put(entry2.getKey(),r);
// }
// }
// }
// }
// res.map.put(entry1.getKey(), resRow);
// }

        return result;
    }

    public static class MulSS implements Runnable {
        ConcurrentHashMap<Integer, Row> A;
        ConcurrentHashMap<Integer, Row> B;
        ConcurrentHashMap<Integer, Row> res;
        static Iterator<HashMap.Entry<Integer, Row>> E;

        public MulSS(ConcurrentHashMap<Integer, Row> res,ConcurrentHashMap<Integer, Row> A, ConcurrentHashMap<Integer, Row> B, Iterator<HashMap.Entry<Integer, Row>> E) {
            this.A = A;
            this.B = B;
            this.E = E;
            this.res = res;
        }

        public void run() {
            for  (Row row1 = getFreeRow(E);row1!=null;row1 = getFreeRow(E)) {
                Row resRow = new Row();
                Iterator<HashMap.Entry<Integer, Row>> iterMap2 = B.entrySet().iterator();
                while (iterMap2.hasNext()) {
                    HashMap.Entry<Integer, Row> entry2 = iterMap2.next();
                    Row row2 = entry2.getValue();
                    if (!row1.mapRow.isEmpty() && !row2.mapRow.isEmpty()) {
                        Iterator<HashMap.Entry<Integer, Integer>> iterRow1 = row1.mapRow.entrySet().iterator();
                        int r = 0;
                        while (iterRow1.hasNext()) {
                            HashMap.Entry<Integer, Integer> entry3 = iterRow1.next();
                            int j = entry3.getKey();
                            if (row2.mapRow.get(j) != null) {
                                r = r + entry3.getValue() * row2.mapRow.get(j);
                            }
                        }
                        resRow.mapRow.put(entry2.getKey(), r);
                    }
                }
                res.put(row1.index, resRow);
            }
        }
        public synchronized Row getFreeRow(Iterator<HashMap.Entry<Integer, Row>> E) {
            if (E.hasNext()) {
                HashMap.Entry<Integer, Row> entry1 = E.next();
                Row row1 = entry1.getValue();
                row1.index = entry1.getKey();
                return row1;
            } else {return null;}
        }
    }

    public void transSparce(ConcurrentHashMap<Integer, Row> m){
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
        this.map = new ConcurrentHashMap<>();
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