package com.sky.exp3.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库辅助类，用于创建和管理“记账”相关的SQLite数据库。
 * 继承自SQLiteOpenHelper，负责数据库的创建、升级以及基础的CRUD操作。
 */
public class TallyDbHelper extends SQLiteOpenHelper {

    // 数据库名称
    public static final String DB_NAME = "tally.db";

    // 数据库版本号，每次修改数据库结构时需要递增
    public static final int DB_VERSION = 2;

    // 表名
    public static final String TABLE_NAME = "tally";

    // 表字段名
    public static final String COLUMN_ID = "_id";          // 主键，自增ID
    public static final String COLUMN_TYPE = "type";       // 账目类型，如餐饮、交通等
    public static final String COLUMN_AMOUNT = "amount";   // 金额，使用REAL类型
    public static final String COLUMN_TIME = "time";       // 记账时间，存储为文本格式

    /**
     * 构造方法
     *
     * @param context 上下文对象，用于打开或创建数据库
     */
    public TallyDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 数据库首次创建时调用，执行建表语句
     *
     * @param db SQLiteDatabase实例
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建记账表，包含ID、类型、金额、时间字段
        String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TYPE + " TEXT," +
                COLUMN_AMOUNT + " REAL," +
                COLUMN_TIME + " TEXT)";
        db.execSQL(SQL_CREATE);
    }

    /**
     * 根据ID删除指定的数据记录
     *
     * @param id 要删除的记录ID
     * @return 删除是否成功，成功返回true，否则false
     */
    public boolean deleteDataById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // 执行删除操作，条件为ID匹配
        int rows = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        System.out.println("Deleting id = " + id + ", rows affected = " + rows);
        return rows > 0;
    }

    /**
     * 数据库版本升级时调用，默认处理为删除旧表重建
     *
     * @param db         SQLiteDatabase实例
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 删除旧表，防止冲突
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // 重新创建新表
        onCreate(db);
    }
}
