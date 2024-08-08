package com.progressoft.juno.minifier;

import com.progressoft.juno.minifier.css.CSSMinifierTest;
import com.progressoft.juno.minifier.exception.MinificationException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractMinifierTest {
	private static final Logger logger = LoggerFactory.getLogger(AbstractMinifierTest.class);
	private static final String RESOURCES_DIR = "src/test/resources";
	protected abstract String extension();

	protected Reader readerForSourceFile(String index) {
		String sourceFile = "input/test-" + index + "." + extension();
		logger.info("Testing -> " + sourceFile);
		return new InputStreamReader(Objects.requireNonNull(CSSMinifierTest.class.getClassLoader().getResourceAsStream(sourceFile)));
	}

	protected String stringForExpectedFile(String index) throws IOException {
		String expectedFile = "output/test-" + index + "." + extension();
		return new String(Files.readAllBytes(Paths.get(RESOURCES_DIR, expectedFile)));
	}

	protected abstract GeneralMinifier miniferForReader(Reader reader);

	protected abstract List<String> resources();

	@Test
	public void actualOutputMatchesExpected() throws IOException {
		for (String index : resources()) {
			Writer out = new StringWriter();
			GeneralMinifier min = miniferForReader(readerForSourceFile(index));
			try {
				min.minify(out);
			} catch (MinificationException e) {
				logger.error("MinificationException with cause: {}", e.getCause().getClass().getName());
				fail(e);
			}
			String expected = stringForExpectedFile(index);
			assertEquals(expected.trim(), out.toString().trim(), getClass().getName() + " failed on index " + index);
		}
	}

	protected void throwsOnMinify(String index, Class<? extends Exception> expected) {
		Writer out = new StringWriter();
		GeneralMinifier min = miniferForReader(readerForSourceFile(index));
		try {
			min.minify(out);
		} catch (Exception e) {
			assertEquals(MinificationException.class, e.getClass());
			assertEquals(expected, e.getCause().getClass());
			return;
		}
		fail("Expected: " + expected.getClass().getName());
	}
}
