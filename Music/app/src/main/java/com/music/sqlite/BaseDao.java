package com.music.sqlite;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.DatabaseConnection;

import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 数据库CRUD操作的Dao，子类继承实现抽象方法
 * Created by dingfeng on 2016/4/12.
 */
public abstract class BaseDao<T, Integer> {

    protected Context mContext;
    protected DatabaseHelper mDatabaseHelper;

    public BaseDao(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context can't be null!");
        }
        mContext = context.getApplicationContext();
        mDatabaseHelper = DatabaseHelper.getDatabaseHelper(mContext);
    }

    public abstract Dao<T, Integer> getDao() throws SQLException;

    /**************************************** 保存 ******************************************************/

    /**
     * 保存一条记录
     *
     * @param t
     * @throws SQLException
     */
    public void save(T t) {
        try {
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                dao.create(t);
                dao.commit(databaseConnection);
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存一组数据
     *
     * @param list
     * @throws SQLException
     */
    public void saveList(List<T> list) {
        if (list.size() <= 0) return;
        try {
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                for (T t : list) {
                    dao.create(t);
                }
                dao.commit(databaseConnection);
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**************************************** 更新 ******************************************************/

    /**
     * 更新一条记录
     *
     * @param t
     * @throws SQLException
     */
    public void update(T t) {
        try {
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                dao.update(t);
                dao.commit(databaseConnection);
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据指定条件和指定字段更新记录
     *
     * @param key
     * @param value
     * @param columnNames
     * @param columnValues
     * @throws SQLException
     */
    public void update(String key, Object value, String[] columnNames, Object[] columnValues) {
        if (columnNames.length != columnNames.length) {
            throw new InvalidParameterException("params size is not equal");
        }
        try {
            Dao<T, Integer> dao = getDao();
            UpdateBuilder<T, Integer> updateBuilder = dao.updateBuilder();
            updateBuilder.where().eq(key, value);
            for (int i = 0; i < columnNames.length; i++) {
                updateBuilder.updateColumnValue(columnNames[i], columnValues[i]);
            }
            PreparedUpdate<T> prepareUpdate = updateBuilder.prepare();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                dao.update(prepareUpdate);
                // also can use dao.queryForEq(columnName,columnValue);
                dao.commit(databaseConnection);
            } catch (SQLException e) {
                Log.d("dingfeng","222");
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                Log.d("dingfeng","3333");
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            Log.d("dingfeng","1111");
            e.printStackTrace();
        }
    }

    /**
     * 根据指定条件和指定字段更新记录
     *
     * @param key
     * @param value
     * @param columnNames
     * @param columnValues
     * @throws SQLException
     */
    public void update(String[] key, Object[] value, String[] columnNames, Object[] columnValues) {
        if (columnNames.length != columnNames.length) {
            throw new InvalidParameterException("params size is not equal");
        }
        try {
            Dao<T, Integer> dao = getDao();
            UpdateBuilder<T, Integer> updateBuilder = dao.updateBuilder();
            Where<T, Integer> where = updateBuilder.where();
            for (int i = 0; i < key.length; i++) {
                if (i == 0) {
                    where.eq(key[i], value[i]);
                } else {
                    where.and().eq(key[i], value[i]);
                }
            }
            for (int i = 0; i < columnNames.length; i++) {
                updateBuilder.updateColumnValue(columnNames[i], columnValues[i]);
            }
            PreparedUpdate<T> prepareUpdate = updateBuilder.prepare();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                dao.update(prepareUpdate);
                // also can use dao.queryForEq(columnName,columnValue);
                dao.commit(databaseConnection);
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据PreparedUpdate更新记录
     *
     * @param preparedUpdate
     * @throws SQLException
     */
    public void update(PreparedUpdate<T> preparedUpdate) {
        try {
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                dao.update(preparedUpdate);
                dao.commit(databaseConnection);
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**************************************** 保存或更新  ******************************************************/

    /**
     * 记录不存在则保持否则更新
     *
     * @param t
     * @throws SQLException
     */
    public void saveOrUpdate(T t) {
        try {
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                dao.createOrUpdate(t);
                dao.commit(databaseConnection);
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savaOrUpdate(List<T> list) {
        if (list.size() <= 0) return;
        Dao<T, Integer> dao = null;
        try {
            dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                for (T t : list) {
//                    dao.createOrUpdate(t);
                    dao.createIfNotExists(t);
                }
                dao.commit(databaseConnection);
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**************************************** 删除操作 ******************************************************/

    /**
     * 删除一条记录
     *
     * @param t
     * @throws SQLException
     */
    public void deleteEntity(T t) {
        try {
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                dao.delete(t);
                dao.commit(databaseConnection);
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定的记录
     *
     * @param list
     * @throws SQLException
     */
    public void deleteList(List<T> list) {
        if (list.size() <= 0) return;
        try {
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                dao.delete(list);
                dao.commit(databaseConnection);
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据条件删除满足条件的所有记录
     *
     * @param columnName
     * @param columnValue
     * @throws SQLException
     * @throws InvalidParameterException
     */
    public void deleteList(String columnName, Object columnValue) {
        try {
            List<T> list = queryList(columnName, columnValue);
            if (null != list && !list.isEmpty()) {
                Dao<T, Integer> dao = getDao();
                DatabaseConnection databaseConnection = null;
                try {
                    databaseConnection = dao.startThreadConnection();
                    dao.setAutoCommit(databaseConnection, false);
                    dao.delete(list);
                    dao.commit(databaseConnection);
                } catch (SQLException e) {
                    dao.rollBack(databaseConnection);
                    e.printStackTrace();
                } finally {
                    dao.endThreadConnection(databaseConnection);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据条件组合删除所有满足条件的记录
     *
     * @param columnNames
     * @param columnValues
     * @throws SQLException
     * @throws InvalidParameterException
     */
    public void deleteList(String[] columnNames, Object[] columnValues) {
        try {
            List<T> list = queryList(columnNames, columnValues);
            if (null != list && !list.isEmpty()) {
                Dao<T, Integer> dao = getDao();
                DatabaseConnection databaseConnection = null;
                try {
                    databaseConnection = dao.startThreadConnection();
                    dao.setAutoCommit(databaseConnection, false);
                    dao.delete(list);
                    dao.commit(databaseConnection);
                } catch (SQLException e) {
                    dao.rollBack(databaseConnection);
                    e.printStackTrace();
                } finally {
                    dao.endThreadConnection(databaseConnection);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据单个条件删除一条记录
     *
     * @param columnName
     * @param columnValue
     * @throws SQLException
     */
    public void deleteEntity(String columnName, Object columnValue) {
        T t = queryEntity(columnName, columnValue);
        deleteEntity(t);
    }

    /**
     * 根据条件组合删除一条记录
     *
     * @param columnNames
     * @param columnValues
     * @throws SQLException
     */
    public void deleteEntity(String[] columnNames, Object[] columnValues) {
        T t = queryEntity(columnNames, columnValues);
        deleteEntity(t);
    }

    /**
     * 根据id删除记录
     *
     * @param id
     * @throws SQLException
     */
    public void deleteEntityById(Integer id) {
        try {
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                dao.deleteById(id);
                dao.commit(databaseConnection);
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id数组删除一组记录
     *
     * @param ids
     * @throws SQLException
     */
    public void deleteByIds(List<Integer> ids) {
        try {
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                dao.deleteIds(ids);
                dao.commit(databaseConnection);
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据PreparedDelete删除记录
     *
     * @param preparedDelete
     * @throws SQLException
     */
    public void delete(PreparedDelete<T> preparedDelete) {
        try {
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                dao.delete(preparedDelete);
                dao.commit(databaseConnection);
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除表中的所有记录
     *
     * @throws SQLException
     */
    public void deleteAll() {
        try {
            List<T> list = queryAll();
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                dao.delete(list);
                dao.commit(databaseConnection);
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**************************************** 查询操作 ******************************************************/

    /**
     * 根据PreparedQuery查询所有记录
     *
     * @param preparedQuery
     * @return
     * @throws SQLException
     */
    public List<T> queryList(PreparedQuery<T> preparedQuery) {
        try {
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                List<T> query = dao.query(preparedQuery);
                dao.commit(databaseConnection);
                return query;
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据单个条件查询所有满足条件的记录
     *
     * @param columnName
     * @param columnValue
     * @return
     * @throws SQLException
     */
    public List<T> queryList(String columnName, Object columnValue) {
        try {
            Dao<T, Integer> dao = getDao();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq(columnName, columnValue);
            PreparedQuery<T> preparedQuery = queryBuilder.prepare();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                List<T> query = dao.query(preparedQuery);
                //also can use dao.queryForEq(columnName,columnValue);
                dao.commit(databaseConnection);
                return query;
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据条件组合查询所有满足条件的记录
     *
     * @param columnNames
     * @param columnValues
     * @return
     * @throws SQLException
     */
    public List<T> queryList(String[] columnNames, Object[] columnValues) {
        if (columnNames.length != columnNames.length) {
            throw new InvalidParameterException("params size is not equal");
        }
        try {
            Dao<T, Integer> dao = getDao();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            Where<T, Integer> wheres = queryBuilder.where();
            for (int i = 0; i < columnNames.length; i++) {
                if (i == 0) {
                    wheres.eq(columnNames[i], columnValues[i]);
                } else {
                    wheres.and().eq(columnNames[i], columnValues[i]);
                }
            }
            PreparedQuery<T> preparedQuery = queryBuilder.prepare();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                List<T> query = dao.query(preparedQuery);
                dao.commit(databaseConnection);
                return query;
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据键值对查询所有满足条件的记录
     *
     * @param map
     * @return
     * @throws SQLException
     */
    public List<T> queryList(Map<String, Object> map) {
        try {
            Dao<T, Integer> dao = getDao();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            if (!map.isEmpty()) {
                Where<T, Integer> wheres = queryBuilder.where();
                Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
                String key = null;
                Object value = null;
                for (int i = 0; iterator.hasNext(); i++) {
                    Map.Entry<String, Object> next = iterator.next();
                    key = next.getKey();
                    value = next.getValue();
                    if (i == 0) {
                        wheres.eq(key, value);
                    } else {
                        wheres.and().eq(key, value);
                    }
                }
            }
            PreparedQuery<T> preparedQuery = queryBuilder.prepare();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                List<T> query = dao.query(preparedQuery);
                dao.commit(databaseConnection);
                return query;
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     * @throws SQLException
     */
    public T queryById(Integer id) {
        try {
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                T t = dao.queryForId(id);
                dao.commit(databaseConnection);
                return t;
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据单个条件查询记录
     *
     * @param columnName
     * @param columnValue
     * @return
     * @throws SQLException
     */
    public T queryEntity(String columnName, Object columnValue) {
        try {
            Dao<T, Integer> dao = getDao();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq(columnName, columnValue);
            PreparedQuery<T> preparedQuery = queryBuilder.prepare();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                T t = dao.queryForFirst(preparedQuery);
                //also can use dao.queryForEq(columnName,columnValue);
                dao.commit(databaseConnection);
                return t;
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据条件组合查询单个记录
     *
     * @param columnNames
     * @param columnValues
     * @return
     * @throws SQLException
     */
    public T queryEntity(String[] columnNames, Object[] columnValues) {
        if (columnNames.length != columnNames.length) {
            throw new InvalidParameterException("params size is not equal");
        }
        try {
            QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder();
            Where<T, Integer> wheres = queryBuilder.where();
            for (int i = 0; i < columnNames.length; i++) {
                if (i == 0) {
                    wheres.eq(columnNames[i], columnValues[i]);
                } else {
                    wheres.and().eq(columnNames[i], columnValues[i]);
                }
            }
            PreparedQuery<T> preparedQuery = queryBuilder.prepare();
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                T t = dao.queryForFirst(preparedQuery);
                dao.commit(databaseConnection);
                return t;
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询所有记录
     *
     * @return 所有记录
     * @throws SQLException
     */
    public T queryFirst() {
        try {
            Dao<T, Integer> dao = getDao();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            PreparedQuery<T> preparedQuery = queryBuilder.prepare();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                T t = dao.queryForFirst(preparedQuery);
                dao.commit(databaseConnection);
                return t;
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 条件查询
     *
     * @param offset      偏移量
     * @param limit       查询数量
     * @param columnName
     * @param columnValue
     * @return
     */
    @SuppressWarnings("deprecation")
    public List<T> queryAll(int offset, int limit, String columnName, Object columnValue) {
        try {
            Dao<T, Integer> dao = getDao();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.offset(offset)
                    .limit(limit)
                    .where().eq(columnName, columnValue);
            PreparedQuery<T> preparedQuery = queryBuilder.prepare();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                List<T> query = dao.query(preparedQuery);
                dao.commit(databaseConnection);
                return query;
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 条件查询
     *
     * @param offset 偏移量
     * @param limit  查询数量
     * @return
     */
    @SuppressWarnings("deprecation")
    public List<T> queryAll(int offset, int limit) {
        try {
            Dao<T, Integer> dao = getDao();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.offset(offset)
                    .limit(limit);
            PreparedQuery<T> preparedQuery = queryBuilder.prepare();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                List<T> query = dao.query(preparedQuery);
                dao.commit(databaseConnection);
                return query;
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 条件查询
     *
     * @param offset 偏移量
     * @param limit  查询数量
     * @return
     */
    @SuppressWarnings("deprecation")
    public List<T> queryAll(int offset, int limit, String order, boolean ascending) {
        try {
            Dao<T, Integer> dao = getDao();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(order, ascending)
                    .offset(offset)
                    .limit(limit);
            PreparedQuery<T> preparedQuery = queryBuilder.prepare();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                List<T> query = dao.query(preparedQuery);
                dao.commit(databaseConnection);
                return query;
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询所有记录
     *
     * @return 所有记录
     * @throws SQLException
     */
    public List<T> queryAll() {
        try {
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                List<T> query = dao.queryForAll();
                dao.commit(databaseConnection);
                return query;
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**************************************** 其他操作 ******************************************************/

    /**
     * 判读表是否存在
     *
     * @return 表是否存在
     * @throws SQLException
     */
    public boolean isTableExists() {
        try {
            return getDao().isTableExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获得记录数
     *
     * @return 记录数
     * @throws SQLException
     */
    public long count() {
        try {
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                long count = dao.countOf();
                dao.commit(databaseConnection);
                return count;
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获得记录数
     *
     * @param preparedQuery
     * @return 记录数
     * @throws SQLException
     */
    public long count(PreparedQuery<T> preparedQuery) {
        try {
            Dao<T, Integer> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);

                long count = dao.countOf(preparedQuery);
                dao.commit(databaseConnection);
                return count;
            } catch (SQLException e) {
                dao.rollBack(databaseConnection);
                e.printStackTrace();
            } finally {
                dao.endThreadConnection(databaseConnection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

