package com.ocr;

import java.util.List;

public class OcrText {

	private String key;

	private String value;

	private String unit;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public String toString(){
		return this.key + " " + this.value + " " + this.unit;
	}
}
