package com.example.a50703.midtermproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 50703 on 2017/11/26.
 */

public class PersonInfo extends AppCompatActivity {
    private SQLiteDatabase db;
    private MySqliteOpenHelper mySqliteOpenHelper;
    private ImageButton yes ;
    private ImageButton no;
    private EditText edt ;
     private TextView name;
     private TextView sex;
     private TextView year ;
     private TextView birth ;
     private TextView jianjie ;
     private TextView nation ;
     private ImageView imageView ;
    private static final int IMAGE = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_info);
        Intent intent = this.getIntent();
        mySqliteOpenHelper = new MySqliteOpenHelper(this);
        db = mySqliteOpenHelper.getWritableDatabase();
        yes = (ImageButton)findViewById(R.id.yes);
        no = (ImageButton)findViewById(R.id.no);
        edt = (EditText)findViewById(R.id.editText);
        edt.setVisibility(View.GONE);
        yes.setVisibility(View.GONE);
        no.setVisibility(View.GONE);
         name = (TextView)findViewById(R.id.personname);
         sex = (TextView)findViewById(R.id.sex);
         year = (TextView)findViewById(R.id.year);
         birth = (TextView)findViewById(R.id.personplace);
         jianjie = (TextView)findViewById(R.id.jianjie);
         nation = (TextView)findViewById(R.id.shili);
         imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageResource(intent.getIntExtra("Personinfo",0));

        Cursor cursor = db.rawQuery("select * from personinformation where picture=" + intent.getIntExtra("Personinfo",0)  , null);
        cursor.moveToNext();
        name.setText(" 姓名：" + cursor.getString(1));
        sex.setText(" 性别：" + cursor.getString(2));
        year.setText(" 生卒年月：" + cursor.getString(3));
        birth.setText(" 籍贯：" + cursor.getString(4));
        nation.setText(" 所属势力：" + cursor.getString(5));
        jianjie.setText("简介：" + cursor.getString(6));

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent1 = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("back" , "back");
                intent1.putExtras(bundle);
                setResult(RESULT_OK,intent1);
                finish();
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_to_edt();
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String changeString = edt.getText().toString();
//                db.execSQL("update personinformation set name=\"" + changeString + "\" where picture=" + imageView.getId());
                        db.execSQL("delete from personinformation where picture=" + imageView.getId());
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("picture", imageView.getId()); contentValues.put("name", changeString); contentValues.put("sex" , sex.getText().toString()); contentValues.put("year",year.getText().toString()); contentValues.put("birth",birth.getText().toString());contentValues.put("nation", nation.getText().toString());
                        contentValues.put("commitment", jianjie.getText().toString());
                        db.insert("personinformation", null, contentValues); contentValues.clear();
                        name.setText(changeString);

                        return_to_info();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        return_to_info();
                    }
                });
            }
        });



    }
    public void change_to_edt(){
        edt.setVisibility(View.VISIBLE);
        yes.setVisibility(View.VISIBLE);
        no.setVisibility(View.VISIBLE);
        name.setVisibility(View.GONE);
        nation.setVisibility(View.GONE);
        sex.setVisibility(View.GONE);
        year.setVisibility(View.GONE);
        birth.setVisibility(View.GONE);
        jianjie.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
    }

    public void return_to_info(){
        edt.setVisibility(View.GONE);
        yes.setVisibility(View.GONE);
        no.setVisibility(View.GONE);
        name.setVisibility(View.VISIBLE);
        nation.setVisibility(View.VISIBLE);
        sex.setVisibility(View.VISIBLE);
        year.setVisibility(View.VISIBLE);
        birth.setVisibility(View.VISIBLE);
        jianjie.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
    }
    public void onClick(View v) {
        //调用相册
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == IMAGE && resultCode == PersonInfo.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            showImage(imagePath);
            c.close();
        }
    }

    //加载图片
    private void showImage(String imaePath){
        Bitmap bm = BitmapFactory.decodeFile(imaePath);
        ((ImageView)findViewById(R.id.imageView)).setImageBitmap(bm);
        db.execSQL("update personinformation set picture=\"" + bm + "\" where picture=" + imageView.getId());
    }
}
