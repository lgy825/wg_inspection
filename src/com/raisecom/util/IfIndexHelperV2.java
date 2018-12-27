package com.raisecom.util;

import com.raisecom.ems.templet.client.util.SnmpUtilities;
import com.raisecom.nms.platform.cnet.ObjService;

/**
 * Created by ligy-008494 on 2018/11/13.
 */
public class IfIndexHelperV2 {

    public static final int SLOTMASK = 0X1F;
    public static final int SLOTSHIFT = 6;
    public static final int VSLOTSHIFT = 23;
    public static final int PORTMASK = 0X3F;
    public static final int PORTSHIFT = 0;
    public static final int VPORTSHIFT = 16;

    public static int getSlotInt(int index) {
        return (index >> SLOTSHIFT) & SLOTMASK;
    }

    public static int getPortInt(int index) {
        return (index >> PORTSHIFT) & PORTMASK;
    }

    private static int getBit(int shift, int mask, int index) {
        return (index >> shift) & mask;
    }

    private static int getPortLayerValue(int index) {
        // return getBit(27, 0X1F, index);
        return getBit(28, 0X1F, index);
    }

    private static int getPortTypeValue(int index) {
        return getBit(12, 0XCFFF, index);
    }

    private static int getBit11Value(int index) {
        return getBit(11, 0X1, index);
    }

