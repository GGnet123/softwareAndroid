package froot.courierservice.db;

import android.content.Context;
import android.provider.SyncStateContract;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = User.class, version = 1)
public abstract class UserDb extends RoomDatabase {
    public abstract UserDao getUserDao();
    private static UserDb userDb;
    public static UserDb getInstance(Context context){
        if (null == userDb){
            userDb = buildDatabasInstance(context);
        }
        return userDb;
    }

    private static UserDb buildDatabasInstance(Context context){
        return Room.databaseBuilder(context, UserDb.class, "User").build();
    }
    public void cleanUp(){
        userDb = null;
    }
}
