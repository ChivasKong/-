package com.example.a50703.midtermproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private SQLiteDatabase db;
    private List<NATION> nationlist;
    private GridView gridView;
    private RecyclerView recyclerView;
    private CommonAdapter commonAdapter , commonAdapter_check;
    static public String [] nation_name = {"东汉","魏","蜀","吴","袁绍","袁术","刘表","起义军","董卓","刘璋","西晋","少数民族","其他"};
    public int[] nation_pic = {R.mipmap.donghan,R.mipmap.wei1,R.mipmap.shu1,R.mipmap.wu1,R.mipmap.yuanshao,R.mipmap.yuanshu,R.mipmap.liubiao,R.mipmap.qiyijun,R.mipmap.dongzhuo,R.mipmap.liuzhang ,R.mipmap.xijin,R.mipmap.shaoshuminzu,R.mipmap.qita};
    private final ArrayList<Map<String , Object>> person_list = new ArrayList<>();
    private AutoCompleteTextView search;
    private ImageButton Searchingbutton;
    private Button TestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListData();
        gridView = (GridView) findViewById(R.id.grid);
        setGridView();
        init_Database();
        init_person();
        search = (AutoCompleteTextView)findViewById(R.id.search);
        Searchingbutton = (ImageButton)findViewById(R.id.imageButton);
        TestButton = (Button)findViewById(R.id.button);
        Searchingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String hint = search.getText().toString();
                Gosearch(hint);
            }
        });
        TestButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(MainActivity.this,TestActivity.class);
                startActivity(intent);
            }
        });
 //      this.deleteDatabase("personinformation.db");
    }

    private void setListData(){   /*势力数据*/
        nationlist = new ArrayList<NATION>();
        for(int i = 0 ; i < nation_name.length ; i++){
            NATION mynation = new NATION();
            mynation.setName(nation_name[i]);mynation.setPic(nation_pic[i]);
            nationlist.add(mynation);
        }
    }

    private void Gosearch(String s){
        Cursor cursor = db.rawQuery("select * from personinformation where name like '%" + s + "%' or birth like '%" + s + "%' or nation like '%" + s + "%'" , null);
        person_list.clear();

        while (cursor.moveToNext())
        {
            Map<String,Object> temp = new LinkedHashMap<>();
            temp.put("姓名",cursor.getString(1));
            temp.put("所属势力",cursor.getString(5));
            temp.put("图像",cursor.getInt(0));
            temp.put("简介",cursor.getString(6));
            person_list.add(temp);
        }
        commonAdapter = new CommonAdapter<Map<String , Object>>(this, R.layout.person_list ,person_list) {
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
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(commonAdapter);
        animationAdapter.setDuration(500);
        recyclerView.setAdapter(animationAdapter);
        recyclerView.setItemAnimator(new OvershootInLeftAnimator());
        cursor.close();
        commonAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int pos) {
                Bundle bundle1 = new Bundle();
                Intent intent1 = new Intent(MainActivity.this,PersonInfo.class);
                bundle1.putInt("Personinfo",Integer.parseInt(person_list.get(pos).get("图像").toString())); //将商品详细信息中对应商品所有信息都传到另一个Acitivity
                intent1.putExtras(bundle1);
                startActivityForResult(intent1 , 1);
            }
            //商品列表长按删除商品
            @Override
            public void onLongClick(int pos) {
                Toast.makeText(MainActivity.this,"已移除"+ person_list.get(pos).get("姓名")+"人物信息" , Toast.LENGTH_SHORT).show();
                db.execSQL("delete from personinformation where picture=" + person_list.get(pos).get("图像"));
                person_list.remove(pos);
                commonAdapter.notifyDataSetChanged();
            }
        });
    }

    /**设置GirdView参数，绑定数据*/
    private void setGridView() {
        int size = nationlist.size();
        int length = 130;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(5); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 设置列数量=列表集合数

        final GridViewAdapter gridViewAdapter = new GridViewAdapter(getApplicationContext(),nationlist);
        gridView.setAdapter(gridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,NationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("NATION",nationlist.get(position).getName());
                bundle.putString("DATABASE" , db.getPath());
                intent.putExtras(bundle);
                startActivityForResult(intent , 0);
            }
        });

    }

    public void init_person(){
        Cursor cursor = db.rawQuery("select * from personinformation order by Random() limit 5", null);
        while (cursor.moveToNext())
        {
            Map<String,Object> temp = new LinkedHashMap<>();
            temp.put("姓名",cursor.getString(1));
            temp.put("所属势力",cursor.getString(5));
            temp.put("图像",cursor.getInt(0));
            temp.put("简介",cursor.getString(6));
            person_list.add(temp);
        }
        commonAdapter = new CommonAdapter<Map<String , Object>>(this, R.layout.person_list ,person_list) {
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
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(commonAdapter);
        animationAdapter.setDuration(500);
        recyclerView.setAdapter(animationAdapter);
        recyclerView.setItemAnimator(new OvershootInLeftAnimator());
        cursor.close();
        commonAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int pos) {
                Bundle bundle1 = new Bundle();
                Intent intent1 = new Intent(MainActivity.this,PersonInfo.class);
                bundle1.putInt("Personinfo",Integer.parseInt(person_list.get(pos).get("图像").toString())); //将商品详细信息中对应商品所有信息都传到另一个Acitivity
                intent1.putExtras(bundle1);
                startActivityForResult(intent1 , 1);
            }
            //商品列表长按删除商品
            @Override
            public void onLongClick(int pos) {
                Toast.makeText(MainActivity.this,"已移除"+ person_list.get(pos).get("姓名")+"人物信息" , Toast.LENGTH_SHORT).show();
                db.execSQL("delete from personinformation where picture=" + person_list.get(pos).get("图像"));
                person_list.remove(pos);
                commonAdapter.notifyDataSetChanged();
            }
        });
    }

    private void init_Database(){
        mContext = this;
        //创建一个帮助类对象
        MySqliteOpenHelper mySqliteOpenHelper = new MySqliteOpenHelper(mContext);
        db = mySqliteOpenHelper.getWritableDatabase();
        //调用getReadableDatabase方法,来初始化数据库的创建
//         ContentValues contentValues = new ContentValues();
//        contentValues.put("picture", R.mipmap.zhugeliang); contentValues.put("name", "诸葛亮"); contentValues.put("sex" , "男"); contentValues.put("year","181-234"); contentValues.put("birth","琅琊阳都");contentValues.put("nation", "蜀");
//        contentValues.put("commitment", "诸葛亮治国治军的才能，济世爱民、谦虚谨慎的品格为后世各种杰出的历史人物树立了榜样。历代君臣、知识分子、人民群众都从不同的角度称赞他。但《三国演义》虽然突出了诸葛亮一生性格、品德、功业等的积极方面，但又把它无限夸大，把他描写成智慧的化身、忠贞的代表，并将其神化成了半人半神的超人形象。");
//        db.insert("personinformation", null, contentValues); contentValues.clear();
//
//        contentValues.put("picture", R.mipmap.liubei); contentValues.put("name", "刘备"); contentValues.put("nation", "蜀");contentValues.put("sex" , "男"); contentValues.put("year","161-223"); contentValues.put("birth","涿郡涿县");
//        contentValues.put("commitment", "在《三国演义》中，作者把刘备描写成“仁”的代表，汉室皇权正统的继承者，因而对刘备的仁爱、宽厚和知人善任的性格特征着力描画，极尽夸张，但在突出其“仁爱”时却又落入了“无能”一面，给人以“无能”和“虚伪”的感觉。");
//        db.insert("personinformation", null, contentValues); contentValues.clear();
//
//        contentValues.put("picture", R.mipmap.guanyu); contentValues.put("name", "关羽"); contentValues.put("nation", "蜀");contentValues.put("sex" , "男"); contentValues.put("year","???-219"); contentValues.put("birth","司隶河东郡解");
//        contentValues.put("commitment", "历史上的关羽为“万人之敌”一虎将，傲上而不悔下，恩怨分明，以信义著称，但“刚而自矜”，勇猛有余，智略不足。在《三国演义》中，因为他是刘备阵营中的人，又有讲信义的特点，所以，就被塑造成“义”的化身。他跟随刘备，不避艰险。总之，在《三国演义》中，作者是不惜笔墨，把关羽刻画成“义重如山之人”，因而其形象也被严重的扭曲了。");
//        db.insert("personinformation", null, contentValues); contentValues.clear();
//
//        contentValues.put("picture", R.mipmap.caocao); contentValues.put("name", "曹操"); contentValues.put("nation", "魏");contentValues.put("sex" , "男"); contentValues.put("year","155-220"); contentValues.put("birth","豫州沛国谯");
//        contentValues.put("commitment", "历史上的曹操性格非常复杂，有人认为曹操在三国历史上“明略最优”。曹操御军三十余年，但手不释卷，登高必赋，长于诗文、草书、围棋。生活节俭，不好华服。他是中国历史上第一流的政治家、军事家、文学家。但是，在《三国演义》中，曹操性格品德中这些好的方面被忽略了，而对他残忍、奸诈的一面又夸大了。因此，罗贯中笔下的曹操是奸诈、残忍、任性、多疑的反面人物典型。");
//        db.insert("personinformation", null, contentValues); contentValues.clear();
//
//        contentValues.put("picture", R.mipmap.zhouyu); contentValues.put("name", "周瑜"); contentValues.put("nation", "吴");contentValues.put("sex" , "男"); contentValues.put("year","172-217"); contentValues.put("birth","扬州庐江郡舒");
//        contentValues.put("commitment", "历史上的周瑜“性度恢廓”，谦让服人，有“雅量高致”。但在《三国演义》中，周瑜成了诸葛亮的垫底人物。写周瑜，是为了抬高诸葛亮。因此，《三国演义》中的周瑜气量狭小，智谋也总是逊诸葛亮一筹，根本不像苏轼所歌颂的周瑜“雄姿英发，羽扇纶巾，谈笑间，樯橹灰飞烟灭”的“千古风流人物”，成了《三国演义》中蒙受最大冤屈的人物。");
//        db.insert("personinformation", null, contentValues); contentValues.clear();
//
//        contentValues.put("picture", R.mipmap.lusu); contentValues.put("name", "鲁肃"); contentValues.put("nation", "吴");contentValues.put("sex" , "男"); contentValues.put("year","175-210"); contentValues.put("birth","徐州下邳国东城");
//        contentValues.put("commitment", "鲁肃这个人在《三国演义》中除了名字外几乎就全是杜撰了。历史上的鲁肃“虽在军阵，手不释卷”。他初见孙权，就提出了建国方针：鼎足江东，北拒曹操，待机剿除黄祖，进发刘表，竟长江而有之，然后建号称帝以图天下。但在《三国演义》中，鲁肃成了诸葛亮与周瑜两人智斗的牺牲品，被愚弄、受欺侮的典型。 ");
//        db.insert("personinformation", null, contentValues); contentValues.clear();
//
//        contentValues.put("picture", R.mipmap.zhangfei); contentValues.put("name", "张飞"); contentValues.put("nation", "蜀");contentValues.put("sex" , "男"); contentValues.put("year","???-221"); contentValues.put("birth","幽州涿郡");
//        contentValues.put("commitment", "少时即与关羽共事刘备。曾在虎牢关与关羽、刘备一起迎战吕布。长坂坡桥头上一声吼，吓退曹操五千精骑，入川义释严颜，分定州县，率精兵万多人，败张郃大军，刘备称王后，拜为右将军，称帝后，拜为车骑将军，封西乡侯。公元221年为替关羽报仇，同刘备起兵攻伐东吴。临行前，因被部将范疆、张达刺杀，死时只有五十五岁。 ");
//        db.insert("personinformation", null, contentValues); contentValues.clear();
//
//        contentValues.put("picture", R.mipmap.zhaoyun); contentValues.put("name", "赵云"); contentValues.put("nation", "蜀");contentValues.put("sex" , "男"); contentValues.put("year","???-229"); contentValues.put("birth","冀州常山国真定");
//        contentValues.put("commitment", "赵云戎马一生，骁勇善战，胆略过人，刘备称其一身是胆，军士呼其虎威将军。赵云见识卓远，清楚认识到吴蜀关系为唇齿相依，力主维护孙、刘联盟；为人刚毅谨细，诸葛亮对其德行十分赞赏。赵云为国，不被天姿国色所迷，为民，不为良田豪宅所动，时人与后人皆敬其德。");
//        db.insert("personinformation", null, contentValues); contentValues.clear();
//
//        contentValues.put("picture", R.mipmap.guojia); contentValues.put("name", "郭嘉"); contentValues.put("nation", "魏");contentValues.put("sex" , "男"); contentValues.put("year","170-207"); contentValues.put("birth","豫州颍川郡阳翟");
//        contentValues.put("commitment", "曹操帐下第一谋士，才华横溢，放荡不羁，不修边幅，爱嗑药！在那个时代真正的属于离经叛道的典型了但其军事才华却是有目共睹的。");
//        db.insert("personinformation", null, contentValues); contentValues.clear();
//
//        contentValues.put("picture", R.mipmap.jiaxu); contentValues.put("name", "贾诩"); contentValues.put("nation", "魏");contentValues.put("sex" , "男"); contentValues.put("year","147-223"); contentValues.put("birth","凉州武威郡姑臧");
//        contentValues.put("commitment", "曹操五大谋士之一，深谙保身之道，三国局面的真正缔造者，军事才能卓著。");
//        db.insert("personinformation", null, contentValues); contentValues.clear();
//
//        contentValues.put("picture", R.mipmap.caimao); contentValues.put("name", "蔡瑁"); contentValues.put("nation", "刘表");contentValues.put("sex" , "男"); contentValues.put("year","???-???"); contentValues.put("birth","荆州南郡襄阳");
//        contentValues.put("commitment", "蔡瑁（生卒年不详），字德珪，襄阳蔡州（今湖北襄阳）人。东汉末年荆州名族，蔡讽之子，姑母（蔡讽之姐）是太尉张温之妻，长姐与二姐先后嫁给黄承彦与刘表成为继室。初平元年（190年），刘表代王睿为荆州刺史，当时江南宗贼兴盛，蔡瑁协助刘表平定荆州，仕奉刘表期间，历任江夏、南郡、章陵等诸郡太守，刘表获得汉廷封赐镇南将军时担任其军师。刘表病亡后，拥护刘琮继位，在公元208年曹操挥军入荆州时，与蒯越共同迫刘琮降伏曹操，尔后仕入曹操麾下，历任从事中郎、司马、长水校尉，封爵为汉阳亭侯。蔡瑁（生卒年不详），字德珪，襄阳蔡州（今湖北襄阳）人。东汉末年荆州名族，蔡讽之子，姑母（蔡讽之姐）是太尉张温之妻，长姐与二姐先后嫁给黄承彦与刘表成为继室。初平元年（190年），刘表代王睿为荆州刺史，当时江南宗贼兴盛，蔡瑁协助刘表平定荆州，仕奉刘表期间，历任江夏、南郡、章陵等诸郡太守，刘表获得汉廷封赐镇南将军时担任其军师。刘表病亡后，拥护刘琮继位，在公元208年曹操挥军入荆州时，与蒯越共同迫刘琮降伏曹操，尔后仕入曹操麾下，历任从事中郎、司马、长水校尉，封爵为汉阳亭侯。" );
//        db.insert("personinformation", null, contentValues); contentValues.clear();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //回调信息的接收处理
        if(resultCode == RESULT_OK){  //回调的信息回传成功
            if(requestCode == 0){
                if(data.getIntExtra("delete",0) == 1){
                    person_list.clear();
                    init_Database();
                    init_person();
                }
            }
        }
    }

    public class NATION{
        private int pic;
        private String name;

        public void setPic(int pic)
        {
            this.pic=pic;
        }
        public void setName(String name)
        {
            this.name=name;
        }
        public int getPic()
        {
            return pic;
        }
        public String getName()
        {
            return name;
        }

    }

}
