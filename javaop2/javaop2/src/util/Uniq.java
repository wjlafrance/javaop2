/*
 * Created on Feb 4, 2005 By iago
 */
package com.javaop.util;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;


public class Uniq
{
    public static String[] uniq(Enumeration e)
    {
        if (e == null)
            return new String[0];

        Vector v = new Vector();

        while (e.hasMoreElements())
            v.add(e.nextElement());

        return uniq(v);
    }

    public static String[] uniq(Vector v)
    {
        if (v == null)
            return new String[0];

        String[] s = new String[v.size()];
        for (int i = 0; i < v.size(); i++)
            s[i] = v.get(i).toString();

        return uniq(s);
    }

    public static String[] uniq(String[] str)
    {
        if (str == null)
            return new String[0];

        if (str.length == 0)
            return str;

        Arrays.sort(str);

        Vector ret = new Vector();

        String current = str[0];
        ret.add(current);
        for (int i = 1; i < str.length; i++)
        {
            if (current.equalsIgnoreCase(str[i]))
                continue;

            current = str[i];
            ret.add(current);
        }

        return (String[]) ret.toArray(new String[ret.size()]);
    }
}
