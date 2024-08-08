package com.progressoft.juno.minifier.css;

import com.progressoft.juno.minifier.AbstractMinifierTest;
import com.progressoft.juno.minifier.GeneralMinifier;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;

public class CSSMinifierTest extends AbstractMinifierTest {

	private static final List<String> RESOURCES = Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
			"17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28");

	private static final String EXTENSION = "css";

	@Override
	protected String extension() {
		return EXTENSION;
	}

	@Override
	protected GeneralMinifier miniferForReader(Reader reader) {
		return new CSSMinifier(reader);
	}

	@Override
	protected List<String> resources() {
		return RESOURCES;
	}
}
