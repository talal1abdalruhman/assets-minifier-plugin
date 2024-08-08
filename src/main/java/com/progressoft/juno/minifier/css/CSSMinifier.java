package com.progressoft.juno.minifier.css;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.progressoft.juno.minifier.AbstractMinifier;
import com.progressoft.juno.minifier.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CSSMinifier extends AbstractMinifier {

    private static final Logger logger = LoggerFactory.getLogger(CSSMinifier.class);

    public CSSMinifier(Reader reader) {
        super(reader);
    }


    @Override
    public void minify(Writer writer) throws MinificationException {
        try (BufferedReader br = new BufferedReader(reader()); PrintWriter pout = new PrintWriter(writer)) {
            int commentIdx, openBraces, currPositionInStream;
            char curr;

            StringBuffer sb = new StringBuffer();
            String s;
            while ((s = br.readLine()) != null) {
                if (s.trim().equals("")) {
                    continue;
                }
                sb.append(s);
            }

            logger.debug("Removing comments...");
            currPositionInStream = 0;
            while ((currPositionInStream = sb.indexOf("/*", currPositionInStream)) != -1) {
                if (sb.charAt(currPositionInStream + 2) == '*' && sb.charAt(currPositionInStream + 3) != '/') {
                    currPositionInStream += 2;
                    continue;
                }
                commentIdx = sb.indexOf("*/", currPositionInStream + 2);
                if (commentIdx == -1) {
                    throw new UnterminatedCommentException();
                }
                sb.delete(currPositionInStream, commentIdx + 2);
            }
            logger.debug("Parsing and processing selectors...");
            List<Selector> selectors = new ArrayList<>();
            currPositionInStream = 0;
            openBraces = 0;
            commentIdx = 0;
            for (int i = 0; i < sb.length(); i++) {
                curr = sb.charAt(i);
                if (openBraces < 0) {
                    throw new UnbalancedBracesException();
                }
                if (curr == '{') {
                    openBraces++;
                } else if (curr == '}') {
                    openBraces--;
                    if (openBraces == 0) {
                        try {
                            selectors.add(new Selector(sb.substring(currPositionInStream, i + 1)));
                        } catch (UnterminatedSelectorException usex) {
                            logger.debug("Unterminated selector: {}", usex.getMessage());
                        } catch (EmptySelectorBodyException ebex) {
                            logger.debug("Empty selector body: {}", ebex.getMessage());
                        }
                        currPositionInStream = i + 1;
                    }
                }
            }

            for (Selector selector : selectors) {
                pout.print(selector.toString());
            }
            pout.print("\r\n");
            logger.debug("Process completed successfully.");
        } catch (UnterminatedCommentException | UnbalancedBracesException | IncompleteSelectorException
                 | IOException e) {
            throw new MinificationException("Minification failed due to Exception.", e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new MinificationException("Minification failed due to Exception.", e);
            }
        }
    }
}
