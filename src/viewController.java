import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class viewController {

    @FXML
    public javafx.scene.control.CheckBox stemming;
    public javafx.scene.control.TextField pathToCorpus;
    public javafx.scene.control.TextField pathToDirectory;
    public javafx.scene.control.TextField pathToquery;
    public javafx.scene.control.Button btn_delete;
    public javafx.scene.control.Button btn_searchPathToCorpus;
    public javafx.scene.control.Button btn_searchPathToPosting;
    public javafx.scene.control.Button btn_showTheDic;
    public javafx.scene.control.Button btn_toTheComputer;
    public javafx.scene.control.Button startMakeDictionary;
    public javafx.scene.control.Button btn_startMakeName;
    public javafx.scene.control.Button btn_startQuery;
    public javafx.scene.control.TextField query;
    public javafx.scene.control.Button btn_startQuerys;
    public javafx.scene.control.CheckBox ifsemanticWithInternet;
    public javafx.scene.control.CheckBox ifsemanticWithNoInternet;
    public javafx.scene.control.Button btn_searchPathToQueries;
    public ListView listViewForDoc;
    public ListView listViewForQuery;
    public javafx.scene.control.ChoiceBox choiceBoxForQuery;
    public javafx.scene.control.ChoiceBox choiceBoxForDoc;

    private static Pattern equalPat = Pattern.compile("=");
    private boolean ifStemming=false;
    private boolean semanticWithNoInternet=false;
    private boolean semanticWithInternet =false;

    String queryNum;
    HashMap<String , HashMap<String ,LinkedList<String> >> forAllQueryAllTheDoc;
    Indexer indexer;
    Searcher searcher;

    public void startMakeDic() throws IOException, ClassNotFoundException {
        if (pathToCorpus.getText().isEmpty() || pathToDirectory.getText().isEmpty() || pathToCorpus == null || pathToDirectory == null) {
            showAlert("please enter the corpus and directory paths");
        }
            File file=new File(pathToCorpus.getText());
            File file2=new File(pathToDirectory.getText());
            if(file.exists()==false || file2.exists()==false)
            {
                showAlert("path not found , enter path again");
            }

        else {
            long startTime = System.nanoTime() / 1000000000;
            Path pathWithStemtemp = Paths.get(pathToDirectory.getText() + "\\stem");
            Files.createDirectories(pathWithStemtemp);
             String pathWithStem = pathWithStemtemp.toString();

            Path pathWithNoStemtemp = Paths.get(pathToDirectory.getText() + "\\nostem");
            Files.createDirectories(pathWithNoStemtemp);
            String pathWithNoStem = pathWithNoStemtemp.toString();

            if (ifStemming) {
                ReadFile x = new ReadFile(pathToCorpus.getText());
                x.readFromFile(ifStemming, pathWithStem.toString());
                indexer = new Indexer(pathWithStem.toString(), ifStemming);
                indexer.onIndexer();
            } else {
                ReadFile x = new ReadFile(pathToCorpus.getText());
                x.readFromFile(ifStemming, pathWithNoStem.toString());
                indexer = new Indexer(pathWithNoStem.toString(), ifStemming);
                indexer.onIndexer();
            }
            long endTime = System.nanoTime() / 1000000000;
            System.out.println("The total running time of the program is " + (endTime - startTime) + " seconds");
        }
    }

    public void ifStemming(Event event) {
        if(ifStemming==true) {
            ifStemming = false;
        }
        else {
            ifStemming = true;
        }
    }

    public void deleteAll(Event event) {
        File directoryPathToCheck = new File( pathToDirectory.getText());
        File[] allTheFolderNameToCheck = directoryPathToCheck.listFiles();//all the folders in the folder
        if(allTheFolderNameToCheck.length==0)
        {
            showAlert("the folder is empty");
        }
        Path pathWithStemtemp = Paths.get(pathToDirectory.getText() + "\\stem");
        String pathWithStem = pathWithStemtemp.toString();
        Path pathWithNoStemtemp = Paths.get(pathToDirectory.getText() + "\\nostem");
        String pathWithNoStem = pathWithNoStemtemp.toString();


        File directoryPath = new File( pathWithStem);
        File[] allTheFolderName;
        allTheFolderName = directoryPath.listFiles();//all the folders in the folder
        for (int i = 0; i < allTheFolderName.length; i++) {
            allTheFolderName[i].delete();
        }
        directoryPath = new File(pathWithNoStem);
        allTheFolderName = directoryPath.listFiles();//all the folders in the folder
        for (int i = 0; i < allTheFolderName.length; i++) {
            allTheFolderName[i].delete();
        }
        File index = new File(pathWithNoStem);
        index.delete();
        File index2 = new File(pathWithStem);
        index2.delete();
    }

    public void loadTheDic() throws IOException, ClassNotFoundException {
        File file=new File(pathToDirectory.getText());
        if(file.exists()==false)
        {
            showAlert("you don't have dictionary to reload");
        }
        else {
            if (indexer == null) {
                indexer = new Indexer(pathToDirectory.getText(), ifStemming);
            }
            indexer.reloadDictionary();
            indexer.reloadEntitiesDocTable();
        }
    }

    public void browseCorpus() {
        String path="";
        DirectoryChooser directoryChooser=new DirectoryChooser();
        Stage loadStage = new Stage();
        File file=directoryChooser.showDialog(loadStage);
        if (file != null) {
            path = file.getAbsolutePath();
            pathToCorpus.setText(path);
        }
    }

    public void browsePostingFile() {
        String path="";
        DirectoryChooser directoryChooser=new DirectoryChooser();
        Stage loadStage = new Stage();
        File file=directoryChooser.showDialog(loadStage);
        if (file != null) {
            path = file.getAbsolutePath();
            pathToDirectory.setText(path);
        }
    }

    public void showTheDic(Event event) throws IOException {
        Path pathWithStemtemp = Paths.get(pathToDirectory.getText() + "\\stem");
        Path pathWithNoStemtemp = Paths.get(pathToDirectory.getText() + "\\nostem");
        String pathWithStem=pathWithStemtemp.toString();
        String pathWithNoStem=pathWithNoStemtemp.toString();
        //Indexer indexer=new Indexer(pathToDirectory.getText() , ifStemming);
        if (ifStemming && (indexer == null && new File(pathWithStem + "\\DictionaryToDisplaystem.txt").exists()==false)) {
            showAlert("there is no dictionary to show\n Please click on load dictionary");
        }
        else if (ifStemming==false && indexer == null && new File(pathWithNoStem + "\\DictionaryToDisplaystem.txt").exists()==false) {
            showAlert("there is no dictionary to show\n Please click on load dictionary");
        }
        else {
            File file;
            if (ifStemming) {
                file = new File(pathWithStem + "\\DictionaryToDisplaystem.txt");
            } else {
                file = new File(pathWithNoStem + "\\DictionaryToDisplay.txt");
            }
            Desktop.getDesktop().open(file);
        }

        /*
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("/DictionaryDisplay.fxml").openStream());
        createNewScene(root);

         */
    }

    public void ifSemanticWithNoInternet(Event event) {
        if(semanticWithNoInternet==true)
            semanticWithNoInternet=false;
        else
            semanticWithNoInternet=true;
    }

    public void semanticWithInternet(Event event) {
        if(semanticWithInternet==true)
            semanticWithInternet=false;
        else
            semanticWithInternet=true;
    }

    public void browseQueries() {
            FileChooser fileChooser = new FileChooser();
            Stage loadStage = new Stage();
            fileChooser.getExtensionFilters();
            File f = fileChooser.showOpenDialog(loadStage);
            if (f != null){
                String path = f.getAbsolutePath();
                pathToquery.setText(path);
            }
        }

    public void startQuerys() throws IOException, ClassNotFoundException {
        if (pathToquery.getText().isEmpty() || pathToDirectory.getText().isEmpty() || pathToquery == null || pathToDirectory == null) {
            showAlert("please enter the query and directory paths");
        }
        File file=new File(pathToquery.getText());
        if(file.exists()==false)
        {
            showAlert("path not found , enter path again");
        }
           else {
            String pathForQuery=pathToquery.getText();
            String pathForPosting=pathToDirectory.getText();
            if(ifStemming==false)
            {
                pathForPosting=pathForPosting+"\\nostem";
            }
            else
            {
                pathForPosting=pathForPosting+"\\stem";
            }

            if (indexer == null) {
                indexer = new Indexer(pathForPosting, ifStemming);
                indexer.reloadDictionary();
                indexer.reloadEntitiesDocTable();
            }
            else
            {
                indexer.setPath(pathForPosting);
            }

            TreeMap<String, String> dic = indexer.getTermDocFreqDic();
            HashMap<String, String> dTable = indexer.getDocTable();

            File file2 = new File(pathForPosting + "\\results.txt");
            file2.delete();
            file2 = new File(pathForPosting + "\\answersToShow.txt");
            file2.delete();

            if (searcher == null) {
                searcher = new Searcher(pathForQuery, pathForPosting ,dic  ,dTable,semanticWithNoInternet , semanticWithInternet , ifStemming );
            }
            else
            {
                searcher.setPathFile(pathForPosting,pathForQuery);
            }
            searcher.manageSearcher();
        }
    }

    public void startQuery() throws IOException, ClassNotFoundException {
        File file=new File(pathToDirectory.getText());
        if (query.getText().isEmpty()){
            showAlert("please enter query");
        }
        else if (file.exists()==false)
        {
            showAlert("path not found , enter path again");
        }
        else {
            String pathForPosting=pathToDirectory.getText();
            String queryforSearch=query.getText();
            if(ifStemming==false)
            {
                pathForPosting=pathForPosting+"\\nostem";
            }
            else
            {
                pathForPosting=pathForPosting+"\\stem";
            }
            File file2 = new File(pathForPosting + "\\results.txt");
            file2.delete();
            file2 = new File(pathForPosting + "\\answersToShow.txt");
            file2.delete();
            if (indexer == null) {
                indexer = new Indexer(pathForPosting, ifStemming);
                indexer.reloadDictionary();
                indexer.reloadEntitiesDocTable();
            }
            else
            {
                indexer.setPath(pathForPosting);
            }
            TreeMap<String, String> dic = indexer.getTermDocFreqDic();
            HashMap<String, String> dTable = indexer.getDocTable();

            if (searcher == null) {
             //   pathForQuery=pathToquery.toString();
                searcher = new Searcher(queryforSearch, true, pathForPosting, dic, dTable, semanticWithNoInternet , semanticWithInternet , ifStemming);
            }
            else
            {
                searcher.setPathFile(true,pathForPosting,queryforSearch);
            }
            searcher.manageSearcher();
        }
    }

    public void showQuery() throws IOException {
        forAllQueryAllTheDoc = new HashMap<>();
        HashMap<String, LinkedList<String>> temp = new HashMap<>();
        File file;
        String pathForPosting=pathToDirectory.getText();
        String pathForQuery=pathToquery.getText();
        if(ifStemming==false)
        {
            pathForPosting=pathForPosting+"\\nostem";
        }
        else
        {
            pathForPosting=pathForPosting+"\\stem";
        }
       // if (ifStemming) {
            file = new File(pathForPosting + "\\entity.txt");
       // } else {
      //      file = new File(pathForPosting + "\\entity.txt");
      //  }
        if (file.exists() == false) {
            showAlert("you must serach for answer before");
        } else {
            ReadFile readFile = new ReadFile(file.getPath());
            String str = readFile.readAllText(file).toString();
            String[] split = str.split("=");
            String[] split2;
            for (int i = 1; i < split.length; i++)//the split is each query
            {
                split2 = split[i].split(";");//the split is each doc
                if (split2.length > 1) {
                    String queryNum = split2[0];
                    String[] split3 = split2[1].split("!");
                    for (int x = 0; x < split3.length - 1; x++) {
                        String[] split4 = split3[x].split(":");
                        String docNum = split4[0];
                        LinkedList<String> Entity = new LinkedList<>();
                        for (int z = 1; z < split4.length; z++) {
                            Entity.add(split4[z]);
                        }


                        temp.put(docNum, Entity);

                    }
                    forAllQueryAllTheDoc.put(queryNum, temp);
                    temp = new HashMap<>();
                }
            }
            LinkedList<String> allTheQueryNum = new LinkedList<>(forAllQueryAllTheDoc.keySet());
            listViewForQuery = new ListView();
            ObservableList<String> list = FXCollections.observableList(allTheQueryNum);
            choiceBoxForQuery.setItems(list);
        }
    }

    public void showDoc() throws IOException {
        File file;
        String pathForPosting=pathToDirectory.getText();
        String pathForQuery=pathToquery.getText();
        if(ifStemming==false)
        {
            pathForPosting=pathForPosting+"\\nostem";
        }
        else
        {
            pathForPosting=pathForPosting+"\\stem";
        }
      //  if (ifStemming) {
            file = new File(pathForPosting + "\\entity.txt");
     //   } else {
     //       file = new File(pathForPosting + "\\entity.txt");
     //   }
        Object doc=choiceBoxForQuery.getValue();
        queryNum=doc.toString();
        HashMap<String ,LinkedList<String>> hashMapForTheQuery=forAllQueryAllTheDoc.get(queryNum);
        /*
        entities=new HashMap<>();
        ReadFile readFile=new ReadFile(file.getPath());
        LinkedList<String> lines=readFile.readLineByLine(file.getPath());
        LinkedList<String> alltheEntityInTheSpecificDoc = new LinkedList<>();

        for (int i=0; i<lines.size(); i++) {
            String[] split =lines.get(i).split(":");
            if(split.length>1) {
                String[] split2 = split[1].split(",");
                for (int j = 0; j < split2.length; j++) {
                    alltheEntityInTheSpecificDoc.add(split2[j]);
                }
            }
            entities.put(split[0] , alltheEntityInTheSpecificDoc);
        }
        */
        LinkedList<String> onlyDocNum=new LinkedList<>(hashMapForTheQuery.keySet());
        listViewForDoc=new ListView();
        ObservableList<String> list= FXCollections.observableList(onlyDocNum);
        choiceBoxForDoc.setItems(list);
       // System.out.println(onlyDocNum.toString());
       // Desktop.getDesktop().open(file);
    }

    public void showEntity()
    {
        Object doc=choiceBoxForDoc.getValue();
        String docNum=doc.toString();
        //System.out.println(docNum);
        HashMap<String ,LinkedList<String>> hashMapForTheQuery=forAllQueryAllTheDoc.get(queryNum);
        LinkedList<String> temp=hashMapForTheQuery.get(docNum);
        String str="";
        for(int i=0; i<temp.size(); i++)
        {
            str+=temp.get(i)+"\n";
        }
        showAlert(str);
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }


}