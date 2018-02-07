package com.wayto.track.storage;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.wayto.track.storage.TemTable;
import com.wayto.track.storage.TrackPointTable;
import com.wayto.track.storage.TrackTable;

import com.wayto.track.storage.TemTableDao;
import com.wayto.track.storage.TrackPointTableDao;
import com.wayto.track.storage.TrackTableDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig temTableDaoConfig;
    private final DaoConfig trackPointTableDaoConfig;
    private final DaoConfig trackTableDaoConfig;

    private final TemTableDao temTableDao;
    private final TrackPointTableDao trackPointTableDao;
    private final TrackTableDao trackTableDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        temTableDaoConfig = daoConfigMap.get(TemTableDao.class).clone();
        temTableDaoConfig.initIdentityScope(type);

        trackPointTableDaoConfig = daoConfigMap.get(TrackPointTableDao.class).clone();
        trackPointTableDaoConfig.initIdentityScope(type);

        trackTableDaoConfig = daoConfigMap.get(TrackTableDao.class).clone();
        trackTableDaoConfig.initIdentityScope(type);

        temTableDao = new TemTableDao(temTableDaoConfig, this);
        trackPointTableDao = new TrackPointTableDao(trackPointTableDaoConfig, this);
        trackTableDao = new TrackTableDao(trackTableDaoConfig, this);

        registerDao(TemTable.class, temTableDao);
        registerDao(TrackPointTable.class, trackPointTableDao);
        registerDao(TrackTable.class, trackTableDao);
    }
    
    public void clear() {
        temTableDaoConfig.clearIdentityScope();
        trackPointTableDaoConfig.clearIdentityScope();
        trackTableDaoConfig.clearIdentityScope();
    }

    public TemTableDao getTemTableDao() {
        return temTableDao;
    }

    public TrackPointTableDao getTrackPointTableDao() {
        return trackPointTableDao;
    }

    public TrackTableDao getTrackTableDao() {
        return trackTableDao;
    }

}
