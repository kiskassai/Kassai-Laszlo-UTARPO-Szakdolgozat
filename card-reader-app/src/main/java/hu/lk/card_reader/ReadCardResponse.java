package hu.lk.card_reader;

public class ReadCardResponse {
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Boolean getInClass() {
        return isInClass;
    }

    public void setInClass(Boolean inClass) {
        isInClass = inClass;
    }

    private String studentName;
    private String className;
    private Boolean isInClass;
}
