package com.raisecom.adapter;

import com.raisecom.util.EPONConstants;

import java.util.StringTokenizer;
import java.util.Vector;



public class NumberList {
    /**
     * 返回数字列表字符串中的最大值
     * @param strNumberList 数字列表字符串
     * @return 最大值
     */
    public static int getMaxNumber(String strNumberList)
    {
        StringTokenizer tk = new StringTokenizer(strNumberList, ", -");
        int m = Integer.MIN_VALUE;
        while (tk.hasMoreTokens())
        {
            int l = Integer.parseInt(tk.nextToken());
            if (l > m)
                m = l;
        }

        return m;
    }

    /**
     * 返回数字列表字符串中的最小值
     * @param strNumberList 数字列表字符串
     * @return 最小值
     */
    public static int getMinNumber(String strNumberList)
    {
        StringTokenizer tk = new StringTokenizer(strNumberList, ", -");
        int m = Integer.MAX_VALUE;
        while (tk.hasMoreTokens())
        {
            int l = Integer.parseInt(tk.nextToken());
            if (l < m)
                m = l;
        }

        return m;
    }

    /**
     * 
     * @param strNumberList 要格式化的字符串
     * @param vRange 范围列表，格式化后的范围保存在此列表中
     * @param vNumber 单个数字列表，格式化后的不连续数字保存在此列表中
     * @return true,成功；false失败
     */
    private static boolean getRange(String strNumberList, Vector vRange, Vector vNumber)
    {
        try
        {
            vRange.clear();
            vNumber.clear();
            StringTokenizer tk = new StringTokenizer(strNumberList, ",");
            String strList[] = new String[tk.countTokens()];
            int iPoint = 0;
            while (tk.hasMoreTokens())
            {
                String temp = tk.nextToken();
                if (temp.indexOf("-") != -1)
                {
                    StringTokenizer tkRange = new StringTokenizer(temp, "-");
                    int iMin = Integer.valueOf(tkRange.nextToken()).intValue();
                    int iMax = Integer.valueOf(tkRange.nextToken()).intValue();
                    if (iMin > iMax)
                        vRange.add("" + iMax + "-" + iMin);
                    else
                        vRange.add(temp);
                }
                else
                {
                    strList[iPoint] = temp;
                    iPoint++;
                }
            }

            vNumber.setSize(iPoint);
            for (int i = 0; i < iPoint; i++)
                vNumber.set(i, strList[i]);

            return true;
        }
        catch (Exception e)
        {
        	EPONConstants.logger.error(e);
            return false;
        }
    }

    /**
     * 判断字符串是否为合法的数字列表字符串
     * @param strNumberList 字符串
     * @return 0：合法；1：存在非法字符；2,4：数字列表中的数字超范围；3：数字列表格式不正确；
     */
    public static int isNumberListString(String strNumberList)
    {
        String strRange = "0123456789, -";
        for (int i = 0; i < strNumberList.length(); i++)
        {
            char c = strNumberList.charAt(i);
            if (strRange.indexOf(c) == -1)
                return 1;
        }
        //去两端空格
        if (strNumberList.startsWith(" ") || strNumberList.endsWith(" "))
            strNumberList = strNumberList.trim();

        //将字符串中一个以上的连续空格压缩成一个空格
        while (strNumberList.indexOf("  ") != -1)
            strNumberList = strNumberList.replaceAll("  ", " ");

        //去逗号左右空格
        while (strNumberList.indexOf(", ") != -1)
            strNumberList = strNumberList.replaceAll(", ", ",");
        while (strNumberList.indexOf(" ,") != -1)
            strNumberList = strNumberList.replaceAll(" ,", ",");
        //去横线左右空格
        while (strNumberList.indexOf("- ") != -1)
            strNumberList = strNumberList.replaceAll("- ", "-");
        while (strNumberList.indexOf(" -") != -1)
            strNumberList = strNumberList.replaceAll(" -", "-");

        //使用逗号代替空格
        strNumberList = strNumberList.replaceAll(" ", ",");
        /////////////////////////////////////////////////////////////////////////////////

        Vector vRange = new Vector(0);
        Vector vNumber = new Vector(0);
        boolean b = getRange(strNumberList, vRange, vNumber);
        //int max = getMaxNumber(strNumberList);
        // int min = getMinNumber(strNumberList);

        for (int i = 0; i < vNumber.size(); i++)
        {
            int iNumber = Integer.parseInt(vNumber.get(i).toString());
            if (iNumber < 0 || iNumber > Integer.MAX_VALUE)
                return 2;
        }

        for (int i = 0; i < vRange.size(); i++)
        {
            String strSubRange = vRange.get(i).toString();
            StringTokenizer tkRange = new StringTokenizer(strSubRange, "-");
            if (tkRange.countTokens() != 2)
                return 3;
            int min = Integer.valueOf(tkRange.nextToken()).intValue();
            int max = Integer.valueOf(tkRange.nextToken()).intValue();
            for (int j = min; j <= max; j++)
            {
                if (j < 0 || j > Integer.MAX_VALUE)
                    return 4;

            }
        }

        return 0;
    }

