package com.wayto.track.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import com.wayto.track.storage.TrackPointTableDao;
import com.wayto.track.storage.TrackTableDao;

/**
 * author: hezhiWu <hezhi.woo@gmail.com>
 * version: V1.0
 * created at 2017/11/21 14:07
 * <p>
 * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public final class TrackData {
    private final String TAG = "TrackData";


    /**
     * 足迹表
     * <p>
     * author: hezhiWu <hezhi.woo@gmail.com>
     * version: V1.0
     * created at 2017/11/21 14:39
     * <p>
     * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
     */
    public static final class Track {
        public static final String AUTHORITY = "com.wayto.independent.track";

        private static final String CONTENT_AUTHORITY_SLASH = "content://" + AUTHORITY + "/";

        public static final String TABLE_NAME = TrackTableDao.TABLENAME;

        public static final Uri TRACK_CONTENT_URI = Uri.parse(CONTENT_AUTHORITY_SLASH + TABLE_NAME);

        public interface TrackColumns extends BaseColumns {

            String KEY = TrackTableDao.Properties.Key.columnName;

            String START_TIME = TrackTableDao.Properties.StartTime.columnName;

            String FINISH_TIME = TrackTableDao.Properties.FinishTime.columnName;

            String DURATION=TrackTableDao.Properties.Duration.columnName;

            String START_LONGITUDE = TrackTableDao.Properties.StartLongitude.columnName;

            String FINISH_LONGITUDE = TrackTableDao.Properties.FinishLongitude.columnName;

            String START_LATITUDE = TrackTableDao.Properties.StartLatitude.columnName;

            String FINISH_LATITUDE = TrackTableDao.Properties.FinishLatitude.columnName;

            String DISTANCE = TrackTableDao.Properties.Distance.columnName;

            String SPEED = TrackTableDao.Properties.Speed.columnName;

            String STATUS = TrackTableDao.Properties.Status.columnName;

            String REMARK = TrackTableDao.Properties.Remark.columnName;

            String REV_STR1 = TrackTableDao.Properties.RevStr1.columnName;

            String REV_STR2 = TrackTableDao.Properties.RevStr2.columnName;

            String REV_STR3 = TrackTableDao.Properties.RevStr3.columnName;

            String REV_STR4 = TrackTableDao.Properties.RevStr4.columnName;

            String REV_INT1 = TrackTableDao.Properties.RevInt1.columnName;

            String REV_INT2 = TrackTableDao.Properties.RevInt2.columnName;

            String REV_INT3 = TrackTableDao.Properties.RevInt3.columnName;

            String REV_INT4 = TrackTableDao.Properties.RevInt4.columnName;
        }
    }

    /**
     * 足迹点位
     * <p>
     * author: hezhiWu <hezhi.woo@gmail.com>
     * version: V1.0
     * created at 2017/11/21 14:50
     * <p>
     * Copyright (c) 2017 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
     */
    public static final class TrackPoint {
        public static final String AUTHORITY = "com.wayto.independent.trackPoint";

        private static final String CONTENT_AUTHORITY_SLASH = "content://" + AUTHORITY + "/";

        public static final String TABLE_NAME = TrackPointTableDao.TABLENAME;

        public static final Uri TRACK_POINT_CONTENT_URI = Uri.parse(CONTENT_AUTHORITY_SLASH + TABLE_NAME);

        public interface TrackPointColums extends BaseColumns {

            String TARCK_TABLE_ID = TrackPointTableDao.Properties.TarckTableId.columnName;

            String LONGITUDE = TrackPointTableDao.Properties.Longitude.columnName;

            String LATITUDE = TrackPointTableDao.Properties.Latitude.columnName;

            String ALTITUDE = TrackPointTableDao.Properties.Altitude.columnName;

            String SPEED = TrackPointTableDao.Properties.Speed.columnName;

            String ACCURACY = TrackPointTableDao.Properties.Accuracy.columnName;

            String SOURCE = TrackPointTableDao.Properties.Source.columnName;

            String PROVIDER = TrackPointTableDao.Properties.Provider.columnName;

            String ADDRESS = TrackPointTableDao.Properties.Address.columnName;

            String SIGNAL = TrackPointTableDao.Properties.Signal.columnName;

            String LOCATION_TIME = TrackPointTableDao.Properties.LocationTime.columnName;

            String REV_STR1 = TrackPointTableDao.Properties.RevStr1.columnName;

            String REV_STR2 = TrackPointTableDao.Properties.RevStr2.columnName;

            String REV_STR3 = TrackPointTableDao.Properties.RevStr3.columnName;

            String REV_STR4 = TrackPointTableDao.Properties.RevStr4.columnName;

            String REV_INT1 = TrackPointTableDao.Properties.RevInt1.columnName;

            String REV_INT2 = TrackPointTableDao.Properties.RevInt2.columnName;

            String REV_INT3 = TrackPointTableDao.Properties.RevInt3.columnName;

            String REV_INT4 = TrackPointTableDao.Properties.RevInt4.columnName;
        }
    }
}
