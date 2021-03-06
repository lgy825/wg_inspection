package com.raisecom.util;

import com.raisecom.ems.platform.util.ResourceUrlParser;

import static com.raisecom.adapter.IfIndexHelperGp.getOnuIdFromPortIndex;
import static com.raisecom.adapter.IfIndexHelperGp.getPonId;

/**
 * Created by ligy-008494 on 2018/11/13.
 */
public class IfIndexHelper {


    /**
     * 改成从URL中直接获取到ONU Instance
     * @param url
     * @return
     */
    public static String urlToInstance(String url)
    {
        String shelfId = ResourceUrlParser.getIdByLabel(url, "/shelf=");
        if (shelfId.equals("null"))
        {
            String portId = ResourceUrlParser.getIdByLabel(url, "/slot=");
            String onuId = ResourceUrlParser.getIdByLabel(url, "/card=1.");
            int instance = Integer.parseInt(portId) * 100000 + Integer.parseInt(onuId);
            return instance + "";
        }
        else
        {
            String instance = ResourceUrlParser.getIdByLabel(url, "/card=");

            return instance;
        }
    }

    /**
     * 槽位号
     * @param instance
     * @return
     */
    public static String getSlotId(String instance)
    {
        int ifIndex = Integer.parseInt(instance);
        int portId = ifIndex / 10000000;
        return "" + portId;
    }

    /**
     * 局端板卡端口
     * @param instance
     * @return
     */
    public static String getPortId(String instance)
    {
        int ifIndex = Integer.parseInt(instance);
        int portId;
        if (ifIndex > 100000)
        {
            int onuIndexInSlot = ifIndex % 10000000;
            portId = onuIndexInSlot / 100000;
            if (portId == 0)
            {
                portId = ifIndex % 10000000;
            }
        }
        else
        {
            portId = ifIndex;
        }
        return "" + portId;
    }

    public static String getOnuId(String onuInstance)//zyx add
    {
        int onuIndexInSlot = Integer.parseInt(onuInstance) % 10000000;
        int onuId = onuIndexInSlot % 100000;
        return "" + onuId;
    }

    /**
     * 远端ONU端口
     * @param onuInstance
     * @param portId
     * @return
     */
    public static String getPortInstance(String onuInstance, String portId)//zyx add
    {
        int onuIndex = Integer.parseInt(onuInstance);
        int onuId = Integer.parseInt(IfIndexHelper.getOnuId(onuInstance));
        int uniIndex = (onuIndex - onuId) + onuId * 1000 + Integer.parseInt(portId);
        return "" + uniIndex;

    }

    /**
     *
     * @param portIndex UNI口索引
     * @return 得到ONU的端口编号
     */
    public static String getPortIdFromPortIndex(String portIndex)//zyx add
    {
        int uniIndex = Integer.parseInt(portIndex);
        int portId = uniIndex % 1000;
        return "" + portId;
    }

    public static String getPortDisplayName(String index)
    {
        String displayName = "";
        String onuInstance = getOnuInstanceFromPortIndex(index);
        String slotId = getSlotId(onuInstance);
        String ponPortId = getPortId(onuInstance);
        String onuId = getOnuId(onuInstance);
        String portId = getPortIdFromPortIndex(index);
        displayName += slotId + "/" + ponPortId + "/" + onuId + "/" + portId;

        return displayName;
    }

    public static String getOnuInstanceFromPortIndex(String portIndex)//zyx add
    {

        int uniIndex = Integer.parseInt(portIndex);
        int uniId = Integer.parseInt(IfIndexHelper.getPortIdFromPortIndex(portIndex));
        int onuId = Integer.parseInt(IfIndexHelper.getOnuIdFromPortIndex(portIndex));
        int onuIndex = (uniIndex - uniId) - onuId * 1000 + onuId;
        return onuIndex + "";

    }
    public static String getOnuIdFromPortIndex(String portIndex)//zyx add
    {
        int uniIndex = Integer.parseInt(portIndex);
        int onuId = uniIndex % 100000;
        onuId = onuId / 1000;
        return "" + onuId;
    }

}
