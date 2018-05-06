package dawars.billingapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    ListView items_lv;
    ArrayList<String> al,rl;
    ArrayList<Float> al2,rl2,gross,pure;
    BillingDatabaseHelper databaseHelper;
    SQLiteDatabase database;
    RecyclerView items_rv;
    Cursor c;
    float f,totalamt;
    float grcv,grate,crcv;
    TextView grossview,pureview,amt_p,gldrcvpure,amt_tbp;
    EditText bhav,gldrcv,gldrate,cashrcv,cusname,cusphn;
    ArrayAdapter<String> ad;
    float sum = 0,sum2=0;
    int bhv=0;
    View view;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        items_lv=findViewById(R.id.items_list);
        items_rv=findViewById(R.id.items_rv);
        grossview=findViewById(R.id.gross_wt);
        gldrcvpure=findViewById(R.id.gold_reciev_pure);
        amt_tbp=findViewById(R.id.to_be_paid);
        pureview=findViewById(R.id.pure_wt);
        amt_p=findViewById(R.id.amt_payble);
        bhav=findViewById(R.id.bhav);
        gldrcv=findViewById(R.id.gold_reciev);
        cashrcv=findViewById(R.id.cash_reciev);
        gldrate=findViewById(R.id.gold_rate);


        cusname=findViewById(R.id.cus_name);
        cusphn=findViewById(R.id.cus_phn);
        gldrcv.setText("0");
        f=0f;
        al=new ArrayList<>();
        rl=new ArrayList<>();
        al2= new ArrayList<>();
        rl2=new ArrayList<>();
        gross=new ArrayList<>();
        pure=new ArrayList<>();

        


        databaseHelper=new BillingDatabaseHelper(this,"billing",null,4);
        database=databaseHelper.getReadableDatabase();

        al.add("Add new Item");
        al2.add(0f);


        c=database.query("Items",
                new String[] {"item_name","item_rate"},null,null,null,null,null);

        if(c.moveToFirst()){
            do {
                al.add(c.getString(0));
                al2.add(c.getFloat(1));
            }while (c.moveToNext());
        }

        c=database.query("Sales",new String[]{"bhav"},null,null,null,null,null);
        c.moveToFirst();
        Integer b=c.getInt(0);
        bhav.setText(String.valueOf(b));

        ad=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,al);
        items_lv.setAdapter(ad);


        items_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    AlertDialog.Builder ab=new AlertDialog.Builder(MainActivity.this);

                    LinearLayout layout = new LinearLayout(MainActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);

// Add a TextView here for the "Title" label, as noted in the comments
                    final EditText titleBox = new EditText(MainActivity.this);
                    titleBox.setHint("Enter new Item Name");
                    layout.addView(titleBox); // Notice this is an add method

