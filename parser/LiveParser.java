/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Eric
 */
public class LiveParser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        BufferedReader br = new BufferedReader(new FileReader("live"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("live.json"));
        String cols = "{\"cols\":["
                + "{\"label\":\"location\",\"type\":\"string\"},"
                + "{\"label\":\"uf\",\"type\":\"number\"},"
                + "],\"rows\":[";
        bw.write(cols);
        String rows = "";
        String str = null;
        String pre = null;
        while ((str = br.readLine()) != null) {
            if (pre != null) {
                String[] total = pre.split("\t");
                rows = "{\"c\":["
                        + "{\"v\":\"" + total[0] + "\"},"
                        + "{\"v\":" + total[1] + "},"
                        + "]},";
                bw.write(rows);;
            }
            pre = str;
        }
        String[] total = pre.split("\t");
        rows = "{\"c\":["
                + "{\"v\":\"" + total[0] + "\"},"
                + "{\"v\":" + total[1] + "},"
                + "]}"
                + "]}";
        bw.write(rows);
        br.close();
        bw.flush();
        bw.close();
    }
}
