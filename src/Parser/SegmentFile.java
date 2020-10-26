import javafx.util.Pair;

import java.io.*;
import java.util.*;

public class SegmentFile {

    HashMap<String, String> dic;
    String path;
    static int m = 0;
    HashMap<String, HashMap<String, Pair<Integer , Boolean>>> newDic;
    boolean ifStem;

    public SegmentFile(HashMap<String, HashMap<String, Pair<Integer , Boolean>>> oldDic, String path, boolean ifStem) throws IOException {
        this.path = path;
        //m=0;
        newDic = oldDic;
        this.ifStem = ifStem;
    }

    public void setStatic()
    {
        m=0;
    }
    /**
     * get string and return the value -97 of the first letter of the string
     *
     * @param str
     * @return integer
     */
    private int getFirstLetter(String str) {
        if (str.length() > 0) {
            if (str.charAt(0) >= '0' && str.charAt(0) <= '9')
                return 26;
            str = str.toLowerCase();
            if (str.charAt(0) >= 'a' && str.charAt(0) <= 'z')
                return str.charAt(0) - 97;
        }
        return 27;
    }

    /**
     * get dictionary from parser and for them segment file in the computer
     * this function make segment file to every letter and counter it with static parameter
     * @throws IOException
     */
    public void writeToSegmanetFile() throws IOException {
        LinkedList<String>[] arr = new LinkedList[28];
        WriteFile wf = new WriteFile();
        String fileName;

        for (int j = 0; j < arr.length; j++) {
            arr[j] = new LinkedList<>();
        }


        LinkedList<String> keys = new LinkedList<String>(newDic.keySet());
        for (int i = 0; i < newDic.size(); i++) {
            HashMap<String, Pair<Integer , Boolean>> values = newDic.get(keys.get(i));
            Set<String> key = values.keySet();
            for (String keyFromArr : key) {
                int firstLetter = getFirstLetter(keyFromArr);//if number 26 if garbage 27
                arr[firstLetter].add(keyFromArr + "=" + keys.get(i) + "=" + values.get(keyFromArr));
            }
        }

        if (ifStem) {
            for (int x = 0; x < arr.length - 1; x++) {
                File file;
                if (x == arr.length - 2) {
                    file = new File(path + "\\numbers" + "" + m + "stem");
                } else {
                    file = new File(path + "\\" + (char) (x + 97) + "" + m + "stem");
                }
                FileOutputStream outputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(arr[x]);
                objectOutputStream.close();
                outputStream.close();
            }
        } else {
            for (int x = 0; x < arr.length - 1; x++) {
                File file;
                if (x == arr.length - 2) {
                    file = new File(path + "\\numbers" + "" + m);
                } else {
                    file = new File(path + "\\" + (char) (x + 97) + "" + m);
                }
                FileOutputStream outputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(arr[x]);
                objectOutputStream.close();
                outputStream.close();
            }
            /*
            FileOutputStream file = new FileOutputStream(, true);
            file.write(arr[x].toJSONString().getBytes());
            file.close();*/

        }


        //   BufferedWriter writer = new BufferedWriter(new FileWriter(path + "\\" +(char)(i+97)+""+m+ ".json") );
        //  writer.write(arr[i].toJSONString() );
        // writer.close();


        // if (arr[i].toString().equals("{}") == false)
        //    {
        //    if(Files.exists(Paths.get(path + "\\" + (i) + ".json"))){
        // Files.write(Paths.get(path + "\\" +(char)(i+97)+""+m+ ".json"),arr[i].toJSONString().getBytes() , StandardOpenOption.APPEND);
        //   }
        //   else{
        //Files.write(Paths.get(path + "\\" + (i) + ".json"),arr[i].toJSONString().getBytes() ,  StandardOpenOption.CREATE );
        //   }
        //   }



/*
       FileOutputStream file=new FileOutputStream(path + "\\" + (m) + ".json" , true);
       file.write(arr.toJSONString().getBytes());
        file.close();
*/
        // Files.write(Paths.get(path + "\\" + (m) + ".json"),arr.toJSONString().getBytes() , StandardOpenOption.APPEND);
        m++;
    }



