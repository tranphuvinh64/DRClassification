package uit.vinh.kk;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class Form implements Serializable {
    private String ID;
    private String today;
    private String name;
    private String dateOfBirth;
    private String sex;
    private String personalID;
    private String classificationResult;
    private String bloodPressure_Systolic;
    private String bloodPressure_Diastolic;
    private String bloodSugar;
    private String hba1c;
    private String cholesterolHDL;
    private String cholesterolLDL;
    private String medicalHistory;
    private String note;

    private String pathOriginalImage;
    private String pathContrastEnhaceImage;


    public Form() {
        this.ID = this.today = this.name = this.dateOfBirth = this.sex = this.personalID = this.classificationResult= "null";
        this.bloodPressure_Diastolic= this.bloodPressure_Systolic= this.bloodSugar= this.hba1c= this.cholesterolHDL = this.cholesterolLDL ="null" ;
        this.medicalHistory= this.note = "null" ;
        this.pathOriginalImage = this.pathContrastEnhaceImage = "null";
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPersonalID() {
        return personalID;
    }

    public void setPersonalID(String personalID) {
        this.personalID = personalID;
    }

    public String getClassificationResult() {
        return classificationResult;
    }

    public void setClassificationResult(String classificationResult) {
        this.classificationResult = classificationResult;
    }

    public String getBloodPressure_Systolic() {
        return bloodPressure_Systolic;
    }

    public void setBloodPressure_Systolic(String bloodPressure_Systolic) {
        this.bloodPressure_Systolic = bloodPressure_Systolic;
    }

    public String getBloodPressure_Diastolic() {
        return bloodPressure_Diastolic;
    }

    public void setBloodPressure_Diastolic(String bloodPressure_Diastolic) {
        this.bloodPressure_Diastolic = bloodPressure_Diastolic;
    }

    public String getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(String bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public String getHba1c() {
        return hba1c;
    }

    public void setHba1c(String hba1c) {
        this.hba1c = hba1c;
    }


    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCholesterolHDL() {
        return cholesterolHDL;
    }

    public void setCholesterolHDL(String cholesterolHDL) {
        this.cholesterolHDL = cholesterolHDL;
    }

    public String getCholesterolLDL() {
        return cholesterolLDL;
    }

    public void setCholesterolLDL(String cholesterolLDL) {
        this.cholesterolLDL = cholesterolLDL;
    }

    @Override
    public String toString() {
        return "Form{" +
                "ID='" + ID + '\'' +
                ", today='" + today + '\'' +
                ", name='" + name + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", sex='" + sex + '\'' +
                ", personalID='" + personalID + '\'' +
                ", classificationResult='" + classificationResult + '\'' +
                ", bloodPressure_Systolic='" + bloodPressure_Systolic + '\'' +
                ", bloodPressure_Diastolic='" + bloodPressure_Diastolic + '\'' +
                ", bloodSugar='" + bloodSugar + '\'' +
                ", hba1c='" + hba1c + '\'' +
                ", cholesterolHDL='" + cholesterolHDL + '\'' +
                ", cholesterolLDL='" + cholesterolLDL + '\'' +
                ", medicalHistory='" + medicalHistory + '\'' +
                ", note='" + note + '\'' +
                ", pathOriginalImage='" + pathOriginalImage + '\'' +
                ", pathContrastEnhaceImage='" + pathContrastEnhaceImage + '\'' +
                '}';
    }

    public static Comparator<Form> CountDate = new Comparator<Form>() {
        @Override
        public int compare(Form o1, Form o2) {
            String date1 = o1.getToday();
            String date2 = o2.getToday();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            try {
                Date date1_paresd = sdf.parse(date1);
                Date date2_parsed = sdf.parse(date2);
                long deviation = date2_parsed.getTime()/86400000 - date1_paresd.getTime()/86400000;
                return (int) (deviation);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    };



    public String getPathOriginalImage() {
        return pathOriginalImage;
    }

    public void setPathOriginalImage(String pathOriginalImage) {
        this.pathOriginalImage = pathOriginalImage;
    }

    public String getPathContrastEnhaceImage() {
        return pathContrastEnhaceImage;
    }

    public void setPathContrastEnhaceImage(String pathContrastEnhaceImage) {
        this.pathContrastEnhaceImage = pathContrastEnhaceImage;
    }
}