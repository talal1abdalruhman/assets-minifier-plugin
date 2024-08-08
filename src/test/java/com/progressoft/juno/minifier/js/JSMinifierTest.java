package com.progressoft.juno.minifier.js;


import com.progressoft.juno.minifier.AbstractMinifierTest;
import com.progressoft.juno.minifier.GeneralMinifier;
import com.progressoft.juno.minifier.exception.UnterminatedCommentException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

public class JSMinifierTest extends AbstractMinifierTest {

	private static final List<String> RESOURCES = Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09",
			"10", "11", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");

	private static final String EXTENSION = "js";

	@Override
	protected String extension() {
		return EXTENSION;
	}

	@Override
	protected GeneralMinifier miniferForReader(Reader reader) {
		return new JSMinifier(reader);
	}

	@Override
	protected List<String> resources() {
		return RESOURCES;
	}

	@Test
	public void unterminatedCommentThrowsException() throws IOException {
		throwsOnMinify("12", UnterminatedCommentException.class);
		return;
	}
}
