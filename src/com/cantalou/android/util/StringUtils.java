package com.cantalou.android.util;

import android.widget.EditText;
import android.widget.TextView;

/**
 * @author cantalou
 * @date 2016年2月29日 上午10:56:15
 */
public class StringUtils {

    /**
     * <p>Compares two CharSequences, returning {@code true} if they represent
     * equal sequences of characters.</p>
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null}
     * references are considered to be equal. The comparison is case sensitive.</p>
     *
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "abc")  = false
     * StringUtils.equals("abc", null)  = false
     * StringUtils.equals("abc", "abc") = true
     * StringUtils.equals("abc", "ABC") = false
     * </pre>
     *
     * @param cs1 the first CharSequence, may be {@code null}
     * @param cs2 the second CharSequence, may be {@code null}
     * @return {@code true} if the String are equal (case-sensitive), or both {@code null}
     * @see Object#equals(Object)
     * @since 3.0 Changed signature from equals(String, String) to equals(CharSequence, CharSequence)
     */
    public static boolean equals(final String cs1, final String cs2)
    {
        if (cs1 == cs2)
        {
            return true;
        }
        if (cs1 == null || cs2 == null)
        {
            return false;
        }
        return cs1.equals(cs2);
    }

    /**
     * Checks if a CharSequence is whitespace, empty ("") or null.<br>
     * StringUtils.isBlank(null)      = true<br>
     * StringUtils.isBlank("")        = true<br>
     * StringUtils.isBlank(" ")       = true<br>
     * StringUtils.isBlank("bob")     = false<br>
     * StringUtils.isBlank("  bob  ") = false<br>
     *
     * @param str the String to check, may be null
     * @return if the String is null, empty or whitespace
     * @author LinZhiWei
     * @date 2015年5月6日 下午1:55:43
     */
    public static boolean isBlank(CharSequence str)
    {
        int strLen;
        if (str == null || (strLen = str.length()) == 0)
        {
            return true;
        }
        for (int i = 0; i < strLen; i++)
        {
            if ((Character.isWhitespace(str.charAt(i)) == false))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a CharSequence is whitespace, empty ("") or null.<br>
     * StringUtils.isBlank(null)      = true<br>
     * StringUtils.isBlank("")        = true<br>
     * StringUtils.isBlank(" ")       = true<br>
     * StringUtils.isBlank("bob")     = false<br>
     * StringUtils.isBlank("  bob  ") = false<br>
     *
     * @param tv the TextView to check, may be null
     * @return if the EditText is null, empty or whitespace
     * @author LinZhiWei
     * @date 2015年5月6日 下午1:55:43
     */
    public static boolean isBlank(TextView tv)
    {
        return tv == null || isBlank(tv.getText());
    }

    /**
     * Checks if a CharSequence is  not whitespace, not empty ("") ,not null.<br>
     *
     * @param str the String to check, may be null
     * @return trur if the String is not null, not empty , not whitespace
     * @author LinZhiWei
     * @date 2015年5月6日 下午1:55:43
     */
    public static boolean isNotBlank(CharSequence str)
    {
        return !isBlank(str);
    }

    /**
     * Checks if a EditText is  not whitespace, not empty ("") ,not null.<br>
     *
     * @param str the String to check, may be null
     * @return trur if the String is not null, not empty , not whitespace
     * @author LinZhiWei
     * @date 2015年5月6日 下午1:55:43
     */
    public static boolean isNotBlank(EditText str)
    {
        return str != null && !isBlank(str.getText());
    }

}