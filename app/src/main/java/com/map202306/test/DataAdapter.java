package com.map202306.test;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataAdapter {
    protected static final String TAG = "DataAdapter";

    // TODO : TABLE 이름을 명시해야함
    protected static final String TABLE_NAME = "toilet";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DBHelper mDbHelper;

    public DataAdapter(Context context)
    {
        this.mContext = context;
        mDbHelper = new DBHelper(mContext);
    }

    public DataAdapter createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DataAdapter open() throws SQLException
    {
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }

    public List getTableData()
    {
        try
        {
            // Table 이름 -> antpool_bitcoin 불러오기
            String sql ="SELECT * FROM " + TABLE_NAME;

            // 모델 넣을 리스트 생성
            List toiletList = new ArrayList();

            // TODO : 모델 선언
            Toilet toilet = null;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur!=null)
            {
                // 칼럼의 마지막까지
                while( mCur.moveToNext() ) {

                    // TODO : 커스텀 모델 생성
                    toilet = new Toilet();

                    // TODO : Record 기술
                    //id,name,doro,old-doro,man_dae,man_so,man2_dae,man2_so,woman,woman2,latitude,longitude
                    toilet.setId(mCur.getInt(0));
                    toilet.setName(mCur.getString(1));
                    toilet.setDoro(mCur.getString(2));
                    toilet.setMan_dae(mCur.getInt(4));
                    toilet.setMan_so(mCur.getInt(5));
                    toilet.setMan2_dae(mCur.getInt(6));
                    toilet.setMan2_so(mCur.getInt(7));
                    toilet.setWoman(mCur.getInt(8));
                    toilet.setWoman2(mCur.getInt(9));
                    toilet.setLatitude(mCur.getDouble(10));
                    toilet.setLongitude(mCur.getDouble(11));
                    // 리스트에 넣기
                    toiletList.add(toilet);
                }

            }
            return toiletList;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

}
