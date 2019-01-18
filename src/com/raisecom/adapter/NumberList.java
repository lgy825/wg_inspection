package com.raisecom.adapter;

import com.raisecom.util.EPONConstants;

import java.util.StringTokenizer;
import java.util.Vector;



public class NumberList {
    /**
     * ���������б��ַ����е����ֵ
     * @param strNumberList �����б��ַ���
     * @return ���ֵ
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
     * ���������б��ַ����е���Сֵ
     * @param strNumberList �����б��ַ���
     * @return ��Сֵ
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
     * @param strNumberList Ҫ��ʽ�����ַ���
     * @param vRange ��Χ�б���ʽ����ķ�Χ�����ڴ��б���
     * @param vNumber ���������б���ʽ����Ĳ��������ֱ����ڴ��б���
     * @return true,�ɹ���falseʧ��
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
     * �ж��ַ����Ƿ�Ϊ�Ϸ��������б��ַ���
     * @param strNumberList �ַ���
     * @return 0���Ϸ���1�����ڷǷ��ַ���2,4�������б��е����ֳ���Χ��3�������б��ʽ����ȷ��
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
        //ȥ���˿ո�
        if (strNumberList.startsWith(" ") || strNumberList.endsWith(" "))
            strNumberList = strNumberList.trim();

        //���ַ�����һ�����ϵ������ո�ѹ����һ���ո�
        while (strNumberList.indexOf("  ") != -1)
            strNumberList = strNumberList.replaceAll("  ", " ");

        //ȥ�������ҿո�
        while (strNumberList.indexOf(", ") != -1)
            strNumberList = strNumberList.replaceAll(", ", ",");
        while (strNumberList.indexOf(" ,") != -1)
            strNumberList = strNumberList.replaceAll(" ,", ",");
        //ȥ�������ҿո�
        while (strNumberList.indexOf("- ") != -1)
            strNumberList = strNumberList.replaceAll("- ", "-");
        while (strNumberList.indexOf(" -") != -1)
            strNumberList = strNumberList.replaceAll(" -", "-");

        //ʹ�ö��Ŵ���ո�
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
     * ת����չ�����б��ַ����������ַ����б�
     * @param strNumberList ��չ�����б��ַ���
     * @return  �����ַ����б�
     * 
     */
    public static int[] getNumberStringList(String strNumberList, int iMax)
    {
        int[] list = new int[iMax];
        /////////////////////////////////////////////////////////////////////////////////
        //��ʽ���ַ���

        //ȥ���˿ո�
        if (strNumberList.startsWith(" ") || strNumberList.endsWith(" "))
            strNumberList = strNumberList.trim();

        //���ַ�����һ�����ϵ������ո�ѹ����һ���ո�
        while (strNumberList.indexOf("  ") != -1)
            strNumberList = strNumberList.replaceAll("  ", " ");

        //ȥ�������ҿո�
        while (strNumberList.indexOf(", ") != -1)
            strNumberList = strNumberList.replaceAll(", ", ",");
        while (strNumberList.indexOf(" ,") != -1)
            strNumberList = strNumberList.replaceAll(" ,", ",");
        //ȥ�������ҿո�
        while (strNumberList.indexOf("- ") != -1)
            strNumberList = strNumberList.replaceAll("- ", "-");
        while (strNumberList.indexOf(" -") != -1)
            strNumberList = strNumberList.replaceAll(" -", "-");

        //ʹ�ö��Ŵ���ո�
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
     * ���ݻ���ȡ���������б�
     * @param buffers ������
     * @return �����б�
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
     * ����byte�����������б���ʼֵΪ0
     * @param bytes byte��
     * @return �����б�
     */
    public static String getNumberStringList(byte[] bytes)
    {
        return getNumberStringList(bytes, 0);
    }

    /**
     * ����byte�����������б�
     * @param bytes byte��
     * @param iStartNumber ��ʼֵ
     * @return �����б�
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