    {
    /*
        private boolean isALetter(char ch) {
        if (ch >= 'a' && ch <= 'z')
            return true;
        if (ch >= 'A' && ch <= 'Z')
            return true;
        return false;
    }


    public HashMap<String, String> makeNewDic(HashMap<String, HashMap<String, Integer>> dictionary) {
        HashMap<String, String> termAndValues = new HashMap<>();
        LinkedList<String> keys = new LinkedList<String>(dictionary.keySet());
        for (int i = 0; i < keys.size(); i++) {
            HashMap hm = dictionary.get(keys.get(i));
            LinkedList<String> key = new LinkedList<String>(hm.keySet());
            LinkedList<Pair> value = new LinkedList<Pair>(hm.values());
            for (int j = 0; j < value.size(); j++) {
                Pair<Integer, Boolean> name = value.get(j);
                String str = keys.get(i) + " " + name.getKey() + " " + name.getValue();
                //str=str+value.get(j);
                termAndValues.put(key.get(j), str);
            }
        }
        return termAndValues;
    }

    public SortedMap makeSortedDic(HashMap<String , HashMap<String ,Integer>> dictionary) {
            HashMap<String, String> hashMap = makeNewDic(dictionary);
            SortedMap<String, String> m = Collections.synchronizedSortedMap(new TreeMap());
            LinkedList<String> keys = new LinkedList<String>(hashMap.keySet());
            LinkedList<String> values = new LinkedList<String>(hashMap.values());
            for (int i = 0; i < keys.size(); i++) {
                m.put(keys.get(i), values.get(i));
            }
            System.out.println(m.toString());
            return m;
        }

    public void makeFiles(String path) throws IOException {
            //need to create 30 files
            String fileName;
            for (int j = 0; j < 28; j++) {
               // for (int k = 97; k < 123; k=k+13) {
                    fileName = j +"";//+ "" + (char) k;
                    File file = new File(path + "\\" + fileName + ".json");//+"\\"+fileName+".txt");
                    file.createNewFile();
                }

           // File file = new File(path + "\\numbers.txt");//+"\\"+fileName+".txt");
        }

    public void writeToSegmanetFile() throws IOException {
        JSONObject[] arr = new JSONObject[28];
        WriteFile wf = new WriteFile();

        for (int j = 0; j < arr.length; j++) {
            arr[j] = new JSONObject();
        }
        LinkedList<String> keys = new LinkedList<>(dic.keySet());
        for (int i = 0; i < dic.size(); i++) {
            int firstLetter = getFirstLetter(keys.get(i));//if number 26 if garbage 27;
            arr[firstLetter].put(keys.get(i), dic.get(keys.get(i)));
        }

        for (int i = 0; i < arr.length; i++) {
           // if (arr[i].toString().equals("{}") == false)
            {
            //    if(Files.exists(Paths.get(path + "\\" + (i) + ".json"))){
                Files.write(Paths.get(path + "\\" + (i) + ".json"),arr[i].toJSONString().getBytes() , StandardOpenOption.APPEND);
             //   }
             //   else{
                    //Files.write(Paths.get(path + "\\" + (i) + ".json"),arr[i].toJSONString().getBytes() ,  StandardOpenOption.CREATE );
             //   }
            }
        }
    }

    public void writeToSegmanetFileSHACHAR() throws IOException {
        JSONObject[] arr = new JSONObject[28];
        WriteFile wf = new WriteFile();
        String fileName;

        for (int j = 0; j < arr.length; j++) {
            arr[j] = new JSONObject();
        }


        LinkedList<String> keys= new LinkedList<String>(newDic.keySet());
        for(int i=0; i<newDic.size(); i++) {
            HashMap<String, Integer> values = newDic.get(keys.get(i));
            Set<String> key = values.keySet();
            for (String keyFromArr : key) {
                    int firstLetter = getFirstLetter(keyFromArr);//if number 26 if garbage 27
                    arr[firstLetter].put(keyFromArr, keys.get(i) + " " + values.get(keyFromArr));
                }
            }


        for (int x = 0; x< arr.length-2; x++) {
            File file=new File(path + "\\" + (char) (x + 97) + "" + m);
            FileOutputStream outputStream=new FileOutputStream(file);
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(arr[x]);
            objectOutputStream.close();
            outputStream.close();
            /*
            FileOutputStream file = new FileOutputStream(, true);
            file.write(arr[x].toJSONString().getBytes());
            file.close();

        }

         //   BufferedWriter writer = new BufferedWriter(new FileWriter(path + "\\" +(char)(i+97)+""+m+ ".json") );
          //  writer.write(arr[i].toJSONString() );
           // writer.close();



            // if (arr[i].toString().equals("{}") == false)
        //    {
                //    if(Files.exists(Paths.get(path + "\\" + (i) + ".json"))){
                // Files.write(Paths.get(path + "\\" +(char)(i+97)+""+m+ ".json"),arr[i].toJSONString().getBytes() , StandardOpenOption.APPEND);
                //   }
                //   else{
                //Files.write(Paths.get(path + "\\" + (i) + ".json"),arr[i].toJSONString().getBytes() ,  StandardOpenOption.CREATE );
                //   }
         //   }




       FileOutputStream file=new FileOutputStream(path + "\\" + (m) + ".json" , true);
       file.write(arr.toJSONString().getBytes());
        file.close();

       // Files.write(Paths.get(path + "\\" + (m) + ".json"),arr.toJSONString().getBytes() , StandardOpenOption.APPEND);
        m++;
    }

    public void writeToSegmanetFilePELEG() throws IOException {
        JSONObject[] arr = new JSONObject[28];
        WriteFile wf = new WriteFile();
        String fileName;

        /*
        for (int j = 'a'; j <='z'; j++) {
            // for (int k = 97; k < 123; k=k+13) {
          //  fileName = j+""+m;//+ "" + (char) k;
            File file = new File("C:\\Users\\shachar meretz\\Desktop\\try" + "\\" + (char)j+m+".json");//+"\\"+fileName+".txt");
            file.createNewFile();
        }

        for (int j = 0; j < arr.length; j++) {
            arr[j] = new JSONObject();
        }

        LinkedList<String> keys = new LinkedList<>(dic.keySet());
        for (int i = 0; i < dic.size(); i++) {
            int firstLetter = getFirstLetter(keys.get(i));//if number 26 if garbage 27;
            arr[firstLetter].put(keys.get(i), dic.get(keys.get(i)));
        }

        for (int i = 0; i < arr.length-2; i++) {

            FileOutputStream file=new FileOutputStream(path + "\\" +(char)(i+97)+""+m+ ".json" , true);
            file.write(arr[i].toJSONString().getBytes());
            file.close();



            //   BufferedWriter writer = new BufferedWriter(new FileWriter(path + "\\" +(char)(i+97)+""+m+ ".json") );
            //  writer.write(arr[i].toJSONString() );
            // writer.close();



            // if (arr[i].toString().equals("{}") == false)
            //    {
            //    if(Files.exists(Paths.get(path + "\\" + (i) + ".json"))){
            // Files.write(Paths.get(path + "\\" +(char)(i+97)+""+m+ ".json"),arr[i].toJSONString().getBytes() , StandardOpenOption.APPEND);
            //   }
            //   else{
            //Files.write(Paths.get(path + "\\" + (i) + ".json"),arr[i].toJSONString().getBytes() ,  StandardOpenOption.CREATE );
            //   }
            //   }
        }



       FileOutputStream file=new FileOutputStream(path + "\\" + (m) + ".json" , true);
       file.write(arr.toJSONString().getBytes());
        file.close();

        // Files.write(Paths.get(path + "\\" + (m) + ".json"),arr.toJSONString().getBytes() , StandardOpenOption.APPEND);
        m++;
    }

    public void writeToSegmanetFileAsi() throws IOException {
    JSONObject arr = new JSONObject();
    //WriteFile wf = new WriteFile();

    LinkedList<String> keys = new LinkedList<>(dic.keySet());
    for (int i = 0; i < keys.size(); i++) {
        arr.put(keys.get(i) , dic.get(keys.get(i)));
    }
    BufferedWriter writer = new BufferedWriter(new FileWriter(path + "\\" + (m) + ".json") );
    writer.write(arr.toJSONString() );
    writer.close();

    //    FileOutputStream file=new FileOutputStream(path + "\\" + (m) + ".json" , true);
    //   file.write(arr.toJSONString().getBytes());
    //  file.close();

    // Files.write(Paths.get(path + "\\" + (m) + ".json"),arr.toJSONString().getBytes() , StandardOpenOption.APPEND);
    m++;
}

 private void sortDictionary() {
        /*
        LinkedList<HashMap<String, Integer>> hm = new LinkedList<>(dictionary.values());
        LinkedList<String> key = new LinkedList<String>(temp2.keySet());
        for (int x = 0; x < hm.size(); x++) {
            HashMap<String, Integer> temp2 = hm.get(x);
            SortedSet<String> docno = new TreeSet<>(dictionary.keySet());
            SortedSet<String> keys = new TreeSet<>((dictionary.values()).keySet());
        }

    }

    public void tryNewOne() throws IOException {
        /*
        path = "C:\\Users\\shachar meretz\\Desktop\\try";
        JSONObject temp = new JSONObject();
        WriteFile wf = new WriteFile();
        LinkedList<String> keys = new LinkedList<String>(dictionary.keySet());
        //  LinkedList<HashMap<String, Integer>> hm = new LinkedList<>(dictionary.values());
        // for (int x = 0; x < hm.size(); x++) {
        //   HashMap<String, Integer> temp2 = hm.get(x);
        // LinkedList<String> key = new LinkedList<String>(temp2.keySet());
        // LinkedList<Integer> value = new LinkedList<Integer>(temp2.values());
        //for (int i = 0; i < key.size(); i++) {
        //  temp.put(key.get(i), value.get(i));

        //wf.writeFun(temp, path + "\\" + j + ".txt");
        //j = j + 1;

    }

    public void makeSegmentFile() throws IOException {
/*
       // long startTime = System.nanoTime();


        WriteFile wf=new WriteFile();
       LinkedList<String> afterConvert=convertHashMapToLinkedList(dictionary);
       // JSONObject jsonObject = new JSONObject();

        for (int i=0; i<afterConvert.size(); i++) {
            String[] wordWithCount = afterConvert.get(i).split(" ");
           String theWord=wordWithCount[0];
           if(theWord.indexOf("P=")!=-1)
           {

           }
           else
           {
           String hash=hashFunction(theWord);
           if(hash!="") {
               if(hash.equals("numbers"))
               {
                  String newPath = path + "\\" + hash + ".txt";
                //   wf.writeFun(newPath, afterConvert.get(i).split("\\s+")[0], "");
               }
               else {
                   //jsonObject.put(afterConvert.get(i), docno);

                   String newPath = path + "\\" + hash + ".txt";
                  // wf.writeFun(newPath, afterConvert.get(i), docno);

               }
           }
           }
        }

        String newPath = path + "\\garbage.txt";
        FileWriter file = new FileWriter(newPath+".json", true);
        file.write(jsonObject.toJSONString());//toJSONString());
        file.close();


    //   long endTime = System.nanoTime();
    //  long timeElapsed = (endTime - startTime)/100000000;

    // System.out.println("Execution time in nanoseconds  : " + timeElapsed);


    }


    private LinkedList<String> convertHashMapToLinkedList(HashMap<String, Integer> hm) {
        LinkedList<String> ans = new LinkedList<>();
        if (hm != null) {
            HashMap<String, Integer> map = hm;
            LinkedList<String> keys = new LinkedList<String>(map.keySet());
            LinkedList<Integer> values = new LinkedList<Integer>(map.values());
            for (int i = 0; i < keys.size(); i++) {
                String str = keys.get(i) + " " + values.get(i);
                ans.add(str);
            }
        }
        return ans;
    }

    private String hashFunction(String term) {

        if (term.length() != 0) {
            char ch = term.charAt(0);
            if (term.charAt(0) >= '0' && term.charAt(0) <= '9') {
                return "numbers";
            }
            if (term.length() > 1) {
                if (isALetter(term.charAt(0)) == true && isALetter(term.charAt(1)) == true) {
                    return term.charAt(0) + "" + term.charAt(1);
                }
            } else if (isALetter(term.charAt(0)) == true) {
                return term.charAt(0) + "" + term.charAt(0);
            }
        }

        return "garbage";
    }

    ///////////////////////////////////////////////////////////////////////
    private int hashFunctiontemp(String term) {

        int num = 0;
        if (term.length() != 0) {
            char ch = term.charAt(0);
            if (term.charAt(0) >= '0' && term.charAt(0) <= '9') {
                return 0;
            }
            if (term.length() > 0) {
                if (isALetter(term.charAt(0)) == true)//&& isALetter(term.charAt(1)) == true) {
                    num = term.charAt(0) - 96;
                if (num < 0)
                    return 26;
                else
                    return num;
                //  return term.charAt(0)-96;// + "" + term.charAt(1);
            }
        }
        /*
            else if(isALetter(term.charAt(0))==true){
                return term.charAt(0) + "" + term.charAt(0);
            }

        return 26;
        // return "garbage";
    }

    public void makeSegmentFiletrySomething() throws IOException {
/*
        // long startTime = System.nanoTime();
        //WriteFile wf=new WriteFile();
        LinkedList<String> keys = new LinkedList<String>(dictionary.keySet());
        LinkedList<Integer> values = new LinkedList<Integer>(dictionary.values());
       // LinkedList<String> afterConvert=convertHashMapToLinkedList(dictionary);
        JSONObject[] arr=new JSONObject[27];

        for (int j=0; j<arr.length;j++) {
            arr[j]=new JSONObject();
        }
StringBuilder sb=new StringBuilder();
        for (int i=0; i<keys.size(); i++) {
            sb.append(keys.get(i)+" "+values.get(i));
            String theWord=keys.get(i);
           // sb.append(keys.get(i));

          //  StringBuilder sb=keys.get(i)+" "+values.get(i);
          //  String[] wordWithCount = afterConvert.get(i).split(" ");
           // String theWord=wordWithCount[0];
            if(theWord.indexOf("P=")!=-1)
           {

          }
            else
            {
                String hash=hashFunction(theWord);
                if(hash!="") {
                    if(hash.equals("numbers"))
                    {
                        arr[26].put(sb.toString(), docno);

                        //String newPath = path + "\\" + hash + ".txt";
                        //wf.writeLine(newPath, afterConvert.get(i).split("\\s+")[0], "");
                    }
                    else {
                        char ch=hash.charAt(0);//Integer.parseInt(hash);
                        int num=ch;
                        num=num-97;
                        if(sb.toString()!=null && num>0 && num<27) {
                            arr[num].put(sb.toString(), docno);
                        }
                       // String newPath = path + "\\" + hash + ".txt";
                        //wf.writeLine(newPath, afterConvert.get(i), docno);

                    }
                }
            }
        }

       // String newPath = path + "\\garbage.txt";
        for (int j=0; j<arr.length;j++) {
            FileWriter file = new FileWriter(path+"\\a" + ".json", true);
            file.write(arr[j].toJSONString());//toJSONString());
            file.close();
        }


        //   long endTime = System.nanoTime();
        //  long timeElapsed = (endTime - startTime)/100000000;

        // System.out.println("Execution time in nanoseconds  : " + timeElapsed);


    }


    public void trySomething() throws IOException {
/*
        path="C:\\Users\\shachar meretz\\Desktop\\try";
        // long startTime = System.nanoTime();
        JSONObject[] arr = new JSONObject[27];

        for (int j = 0; j < arr.length; j++) {
            arr[j] = new JSONObject();
        }
        WriteFile wf = new WriteFile();
        LinkedList<String> keys = new LinkedList<String>(dictionary.keySet());
        LinkedList<Integer> value = new LinkedList<Integer>(dictionary.values());
        LinkedList<Integer> values = new LinkedList<Integer>(dictionary.values());


        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            int num = hashFunctiontemp(keys.get(i));
             arr[num].put(keys.get(i), values.get(i));
        //    sb.delete(0, sb.length());
         //   sb.append(keys.get(i) + " " + values.get(i));
            String theWord = keys.get(i);

            if (theWord.indexOf("P=") != -1) {

            } else {
                String hash = hashFunction(theWord);
                if (hash != "") {
                    if (hash.equals("numbers")) {
                        String newPath = path + "\\" + hash + ".txt";
                        wf.writeFun(arr[num], newPath);
                    } else {
                        String newPath = path + "\\" + hash + ".txt";
                        wf.writeFun(arr[num], newPath);

                    }
                }
            }

            for (int j = 0; j < arr.length; j++) {
                String str = path + "\\" + j + ".text";
                FileWriter file = new FileWriter(str, true);
                file.write(arr[j].toJSONString());
                file.close();
            }

            //    }
            // }
    */
    }

}