package dawars.billingapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity {


    private BillingDatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private Bundle bundle;



    ArrayList<String> names;
    ArrayList<String> igross,ipure,irates;
    String tap,tbp,glrcv,glrate,glpure,ttlgross,ttlpure;
    String Bhav;
    RecyclerView recyclerView;
    TextView billno,dateiew,cusname,cusphn;
    TextView b_tap,b_tbp,b_glrcv,b_glrate,b_glpure,b_ttlgross,b_ttlpure,b_bhav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        databaseHelper=new BillingDatabaseHelper(this,"billing",null,4);
        database=databaseHelper.getReadableDatabase();
        bundle=getIntent().getBundleExtra("Bundle");

        billno=findViewById(R.id.bill_no);
        dateiew=findViewById(R.id.bill_date);

        billno.setText(bundle.getString("billno"));
        dateiew.setText(bundle.getString("date"));

        b_tap=findViewById(R.id.bill_amt_payble);
        b_tbp=findViewById(R.id.bill_to_be_paid);
        b_glrcv=findViewById(R.id.bill_gold_reciev);
        b_glrate=findViewById(R.id.bill_gold_rate);
        b_glpure=findViewById(R.id.bill_gold_reciev_pure);
        b_ttlgross=findViewById(R.id.bill_gross_wt);
        b_ttlpure=findViewById(R.id.bill_pure_wt);
        b_bhav=findViewById(R.id.bill_bhav);

        cusname=findViewById(R.id.bill_cus_name);
        cusphn=findViewById(R.id.bill_cus_phn);

        cusname.setText(bundle.getString("cusname"));
        cusphn.setText(bundle.getString("cusphn"));


        names=bundle.getStringArrayList("names");
        igross=bundle.getStringArrayList("igross");
        ipure=bundle.getStringArrayList("ipure");
        irates=bundle.getStringArrayList("irates");


        tap=bundle.getString("tap");
        tbp=bundle.getString("tbp");
        glrcv=bundle.getString("glrcv");
        glrate=bundle.getString("glrate");
        glpure=bundle.getString("glpure");
        ttlgross=bundle.getString("ttlgross");
        ttlpure=bundle.getString("ttlpure");
        Bhav=bundle.getString("bhav");


       recyclerView=findViewById(R.id.bill_rv);
    }


    @Override
    protected void onStart() {
        super.onStart();
                b_tap.setText(tap);
                b_tbp.setText(tbp);
                b_glrcv.setText(glrcv);
                b_glrate.setText(glrate);
                b_glpure.setText(glpure);
                b_ttlgross.setText(ttlgross);
                b_ttlpure.setText(ttlpure);
                b_bhav.setText(Bhav);



                recyclerView.setAdapter(new RecyclerView.Adapter<RecyclerViewHolder>() {
                    @Override
                    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View v=getLayoutInflater().inflate(R.layout.bill_item_card,parent,false);
                        return new RecyclerViewHolder(v);
                    }

                    @Override
                    public void onBindViewHolder(RecyclerViewHolder holder, int i) {
                        holder.fillItem(names.get(i),irates.get(i),igross.get(i),ipure.get(i));

                    }

                    @Override
                    public int getItemCount() {
                        return names.size();
                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(PreviewActivity.this));

    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
            TextView t1,t2,t3,t4;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
          t1=itemView.findViewById(R.id.bill_item_name);
          t2=itemView.findViewById(R.id.bill_item_rate);
          t3=itemView.findViewById(R.id.bill_item_weight);
          t4=itemView.findViewById(R.id.bill_item_pure);

        }
        public void fillItem(String name,String rate,String weight,String pure){
            t1.setText(name);
            t2.setText(rate);
            t3.setText(weight);
            t4.setText(pure);
        }
    }


    public void doPrint(View view) {

        Cursor c=database.query("Sales",new String[]{"tcash,tgold"},null,null,null,null,null);
        ContentValues cv=new ContentValues();
        c.moveToFirst();
        int tcash;
        try {
            tcash=Integer.parseInt(bundle.getString("cashrcv"));
        }
        catch (NumberFormatException e){
            tcash=0;
        }

        float tgold=Float.parseFloat(glrcv);



        tcash=tcash+c.getInt(0);
        tgold=tgold+c.getFloat(1);

        cv.put("tcash",tcash);
        cv.put("tgold",tgold);
        cv.put("bhav",Integer.parseInt(Bhav));

        database.update("Sales",cv,null,null);


        databaseHelper.insertBill(database,bundle);
        Intent startMain = new Intent(this,MainActivity.class);
        finish();
        startActivity(startMain);
    }
}
