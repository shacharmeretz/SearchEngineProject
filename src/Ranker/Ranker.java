import javafx.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Ranker {

    HashMap<Pair<String, Double>, HashMap<String, Pair<Integer, Boolean>>> infoAboutTerm;
    HashMap<String , Integer> infoAboutDoc;
    HashMap<String,String> docTable;
    int sizeOfCorpus;
    private static Pattern equalPat = Pattern.compile("=");
    private String pathForPosting;
    private double avg;
    public Ranker(HashMap<Pair<String, Double>, HashMap<String, Pair<Integer, Boolean>>> infoAboutTerm , HashMap<String , Integer> infoAboutDoc , int sizeOfCorpus , HashMap<String,String> dTable , String pathForPosting , double avg)
    //hashMap<term , HashMap<docno , Pair<HowManyTimeInTheSpecificDoc , ifHeader>>> , HashMap<Docno , SizeOfDoc> , int sizeOfCorpus)
    {
        this.infoAboutDoc=infoAboutDoc;
        this.infoAboutTerm=infoAboutTerm;
        this.sizeOfCorpus=sizeOfCorpus;
        this.docTable=dTable;
        this.pathForPosting=pathForPosting;
        this.avg=avg;
    }

    /**
     * this function manage the ranker
     * this function send the query word to the ranker fnction and return the relevant document
     * @return
     * @throws IOException
     */
    public LinkedList<String> calculateRank() throws IOException {
        LinkedList<Pair<String, Double>> rankForReturn = new LinkedList<>();

        LinkedList<String> docnoList=new LinkedList<>(infoAboutDoc.keySet());
       // double avgDl=getAvgDl();
        for (int i=0; i <docnoList.size(); i++)
        {
            rankForReturn.add(new Pair(docnoList.get(i) , BM25(docnoList.get(i) ,avg )));
        }

        LinkedList<Pair<String, Double>> sortedRank=sortList(rankForReturn);
        return make50Files(sortedRank);
    }

    private LinkedList<Pair<String, Double>> sortList(LinkedList<Pair<String, Double>> rankForReturn) {

        List<Pair<String,Double>> temp=sort(rankForReturn);
        LinkedList<Pair<String , Double>> forReturn=new LinkedList<>();
        for(int i=0; i<temp.size(); i++)
        {
            forReturn.add(temp.get(i));
        }
        return forReturn;
    }

    public List<Pair<String,Double>> sort(LinkedList<Pair<String, Double>> rankForReturn)
    {
        rankForReturn.sort((o1,o2)-> o2.getValue().compareTo(o1.getValue()));
        int minimum=Math.min(50 , rankForReturn.size());
        return rankForReturn.subList(0,minimum);
    }

    /*
    public static HashMap<String, Double> sortByValue(HashMap<String, Double> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Double> > list =
                new LinkedList<Map.Entry<String, Double> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2)
            {
                if(o1.getValue()> o2.getValue())
                    return -1;
                else if(o1.getValue()==o2.getValue())
                    return 1;
                else
                    return 0;
                // return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
*/
    /*
    private double getAvgDl() {
        double count=0;
        LinkedList<Integer> docnoList=new LinkedList<>(infoAboutDoc.values());
        for(int i=0; i<docnoList.size() ; i++)
        {
            count+=docnoList.get(i);
        }
        return count/docnoList.size();
    }
*/
    //this function calculate the score for the query and the specific docno
    //    HashMap<Pair<String, Double>, HashMap<String, Pair<Integer, Boolean>>> infoAboutTerm;
    private double BM25(String docno , double avgDl)
    {
        //with 1.2 it will be 150
        //with 0.3 it will be 173
        double k=0.3;
        double b=0.4;//0.4;
        double score=0;
        LinkedList<Pair<String, Double>> allTheTermInTheQuery=new LinkedList<>(infoAboutTerm.keySet());
        for (int i=0; i<allTheTermInTheQuery.size(); i++)//its mean the Qi like sigma
        {
            boolean ifHeader=false;
            double tf=0;
            double ifDesc=allTheTermInTheQuery.get(i).getValue();
            double idf=returnIdf(infoAboutTerm.get(allTheTermInTheQuery.get(i)).size()); // n(qi)
            Pair<Integer , Boolean> pair=(infoAboutTerm.get(allTheTermInTheQuery.get(i))).get(docno);
            if(pair!=null) {
                tf = pair.getKey();
                ifHeader=infoAboutTerm.get(allTheTermInTheQuery.get(i)).get(docno).getValue();
            }
            double sizeOfDocno=infoAboutDoc.get(docno);
            if(ifHeader==true) {
                score += ifDesc*(idf * ((tf * (k + 1)) / (tf + k * (1 - b + (b * sizeOfDocno / avgDl)))))*1.25;
            }
            else
            {
                score +=ifDesc* idf * ((tf * (k + 1)) / (tf + k * (1 - b + (b * sizeOfDocno / avgDl))));
            }
        }
      //  System.out.println(score);
        return score;
    }

    private double returnIdf(int countOfTheNumberThatIncludeQi) {
        double num=(sizeOfCorpus-countOfTheNumberThatIncludeQi+0.5)/(countOfTheNumberThatIncludeQi+0.5);
        num= Math.log(num)/Math.log(2.0);
        return num;
    }

    private LinkedList<String> make50Files(LinkedList<Pair<String, Double>> rankListDoc) throws IOException {
        /*
        LinkedList<String> docToReturn=new LinkedList<>();
        LinkedList<String> onlyKey=new LinkedList<>(rankForReturn.keySet());

        if(rankForReturn.size()>50)
        {
            for (int i=0; i<50; i++)
            {
                docToReturn.add(onlyKey.get(i));
            }
        }
        else {
            docToReturn=onlyKey;
        }
*/
        LinkedList<String> docToReturn=new LinkedList<>();
        for(int i=0;i<rankListDoc.size(); i++)
        {
            docToReturn.add(rankListDoc.get(i).getKey());
        }
        WriteFile wf= new WriteFile();
        String entity="";
        /*
        wf.write("new Query", pathForPosting + "\\entity.txt");
        wf.write("\n", pathForPosting + "\\entity.txt");

        for (int i=0; i<docToReturn.size(); i++) {
            String[] help=equalPat.split(docTable.get(docToReturn.get(i)));
            int size=Integer.parseInt(help[2]);
            int num = equalPat.split(docTable.get(docToReturn.get(i))).length;
            entity = docToReturn.get(i) + ":";
            String str = docTable.get(docToReturn.get(i));
            LinkedList<String> forRankTheEntity=new LinkedList();
            for (int j = 4; j < num; j++) {
                String theWord=equalPat.split(str)[j];
                //String[] splitTheWord=theWord.split(":");
                //entity +=  + ",";
                forRankTheEntity.add(theWord);
            }
            entity+=rankTheEntity(forRankTheEntity,size);
            //here make the entity txt
            //  entity = entity.substring(0, entity.length() - 1);
            wf.write(entity, pathForPosting + "\\entity.txt");
            wf.write("\n", pathForPosting + "\\entity.txt");
*/

        return docToReturn;
    }

    private String rankTheEntity(LinkedList<String> forRankTheEntity , int size) {
        if (forRankTheEntity.size() == 0) {
            return "";
        }
        LinkedList<Pair<String , Double>> score=new LinkedList<Pair<String, Double>>();
        for(int i=0; i<forRankTheEntity.size(); i++)
        {
            String[] split=forRankTheEntity.get(i).split(":");
            if(split.length==3) {
                int tf = Integer.parseInt(split[1]);
                boolean header = Boolean.parseBoolean(split[2]);
                double rank = (tf * 100) / size;
                if (header) {
                    rank = rank * 1.25;
                }
                score.add(new Pair<>(split[0], rank));
            }
        }
        score=sortList(score);
        String forReturn="";
        //LinkedList<String> temp=new LinkedList<>(score.());
        for (int j=0; j<score.size(); j++)
        {
            double num=score.get(j).getValue();
            forReturn+=score.get(j).getKey()+":"+num+" ,";
        }
        forReturn=forReturn.substring(0,forReturn.length()-1);
        return forReturn;
    }



}