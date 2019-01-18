package com.raisecom.adapter;


import com.raisecom.ems.templet.server.adapter.MibNodeAdapter;
import com.raisecom.ems.templet.util.ExtendNumberList;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.nms.snmp.SnmpString;
import com.raisecom.nms.snmp.SnmpVar;
import com.raisecom.nms.util.ServerUtilities;

/**
 * Vlanset类型节点适配器
 * 
 * @author zyy
 */
public class VlansetAdapter extends MibNodeAdapter{
	
	public VlansetAdapter()
	{
	}

	/**
	 * @see com.raisecom.ems.templet.server.adapter.MibNodeAdapter#readMibData(com.adventnet.snmp.snmp2.SnmpVar,
	 *      ObjService)
	 */
	public String readMibData(SnmpVar var, ObjService mibnode)
	{
		String syntaxName = mibnode.getStringValue("SyntaxName");
		String label = mibnode.getStringValue("Label");

		ServerUtilities.debug("[" + label + "] Name is [" + syntaxName + "], Syntax is [" + var.getTypeString() + "]");

		if (var instanceof SnmpString)
		{
			String strNumberList = NumberList.getNumberStringList(var.toBytes(), 1);

			return strNumberList;
		}

		return var.toString();
	}

	/**
	 * @see com.raisecom.ems.templet.server.adapter.MibNodeAdapter#writeMibData(String,
	 *      ObjService)
	 */
	public SnmpVar writeMibData(String value, ObjService mibnode)
	{
	  //add huxielong 20170606 泰国需求定制
		 while (value.indexOf("/") != -1){
			 value = value.replaceAll("/", ",");
		 }
		String syntaxName = mibnode.getStringValue("SyntaxName");
		String label = mibnode.getStringValue("Label");

		ServerUtilities.debug("[" + label + "] Name is [" + syntaxName + "], Value is [" + value + "]");

		SnmpVar var = null;

		byte vlanSetBytes[] = new byte[512];
		for (int i = 0; i < vlanSetBytes.length; i++)
		{
			vlanSetBytes[i] = 0;
		}

		value = value.trim();
		if (value.equals(""))
		{
			var = (SnmpVar) new SnmpString(vlanSetBytes);
			return var;
		}

		int[] vlanids = NumberList.getNumberStringList(value, 4096);
		for (int i = 1; i < vlanids.length; i++)
		{
			if (vlanids[i] == 1)
			{
				int vlanid = i - 1;
				int pos = vlanid / 8;
				int bitpos = vlanid % 8;
				int orvalue = 1 << (7 - bitpos);

				vlanSetBytes[pos] |= orvalue;
			}
		}

		var = (SnmpVar) new SnmpString(vlanSetBytes);

		return var;
	}

	public String readMibData_bak(SnmpVar var, ObjService mibnode)
	{
		String syntaxName = mibnode.getStringValue("SyntaxName");
		String label = mibnode.getStringValue("Label");

		ServerUtilities.debug("[" + label + "] Name is [" + syntaxName + "], Syntax is [" + var.getTypeString() + "]");

		if (var instanceof SnmpString)
		{
			byte portlist[] = var.toBytes();
			// ServerUtilities.debug("Vlanset length is : " + portlist.length);
			String retval = "";

			for (int i = 0; i < portlist.length; i++)
			{
				int andvalue = 128;
				for (int j = 1; j <= 8; j++)
				{
					if ((portlist[i] & andvalue) > 0 && i * 8 + j < 4095)
						retval += String.valueOf(i * 8 + j) + ",";
					andvalue = andvalue >>> 1;
				}
			}

			if (!retval.equals(""))
				retval = retval.substring(0, retval.length() - 1);

			return ExtendNumberList.getExtendStringList(retval, true);
		}

		return var.toString();
	}

	/**
	 * @see com.raisecom.ems.templet.server.adapter.MibNodeAdapter#writeMibData(String,
	 *      ObjService)
	 */
	public SnmpVar writeMibData_bak(String value, ObjService mibnode)
	{
		String syntaxName = mibnode.getStringValue("SyntaxName");
		String label = mibnode.getStringValue("Label");

		ServerUtilities.debug("[" + label + "] Name is [" + syntaxName + "], Value is [" + value + "]");

		SnmpVar var = null;

		byte vlanSetBytes[] = new byte[512];
		for (int i = 0; i < vlanSetBytes.length; i++)
		{
			vlanSetBytes[i] = 0;
		}

		value = value.trim();
		if (value.equals(""))
		{
			var = (SnmpVar) new SnmpString(vlanSetBytes);
			return var;
		}

		String sections[] = ExtendNumberList.getNumberStringList(value);
		// String sections[] = value.split(",");
		// ServerUtilities.debug("sections.length : "+sections.length);
		for (int i = 0; i < sections.length; i++)
		{
			// ServerUtilities.debug("sections[" + i + "] : " + sections[i]);
			int vlanid = Integer.parseInt(sections[i]) - 1;
			int pos = vlanid / 8;
			int bitpos = vlanid % 8;
			int orvalue = 1 << (7 - bitpos);

			vlanSetBytes[pos] |= orvalue;
		}

		var = (SnmpVar) new SnmpString(vlanSetBytes);

		return var;
	}



}
