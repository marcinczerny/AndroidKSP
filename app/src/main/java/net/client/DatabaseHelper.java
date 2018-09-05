package net.client;

import android.support.v7.app.AppCompatActivity;

import net.client.Database.DaoSession;
import net.client.Database.Measure;

import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper extends AppCompatActivity {

    private DaoSession daoSession;

    public void initialize(DaoSession mDaoSession) {

        this.daoSession = mDaoSession;

        /*if(daoSession.getMeasureDao().loadAll().size() == 0) {

            java.util.Date time = new java.util.Date();
            daoSession.getMeasureDao().insert(new Measure(1L,time.getTime(), 0.0f,5.0f,-70f));
            daoSession.getMeasureDao().insert(new Measure(2L,time.getTime()+10000,10.0f,100.0f,0.0f));
            daoSession.getMeasureDao().insert(new Measure(3L,time.getTime()+20000,15.0f,200.0f,36.6f));
            daoSession.getMeasureDao().insert(new Measure(4L,time.getTime()+30000,20.0f,300.0f,73.2f));
        }*/

    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
    public void setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
    }
}
