import javafx.util.Pair;
import java.io.FileReader;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ReadFile {
    static long timeToRead=0;
    String pathForCorpus;//the path to the folder
    String pathForStopWords;
    String path;
    String text;//the text i read now
    public LinkedList<String> stopWords = new LinkedList<>();
    private static Pattern equalPat = Pattern.compile("<TEXT>");

    public ReadFile(String path) throws IOException {
        this.path = path;
    }

    /**
     * get path and enter the real path to all of the field in this class
     * @param path
     */
    private void makePathForTheTwo(String path) {
        File directoryPath = new File(path);
        //List of all files and directories
        String[] allTheFolderName;
        allTheFolderName = directoryPath.list();//all the folders in corpus
        for(int i=0; i<allTheFolderName.length; i++)
        {
            File file=new File(path+"\\"+allTheFolderName[i]);
            if(file.isFile())
            {
                pathForStopWords=path+"\\"+allTheFolderName[i];
            }
            else
            {
                pathForCorpus=path+"\\"+allTheFolderName[i];
            }
        }
    }

    /**
     * this function get path and read line by line the text in this file
     * @param newPath
     * @return link list that every node in the list is one line
     * @throws IOException
     */
    public LinkedList readLineByLine(String newPath) throws IOException {
        File newText = new File(newPath);
        String allText = new String();
        LinkedList<String> list = new LinkedList<>();
        Scanner scanner = new Scanner(newText);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            list.add(line);

        }
        return list;
    }

    public String readThisLine(String path,int lineNo) throws IOException {
        Stream<String> lines = Files.lines(Paths.get(path));
        String line = lines.skip(lineNo).findFirst().get();
        /*
        File file=new File(path);
        Scanner scanner = new Scanner(file);
        int lineCounter=0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(lineCounter==lineNo){
                return line;
            }
            lineCounter++;
        }
        return null;
        */
        return line;
    }

    /**
     * this function get path and read all the text together
     * @param file
     * @return string eith all the text
     * @throws IOException
     */
    public StringBuilder readAllText(File file) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder str = new StringBuilder();
        String st;
        while ((st = br.readLine()) != null) {
            str = str.append(st);
        }
        return str;
    }

    /**
     * this function read the stop words and enter it to the linked list in this class
     * @throws IOException
     */
    public void readStopWords() throws IOException {

        stopWords = readLineByLine(pathForStopWords);
        //for (int i = 0; i < stopWords.size(); i++) {
        // if (stopWords.get(i).length() == 1) {
        //     stopWords.remove(i);
        //     i = i - 1;
        // }
        //}

    }

    /**
     * this function manage all the dictionary making. this function read all the text in the corpus and sent it by batch to the parser
     * @param ifStem
     * @param pathForParse
     * @throws IOException
     */
    public void readFromFile(boolean ifStem ,String pathForParse) throws IOException {
        // String fileName;
       /* for (int j = 0; j < 605; j++) {
            // for (int k = 97; k < 123; k=k+13) {
            fileName = j + "";//+ "" + (char) k;
            File file = new File("C:\\Users\\shachar meretz\\Desktop\\try" + "\\" + fileName + ".json");//+"\\"+fileName+".txt");
            file.createNewFile();
        }
*/
        SegmentFile segmentFile=new SegmentFile(null, path,ifStem);
        segmentFile.setStatic();

        makePathForTheTwo(path);
        readStopWords();
        String file2Name;
        // readStopWords();
        StringBuilder allText = new StringBuilder();
        String[] allTheFolderName;
        String TheTextName;
        //Creating a File object for directory
        File directoryPath = new File(pathForCorpus);
        //List of all files and directories
        allTheFolderName = directoryPath.list();//all the folders in corpus
        /*allTheFolderName = new String[contents.length];
        for (int i = 0; i < contents.length; i++) {
            allTheFolderName[i] = contents[i];
        }*/
        //docno , text , header
        Parse parse=null;
        LinkedList<Pair<String, Pair<String , String>>> allTheDoc = new LinkedList<>();//list with all the text we already read from the folder
        for (int i = 0; i < allTheFolderName.length; i++) {
            String header="";
            file2Name = allTheFolderName[i];
            parse = new Parse(pathForParse, stopWords, ifStem);//check we already read the stop words
            File text = new File(pathForCorpus + "\\" + file2Name);
            String info[] = text.list();
            TheTextName = info[0];
            //String name = TheTextName;
            File newText = new File(pathForCorpus + "\\" + file2Name + "\\" + TheTextName);
            allText = readAllText(newText);//manyOfTextInOneFile
            //now i need to split all the text - DOCNO and TEXT together in the same pair
            String[] afterSplit = equalPat.split(allText.toString());
            // String docNum=getOutTheDocNum(afterSplit[0]);
            header=returnTheTI(afterSplit[0]);

            String docno = returnTheDOCNO(afterSplit[0]);//the first doc
            for (int j = 1; j < afterSplit.length; j++) {

                String textinTheDoc = returnTheText(afterSplit[j]);
                allTheDoc.add(new Pair<>(docno, new Pair<>(textinTheDoc , header)));
                docno = returnTheDOCNO(afterSplit[j]);
                header=returnTheTI(afterSplit[j]);


                /*
                String[] afterSecondSplit = afterSplit[j].split("</TEXT>");
////////////////need to add here the doc num!!!
                allTheDoc.add(new Pair<>("docnum" , afterSecondSplit[0]));
                */
            }

            if (i % 10 == 0) {
                parse.parseAllDoc(allTheDoc);
                allTheDoc = new LinkedList<>();//list with all the text we already read from the folder
            }
            if(i==allTheFolderName.length-1)
            {
                parse.parseAllDoc(allTheDoc);
            }

        }
        HashMap<String ,String> staticTableParameters=parse.getStaticTableForAllTheDic();
        //  System.out.println(staticTableParameters.size());//how many doc i have
        FileOutputStream outputStream=new FileOutputStream(pathForParse+"\\staticTable.txt");
        ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(staticTableParameters);
        objectOutputStream.close();
        outputStream.close();
    }

    /**
     * this function get path and return the read the segment file and return it by linked list
     * @param path
     * @return
     * @throws IOException
     */
    public LinkedList<String> getSegmentFile(String path) throws IOException {
        FileInputStream fileInputStream=new FileInputStream(new File(path));
        ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
        LinkedList<String> dic= null;
        try {
            dic = (LinkedList<String>)objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        fileInputStream.close();
        objectInputStream.close();
        return dic;
    }

    /**
     * this function get string and return the string after split the string "text"
     * @param str
     * @return
     */
    private String returnTheText(String str) {
        String[] afterSplit = str.split("</TEXT>");
        //  System.out.println(z);
        //  z++;
        return afterSplit[0];
    }
    /**
     * this function get string and return the string after split the string "docno"
     * @param str
     * @return
     */
    private String returnTheDOCNO(String str) {
        String[] afterSplit = str.split("<DOCNO>");
        if (afterSplit.length > 1) {
            String[] afterSecondSplit = afterSplit[1].split("</DOCNO>");
            return afterSecondSplit[0].replaceAll("\\s+", "");
        } else
            return "";
    }
    /**
     * this function get string and return the string after split the string "ti"
     * @param str
     * @return
     */
    private String returnTheTI(String str) {
        String[] afterSplit = str.split("<TI>");
        if(afterSplit.length==1)
        {
            return "";
        }
        String[] afterSplit2 = afterSplit[1].split("</TI>");

        return afterSplit2[0];
    }

    {
    /*
    public String temp(File file) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(file));

        String allText = "";
        String st;
        while ((st = br.readLine()) != null) {
            allText = allText + st;
        }
        return allText;
    }
    */
/*
        public LinkedList<String> readJsonFile (String path) throws IOException {
        // Set<String> aa = new LinkedHashSet<>();
        //  File input = new File(path);
        //Document doc = Jsoup.parse(input, "UTF-8");
        //Document doc = Jsoup.parse(new File(path), "US_ASCII");
        //Elements allTheFiles = ((org.jsoup.nodes.Document) doc).getElementsByTag("DOC");
        //  for (Element allTheFile : allTheFiles) {
        //      Elements elements=allTheFile.children();

        //path="C:\\Users\\shachar meretz\\Desktop\\try\\2.json";
        JSONParser parser = new JSONParser();
        LinkedList<String> segmentFile = new LinkedList<>();

        try {
            //for (int i=0; i<28; i++) {
            Object obj = parser.parse(new FileReader(path));//"C:\\Users\\shachar meretz\\Desktop\\try\\"+i+".json"));
            JSONObject jsonObject = (JSONObject) obj;
            Set<String> keys = jsonObject.keySet();
            //  segmentFile=new LinkedList<>();
            for (String key : keys) {
                String str = (String) jsonObject.get(key);
                segmentFile.add(key + " " + str);
            }

        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return segmentFile;
    }

*/
    }

}