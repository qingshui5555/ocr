package com.ocr;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kwqb535 on 2018-09-07
 */
public class TesseractTest {

	private static String[] title = {"test", "method", "result", "units", "comments"};
	private static String[] units = {"U/L","u/L","g/L","mg/dL", "mmol/mol", "mmol/L", "%", "µmol/L", "IU/L", "mEq/L"};
	private static Map<String, String> resultMap = new LinkedHashMap<String,String>();

	private static boolean checkTitle(String[] lines){
		if(lines == null || lines.length != title.length){
			return false;
		}
		for(int i=0; i<lines.length; i++){
			if(!lines[i].equalsIgnoreCase(title[i])){
				return false;
			}
		}

		return true;
	}

	private static boolean checkData(String[] lines){
		if(lines == null || lines.length != title.length){
			return false;
		}

		return true;
	}


	public static Map<String, OcrText> dealwithText1(String text){
		Map<String, OcrText> textMap = new LinkedHashMap<>(16);
		String[] lines = text.split("\n");
		for(String line : lines){
			String s = "[A-Za-z+\\s\\-\\.\\/]+\\s+\\d+.?\\d+\\s+";
			Pattern pattern = Pattern.compile(s);
			if(line.indexOf("(") > 0 && line.indexOf(")") > 0 && line.indexOf(")") > line.indexOf("(")) {
				String bracket = line.substring(line.indexOf("("), line.indexOf(")") + 1);
				line = line.replace(bracket, "");
			}
			Matcher ma = pattern.matcher(line);
			while(ma.find()){
				String str = ma.group().trim();
				int n = str.lastIndexOf(" ");
				String key = str.substring(0, n).trim();
				OcrText ocrText = new OcrText();
				ocrText.setKey(key);
				ocrText.setValue(str.substring(n+1, str.length()).trim());
				for(String unit : units){
					if(line.contains(unit)){
						ocrText.setUnit(unit);
						break;
					}
				}
				textMap.put(key, ocrText);
			}
		}
		return textMap;
	}

	public static Map<String, OcrText> expression(String text){
		Map<String, OcrText> textMap = new LinkedHashMap<>(16);
		String s = "[A-Za-z+\\s\\-\\.\\/]+\\s+\\d+.?\\d+\\s+";
		Pattern pattern = Pattern.compile(s);
		Matcher ma = pattern.matcher(text);
		while(ma.find()){
			String str = ma.group().trim();
			int n = str.lastIndexOf(" ");
			String key = str.substring(0, n).trim();
			String value = str.substring(n, str.length()).trim();
			if(!isNumeric(value)){
				continue;
			}
			OcrText ocrText = new OcrText();
			ocrText.setKey(key);
			ocrText.setValue(value);
			textMap.put(key, ocrText);
		}
		return textMap;
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("^(\\-|\\+)?\\d+(\\.\\d+)?$");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

//	public static Map<String, OcrText> dealwithText2(String text){
//		Map<String, OcrText> textMap = new LinkedHashMap<>(16);
//		String[] lines = text.split("\n");
//		for(String line : lines){
//			String s = "\\d+.\\d+|\\<\\s+\\d+.\\d+|\\<\\d+.\\d+|\\w+\\/\\w+|\\w+(,)\\s+\\w+|\\w+(—)\\w+|\\w+";
//			Pattern pattern = Pattern.compile(s);
//			Matcher ma = pattern.matcher(line);
//			OcrText ocrText = new OcrText();
//			String key = "";
//			int i = 0;
//			int m = 0;
//			boolean flag = false;
//			while(ma.find()){
//				String str = ma.group();
//				if(str.matches("(<)\\s+-?[0-9]+.*[0-9]*|-?[0-9]+.*[0-9]*")){
//					ocrText.setValue(str);
//					m = i;
//					flag = true;
//				}
//				if(i == 0){
//					key = str;
//					ocrText.setKey(str);
//				} else if(i == m+1 && flag){
//					if(str.matches("(/)")) {
//						ocrText.setUnit(str);
//					}else{
//						m = i;
//					}
//				} else if(!flag && ocrText.getExtensions() == null){
//					ocrText.setExtensions(new ArrayList<String>());
//					ocrText.getExtensions().add(str);
//				} else if(!flag){
//					ocrText.getExtensions().add(str);
//				}
//
//				i++;
//			}
//			if(flag) {
//				textMap.put(key, ocrText);
//			}
//		}
//		return textMap;
//	}
//

	public static String recognizeTextByTess4j(String language, File imageFile) throws Exception {
		ITesseract instance = new Tesseract();
		instance.setDatapath(System.getProperty("user.dir") + File.separator + "tessdata");
		instance.setLanguage(language);
		return instance.doOCR(imageFile);
	}

	public static String recognizeTextByCommand(String language, File imageFile) throws Exception {
		File outputFile = new File(imageFile.getParentFile(),"output");
		StringBuffer strB = new StringBuffer();

		List<String> cmd = new ArrayList<>();
		cmd.add("tesseract");
		cmd.add(imageFile.getName());
		cmd.add(outputFile.getName());
		cmd.add("-l");
		cmd.add(language);

		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(imageFile.getParentFile());
		pb.command(cmd);
		pb.redirectErrorStream(true);
		Process process = pb.start();
		int w = process.waitFor();
		if (w == 0){
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile.getAbsolutePath()+ ".txt"),"UTF-8"));
			String str;
			while ((str = in.readLine()) != null) {
				strB.append(str).append(System.getProperty("line.separator"));
			}
			in.close();
		} else{
			String msg;
			switch (w){
				case 1:
					msg = "Errors accessing files. There may be spaces in your image's filename.";
					break;
				case 29:
					msg = "Cannot recognize the image or its selected region.";
					break;
				case 31:
					msg = "Unsupported image format.";
					break;
				default:
					msg = "Errors occurred.";
			} throw new RuntimeException(msg);
		}
//		new File(outputFile.getAbsolutePath()+ ".txt").delete();
		return strB.toString();
	}

	public static void main(String[] args){
		try{
			Long startTime = System.currentTimeMillis();
			File imageFile = new File("C:\\Users\\kwqb535\\Desktop\\Henry\\ocr\\cut\\labtest1.jpg");
			String result = recognizeTextByCommand("eng", imageFile);
			System.out.println("=========================begin ocr =================================");
			System.out.println(result);
			System.out.println("=========================end ocr =================================");
			System.out.println("=========================begin code =================================");
			Map<String, OcrText> textMap = dealwithText1(result);
			for(Map.Entry<String, OcrText> entry : textMap.entrySet()){
				System.out.println(entry.getValue().toString());
			}
			System.out.println("=========================end code =================================");

			System.out.println("Cost Time: " + (System.currentTimeMillis() - startTime)/1000 + "s");
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}
