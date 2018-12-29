package com.raisecom.boot;

import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.util.EPONCommonDBUtil;
import com.raisecom.util.EPONConstants;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ligy-008494 on 2018/11/13.
 */
public class EponGlobalVariablesInit {


    private static String DELETE_ONU_DEVICE_STATUS = "1";
    private static String CREATE_ONU_DEVICE_STATUS = "1";
    private static String UPDATE_ONU_DEVICE_STATUS = "1";
    private static String PARTNER_DIG_STATUS = "0";
    private static String UPDATE_PORT_STATUS = "1";
    private static String SYN_DEL_ENABLE = "0";

    //从v_topo_type表中查询出所有onu的数据
    private static Set<String> OnuTopoTypeDatas = new HashSet<String>();
    //查出需要在主拓扑上添加的onu类型
    private static Set<String> MainTopoOnuTypeDatas = new HashSet<String>();
    private static String EPON_MAX_TEMPERATURE = "";
    private static String OLT_CPU_THRESHOLD = "";
    private static String OLT_MEMORY_THRESHOLD = "";

    private EPONCommonDBUtil dbUtil = EPONCommonDBUtil.getInstance();

    private EponGlobalVariablesInit()
    {
        initOnuTopoTypeDatas();
    }
    private static EponGlobalVariablesInit instance = new EponGlobalVariablesInit();
    public static EponGlobalVariablesInit getInstance()
    {
        return instance;
    }
    //获取到全局变量的值，1表示进行该操作，0表示不进行该操作
    public void initGlobalVariables()
    {
        DELETE_ONU_DEVICE_STATUS = dbUtil.getVariableStatus("DELETE_ONU_DEVICE_STATUS");
        CREATE_ONU_DEVICE_STATUS = dbUtil.getVariableStatus("CREATE_ONU_DEVICE_STATUS");
        UPDATE_ONU_DEVICE_STATUS = dbUtil.getVariableStatus("UPDATE_ONU_DEVICE_STATUS");
        PARTNER_DIG_STATUS = dbUtil.getVariableStatus("PARTNER_DIG_STATUS");
        UPDATE_PORT_STATUS = dbUtil.getVariableStatus("UPDATE_PORT_STATUS");
        SYN_DEL_ENABLE = dbUtil.getVariableStatus("SYN_DEL_ENABLE");
    }


    /**
     * sijl add 20150522 获取PON首选项中设备巡检各个属性的值
     */
    public void initDeviceInspectVariables()
    {
        EPON_MAX_TEMPERATURE = dbUtil.getVariableStatus("EPON_MAX_TEMPERATURE");
        OLT_CPU_THRESHOLD = dbUtil.getVariableStatus("OLT_CPU_THRESHOLD");
        OLT_MEMORY_THRESHOLD = dbUtil.getVariableStatus("OLT_MEMORY_THRESHOLD");
    }


    /**
     * kangzj- 2013-11-20 修改成private
     */
    private static void initOnuTopoTypeDatas()
    {
        try
        {
            ObjService ONUDatas = EPONCommonDBUtil.getInstance().getONU_TOPO_TYPE_ID_EXCEPTION_FOT();
            for(int i = 0; i < ONUDatas.objectSize("RowObj"); i++){
                ObjService obj = ONUDatas.objectAt("RowObj", i);
                OnuTopoTypeDatas.add(obj.getStringValue("TOPO_TYPE_ID"));
            }
            ObjService mainTopoOnuDatas = EPONCommonDBUtil.getInstance().getMainTopoONU();
            for(int i = 0; i < mainTopoOnuDatas.objectSize("RowObj"); i++){
                ObjService obj = mainTopoOnuDatas.objectAt("RowObj", i);
                MainTopoOnuTypeDatas.add(obj.getStringValue("TOPO_TYPE_ID"));
            }
        }
        catch (Exception e)
        {
            EPONConstants.logger.error(e);
        }

    }

    public  String getDELETE_ONU_DEVICE_STATUS()
    {
        return dbUtil.getVariableStatus("DELETE_ONU_DEVICE_STATUS");
    }

    public  void setDELETE_ONU_DEVICE_STATUS(String value)
    {
        DELETE_ONU_DEVICE_STATUS = value;
    }

    public  String getCREATE_ONU_DEVICE_STATUS()
    {
        return dbUtil.getVariableStatus("CREATE_ONU_DEVICE_STATUS");
    }

    public  void setCREATE_ONU_DEVICE_STATUS(String value)
    {
        CREATE_ONU_DEVICE_STATUS = value;
    }

    public  String getUPDATE_ONU_DEVICE_STATUS()
    {
        return dbUtil.getVariableStatus("UPDATE_ONU_DEVICE_STATUS");
    }

    public  void setUPDATE_ONU_DEVICE_STATUS(String value)
    {
        UPDATE_ONU_DEVICE_STATUS = value;
    }

    public  String getPARTNER_DIG_STATUS()
    {
        return dbUtil.getVariableStatus("PARTNER_DIG_STATUS");
    }

    public  void setPARTNER_DIG_STATUS(String value)
    {
        PARTNER_DIG_STATUS = value;
    }

    public  String getUPDATE_PORT_STATUS()
    {
        return dbUtil.getVariableStatus("UPDATE_PORT_STATUS");
    }

    public  void setUPDATE_PORT_STATUS(String value)
    {
        UPDATE_PORT_STATUS = value;
    }
    public  Set<String> getOnuTopoTypeDatas()
    {
        return this.OnuTopoTypeDatas;
    }
    public  Set<String> getMainTopoOnuTypeDatas()
    {
        return this.MainTopoOnuTypeDatas;
    }

    public  String getEpon_Max_Temperature()
    {
        return EPON_MAX_TEMPERATURE;
    }

    public  void setEpon_Max_Temperature(String value)
    {
        EPON_MAX_TEMPERATURE = value;
    }
    public static String getOLT_CPU_THRESHOLD() {
        return OLT_CPU_THRESHOLD;
    }
    public static void setOLT_CPU_THRESHOLD(String olt_cpu_threshold) {
        OLT_CPU_THRESHOLD = olt_cpu_threshold;
    }
    public static String getOLT_MEMORY_THRESHOLD() {
        return OLT_MEMORY_THRESHOLD;
    }
    public static void setOLT_MEMORY_THRESHOLD(String olt_memory_threshold) {
        OLT_MEMORY_THRESHOLD = olt_memory_threshold;
    }
    public  String getSYN_DEL_ENABLE() {
        return dbUtil.getVariableStatus("SYN_DEL_ENABLE");
    }
    public  void setSYN_DEL_ENABLE(String sYN_DEL_ENABLE) {
        SYN_DEL_ENABLE = sYN_DEL_ENABLE;
    }
}
