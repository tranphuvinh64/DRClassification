package uit.vinh.kk;

public class DataModel {
    private String name;
    private String personalID;
    private String idForm;
    private String dateOfBirth;

    public DataModel(String name, String personalID, String idForm, String dateOfBirth) {
        this.name = name;
        this.personalID = personalID;
        this.idForm = idForm;
        this.dateOfBirth = dateOfBirth;
    }

    public DataModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersonalID() {
        return personalID;
    }

    public void setPersonalID(String personalID) {
        this.personalID = personalID;
    }

    public String getIdForm() {
        return idForm;
    }

    public void setIdForm(String idForm) {
        this.idForm = idForm;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
