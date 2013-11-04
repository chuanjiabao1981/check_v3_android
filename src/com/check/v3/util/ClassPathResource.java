package com.check.v3.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassPathResource
{
  public static boolean isEmail(String paramString)
  {
    return Pattern.compile("^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$").matcher(paramString).matches();
  }

  public static boolean isMobileNO(String paramString)
  {
    return Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$").matcher(paramString).matches();
  }
}