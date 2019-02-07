package com.practise.neo.shoppingscanner;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.practise.neo.shoppingscanner.Model.ItemModel;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    public ArrayList<ItemModel> itemModelArrayList;
    public OnItemClickListener itemClickListener;

    public interface OnItemClickListener{
        void onDeleteItemClicked(int position);
    }

    public ItemAdapter(ArrayList<ItemModel> itemModelArrayList)
    {
        this.itemModelArrayList=itemModelArrayList;
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.itemClickListener=listener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public TextView productName,barcodeNumb,quantity,price;
        public ImageView deletebtn;
        public ItemViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            productName=itemView.findViewById(R.id.textcode);
            barcodeNumb=itemView.findViewById(R.id.barcode_number);
            quantity=itemView.findViewById(R.id.quantity_number);
            price=itemView.findViewById(R.id.item_price);
            deletebtn=itemView.findViewById(R.id.delete_item_btn);
            deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                    {
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION) {
                            listener.onDeleteItemClicked(position);
                        }
                    }

                }
            });
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_main_item_view,parent,false);
        ItemViewHolder itemViewHolder=new ItemViewHolder(v,itemClickListener);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        ItemModel currentItem=itemModelArrayList.get(position);
        holder.productName.setText(currentItem.getProductName());
        holder.barcodeNumb.setText(currentItem.getBarcodeNumber());
        holder.quantity.setText(currentItem.getQuantity());
        holder.price.setText(currentItem.getPrice());

    }

    @Override
    public int getItemCount() {
        return itemModelArrayList.size();
    }

}