// Add another TextView here for the "Description" label
                    final EditText descriptionBox = new EditText(MainActivity.this);
                    descriptionBox.setHint("Enter Defauld rate");
                    descriptionBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                    layout.addView(descriptionBox); // Another add method

                    ab.setView(layout);

                    ab.setPositiveButton("Create Item", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String s1= titleBox.getText().toString();
                            String temp=descriptionBox.getText().toString();
                            float s2=Float.parseFloat(temp);
                            if(!s1.isEmpty() && !temp.isEmpty()){
                                ContentValues cv=new ContentValues();

                                cv.put("item_name",s1);
                                cv.put("item_rate",s2);
                                database.insert("Items",null,cv);
                                al.add(s1);
                                al2.add(s2);
                                ad.notifyDataSetChanged();
                            }

                            dialogInterface.dismiss();
                        }

                    });

                    ab.show();
                }

                else
                {
                rl.add(al.get(i));
                rl2.add(al2.get(i));

                gross.add(0f);
                pure.add(0f);

                items_rv.setAdapter(new ItemsAdapter());

                }

            }
        });


        items_rv.setAdapter(new  ItemsAdapter());
        items_rv.setLayoutManager(new LinearLayoutManager(this));


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        super.onStart();
        CustomOnclick customOnclick=new CustomOnclick();

        bhav.setShowSoftInputOnFocus(false);
        gldrate.setShowSoftInputOnFocus(false);
        gldrcv.setShowSoftInputOnFocus(false);
        cashrcv.setShowSoftInputOnFocus(false);

        bhav.setOnFocusChangeListener(customOnclick);
        gldrcv.setOnFocusChangeListener(customOnclick);
        gldrate.setOnFocusChangeListener(customOnclick);
        cashrcv.setOnFocusChangeListener(customOnclick);

        findViewById(R.id.num_key_0).setOnClickListener(customOnclick);
        findViewById(R.id.num_key_1).setOnClickListener(customOnclick);
        findViewById(R.id.num_key_2).setOnClickListener(customOnclick);
        findViewById(R.id.num_key_3).setOnClickListener(customOnclick);
        findViewById(R.id.num_key_4).setOnClickListener(customOnclick);
        findViewById(R.id.num_key_5).setOnClickListener(customOnclick);
        findViewById(R.id.num_key_6).setOnClickListener(customOnclick);
        findViewById(R.id.num_key_7).setOnClickListener(customOnclick);
        findViewById(R.id.num_key_8).setOnClickListener(customOnclick);
        findViewById(R.id.num_key_9).setOnClickListener(customOnclick);
        findViewById(R.id.num_key_dot).setOnClickListener(customOnclick);
        findViewById(R.id.num_key_X).setOnClickListener(customOnclick);

    }

    public class ItemsAdapter extends RecyclerView.Adapter<RecyclerViewAdapter> {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public RecyclerViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {

            View view=getLayoutInflater().inflate(R.layout.item_card,parent,false);
            RecyclerViewAdapter vh = new RecyclerViewAdapter(view, new MyCustomEditTextListener(view));
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter holder, int position) {
            holder.textListener.updatePosition(holder.getAdapterPosition());
            holder.fillRateAndName(rl.get(holder.getAdapterPosition()),rl2.get(holder.getAdapterPosition()),
                    holder.getAdapterPosition());


        }

        @Override
        public int getItemCount() {
            return rl.size();
        }
    }

    public  class RecyclerViewAdapter extends RecyclerView.ViewHolder
    {
        View mview;
        MyCustomEditTextListener textListener;
        EditText item_rate,item_weight;
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private RecyclerViewAdapter(View itemView, MyCustomEditTextListener textListener) {
            super(itemView);
            mview=itemView;
            this.textListener=textListener;
            item_weight= itemView.findViewById(R.id.i_wt_id);
            item_rate=itemView.findViewById(R.id.item_rate);


            item_weight.setShowSoftInputOnFocus(false);
           item_weight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
               @Override
               public void onFocusChange(View view, boolean b) {
                   editText=item_weight;
               }
           });

            item_rate.setShowSoftInputOnFocus(false);
            item_rate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    editText=item_rate;
                }
            });

            item_weight.addTextChangedListener(textListener);
            item_rate.addTextChangedListener(textListener);

        }

        private void fillRateAndName(String name, final float r, final int pos){
            TextView tv=mview.findViewById(R.id.item_name);
            item_rate.setText(String.valueOf(r));
            tv.setText(name);


            final TextView item_pure= mview.findViewById(R.id.i_pg_id);

          //  item_weight.setText(String.valueOf(gross.get(pos)));
            try {

                if(gross.get(pos)!=0.0f)
                item_weight.setText(String.valueOf(gross.get(pos)));
                item_pure.setText(String.valueOf(pure.get(pos)));

            }
            catch (IndexOutOfBoundsException e){

            }


            Button cancle=mview.findViewById(R.id.btn_cancle);
            cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    gross.remove(pos);
                    pure.remove(pos);

                    rl.remove(pos);
                    rl2.remove(pos);

                    items_rv.setAdapter(new ItemsAdapter());

                    updateGrossAndPure();
                }
            });
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        c.close();
        database.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        Intent i=new Intent(MainActivity.this,SettingsList.class);
        switch (id){
            case R.id.settings:
            startActivity(i);
            break;
            case R.id.refresh:

                finish();
                startActivity(getIntent());
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int position;
        private View mview;
        EditText item_weight;
        TextView item_pure;
        EditText item_rate;
        public void updatePosition(int position) {
            this.position = position;
        }
        MyCustomEditTextListener(View view)
        {
            mview=view;
            item_weight= mview.findViewById(R.id.i_wt_id);
          item_pure = mview.findViewById(R.id.i_pg_id);
           item_rate  =mview.findViewById(R.id.item_rate);

        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
           // mDataset[position] = charSequence.toString();


            try {
                float f1= Float.parseFloat(item_weight.getText().toString());
                float f2=Float.parseFloat(item_rate.getText().toString());

                rl2.set(position,f2);
                gross.set(position,f1);
                f1=(f1*f2)/100;
                pure.set(position,f1);

                item_pure.setText(String.valueOf(f1));


                updateGrossAndPure();



            }
            catch (NumberFormatException e){

                item_pure.setText(String.valueOf(0.0));
            }
            catch (NullPointerException e){

            }
            catch (IndexOutOfBoundsException e){

            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op

        }


    }


    public void doCalculation(View view) {
        float f1;
        String temp;

        try {

            updateGrossAndPure();


            temp=cashrcv.getText().toString();
            if(!temp.isEmpty())
                crcv=Float.parseFloat(temp);
            else
                crcv=0f;

            temp=gldrcv.getText().toString();
            if(!temp.isEmpty())
                grcv = Float.parseFloat(temp);
            else
                grcv=0f;

            grate = Float.parseFloat(gldrate.getText().toString());

            f1 = (grcv * grate) / 100;
            gldrcvpure.setText(String.valueOf(f1));

            temp=bhav.getText().toString();

            if(!temp.isEmpty())
            bhv = Integer.parseInt(temp);
            else
            bhv=0;

            totalamt=sum2 *bhv;
            amt_p.setText(String.valueOf(totalamt));


            f=(sum2-f1)*bhv - crcv;

            amt_tbp.setText(String.valueOf(f));

        }
        catch (NumberFormatException e){

        }

    }


    public void updateGrossAndPure(){
        sum=0;
        for( Float d : gross)
            sum += d;
        grossview.setText(String.valueOf(sum));
        sum2 = 0;
        for( Float d : pure)
            sum2 += d;

        pureview.setText(String.valueOf(sum2));
    }


    public void goToPreview(View view) {


        Intent intent=new Intent(this,PreviewActivity.class);
        Bundle bundle=new Bundle();
        ArrayList<String> names;
        ArrayList<String> igross,ipure,irates;
        String tap,tbp,glrcv,glrate,glpure,ttlgross,ttlpure;
        String Bhav;
        igross=new ArrayList<>();
        ipure=new ArrayList<>();
        irates=new ArrayList<>();
        String date= Calendar.getInstance().getTime().toString();

        int index=Integer.parseInt(date.substring(30));
        index++;
        date=date.split(String.valueOf("G"),9)[0];


        date=date.concat(String.valueOf(index));
        names=rl;

        Cursor c=database.query("Pointer",new String[]{"bill_pointer"},null,null,null,null,null);
        c.moveToFirst();

        index=c.getInt(0);
        if(index==5)
            bundle.putString("billno",String.format("%03d",(1)));
        else
            bundle.putString("billno",String.format("%03d",(index+1)));


        for(int i=0;i<gross.size();i++)
            igross.add(String.valueOf(gross.get(i)));

        for(int i=0;i<pure.size();i++)
            ipure.add(String.valueOf(pure.get(i)));

        for(int i=0;i<rl2.size();i++)
            irates.add(String.valueOf(rl2.get(i)));




        ttlgross=cusname.getText().toString();
        bundle.putString("cusname",ttlgross);
        ttlgross=cusphn.getText().toString();
        bundle.putString("cusphn",ttlgross);

        
        Bhav=String.valueOf(bhv);
        tap=String.valueOf(totalamt);
        tbp=String.valueOf(f);
        glrcv=String.valueOf(grcv);
        glrate=String.valueOf(grate);
        glpure=gldrcvpure.getText().toString();
        ttlgross=String.valueOf(sum);
        ttlpure=String.valueOf(sum2);





        bundle.putStringArrayList("names",names);
        bundle.putStringArrayList("igross",igross);
        bundle.putStringArrayList("ipure",ipure);
        bundle.putStringArrayList("irates",irates);
        bundle.putString("bhav",Bhav);
        bundle.putString("tap",tap);
        bundle.putString("tbp",tbp);
        bundle.putString("glrcv",glrcv);
        bundle.putString("glrate",glrate);
        bundle.putString("glpure",glpure);
        bundle.putString("ttlgross",ttlgross);
        bundle.putString("ttlpure",ttlpure);
        bundle.putString("date",date);
        bundle.putString("cashrcv",cashrcv.getText().toString());


        intent.putExtra("Bundle",bundle);
        startActivity(intent);
        finish();

    }

    public class CustomOnclick implements View.OnClickListener,View.OnFocusChangeListener
    {

        @Override
        public void onClick(View view) {
            if(editText==null)
                return;
            if (view.getTag() != null && "num_key".equals(view.getTag())) {
                editText.append(((TextView) view).getText());
                return;
            }
            switch (view.getId()) {
                case R.id.num_key_dot: { // handle clear button
                    editText.append(((TextView) view).getText());
                }
                break;
                case R.id.num_key_X: { // handle backspace button
                    // delete one character
                    Editable editable = editText.getText();
                    int charCount = editable.length();
                    if (charCount > 0) {

                        editable.delete(charCount - 1, charCount);
                    }
                }
                break;
            }
        }


        @Override
        public void onFocusChange(View view, boolean b) {
            editText=(EditText) view;
        }
    }

}
