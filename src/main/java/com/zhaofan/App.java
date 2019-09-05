package com.zhaofan;

import com.zhaofan.util.CommonUtils;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println(CommonUtils.class.getClassLoader().getResourceAsStream("datasource.properties"));
        System.out.println( "Hello World!" );
    }
}
