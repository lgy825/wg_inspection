package com.raisecom.adapter;



import com.raisecom.ems.templet.client.adapter.ValueAdapter;
import com.raisecom.ems.templet.client.util.Resource;
import com.raisecom.ems.templet.util.Utilities;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.util.IfIndexHelperV2;

/**
 * @author shenxc-2695
 * @version EPON 7.0
 * @since 2012.06.20
 */
public class TrunkGroupPortListValueAdapter extends ValueAdapter{
	
	public boolean isValid(String value)
	{
		String zyh = Utilities.getResource(Resource.RESOURCE, "ZYH");
		String yyh = Utilities.getResource(Resource.RESOURCE, "YYH");
		String dh  = Utilities.getResource(Resource.RESOURCE, "DOUHAO");
		String reEnter = Utilities.getResource(Resource.RESOURCE, "PLEASE_RE_ENTER");
		String label = this.m_mibNodeInfo.getStringValue("Label");
		
		ObjService valueAdapter = this.m_mibNodeInfo.objectAt("ValueAdapter", 0);
		int slotMax = valueAdapter.getIntValue("SlotMax");
		int slotMin = valueAdapter.getIntValue("SlotMin");
		int portMax = valueAdapter.getIntValue("PortMax");
		int portMin = valueAdapter.getIntValue("PortMin");
		
		String fullLabel = zyh + label + yyh;
		String errorDesc = null;
		if(slotMax == slotMin){
			//errorDesc = Utilities.getResource(EPONConstants.OAM_MIB_RB, "PortListFormatNoCard");
		}else{
			//errorDesc = Utilities.getResource(EPONConstants.OAM_MIB_RB, "PortListFormat");
			errorDesc = errorDesc.replaceAll("#slotMax", String.valueOf(slotMax));
		}
		
		errorDesc = errorDesc.replaceAll("#slotMin", String.valueOf(slotMin));
		errorDesc = errorDesc.replaceAll("#portMin", String.valueOf(portMin));
		errorDesc = errorDesc.replaceAll("#portMax", String.valueOf(portMax));
		value = value.trim();
		
		
		
		if(value.equals("") || value == null){
			return true;
		}
		String[] portListArray = value.split(",");
		for(int i=0; i<portListArray.length; i++){
			String portList = portListArray[i];
			String[] portArray = portList.split("\\/");
			try {
				int slotId = Integer.parseInt(portArray[0]);
				if(slotId < slotMin || slotId >slotMax){
					this.m_errDesc = fullLabel + errorDesc + dh + reEnter;
					return false;
				}
				String[] port = portArray[1].split("-");
				if(port.length == 1){
					int portId = Integer.parseInt(port[0]);
					if(portId < portMin || portId > portMax){
						this.m_errDesc = fullLabel + errorDesc + dh + reEnter;
						return false;
					}
				}else {
					int portId1 = Integer.parseInt(port[0]);
					int portId2 = Integer.parseInt(port[1]);
					if(portId1 < portMin || portId1 > portMax || portId2 < portMin || portId2 > portMax){
						this.m_errDesc = fullLabel + errorDesc + dh + reEnter;
						return false;
					}
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				//EPONConstants.logger.debug(e);
				this.m_errDesc = fullLabel + errorDesc + dh + reEnter;
				return false;
			}
		}
		return true;




	}

	public String getValue(String value)
	{
		value = value.trim();
		if(value.equals("") || value == null){
			return value;
		}
		String result = "";
		String[] arrayValue = value.split(",");
		for(int i=0; i<arrayValue.length; i++){
			String portList = arrayValue[i];
			String[] portListArray = portList.split("\\/");
			String[] portArray = portListArray[1].split("-");
			if(portArray.length == 1){
				result += IfIndexHelperV2.newFormat2newPortIndex(portList) + ",";
			}else{
				int slot = 0;
				int port1 = 0;
				int port2 = 0;
				try {
					slot = Integer.parseInt(portListArray[0]);
					port1 = Integer.parseInt(portArray[0]);
					port2 = Integer.parseInt(portArray[1]);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					//EPONConstants.logger.debug(e);
				}
				int newPortIndex1 = IfIndexHelperV2.getNewPortIndex(slot, port1);
				int newPortIndex2 = IfIndexHelperV2.getNewPortIndex(slot, port2);
				result += newPortIndex1 + "-" + newPortIndex2 + ",";
			}
		}
		return result.substring(0, (result.length()-1));
	}

	public String setValue(String value)
	{
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
