
package com.raisecom.adapter;

import com.raisecom.ems.templet.server.adapter.MibNodeAdapter;
import com.raisecom.ems.templet.util.ExtendNumberList;
import com.raisecom.nms.platform.cnet.ObjService;
import com.raisecom.nms.snmp.SnmpString;
import com.raisecom.nms.snmp.SnmpVar;
import com.raisecom.nms.util.ServerUtilities;

/**
 * PortList类型节点适配器 *
 */
public class TrunkPortListMibNodeAdapter extends MibNodeAdapter
{
	public TrunkPortListMibNodeAdapter()
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
			byte portlist[] = var.toBytes();
			String retval = "";
			for (int i = 0; i < portlist.length; i++)
			{
				int andvalue = 128;
				for (int j = 1; j <= 8; j++)
				{
					if ((portlist[i] & andvalue) > 0)
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
	public SnmpVar writeMibData(String value, ObjService mibnode)
	{
		String syntaxName = mibnode.getStringValue("SyntaxName");
		String label = mibnode.getStringValue("Label");

		ServerUtilities.debug("[" + label + "] Name is [" + syntaxName + "], Value is [" + value + "]");
		SnmpVar var = null;

		byte portlist[] = new byte[512];
		for (int i = 0; i < portlist.length; i++)
			portlist[i] = 0;

		value = value.trim();
		if (value.equals(""))
		{
			var = (SnmpVar) new SnmpString(portlist);
			return var;
		}

		String sections[] = ExtendNumberList.getNumberStringList(value);

		for (int i = 0; i < sections.length; i++)
		{
			int port = Integer.parseInt(sections[i]) - 1;

			int pos = port / 8;
			int bitpos = port % 8;
			int orvalue = 1 << (7 - bitpos);

			portlist[pos] |= orvalue;
		}

		var = (SnmpVar) new SnmpString(portlist);

		return var;
	}


}
