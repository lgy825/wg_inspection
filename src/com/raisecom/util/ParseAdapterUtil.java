package com.raisecom.util;

import com.raisecom.ems.templet.util.ExtendNumberList;

/**
 * Created by liujs-008398 on 2019-01-15.
 */
public class ParseAdapterUtil {

    public static String setValue(String value) {
        value = value.trim();
        if(value.equals("") || value == null){
            return "";
        }
        String result = "";
        String[] arrayValue = value.split(",");
        for(int i=0; i<arrayValue.length; i++){
            String portList = arrayValue[i];
            String[] portListArray = portList.split("-");
            if(portListArray.length == 1){
                result += IfIndexHelperV2.newPortIndex2NewFormat(portListArray[0]) + ",";
            }else{
                int slot = IfIndexHelperV2.getSlotId(Integer.parseInt(portListArray[0]));
                int port1 = IfIndexHelperV2.getPortId(Integer.parseInt(portListArray[0]));
                int port2 = IfIndexHelperV2.getPortId(Integer.parseInt(portListArray[1]));
                result += slot + "/" + port1 + "-" + port2 + ",";
            }
        }
        return result.substring(0, (result.length()-1));
    }

}
