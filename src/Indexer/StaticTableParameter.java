import javafx.util.Pair;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.regex.Pattern;

public class StaticTableParameter implements Serializable {
    private static Pattern equalPat = Pattern.compile("=");
    private static Pattern parPat = Pattern.compile(":");
    String docNum;
    int countMaxWord; //how much the max word appear in the table
    int dictionarySize;//how many words in the table with counter
    int dictionaryWordCount;//how many diffrent words there are in the dictionary
    String fiveEntity;
    PriorityQueue<Pair<String,TermMetaData>> docEntities=new PriorityQueue<>((o1, o2) -> {
        TermMetaData t1 = o1.getValue();
        TermMetaData t2 = o2.getValue();
        int freq1 = t1.getFrequency();
        int freq2 = t2.getFrequency();
        return Integer.compare(freq1,freq2);
    });



    public StaticTableParameter(String docnum , int countMaxWord, int dictionarySize , int dictionaryWordCount)
    {
        this.docNum=docnum;
        this.countMaxWord=countMaxWord;
        this.dictionarySize=dictionarySize;
        this.dictionaryWordCount=dictionaryWordCount;
    }

    public StaticTableParameter(String allFields){
        String[] splited=equalPat.split(allFields);
        String[] entSplit;
        TermMetaData termMetaData;
        if(splited.length>=4){
            docNum=splited[0];
            countMaxWord=Integer.parseInt(splited[1]);
            dictionarySize=Integer.parseInt(splited[2]);
            dictionaryWordCount=Integer.parseInt(splited[3]);
            if(splited.length>4){
                for (int i=4;i<splited.length;i++){
                    entSplit=parPat.split(splited[i]);
                    termMetaData=new TermMetaData(Integer.parseInt(entSplit[1]),Boolean.valueOf(entSplit[2]));
                    docEntities.add(new Pair<>(entSplit[0],termMetaData));
                }
            }
        }
    }


    public String toString1()
    {
        PriorityQueue<Pair<String,TermMetaData>> temp=new PriorityQueue<>((o1, o2) -> {
            TermMetaData t1 = o1.getValue();
            TermMetaData t2 = o2.getValue();
            int freq1 = t1.getFrequency();
            int freq2 = t2.getFrequency();
            return Integer.compare(freq1,freq2);
        });
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(docNum+"="+countMaxWord+"="+dictionarySize+"="+dictionaryWordCount);
        Pair<String,TermMetaData> pair;
        int size=docEntities.size();
        for (int i=0;i<size;i++){
            pair=docEntities.poll();
            temp.add(pair);
            stringBuilder.append("="+pair.getKey()+":"+pair.getValue().getFrequency()+":"+pair.getValue().isHeadLine());
        }
        docEntities=temp;
        return stringBuilder.toString();
    }


}