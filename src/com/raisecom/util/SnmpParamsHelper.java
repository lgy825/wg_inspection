package com.raisecom.util;

import com.raisecom.nms.component.topo.model.EntityObject;
import com.raisecom.nms.platform.client.util.Argument;
import com.raisecom.nms.platform.cnet.ObjService;

import java.util.Hashtable;

/**
 * Created by ligy-008494 on 2018/11/13.
 */
public class SnmpParamsHelper {
    public static ObjService getOption(ObjService param)
    {
        ObjService option = new ObjService();
        option.setValue("NEType", param.getStringValue("iRCNETypeID"));
        option.setValue("ReadCommunity", param.getStringValue("READCOM"));
        option.setValue("WriteCommunity", param.getStringValue("WRITECOM"));
        option.setValue("NetNodeID", param.getStringValue("IRCNETNODEID"));
        option.setValue("SNMPVersion", "V" + param.getStringValue("VERS"));
        option.setValue("Timeout", param.getStringValue("TIMEOUT"));
        option.setValue("SNMPPort", param.getStringValue("PORT"));
        option.setValue("IPAddress", param.getStringValue("IPADDRESS"));
        option.setValue("Retry", param.getStringValue("RETRY"));
        return option;
    }

    public static ObjService getOption(EntityObject device)
    {
        ObjService option = new ObjService();
        option.setValue("NEType", device.getProperty("iRCNETypeID"));
        option.setValue("ReadCommunity", device.getProperty("READCOM"));
        option.setValue("WriteCommunity", device.getProperty("WRITECOM"));
        option.setValue("NetNodeID", device.getProperty("IRCNETNODEID"));
        option.setValue("SNMPVersion", "V" + device.getProperty("VERS"));
        option.setValue("Timeout", device.getProperty("TIMEOUT"));
        option.setValue("SNMPPort", device.getProperty("PORT"));
        option.setValue("IPAddress", device.getProperty("IPADDRESS"));
        option.setValue("Retry", device.getProperty("RETRY"));
        return option;
    }

    public static Hashtable makeOption(ObjService deviceInfo)
    {
        Hashtable op = new Hashtable();

        String snmpVersion = deviceInfo.getStringValue("VERS");

        op.put(Argument.IPAddress, deviceInfo.getStringValue("IPADDRESS"));
        op.put(Argument.NetNodeID, deviceInfo.getStringValue("IRCNETNODEID"));
        op.put(Argument.NEType, deviceInfo.getStringValue("iRCNETypeID"));
        op.put(Argument.ReadCommunity, deviceInfo.getStringValue("READCOM"));
        op.put(Argument.WriteCommunity, deviceInfo.getStringValue("WRITECOM"));
        op.put(Argument.Timeout, deviceInfo.getStringValue("TIMEOUT"));
        op.put(Argument.Retry, deviceInfo.getStringValue("RETRY"));
        op.put(Argument.SNMPPort, deviceInfo.getStringValue("PORT"));
        op.put(Argument.SNMPVersion, snmpVersion);

        if (snmpVersion.equalsIgnoreCase("3") || snmpVersion.equalsIgnoreCase("V3"))
        {
            String v3 = deviceInfo.getStringValue("V3");
            ObjService v3p = new ObjService();
            v3p.setCurrentHashtable(op);
            Argument.decodeSNMPv3(v3p, v3);
        }

        return op;
    }

    public static ObjService getOption(String neid){
        ObjService node = EPONCommonDBUtil.getInstance().getNePropertyForSnmpParametersFromDB(neid);
        if(node == null)//兼容处理，否则会抛出异常
            node = new ObjService();
        return getOption(node);
    }
}
