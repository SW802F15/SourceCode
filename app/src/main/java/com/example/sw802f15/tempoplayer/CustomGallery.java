package com.example.sw802f15.tempoplayer;

import android.content.Context;
import android.content.res.TypedArray;import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.internal.widget.TintTypedArray.obtainStyledAttributes;

/**
 * Created by Draegert on 18-02-2015.
 */
public class CustomGallery extends BaseAdapter {

    Context context;
    int itemBackground;

    ArrayList<Integer> imageIDs = new ArrayList<>();


    public CustomGallery(Context c){
        context = c;


        TypedArray a = c.obtainStyledAttributes(R.styleable.gallery);
        itemBackground = a.getResourceId(R.styleable.gallery_android_galleryItemBackground, 0);
        a.recycle();

    }

    public void addImageToGallery(Integer image){
        imageIDs.add(image);
    }

    public void removeImageFromGallery(Integer image){
        imageIDs.remove(image);
    }

    public int getCount() {
        return imageIDs.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView itemImage;
        if (convertView == null){
            itemImage = new ImageView(context);
            itemImage.setScaleType(ImageView.ScaleType.FIT_XY);
            itemImage.setLayoutParams(new Gallery.LayoutParams((int)(parent.getHeight()), parent.getHeight()));
            itemImage.setImageResource(imageIDs.get(position));
        }
        else {
            itemImage = (ImageView)convertView;
        }
        itemImage.setBackgroundResource(itemBackground);

        return itemImage;
    }
}
