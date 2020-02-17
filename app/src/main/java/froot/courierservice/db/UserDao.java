package froot.courierservice.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);

    @Query("delete from User")
    public void clearDb();

    @Query("select * from User")
    List<User> getUsers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);
}
