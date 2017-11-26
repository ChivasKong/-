package com.example.a50703.midtermproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 50703 on 2017/11/25.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;  //存储list_item的子view
    private View mConvertView;       //存储list_item
    public ViewHolder(Context context , View itemView , ViewGroup parent ) {
        super(itemView);   //指向父类的(View view)构造方法
        mConvertView = itemView;
        mViews = new SparseArray<View>();
    }
    //获取ViewHolder实例
    public static ViewHolder get(Context context , ViewGroup parent , int layoutId){
        View itemView = LayoutInflater.from(context).inflate(layoutId,parent,false);
        ViewHolder holder = new ViewHolder(context , itemView , parent);
        return holder;
    }
    //将还没被缓存到稀疏数组里的子View通过ID查找到后缓存，已被缓存的直接返回
    public <T extends View> T getView(int viewID){  //抽象函数
        View view=mViews.get(viewID);
        if(view==null){
            view=mConvertView.findViewById(viewID);
            mViews.put(viewID,view);
        }
        return (T)view;
    }
}
