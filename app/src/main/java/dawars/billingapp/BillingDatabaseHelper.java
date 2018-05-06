package dawars.billingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Parcel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shivam97 on 20/3/18.
 */

public class BillingDatabaseHelper extends SQLiteOpenHelper {

    private static int db_version;


    public BillingDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        db_version=version;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        updatemyDatabase(sqLiteDatabase,0,db_version);

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            updatemyDatabase(sqLiteDatabase,i,i1);
    }


    private void updatemyDatabase(SQLiteDatabase sqLiteDatabase, int i, int db_version) {
        ContentValues cv1;
        if(i<1){
            sqLiteDatabase.execSQL("Create Table Items(" +
                    "_id Integer Primary key Autoincrement," +
                    "item_name Text," +
                    "item_rate Float);");
            insertItem(sqLiteDatabase,"Sania Mirza",62f);
            insertItem(sqLiteDatabase,"Bombay",62f);
            insertItem(sqLiteDatabase,"Die/Thappa",63f);


        }

        if(i<2){

            sqLiteDatabase.execSQL("Create Table Bills("+
                    "_billno Integer Primary key Autoincrement,"+
                    "date Text"+
                    ");");


            sqLiteDatabase.execSQL("Create Table Sales("  +
                    "tcash Integer," +
                    "tgold Float"+
                    ");");
            
            cv1=new ContentValues();
            cv1.put("tcash",0);
            cv1.put("tgold",0f);
            sqLiteDatabase.insert("Sales",null,cv1);





        }
        if (i<3){
            sqLiteDatabase.execSQL("Create Table Pointer(" +
                    "bill_pointer Integer);");
            cv1=new ContentValues();
            cv1.put("bill_pointer",0);
            sqLiteDatabase.insert("Pointer",null,cv1);

        }

        if(i<4){
            sqLiteDatabase.execSQL("Alter table Bills " +
                    "add column bundle blob");

            sqLiteDatabase.execSQL("Alter table Sales " +
                    "add column bhav int");
        }


    }

    private static void insertItem(SQLiteDatabase db,String name,float desc){
        ContentValues cv=new ContentValues();
        cv.put("item_name",name);
        cv.put("item_rate",desc);
        db.insert("Items",null,cv);

    }


    public  static void insertBill(SQLiteDatabase db,Bundle bundle){
//        ContentValues cv2=new ContentValues();
//        cv2.put("cname",cname);
//        cv2.put("cphn",cphn);




        Cursor c=db.query("Pointer",new String[]{"bill_pointer"},null,null,null,null,null);
        c.moveToFirst();

        int index;
        index=c.getInt(0);

        String date=bundle.getString("date");

        ContentValues cv1= new ContentValues();
        cv1.put("date",date);

            ByteArrayOutputStream valueStream = new ByteArrayOutputStream();
            try {

                Parcel p = Parcel.obtain();
                bundle.writeToParcel(p, 0);

                valueStream.write(p.marshall());
                cv1.put("bundle", valueStream.toByteArray());

                valueStream.close();
            } catch (IOException e) {

            }

        c=db.query("Bills",new String[]{"_billno"},null,null,null,null,null);

        if(c.moveToPosition(index-1))
        {


           if(index==5) {

               index = 1;
               db.update("Bills",cv1,"_billno = ?",new String[] {String.valueOf(index)});
           }
           else {

               index++;
               if(c.moveToNext())
                   db.update("Bills",cv1,"_billno = ?",new String[] {String.valueOf(index)});

                else
               db.insert("Bills",null,cv1);
           }


        }
        else {

            db.insert("Bills",null,cv1);
            index++;
        }


            cv1.clear();
        cv1.put("bill_pointer",index);

        db.update("Pointer",cv1,null,null);


    }
}
