/**
 * 
 */
package com.raisecom.adapter;

/**
 * 
 * 
 * 
 * ONU层次,onu ID 以两位显示 0-99， 大于99是需用到前置码：
 *   100=<ONU id <=199   需在索引上加前置码 28 bit ~31 bit 二进制： 110000000000000000000000000000  十进制 805306368
 *   200=<ONU id <=299   需在索引上加前置码 28 bit ~31 bit 二进制：1000000000000000000000000000000  十进制 1073741824
 *      
 * 
 * 
 * 
 * 
 * 
 * @author kangzj-1550
 * @since  2014-1-20
 */
public class IfIndexHelperGp {
	
	public static final int LAYERTYPE = 0XFFFFFFF;
	public static final int PREFIX_3 = 805306368;
	public static final int PREFIX_4 = 1073741824;
	
	/**
	 * ONU
	 * 
	 * 
	10201001    100110111010011110101001
	
	10200001
	10200100
	  100000

	815507369   110000100110111010011110101001   
	ONU PORT 815508369   ONU Instance 10200102
	 */
	public static void main(String args[])
	{
//		String index = "815508369";//"815507369";
//		String index = "10201001";
		String onu = "10200002";
		
		System.out.println(getOnuIdFromOnuIndex(onu));
		System.out.println(getPortInstance(onu, 1));
		
		//getPortDisplayName("PON", index);
	}
	
	
	public static String getPortDisplayName(String preFix, String index)
	{
		return preFix + " " + getSlotId(index) + "/" + getPonId(index) + "/" + getOnuIdFromPortIndex(index) + "/" + getPortId(index);
	}
	
	
	
	public static String getOnuPortIdFromPortIndex(String index)
	{
		
		return Integer.parseInt(getFormatIndex(index)) % 1000 + "";
	}
	
	/**
	 * 
	 * @param onuInstancde  ONU索引
	 * @param portId  第几个端口
	 * @return
	 */
	public static String getPortInstance(String onuInstance, int portId)
	{
		return getOnuPortPrefixIndex(onuInstance) + portId + "";
	}
	
	
	public static String getOnuInstanceByPort(String index)
	{
		return (Integer.parseInt(getFormatIndex(index)) / 100000 ) * 100000 + Integer.parseInt(getOnuIdFromPortIndex(index)) + "";
	}
	
	public static String getSlotId(String index)
	{
		return Integer.parseInt(getFormatIndex(index)) / 10000000 + "";
	}
	
	/**
	 * 
	 * @param index: ONU index
	 * @return
	 */
	public static String getPonId(String index)
	{
		return (Integer.parseInt(getFormatIndex(index)) / 100000) % 100 + "";
	}
	
	/**
	 * 
	 * @param index   onu 端口 instance
	 * @return
	 */
	public static String getOnuIdFromPortIndex(String index)
	{
		String formatIndex = getFormatIndex(index);
		switch(getLayer(index)){
			case 0 :
			{
				return getOnuIdByFormatPortIndex(formatIndex) + "";
			}
			case 3 :
			{
				return (100 + getOnuIdByFormatPortIndex(formatIndex)) + "";
			}
			case 4 :
			{
				return (200 + getOnuIdByFormatPortIndex(formatIndex)) + "";
			}
		}
		return "--";
	}
	
	/**
	 * 
	 * @param index: port index
	 * @return
	 */
	public static String getPortId(String index)
	{
		return Integer.parseInt(getFormatIndex(index)) % 1000 + "";
	}
	
	/**
	 * 
	 * @param index ONU的索引
	 * @return
	 */
	public static int getOnuIdFromOnuIndex(String index)
	{
		return Integer.parseInt(index) % 100000 ;
	}
	
	
	
	
	/** -------------------私有方法-------------------------------*/
	
	private static int getOnuIdByFormatPortIndex(String index)
	{
		return (Integer.parseInt(index) / 1000) % 100;
	}
	
	/**
	 * 
	 * @param onuId
	 * @return
	 */
	private static int getOnuPortPrefixIndex(String index)
	{
		int onuId = getOnuIdFromOnuIndex(index);
		switch(onuId / 100)
		{
			case 0: return getSlotAndPonPort(index) * 100000 + onuId * 1000;
			case 1: return getSlotAndPonPort(index) * 100000 + (onuId % 100) * 1000 + PREFIX_3;
			case 2: return getSlotAndPonPort(index) * 100000 + (onuId % 100) * 1000 + PREFIX_4;
		}
		return 0;
	}
	
	
	private static int getSlotAndPonPort(String index)
	{
		
		return Integer.parseInt(index) / 100000;
	}
	
	private static String getFormatIndex(String index)
	{
		return (Integer.parseInt(index) & LAYERTYPE) + "";
	}
	
	private static int getLayer(String index)
	{
		return Integer.parseInt(index) >> 28;
	}

}
