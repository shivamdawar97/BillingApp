package dawars.billingapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class BillsActivity extends AppCompatActivity {

    ListView billList;
    Cursor cursor;
    int index;
    ArrayList<String> s1,s2;
    SQLiteDatabase database;
    TextView pointer_position;
    BillingDatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills);
        s1=new ArrayList<>();
        s2=new ArrayList<>();
        pointer_position=findViewById(R.id.textView);
        databaseHelper=new BillingDatabaseHelper(this,"billing",null,4);
        database=databaseHelper.getReadableDatabase();
        billList=findViewById(R.id.dynamic);

        cursor=database.query("Pointer",new String[]{"bill_pointer"},null,null,null,null,null);
        cursor.moveToFirst();
        index=cursor.getInt(0);
        pointer_position.setText(String.valueOf(index));

        cursor=database.query("Bills", new String[]{"_billno","date","bundle"},null,null,null,null,null);
        pointer_position.append("/"+String.valueOf(cursor.getCount()));


        if(cursor.moveToFirst()){
            do{
                s1.add(String.format("%03d",cursor.getInt(0)));
                s2.add(cursor.getString(1));
            }
            while (cursor.moveToNext());
        }

        /*
        if(cursor.moveToPosition(index))
        {
            try {
                for(int i=index; ; i--){

                    s1.add(String.format("%03d",cursor.getInt(0)));
                    s2.add(cursor.getString(1));

                    if(i==1) {
                        i=cursor.getCount();
                        cursor.moveToPosition(i);
                    }else
                        cursor.moveToPrevious();
                    if(cursor.getPosition()==index)
                        break;

                }

            }
            catch (CursorIndexOutOfBoundsException e){

            }


        }*/





        BillDataAdapter adapter=new BillDataAdapter(this,s1,s2);
        billList.setAdapter(adapter);

    }



    private class BillDataAdapter extends BaseAdapter {

        Context ctx;
        ArrayList<String> data;
        ArrayList<String> date;
        public BillDataAdapter(Context context, ArrayList<String> resource, ArrayList<String> dateRes) {

            ctx=context;
            data=resource;
            date=dateRes;
        }


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public View getView(final int position, View convertView, ViewGroup parent ){

        View view= getLayoutInflater().inflate(R.layout.listviewitem,parent,false);
            TextView header=view.findViewById(R.id.Bill_No);
            header.setText(data.get(position));
            header=view.findViewById(R.id.bill_date);
            header.setText(date.get(position));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cursor.moveToPosition(position);
                    byte[] bundleBytes = cursor.getBlob(2);
                    final Parcel parcel = Parcel.obtain();
                    parcel.unmarshall(bundleBytes, 0, bundleBytes.length);
                    parcel.setDataPosition(0);
                    Bundle bundle = (Bundle) parcel.readBundle();
                    parcel.recycle();

                    Intent intent=new Intent(BillsActivity.this,PreviewActivity.class);
                    intent.putExtra("Bundle",bundle);
                    startActivity(intent);
                }
            });
        return view;
        }


    }
}
