package com.example.a50703.midtermproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by 50703 on 2017/11/25.
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    private Context context;
    private List<T> datas ;      //列表
    private  int layoutId;      //布局ID
    private OnItemClickListener onItemClickListener;
    public CommonAdapter(Context context , int layoutId , List datas){ //构造函数
        this.context = context;
        this.layoutId = layoutId;
        this.datas = datas;
    }
    //新建一个item视图，返回对应的viewholder
    public ViewHolder onCreateViewHolder(final ViewGroup parent , int viewType){
        ViewHolder viewholder = ViewHolder.get(context , parent , layoutId);
        return viewholder;
    }
    //数据绑定到正确的Viewholder上
    public void onBindViewHolder(final ViewHolder holder , int pos){
        //在MainActivit调用时重写的convert函数
        convert(holder , datas.get(pos));
        //实现OnItemClickListener
        if(onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                //点击Adapter中某个item的位置则响应给被点击的那个item的监听器
                @Override
                public void onClick(View v){
                    onItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v){
                    onItemClickListener.onLongClick(holder.getAdapterPosition());
                    return false;
                }
            });
        }
    }

    protected abstract void convert(ViewHolder holder, T t);

    public int getItemCount(){ return datas.size();} // 返回list的大小
    //为RecyclerView实现OnItemClickListener,设置监听函数
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

}

