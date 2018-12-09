/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package systemprogramming;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Katary
 */
public class Assembler {
    
    
    private HashMap <String ,Integer> SymTable;
    private FileParser code;
    String[] objectCode;
    
    public Assembler(String textFileName) throws IOException{
        code = new FileParser(textFileName); 
        objectCode = new String[code.getlen()];
    }
    public void firstPass(){
        SymTable = new HashMap<>();
        
        int start = code.getStartaddr();
        int[] locations = new int[code.getlen()+1] ;
        
        
        for(int i=0 ; i<code.getlen() ; i++){
            locations[i] = start ;
            if( !code.getNem(i).equals("RESW") && !code.getNem(i).equals("RESB") 
                && !code.getNem(i).equals("WORD") && !code.getNem(i).equals("BYTE"))
                start = start + 3 ; 

            else if( code.getNem(i).equals("RESW"))
                start = start + Integer.parseInt(code.getadd(i))* 3;
        
            else if(code.getNem(i).equals("RESB"))
                start = start + Integer.parseInt(code.getadd(i));
        
            else if( code.getNem(i).equals("BYTE")){
                if(code.getadd(i).charAt(0) == 'C')
                    start = start + (code.getadd(i).length()-3); 
                else if(code.getadd(i).charAt(0) == 'X')
                    start = start + (code.getadd(i).length()-3)/2; 
            }
            else if( code.getNem(i).equals("WORD"))
                 start = start + 0x3 ; 
        
            if( !code.getLabel(i).equals("no")) 
                SymTable.put(code.getLabel(i), locations[i]);  
        }
        locations[code.getlen()] = start;
        code.setLoc(locations);
        createIntermediateFile();
        try {
            writeSymTable(SymTable);
        } catch (IOException ex) {
            Logger.getLogger(Assembler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void secondPass(){
        int tempAddress = 0;
        String decodedInstruction = "";
        for (int i = 0 ; i < code.getlen() ; i++){

                if(SymTable.containsKey(code.getadd(i)) && code.containsNemonic(code.getNem(i))){
                    tempAddress = SymTable.get(code.getadd(i));
                    if( code.getindexed(i ) == 1)
                        tempAddress = tempAddress ^ 0x8000;
                   
                    decodedInstruction = code.getNemonicAdss(code.getNem(i));
                    decodedInstruction = decodedInstruction.concat(String.format("%4s", Integer.toHexString(tempAddress)).toUpperCase().replace(' ', '0')); 
                    objectCode[i] = decodedInstruction;
                }else if(code.getadd(i).equals("null")){
                    decodedInstruction = code.getNemonicAdss(code.getNem(i));
                    decodedInstruction = decodedInstruction.concat("0000");
                    objectCode[i] = decodedInstruction;
                }
                else if( code.getNem(i).equals("WORD")){
                    tempAddress = Integer.parseInt(code.getadd(i));
                    decodedInstruction = Integer.toHexString(tempAddress);
                    decodedInstruction = String.format("%6s", decodedInstruction).toUpperCase().replace(' ', '0');       
                    objectCode[i] = decodedInstruction;
                }else if( code.getNem(i).equals("BYTE")){
                    String subString = code.getadd(i).substring(2, code.getadd(i).length()-1);
                    StringBuilder hexsb = new StringBuilder();
                    if(code.getadd(i).charAt(0) == 'C'){
                    for (char c : subString.toCharArray())
                         hexsb.append(Integer.toHexString((int)c).toUpperCase());          
                    decodedInstruction = hexsb.toString();
                    }else if(code.getadd(i).charAt(0) == 'X'){
                        decodedInstruction = subString;
                    }
                    //decodedInstruction = String.format("%6s", decodedInstruction).replace(' ', '0'); 
                    objectCode[i] = decodedInstruction;
                }else if( code.getNem(i).equals("RESW") || code.getNem(i).equals("RESB")){
                
                }
           }
        createObjectProg();
        createListingFile();
    }  
    
    public void createObjectProg(){
        
        
        BufferedWriter writer;
        int count = 0;
        int j = 0;
        int startAdd = 0;
        String startString = "T ";
        String text = "";
        String lineLength = "";
        int codeSize = code.getLoc()[code.getLoc().length-1] - code.getLoc()[0];
        
        try{
            File file = new File("ObjectProgram.txt");
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("H "+code.getPrgmname()+" "+String.format("%6s", Integer.toHexString(code.getStartaddr())).toUpperCase().replace(' ', '0') +" "+
                    String.format("%6s", Integer.toHexString(codeSize)).toUpperCase().replace(' ', '0'));
            writer.newLine();
            for(int i = 0; i < objectCode.length ; i++){
                if (objectCode[i] != null){
                    if( count == 0){
                        startAdd = code.getLoc()[i];
                        startString = "T " + String.format("%6s", Integer.toHexString(code.getLoc()[i])).toUpperCase().replace(' ', '0')+" ";
                        text = text.concat(objectCode[i]+" ");
                                    }
                    else 
                        text = text.concat(objectCode[i]+" ");    
                    j = i;
                 } 
                count++;
                
                if((code.getLoc()[i+1] - startAdd) > 27 ){//    count == 10 || objectCode[i] == null    || (i+1 < code.getlen() &&(code.getLoc()[i+1]-startAdd) > 27) &&((code.getLoc()[i+1]-startAdd) < 30)
                    lineLength = String.format("%2s", Integer.toHexString(code.getLoc()[j+1] - startAdd)).toUpperCase().replace(' ', '0')+" ";
                    writer.write(startString + lineLength + text);
                    writer.newLine();
                    text = "";
                    count = 0;
                    while( i+1 < code.getlen() && objectCode[i+1] == null)
                        i++;
                }
             }
            if (!text.equals("")){
                lineLength =  String.format("%2s", Integer.toHexString(code.getLoc()[j+1] - startAdd)).toUpperCase().replace(' ', '0')+" ";
                writer.write(startString + lineLength + text);
                writer.newLine();
            }
            writer.write("E " + String.format("%6s", Integer.toHexString(code.getLoc()[0])).toUpperCase().replace(' ', '0'));
            writer.close();
            
            }catch(Exception e){
                System.out.println(e);
            
        }

    }
    
    public void createListingFile(){
        BufferedWriter writer;
        String line;
        
        try{
            File file = new File("ListingFile.txt");
            writer = new BufferedWriter(new FileWriter(file));
            line = String.format("%-8s",Integer.toHexString(code.getLoc()[0]).toUpperCase())  
                    + String.format("%-14s",code.getPrgmname())+ Integer.toHexString(code.getLoc()[0]).toUpperCase();
            writer.write(line);
            writer.newLine();
                
            for(int i = 0; i < objectCode.length ; i++){
                if(!code.getLabel(i).equals("no")){
                    line = String.format("%-8s",Integer.toHexString(code.getLoc()[i]).toUpperCase());
                    line = line.concat(String.format("%-8s",code.getLabel(i)));
                }
                else
                     line = String.format("%-16s",Integer.toHexString(code.getLoc()[i]).toUpperCase());

                line = line.concat(String.format("%-6s",code.getNem(i)));
                
                if(!code.getadd(i).equals("null")) 
                    line = line.concat(String.format("%-8s",code.getadd(i)));

                if(objectCode[i] != null)
                    line = line.concat(objectCode[i].toUpperCase());
                
                writer.write(line);
                writer.newLine();
            }
            writer.close();
             
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    public void createIntermediateFile(){
        BufferedWriter writer;
        String line;
        
        try{
            File file = new File("IntermediateFile.txt");
            writer = new BufferedWriter(new FileWriter(file));
            line = String.format("%-8s",Integer.toHexString(code.getLoc()[0]).toUpperCase())  
                    + String.format("%-14s",code.getPrgmname())+ Integer.toHexString(code.getLoc()[0]).toUpperCase();
            writer.write(line);
            writer.newLine();
                
            for(int i = 0; i < objectCode.length ; i++){
                if(!code.getLabel(i).equals("no")){
                    line = String.format("%-8s",Integer.toHexString(code.getLoc()[i]).toUpperCase());
                    line = line.concat(String.format("%-8s",code.getLabel(i)));
                }
                else
                     line = String.format("%-16s",Integer.toHexString(code.getLoc()[i]).toUpperCase());

                line = line.concat(String.format("%-6s",code.getNem(i)));
                
                if(!code.getadd(i).equals("null")) 
                    line = line.concat(String.format("%-8s",code.getadd(i)));

                if(objectCode[i] != null)
                    line = line.concat(objectCode[i].toUpperCase());
                
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        }catch(Exception e){
            System.out.println(e);
        }
        
    }
    
    public void assemble(){
        
       long start;
       long finish;
       double msTaken;
        
        start = System.nanoTime();
        firstPass();
        secondPass();
        finish = System.nanoTime();
        msTaken = (double)(finish-start)/ 1000000;
       System.out.println("Assembling time: " + msTaken + " ms\n");
        
    }

    private void writeSymTable(HashMap<String, Integer> SymTable) throws IOException {
       File f = new File("Symbol.txt");
        BufferedWriter write = new BufferedWriter(new FileWriter(f));
        Object l[]= SymTable.keySet().toArray();
        String line;
        for (int i = 0; i < SymTable.size(); i++) {
            line=String.format("%-6s %5s",l[i].toString(),Integer.toHexString(SymTable.get(l[i].toString())).toUpperCase());
            write.write( line);
            write.newLine();
        }
        write.close();
        
    }
}
