import java.util.regex.Pattern;

public class Query {

    private String num;
    private String title;
    private String desc;
    private String narr;
    private static Pattern numPat = Pattern.compile("<num>");
    private static Pattern titlePat = Pattern.compile("<title>");
    private static Pattern descPat = Pattern.compile("<desc>");
    private static Pattern narrPat = Pattern.compile("<narr>");
    private static Pattern topEndPat = Pattern.compile("<top/>");
    public Query(String query) {
        String[] afterSplit = numPat.split(query);
        for (int i = 1; i < afterSplit.length; i++) {
            num = titlePat.split(afterSplit[i])[0];
            num=num.substring(8);
        }
        afterSplit = titlePat.split(query);
        for (int i = 1; i < afterSplit.length; i++) {
            title = descPat.split(afterSplit[i])[0];
        }
        afterSplit = descPat.split(query);
        for (int i = 1; i < afterSplit.length; i++) {
            desc = narrPat.split(afterSplit[i])[0];
         //   desc=desc.substring(9,desc.length());//check if it help
        }
        afterSplit = narrPat.split(query);
        for (int i = 1; i < afterSplit.length; i++) {
            narr = topEndPat.split(afterSplit[i])[0];
        }
    }

    public String getNum() {
        return num;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getNarr() {
        return narr;
    }
}