package com.ocr.tesseract;

import com.ocr.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author qingshui on 2018-09-26
 */
class ProcessOutputThread extends Thread {

	private InputStream is;

	private StringBuffer outputBuffer;

	public ProcessOutputThread(InputStream is) throws IOException {
		if (null == is) {
			throw new IOException("the provided InputStream is null");
		}
		this.is = is;
		this.outputBuffer = new StringBuffer();
	}

	public StringBuffer getOutputBuffer() {
		return this.outputBuffer;
	}

	@Override
	public void run() {
		InputStreamReader ir = null;
		BufferedReader br = null;
		try {
			ir = new InputStreamReader(this.is);
			br = new BufferedReader(ir);
			String output = null;
			while (null != (output = br.readLine())) {
				outputBuffer.append(output).append(System.getProperty(Constants.PROPERTY_LINE_SEPARATOR));
			}
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			try {
				if (null != br) {
					br.close();
				}

				if (null != ir) {
					ir.close();
				}

				if (null != this.is) {
					this.is.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}

		}
	}

}
