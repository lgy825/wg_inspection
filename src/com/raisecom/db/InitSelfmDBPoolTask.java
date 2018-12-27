package com.raisecom.db;

import com.raisecom.common.logging.LogFactory;
import com.raisecom.common.logging.Logger;
import com.raisecom.nms.platform.client.ResourceManager;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.nms.selfm.util.SelfmUtils;
import com.raisecom.nms.util.DBConnectionManager;
import com.raisecom.util.XMLFileHelperUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by ligy-008494 on 2018/11/14.
 */
public class InitSelfmDBPoolTask  {

    private static final Logger logger = LogFactory.getLogger("selfm");

//    public static void main(String[] args) {
//        execute();
//        System.out.println(DBConnectionManager.getConnection());
//    }
    public static   boolean execute() {
        logger.log(300, "******************* Initialize database connection pool ***************");
        ObjService dbConn = XMLFileHelperUtil.loadConfigInObjService("../wg_inspection/src/config/dbproperty.xml");
        try {
            DBConnectionManager.getInstance().init(dbConn);
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(300,"数据库连接异常");
        }
        return true;
    }


}
