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
public class FriendsParser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        BufferedReader br = new BufferedReader(new FileReader("friends"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("friends.json"));
        String cols = "{\"cols\":["
                + "{\"label\":\"location\",\"type\":\"string\"},"
                + "{\"label\":\"small\",\"type\":\"number\"},"
                + "{\"label\":\"medium\",\"type\":\"number\"},"
                + "{\"label\":\"large\",\"type\":\"number\"}"
                + "],\"rows\":[";
        bw.write(cols);
        String rows = "";
        String str = null;
        String pre = null;
        while ((str = br.readLine()) != null) {
            if (pre != null) {
                String[] total = pre.split("\t");
                String first = total[0];
                String[] second = total[1].split(" ");
                rows = "{\"c\":["
                        + "{\"v\":\"" + first + "\"},"
                        + "{\"v\":" + second[0] + "},"
                        + "{\"v\":" + second[1] + "},"
                        + "{\"v\":" + second[2] + "}"
                        + "]},";
                bw.write(rows);
            }
            pre = str;
        }
        String[] total = pre.split("\t");
        String first = total[0];
        String[] second = total[1].split(" ");
        rows = "{\"c\":["
                + "{\"v\":\"" + first + "\"},"
                + "{\"v\":" + second[0] + "},"
                + "{\"v\":" + second[1] + "},"
                + "{\"v\":" + second[2] + "}"
                + "]}"
                + "]}";
        bw.write(rows);
        br.close();
        bw.flush();
        bw.close();
    }
}
