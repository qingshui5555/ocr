package com.ocr;

/**
 * @author qingshui on 2018-09-26
 */
public class Constants {
	public static String PROPERTY_USER_DIR = "user.dir";
	public static String PROPERTY_LINE_SEPARATOR = "line.separator";

	public static String COMMAND_TESSERACT = "tesseract";
	public static String COMMAND_LANGUAGE_SEPARATOR = "-l";

	public static String FILE_OUTPUT_NAME = "output";
	public static String FILE_EXTENSION_TXT = ".txt";
	public static String FILE_CHARSET_NAME_UTF8 = "UTF-8";

	public static String ERROR_MSG_1 = "Errors accessing files. There may be spaces in your image's filename.";
	public static String ERROR_MSG_29 = "Cannot recognize the image or its selected region.";
	public static String ERROR_MSG_31 = "Unsupported image format.";
	public static String ERROR_MSG = "Errors occurred.";

	public static String EXPRESSION_IN_LINE = "[A-Za-z+\\s\\-\\.\\/]+\\s+\\d+.?\\d+\\s+";

	public static String CARRIAGE_RETURN = "\r";
	public static String EMPTY_STRING = " ";
	public static String SPLIT_STRING = "#";
}
