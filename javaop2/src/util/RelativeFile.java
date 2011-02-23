/*
 * Created on Dec 4, 2004 By iago
 */

package com.javaop.util;

import java.io.File;


public class RelativeFile extends File
{
    private static final long serialVersionUID = 1L;

    public RelativeFile(String file)
    {
        super((file.length() > 0 && (file.charAt(0) == '/' || file.matches(".\\:\\\\.*"))) ? (file)
                : (System.getProperty("user.dir") + "/" + file));
    }
}