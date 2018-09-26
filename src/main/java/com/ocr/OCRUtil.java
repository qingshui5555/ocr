package com.ocr;

public class OCRUtil {
	public static String chiSIM = "chi_sim";

	public static void runOCR(String realPath, String imagePath, String outPath, boolean isChi) throws Exception{
		Runtime r = Runtime.getRuntime();
		String cmd = "\""+realPath+"Tesseract-OCR\\tesseract.exe\" \""+imagePath+"\" \""+outPath+"\" -l "+(isChi?chiSIM:"");
		r.exec(cmd);
	}
}

