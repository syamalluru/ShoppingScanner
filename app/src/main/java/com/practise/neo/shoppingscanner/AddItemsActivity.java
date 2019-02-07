package com.practise.neo.shoppingscanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.practise.neo.shoppingscanner.Model.ItemModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;

public class AddItemsActivity extends AppCompatActivity {

    private static final String TAG = "sham";
    FloatingActionButton additembtn;
    ListView listView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    EditText productNameEt,barcodeEt,priceEt;
    Button saveBtn;

    RecyclerView recyclerView;
    ItemAdapter recyclerViewAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ItemModel> itemModelArrayListAddItems;
    ArrayList<String> barcodearraylistAddItems;
    private boolean lock=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_items);
        additembtn=findViewById(R.id.add_item_button);
        productNameEt=findViewById(R.id.product_name_et);
        barcodeEt=findViewById(R.id.barcode_et);
        //quantityEt=findViewById(R.id.quantity_et);
        priceEt=findViewById(R.id.price_et);
        saveBtn=findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodearraylistAddItems.add(barcodeEt.getText().toString());
                itemModelArrayListAddItems.add(new ItemModel(productNameEt.getText().toString(),barcodeEt.getText().toString(),null,priceEt.getText().toString()));
                recyclerViewAdapter.notifyDataSetChanged();
                lock=false;
                resetEditTexts();
            }
        });
        SharedPreferences sharedPreferences=getSharedPreferences("listitems",Context.MODE_PRIVATE);
        String dum=sharedPreferences.getString("additemsflag",null);
        Log.d(TAG, "shared flag: "+dum);
        if(dum!=null && dum.equals("set"))
        {
            Log.d(TAG, "calling restoreitemlist: ");
            restoreItemList();
        }
        else
        {
            itemModelArrayListAddItems=new ArrayList<>();
            barcodearraylistAddItems=new ArrayList<>();
        }
        
        
        
        recyclerView=findViewById(R.id.recyclerview_add_items);
        recyclerViewAdapter=new ItemAdapter(itemModelArrayListAddItems);
        layoutManager=new LinearLayoutManager(AddItemsActivity.this);


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onDeleteItemClicked(int position) {
                itemModelArrayListAddItems.remove(position);
                barcodearraylistAddItems.remove(position);
                recyclerViewAdapter.notifyItemRemoved(position);
                Log.d(TAG, "onDeleteItemClicked: "+itemModelArrayListAddItems+"  "+barcodearraylistAddItems);
            }
        });
        
        

        arrayList=new ArrayList<>();

        additembtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lock==false)
                    startActivityForResult(new Intent(AddItemsActivity.this,ScanActivity.class),2);
                else
                {
                    //productNameEt.setError("Please save the details");
                    Toast.makeText(AddItemsActivity.this,"Enter the details",Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void resetEditTexts() {
        productNameEt.setText("");
        barcodeEt.setText("");
        priceEt.setText("");
        Toast.makeText(AddItemsActivity.this,"saved",Toast.LENGTH_SHORT).show();
    }

    private void restoreItemList() {

        SharedPreferences sharedPreferences=getSharedPreferences("listitems",Context.MODE_PRIVATE);
        Gson gson=new Gson();
        Log.d(TAG, "restoreItemList: ");
        String json=sharedPreferences.getString("itemmodelarraylistadditems",null);
        if(json!=null) {
            Type type = new TypeToken<ArrayList<ItemModel>>() {
            }.getType();
            itemModelArrayListAddItems = gson.fromJson(json, type);
            Log.d(TAG, "itemmodelarraylist: "+itemModelArrayListAddItems);
            for(int i=0;i<itemModelArrayListAddItems.size();i++)
            {
                Log.d(TAG, "  "+itemModelArrayListAddItems.get(i).getBarcodeNumber());
            }
            //recyclerViewAdapter.notifyDataSetChanged();

        }
        else
        {
            itemModelArrayListAddItems=new ArrayList<>();

        }
        json=sharedPreferences.getString("barcodearraylistadditems",null);
        Log.d(TAG, "json again: "+json);
        if(json!=null)
        {
            Type type=new TypeToken<ArrayList<String>>(){}.getType();
            barcodearraylistAddItems=new Gson().fromJson(json,type);
            for(int i=0;i<barcodearraylistAddItems.size();i++)
            {
                Log.d(TAG, "barcode: "+barcodearraylistAddItems.get(i));
            }
        }
        else
        {
            barcodearraylistAddItems=new ArrayList<>();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==2 && resultCode==RESULT_OK)
        {
            insertItem(data.getStringExtra("code"));
            barcodeEt.setText(data.getStringExtra("code").trim());

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    private void insertItem(String ss) {
        ItemModel itemModel;
        if(itemModelArrayListAddItems.isEmpty())
        {
            itemModel=null;
        }
        else
        {
            itemModel=itemModelArrayListAddItems.get(itemModelArrayListAddItems.size()-1);
            Log.d(TAG, "last item in the list : "+itemModel.getProductName()+"  "+itemModel.getBarcodeNumber()+"  "+itemModel.getQuantity()+"   "+itemModel.getPrice());
        }





        ss=ss.trim();
        int positionOfSameValue=-1;
        if((positionOfSameValue=barcodearraylistAddItems.indexOf(ss))!=-1)
        {

            Log.d(TAG, "position of same value: "+positionOfSameValue+"itemlist"+itemModelArrayListAddItems+" barcodelist"+barcodearraylistAddItems);
//            int i=Integer.parseInt(itemModelArrayListAddItems.get(positionOfSameValue).getQuantity());
//            Log.d(TAG, "i val: "+i);
//            i++;
//            itemModelArrayListAddItems.get(positionOfSameValue).setQuantity(String.valueOf(i));

            Toast.makeText(AddItemsActivity.this,"already in list",Toast.LENGTH_SHORT).show();
            lock=false;

        }
        else
        {
            //barcodearraylistAddItems.add(ss);
            //itemModelArrayListAddItems.add(new ItemModel("",ss,"1",""));
            Toast.makeText(AddItemsActivity.this,"Enter the details",Toast.LENGTH_SHORT).show();
            lock=true;
        }
        Log.d(TAG, "insertItem: "+positionOfSameValue);
        //recyclerViewAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: of additemsactivity");
        SharedPreferences sharedPreferences=getSharedPreferences("listitems", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson gson=new Gson();
        String json=gson.toJson(itemModelArrayListAddItems);
        editor.putString("itemmodelarraylistadditems",json);
        json=gson.toJson(barcodearraylistAddItems);
        editor.putString("barcodearraylistadditems",json);
        editor.putString("additemsflag","set");
        editor.apply();
        Log.d(TAG, "flag: "+sharedPreferences.getString("flag","m"));
        Log.d(TAG, "onDestroy: itemmodel");
        for(int i=0;i<itemModelArrayListAddItems.size();i++)
        {
            Log.d(TAG, ":"+itemModelArrayListAddItems.get(i).getBarcodeNumber());
        }
        Log.d(TAG, "onDestroy: barcode");
        for(int i=0;i<barcodearraylistAddItems.size();i++)
        {
            Log.d(TAG, ": "+barcodearraylistAddItems.get(i));
        }

    }
}
