package com.progressoft.juno.minifier.css;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.progressoft.juno.util.Constants.*;

class Part {
    String contents;
    String property;

    public Part(String contents, String property) throws Exception {
        this.contents = " " + contents;
        this.property = property;
        simplify();
    }

    private void simplify() {
        this.contents = this.contents.replaceAll(" !important", "!important");

        this.contents = this.contents.replaceAll("(\\s)(0)(px|em|%|in|cm|mm|pc|pt|ex)", "$1$2");

        this.contents = this.contents.trim();

        if (this.contents.equals("0 0 0 0")) {
            this.contents = "0";
        }
        if (this.contents.equals("0 0 0")) {
            this.contents = "0";
        }
        if (this.contents.equals("0 0")) {
            this.contents = "0";
        }
        simplifyParameters();
        simplifyFontWeights();
        simplifyQuotesAndCaps();
        simplifyColourNames();
        simplifyHexColours();
    }

    private void simplifyParameters() {
        if (this.property.equals("background-size") || this.property.equals("quotes")
                || this.property.equals("transform-origin"))
            return;

        StringBuffer newContents = new StringBuffer();

        String[] params = this.contents.split(" ");
        if (params.length == 4) {
            if (params[1].equalsIgnoreCase(params[3])) {
                params = Arrays.copyOf(params, 3);
            }
        }
        if (params.length == 3) {
            if (params[0].equalsIgnoreCase(params[2])) {
                params = Arrays.copyOf(params, 2);
            }
        }
        if (params.length == 2) {
            if (params[0].equalsIgnoreCase(params[1])) {
                params = Arrays.copyOf(params, 1);
            }
        }

        for (int i = 0; i < params.length; i++) {
            newContents.append(params[i] + " ");
        }
        newContents.deleteCharAt(newContents.length() - 1);
        this.contents = newContents.toString();
    }

    private void simplifyFontWeights() {
        if (!this.property.equals("font-weight"))
            return;

        String lcContents = this.contents.toLowerCase();

        for (int i = 0; i < FONT_WEIGHT_NAMES.length; i++) {
            if (lcContents.equals(FONT_WEIGHT_NAMES[i])) {
                this.contents = FONT_WEIGHT_VALUES[i];
                break;
            }
        }
    }

    private void simplifyQuotesAndCaps() {
        if ((this.contents.length() > 4) && (this.contents.substring(0, 4).equalsIgnoreCase("url("))) {
            this.contents = this.contents.replaceAll("(?i)url\\(('|\")?(.*?)\\1\\)", "url($2)");
        } else if ((this.contents.length() > 4) && (this.contents.substring(0, 4).equalsIgnoreCase("var("))) {
            this.contents = this.contents.replaceAll("\\s{2,}", " ").trim();
        } else {
            String[] words = this.contents.split("\\s");
            if (words.length == 1) {
                if (!this.property.equalsIgnoreCase("animation-name")) {
                    this.contents = this.contents.toLowerCase();
                }
                this.contents = this.contents.replaceAll("('|\")?(.*?)\1", "$2");
            }
        }
    }

    private void simplifyColourNames() {
        String lcContents = this.contents.toLowerCase();

        for (int i = 0; i < HTML_COLOUR_NAMES.length; i++) {
            if (lcContents.equals(HTML_COLOUR_NAMES[i])) {
                if (HTML_COLOUR_VALUES[i].length() < HTML_COLOUR_NAMES[i].length()) {
                    this.contents = HTML_COLOUR_VALUES[i];
                }
                break;
            } else if (lcContents.equals(HTML_COLOUR_VALUES[i])) {
                if (HTML_COLOUR_NAMES[i].length() < HTML_COLOUR_VALUES[i].length()) {
                    this.contents = HTML_COLOUR_NAMES[i];
                }
            }
        }
    }

    private void simplifyHexColours() {
        StringBuffer newContents = new StringBuffer();

        Pattern pattern = Pattern
                .compile("#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])");
        Matcher matcher = pattern.matcher(this.contents);

        while (matcher.find()) {
            if (matcher.group(1).equalsIgnoreCase(matcher.group(2))
                    && matcher.group(3).equalsIgnoreCase(matcher.group(4))
                    && matcher.group(5).equalsIgnoreCase(matcher.group(6))) {
                matcher.appendReplacement(newContents, "#" + matcher.group(1).toLowerCase()
                        + matcher.group(3).toLowerCase() + matcher.group(5).toLowerCase());
            } else {
                matcher.appendReplacement(newContents, matcher.group().toLowerCase());
            }
        }
        matcher.appendTail(newContents);

        this.contents = newContents.toString();
    }

    public String toString() {
        return this.contents;
    }
}
