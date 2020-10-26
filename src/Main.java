import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;


public class Main extends Application {
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("My Application!");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("Gui.fxml").openStream());//main stage
        Scene scene = new Scene(root, 850, 600);
        scene.getStylesheets().add(getClass().getResource("CSS.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main (String[] args) throws IOException, ClassNotFoundException {
           launch(args);

        String pathForPosting="C:\\Users\\meretz\\IdeaProjects\\postingFile";
        String pathForCorpus="C:\\Users\\meretz\\IdeaProjects\\peleg_shahar\\Info_Retrieval\\corpusForTest";
        String pathForQuery="C:\\Users\\meretz\\IdeaProjects\\peleg_shahar\\Info_Retrieval\\queries.txt";

/*
        System.out.println("start");
        ReadFile readFile=new ReadFile(pathForCorpus);
        readFile.readFromFile(false,pathForPosting);
        Indexer indexer=new Indexer(pathForPosting , false);
        indexer.onIndexer();
        System.out.println("end");

*/


        // LinkedList<LinkedList<String>> temp=managerQuery.startMakeAnswerWithQueryFile( "C:\\Users\\shachar meretz\\Desktop\\GIT\\peleg_shahar\\Info_Retrieval\\queries.txt" , "C:\\Users\\shachar meretz\\Desktop\\try");


/*
        System.out.println("start");
        File file= new File(pathForCorpus+"\\entity.txt");
        file.delete();
        Indexer indexer=new Indexer(pathForPosting ,false);
        indexer.reloadDictionary();
        indexer.reloadDocTable();
        TreeMap<String, String> dic=indexer.getTermDocFreqDic();
        HashMap<String,String> dTable=indexer.getDocTable();
        ReadFile readFile=new ReadFile(pathForQuery);
        String pathForStopWords=pathForPosting+"\\stopWords.txt";
        readFile.pathForStopWords=pathForStopWords;
        readFile.readStopWords();
        System.out.println("AFTER RELOAD");
        ManagerQuery managerQuery=new ManagerQuery(false , false , dic , dTable , readFile);
        managerQuery.startMakeAnswerWithQueryFile( pathForQuery , pathForPosting,indexer);
        System.out.println("end");
*/
       /*
        System.out.println("start");

        HashMap<String , HashMap<String , Pair<Integer , Boolean>>> infoAboutTerm=new HashMap<String , HashMap<String , Pair<Integer , Boolean>>> ();
        HashMap<String , Pair<Integer , Boolean>> docno=new  HashMap<String , Pair<Integer , Boolean>>();
        Pair<Integer , Boolean> howMany1=new Pair<Integer , Boolean>(2,false);
        Pair<Integer , Boolean> howMany2=new Pair<Integer , Boolean>(3,false);
        Pair<Integer , Boolean> howMany3=new Pair<Integer , Boolean>(4,false);
        docno.put("LA1" , howMany1);
        docno.put("LA2" , howMany2);
        docno.put("LA3" , howMany3);
        infoAboutTerm.put("hello"  ,docno);


        HashMap<String , Pair<Integer , Boolean>> docno2=new  HashMap<String , Pair<Integer , Boolean>>();
        Pair<Integer , Boolean> howMany4=new Pair<Integer , Boolean>(2,false);
        Pair<Integer , Boolean> howMany5=new Pair<Integer , Boolean>(3,false);
        Pair<Integer , Boolean> howMany6=new Pair<Integer , Boolean>(4,false);
        docno2.put("LA1" , howMany4);
        docno2.put("LA2" , howMany5);
        docno2.put("LA3" , howMany6);
        infoAboutTerm.put("shachar"  ,docno2);


        HashMap<String , Integer> infoAboutDoc=new HashMap<String , Integer>();
        infoAboutDoc.put("LA1" , 17);
        infoAboutDoc.put("LA2" , 30);
        infoAboutDoc.put("LA3" , 15);

        Ranker ranker=new Ranker(infoAboutTerm ,infoAboutDoc , 7 );
        LinkedList<String> afterRank=ranker.calculateRank();
        System.out.println( afterRank.toString());
        System.out.println("end");

*/
    }
}