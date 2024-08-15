package com.progressoft.juno.minifier.css;

import com.progressoft.juno.minifier.exception.IncompletePropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Property implements Comparable<Property> {
    private static final Logger logger = LoggerFactory.getLogger(Property.class);
    protected String prop;
    protected Part[] parts;

    public Property(String property) throws IncompletePropertyException {
        ArrayList<String> prts = new ArrayList<String>();
        boolean bCanSplit = true;
        int j = 0;
        String substr;
        logger.debug("\t\tExamining property: {}", property);
        for (int i = 0; i < property.length(); i++) {
            if (!bCanSplit) {
                bCanSplit = (property.charAt(i) == '"');
            } else if (property.charAt(i) == '"') {
                bCanSplit = false;
            } else if (property.charAt(i) == ':' && prts.isEmpty()) {
                substr = property.substring(j, i);
                if (!substr.trim().isEmpty()) {
                    prts.add(substr);
                }
                j = i + 1;
            }
        }
        substr = property.substring(j);
        if (!substr.trim().isEmpty()) {
            prts.add(substr);
        }
        if (prts.size() < 2) {
            throw new IncompletePropertyException(property);
        }

        String prop = prts.get(0).trim();
        if (!(prop.length() > 2 && prop.startsWith("--"))) {
            prop = prop.toLowerCase();
        }
        this.prop = prop;
        this.parts = parseValues(simplifyColours(prts.get(1).trim().replaceAll(", ", ",")));
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.prop).append(":");
        for (Part p : this.parts) {
            sb.append(p.toString()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(";");
        return sb.toString();
    }

    public int compareTo(Property other) {
        // We can't just use String.compareTo(), because we need to sort properties that
        // have hack prefixes last -- eg, *display should come after display.
        String thisProp = this.prop;
        String thatProp = other.prop;

        if (thisProp.charAt(0) == '-') {
            thisProp = thisProp.substring(1);
            thisProp = thisProp.substring(thisProp.indexOf('-') + 1);
        } else if (thisProp.charAt(0) < 65) {
            thisProp = thisProp.substring(1);
        }

        if (thatProp.charAt(0) == '-') {
            thatProp = thatProp.substring(1);
            thatProp = thatProp.substring(thatProp.indexOf('-') + 1);
        } else if (thatProp.charAt(0) < 65) {
            thatProp = thatProp.substring(1);
        }

        return thisProp.compareTo(thatProp);
    }

    private Part[] parseValues(String contents) {
        String[] prts = contents.split(",");
        Part[] results = new Part[prts.length];

        for (int i = 0; i < prts.length; i++) {
            try {
                results[i] = new Part(prts[i], prop);
            } catch (Exception e) {
                logger.debug("Exception in parseValues().", e);
                results[i] = null;
            }
        }

        return results;
    }

    private String simplifyColours(String contents) {
        // This replacement, although it results in a smaller uncompressed file,
        // actually makes the gzipped file bigger -- people tend to use rgba(0,0,0,0.x)
        // quite a lot, which means that rgba(0,0,0,0) has its first eight or so
        // characters
        // compressed really efficiently; much more so than "transparent".
        // contents = contents.replaceAll("rgba\\(0,0,0,0\\)", "transparent");

        return simplifyRGBColours(contents);
    }

    // Convert rgb(51,102,153) to #336699 (this code largely based on YUI code)
    private String simplifyRGBColours(String contents) {
        StringBuffer newContents = new StringBuffer();
        StringBuilder hexColour;
        String[] rgbColours;
        int colourValue;

        Pattern pattern = Pattern.compile("rgb\\s*\\(\\s*([0-9,\\s]+)\\s*\\)");
        Matcher matcher = pattern.matcher(contents);

        while (matcher.find()) {
            hexColour = new StringBuilder("#");
            rgbColours = matcher.group(1).split(",");
            for (String rgbColour : rgbColours) {
                colourValue = Integer.parseInt(rgbColour);
                if (colourValue < 16) {
                    hexColour.append("0");
                }
                hexColour.append(Integer.toHexString(colourValue));
            }
            matcher.appendReplacement(newContents, hexColour.toString());
        }
        matcher.appendTail(newContents);
        return newContents.toString();
    }
}
