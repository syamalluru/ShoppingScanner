package com.practise.neo.shoppingscanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.practise.neo.shoppingscanner.Model.ItemModel;

import java.lang.reflect.Type;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "sham";
    private static final int ADD_ITEMS_REQ_CODE = 3;
    String pProductName, pBarcode, pPrice;


    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    ItemAdapter recyclerViewAdapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;

    ArrayList<ItemModel> itemModelArrayList, itemModelArrayListAddItems;
    TextView textView,priceTv;
    private ArrayList<String> barcodearraylist, barcodearraylistAddItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatingActionButton = findViewById(R.id.floatingbtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });
        priceTv=findViewById(R.id.price_value_main);

        //itemModelArrayList.add(new ItemModel("phone","234323432","2","432"));


//        itemModelArrayList.add(new ItemModel("mobile","234323432","2","432"));
//        itemModelArrayList.add(new ItemModel("phone","234323432","2","432"));
//        itemModelArrayList.add(new ItemModel("mobile","234323432","2","432"));
//        itemModelArrayList.add(new ItemModel("phone","234323432","2","432"));
//        itemModelArrayList.add(new ItemModel("mobile","234323432","2","432"));
//        itemModelArrayList.add(new ItemModel("phone","234323432","2","432"));
//        itemModelArrayList.add(new ItemModel("mobile","234323432","2","432"));

        SharedPreferences sharedPreferences = getSharedPreferences("listitems", Context.MODE_PRIVATE);
        String dum = sharedPreferences.getString("flag", null);
        priceTv.setText(sharedPreferences.getString("pricevalue","---"));
        Log.d(TAG, "shared flag: " + dum);
        if (dum != null && dum.equals("set")) {
            Log.d(TAG, "calling restoreitemlist: ");
            restoreItemList();
        }
        else {
            itemModelArrayList = new ArrayList<>();
            barcodearraylist = new ArrayList<>();
        }
        recyclerView = findViewById(R.id.recyclerviewMain);
        recyclerViewLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerViewAdapter = new ItemAdapter(itemModelArrayList);

        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onDeleteItemClicked(int position) {
                int toRemovePrice=Integer.parseInt(itemModelArrayList.get(position).getPrice().trim());
                int quantity=Integer.parseInt(itemModelArrayList.get(position).getQuantity().trim());
                if(quantity>1)
                {
                    int indivPrice=toRemovePrice/quantity;
                    int afterremovalPrice=toRemovePrice=indivPrice;
                    itemModelArrayList.get(position).setPrice(String.valueOf(afterremovalPrice));
                    itemModelArrayList.get(position).setQuantity(String.valueOf(quantity-1));
                    int a=Integer.parseInt(priceTv.getText().toString());
                    int b=a-indivPrice;
                    priceTv.setText(b+"");
                }
                else
                {
                    int a=Integer.parseInt(priceTv.getText().toString());
                    int b=a-toRemovePrice;
                    priceTv.setText(b+"");
                    itemModelArrayList.remove(position);
                    barcodearraylist.remove(position);
                }

                recyclerViewAdapter.notifyItemRemoved(position);
                Log.d(TAG, "onDeleteItemClicked: " + itemModelArrayList + "  " + barcodearraylist);
            }
        });


    }

    private void restoreItemList() {
        SharedPreferences sharedPreferences = getSharedPreferences("listitems", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Log.d(TAG, "restoreItemList: ");
        String json = sharedPreferences.getString("itemmodelarraylist", null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<ItemModel>>() {
            }.getType();
            itemModelArrayList = gson.fromJson(json, type);
            Log.d(TAG, "itemmodelarraylist: " + itemModelArrayList);
            for (int i = 0; i < itemModelArrayList.size(); i++) {
                Log.d(TAG, "  " + itemModelArrayList.get(i).getBarcodeNumber());
            }
            //recyclerViewAdapter.notifyDataSetChanged();

        } else {
            itemModelArrayList = new ArrayList<>();

        }
        json = sharedPreferences.getString("barcodearraylist", null);
        Log.d(TAG, "json again: " + json);
        if (json != null) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            barcodearraylist = new Gson().fromJson(json, type);
            for (int i = 0; i < barcodearraylist.size(); i++) {
                Log.d(TAG, "barcode: " + barcodearraylist.get(i));
            }
        } else {
            barcodearraylist = new ArrayList<>();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();

        //TODO: save to database
        SharedPreferences sharedPreferences = getSharedPreferences("listitems", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(itemModelArrayList);
        editor.putString("itemmodelarraylist", json);
        json = gson.toJson(barcodearraylist);
        editor.putString("barcodearraylist", json);
        editor.putString("pricevalue",priceTv.getText().toString());
        editor.putString("flag", "set");
        editor.apply();
        Log.d(TAG, "flag: " + sharedPreferences.getString("flag", "m"));
        Log.d(TAG, "onDestroy: itemmodel");
        for (int i = 0; i < itemModelArrayList.size(); i++) {
            Log.d(TAG, ":" + itemModelArrayList.get(i).getBarcodeNumber());
        }
        Log.d(TAG, "onDestroy: barcode");
        for (int i = 0; i < barcodearraylist.size(); i++) {
            Log.d(TAG, ": " + barcodearraylist.get(i));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.scan_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan_icon:
                scan();
                break;
            case R.id.add_items:
                Intent intent = new Intent(MainActivity.this, AddItemsActivity.class);
                Gson gson = new Gson();
                String json = gson.toJson(itemModelArrayList);
                intent.putExtra("itemmodelarraylist", json);
                json = gson.toJson(barcodearraylist);
                intent.putExtra("barcodearraylist", json);
                startActivityForResult(intent, ADD_ITEMS_REQ_CODE);
                break;
            case R.id.bill_gen:
                calculateBill();

        }

        return true;
    }

    private void calculateBill() {

        AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Final Bill:");
        dialog.setMessage("total bill is : "+priceTv.getText().toString().trim()+"/-");
        dialog.setPositiveButton("FINISH", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        dialog.setNegativeButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();

    }

    private void updatePrice(String price) {
        //priceTv=findViewById(R.id.price_value_main);
        Log.d(TAG, "updatePrice: act:"+priceTv.getText().toString());
        //String s=priceTv.getText().toString().substring(0,priceTv.getText().toString().trim().length()-2);
        //Log.d(TAG, "updatePrice: substr:"+s);
        int currentprice=Integer.parseInt(priceTv.getText().toString().trim());
        Log.d(TAG, "current price: "+currentprice);
        int added=currentprice+Integer.parseInt(price);
        priceTv.setText(added+"");

    }

    private void scan() {
        startActivityForResult(new Intent(MainActivity.this, ScanActivity.class), 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        SharedPreferences sharedPreferences = getSharedPreferences("listitems", Context.MODE_PRIVATE);
        String str = sharedPreferences.getString("itemmodelarraylistadditems", "missing");

        Type type = new TypeToken<ArrayList<ItemModel>>() {
        }.getType();
        //Log.d(TAG, "string:" + str);
        if (!(str.equals("missing"))) {
            itemModelArrayListAddItems = new Gson().fromJson(str, type);
//            for (int i = 0; i < itemModelArrayListAddItems.size(); i++) {
//                Log.d(TAG, ": " + itemModelArrayListAddItems.get(i).getProductName());
//            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "reqcode: " + requestCode + "  res" + resultCode);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: ");
            String ss = data.getStringExtra("code");
            getDataFromPrefsToMatch(ss);
            insertItem(ss);
        }
        if (requestCode == ADD_ITEMS_REQ_CODE && false) {
            Log.d(TAG, "req code 3: " + data.getStringExtra("code"));
            //String

            String ss = data.getStringExtra("code");

            //TODO:
            //Type type=new TypeToken<ArrayList<ItemModel>>(){}.getType();
//            itemModelArrayListAddItems=new Gson().fromJson(ss,type);
            Log.d(TAG, "itemmodelarraylistadditems: ");
            for (int i = 0; i < itemModelArrayListAddItems.size(); i++) {
                Log.d(TAG, ": " + itemModelArrayListAddItems.get(i).getProductName());
            }
        }
    }

    private void getDataFromPrefsToMatch(String barcodeValue) {

        Log.d(TAG, "getDataFromPrefsToMatch: " + barcodeValue);

        if (itemModelArrayListAddItems != null) {
            for (int i = 0; i < itemModelArrayListAddItems.size(); i++) {
                Log.d(TAG, ": " + itemModelArrayListAddItems.get(i).getBarcodeNumber());
                if (barcodeValue.equals(itemModelArrayListAddItems.get(i).getBarcodeNumber())) {
                    Log.d(TAG, "getDataFromPrefsToMatch: matched");
                    pProductName = itemModelArrayListAddItems.get(i).getProductName();
                    pBarcode = itemModelArrayListAddItems.get(i).getBarcodeNumber();
                    pPrice = itemModelArrayListAddItems.get(i).getPrice();
                }

            }

        }


    }

    private void insertItem(String ss) {

        ItemModel itemModel;
        if (itemModelArrayList.isEmpty()) {
            itemModel = null;
        } else {
            itemModel = itemModelArrayList.get(itemModelArrayList.size() - 1);
            Log.d(TAG, "last item in the list : " + itemModel.getProductName() + "  " + itemModel.getBarcodeNumber() + "  " + itemModel.getQuantity() + "   " + itemModel.getPrice());
        }


        ss = ss.trim();
        int positionOfSameValue = -1;
        if ((positionOfSameValue = barcodearraylist.indexOf(ss)) != -1) {

            Log.d(TAG, "position of same value: " + positionOfSameValue + "itemlist" + itemModelArrayList + " barcodelist" + barcodearraylist);
            int i = Integer.parseInt(itemModelArrayList.get(positionOfSameValue).getQuantity());
            int iiprice = Integer.parseInt(itemModelArrayList.get(positionOfSameValue).getPrice());
            Log.d(TAG, "i val: " + i + " price" + iiprice);
            int indiv = iiprice / i;
            i++;

            iiprice = i * indiv;
            if(priceTv.getText().toString().trim().contains("---"))
            {
                Log.d(TAG, "contains ---: ");
            }
            else
            {
                updatePrice(String.valueOf(indiv));
            }

            itemModelArrayList.get(positionOfSameValue).setQuantity(String.valueOf(i));
            itemModelArrayList.get(positionOfSameValue).setPrice(String.valueOf(iiprice));
        } else {

            if (pProductName != null) {

                barcodearraylist.add(ss);
                itemModelArrayList.add(new ItemModel(pProductName, ss, "1", pPrice));
                if(priceTv.getText().toString().trim().contains("---"))
                {
                    Log.d(TAG, "fasak: ");
                    priceTv.setText(pPrice);
                }
                else
                {
                    updatePrice(pPrice);
                }

                Log.d(TAG, "insertItem: while populating:" + pProductName + "   " + pPrice);
            } else {
                Toast.makeText(MainActivity.this, "Item is not in the database", Toast.LENGTH_LONG).show();
            }

        }
        Log.d(TAG, "insertItem: " + positionOfSameValue);


        recyclerViewAdapter.notifyDataSetChanged();
    }
}
