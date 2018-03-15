package gui.tablemodel;

public class RowData {
    private String name;
    private String comment;

    public RowData(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }

    public void setName(String str) {
        name = str;
    }

    public void setComment(String str) {
        comment = str;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }
}