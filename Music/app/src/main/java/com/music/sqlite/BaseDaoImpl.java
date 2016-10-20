package com.music.sqlite;

/**
 * Created by dingfeng on 2016/4/12.
 */

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**BaseDao泛型实现类
 * @author dingfeng
 * @param <T>
 * @param <Integer>
 */
public class BaseDaoImpl<T, Integer> extends BaseDao<T, Integer> {

    private Class<T> clazz;
    private Map<Class<T>, Dao<T, Integer>> mDaoMap = new HashMap<Class<T>, Dao<T, Integer>>();

    public BaseDaoImpl(Context context, Class<T> clazz) {
        super(context);
        // TODO Auto-generated constructor stub
        this.clazz = clazz;
    }

    @Override
    public Dao<T, Integer> getDao() throws SQLException {
        // TODO Auto-generated method stub
        Dao<T, Integer> dao = mDaoMap.get(clazz);
        if (null == dao) {
            dao = mDatabaseHelper.getDao(clazz);
            mDaoMap.put(clazz, dao);
        }
        return dao;
    }

}
