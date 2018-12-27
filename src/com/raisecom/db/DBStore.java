package com.raisecom.db;

import com.raisecom.common.logging.Logger;
import com.raisecom.nms.util.DBConnectionManager;
import com.raisecom.nms.util.ErrorObjService;
import com.raisecom.util.EPONConstants;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ligy-008494 on 2018/11/13.
 */
public class DBStore {
    private Logger logger = EPONConstants.logger;

    public DBStore() {
    }

    /**
     * 执行sql语句：添加
     *
     * @param sql
     *            : sql语句
     * @return true: 成功 false: 失败
     */
    public ErrorObjService execteBatchAddSql(String TABLE_NAME, List<String> sqlVector, int startNumber) {

        ErrorObjService resOperationResult = new ErrorObjService();
        resOperationResult.setErrCode("0");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        long start1L = System.currentTimeMillis();
        String start1_format = sdf.format(new Date(start1L));
        logger.debug(this.getClass().getSimpleName() + " >begin time: " + start1_format);
        logger.debug("sql: " + sqlVector);

        int sqlVectorSize = sqlVector.size();
        int sqlVSize = sqlVectorSize + startNumber;
        logger.info("Execute Add " + TABLE_NAME
                + " Table Operator Begin Time: (" + start1_format
                + ") ,Add Sqls StartStepNumber Is: " + startNumber
                + ", Sqls Count Is: " + sqlVectorSize + ".");

        Statement st = null;
        Connection conn = null;
        int updateCount = 0;

        try {
            conn = DBConnectionManager.getConnection();
            st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            String aSql = "";
            for (int i = 0; i < sqlVectorSize; i++) {
                aSql = sqlVector.get(i);
                st.addBatch(aSql);
                int j = i + 1 + startNumber;
                logger.debug("Execute Add " + TABLE_NAME
                        + " Table Rows Operator,SqlsStartStepNumber Is:"
                        + startNumber + ",Sql(" + j + "/" + sqlVSize + "): "
                        + aSql + ".");
            }
            st.executeBatch();
            updateCount = st.getUpdateCount();
        } catch (SQLException e) {
            resOperationResult.setErrCode("10001");
            logger.error(e);
        } finally {
            DBConnectionManager.getInstance().free(conn, st, null);
            String end2_format = sdf.format(new Date());
            long end2L = System.currentTimeMillis();
            long used2L = end2L - start1L;
            logger.debug("Execute Add " + TABLE_NAME
                    + " Table Rows Sql End Time: (" + end2_format
                    + "),used time: (" + used2L + "),SqlStartStepNumber Is: "
                    + startNumber + ",UpdateCount: (" + updateCount + ").");
        }
        return resOperationResult;
    }

    public ErrorObjService execteBatchAddSql(List<String> sqlVector,
                                             int startNumber) {
        return execteBatchSql(sqlVector, startNumber, "Add");
    }

    public ErrorObjService execteBatchDelSql(List<String> sqlVector,
                                             int startNumber) {
        return execteBatchSql(sqlVector, startNumber, "Del");
    }

    public ErrorObjService execteBatchUpdateSql(List<String> sqlVector,
                                                int startNumber) {
        return execteBatchSql(sqlVector, startNumber, "Update");
    }

    /**
     * 执行sql语句：添加、删除、修改
     *
     * @param sql
     *            : sql语句
     * @return true: 成功 false: 失败
     */
    public ErrorObjService execteBatchSql(List<String> sqlVector,
                                          int startNumber, String operatorFlag) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        long start1L = System.currentTimeMillis();
        String start1_format = sdf.format(new Date(start1L));
        logger.debug(this.getClass().getSimpleName() + " >begin time: " + start1_format);
        logger.debug("sql: " + sqlVector);
        ErrorObjService resOperationResult = new ErrorObjService();
        resOperationResult.setErrCode("0");

        int sqlVectorSize = sqlVector.size();
        int sqlVSize = sqlVectorSize + startNumber;

        Statement st = null;
        Connection conn = null;
        int updateCount = 0;

        try {
            conn = DBConnectionManager.getConnection();
            st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            String aSql = "";
            for (int i = 0; i < sqlVectorSize; i++) {
                aSql = sqlVector.get(i);
                st.addBatch(aSql);
                int j = i + 1 + startNumber;
                logger.debug("Execute " + operatorFlag
                        + " Table Rows Operator,SqlsStartStepNumber Is:"
                        + startNumber + ",Sql(" + j + "/" + sqlVSize + "): "
                        + aSql + ".");
            }
            st.executeBatch();
            updateCount = st.getUpdateCount();
        } catch (SQLException e) {
            resOperationResult.setErrCode("10001");
            logger.error(e);
        } finally {
            DBConnectionManager.getInstance().free(conn, st, null);
            String end2_format = sdf.format(new Date());
            long end2L = System.currentTimeMillis();
            long used2L = end2L - start1L;
            logger.debug("Execute " + operatorFlag
                    + " Table Rows Sql End Time: (" + end2_format
                    + "),used time: (" + used2L + "),SqlStartStepNumber Is: "
                    + startNumber + ",UpdateCount: (" + updateCount + ").");
        }
        return resOperationResult;
    }
}
