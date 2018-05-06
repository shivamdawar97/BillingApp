package dawars.billingapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SettingsList extends AppCompatActivity {

    String list[];
    ListView listView;
    BillingDatabaseHelper databaseHelper;
    SQLiteDatabase database;
    TextView cashview,goldview;
    Cursor c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_list);
        listView=findViewById(R.id.sett_list);
        cashview=findViewById(R.id.ttl_cash);
        goldview=findViewById(R.id.ttl_gold);


        databaseHelper=new BillingDatabaseHelper(this,"billing",null,4);
        database=databaseHelper.getReadableDatabase();
        c=database.query("Sales",new String[]{"tcash,tgold"},null,null,null,null,null);
        c.moveToFirst();
        int tcash=c.getInt(0);
        float tgold=c.getFloat(1);
        cashview.setText(String.valueOf(tcash));
        goldview.setText(String.valueOf(tgold));


        list=new String[3];
        list[0]="Bills";
        list[1]="Add a Customer";
        list[2]="Delete an Product";

        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0)
                {
                    startActivity(new Intent(SettingsList.this,BillsActivity.class));
                }
                if(i==2)
                {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(SettingsList.this);
                    builderSingle.setTitle("Select the Product to be Deleted:");

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SettingsList.this, android.R.layout.select_dialog_singlechoice);
                    c=database.query("Items",
                            new String[] {"item_name"},null,null,null,null,null);
                    if(c.moveToFirst()){
                        do {

                            arrayAdapter.add(c.getString(0));

                        }while (c.moveToNext());
                    }
                    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            database.delete("Items","item_name=?",new String[]{arrayAdapter.getItem(i)});
                        }
                    });
                    builderSingle.show();
                }

            }
        });

    }

    public void doSalesClear(View view) {
        ContentValues cv=new ContentValues();
        cv.put("tcash",0);
        cv.put("tgold",0f);
        database.update("Sales",cv,null,null);
        cashview.setText(String.valueOf(0));
        goldview.setText(String.valueOf(0f));
    }
}
