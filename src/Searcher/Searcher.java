import com.medallia.word2vec.Word2VecModel;
import javafx.util.Pair;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class Searcher {
    private HashMap<Pair<String , Double> , HashMap<String , Pair<Integer , Boolean>>> termsInfo;//term:docNo-5F,doNo-3T
    private HashMap<String,Integer> docsInfo;
    private int sizeOfCorpus;
    private ReadFile reader;

    private TreeMap<String, String> termDocFreqDic;// in file the value save at this format tf=lineNo
    private HashMap<String,String> docTable;//value
    private String pathForPosting;
    private String pathForQuery;
    // private LinkedList<String> queries;
    private static Pattern equalPat = Pattern.compile("=");
    private static Pattern patternPat = Pattern.compile(":");
    private boolean semanticWithNoInternet;
    private boolean semanticWithInternet;
    private ManagerQuery managerQuery;
    private boolean stemming;
    double avg;
    //String query;
    Pair<String , HashMap<String, Pair<Integer, Boolean>>> query;
    LinkedList<Pair<Query,HashMap<String, Double>>> allTheQuery;


    public Searcher(String query , String pathForPosting,TreeMap<String, String> dic,HashMap<String,String> dTable , boolean semanticWithNoInternet , boolean semanticWithInternet , boolean stemming) throws IOException, ClassNotFoundException {
        this.pathForQuery=query;
        termDocFreqDic=dic;
        reader=new ReadFile(pathForPosting);
        docTable=dTable;
        sizeOfCorpus=dTable.size();
        this.pathForPosting=pathForPosting;
        ManagerQuery managerQuery=new ManagerQuery(stemming);
        allTheQuery=managerQuery.startMakeAnswerWithQueryFile(pathForQuery , pathForPosting);
        getAvg();
        this.stemming=stemming;
        this.semanticWithNoInternet=semanticWithNoInternet;
        this.semanticWithInternet=semanticWithInternet;
        this.query=null;
    }

    public Searcher(String query ,boolean stam , String pathForPosting,TreeMap<String, String> dic,HashMap<String,String> dTable ,boolean semanticWithNoInternet , boolean semanticWithInternet , boolean stemming) throws IOException, ClassNotFoundException {
        termDocFreqDic=dic;
        docTable=dTable;
        reader=new ReadFile(pathForPosting);
        sizeOfCorpus=dTable.size();
        this.pathForPosting=pathForPosting;
        ManagerQuery managerQuery=new ManagerQuery(stemming);
        this.query=managerQuery.startMakeAnswer(query,pathForPosting);
        getAvg();
        this.semanticWithNoInternet=semanticWithNoInternet;
        this.semanticWithInternet=semanticWithInternet;
        this.stemming=stemming;
        this.allTheQuery=null;
    }

    public void setPathFile(String pathForPosting , String pathForQuery)
    {
        this.pathForPosting=pathForPosting;
        this.pathForQuery=pathForQuery;
    }

    public void setPathFile(boolean stam , String pathForPosting , String queryfromVGui) throws IOException {
        this.pathForPosting=pathForPosting;
        this.query=managerQuery.startMakeAnswer(queryfromVGui,pathForPosting);
    }

    private void getAvg() throws IOException {
        ReadFile rf=new ReadFile(pathForPosting+"\\avg.txt");
        String str=rf.readAllText(new File(pathForPosting+"\\avg.txt")).toString();
        avg=Double.parseDouble(str);
    }
//    LinkedList<Pair<Query,HashMap<String, Double>>> allTheQuery;
    public void manageSearcher() throws IOException {
       // System.out.println("start manage seracher");
        if(allTheQuery!=null)
        {
            for(int i=0; i<allTheQuery.size(); i++)
            {
                System.out.println(i);
                //LinkedList<String> queryForManager=new LinkedList<>(allTheQuery.get(i).getValue().keySet());
                LinkedList<String> afterRank = searchForRelevantDocs(allTheQuery.get(i).getValue());
                String queryNum=allTheQuery.get(i).getKey().getNum();
                printToTheAnswerFile(afterRank , queryNum);
                printToTheResultsFile(afterRank , queryNum);
            }
            File file=new File(pathForPosting + "\\answersToShow.txt");
            Desktop.getDesktop().open(file);
        }
        else
        {
            LinkedList<String> queryForManager=new LinkedList<>(this.query.getValue().keySet());
            HashMap<String, Double> forQuery =new HashMap<>();
            for(int i=0; i<queryForManager.size();i++)
            {
                forQuery.put(queryForManager.get(i) , 1.0);
            }
            LinkedList<String> afterRank = searchForRelevantDocs(forQuery);
            String queryNum=this.query.getKey();

            printToTheAnswerFile(afterRank , queryNum);
            printToTheResultsFile(afterRank , queryNum);
            File file=new File(pathForPosting + "\\answersToShow.txt");
            Desktop.getDesktop().open(file);
        }
    }

    private void printToTheResultsFile(LinkedList<String> afterRank , String queryNum) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        WriteFile writeFile=new WriteFile();
        for (int j = 0; j < afterRank.size(); j++) {
            writeFile.write(queryNum + " 0 " + afterRank.get(j) + " 1 42.38 mt" + "\n", pathForPosting + "\\results.txt");
        }
    }

    private void printToTheAnswerFile(LinkedList<String> afterRank , String queryNum) throws IOException {
        WriteFile writeFile=new WriteFile();
        StringBuilder forQueryAnswer = new StringBuilder();
        forQueryAnswer.append("query number "+queryNum + ": "+afterRank.size()+" doc return.");
        forQueryAnswer.append("\n");
        for (int x = 0; x < afterRank.size(); x++) {
            forQueryAnswer.append(afterRank.get(x) + ",");
        }
        forQueryAnswer.deleteCharAt(forQueryAnswer.length() - 1);
        forQueryAnswer.append("\n");
        writeFile.write(forQueryAnswer.toString(), pathForPosting + "\\answersToShow.txt");
    }

    public LinkedList<String> searchForRelevantDocs(HashMap<String, Double> forQuery ) throws IOException {
        LinkedList<String> semanticWords1;
        LinkedList<String> semanticWords2;

        if(semanticWithNoInternet==true) {
            semanticWords1 = semanticModelNoInternet(new LinkedList<>(forQuery.keySet()));//addSemanticWordsToTheQuery();
            for (int i = 0; i < semanticWords1.size(); i++) {
                forQuery.put(semanticWords1.get(i), 0.6);
            }
        }
        if(semanticWithInternet==true) {
            semanticWords2=addSemanticWordsToTheQuery(new LinkedList<>(forQuery.keySet()));
            for (int i=0; i<semanticWords2.size(); i++)
            {
                forQuery.put(semanticWords2.get(i) , 0.6);
            }
        }
       LinkedList<String> listForQuery=new LinkedList<>(forQuery.keySet());//all the word in the query
        termsInfo=new HashMap<Pair<String , Double> , HashMap<String , Pair<Integer , Boolean>>>();
        docsInfo=new HashMap<>();
        char firstLetter=' ';
        int lineNo;
        String fileName="";
        LinkedList<String> docs=new LinkedList<>();
        for(int i=0;i<forQuery.size();i++) {
            if (termDocFreqDic.containsKey(listForQuery.get(i))) {//here i check for every word all the doc it includes
                lineNo = Integer.parseInt(equalPat.split(termDocFreqDic.get(listForQuery.get(i)))[1]);
                firstLetter = listForQuery.get(i).charAt(0);
                fileName = findPostingFile(firstLetter);
                HashMap<String , Pair<Integer , Boolean>> allTheDocInString=fromPostingFile(fileName, lineNo);//all the doc
                LinkedList<String> docForTerm=new LinkedList<>(allTheDocInString.keySet());// all the doc for the term
                termsInfo.put(new Pair(listForQuery.get(i) , forQuery.get(listForQuery.get(i))),allTheDocInString );
                //LinkedList<Pair<String , Double>> keys=new LinkedList<>(termsInfo.keySet());
                    //LinkedList<String> temp = new LinkedList<>(termsInfo.get(keys.get(i)).keySet());
                  //  for(int x=0;x<temp.size();x++)
                   // {
                  //      docs.add(temp.get(i));
                  //  }
                for (String key : docForTerm) {
                   if (!docsInfo.containsKey(key)) {
                        docsInfo.put(key, Integer.parseInt(equalPat.split(docTable.get(key))[2]));//docNo, doc length

                    }
                }
            }
        }
        return sendToRanker();
    }

    private String findPostingFile(char firstLetter){
        String fileName="";
        if (firstLetter>96 && firstLetter<123){
            fileName+=firstLetter;
        }
        else if(firstLetter>64 && firstLetter<91){
            fileName+=firstLetter;
            fileName=fileName.toLowerCase();
        }
        else if(firstLetter>47 && firstLetter<58){
            fileName="numbers";
        }
        return fileName;
    }

    private HashMap<String , Pair<Integer , Boolean>> fromPostingFile(String firstLetter, int lineNo) throws IOException {
        String line;
        HashMap<String , Pair<Integer , Boolean>> infoFromPosting=new HashMap<>();
        if(stemming) {
            line = reader.readThisLine(pathForPosting + "\\" + firstLetter + "stem.txt", lineNo);
        }
        else
        {
            line = reader.readThisLine(pathForPosting + "\\" + firstLetter + ".txt", lineNo);

        }
        String[] lineSplited=patternPat.split(line);
        String[] secondSplit;
        Pair<Integer , Boolean> pair;
        for (int i=1; i<lineSplited.length;i++){
            secondSplit=equalPat.split(lineSplited[i]);
            pair=new Pair(Integer.parseInt(secondSplit[1]),Boolean.valueOf(secondSplit[2]));
            infoFromPosting.put(secondSplit[0],pair);
        }
        return infoFromPosting;
    }

    private LinkedList<String> sendToRanker() throws IOException {
        Ranker ranker=new Ranker(termsInfo,docsInfo,sizeOfCorpus , docTable , pathForPosting , avg);
        return ranker.calculateRank();
    }

    public LinkedList<String> semanticModelNoInternet(LinkedList<String> query) throws IOException{
        LinkedList<String> toRetrun=new LinkedList<>();
        try {
            for (int i = 0; i < query.size(); i++) {
                Word2VecModel model = Word2VecModel.fromTextFile(new File( pathForPosting+"\\word2vec.c.output.model.txt"));
                com.medallia.word2vec.Searcher searcher = model.forSearch();
                int num = 3;
                List<com.medallia.word2vec.Searcher.Match> matches = searcher.getMatches(query.get(i), num);
                for (com.medallia.word2vec.Searcher.Match match : matches) {
                    match.match();
                }
                for (int j = 1; j < matches.size(); j++) {
                    String temp=matches.get(j).toString();
                    String[] arr=temp.split("\\[");
                    arr[1]=arr[1].substring(0,arr[1].length()-1);
                    // if(Double.parseDouble(arr[1])>0.95)
                    toRetrun.add(arr[0]);
                }
            }
        }
        catch (com.medallia.word2vec.Searcher.UnknownWordException e)
        {

        }
       // System.out.println(toRetrun.toString());
        return toRetrun;

    }

    /*
    private double getAvgDl() {
        double count=0;
        LinkedList<String> docNum=new LinkedList<>(docTable.keySet());
        for(int i=0; i<docNum.size() ; i++)
        {
            String str=equalPat.split(docTable.get(docNum.get(i)))[2];
            count+=Double.parseDouble(str);
        }
        return count/docNum.size();
    }
*/
    //////////////////all this function for semantic with internet

    public LinkedList<String> addSemanticWordsToTheQuery(LinkedList<String> query)
    {
        LinkedList<String> whatWeAddToTheQueryList=new LinkedList<>();
        String path="https://api.datamuse.com/words?ml=";
        for(int i=0; i<query.size(); i++)
        {
            String wordOfQuery=query.get(i);
            if(wordOfQuery.contains(" "))
            {
                String[] split=wordOfQuery.split("\\s+");
                wordOfQuery=split[0];
                for (int j=1;j<split.length; j++)
                {
                    wordOfQuery="_"+split[j];
                }
            }
            String newPath=path+wordOfQuery;
            HashMap<String , Integer> allTheSemanticWords=useSemantic(newPath);
            LinkedList<String > afterRemoveScore=removeScore(allTheSemanticWords);
            for(int j=0; j<afterRemoveScore.size(); j++)
            {
                whatWeAddToTheQueryList.add(afterRemoveScore.get(j));
            }
        }
        return whatWeAddToTheQueryList;
    }
    private LinkedList<String> removeScore(HashMap<String, Integer> allTheSemanticWords) {
        LinkedList<String> toReturn=new LinkedList<>();
        LinkedList<String> keys=new LinkedList<>(allTheSemanticWords.keySet());
        for(int i=0; i<allTheSemanticWords.size(); i++)
        {
            if(allTheSemanticWords.get(keys.get(i))>75000)
            {
                toReturn.add(keys.get(i));
            }
            else {
                break;
            }
        }
        return toReturn;
    }

    public HashMap<String , Integer> useSemantic(String path) {
        HashMap<String, Integer> semanticWords = new HashMap<>();
        try {
            URL yahoo = new URL(path);
            URLConnection yc = yahoo.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            String str = "";
            while ((inputLine = in.readLine()) != null) {
                str = inputLine;
            }
            in.close();
            String[] split = str.split("\\{");
            for (int i = 1; i < split.length; i++) {
                String[] split2 = split[i].split(":");
                String[] split3 = split2[1].split(",");
                String word = split3[0];
                word = word.substring(1, word.length() - 1);
                String[] split4 = split2[2].split(",");
                if(split4[0].charAt(split4[0].length()-1)<'0' || split4[0].charAt(split4[0].length()-1)>'9')
                    split4[0].substring(0,split4[0].length()-1);
                int score = Integer.parseInt(split4[0]);
                semanticWords.put(word, score);
                //System.out.println(word+" "+score);
            }
        }
        catch (IOException E)
        {
            System.out.println(path);
            System.out.println("problem with the URL");
        }
        catch (Exception e)
        {
            // System.out.println(e.toString());
        }
        //System.out.println(semanticWords.toString());
        return semanticWords;
    }
}