package com.raisecom.util;

import com.raisecom.boot.EponGlobalVariablesInit;
import com.raisecom.common.logging.LogFactory;
import com.raisecom.common.logging.Logger;
import com.raisecom.db.DBStore;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.nms.securitymgt.client.util.SecurityManagerCenter;
import com.raisecom.nms.securitymgt.client.util.SecurityUtils;
import com.raisecom.nms.util.DBConnectionManager;
import com.raisecom.nms.util.ErrorObjService;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by ligy-008494 on 2018/11/13.
 */
public class EPONCommonDBUtil {

    private static EPONCommonDBUtil util = null;
    private Logger logger = LogFactory.getLogger("EPON");
    public synchronized static EPONCommonDBUtil getInstance() {
        if (util == null)
            util = new EPONCommonDBUtil();
        return util;
    }

    /**
     * 从数据库中获取某个表的数据，条件为condition
     */
    public ObjService getAllObjServicesFromDB(String columns, String tableName, String condition) throws Exception {
        String sql = "SELECT "+columns+" FROM " + tableName + " WHERE " + condition;
        if(tableName == null || tableName.equals("") ){
            return null;
        }
        if (columns == null || columns.equals("")) {
            if(condition == null || condition.equals("")){
                sql = "SELECT * FROM " + tableName;
            }else{
                sql = "SELECT * FROM " + tableName + " WHERE " + condition;
            }
        }else{
            if(condition == null || condition.equals("")){
                sql = "SELECT "+columns+" FROM " + tableName;
            }
        }
        return selectObject(sql, null);
    }

