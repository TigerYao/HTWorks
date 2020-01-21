package com.huatu.handheld_huatu.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjx on 2019\4\10 0010.  参考百家云 com.baijiahulian.downloader.download.db
 */


public abstract class AbsDataBaseDao<T> {
    private SQLiteOpenHelper helper;

    public AbsDataBaseDao(SQLiteOpenHelper var1) {
        this.helper = var1;
    }

    public final SQLiteDatabase openReader() {
        return this.helper.getReadableDatabase();
    }

    public final SQLiteDatabase openWriter() {
        return this.helper.getWritableDatabase();
    }

    protected final void closeDatabase(SQLiteDatabase var1, Cursor var2) {
        if(var2 != null && !var2.isClosed()) {
            var2.close();
        }

       /*  if(var1 != null && var1.isOpen()) {
            var1.close();
        }*/
    }

    protected abstract String getTableName();

    public int count() {
        return this.countColumn("_id");
    }

    public int countColumn(String var1) {
        String var2 = "SELECT COUNT(?) FROM " + this.getTableName();
        SQLiteDatabase var3 = this.openReader();
        Cursor var4 = null;

        try {
            var3.beginTransaction();
            var4 = var3.rawQuery(var2, new String[]{var1});
            int var5 = 0;
            if(var4.moveToNext()) {
                var5 = var4.getInt(0);
            }

            var3.setTransactionSuccessful();
            int var6 = var5;
            return var6;
        } catch (Exception var10) {
            var10.printStackTrace();
        } finally {
            var3.endTransaction();
            this.closeDatabase(var3, var4);
        }

        return 0;
    }

    public int deleteAll() {
        return this.delete((String)null, (String[])null);
    }

    //String whereClause, String[] whereArgs
    public int delete(String whereClause, String[] whereArgs) {
        SQLiteDatabase var3 = this.openWriter();

        try {
            var3.beginTransaction();
            int var4 = var3.delete(this.getTableName(), whereClause, whereArgs);
            var3.setTransactionSuccessful();
            int var5 = var4;
            return var5;
        } catch (Exception var9) {
            var9.printStackTrace();
        } finally {
            var3.endTransaction();
            this.closeDatabase(var3, (Cursor)null);
        }

        return 0;
    }

    public List<T> getAll() {
        return this.get((String)null, (String[])null);
    }

    public List<T> get(String selection, String[] selectionArgs) {
        return this.get((String[])null, selection, selectionArgs, (String)null, (String)null, (String)null, (String)null);
    }

    public List<T> getOnlyOne(String selection, String[] selectionArgs) {
        return this.get((String[])null, selection, selectionArgs, (String)null, (String)null, (String)null, "1");
    }

    //String table, String[] columns, String selection,String[] selectionArgs, String groupBy, String having, String orderBy, String limit
    public List<T> get(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        SQLiteDatabase var8 = this.openReader();
        ArrayList var9 = new ArrayList();
        Cursor var10 = null;

        try {
            var8.beginTransaction();
            var10 = var8.query(this.getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);

            while(!var10.isClosed() && var10.moveToNext()) {
                var9.add(this.parseCursorToBean(var10));
            }

            var8.setTransactionSuccessful();
        } catch (Exception var15) {
            ;
        } finally {
            var8.endTransaction();
            this.closeDatabase(var8, var10);
        }

        return var9;
    }

    public long replace(T var1) {
        SQLiteDatabase var2 = this.openWriter();

        try {
            var2.beginTransaction();
            long var3 = var2.replace(this.getTableName(), (String)null, this.getContentValues(var1));
            var2.setTransactionSuccessful();
            long var5 = var3;
            return var5;
        } catch (Exception var10) {
            var10.printStackTrace();
        } finally {
            var2.endTransaction();
            this.closeDatabase(var2, (Cursor)null);
        }

        return 0L;
    }

    public long create(T var1) {
        SQLiteDatabase var2 = this.openWriter();

        try {
            var2.beginTransaction();
            long var3 = var2.insert(this.getTableName(), (String)null, this.getContentValues(var1));
            var2.setTransactionSuccessful();
            long var5 = var3;
            return var5;
        } catch (Exception var10) {
            var10.printStackTrace();
        } finally {
            var2.endTransaction();
            this.closeDatabase(var2, (Cursor)null);
        }

        return 0L;
    }

    public int update(T var1, String var2, String[] var3) {
        SQLiteDatabase var4 = this.openWriter();

        try {
            var4.beginTransaction();
            int var5 = var4.update(this.getTableName(), this.getContentValues(var1), var2, var3);
            var4.setTransactionSuccessful();
            int var6 = var5;
            return var6;
        } catch (Exception var10) {
            var10.printStackTrace();
        } finally {
            var4.endTransaction();
            this.closeDatabase(var4, (Cursor)null);
        }

        return 0;
    }

    public abstract T parseCursorToBean(Cursor var1);

    public abstract ContentValues getContentValues(T var1);

   //  public abstract String getnullColumnHack();
}