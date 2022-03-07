package bg.webaudioportal.app.model;

public class ResultOfCompare {
    private String noteName;
    private String resultOfComparison;
    private long startMS;

    public long getStartMS() {
        return startMS;
    }

    public void setStartMS(long startMS) {
        this.startMS = startMS;
    }

    public String getNoteName() {
        return noteName;
    }

    public String getResultOfComparison() {
        return resultOfComparison;
    }

    public void setResultOfComparison(String resultOfComparison) {
        this.resultOfComparison = resultOfComparison;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public ResultOfCompare(String noteName, String resultOfComparison, long startMS) {
        this.noteName = noteName;
        this.resultOfComparison = resultOfComparison;
        this.startMS = startMS;
    }
}
