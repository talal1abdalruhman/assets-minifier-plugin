package com.progressoft.juno.minifier.css;

import com.progressoft.juno.minifier.exception.EmptySelectorBodyException;
import com.progressoft.juno.minifier.exception.IncompletePropertyException;
import com.progressoft.juno.minifier.exception.IncompleteSelectorException;
import com.progressoft.juno.minifier.exception.UnterminatedSelectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Selector {
    private static final Logger logger = LoggerFactory.getLogger(Selector.class);
    private Property[] properties = null;
    private List<Selector> subSelectors = null;
    private String slctr;


    public Selector(String selector)
            throws IncompleteSelectorException, UnterminatedSelectorException, EmptySelectorBodyException {
        String[] parts = selector.split("\\{");

        if (parts.length < 2) {
            throw new IncompleteSelectorException(selector);
        }

        this.slctr = parts[0].trim();

        this.slctr = this.slctr.replaceAll("\\s?(\\+|~|,|=|~=|\\^=|\\$=|\\*=|\\|=|>)\\s?", "$1");

        if (parts.length > 2) {
            this.subSelectors = new ArrayList<>();
            parts = selector.split("(\\s*\\{\\s*)|(\\s*\\}\\s*)");
            for (int i = 1; i < parts.length; i += 2) {
                parts[i] = parts[i].trim();
                parts[i + 1] = parts[i + 1].trim();
                if (!(parts[i].equals("") || (parts[i + 1].equals("")))) {
                    this.subSelectors.add(new Selector(parts[i] + "{" + parts[i + 1] + "}"));
                }
            }
        } else {
            String contents = parts[parts.length - 1].trim();
            logger.debug("Parsing selector: {}", this.slctr);
            logger.debug("\t{}", contents);
            if (contents.charAt(contents.length() - 1) != '}') {
                throw new UnterminatedSelectorException(selector);
            }
            if (contents.length() == 1) {
                throw new EmptySelectorBodyException(selector);
            }
            contents = contents.substring(0, contents.length() - 1);
            if (contents.charAt(contents.length() - 1) == ';') {
                contents = contents.substring(0, contents.length() - 1);
            }

            this.properties = new Property[0];
            this.properties = parseProperties(contents).toArray(this.properties);
            sortProperties(this.properties);
        }
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.slctr).append("{");
        if (this.subSelectors != null) {
            for (Selector s : this.subSelectors) {
                sb.append(s.toString());
            }
        }
        if (this.properties != null) {
            for (Property p : this.properties) {
                sb.append(p.toString());
            }
        }
        if (sb.charAt(sb.length() - 1) == ';') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

    private ArrayList<Property> parseProperties(String contents) {
        List<String> parts = new ArrayList<>();
        boolean bInsideString = false, bInsideURL = false;
        int j = 0;
        String substr;
        for (int i = 0; i < contents.length(); i++) {
            if (bInsideString) {
                bInsideString = !(contents.charAt(i) == '"');
            } else if (bInsideURL) {
                bInsideURL = !(contents.charAt(i) == ')');
            } else if (contents.charAt(i) == '"') {
                bInsideString = true;
            } else if (contents.charAt(i) == '(') {
                if ((i - 3) > 0 && "url".equals(contents.substring(i - 3, i)))
                    bInsideURL = true;
            } else if (contents.charAt(i) == ';') {
                substr = contents.substring(j, i);
                if (!(substr.trim().equals("") || (substr == null))) {
                    parts.add(substr);
                }
                j = i + 1;
            }
        }
        substr = contents.substring(j, contents.length());
        if (!substr.trim().equals("")) {
            parts.add(substr);
        }

        ArrayList<Property> results = new ArrayList<Property>();
        for (int i = 0; i < parts.size(); i++) {
            try {
                results.add(new Property(parts.get(i)));
            } catch (IncompletePropertyException ipex) {
                logger.debug("Incomplete property in selector '{}': {}", slctr, ipex.getMessage());
            }
        }

        return results;
    }


    private void sortProperties(Property[] properties) {
        Arrays.sort(properties);
    }
}
