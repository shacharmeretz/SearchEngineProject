import java.io.*;


public class WriteFile {


    /**
     * this function get string and path and write the string to the file
     * @param text
     * @param path
     * @throws IOException
     */
    public void write(String text, String path) throws IOException {
        FileWriter file = new FileWriter(path, true);
        file.write(text);
        file.close();
    }


    {
/*
    public void writeLine(String path, String text, String docno) throws IOException {
        FileWriter fw = new FileWriter(path, true);
        BufferedWriter bw = new BufferedWriter(fw);
        if (docno.equals("")) {
            bw.write(text);
        } else {
            bw.write(text + " " + docno);
        }
        bw.newLine();
        bw.close();

    }

    public void writeFun(JSONObject temp, String path) throws IOException {//String path , String text ,  String docno) throws IOException {

        FileOutputStream file=new FileOutputStream(path);
        file.write(text.getBytes());
        file.close();

        FileChannel rwChannel = new RandomAccessFile(path, "rw").getChannel();
        ByteBuffer wrBuf = rwChannel.map(FileChannel.MapMode.READ_WRITE, 0, text.length());

        wrBuf.put(text.getBytes());

        rwChannel.close();

        //try with .txt and with .json
        //JSONObject jsonObject = new JSONObject();
        // jsonObject.put(text, docno);

        Files.write(Paths.get(path), temp.toJSONString().getBytes());


        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(entry.getKey()+"|"+entry.getValue()+"\n");
        writer.close();


        //   FileWriter file = new FileWriter(path+".json", true);
        //  file.write(jsonObject.toJSONString());//toJSONString());
        // file.close();
    }


//JSONObject obj = new JSONObject();
//obj.put(text , docno);
            FileWriter file = new FileWriter(path , true);
           // for (Map.Entry<String, String> entry : termSoFar.entrySet()){
            //    obj.put(entry.getKey(), entry.getValue());
           // }
            file.write(text);
            file.flush();
            file.close();

        //bw.close();

       // JSONObject obj = new JSONObject();
    //    obj.put(text, docno);
        //try (FileWriter file = new FileWriter(path , true)) {
          //  BufferedWriter bw = new BufferedWriter(file);
           // bw.newLine();
         //   bw.write(obj.toJSONString());
        //    bw.close();
       // } catch (IOException e) {
      //      e.printStackTrace();
        }


      //  System.out.print(obj);
        //Country countryObj = new Country();
        //countryObj.name = "India";
        //countryObj.population = 1000000;
/*
        List<String> listOfStates = new ArrayList<String>();
        listOfStates.add("Madhya Pradesh");
        listOfStates.add("Maharastra");
        listOfStates.add("Rajasthan");

      //  countryObj.states = listOfStates ;
     //   ObjectMapper mapper = new ObjectMapper();

        try {

            // Writing to a file
            mapper.writeValue(new File("c:\\country.json"), "hwllo" );

        } catch (IOException e) {
            e.printStackTrace();
        }

*/

//}

   /* public void writeLineByLine(LinkedList<String> list , String path) throws IOException {
        for(int i=0; i<list.size(); i++)
        {
            writeLine(path , list.get(i));
        }
    }*/

    }
}