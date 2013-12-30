package com.check.v3.api;

public class ApiConstant
{
  public static final String BASE = "base";
  public static final String ERROR = "errors";
  public static final String FIELD = "field";
  public static final String MESSAGE = "message";
  
  
  public static final String KEY_QUICK_CHECK_ACTION = "check_action_key";
  public static final String KEY_QUICK_CHECK_DATA = "check_data_key";
  public static final int QUICK_CHECK_NONE_ACTION = 0;
  public static final int QUICK_CHECK_EDIT_NEW_ACTION = 1;
  public static final int QUICK_CHECK_EDIT_EXIST_ACTION = 2;
  public static final int QUICK_CHECK_VIEW_ACTION = 3;
  public static final int QUICK_CHECK_VIEW_RSP_ACTION = 4;
  
  
  public static final String ORIG_REPORT_ID = "orig_report_id";
  public static final String ORIG_RESOLUTION_ID = "orig_resolution_id";
  public static final String ORIG_RESOLUTION_DATA = "orig_resolution_data";
  public static final String WHAT_ACTION = "what_action";
  public static final int ACTION_MODE_RSLV_NEW_ADDED = 1;
  public static final int ACTION_MODE_RSLV_EDIT_EXIST = 2;
  
  public static final int ACTION_MODE_VIEW_RSLV_FROM_LIST = 11;
  public static final int ACTION_MODE_VIEW_RSLV_FROM_SUBMIT_RSP = 12;
  
  public static final String KEY_QUICK_CHECK_VIEW_PHOTO_URL_LIST = "qc_view_photo_url_list";
  
  public static final String QUICK_REPORT_JSON_PART_KEY = "quickReportJson";
  public static final String QUICK_REPORT_FILE_PART_KEY = "quickReportImages";
  
  public static final String QUICK_REPORT_RSLV_JSON_PART_KEY = "quickReportResolveJson";
  public static final String QUICK_REPORT_RSLV_FILE_PART_KEY = "quickReportResolveImages";
  
  public static final String JSON_CONTENT_TYPE = "application/json";
  public static final String IMAGE_JPEG_CONTENT_TYPE = "image/jpeg";
}
