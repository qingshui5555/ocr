package com.ocr.tesseract;

import com.ocr.Constants;
import net.sourceforge.tess4j.ITesseract;

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
 * @author qingshui on 2018-09-26
 */
public class TesseractOcr {

	/**
	 * analysis text with expression by lines
	 * @param text
	 * @return
	 */
	public static Map<String, Object> analysisByLines(String text, String expression){
		Map<String, Object> textMap = new LinkedHashMap<>(16);
		String[] lines = text.split(System.getProperty(Constants.PROPERTY_LINE_SEPARATOR));
		for(String line : lines){
			textMap.putAll(analysis(line, expression));
		}
		return textMap;
	}

	/**
	 * analysis text with expression by whole text
	 * @param text
	 * @param expression
	 * @return
	 */
	public static Map<String, Object> analysis(String text, String expression){
		Map<String, Object> resultMap = new LinkedHashMap<>(16);
		Pattern pattern = Pattern.compile(expression);
		Matcher ma = pattern.matcher(text);
		while(ma.find()){
			String str = ma.group().trim();
			int n = str.lastIndexOf(Constants.EMPTY_STRING);
			if (n < 1 || str.length() < 1) {
				break;
			}
			String key = str.substring(0, n).trim();
			String value = str.substring(n, str.length()).trim();
			resultMap.put(key, key + Constants.SPLIT_STRING + value);
		}
		return resultMap;
	}

	/**
	 * recognize text by tess4j
	 * @param language
	 * @param imageFile
	 * @return
	 * @throws Exception
	 */
	public static String recognizeTextByTess4j(String language, File imageFile) throws Exception {
		ITesseract instance = new net.sourceforge.tess4j.Tesseract();
		instance.setDatapath(System.getProperty(Constants.PROPERTY_USER_DIR));
		instance.setLanguage(language);
		return instance.doOCR(imageFile);
	}

	/**
	 * recognize text by command
	 * @param language
	 * @param imageFile
	 * @return
	 * @throws Exception
	 */
	public static String recognizeTextByCommand(String language, File imageFile) throws Exception {
		File outputFile = new File(imageFile.getParentFile(),Constants.FILE_OUTPUT_NAME);
		StringBuffer stringBuffer = new StringBuffer();

		List<String> cmd = new ArrayList<>();
		cmd.add(Constants.COMMAND_TESSERACT);
		cmd.add(imageFile.getName());
		cmd.add(outputFile.getName());
		cmd.add(Constants.COMMAND_LANGUAGE_SEPARATOR);
		cmd.add(language);

		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(imageFile.getParentFile());
		pb.command(cmd);
		pb.redirectErrorStream(true);
		Process process = pb.start();

		Thread outputThread = new ProcessOutputThread(process.getInputStream());
		Thread errorOutputThread = new ProcessOutputThread(process.getErrorStream());
		outputThread.start();
		errorOutputThread.start();

		int rs = process.waitFor();
		outputThread.join();
		errorOutputThread.join();
		if (rs == 0){
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile.getAbsolutePath() + Constants.FILE_EXTENSION_TXT), Constants.FILE_CHARSET_NAME_UTF8));
			String str;
			while ((str = in.readLine()) != null) {
				stringBuffer.append(str).append(System.getProperty(Constants.PROPERTY_LINE_SEPARATOR));
			}
			in.close();
		} else{
			String msg;
			switch (rs){
				case 1:
					msg = Constants.ERROR_MSG_1;
					break;
				case 29:
					msg = Constants.ERROR_MSG_29;
					break;
				case 31:
					msg = Constants.ERROR_MSG_31;
					break;
				default:
					msg = Constants.ERROR_MSG;
			} throw new RuntimeException(msg);
		}
		new File(outputFile.getAbsolutePath() + Constants.FILE_EXTENSION_TXT).delete();
		return stringBuffer.toString();
	}


	public static void main(String[] args){
		try{
			Long startTime = System.currentTimeMillis();
			Long endTime;
			File imageFile = new File("C:\\Users\\kwqb535\\Desktop\\Henry\\ocr\\cut\\labtest1.jpg");
			String resultTess4j = recognizeTextByTess4j(TesseractLanguage.ENG + "+" + TesseractLanguage.CHI_SIM, imageFile);
			System.out.println("=========================begin ocr Tess4j=================================");
			System.out.println(resultTess4j);
			System.out.println("=========================end ocr Tess4j=================================");
			endTime = System.currentTimeMillis();
			System.out.println("Ocr Cost Time: " + (endTime - startTime) + "ms");

			System.out.println("=========================begin analysis Tess4j=================================");
			Map<String, Object> textTess4jMap = analysisByLines(resultTess4j, Constants.EXPRESSION_IN_LINE);
			for(Map.Entry<String, Object> entry : textTess4jMap.entrySet()){
				System.out.println(entry.getValue().toString());
			}
			System.out.println("=========================end analysis Tess4j=================================");
			startTime = endTime;
			endTime = System.currentTimeMillis();
			System.out.println("Analysis Cost Time: " + (endTime - startTime) + "ms");

			String resultCommand = recognizeTextByCommand(TesseractLanguage.ENG + "+" + TesseractLanguage.CHI_SIM, imageFile);
			System.out.println("=========================begin ocr Command=================================");
			System.out.println(resultCommand);
			System.out.println("=========================end ocr Command=================================");
			startTime = endTime;
			endTime = System.currentTimeMillis();
			System.out.println("Ocr Cost Time: " + (endTime - startTime) + "ms");

			System.out.println("=========================begin analysis Command=================================");
			Map<String, Object> textCommandMap = analysis(resultCommand, Constants.EXPRESSION_IN_LINE);
			for(Map.Entry<String, Object> entry : textCommandMap.entrySet()){
				System.out.println(entry.getValue().toString());
			}
			System.out.println("=========================end analysis Command=================================");
			startTime = endTime;
			endTime = System.currentTimeMillis();
			System.out.println("Analysis Cost Time: " + (endTime - startTime) + "ms");
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}
