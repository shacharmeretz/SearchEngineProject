import javafx.util.Pair;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class ManagerQuery {
    private static Pattern topPat = Pattern.compile("<top>");
    ReadFile readFile;
    private boolean stemming;

    public ManagerQuery(boolean stemming) throws IOException {
        readFile=new ReadFile("");
        this.stemming=stemming;
    }

    /***
     *
     * @param query
     * @param path
     * @return this function return pait that include the query num and hash map that include all the word in the query after parse
     * @throws IOException
     */
    public Pair<String , HashMap<String, Pair<Integer, Boolean>>> startMakeAnswer(String query, String path) throws IOException {

        String allText = query + " test test test";
        Parse parser = new Parse("", readFile.stopWords, false);
        HashMap<String, Pair<Integer, Boolean>> queryAfterParse = parser.parseDoc("", allText, "");
        Pair<String , HashMap<String, Pair<Integer, Boolean>>> queryForManage=new Pair<>("0" , queryAfterParse);
        return queryForManage;
    }

    /***
     *
     * @param pathForQuery
     * @param pathForPosting
     * @return this function return linked list that include pair foe every query oin the query file and for all pair the query num and hash map that include all the word in the query after parse
     * @throws IOException
     */
    public LinkedList<Pair<Query,HashMap<String, Double>>> startMakeAnswerWithQueryFile(String pathForQuery, String pathForPosting) throws IOException {

        LinkedList<Pair<Query,HashMap<String, Double>>> allTheQuery = new LinkedList<>();
        String allText = readFile.readAllText(new File(pathForQuery)).toString();
        String[] afterSplit = topPat.split(allText);
        for (int i = 1; i < afterSplit.length; i++) {
            Query query = new Query(afterSplit[i]);
            Parse parser = new Parse("", readFile.stopWords, false);
            HashMap<String, Pair<Integer, Boolean>> queryAfterParse = parser.parseDoc("", query.getDesc()+ " test test test", "");
            HashMap<String, Pair<Integer, Boolean>> queryAfterParse2 = parser.parseDoc("", query.getTitle() + " test test test", "");
            HashMap<String, Pair<Integer, Boolean>> queryAfterParse3 = parser.parseDoc("", query.getTitle().toLowerCase()+ " test test test", "");


            LinkedList<String> queryAfterParseOnlyKey=new LinkedList<>(queryAfterParse.keySet());
            LinkedList<String> queryAfterParseOnlyKey2=new LinkedList<>(queryAfterParse2.keySet());
            LinkedList<String> queryAfterParseOnlyKey3=new LinkedList<>(queryAfterParse3.keySet());

            HashMap<String, Double> queryAfterMerge=merge(queryAfterParseOnlyKey , queryAfterParseOnlyKey2 , queryAfterParseOnlyKey3 ); //desc will be 0.2 , title will be 0.8
            if(stemming)
            {
                queryAfterMerge=stemmingTheHash(queryAfterMerge);
            }
            allTheQuery.add(new Pair<Query,HashMap<String, Double>>(query , queryAfterMerge));
        }
        return allTheQuery;
    }

    /***
     * this function get hash map full of word and return the same hashmap after all the word enter to the stemmer
     * @param queryAfterMerge
     * @return
     */
    private HashMap<String, Double> stemmingTheHash(HashMap<String, Double> queryAfterMerge) {
        Stemmer stemmer=new Stemmer();
        LinkedList<String> temp=new LinkedList<>(queryAfterMerge.keySet());
        HashMap<String, Double> newHash=new HashMap<>();
        for(int i=0; i<temp.size(); i++)
        {
            String afterStem=stemmer.stemTerm(temp.get(i));
            newHash.put(afterStem , queryAfterMerge.get(temp.get(i)));
            //queryAfterMerge.remove(temp.get(i));
        }
        return newHash;
    }

    /**
     * this function get 3 Hash map and merge them to one hash map
     * @param queryAfterParse
     * @param queryAfterParse2
     * @param queryAfterParse3
     * @return
     */
    private HashMap<String, Double> merge(LinkedList<String> queryAfterParse, LinkedList<String> queryAfterParse2 , LinkedList<String> queryAfterParse3)
    {
        HashMap<String, Double> forReturn=new HashMap<>();
        for(int i=0; i<queryAfterParse.size(); i++)
        {
            forReturn.put(queryAfterParse.get(i) , 0.3);
        }
        for(int i=0; i<queryAfterParse2.size(); i++)
        {
            forReturn.put(queryAfterParse2.get(i) , 0.8);
        }
        for(int i=0; i<queryAfterParse3.size(); i++)
        {
            forReturn.put(queryAfterParse3.get(i) , 0.8);
        }
        return forReturn;
    }

}