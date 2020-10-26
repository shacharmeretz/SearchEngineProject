import javafx.util.Pair;

import javax.sound.sampled.Line;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;
import java.util.*;
import java.util.regex.Pattern;

/**
 * this class create inverted index from segment files
 * the inverted index contains dictionary and posting files
 */
public class Indexer {
    private DualityCheckerDictionary dictionary;
    private static TreeMap<String, String> termDocFreqDic;// in file the value save at this format tf=lineNo
    private String path;
    private static HashMap<String,String> docTable;
    private boolean ifStem;
    private static Pattern equalPat = Pattern.compile("=");
    private static HashMap<String, Boolean> entities;

    /**
     * constructor
     * @param path to the segment file
     * @param ifStem if stemming used
     */

    public Indexer(String path, boolean ifStem){
        dictionary=new DualityCheckerDictionary();
        termDocFreqDic=new TreeMap<>();
        this.path=path;
        this.docTable=new HashMap<>();
        this.ifStem=ifStem;
        entities=new HashMap<String, Boolean>();
    }
    public void setPath(String path)
    {
        this.path=path;
    }

    public HashMap<String,String> getDocTable()
    {
        return docTable;
    }
    /**
     * this method turn on the indexer
     * calling for all the methods of the class
     * @throws IOException
     */
    public void onIndexer() throws IOException, ClassNotFoundException {
        File folder=new File(path);
        int numOfFilesForLetter=(folder.listFiles().length-1)/27;
        String fileName="";
        for (int i=97;i<124;i++){
            for(int j=0;j<numOfFilesForLetter;j++) {
                fileName = "" + (char) i;
                if (i==123){
                    fileName="numbers";
                }
                fileName += j;
                if(ifStem){
                    fileName+="stem";
                }
                enterList(fileName);
            }
            fileName=""+(char)i;
            if(i==123){
                fileName="numbers";
            }
            if(ifStem){
                fileName+="stem";
            }
            dicToPost(path+"\\"+fileName+".txt");
            dictionary=new DualityCheckerDictionary();
        }
        saveDictionary();
        saveDictionaryToDisplay();
        fillEntitiesDic();
        int size=termDocFreqDic.size();
        termDocFreqDic=null;
        System.gc();
        reloadDocTable();
        fileName="avg.txt";
        FileWriter writer=new FileWriter(path+"\\"+fileName);
        String avarge=String.valueOf(getAvgDl());
        writer.write(avarge);
        writer.close();
        updateDocTable();
        saveUpdatedDocTable();
        printAllDetails(size);
    }
    public void saveDictionaryToDisplay() throws IOException {
        StringBuilder builder=new StringBuilder();
        for (String key: getTermDocFreqDic().keySet()){
            builder.append("The term: <"+key+"> has "+equalPat.split(getTermDocFreqDic().get(key))[0]+ " shows in the corpus \n");
            builder.append(String.format("%n", ""));

        }
        String fileName;
        if(ifStem){
            fileName=path+"\\DictionaryToDisplaystem.txt";
        }
        else {
            fileName=path+"\\DictionaryToDisplay.txt";
        }
        FileWriter writer=new FileWriter(fileName);
        writer.write(builder.toString());
        writer.close();
    }
    public void updateDocTable(){
        Set<String> allDocNos=docTable.keySet();
        StaticTableParameter staticTableParameter;
        String debug="";
        Pair<String,TermMetaData> pair;
        boolean hasChanged;
        int size;
        for (String docNo:allDocNos
        ) {
            hasChanged=false;
            PriorityQueue<Pair<String,TermMetaData>> temp=new PriorityQueue<>((o1, o2) -> {
                TermMetaData t1 = o1.getValue();
                TermMetaData t2 = o2.getValue();
                int freq1 = t1.getFrequency();
                int freq2 = t2.getFrequency();
                return Integer.compare(freq1,freq2);
            });
            debug=docTable.get(docNo);
            staticTableParameter=new StaticTableParameter(debug);
            size=staticTableParameter.docEntities.size();
            for(int i=0;i<size;i++){
                pair=staticTableParameter.docEntities.poll();
                if(pair==null){
                    continue;
                }
                if(entities.containsKey(pair.getKey())){
                    temp.add(pair);
                }
                else {
                    hasChanged=true;
                }
            }
            while (temp.size()>5){
                temp.poll();
                hasChanged=true;
            }
            if(hasChanged){
                staticTableParameter.docEntities=temp;
                docTable.put(docNo,staticTableParameter.toString1());
            }
        }
    }

    public void fillEntitiesDic(){
        Set<String> terms=termDocFreqDic.keySet();
        for (String term:terms
        ) {
            if ((term.charAt(0)>64) || (term.charAt(0)<91) || term.toUpperCase().equals(term)){
                entities.put(term,true);
            }
        }
    }

    public void reloadEntitiesDocTable()throws IOException, ClassNotFoundException{
        File file=new File(path+"\\docTable.txt");
        FileInputStream inputStream=new FileInputStream(file);
        ObjectInputStream objectInputStream=new ObjectInputStream(inputStream);
        docTable=(HashMap<String,String>)objectInputStream.readObject();
        objectInputStream.close();
        inputStream.close();
    }

