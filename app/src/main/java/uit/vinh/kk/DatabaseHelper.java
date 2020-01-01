package uit.vinh.kk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    public DatabaseHelper(Context context){
        super(context, CONSTANTS.DATABASE_NAME,null,CONSTANTS.DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }

//    public DatabaseHelper (Context context){
//        super(context,DATABASE_NAME, null, 1);
//    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String column_FormID = "ID INTEGER PRIMARY KEY AUTOINCREMENT,";
        String column_Today = "TODAY CHAR(20),";
        String column_PatientName = "PATIENTNAME TEXT,";
        String column_DOB = "DATEOFBIRTH CHAR(20),";
        String column_Sex = "SEX CHAR(10),";
        String column_PersonalID = "PERSONALID TEXT,";
        String column_Result = "RESULT CHAR(50),";
        String column_Systolic = "BLOODPRESSURE_SYSTOLIC FLOAT,";
        String column_Diastolic = "BLOODPRESSURE_DIASTOLIC FLOAT,";
        String column_BloodSugar = "BLOODSUGAR FLOAT,";
        String column_Hba1c = "HBA1C FLOAT,";
        String column_LDL = "CHOLESTEROL_LDL FLOAT,";
        String column_HDL = "CHOLESTEROL_HDL FLOAT,";
        String column_MedHist = "MEDICALHISTORY TEXT,";
        String column_Note  = "NOTE TEXT,";
        // kiểu dữ liệu chứa 2 ảnh
        String column_OriginalImage = "ORIGINALIMAGE BLOB";
        String temp = "("+column_FormID+column_Today+column_PatientName+column_DOB+column_Sex+column_PersonalID
                +column_Result+column_Systolic+column_Diastolic+column_BloodSugar+column_Hba1c+column_LDL+column_HDL
                +column_MedHist+column_Note+column_OriginalImage+")";
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CONSTANTS.TABLE_NAME + temp);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertNewForm (Form newForm){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONSTANTS.COLUMN_1_TODAY, newForm.getToday());
        contentValues.put(CONSTANTS.COLUMN_2_NAME,newForm.getName());
        contentValues.put(CONSTANTS.COLUMN_3_DOB,newForm.getDateOfBirth());
        contentValues.put(CONSTANTS.COLUMN_4_SEX, newForm.getSex());
        contentValues.put(CONSTANTS.COLUMN_5_PERSONALID, newForm.getPersonalID());
        contentValues.put(CONSTANTS.COLUMN_6_RESULT, newForm.getClassificationResult());
        contentValues.put(CONSTANTS.COLUMN_7_SYSTOLIC, newForm.getBloodPressure_Systolic());
        contentValues.put(CONSTANTS.COLUMN_8_DIASTOLIC,newForm.getBloodPressure_Diastolic());
        contentValues.put(CONSTANTS.COLUMN_9_BLOODSUGAR, newForm.getBloodSugar());
        contentValues.put(CONSTANTS.COLUMN_10_HBA1C, newForm.getHba1c());
        contentValues.put(CONSTANTS.COLUMN_11_LDL, newForm.getCholesterolLDL());
        contentValues.put(CONSTANTS.COLUMN_12_HDL,newForm.getCholesterolHDL());
        contentValues.put(CONSTANTS.COLUMN_13_MEDICALHISTORY,newForm.getMedicalHistory());
        contentValues.put(CONSTANTS.COLUMN_14_NOTE,newForm.getNote());
        contentValues.put(CONSTANTS.COLUMN_15_ORIGINALIMAGE, newForm.getBytearrOriginalImage());

        long result = db.insert(CONSTANTS.TABLE_NAME,null,contentValues);
        if(result == -1){
            return false;
        }
        return true;
    }

    public Cursor loadData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + CONSTANTS.TABLE_NAME,null);
        return res;
    }

    public boolean updateData(String id, Form newForm){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONSTANTS.COLUMN_1_TODAY, newForm.getToday());
        contentValues.put(CONSTANTS.COLUMN_2_NAME,newForm.getName());
        contentValues.put(CONSTANTS.COLUMN_3_DOB,newForm.getDateOfBirth());
        contentValues.put(CONSTANTS.COLUMN_4_SEX, newForm.getSex());
        contentValues.put(CONSTANTS.COLUMN_5_PERSONALID, newForm.getPersonalID());
        contentValues.put(CONSTANTS.COLUMN_6_RESULT, newForm.getClassificationResult());
        contentValues.put(CONSTANTS.COLUMN_7_SYSTOLIC, newForm.getBloodPressure_Systolic());
        contentValues.put(CONSTANTS.COLUMN_8_DIASTOLIC,newForm.getBloodPressure_Diastolic());
        contentValues.put(CONSTANTS.COLUMN_9_BLOODSUGAR, newForm.getBloodSugar());
        contentValues.put(CONSTANTS.COLUMN_10_HBA1C, newForm.getHba1c());
        contentValues.put(CONSTANTS.COLUMN_11_LDL, newForm.getCholesterolLDL());
        contentValues.put(CONSTANTS.COLUMN_12_HDL,newForm.getCholesterolHDL());
        contentValues.put(CONSTANTS.COLUMN_13_MEDICALHISTORY,newForm.getMedicalHistory());
        contentValues.put(CONSTANTS.COLUMN_14_NOTE,newForm.getNote());
        db.update(CONSTANTS.TABLE_NAME,contentValues,"ID = ?",new String[] {id});
        return true;
    }

    private byte[] convertBitmaptoByteArray(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}