    /**
     * 转换扩展数字列表字符串到数字字符串列表
     * @param strNumberList 扩展数字列表字符串
     * @return  数字字符串列表
     * 
     */
    public static int[] getNumberStringList(String strNumberList, int iMax)
    {
        int[] list = new int[iMax];
        /////////////////////////////////////////////////////////////////////////////////
        //格式化字符串

        //去两端空格
        if (strNumberList.startsWith(" ") || strNumberList.endsWith(" "))
            strNumberList = strNumberList.trim();

        //将字符串中一个以上的连续空格压缩成一个空格
        while (strNumberList.indexOf("  ") != -1)
            strNumberList = strNumberList.replaceAll("  ", " ");

        //去逗号左右空格
        while (strNumberList.indexOf(", ") != -1)
            strNumberList = strNumberList.replaceAll(", ", ",");
        while (strNumberList.indexOf(" ,") != -1)
            strNumberList = strNumberList.replaceAll(" ,", ",");
        //去横线左右空格
        while (strNumberList.indexOf("- ") != -1)
            strNumberList = strNumberList.replaceAll("- ", "-");
        while (strNumberList.indexOf(" -") != -1)
            strNumberList = strNumberList.replaceAll(" -", "-");

        //使用逗号代替空格
        strNumberList = strNumberList.replaceAll(" ", ",");
        /////////////////////////////////////////////////////////////////////////////////

        //System.err.println(strNumberList);

        Vector vRange = new Vector(0);
        Vector vNumber = new Vector(0);
        boolean b = getRange(strNumberList, vRange, vNumber);
        //int max = getMaxNumber(strNumberList);
        // int min = getMinNumber(strNumberList);

        for (int i = 0; i < vNumber.size(); i++)
        {
            int iNumber = Integer.parseInt(vNumber.get(i).toString());
            if (iNumber < 0 || iNumber > iMax)
                continue;
            list[iNumber] = 1;
        }

        for (int i = 0; i < vRange.size(); i++)
        {
            String strRange = vRange.get(i).toString();
            StringTokenizer tkRange = new StringTokenizer(strRange, "-");
            int min = Integer.valueOf(tkRange.nextToken()).intValue();
            int max = Integer.valueOf(tkRange.nextToken()).intValue();
            for (int j = min; j <= max; j++)
            {
                if (j < 0 || j > iMax)
                    continue;

                list[j] = 1;
            }
        }

        return list;
    }

    /**
     * 根据缓冲取返回数字列表
     * @param buffers 缓冲区
     * @return 数字列表
     */
    public static Vector getNumberStringlist(int[] buffers)
    {
        Vector vNumber = new Vector();
        for (int i = 0; i < buffers.length; i++)
            if (buffers[i] == 1)
                vNumber.add("" + i);

        return vNumber;
    }

    /**
     * 根据byte流返回数字列表，起始值为0
     * @param bytes byte流
     * @return 数字列表
     */
    public static String getNumberStringList(byte[] bytes)
    {
        return getNumberStringList(bytes, 0);
    }

    /**
     * 根据byte流返回数字列表
     * @param bytes byte流
     * @param iStartNumber 起始值
     * @return 数字列表
     */
    public static String getNumberStringList(byte[] bytes, int iStartNumber)
    {
        int[] list = new int[bytes.length * 8 + 1];

        for (int i = 0; i < list.length; i++)
            list[i] = 0;

        for (int i = 0; i < bytes.length; i++)
        {
            int andvalue = 128;
            for (int j = 1; j <= 8; j++)
            {
                if ((bytes[i] & andvalue) > 0 && i * 8 + j <= 4096)
                    list[i * 8 + j - 1] = 1;
                andvalue = andvalue >>> 1;
            }
        }

        String strNumberlist = "";
        int point = 0;
        int start = 0;
        int end = 0;
        boolean isRange = false;

        while (point < list.length)
        {
            if (list[point] == 1)
            {
                if (!isRange)
                {
                    start = point + iStartNumber;
                    isRange = true;
                }
                end = point + iStartNumber;

                //                if (point == list.length - 1)
                //                {
                //                    if (isRange)
                //                    {
                //                        if (start == end)
                //                            strNumberlist = strNumberlist + "" + start + " ";
                //                        else
                //                            strNumberlist = strNumberlist + "" + start + "-" + end + " ";
                //
                //                        isRange = false;
                //                    }
                //                }
            }
            else
            {
                if (isRange)
                {
                    if (start == end)
                        strNumberlist = strNumberlist + "" + start + " ";
                    else
                        strNumberlist = strNumberlist + "" + start + "-" + end + " ";

                    isRange = false;
                }
            }

            point++;
        }

        strNumberlist = strNumberlist.trim().replaceAll(" ", ",");

        return strNumberlist;
    }



}
