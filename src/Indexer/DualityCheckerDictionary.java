import java.util.HashMap;

/**
 * this class represent a dictionary and it checks duality
 *
 */
public class DualityCheckerDictionary {
    private HashMap<String, HashMap<String, TermMetaData>> dictionary;
    /**
     * constructor
     */
    public DualityCheckerDictionary() {
        dictionary = new HashMap<>();
    }

    /**
     * when its needed to change a term in the dictionary
     * @param oldKey
     * @param newKey
     */
    private void setKey(String oldKey, String newKey) {
        HashMap<String, TermMetaData> value = dictionary.get(oldKey);
        dictionary.put(newKey, value);
        dictionary.remove(oldKey);
    }

    /**
     * this method checks every show of a term
     * check if showed in capital/small letters
     * if needed merges between hashmaps of shows of a term
     * @param line
     */
    public void checkAndPut(String[] line) {//line format:length=4
        HashMap<String, TermMetaData> termInfo;//term docID tf isHeadLine
        boolean headLine = false;
        if (line[3].equals("true")) {
            headLine = true;
        }
        if (dictionary.containsKey(line[0].toLowerCase())) {//all small letters
            termInfo = dictionary.get(line[0].toLowerCase());
            putTermInfoNoDuality(line, termInfo);
        } else if (dictionary.containsKey(line[0].toUpperCase())) {//all Capital letters
            if (line[0].equals(line[0].toLowerCase())) {
                setKey(line[0].toUpperCase(), line[0].toLowerCase());
                termInfo = dictionary.get(line[0].toLowerCase());
                putTermInfoNoDuality(line, termInfo);
            } else {
                termInfo = dictionary.get(line[0].toUpperCase());
                putTermInfoNoDuality(line, termInfo);
            }
        } else if (containsKeyWith1CapitalLetter(line[0])) {//1 capital letter
            if (line[0].equals(line[0].toLowerCase())) {
                if (line[0].length() == 1) {
                    setKey(switchTo1CapitalLetter(line[0]), line[0].toLowerCase());
                } else {
                    setKey(switchTo1CapitalLetterFewWords(line[0]), line[0].toLowerCase());
                }
                termInfo = dictionary.get(line[0].toLowerCase());
                putTermInfoNoDuality(line, termInfo);
            } else if (line[0].equals(line[0].toUpperCase())) {
                if (line[0].length() == 1) {
                    setKey(switchTo1CapitalLetter(line[0]), line[0].toUpperCase());
                } else {
                    setKey(switchTo1CapitalLetterFewWords(line[0]), line[0].toUpperCase());
                }
                termInfo = dictionary.get(line[0].toUpperCase());
                putTermInfoNoDuality(line, termInfo);
            } else {
                if (line[0].length() == 1) {
                    termInfo = dictionary.get(switchTo1CapitalLetter(line[0]));
                    putTermInfoNoDuality(line, termInfo);
                    setKey(switchTo1CapitalLetter(line[0]), line[0].toUpperCase());
                } else {
                    termInfo = dictionary.get(switchTo1CapitalLetterFewWords(line[0]));
                    putTermInfoNoDuality(line, termInfo);
                    setKey(switchTo1CapitalLetterFewWords(line[0]), line[0].toUpperCase());
                }

            }
        } else {

            termInfo = new HashMap<>();
            TermMetaData mData = new TermMetaData(Integer.parseInt(line[2]), headLine);
            termInfo.put(line[1], mData);
            dictionary.put(line[0], termInfo);
        }
    }

    /**
     * this method switch the first letter of each word to capital letter
     * @param key
     * @return
     */
    private String switchTo1CapitalLetterFewWords(String key) {
        String afterSwitch = "";
        String[] beforeSwitch = key.split(" ");
        for (int i = 0; i < beforeSwitch.length; i++) {
            afterSwitch += switchTo1CapitalLetter(beforeSwitch[i]) + " ";
        }
        afterSwitch = afterSwitch.substring(0, afterSwitch.length() - 1);
        return afterSwitch;
    }

    /**
     * this method switch the first letter of the key to capital letter
     * @param key
     * @return
     */
    private String switchTo1CapitalLetter(String key) {
        String word = key.toLowerCase();
        String letter = word.substring(0, 1);
        letter = letter.toUpperCase();
        String k = word.substring(1);
        return letter + k;
    }

    /**
     * this method checks if the term has shows in the dictionary on 1 capital letter
     * @param key
     * @return
     */
    private boolean containsKeyWith1CapitalLetter(String key) {
        String word;
        if (key.split(" ").length > 1) {
            word = switchTo1CapitalLetterFewWords(key);
        } else {
            word = switchTo1CapitalLetter(key);
        }
        if (dictionary.containsKey(word)) {
            return true;
        }
        return false;
    }

    /**
     * this method sum the shows of a term when duality found
     * @param line
     * @param termInfo
     * @return
     */
    private int sumSameTermSameDoc(String[] line, HashMap<String, TermMetaData> termInfo) {
        Integer sum = Integer.parseInt(line[2]);
        sum = sum + termInfo.get(line[1]).getFrequency();
        return sum;
    }

    /**
     * this method checks that no duality for docNo
     * if needed it sums the frequency of a term
     * @param line
     * @param termInfo
     */
    private void putTermInfoNoDuality(String[] line, HashMap<String, TermMetaData> termInfo) {
        boolean headLine = false;
        if (line[3].equals("true")) {
            headLine = true;
        }
        TermMetaData metaData;
        if (termInfo.containsKey(line[1])) {
            metaData = new TermMetaData(sumSameTermSameDoc(line, termInfo), headLine);
        } else {
            metaData = new TermMetaData(Integer.parseInt(line[2]), headLine);
        }
        termInfo.put(line[1], metaData);
    }

    /**
     * getter
     * @return
     */
    public HashMap<String, HashMap<String, TermMetaData>> getDictionary() {
        return dictionary;
    }
}