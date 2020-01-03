package uit.vinh.kk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

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
        String column_0_FormID = CONSTANTS.COLUMN_0_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,";
        String column_1_Today = CONSTANTS.COLUMN_1_TODAY + " CHAR(20),";
        String column_2_PatientName = CONSTANTS.COLUMN_2_NAME + " TEXT,";
        String column_3_DOB = CONSTANTS.COLUMN_3_DOB + " CHAR(20),";
        String column_4_Sex = CONSTANTS.COLUMN_4_SEX + " CHAR(10),";
        String column_5_PersonalID = CONSTANTS.COLUMN_5_PERSONALID + " TEXT,";
        String column_6_Result = CONSTANTS.COLUMN_6_RESULT + " CHAR(50),";
        String column_7_Systolic = CONSTANTS.COLUMN_7_SYSTOLIC + " FLOAT,";
        String column_8_Diastolic = CONSTANTS.COLUMN_8_DIASTOLIC + " FLOAT,";
        String column_9_BloodSugar = CONSTANTS.COLUMN_9_BLOODSUGAR + " FLOAT,";
        String column_10_Hba1c = CONSTANTS.COLUMN_10_HBA1C + " FLOAT,";
        String column_11_LDL = CONSTANTS.COLUMN_11_LDL + " FLOAT,";
        String column_12_HDL = CONSTANTS.COLUMN_12_HDL + " FLOAT,";
        String column_13_MedHist = CONSTANTS.COLUMN_13_MEDICALHISTORY + " TEXT,";
        String column_14_Note  = CONSTANTS.COLUMN_14_NOTE + " TEXT,";
        String column_15_PathOriginalImage = CONSTANTS.COLUMN_15_PATHORIGINALIMAGE + " TEXT,";
        String column_16_PathContrastEnhnaceImage = CONSTANTS.COLUMN_16_PATHCONTRASTENHANCE + " TEXT,";
        String column_17_isDeleted = CONSTANTS.COLUMN_17_ISDELETE + " BOOLEAN DEFAULT FALSE";

        String temp = "("+column_0_FormID+column_1_Today+column_2_PatientName+column_3_DOB+column_4_Sex+column_5_PersonalID
                +column_6_Result+column_7_Systolic+column_8_Diastolic+column_9_BloodSugar+column_10_Hba1c+column_11_LDL+column_12_HDL
                + column_13_MedHist+column_14_Note + column_15_PathOriginalImage + column_16_PathContrastEnhnaceImage + column_17_isDeleted + ")";
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
        contentValues.put(CONSTANTS.COLUMN_15_PATHORIGINALIMAGE,newForm.getPathOriginalImage());
        contentValues.put(CONSTANTS.COLUMN_16_PATHCONTRASTENHANCE,newForm.getPathContrastEnhaceImage());
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

    public boolean deleteForm (String id ){
        String query = "UPDATE " + CONSTANTS.TABLE_NAME + " SET "
                + CONSTANTS.COLUMN_17_ISDELETE + " = ?  WHERE "
                + CONSTANTS.COLUMN_0_ID + " = ?";
        Log.d("debug", "deleteForm: query = "+ query );
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query,new String[]{"TRUE",id});
        return true;
    }
}
