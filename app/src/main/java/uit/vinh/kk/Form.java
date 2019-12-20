package uit.vinh.kk;

public class Form {
    private String ID;
    private String Name;

    public Form() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return "Form{" +
                "ID='" + ID + '\'' +
                ", Name='" + Name + '\'' +
                '}';
    }
}