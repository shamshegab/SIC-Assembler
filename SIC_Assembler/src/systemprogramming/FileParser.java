/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package systemprogramming;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Shams Sherif
 */
public class FileParser {

    private String prgmname, startaddr;
    private ArrayList<String> labels = new ArrayList(), nem = new ArrayList(), addr = new ArrayList();
    private ArrayList<Long> indexed = new ArrayList<Long>();
    int[] locations;

    public String getPrgmname() {
        return prgmname;
    }

    private void setPrgmname(String prgmname) {
        this.prgmname = prgmname;
    }

    public int getStartaddr() {
        int result = Integer.parseInt(startaddr, 16);
        return result;
    }

    private void setStartaddr(String startaddr) {
        this.startaddr = startaddr;
    }
    static HashMap<String, String> mnemonic = new HashMap<>();

    public FileParser(String path) throws FileNotFoundException, IOException {
        mnemonic.put("LDA", "00");
        mnemonic.put("LDB", "68");
        mnemonic.put("LDA", "00");
        mnemonic.put("LDCH", "50");
        mnemonic.put("LDF", "70");
        mnemonic.put("LDL", "08");
        mnemonic.put("LDS", "6C");
        mnemonic.put("LDT", "74");
        mnemonic.put("LDX", "04");
        mnemonic.put("LPS", "D0");
        mnemonic.put("LDA", "00");
        mnemonic.put("MUL", "20");
        mnemonic.put("J", "3C");
        mnemonic.put("JEQ", "30");
        mnemonic.put("JGT", "34");
        mnemonic.put("JLT", "38");
        mnemonic.put("JSUB", "48");
        mnemonic.put("ADD", "18");
        mnemonic.put("ADDF", "58");
        mnemonic.put("ADDR", "90");
        mnemonic.put("AND", "40");
        mnemonic.put("CLEAR", "B4");
        mnemonic.put("COMP", "28");
        mnemonic.put("COMPF", "88");
        mnemonic.put("COMPR", "A0");
        mnemonic.put("DIV", "24");
        mnemonic.put("DIVF", "64");
        mnemonic.put("DIVR", "9C");
        mnemonic.put("FIX", "C4");
        mnemonic.put("FLOAT", "C0");
        mnemonic.put("HIO", "F4");
        mnemonic.put("MULF", "60");
        mnemonic.put("MULR", "98");
        mnemonic.put("NORM", "C8");
        mnemonic.put("OR", "44");
        mnemonic.put("OR", "44");
        mnemonic.put("RD", "D8");
        mnemonic.put("RMO", "AC");
        mnemonic.put("RSUB", "4C");
        mnemonic.put("SHIFTL", "A4");
        mnemonic.put("SHIFTR", "A8");
        mnemonic.put("SIO", "F0");
        mnemonic.put("SUBR", "94");
        mnemonic.put("TD", "E0");
        mnemonic.put("TIO", "F8");
        mnemonic.put("TIX", "2C");
        mnemonic.put("TIXR", "B8");
        mnemonic.put("WD", "DC");
        mnemonic.put("OR", "44");
        mnemonic.put("STA", "0C");
        mnemonic.put("STL", "14");

        mnemonic.put("STCH", "54");
        mnemonic.put("STX", "10");
        parse(path);
    }

    public String getNemonicAdss(String label) {
        return mnemonic.get(label);
    }

    public boolean containsNemonic(String label) {
        return mnemonic.containsKey(label);
    }

    private void parse(String path) throws FileNotFoundException, IOException {
        Scanner br = new Scanner(new FileReader(path));
        String first = br.nextLine();
        first = first.replace(" ", "-");
        //System.out.println(first);
        String n[] = first.split("-");
        ArrayList<String> command = new ArrayList();
        for (int i = 0; i < n.length; i++) {
            if (n[i].matches(".*\\w.*")) {
                command.add(n[i].trim());
            }

        }
        setPrgmname(command.get(0));
        setStartaddr(command.get(2));
        int k = 0;
        while (br.hasNext()) {
            String line = br.nextLine();
            line = line.replace(" ", "-");
            String inst[] = line.split("-");

            ArrayList<String> lininst = new ArrayList();
            for (int i = 0; i < inst.length; i++) {
                if (inst[i].matches(".*\\w.*")) {
                    lininst.add(inst[i].trim());
                    // System.out.println(inst[i]);
                }
            }
            if (lininst.size() > 0) {
                if (lininst.get(0) != "END" && lininst.get(0) != "end" && !(lininst.get(0).contains("END"))) {

                    if (lininst.size() == 1) {
                        labels.add("no");
                        nem.add(lininst.get(0));
                        addr.add("null");
                        indexed.add(0l);
                    } else if (mnemonic.containsKey(lininst.get(0))) {
                        labels.add("no");
                        nem.add(lininst.get(0));

                        if (lininst.get(1).contains(",")) {
                            addr.add(lininst.get(1).substring(0, lininst.get(1).length() - 2));
                            indexed.add(1l);
                        } else {
                            indexed.add(0l);
                            addr.add(lininst.get(1));
                        }
                    } else {

                        labels.add(lininst.get(0));
                        nem.add(lininst.get(1));
                        if (lininst.get(2).contains(",")) {
                            addr.add(lininst.get(2).substring(0, lininst.get(2).length() - 2));
                            indexed.add(1l);
                        } else {
                            indexed.add(0l);
                            addr.add(lininst.get(2));
                        }
                    }

                }
            }
        }
        /*  for (int i = 0; i < labels.size(); i++) {
            System.out.println(labels.get(i)+"  "+nem.get(i)+"  "+addr.get(i)+"  "+indexed.get(i));
        }
         */
    }

    public int getlen() {
        return labels.size();
    }

    public String getLabel(int j) {
        return labels.get(j);
    }

    public String getNem(int j) {
        return nem.get(j);
    }

    public String getadd(int j) {
        return addr.get(j);
    }

    public long getindexed(int j) {
        return indexed.get(j);
    }

    public void setLoc(int[] locations) {
        this.locations = locations;
    }

    public int[] getLoc() {
        return locations;
    }
}
