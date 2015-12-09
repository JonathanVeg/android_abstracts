
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.laivon.street.abstracts.Globals;

public class DBTools {

    /*
    *
    * Classe criada para melhorar e facilitar o uso do banco de dados em aplicações.
    *
    * A classe CreateDatabase chamada no construtor é a classe que extende a SQLiteOpenHelper e, basicamente, serve para criar a atualizar o banco de dados
    *
    * */

    CreateDatabase dbCreate;
    SQLiteDatabase db;
    Cursor dbCursor;
    String _lastError = null;

    public DBTools(Context context, String dbName) {
        dbCreate = new CreateDatabase(context, dbName);
    }


    public DBTools(Context context) {
        this(context, Globals.dbName);
    }

    public boolean exec(String sql) {
        try {
            open(false);
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            _lastError = e.toString();

            Log.e("SQL ERROR", _lastError);

            return false;
        } finally {
            close();
        }
    }

    public boolean open(boolean isRead) {
        try {
            db = dbCreate.getDatabase(isRead);
            return true;
        } catch (Exception e) {
            _lastError = e.toString();
            return false;
        }
    }

    public boolean close() {
        try {
            db.close();
            return true;
        } catch (Exception e) {
            _lastError = e.toString();
            return false;
        }
    }

    public boolean insert(String table, ContentValues values) {
        try {
            open(false);
            db.insert(table, null, values);
            close();
            return true;
        } catch (Exception e) {
            _lastError = e.toString();
            return false;
        } finally {
            close();
        }
    }

    public boolean update(String table, ContentValues values,
                          String whereClause, String[] whereArgs) {
        try {
            open(false);
            db.update(table, values, whereClause, whereArgs);
            close();
            return true;
        } catch (Exception e) {
            _lastError = e.toString();
            return false;
        } finally {
            close();
        }
    }

    public boolean remove(String table, String whereClause, String[] whereArgs) {
        try {
            open(false);
            db.delete(table, whereClause, whereArgs);
            close();
            return true;
        } catch (Exception e) {
            _lastError = e.toString();
            return false;
        } finally {
            close();
        }
    }

    public int search(boolean isDistinct, String table, String[] columns) {
        try {
            open(true);

            dbCursor = db.query(isDistinct, table, columns, null, null, null,
                    null, null, null, null);

            return dbCursor.getCount();

        } catch (Exception e) {
            _lastError = e.toString();
            return -1;
        } finally {
            close();
        }
    }

    public int search(String sql) {
        try {
            open(true);

            dbCursor = db.rawQuery(sql, null);

            return dbCursor.getCount();

        } catch (Exception e) {
            _lastError = e.toString();
            return -1;
        } finally {
            close();
        }
    }

    public int getRowLength(int row) {
        dbCursor.moveToPosition(row);

        return dbCursor.getColumnCount();
    }


    public String getData(int record, int column) {
        try {
            dbCursor.moveToPosition(record);

            return dbCursor.getString(column);
        } catch (Exception e) {
            _lastError = e.toString();
            return "";
        }
    }

    public String getData(int column) {
        try {
            dbCursor.moveToPosition(0);

            return dbCursor.getString(column);
        } catch (Exception e) {
            _lastError = e.toString();
            return "";
        }
    }

    public String getError() {
        return _lastError;
    }

    public void clearDatabase() {
        int count = search("SELECT name FROM sqlite_master WHERE type='table'");

        String table;
        for (int i = 0; i < count; i++) {
            table = getData(i, 0);

            if (!table.equals("android_metadata")) {
                exec("delete from " + table);
            }
        }
    }
}
