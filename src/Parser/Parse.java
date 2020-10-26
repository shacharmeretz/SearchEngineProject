import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public class Parse{

    static HashMap<String,String> staticTableForAllTheDic=new HashMap<>();
    HashMap<String, HashMap<String , Pair<Integer , Boolean>>> dictionary;
    String word1;
    String word2;
    String word3;
    String word4;
    int i;
    String path;
    LinkedList<String> stopWords;
    Stemmer stemmer;
    boolean ifStem;
    String[] months={"January", "Jan", "JANUARY", "February", "Feb", "FEBRUARY", "March", "Mar", "MARCH", "April", "Apr", "APRIL",
            "May","MAY", "June", "Jun", "JUNE", "July", "Jul", "JULY", "August", "Aug", "AUGUST", "September", "Sep","SEPTEMBER",
            "October", "Oct", "OCTOBER", "November", "Nov", "NOVEMBER", "December", "Dec", "DECEMBER"};

    public Parse(String path , LinkedList<String> stopWords , boolean ifStem)
    {
        dictionary = new HashMap<>();
        this.path=path;//pathToTheSegment
        this.stopWords=stopWords;
        this.stemmer=new Stemmer();
        this.ifStem=ifStem;
    }

    public  HashMap<String,String> getStaticTableForAllTheDic()
    {
        return staticTableForAllTheDic;
    }

    /**staticTableForAllTheDic
     * this function get list with docno , and for all docno string with the text and the header
     * this function parse all the doc and send all the terms to the segment file class
     * @param list
     * @throws IOException
     */
    public void parseAllDoc( LinkedList<Pair<String, Pair<String , String>>>  list) throws IOException {
        for(int j=0; j<list.size(); j++)
        {
            String docno=list.get(j).getKey();
            i=0;
            HashMap<String , Pair<Integer , Boolean>> dicByDoc=parseDoc(docno , (String)((Pair)(Pair)list.get(j).getValue()).getKey()+" test test test" , (String)((Pair)(Pair)list.get(j).getValue()).getValue() );//key = docnum  , key of second pair =text , value of second pair-header
            dictionary.put(docno , dicByDoc);
        }
        SegmentFile segmentFile=new SegmentFile(dictionary, path,ifStem);
        segmentFile.writeToSegmanetFile();
    }

    private String make5entity(HashMap<String , Pair<Integer , Boolean>> dicByDoc) {
        HashMap<String ,Double> entity=new HashMap<>();
        LinkedList<String> onlyName=new LinkedList<>(dicByDoc.keySet());
        for(int i=0; i<onlyName.size(); i++)
        {
            if (onlyName.get(i).length()!=0)
                if(onlyName.get(i).charAt(0)>='A' && onlyName.get(i).charAt(0)<='Z')
                {
                    double num=(double)dicByDoc.get(onlyName.get(i)).getKey();
                    entity.put(onlyName.get(i) ,num);
                }
        }
        entity=sortByValue(entity);
        if(entity.size()>5)
        {
            LinkedList<String> onlyKey=new LinkedList<>(entity.keySet());
            Pair temp=dicByDoc.get(onlyKey.get(0));
            String str=onlyKey.get(0)+":"+temp.getKey()+":"+temp.getValue();
            for(int i=1; i<5; i++)
            {
                temp=dicByDoc.get(onlyKey.get(i));
                str+="="+onlyKey.get(i)+":"+temp.getKey()+":"+temp.getValue();
            }
            return str;
        }
        else
        {
            LinkedList<String> onlyKey=new LinkedList<>(entity.keySet());
            String str="";
            if(onlyKey.size()!=0) {
                Pair temp = dicByDoc.get(onlyKey.get(0));
                str = onlyKey.get(0) + ":" + temp.getKey() + ":" + temp.getValue();
                for (int i = 1; i < entity.size(); i++) {
                    str += "=" + onlyKey.get(i) + ":" + temp.getKey() + ":" + temp.getValue();
                }
            }
            return str;
        }
    }

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

    /**
     * get text and parse it to terms.
     * @param docNum
     * @param text
     * @param header
     * @return
     */
    public HashMap parseDoc(String docNum, String text , String header) throws IOException {
        StaticTableParameter staticTableParameter;
        HashMap<String, Pair<Integer , Boolean>> termsByDoc = new HashMap<>();

        //splittedText = text.split(" ~; ! ? =#&^*+\\\\|:\\\"()[]<>\\n\\r\\t ");

        //   String[] splittedText = text.split("[\\s+ | & ; @ \\( \\) \\[ \\] : \\"  ]");
        //now the text splited in the array
        String[] splittedText = StringUtils.split(text, " ~;!?=#&^*+\\|:\"(){}[]<>\n\r\t");

        i=0;
        for (i = 0; i < splittedText.length - 3; i++) {//check the last 3
            splittedText[i] = deletePunctuation(splittedText[i]);
            if (splittedText[i].length()==0) {
            } else {
                /*
                if (checkIfQuote(splittedText[i])) {
                    splittedText[i] = splittedText[i].substring(1);
                    int temp = i;
                    boolean flag = true;
                    while (temp < splittedText.length && flag == true) {

                        if (checkIfQuote(splittedText[temp])) {
                            flag = false;
                            splittedText[temp] = deletePunctuation(splittedText[temp]);
                            splittedText[temp] = splittedText[temp].substring(0, splittedText[temp].length() - 1);
                            splittedText[temp] = deletePunctuation(splittedText[temp]);
                        } else {
                            temp++;
                        }
                    }
                }
                */
                /*
                if(checkIfParenthesis(splittedText[i]))
                {
                    splittedText[i]=splittedText[i].substring(1);
                    int temp=i;
                    boolean flag=true;
                    while(temp<splittedText.length && flag)
                    {
                        if(checkIfParenthesis(splittedText[temp])) {
                            flag = false;
                            splittedText[temp] = deletePunctuation(splittedText[temp]);
                            splittedText[temp]=splittedText[temp].substring(0 , splittedText[temp].length()-1);
                            splittedText[temp] = deletePunctuation(splittedText[temp]);

                        }
                        else
                        {
                            temp++;
                        }
                    }
                }
*/
                /*
                if(checkIfSquareParenthesis(splittedText[i]) ) {
                    splittedText[i] = splittedText[i].substring(1);
                    int temp = i;
                    boolean flag = true;
                    while (temp < splittedText.length && flag == true) {
                        if (checkIfSquareParenthesis(splittedText[temp])) {
                            flag = false;
                            splittedText[temp] = deletePunctuation(splittedText[temp]);
                            splittedText[temp] = splittedText[temp].substring(0, splittedText[temp].length() - 1);
                            splittedText[temp] = deletePunctuation(splittedText[temp]);
                        } else {
                            temp++;
                        }
                    }
                }
                */
                if(includeInsideHyphen(splittedText[i]))
                {
                    String[] arr=removeThisHyphen(splittedText[i]);
                    for(int z=0; z<arr.length; z++) {
                        if(ifStem) {
                            arr[z] = stemmer.stemTerm(arr[z]);
                        }
                        termsByDoc = addToDic(arr[z], termsByDoc , header);
                    }

                }
                /*
                else if(splittedText[i].contains("&"))
                {
                    System.out.println(splittedText[i]);
                }
                else if(splittedText[i].contains(";"))
                {
                   // System.out.println(splittedText[i]);
                }
                else if(splittedText[i].contains("<") || splittedText[i].contains(">"))
                {
                    String[] arr=splittedText[i].split(">");
                    if(arr.length>1) {
                        termsByDoc = addToDic(arr[1], termsByDoc, header);
                    }
                }
                */
                else if (splittedText[i].equals("between") || splittedText[i].equals("Between")) {
                    if (betweenCheck(splittedText, i) == true) {
                        String[] betweenTerm = betweenToTerm(splittedText, i);
                        for (int j = 0; j < 3; j++) {
                            termsByDoc = addToDic(betweenTerm[j], termsByDoc, header);
                        }
                    }
                }
                else if (splittedText[i].length() > 0 && splittedText[i].charAt(0) >= 'A' && splittedText[i].charAt(0) <= 'Z') {
                    String term = capitalLetterHandle(splittedText, i);
                    if (termsByDoc.containsKey(term.toLowerCase())) {
                        term = term.toLowerCase();
                    } else if (termsByDoc.containsKey(term.toUpperCase())) {
                        term = term.toUpperCase();
                    }
                    if(ifStem) {
                        term = stemmer.stemTerm(term);
                    }
                    termsByDoc = addToDic(term, termsByDoc, header);
                }
                else if ((splittedText[i].length() > 0))
                    if((splittedText[i].charAt(0) >= '0' && splittedText[i].charAt(0) <= '9') || splittedText[i].charAt(0) == '$') {
                        termsByDoc = addToDic(numberHandle(splittedText, i), termsByDoc, header);
                    }
                    else if (splittedText[i].length() > 0 && splittedText[i].charAt(0) >= 'a' && splittedText[i].charAt(0) <= 'z') {
                        if(splittedText[i].length()>2 && splittedText[i].charAt(splittedText[i].length()-1)=='s' && splittedText[i].charAt(splittedText[i].length()-2)=='\'')
                            splittedText[i]=splittedText[i].substring(0,splittedText[i].length()-2);
                        if (includeInTheDicInBigLetterInTheStart(splittedText[i], termsByDoc)) {//now i need to remove the term with the big letter and put it with small letter.
                            String BigLetterInTheStart = getBigLetterInTheStart(splittedText[i]);
                            int num = termsByDoc.get(BigLetterInTheStart).getKey();
                            termsByDoc.remove(BigLetterInTheStart);
                            Pair<Integer , Boolean> enterToTheDic=new Pair<>(num , false);
                            termsByDoc.put(splittedText[i], enterToTheDic);
                            splittedText[1]=deletePunctuation(splittedText[1]);

                            if(ifStem) {
                                splittedText[i] = stemmer.stemTerm(splittedText[i]);
                            }
                            termsByDoc = addToDic(splittedText[i], termsByDoc, header);

                        }
                        else if(splittedText[i].contains(",") && splittedText[i].charAt(splittedText[i].length()-1)>= 'a' &&  splittedText[i].charAt(splittedText[i].length()-1) <= 'z')
                        {
                            //  System.out.println(splittedText[i].toString()+"    first");
                            String[] split=splittedText[i].split(",");
                            // System.out.println(split[0].toString());//+" "+split[1].toString()+"    after split");
                            //  System.out.println(split[1].toString());
                            split[1]=deletePunctuation(split[1]);

                            if(ifStem) {
                                split[0] = stemmer.stemTerm(split[0]);
                                split[1] = stemmer.stemTerm(split[1]);
                            }
                            termsByDoc = addToDic(split[0], termsByDoc, header);
                            termsByDoc = addToDic(split[1], termsByDoc, header);
                        }
                        else if(splittedText[i].contains(".") && splittedText[i].charAt(splittedText[i].length()-1)>= 'a' &&  splittedText[i].charAt(splittedText[i].length()-1) <= 'z')
                        {
                            //  System.out.println(splittedText[i].toString()+"    first");
                            String[] split=splittedText[i].split("\\.");
                            // System.out.println(split[0].toString());//+" "+split[1].toString()+"    after split");
                            //  System.out.println(split[1].toString());
                            split[1]=deletePunctuation(split[1]);

                            if(ifStem) {
                                split[0] = stemmer.stemTerm(split[0]);
                                split[1] = stemmer.stemTerm(split[1]);
                            }
                            termsByDoc = addToDic(split[0], termsByDoc, header);
                            termsByDoc = addToDic(split[1], termsByDoc, header);
                        }
                        else if(splittedText[i].contains("\\\\") && splittedText[i].charAt(splittedText[i].length()-1)>= 'a' &&  splittedText[i].charAt(splittedText[i].length()-1) <= 'z')
                        {
                            //  System.out.println(splittedText[i].toString()+"    first");
                            String[] split=splittedText[i].split("\\\\");
                            // System.out.println(split[0].toString());//+" "+split[1].toString()+"    after split");
                            //  System.out.println(split[1].toString());

                            split[1]=deletePunctuation(split[1]);
                            if(ifStem) {
                                split[0] = stemmer.stemTerm(split[0]);
                                split[1] = stemmer.stemTerm(split[1]);
                            }
                            termsByDoc = addToDic(split[0], termsByDoc, header);
                            termsByDoc = addToDic(split[1], termsByDoc, header);
                        }
                        else {
                            splittedText[i]=deletePunctuation(splittedText[i]);

                            if(ifStem) {
                                splittedText[i] = stemmer.stemTerm(splittedText[i]);
                            }
                            termsByDoc = addToDic(splittedText[i], termsByDoc, header);
                        }
                    }
                    else {
                        /*
                        if(!splittedText[i].equals("-")) {
                            WriteFile wf = new WriteFile();
                            wf.write(splittedText[i], "d:\\documents\\users\\meretz\\Downloads\\forPostingFile\\New folder\\temp.txt");
                            wf.write("\n", "d:\\documents\\users\\meretz\\Downloads\\forPostingFile\\New folder\\temp.txt");
                        }
                        */
                        //System.out.println(splittedText[i]);
                    }
            }
        }
        termsByDoc=deleteStopWords(termsByDoc);
        String str=makeFewEntity(termsByDoc,20);
        staticTableParameter = new StaticTableParameter(docNum , getMaxWord(termsByDoc) , getDictionarySize(termsByDoc) , getDictionaryWOrdCount(termsByDoc));
        staticTableForAllTheDic.put(staticTableParameter.docNum,staticTableParameter.toString());
        return termsByDoc;
    }
    private String makeFewEntity(HashMap<String , Pair<Integer , Boolean>> dicByDoc,int numOfEntities) {
        PriorityQueue<Pair<String,Double>> entity=new PriorityQueue<>((o1, o2) -> {
            double d1 = o1.getValue();
            double d2 = o2.getValue();
            int freq1 = (int)d1;
            int freq2 = (int)d2;
            return Integer.compare(freq2,freq1);
        });
        Pair<String ,Double> pair;
        LinkedList<String> onlyName=new LinkedList<>(dicByDoc.keySet());
        for(int i=0; i<onlyName.size(); i++)
        {
            if (onlyName.get(i).length()!=0)
                if(onlyName.get(i).charAt(0)>='A' && onlyName.get(i).charAt(0)<='Z')
                {
                    double num=(double)dicByDoc.get(onlyName.get(i)).getKey();
                    if(dicByDoc.get(onlyName.get(i)).getValue()==true)
                    {
                        num=num*1.25;
                    }
                    pair=new Pair<>(onlyName.get(i) ,num);
                    entity.add(pair);
                }
        }
        int size=entity.size();
        Pair temp;
        StringBuilder stringBuilder=new StringBuilder();
        if(numOfEntities<=size) {
            for (int i = 0; i < numOfEntities; i++) {
                pair = entity.poll();
                temp = dicByDoc.get(pair.getKey());
                stringBuilder.append("=" + pair.getKey() + ":" + temp.getKey() + ":" + temp.getValue());
            }
        }
        else {
            for (int i = 0; i < size; i++) {
                pair = entity.poll();
                temp = dicByDoc.get(pair.getKey());
                stringBuilder.append("=" + pair.getKey() + ":" + temp.getKey() + ":" + temp.getValue());
            }
        }
        return stringBuilder.toString();
    }
    /**
     * get string and retrun the string without the hyphen (-)
     * @param s
     * @return
     */
    private String[] removeThisHyphen(String s) {
        String[] arr=null;
        for(int z=0; z<s.length()-1; z++)
        {
            if(s.charAt(z)=='-' && s.charAt(z+1)=='-')
                arr = s.split("--");
        }
        return arr;
    }

    /**
     * check if the string include hyphen (-)
     * @param s
     * @return
     */
    private boolean includeInsideHyphen(String s) {
        for(int z=0; z<s.length()-1; z++)
        {
            if(s.charAt(z)=='-' && s.charAt(z+1)=='-')
                return true;
        }
        return false;
    }

    /**
     * return the word that was the max times in the specific dic
     * @param termsByDoc
     * @return
     */
    private int getMaxWord(HashMap<String, Pair<Integer, Boolean>> termsByDoc) {
        int max=0;
        Set<String> set=termsByDoc.keySet();
        Iterator<String> iter=set.iterator();
        while(iter.hasNext()==true) {
            String str=iter.next();
            if(termsByDoc.get(str).getKey()>max)
                max=termsByDoc.get(str).getKey();
            //for (int k = 0; k < str.length; k++) {
            //     if (termsByDoc.get(str[k]).getKey() > max)
            //         max = termsByDoc.get(str[k]).getKey();
            //   }
        }
        return max;
    }

    /**
     * return the dictionary size with duplication
     * @param termsByDoc
     * @return
     */
    private int getDictionarySize(HashMap<String, Pair<Integer, Boolean>> termsByDoc) {
        int counter=0;
        Set<String> set=termsByDoc.keySet();
        Iterator<String> iter=set.iterator();
        while(iter.hasNext()==true) {
            String str = iter.next();
            counter=counter+termsByDoc.get(str).getKey();
        }
        /*
        int counter=0;
        Set<String> set=termsByDoc.keySet();
        String[] str=(String[])set.toArray();
        for(int k=0; k<str.length; k++)
        {
            counter=counter+termsByDoc.get(str[k]).getKey();
        }
        */
        return counter;
    }

    /**
     * return the dictionary size with no duplication
     * @param termsByDoc
     * @return
     */
    private int getDictionaryWOrdCount(HashMap<String, Pair<Integer, Boolean>> termsByDoc) {
        return termsByDoc.size();
    }

    /**
     * delete stop words from the dictionary
     * @param termsByDoc
     * @return
     */
    private HashMap<String, Pair<Integer , Boolean>> deleteStopWords(HashMap<String, Pair<Integer , Boolean>> termsByDoc ) {
        for(int j=0; j<stopWords.size(); j++)
        {
            if(termsByDoc.containsKey(stopWords.get(j))==true)
            {
                termsByDoc.remove(stopWords.get(j));
            }
        }
        return termsByDoc;
    }

    /**
     * get string and return this string with big letter in the start
     * @param str
     * @return
     */
    private String  getBigLetterInTheStart(String str)
    {
        char theFirstChar=str.charAt(0);
        char afterMakeSmall=Character.toUpperCase(theFirstChar);
        String afterMakeAllSmall=afterMakeSmall+str.substring(1,str.length());
        return afterMakeAllSmall;
    }

    /**
     * check if the string include parenthesis
     * @param str
     * @return
     */
    private boolean checkIfParenthesis(String str)
    {
        if(str.length()==0)
            return false;
        if(str.charAt(0)=='(' || str.charAt(str.length()-1)==')')
            return true;
        else
            return false;
    }

    /**
     * check if the string include square parenthesis
     * @param str
     * @return
     */
    private boolean checkIfSquareParenthesis(String str)
    {
        if(str.length()==0)
            return false;
        if(str.length()>1)
            if(str.charAt(0)=='[' || str.charAt(str.length()-1)==']' || str.charAt(str.length()-2)==']')
                return true;
            else
                return false;
        return false;
    }

    /**
     * check if the string include "
     * @param str
     * @return
     */
    private boolean checkIfQuote(String str)
    {
        if(str.length()==0 || str.length()==1)
            return false;

        if(str.charAt(0)=='\"' || str.charAt(str.length()-1)=='\"')
            return true;
        else
            return false;
    }

    /**
     * check if the dictionary already include the string in the hashMap
     * @param str
     * @param dic
     * @return
     */
    private boolean includeInTheDicInBigLetterInTheStart(String str,HashMap<String, Pair<Integer , Boolean>> dic)
    {
        char theFirstChar=str.charAt(0);
        char afterMakeSmall=Character.toUpperCase(theFirstChar);
        String afterMakeAllSmall=afterMakeSmall+str.substring(1,str.length());
        if(dic.containsKey(afterMakeAllSmall))
            return true;
        return false;
    }

    /**
     * get term and dictionary , and enter the term to the dictionary
     * @param term
     * @param dic
     * @param header
     * @return
     */
    private HashMap addToDic(String term , HashMap<String , Pair<Integer , Boolean>> dic , String header)
    {
        if(term==" ")
        {
            return dic;
        }
        if(dic.containsKey(term))
        {
            int num=dic.get(term).getKey();
            boolean bool=dic.get(term).getValue();
            Pair<Integer , Boolean> temp=new Pair(num+1 ,bool );
            // dic.get(term).getValue().setKey(dic.get(term).getValue().getKey()+1)
            dic.replace(term , temp);
        }
        else{
            boolean ifInHeader =checkIfInHeader(header ," "+term+" ");
            // term=deletePunctuation(term);
            Pair<Integer , Boolean> temp=new Pair(1 ,ifInHeader );
            dic.put(term,temp);
        }
        return dic;
    }

    /**
     * check if the string include in the header
     * @param header
     * @param term
     * @return
     */
    private boolean checkIfInHeader(String header, String term) {
        if(header.length()>1)
            return header.toLowerCase().contains(term.toLowerCase());
        else
            return false;
        /*
        String[] headerArr=header.split("\\s+");
        String[] termArr=term.split("\\s+");
        int counter=0;
        for (int j=0; j<headerArr.length; j++)
        {
            for (int x=0; x<termArr.length; x++)

                if(headerArr[j].toLowerCase().equals(termArr[x].toLowerCase()))
                counter++;
        }
        if(counter>=termArr.length)
            return true;
        else
            return false;
            */
    }

    /**
     * this function get string and return the string without the punctuation
     * @param str
     * @return
     */
    private String deletePunctuation(String str)
    {
        //   System.out.println(str);
        if(str.length()>0) {
            if (str.charAt(str.length() - 1) == '.') {
                str = str.substring(0, str.length() - 1);
                if (str.length() > 1)
                    if (str.charAt(str.length() - 1) == '.') {
                        str = str.substring(0, str.length() - 1);
                    }
            }
        }
        if(str.length()>0) {
            if (str.charAt(0) == '.') {
                str = str.substring(1, str.length());
                if (str.length() > 0)
                    if (str.charAt(0) == '.') {
                        str = str.substring(1, str.length());
                        if (str.length() > 0)
                            if (str.charAt(0) == '.') {
                                str = str.substring(1, str.length());
                                if (str.length() > 0)
                                    if (str.charAt(0) == '.') {
                                        str = str.substring(1, str.length());
                                    }
                            }
                    }
            }
        }
        if(str.length()>0) {
            if (str.charAt(str.length() - 1) == '-') {
                str = str.substring(0, str.length() - 1);
            }
        }
        if(str.length()>0) {
            if (str.charAt(str.length() - 1) == '\'') {
                str = str.substring(0, str.length() - 1);
            }
        }
        if(str.length()>0) {
            if (str.charAt(str.length() - 1) == '\'') {
                str = str.substring(0, str.length() - 1);
            }
        }
        if(str.length()>0) {
            if (str.charAt(0) == '-') {
                str = str.substring(1, str.length());
            }
        }
        if(str.length()>0) {
            if (str.charAt(0) == '%') {
                str = str.substring(1, str.length());
            }
        }
        if(str.length()>0) {
            if (str.charAt(0) == '-') {
                str = str.substring(1, str.length());
            }
        }

        if(str.length()>0) {
            if (str.charAt(0) == '/') {
                str = str.substring(1, str.length());
            }
        }
        if(str.length()>0) {
            if (str.charAt(str.length() - 1) == ',') {
                str = str.substring(0, str.length() - 1);
            }
        }
        if(str.length()>0) {
            if (str.charAt(0) == '`') {
                str = str.substring(1, str.length());
            }
        }
        if(str.length()>0) {
            if (str.charAt(0) == '\'') {
                str = str.substring(1, str.length());
            }
        }
        if(str.length()>0) {
            if (str.charAt(0) == '/') {
                str = str.substring(1, str.length());
            }
        }

        if(str.length()>0) {
            if (str.charAt(0) == '`') {
                str = str.substring(1, str.length());
            }
        }

        if(str.length()>0) {
            str = removeApotrop(str);
        }
        // System.out.println(str );

        return str;
    }

    private String removeApotrop(String str) {
        if(str.indexOf('\'')==str.length()-1)
        {
            str=str.substring(0,str.length()-1);
        }
        else if(str.indexOf('\'')==0)
        {
            str=str.substring(1,str.length());
        }
        return str;
    }

    /**
     * this function get word (specific index in the array) and return the word after parse with the rules for numbers
     * @param splittedText
     * @param index
     * @return
     */
    private String numberHandle(String[] splittedText, int index){//return the ready term
        word1 = splittedText[index];
        word2 = splittedText[index + 1];
        word3 = splittedText[index + 2];
        word4 = splittedText[index + 3];
        String term=isADateWithYear(word1,word2,word3);
        if(term.equals("")==false)//if its a date
        {
            i=i+2;
            return term;
        }
        term=isADate(word1,word2);
        if(term.equals("")==false)//if its a date
        {
            i=i+1;
            return term;
        }
        else if(word1.length()>3 && word1.charAt(word1.length()-1)=='h' &&  word1.charAt(word1.length()-2)=='t')
        {
            i=i+1;
            term=word1;
        }
        else if(word1.length()==2 && (word2.equals("a.m.") || word2.equals("p.m.") || word2.equals("A.M.") || word2.equals("P.M.") || word2.equals("am") || word2.equals("pm") || word2.equals("AM") || word2.equals("PM")))
        {
            term=clockTimeHandle(word1,word2);
            i=i+1;
        }
        else if(word2.equals("million") && word3.equals("U.S.") && word4.equals("dollars") )
        {
            term=word1+" M Dollars";
            i=i+3;
        }
        else if(word2.equals("billion") && word3.equals("U.S.") && word4.equals("dollars") )
        {
            term=word1+"000 M Dollars";
            i=i+3;
        }
        else if(word1.charAt(word1.length()-1)=='m' && word2.equals("Dollars")){
            term=word1.substring(0,word1.length()-1)+ " M Dollars";
            i=i+1;
        }
        else if(word1.charAt(word1.length()-1)=='n' && word1.charAt(word1.length()-2)=='b' || word2.equals("Dollars")){
            term=word1.substring(0,word1.length()-1 )+ "000 M Dollars";
            i=i+1;
        }
        else if(word2.equals("percent") || word2.equals("percentage")){
            term=word1+"%";
            i=i+1;
        }
        else if(word1.charAt(word1.length()-1)=='%')
        {
            term=word1;
        }

        else if(word2.equals("million") && word1.charAt(0)=='$'){
            term=word1.substring(1,word1.length())+" M Dollars";
            i=i+1;
        }
        else if(word2.equals("billion") && word1.charAt(0)=='$'){
            term=word1.substring(1,word1.length())+"000 M Dollars";
            i=i+1;
        }

        else if(word1.charAt(0)=='$' && word1.length()>17)
        {
            String str=word1.substring(1,word1.length());
            str=getOutPsik(str);
            str=str.substring(0,str.length()-6);
            term=str+" M Dollars";
        }
        else if(word1.charAt(0)=='$' && word1.length()>13)
        {
            String str=word1.substring(1,word1.length());
            str=getOutPsik(str);
            str=str.substring(0,str.length()-6);
            term=str+" M Dollars";
        }
        else if(word1.charAt(0)=='$' && word1.length()>9)
        {
            String str=word1.substring(1,word1.length());
            str=getOutPsik(str);
            str=str.substring(0,str.length()-6);
            term=str+" M Dollars";
        }

        else if(word1.length()>12 && word2.equals("Dollars")&& word1.charAt(0)!='$')
        {
            word1=getOutPsik(word1);
            i=i+1;
            term=word1.substring(0,word1.length()-9);
            term=term+","+word1.substring(word1.length()-9 , word1.length()-6);
            term=term+" M "+word2;
        }
        else if(word1.length()>9 && word2.equals("Dollars")&& word1.charAt(0)!='$')
        {
            word1=getOutPsik(word1);
            i=i+1;
            term=word1.substring(0,word1.length()-6);
            term=term+" M "+word2;
        }
        else if(word1.length()>6 && word2.equals("Dollars") && word1.charAt(0)!='$')
        {
            word1=getOutPsik(word1);
            i=i+1;
            term=word1.substring(0,word1.length()-6);
            term=term+" M "+word2;
        }
        else if(word3.equals("Dollars") && includeSlash(word2))
        {
            i=i+2;
            term=word1+" "+word2+" "+word3;
        }
        else if(word2.equals("Dollars") && word1.charAt(0)!='$'){
            term=word1+" "+word2;
            i=i+1;
        }
        else if(word1.charAt(0)=='$')
        {
            term=word1.substring(1,word1.length()) + " Dollars";
        }
        else if(word2.length()>1 && (word2.charAt(0)>='0'&& word2.charAt(0)<='9' && includeSlash(word2)) && includeSlash(word1)==false)
        {
            term=word1+" "+word2;
            i=i+1;
        }
        else if(includeSlash(word1))
        {
            term=word1;
        }
        else if(word1.contains("-"))
        {
            term=word1;
        }
        else// just word1 is number and the other word not necessary
        {
            String[] splittedNumber=word1.split("\\.");//split by dot
            term="";
            if(splittedNumber[0].length()>=1 && splittedNumber[0].length()<=3){
                term=word1;
                return term;
            }
            else if(splittedNumber[0].length()>=4 && splittedNumber[0].length()<=6){
                term=keep3DigitsAfterPoint(splittedNumber[0],3)+'K';
            }
            else  if(splittedNumber[0].length()>=7 && splittedNumber[0].length()<=9){
                term=keep3DigitsAfterPoint(splittedNumber[0],6)+'M';
            }
            else  if(splittedNumber[0].length()>=10 && splittedNumber[0].length()<=12){
                term=keep3DigitsAfterPoint(splittedNumber[0],9)+'B';
            }

            if(term.length()>=2) {
                if (term.charAt(term.length() - 2) == '0') {
                    String allTheNumber = term.substring(0, term.length() - 2);
                    String theLetter = term.substring(term.length() - 1);
                    term = allTheNumber + theLetter;
                }
            }
            if(term.length()>=2) {
                if (term.charAt(term.length() - 2) == '0') {
                    String allTheNumber = term.substring(0, term.length() - 2);
                    String theLetter = term.substring(term.length() - 1);
                    term = allTheNumber + theLetter;
                }
            }
            if(term.length()>=2) {
                if (term.charAt(term.length() - 2) == '0') {
                    String allTheNumber = term.substring(0, term.length() - 2);
                    String theLetter = term.substring(term.length() - 1);
                    term = allTheNumber + theLetter;
                }
            }
            if(term.length()>1) {
                char ch = term.charAt(term.length() - 1);
                term = deletePunctuation(term.substring(0, term.length() - 1));
                term = term + ch;
            }
            return term;
        }
        return term;
    }

    /**
     * this function get 2 strings and return them by clock show (00:00)
     * @param word1
     * @param word2
     * @return
     */
    private String clockTimeHandle(String word1, String word2) {
        if(word1.length()>2) {
            if (word1.charAt(1) == ':' || word1.charAt(2) == ':') {
                i = i + 1;
                return word1 + " " + word2;
            }
        }
        if(word2.equals("a.m.") ||word2.equals("A.M.") ) {
            i=i+1;
            return word1 + ":00";
        }
        else if(word2.equals("p.m.") || word2.equals("P.M.")) {
            int num=0;
            if (word1.length()>1 &&( word1.charAt(1)<'0' || word1.charAt(1)>'9'))
            {
                char ch=word1.charAt(0);
                num=Integer.parseInt(String.valueOf(ch));

            }
            else if (word1.length()>2 && (word1.charAt(2)<'0' || word1.charAt(2)>'9'))
            {
                char ch=word1.charAt(0);
                String str=ch+""+word1.charAt(1);
                num=Integer.parseInt(str);

            }
            else {
                num = Integer.parseInt(word1);
            }
            num=num+12;
            String str=String.valueOf(num);
            i=i+1;
            return str + ":00";
        }
        return "";
    }

    /**
     * get string and return the string without psik
     * @param str
     * @return
     */
    private String getOutPsik(String str)
    {
        String newStr="";
        for(int j=0; j<str.length(); j++)
        {
            if(str.charAt(j)!=',')
            {
                newStr=newStr+str.charAt(j);
            }
        }
        return newStr;
    }

    /**
     * check if the string include slash
     * @param str
     * @return
     */
    private boolean includeSlash(String str)
    {
        for(int j=0; j<str.length(); j++)
        {
            if(str.charAt(j)=='/')
                return true;
        }
        return false;
    }

    /**
     * this function get string and return the string after make parser with the rule for thousand
     * @param leftToPoint
     * @param DigitsToLetter
     * @return
     */
    private String keep3DigitsAfterPoint(String leftToPoint, int DigitsToLetter) {
        //the string is the significant digits
        //the int will become to 3=K 6=M 9=B

        String term="";
        for (int j=0;j<(leftToPoint.length())-DigitsToLetter;j++)
        {
            term=term+leftToPoint.charAt(j);
        }
        term=deletePunctuation(term);
        term=term+'.';
        for (int j=(leftToPoint.length())-DigitsToLetter;j<leftToPoint.length()-DigitsToLetter+3;j++)
        {
            term=term+leftToPoint.charAt(j);
        }
        return term;
    }

    /**
     * this function get word (specific index in the array) and return the word after parse with the rules for "between"
     * @param splittedText
     * @param index
     * @return
     */
    private String[] betweenToTerm(String[] splittedText, int index) {
        word1 = splittedText[index];
        word2 = splittedText[index + 1];
        word3 = splittedText[index + 2];
        word4 = splittedText[index + 3];
        String []termToReturn={word2 , word4 ,word2+"-"+word4 };
        i=i+3;
        return termToReturn;
    }

    /**
     * get word (specific index in the array) and return if the rule for between exist
     * @param splittedText
     * @param index
     * @return
     */
    private boolean betweenCheck(String[] splittedText, int index)
    {
        word1=splittedText[index];
        word2=splittedText[index+1];
        word3=splittedText[index+2];
        word4=splittedText[index+3];

        if(word2.charAt(0)>='0' && word2.charAt(0)<='9' && word4.charAt(0)>='0' && word4.charAt(0)<='9' && word3.equals("and"))
        {
            return true;
        }
        else return false;
    }

    /**
     * get string and return if the string include punctuation
     * @param str
     * @return
     */
    private boolean checkIfIncludePunctuation(String str)
    {
        if(str.length()>0)
            if(str.charAt(0)<'0' || str.charAt(0)>'9')
            {
                if(str.charAt(str.length()-1)=='.' || str.charAt(str.length()-1)==',' || str.charAt(str.length()-1)=='!' || str.charAt(str.length()-1)=='?' || str.charAt(str.length()-1)==':' || str.charAt(str.length()-1)==';' )
                    return true  ;
            }
        return false;
    }

    /**
     * this function get word (specific index in the array) and return the word after parse with the rules for big letter
     * @param splittedText
     * @param index
     * @return
     */
    private String capitalLetterHandle(String[] splittedText, int index){
        //need to check if there is dot or psik !? between two words and if there is dont make them together
        word1=splittedText[index];
        word2=splittedText[index+1];
        word3=splittedText[index+2];
        word4=splittedText[index+3];
        if(checkIfIncludePunctuation(word1))
        {
            word2=" ";
            word3=" ";
            word4=" ";
            word1=deletePunctuation(word1);
        }
        else if(checkIfIncludePunctuation(word2))
        {
            word3=" ";
            word4=" ";
            word1=deletePunctuation(word1);

        }
        else if(checkIfIncludePunctuation(word3))
        {
            word4=" ";
            word3=deletePunctuation(word3);
        }
        else {
            word4 = deletePunctuation(word4);
        }
        String term=isADateWithYear(word1,word2,word3);
        if(term.equals("")==false)//if its a date
        {
            i=i+2;
            return term;
        }
        term=isADateWithYear(word1,word2 , word3);
        if(term.equals("")==false)//if its a date
        {
            i=i+2;
            return term;
        }
        term=isADate(word1,word2);
        if(term.equals("")==false)
        {
            i=i+1;
            return term;
        }
        if (word2.length()!=0 && word2.charAt(0)>='A' && word2.charAt(0)<='Z'){//checking next word
            if (word3.length()!=0 && word3.charAt(0)>='A' && word3.charAt(0)<='Z'){//checking next word
                if (word4.length()!=0 && word4.charAt(0)>='A' && word4.charAt(0)<='Z'){
                    i=i+3;
                    term= word1+" "+word2+" "+word3+" "+word4;
                }
                else {
                    i=i+2;
                    term= word1+" "+word2+" "+word3;
                }
            }
            else {
                i=i+1;
                term= word1+" "+word2;
            }
        }
        else{
            term= word1;
        }
        return term;
    }

    /**
     * this function get 2 string and return if both of them are the sane like the date format
     * @param word1
     * @param word2
     * @return
     */
    private String isADate(String word1,String word2){
        String ans="";
        word1=deletePunctuation(word1);
        word2=deletePunctuation(word2);
        for (int j=0;j<months.length;j++){
            if(word1.equals(months[j]) &&word2.length()>0 && (word2.charAt(0)>='0' && word2.charAt(0)<='9')){
                //   if((word2.length()==2 && Integer.parseInt(word2)>=1 && Integer.parseInt(word2)<=31) || (word2.length()==4) ||(word2.length()==1 ))
                {
                    ans=dateToTerm(word1,word2);
                }
            }
            if(word2.equals(months[j]) && word1.length()>0 &&(word1.charAt(0)>='0' && word1.charAt(0)<='9')){
                //  if((word1.length()==2 && Integer.parseInt(word1)>=1 && Integer.parseInt(word1)<=31) ||( word1.length()==4) ||(word1.length()==1) )
                {
                    ans=dateToTerm(word2,word1);
                }
            }

        }
        return ans;
    }

    /**
     * this function get 3 string and return if both of them are the sane like the date format
     * @param word1
     * @param word2
     * @param word3
     * @return
     */
    private String isADateWithYear(String word1,String word2 , String word3){
        String ans="";
        word1=deletePunctuation(word1);
        word2=deletePunctuation(word2);
        word3=deletePunctuation(word3);

        for (int j=0;j<months.length;j++){
            if(word1.equals(months[j]) &&word2.length()>0 && (word2.charAt(0)>='0' && word2.charAt(0)<='9')&&(word2.length()==1 || word2.length()==2) && word3.length()==4 && word3.charAt(0)>='0' && word3.charAt(0)<='9'){
                //   if((word2.length()==2 && Integer.parseInt(word2)>=1 && Integer.parseInt(word2)<=31) || (word2.length()==4) ||(word2.length()==1 ))
                {
                    ans=dateToTermWithYear(word1,word2 , word3);
                }
            }
            if(word2.equals(months[j])&&word1.length()>0  &&( word1.charAt(0)>='0' && word1.charAt(0)<='9') &&(word1.length()==1 || word1.length()==2) && word3.length()==4 && word3.charAt(0)>='0' && word3.charAt(0)<='9'){
                //  if((word1.length()==2 && Integer.parseInt(word1)>=1 && Integer.parseInt(word1)<=31) ||( word1.length()==4) ||(word1.length()==1) )
                {
                    ans=dateToTermWithYear(word2,word1 , word3);
                }
            }

        }
        return ans;
    }

    /**
     * this function get month in string and return the number of the month
     * @param word
     * @return
     */
    private String makeNameToNumberMonth(String word)
    {
        if(word.equals("January") || word.equals("Jan")||word.equals("JANUARY"))
        {
            return "01";
        }
        else if(word.equals("February")||word.equals("Feb")||word.equals("FEBRUARY"))
        {
            return "02";
        }
        else if(word.equals("March")||word.equals("Mar")||word.equals("MARCH"))
        {
            return "03";
        }
        else if(word.equals("April")||word.equals("Apr")||word.equals("APRIL"))
        {
            return "04";
        }
        else if(word.equals("May") ||word.equals("MAY"))
        {
            return "05";
        }
        else if(word.equals("June") || word.equals("Jun") ||word.equals("JUNE"))
        {
            return "06";
        }
        else if(word.equals("July")|word.equals("Jul")||word.equals("JULY"))
        {
            return "07";
        }
        else if(word.equals("August") ||word.equals("Aug")||word.equals( "AUGUST"))
        {
            return "08";
        }
        else if(word.equals("September")||word.equals("Sep")||word.equals("SEPTEMBER"))
        {
            return "09";
        }
        else if(word.equals("October")||word.equals("Oct")||word.equals("OCTOBER"))
        {
            return "10";
        }
        else if(word.equals("November")||word.equals("Nov")||word.equals("NOVEMBER"))
        {
            return "11";
        }
        else if(word.equals("December")||word.equals("Dec")||word.equals("DECEMBER"))
        {
            return "12";
        }
        return "";
    }

    /**
     * this function get month and day or month and year and return the term for date
     * @param word
     * @param number
     * @return
     */
    private String dateToTerm(String word, String number){
        String month=makeNameToNumberMonth(word);
        String termToReturn="";
        if(number.length()==4)
        {
            termToReturn=number+"-"+month;
        }
        else if(number.length()==1)
        {
            termToReturn=month+"-"+'0'+number;

        }
        else if(number.length()==2)
        {
            termToReturn=month+"-"+number;
        }
        else
        {
            termToReturn=number+" "+word;
        }
        return termToReturn;
    }

    /**
     * this function get month, day and year and return the term for date
     * @param word
     * @param number
     * @param year
     * @return
     */
    private String dateToTermWithYear(String word, String number , String year){
        String month=makeNameToNumberMonth(word);
        String termToReturn="";
        if(number.length()==1)
        {
            termToReturn=month+"-"+'0'+number+"-"+year;

        }
        else if(number.length()==2)
        {
            termToReturn=month+"-"+number+"-"+year;
        }
        return termToReturn;
    }

    private HashMap returnDicAfterStemming(HashMap map , String oldPath) throws IOException {
      /*  oldPath="C:\\Users\\shachar meretz\\Desktop\\try";

        String path=oldPath+"\\forStem.txt";
        File file = new File(path);//+"\\"+fileName+".txt");
        if(file.exists()==false) {
            file.createNewFile();
        }
        else {
            file.delete();
            file.createNewFile();
        }
        WriteFile writeFile=new WriteFile();
        */
        //      LinkedList<String> keys = new LinkedList<String>(map.keySet());
    /*    for(int j=0; j<keys.size(); j++)
        {
            writeFile.writeLine(path,keys.get(j) , "");
        }
*/
        Stemmer stem=new Stemmer();
        String[] arr=new String[1];
        arr[0]=path;
        //  LinkedList<String> afterStem=
        stem.useStemmer(arr);
        return map;
    }


}