    public static boolean isPanelPort(int index) {
        if (getPortLayerValue(index) == 0) {
            if (getPortTypeValue(index) == 0) {
                if (getBit11Value(index) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isTrunkPort(int index) {
        if (getPortLayerValue(index) == 0) {
            if (getPortTypeValue(index) == 0) {
                if (getBit11Value(index) == 1) {
                    if (getBit(9, 0X3, index) == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //hb EPON00078461 16/11/14 start
    public static boolean isIpIntefaecPort(int index) {
        if (getPortLayerValue(index) == 3) {
            if (getPortTypeValue(index) == 0) {
                if (getBit11Value(index) == 0) {
                    return true;
                }
            }
        }
        return false;
    }
    //hb EPON00078461 16/11/14 end

    public static boolean isCpuInPort(int index) {
        if (getPortLayerValue(index) == 0) {
            if (getPortTypeValue(index) == 0) {
                if (getBit11Value(index) == 1) {
                    if (getBit(4, 0XCF, index) == 32) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isCpuOutPort(int index) {
        if (getPortLayerValue(index) == 0) {
            if (getPortTypeValue(index) == 0) {
                if (getBit11Value(index) == 1) {
                    if (getBit(4, 0XCF, index) == 33) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isCyclePort(int index) {
        if (getPortLayerValue(index) == 0) {
            if (getPortTypeValue(index) == 0) {
                if (getBit11Value(index) == 1) {
                    if (getBit(4, 0XCF, index) == 34) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean is3LayerPort(int index) {
        if (getPortLayerValue(index) == 0) {
            if (getPortTypeValue(index) == 0) {
                if (getBit11Value(index) == 1) {
                    if (getBit(10, 0X1, index) == 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int to3LayerPortFor6800(int index) {
        return index - 3072;
    }
    public static int toTrunkPort(int index) {
        return index - 2048;
    }
    //niby add 6800 ver2.4x-2.50 rcIpBaseTableGroup
    public static int toIpBaseTableLayerPortFor6800(int index){
        return index - 838860800;
    }
    public static int getIpBaseTableLayerPortFor6800(int index){
        return index + 838860800;
    }
    //tmy add 5508q-gp ver2.4x-2.50 rcIpBaseTableGroup
    public static int toIpBaseTableLayerPortFor5508Q(int index){
        return index - 838860800;
    }
    public static int getIpBaseTableLayerPortFor5508Q(int index){
        return index + 838860800;
    }
    public static boolean isVPort(int index) {
        if (getPortLayerValue(index) == 1) {
            return true;
        }
        return false;
    }

    public static boolean isLLIDPort(int index) {
        if (getPortLayerValue(index) == 2) {
            return true;
        }
        return false;
    }

    public static int getSlotId(int v) {
        if (isVPort(v) || isLLIDPort(v)) {
            return ((v >> VSLOTSHIFT) & SLOTMASK);
        } else {
            return ((v >> SLOTSHIFT) & SLOTMASK);
        }
    }

    public static int getPortId(int v) {
        if (isVPort(v) || isLLIDPort(v)) {
            return ((v >> VPORTSHIFT) & PORTMASK);
        } else {
            return ((v >> PORTSHIFT) & PORTMASK);
        }
    }

    /* 根据槽位号s、端口号p返回新索引，有待改进 */
    public static int getNewPortIndex(int slot, int port) {
        return ((slot & SLOTMASK) << SLOTSHIFT)
                + ((port & PORTMASK) << PORTSHIFT);
    }

    /* 根据槽位号s、端口号p、ONU_id返回虚端口索引，有待改进 */
    public static int getNewVPortIndex(int slot, int port, int onu) {
        int newIndex = (1 << 28) + (slot << 23) + (port << 16) + (onu << 0);
        return newIndex;
    }

    public static int getONUId(int index) {
        return index & 0XFFFF;
    }

    public static String indexNew2Old(String index) {
        int v = Integer.parseInt(index.trim());
        int slot = getSlotId(v);
        int port = getPortId(v);
        int onuId = 0;
        if (isLLIDPort(v) || isVPort(v)) {
            onuId = getONUId(v);
        }
        if (onuId == 0) {
            if (slot == 0) {
                return port + "";
            } else {
                return slot * 10000000 + port + "";
            }
        } else {
            if (slot == 0) {
                return port * 100000 + onuId + "";
            } else {
                return slot * 10000000 + port * 100000 + onuId + "";
            }
        }
    }

    /* 判断ONU索引是新的还是旧的 */
    public static boolean isNewOnuIndex(String index) {
        int slot_id = 0;
        int port_id = 0;
        int onu_id = 0;
        try {
            slot_id = Integer.parseInt(IfIndexHelper.getSlotId(index));
            port_id = Integer.parseInt(IfIndexHelper.getPortId(index));
            onu_id = Integer.parseInt(IfIndexHelper.getOnuId(index));
        } catch (NumberFormatException e) {
            EPONConstants.logger.debug(e);
        }
        if (slot_id > 26 || port_id > 16 || onu_id > 128)
            return true;
        else
            return false;
    }

    /* 判断PON索引是新的还是旧的 */
    public static boolean isNewPONIndex(String index) {
        int v = Integer.parseInt(index.trim());
        return (v < 4096);
    }

    public static String OnuIndexOld2New(String index) {
        int v = Integer.parseInt(index.trim());
        int slot = v / 10000000;
        int port = (v % 10000000) / 100000;
        int onu = v % 100000;
        int newIndex = (1 << 28) + (slot << 23) + (port << 16) + (onu << 0);
        return newIndex + "";
    }

    /* 将panelPort由新索引转换为新的显示格式，例如将"961"转换为"15/1" 或者将"377552897"转换为"13/1/1" */
    public static String newPortIndex2NewFormat(String port) {
        int intPort = 0;
        try {
            intPort = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            EPONConstants.logger.debug(e);
        }
        int slotid = getSlotId(intPort);
        int portid = getPortId(intPort);
        int onuId = 0;
        if (isLLIDPort(intPort) || isVPort(intPort)) {
            onuId = getONUId(intPort);
        }
        if (onuId == 0) {
            if (slotid == 0) {
                return port + "";
            } else {
                return "" + slotid + "/" + portid;
            }
        } else {
            if (slotid == 0) {
                return "" + portid + "/" + onuId;
            } else {
                return "" + slotid + "/" + portid + "/" + onuId;
            }
        }
    }

    /* 将panelPort由新显示格式转换为新索引，例如将"15/1"转换为"961" */
    public static String newFormat2newPortIndex(String format) {
        String[] arrayValue = format.split("\\/");
        int slot = 0;
        int port = 0;
        try {
            slot = Integer.parseInt(arrayValue[0]);
            port = Integer.parseInt(arrayValue[1]);
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            EPONConstants.logger.debug(e);
        }
        int newPortIndex = getNewPortIndex(slot, port);
        return "" + newPortIndex;
    }


    /* 将panelPort由新显示格式转换为新索引，例如将"10000001"转换为"65" */
    public static String oldPanelPort2newPortIndex(String oldPanelPortIndex) {
        int portIndex = 0;
        try {
            portIndex = Integer.parseInt(oldPanelPortIndex);
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            EPONConstants.logger.debug(e);
        }
        int slot = portIndex / 10000000;
        int port = portIndex % 10000000;
        int newPortIndex = getNewPortIndex(slot, port);
        return "" + newPortIndex;
    }

    /* 将包含onu的panelPort由新显示格式转换为新索引，例如将"102*****"转换为"66" */
    public static String oldPanelPortContainOnu2newPortIndex(String oldPanelPortIndex) {
        int portIndex = 0;
        try {
            portIndex = Integer.parseInt(oldPanelPortIndex);
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            EPONConstants.logger.debug(e);
        }
        int slot = portIndex / 10000000;
        int port = portIndex % 10000000 / 100000;
        int newPortIndex = getNewPortIndex(slot, port);
        return "" + newPortIndex;
    }

    // 根据端口索引，读取端口类型的类型值,10GEPON:65,PON:52,GE:22,10GE:31
    public static String getPorttypeByPortindex(ObjService obj,
                                                String port_index) {
        try {
            ObjService result = null;
            obj.setValue("TableName", "ifMauTable");
            obj.setValue("Instance", port_index + ".0");
            result = SnmpUtilities.SnmpOperation(obj, "getValue");

            result = result.objectAt("RowSet", 0);
            String ifMauType = result.getStringValue("ifMauType");
            ifMauType = ifMauType.substring(ifMauType.lastIndexOf(".") + 1);

            return ifMauType;
        } catch (Exception e) {
            EPONConstants.logger.error(e);
            return null;
        }
    }
}
