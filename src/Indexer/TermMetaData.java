public class TermMetaData {
    private int frequency;
    private boolean isHeadLine;

    public TermMetaData(){
        frequency=0;
        isHeadLine=false;
    }
    public TermMetaData(int frequency, boolean headLine){
        this.frequency=frequency;
        isHeadLine=headLine;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isHeadLine() {
        return isHeadLine;
    }

    public String toString(){
        return getFrequency()+":"+isHeadLine;
    }

    public void setHeadLine(boolean headLine) {
        isHeadLine = headLine;
    }
}