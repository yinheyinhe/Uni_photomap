package com.HBuilder.UniPlugin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String SWORD="SWORD";
    //三个不同参数的构造函数
    //带全部参数的构造函数，此构造函数必不可少
    //带全部参数的构造函数，此构造函数必不可少
    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    //带两个参数的构造函数，调用的其实是带三个参数的构造函数
    //public DatabaseHelper(Context context,String name){
    //   this(context,name,VERSION);
    //}
    //创建数据库
    public void onCreate(SQLiteDatabase db) {
        Log.i(SWORD,"create a Database");
        //创建数据库sql语句
        //序号 图片地址 经度 纬度 方向
        String sql = "create table user(" +
                "id int primary key autoincrement not null," +
                "path varchar(40)," +
                "location varchsr(50)," +
                "longitude varchar(40)," +
                "latitude varchar(40)," +
                "orientation varchar(40))";
        //执行创建数据库操作
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //创建成功，日志输出提示
        Log.i(SWORD,"update Database");
    }

}