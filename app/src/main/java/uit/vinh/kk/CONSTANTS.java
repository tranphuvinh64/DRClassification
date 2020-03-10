package uit.vinh.kk;

import android.os.Environment;

import java.io.File;

public final class CONSTANTS {

    // constants for DatabaseHelper
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "patient.db";
    public static final String TABLE_NAME = "FORM";
    public static final String COLUMN_0_ID = "ID";
    public static final String COLUMN_1_TODAY = "TODAY";
    public static final String COLUMN_2_NAME = "PATIENTNAME";
    public static final String COLUMN_3_DOB = "DATEOFBIRTH";
    public static final String COLUMN_4_SEX = "SEX";
    public static final String COLUMN_5_PERSONALID = "PERSONALID";
    public static final String COLUMN_6_RESULT = "RESULT";
    public static final String COLUMN_7_SYSTOLIC = "BLOODPRESSURE_SYSTOLIC";
    public static final String COLUMN_8_DIASTOLIC = "BLOODPRESSURE_DIASTOLIC";
    public static final String COLUMN_9_BLOODSUGAR = "BLOODSUGAR";
    public static final String COLUMN_10_HBA1C = "HBA1C";
    public static final String COLUMN_11_LDL = "CHOLESTEROL_LDL";
    public static final String COLUMN_12_HDL = "CHOLESTEROL_HDL";
    public static final String COLUMN_13_MEDICALHISTORY = "MEDICALHISTORY";
    public static final String COLUMN_14_NOTE = "NOTE";
    public static final String COLUMN_15_PATHORIGINALIMAGE = "PATHORIGINALIMAGE";
    public static final String COLUMN_16_PATHCONTRASTENHANCE = "PATHCONTRASTENHANCEIMAGE";
    public static final String COLUMN_17_ISDELETE = "isDelete";

    // constants for SaveActivity
    public static final String SAVE_AS_MODE_EDIT = "OLD";
    public static final String SAVE_AS_MODE_NEW = "NEW";

    // constants for MainActivity
    public static final int MAX_LENGTH_THIRD_STRING = 35;
    public static final int MAX_LENGTH_FIRST_STRING = 35;


    public static final int COLUMN_ID_INDEX = 0;
    public static final int COLUMN_TODAY_INDEX = 1;
    public static final int COLUMN_PATIENT_NAME_INDEX = 2;
    public static final int COLUMN_DOB_INDEX = 3;
    public static final int COLUMN_SEX_INDEX = 4;
    public static final int COLUMN_PERSONALID_INDEX = 5;
    public static final int COLUMN_RESULT_INDEX = 6;
    public static final int COLUMN_SYSTOLIC_INDEX = 7;
    public static final int COLUMN_DIASTOLIC_INDEX = 8;
    public static final int COLUMN_BLOODSUGAR_INDEX = 9;
    public static final int COLUMN_HBA1C_INDEX = 10;
    public static final int COLUMN_CHOLESTEROL_LDL_INDEX = 11;
    public static final int COLUMN_CHOLESTEROL_HDL_INDEX = 12;
    public static final int COLUMN_MEDICAL_HISTORY_INDEX = 13;
    public static final int COLUMN_NOTE_INDEX = 14;
    public static final int COLUMN_PATH_ORIGINAL_IMAGE_INDEX = 15;
    public static final int COLUMN_PATH_CONTRAST_ENHANCE_IMAGE_INDEX = 16;
    public static final int COLUMN_ISDELETE = 17;

    public static  final String STORE_IMG_FOLDER_NAME = "DRClassification";

    //
    public static final String FOLDER_PATH_STORE_IMG = Environment.getExternalStorageDirectory()+ File.separator + STORE_IMG_FOLDER_NAME;

    public static final int MAX_HEIGHT = 4500;
    public static final int MAX_WIDTH = 6300;

    public static final String DateFormat = "dd/MM/yyyy";

    // Permission
    public static final int REQUEST_PERMISSION = 300;
    public static final int REQUEST_IMAGE = 100;


    public static final float THRESHOLD = (float)0.5 ;

}