    public void saveUpdatedDocTable() throws IOException {
        File file=new File(path+"\\docTable.txt");
        FileOutputStream outputStream=new FileOutputStream(file);
        ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(docTable);
        objectOutputStream.close();
        outputStream.close();
    }
    /**
     * this method insert the data of segment file to the duality checker dictionary
     * @param fileName
     * @throws IOException
     */
    private void enterList(String fileName) throws IOException{
        LinkedList<String> list;
        ReadFile readFile=new ReadFile("");
        File file=new File(path + "\\" + fileName);
        list = readFile.getSegmentFile(file.getPath());
        segmentFileAsListToDic(list);
    }

    /**
     * this method send line by line to check and put method
     * @param lines each list represent a data of segment file
     * @return
     */
    private DualityCheckerDictionary segmentFileAsListToDic(LinkedList<String> lines){
        String[] line1;
        for(String link: lines){
            //Lines format:term=docID=tf=isHeadline
            line1 = equalPat.split(link);
            if(line1.length==4){
                dictionary.checkAndPut(line1);
            }
        }
        return dictionary;
    }

    /**
     * this method check if a term has more then 1 show in the corpus
     * @param hash hashmap<docNo,metaData> all of the shows of a term
     * @return
     */
    private boolean trashOld(HashMap<String,TermMetaData> hash){
        if(hash.size()==1){
            Set<String> set=hash.keySet();
            for (String docNo: set){
                if(hash.get(docNo).getFrequency()==1){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * this method write the content of the duality checker dictionary to the posting file
     * @param pathAndFile the path for the posting file
     * @throws IOException
     */
    private void dicToPost(String pathAndFile)throws IOException{
        FileWriter writer=new FileWriter(pathAndFile);
        int lineNum=0;
        int sum=0;
        Set<String> keys=dictionary.getDictionary().keySet();
        HashMap<String , TermMetaData> docAndCounter;
        StringBuilder line;
        for (String term:keys
        ) {
            sum=0;
            docAndCounter=dictionary.getDictionary().get(term);
            String nDoc=String.valueOf(docAndCounter.size());
            line=new StringBuilder();
            line.append(term+' '+nDoc+':');
            Set<String> insideKeys=docAndCounter.keySet();
            for (String docNo:insideKeys
            ) {
                String headLine= String.valueOf(docAndCounter.get(docNo).isHeadLine());
                line.append(docNo+'='+ docAndCounter.get(docNo).getFrequency()+'='+headLine +':');
                sum+=docAndCounter.get(docNo).getFrequency();
            }
            if(trashOld(docAndCounter)==false){
                writer.write(line.toString().substring(0,line.toString().length()-1)+"\n");
                String info=String.valueOf(sum)+'='+String.valueOf(lineNum);
                termDocFreqDic.put(term,info);
                lineNum++;
            }
        }
        writer.close();
    }

    public TreeMap<String, String> getTermDocFreqDic(){return termDocFreqDic;}
    /**
     * this method reload the dictionary from the file to the field
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void reloadDictionary()throws IOException, ClassNotFoundException{
        File file;
        if(ifStem){
            file=new File(path+"\\Dictionarystem.txt");
        }
        else {
            file=new File(path+"\\Dictionary.txt");
        }
        FileInputStream inputStream=new FileInputStream(file);
        ObjectInputStream objectInputStream=new ObjectInputStream(inputStream);
        termDocFreqDic=(TreeMap<String, String>)objectInputStream.readObject();
        objectInputStream.close();
        inputStream.close();
    }

    /**
     * this method save the dictionary from the field to the file
     * @throws IOException
     */
    public void saveDictionary()throws IOException{
        File file;
        if(ifStem){
            file=new File(path+"\\Dictionarystem.txt");
        }
        else {
            file=new File(path+"\\Dictionary.txt");
        }
        FileOutputStream outputStream=new FileOutputStream(file);
        ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(termDocFreqDic);
        objectOutputStream.close();
        outputStream.close();
    }

    /**
     * this method reload the documents table from the file to the field
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void reloadDocTable()throws IOException, ClassNotFoundException{
        File file=new File(path+"\\staticTable.txt");
        FileInputStream inputStream=new FileInputStream(file);
        ObjectInputStream objectInputStream=new ObjectInputStream(inputStream);
        docTable=(HashMap<String,String>)objectInputStream.readObject();
        objectInputStream.close();
        inputStream.close();
    }

    private void printAllDetails(int size){
        System.out.println("there are "+ size+ " unique terms");
        System.out.println("Read from "+ docTable.size()+ " documents");
    }
    public double getAvgDl() {
        double count=0;
        LinkedList<String> docNum=new LinkedList<>(docTable.keySet());
        for(int i=0; i<docNum.size() ; i++)
        {
            String str=equalPat.split(docTable.get(docNum.get(i)))[2];
            count+=Double.parseDouble(str);
        }
        return count/docNum.size();
    }
}