    /**
     *
     * @param sql
     * @param params
     *            字符串数组，里面是PreparedStatement的参数，必须按照顺序来填写。
     * @return 返回ObjService的结构和SlientDbOperator返回的结构一致
     * @throws Exception
     */
    public ObjService selectObject(String sql, String[] params) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; params != null && i < params.length; i++) {
                pstmt.setString(i + 1, params[i]);
            }
            rs = pstmt.executeQuery();
            ObjService result = new ObjService();
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                ObjService row = new ObjService("RowObj");
                int size = meta.getColumnCount();
                for (int i = 1; i <= size; i++) {
                    String columnName = meta.getColumnName(i);
                    row.setValue(columnName.toUpperCase(), rs.getString(columnName));
                }
                result.addContainedObject(row);
            }
            return result;
        } catch (Exception e) {
            logger.error("Execute sql : " + sql + " fail!!!");
            throw e;
        } finally {
            DBConnectionManager.getInstance().free(conn, pstmt, rs);
        }
    }

    /**
     * 返回的属性只有关于snmp参数的属性。
     *
     * @param neId
     * @return
     */
    public ObjService getNePropertyForSnmpParametersFromDB(String neId) {
        try {
            String sql = "SELECT iRCNETypeID, READCOM, WRITECOM, IRCNETNODEID, VERS, TIMEOUT, PORT, IPADDRESS, RETRY FROM RCNETNODE WHERE IRCNETNODEID=" + neId;
            return selectObject(sql, null).objectAt("RowObj", 0);
        } catch (Exception e) {
            logger.info("Get node " + neId + " info from db fail!!!");
            logger.info(e);
            return null;
        }
    }

    /**
     * 从数据库中获取某个表的数据，条件为condition
     */
    public List<ObjService> getAllDataFromDB(String columns, String tableName, String condition) throws Exception {
        String sql = "SELECT * FROM " + tableName + " WHERE " + condition;
        if(tableName == null || tableName.equals("") ){
            return null;
        }
        if (columns == null || columns.equals("")) {
            if(condition == null || condition.equals("")){
                sql = "SELECT * FROM " + tableName;
            }else{
                sql = "SELECT * FROM " + tableName + " WHERE " + condition;
            }
        }else{
            if(condition == null || condition.equals("")){
                sql = "SELECT "+columns+" FROM " + tableName;
            }else{
                sql = "SELECT "+columns+" FROM " + tableName + " WHERE " + condition;
            }
        }
        ObjService so = selectObject(sql, null);
        return objServiceToList(so, null);
    }

    /**
     * 根据selectObject返回的结果，将其中以RowObj为Clsname的结果组成List的形式返回
     *
     * @param obj
     * @param clsName
     *            转换后的Clsname
     * @return
     */
    private List<ObjService> objServiceToList(ObjService obj, String clsName) {
        ArrayList<ObjService> result = new ArrayList<ObjService>();
        for (int i = 0; i < obj.objectSize("RowObj"); i++) {
            ObjService temp = (ObjService) obj.objectAt("RowObj", i).clone();
            if (clsName != null && !clsName.equals(""))
                temp.setClsName(clsName);
            result.add(temp);
        }
        return result;
    }

    public void executeSql(String sql, String[] params) throws Exception {
        deleteObject(sql, params);
    }
    public void deleteObject(String sql, String[] params) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; params != null && i < params.length; i++) {
                pstmt.setString(i + 1, params[i]);
            }
            pstmt.execute();
        } catch (Exception e) {
            logger.info("Execute sql : " + sql + " fail!!!");
            throw e;
        } finally {
            DBConnectionManager.getInstance().free(conn, pstmt, null);
        }
    }

    synchronized public boolean executeSqlWithResult(String sql, String[] params) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; params != null && i < params.length; i++) {
                pstmt.setString(i + 1, params[i]);
            }
            pstmt.execute();
            return true;
        } catch (Exception e) {
            logger.info("Execute sql : " + sql + " fail!!!");
            throw e;
        } finally {
            DBConnectionManager.getInstance().free(conn, pstmt, null);
        }
    }

    /**
     * 执行添加、删除、修改
     * 处理多条sql,batch,批量,
     * 注意每条语句分号结尾;
     * @param batchSql
     * @return
     * @auther niyb-006295
     * 2016-3-14上午09:18:13
     */
    public ErrorObjService excuteBatchSql(List<String> batchSql){
        ErrorObjService resOperationResult = new ErrorObjService();
        resOperationResult.setErrCode("-1");
        try{
            //批量执行sql
            DBStore ResOpratorBatch=new DBStore();
            resOperationResult=ResOpratorBatch.execteBatchUpdateSql(batchSql,0);
            if(resOperationResult.getErrCode() != 0){
                logger.error(this.getClass().getName()+" excuteBatchSql failed");
            }

        }catch(Exception e){
            logger.error(this.getClass().getName()+" excuteBatchSql failed");
        }
        return resOperationResult;


    }

    //operationName支持的值:DELETE_ONU_DEVICE_STATUS,CREATE_ONU_DEVICE_STATUS,UPDATE_ONU_DEVICE_STATUS
    //获取上面3个字段中之一所对应的value值
    public String getVariableStatus(String operationName)
    {
        String deleteOnuDeviceStatus = "";
        String sqlStr = "select value from  rc_properties where PROPNAME = '" + operationName + "'";
        ObjService result = null;
        try {
            result = selectObject(sqlStr, null);
        }
        catch (Exception e)
        {
            logger.error("get rc_properties :DELETE_ONU_DEVICE_STATUS " + " fail!");
        }
        if (result == null || result.getAllChildObjects().size() == 0) {
            EPONConstants.logger.debug("NETYPE_DISPLAY_NAME of " + sqlStr + " is not exist ! ");
        }
        else
        {
            ObjService rowObj = (ObjService) result.objectAt("RowObj",0).clone();
            deleteOnuDeviceStatus = rowObj.getStringValue("VALUE");
        }
        return deleteOnuDeviceStatus;
    }
    public String getEponMaxTemperature(){
        EponGlobalVariablesInit globalVariables  = EponGlobalVariablesInit.getInstance();
        String tem = globalVariables.getEpon_Max_Temperature();
        if("".equals(tem)||"null".equalsIgnoreCase(tem)){
            tem="45";
        }
        return tem;
    }

    /**
     * 从v_topo_type表中查询出所有onu的数据,不包括光纤收发器
     * FOT:Fiber Optical Transceiver(光纤收发器)
     */
    public ObjService getONU_TOPO_TYPE_ID_EXCEPTION_FOT() throws Exception {
        String sql = "select TOPO_TYPE_ID from v_topo_type where fieldname in (select fieldname from rcnetype where NE_CATEGORY_ID ='3' and PANEL_ID !='-1')";
        ObjService onuTopoTypeDatas = selectObject(sql, null);

        return onuTopoTypeDatas;
    }

    /**
     * 查出在主拓扑上添加的onu，如ISCOM5104GF-NP
     * modify by changjun
     * 由于独立网管ONU fieldname要统一为SNMP ONU，这里要把光纤收发器独立出来，并且也属于SNMP ONU
     */
    public ObjService getMainTopoONU() throws Exception {
//		String sql = "select TOPO_TYPE_ID from v_topo_type where fieldname in (select fieldname from rcnetype where NE_CATEGORY_ID ='3' and PANEL_ID ='-1')";
        String sql = "select TOPO_TYPE_ID from v_topo_type where ( DEVICE_TYPE in (select ircnetypeid from rcnetype where NE_CATEGORY_ID ='3' and PANEL_ID ='-1')) or TOPO_TYPE_ID = '1_SNMP ONU'";
        ObjService onuTopoTypeDatas = selectObject(sql, null);

        return onuTopoTypeDatas;
    }

    /**
     * 设备巡检,首选项Memory
     * @author niyb-006295
     * niyb add 20151212
     * @return
     */
    public String getEponMaxMemory(){
        EponGlobalVariablesInit globalVariables  = EponGlobalVariablesInit.getInstance();
        String ram = globalVariables.getOLT_MEMORY_THRESHOLD();
        if("".equals(ram)||"null".equalsIgnoreCase(ram)){
            ram="80";
        }
        return ram;
    }

    public static List selectDataBy(String sql){
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        List list=new ArrayList<>();
        try {
            conn = DBConnectionManager.getConnection();
            st = conn.prepareStatement(sql);
            rs=st.executeQuery();
            ResultSetMetaData resultSetMetaData=rs.getMetaData();

            int count=resultSetMetaData.getColumnCount();
            while (rs.next()) {
                for(int i=1;i<=count;i++){
                    String columnName = resultSetMetaData.getColumnName(i);
                    list.add(rs.getString(columnName));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;

    }

    public static  ObjService selectDataByParam(String sql){
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        ObjService row=new ObjService();
        try {
            conn = DBConnectionManager.getConnection();
            st = conn.prepareStatement(sql);
            rs=st.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                int size = meta.getColumnCount();
                for (int i = 1; i <= size; i++) {
                    String columnName = meta.getColumnName(i);
                    row.setValue(columnName.toUpperCase(), rs.getString(columnName));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row;
    }

    /**
     * 查询OLT下ONU的在线的状态
     * @param oltNeId
     * @param onuInstances
     * @return
     * @throws Exception
     */
    public  HashMap<String,String>  getOnuOnlineStatusByOltIdAndOnuList(String oltNeId, List<String> onuInstances)throws Exception {
        HashMap<String,String> map = new HashMap<String, String>();
        if(onuInstances == null || onuInstances.size() == 0){
            return map;
        }

        StringBuilder condition = new StringBuilder("");
        for(String instance:onuInstances){
            condition.append("'").append(instance).append("',");
        }
        condition = condition.replace(condition.length()-1, condition.length(), ")");
        String sql = "SELECT ispingok,INDEX_IN_MIB from rcnetnode where managed_url = '/ne="+ oltNeId+"' and INDEX_IN_MIB in ("  +condition;
        ObjService obj = selectObject(sql, null);
        for(int i = 0;i <obj.objectSize("RowObj");i++){
            map.put(obj.objectAt("RowObj", i).getStringValue("INDEX_IN_MIB"), obj.objectAt("RowObj", i).getStringValue("ispingok"));
        }
        return map;
    }

    public Vector<String> getONUInstanceFromDBByOltNeID(String neID){
        Vector<String> onuInstance = new Vector<String>();
        List<ObjService> vobj = new Vector<ObjService>();
        String condition = SecurityManagerCenter.getInstance().getDomainAllNEIDString();
        try {
            if (condition.equals(SecurityUtils.ALL))
            {
                vobj = EPONCommonDBUtil.getInstance().getONUInstanceFromDBByOltNeId(neID);
            } else if (condition.equals(SecurityUtils.NONE))
            {
                return null;
            } else
            {
                vobj = EPONCommonDBUtil.getInstance().getONUInstanceFromDBByOltNeIdAndAuthority(neID,condition);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            EPONConstants.logger.error("DeviceInspectTDataTreeBuilder getONUInstanceFromDBByOltNeID for neid " + neID+" error "+e);
        }
        for(int i = 0; i < vobj.size(); i++){
            onuInstance.add(vobj.get(i).getStringValue("INDEX_IN_MIB"));
        }
        return onuInstance;
    }

    /**
     * 根据oltNeId获取下挂所有ONU的index_in_mib
     * @param neID
     * @return
     * @throws Exception
     */
    public List<ObjService> getONUInstanceFromDBByOltNeId(String neID) throws Exception {
        String sql = "SELECT rcnetnode.INDEX_IN_MIB from rcnetnode ,rcnetype where rcnetype.NE_CATEGORY_ID = '3' and rcnetnode.iRCNETypeID = rcnetype.iRCNETypeID and rcnetnode.MANAGED_URL = '/ne=" + neID + "'";
        return objServiceToList(selectObject(sql, null), "ONU");
    }

    /**
     *  根据oltNeId过滤当前选中的OLT在当前用户管理域下可管的ONU的index_in_mib
     * @param neID
     * @param condition
     * @return
     * @throws Exception
     */
    public List<ObjService> getONUInstanceFromDBByOltNeIdAndAuthority(String neID,String condition) throws Exception {
        String sql = "SELECT rcnetnode.INDEX_IN_MIB from rcnetnode ,rcnetype where rcnetype.NE_CATEGORY_ID = '3'" +"and managed_url = '/ne=" +neID +"' and rcnetnode.iRCNETypeID = rcnetype.iRCNETypeID and rcnetnode.ircnetnodeid in (" + condition + ")";
        return objServiceToList(selectObject(sql, null), "ONU");
    }

    public Vector<String> getONUInstanceFromDBByOltNeIDs(String neID){
        Vector<String> onuInstance = new Vector<String>();
        List<ObjService> vobj = new Vector<ObjService>();
        String condition = SecurityManagerCenter.getInstance().getDomainAllNEIDString();
        try {
            if (condition.equals(SecurityUtils.ALL))
            {
                vobj = EPONCommonDBUtil.getInstance().getONUInstanceFromDBByOltNeId(neID);
            } else if (condition.equals(SecurityUtils.NONE))
            {
                return null;
            } else
            {
                vobj = EPONCommonDBUtil.getInstance().getONUInstanceFromDBByOltNeIdAndAuthority(neID,condition);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
        for(int i = 0; i < vobj.size(); i++){
            onuInstance.add(vobj.get(i).getStringValue("INDEX_IN_MIB"));
        }
        return onuInstance;
    }


    public static ErrorObjService executeQuery(String sql, String[] params) {
        ErrorObjService errResult = new ErrorObjService();
        if (sql != null && !"".equals(sql) &&  params != null) {
            Connection conn = null;
            ResultSet result = null;
            PreparedStatement pstmt = null;
            ResultSetMetaData data = null;
            try {
                conn = DBConnectionManager.getConnection();
                pstmt = conn.prepareStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    pstmt.setString(i + 1, params[i]);
                }
                result = pstmt.executeQuery();
                data = result.getMetaData();
                int columns = data.getColumnCount();
                while (result.next()) {
                    ObjService row = new ObjService("row");
                    for (int i = 1; i <= columns; i++) {
                        String value = result.getString(i).trim();
                        row.setValue(data.getColumnName(i), value);
                    }
                    errResult.addContainedObject(row);
                }
            } catch (SQLException e) {
                errResult.setErrCode("-1");
                errResult.setErrDesc(e.toString());
            } finally {
                DBConnectionManager.getInstance().free(conn, pstmt, result);
            }
        } else {
            errResult.setErrCode("-1");
            errResult.setErrDesc("SQL or Parameters error.");
        }
        return errResult;
    }
    public static ErrorObjService executeQuery(String sql) {
        ErrorObjService errResult = new ErrorObjService();
        if (sql != null && !"".equals(sql)) {
            Connection conn = null;
            ResultSet result = null;
            PreparedStatement pstmt = null;
            ResultSetMetaData data = null;
            try {
                conn = DBConnectionManager.getConnection();
                pstmt = conn.prepareStatement(sql);
                result = pstmt.executeQuery();
                data = result.getMetaData();
                int columns = data.getColumnCount();
                while (result.next()) {
                    ObjService row = new ObjService("row");
                    for (int i = 1; i <= columns; i++) {
                        String value = result.getString(i).trim();
                        row.setValue(data.getColumnName(i), value);
                    }
                    errResult.addContainedObject(row);
                }
            } catch (SQLException e) {
                errResult.setErrCode("-1");
                errResult.setErrDesc(e.toString());
                DBConnectionManager.getInstance().free(conn, pstmt, result);
            }
        } else {
            errResult.setErrCode("-1");
            errResult.setErrDesc("SQL or Parameters error.");
        }
        return errResult;
    }

    public  List<ObjService> getONUInstanceOnlineFromDBByOltNeId(String neID) throws Exception {
        String sql = "SELECT rcnetnode.INDEX_IN_MIB,rcnetype.iRCNETypeID,rcnetnode.IRCNETNODEID,rcnetnode.DISTANCE from rcnetnode ,rcnetype " +
                "where rcnetype.NE_CATEGORY_ID = '3' and rcnetnode.iRCNETypeID = rcnetype.iRCNETypeID and rcnetnode.MANAGED_URL = '/ne=" + neID+"'";
        List<ObjService> objServices=objServiceToList(selectObject(sql, null), "ONU");

        return objServices;
    }

    public  List<ObjService> getCardInfoFromDBByOltNeId(String neID) throws Exception {
        String sql = "SELECT card_id,INDEX_IN_MIB from card where IRCNETNODEID='" + neID+"'";
        List<ObjService> objServices=objServiceToList(selectObject(sql, null), "Card");
        return objServices;
    }



}
