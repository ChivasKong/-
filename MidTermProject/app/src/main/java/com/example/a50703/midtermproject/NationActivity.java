package com.example.a50703.midtermproject;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;

/**
 * Created by 50703 on 2017/11/26.
 */

public class NationActivity extends AppCompatActivity{
    private SQLiteDatabase db;
    private String mynation;
    private RecyclerView recyclerView;
    private CommonAdapter commonAdapter;
    private MySqliteOpenHelper mySqliteOpenHelper;
    final private ArrayList<Map<String , Object>> pl = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nation_person);
        Intent intent = this.getIntent();
        mynation = (String)intent.getStringExtra("NATION");
        mySqliteOpenHelper = new MySqliteOpenHelper(this);
        db = mySqliteOpenHelper.getWritableDatabase();
        TextView textView = (TextView) findViewById(R.id.shili);
        textView.setText("势力："+mynation);
        check_person(mynation);

        ImageView imageView = (ImageView)findViewById(R.id.back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //按下返回键，肯定返回到MainActivity，判断购物车的按键是否被按下过，假如是，则回传
                //商品的详细信息
                Intent intent1 = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("delete" ,1);
                intent1.putExtras(bundle);
                setResult(RESULT_OK,intent1);
                finish();
            }
        });

        commonAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int pos) {
                Bundle bundle1 = new Bundle();
                Intent intent1 = new Intent(NationActivity.this,PersonInfo.class);
                bundle1.putInt("Personinfo",Integer.parseInt(pl.get(pos).get("图像").toString())); //将商品详细信息中对应商品所有信息都传到另一个Acitivity
                intent1.putExtras(bundle1);
                startActivityForResult(intent1 , 1);
            }
            //商品列表长按删除商品
            @Override
            public void onLongClick(int pos) {
                Toast.makeText(NationActivity.this,"已移除"+ pl.get(pos).get("姓名")+"人物信息" , Toast.LENGTH_SHORT).show();
                db.execSQL("delete from personinformation where picture=" + pl.get(pos).get("图像").toString());
                pl.remove(pos);
                commonAdapter.notifyDataSetChanged();
            }
        });
    }
    public void check_person(String mynation){
        Cursor cursor = db.rawQuery("select * from personinformation where nation=\"" + mynation +"\"" , null);
        while (cursor.moveToNext())
        {
            Map<String,Object> temp = new LinkedHashMap<>();
            temp.put("姓名",cursor.getString(1));
            temp.put("所属势力",cursor.getString(5));
            temp.put("图像",cursor.getInt(0));
            temp.put("简介",cursor.getString(6));
            pl.add(temp);
        }
        commonAdapter = new CommonAdapter<Map<String , Object>>(this, R.layout.person_list ,pl) {
            @Override
            protected void convert(ViewHolder holder, Map<String, Object> s) {
                TextView name = holder.getView(R.id.name);
                name.setText("姓名："+s.get("姓名").toString());
                TextView nation = holder.getView(R.id.nation);
                nation.setText("所属势力："+s.get("所属势力").toString());
                ImageView pic = holder.getView(R.id.person);
                pic.setImageResource(Integer.parseInt(s.get("图像").toString()));
                TextView commit = holder.getView(R.id.infomation);
                commit.setText("简介："+s.get("简介".toString()));
            }
        };
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(commonAdapter);
        animationAdapter.setDuration(500);
        recyclerView.setAdapter(animationAdapter);
        recyclerView.setItemAnimator(new OvershootInLeftAnimator());
        cursor.close();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //回调信息的接收处理
        if(resultCode == RESULT_OK){  //回调的信息回传成功

        }
    }
}
