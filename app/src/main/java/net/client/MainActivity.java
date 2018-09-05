package net.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.client.Database.DaoMaster;
import net.client.Database.DaoSession;
import net.client.TabLayout.TabLayoutFragment;

public class MainActivity extends AppCompatActivity {

    private DaoSession daoSession;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        daoSession = new DaoMaster(new DaoMaster.DevOpenHelper(this, "net.client.db").getWritableDb()).newSession();
        databaseHelper = new DatabaseHelper();
        databaseHelper.initialize(daoSession);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TabLayoutFragment()).commit();

    }

    public DatabaseHelper getDatabaseHelper() {
        return this.databaseHelper;
    }
}