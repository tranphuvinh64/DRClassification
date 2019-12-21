package uit.vinh.kk;

public class DataModel {
    private String name;
    private String personalID;
    private String result;
    private String dateOfBirth;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataModel(String name, String personalID, String result, String dateOfBirth) {
        this.name = name;
        this.personalID = personalID;
        this.result = result;
        this.dateOfBirth = dateOfBirth;
    }

    public String getPersonalID() {
        return personalID;
    }

    public void setPersonalID(String personalID) {
        this.personalID = personalID;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
