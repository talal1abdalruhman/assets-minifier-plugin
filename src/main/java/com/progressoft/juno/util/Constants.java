package com.progressoft.juno.util;

public class Constants {

    private Constants() {}


    public static final String PATTERN_HANDLER_PREFIX = "[";

    public static final String PATTERN_HANDLER_SUFFIX = "]";

    public static final String REGEX_HANDLER_PREFIX = "%regex" + PATTERN_HANDLER_PREFIX;

    public static final String ANT_HANDLER_PREFIX = "%ant" + PATTERN_HANDLER_PREFIX;

    /**
     * Symbolic colour names defined by HTML
     */
    public static final String[] HTML_COLOUR_NAMES = { "aliceblue", "antiquewhite", "aqua", "aquamarine", "azure", "beige",
            "bisque", "black", "blanchedalmond", "blue", "blueviolet", "brown", "burlywood", "cadetblue", "chartreuse",
            "chocolate", "coral", "cornflowerblue", "cornsilk", "crimson", "cyan", "darkblue", "darkcyan",
            "darkgoldenrod", "darkgray", "darkgreen", "darkkhaki", "darkmagenta", "darkolivegreen", "darkorange",
            "darkorchid", "darkred", "darksalmon", "darkseagreen", "darkslateblue", "darkslategray", "darkturquoise",
            "darkviolet", "deeppink", "deepskyblue", "dimgray", "dodgerblue", "firebrick", "floralwhite", "forestgreen",
            "fuchsia", "gainsboro", "ghostwhite", "gold", "goldenrod", "gray", "green", "greenyellow", "honeydew",
            "hotpink", "indianred ", "indigo ", "ivory", "khaki", "lavender", "lavenderblush", "lawngreen",
            "lemonchiffon", "lightblue", "lightcoral", "lightcyan", "lightgoldenrodyellow", "lightgrey", "lightgreen",
            "lightpink", "lightsalmon", "lightseagreen", "lightskyblue", "lightslategray", "lightsteelblue",
            "lightyellow", "lime", "limegreen", "linen", "magenta", "maroon", "mediumaquamarine", "mediumblue",
            "mediumorchid", "mediumpurple", "mediumseagreen", "mediumslateblue", "mediumspringgreen", "mediumturquoise",
            "mediumvioletred", "midnightblue", "mintcream", "mistyrose", "moccasin", "navajowhite", "navy", "oldlace",
            "olive", "olivedrab", "orange", "orangered", "orchid", "palegoldenrod", "palegreen", "paleturquoise",
            "palevioletred", "papayawhip", "peachpuff", "peru", "pink", "plum", "powderblue", "purple", "red",
            "rosybrown", "royalblue", "saddlebrown", "salmon", "sandybrown", "seagreen", "seashell", "sienna", "silver",
            "skyblue", "slateblue", "slategray", "snow", "springgreen", "steelblue", "tan", "teal", "thistle", "tomato",
            "turquoise", "violet", "wheat", "white", "whitesmoke", "yellow", "yellowgreen" };

    /**
     * Corresponding hex colour values
     */
    public static final String[] HTML_COLOUR_VALUES = { "#f0f8ff", "#faebd7", "#00ffff", "#7fffd4", "#f0ffff", "#f5f5dc",
            "#ffe4c4", "#000", "#ffebcd", "#00f", "#8a2be2", "#a52a2a", "#deb887", "#5f9ea0", "#7fff00", "#d2691e",
            "#ff7f50", "#6495ed", "#fff8dc", "#dc143c", "#0ff", "#00008b", "#008b8b", "#b8860b", "#a9a9a9", "#006400",
            "#bdb76b", "#8b008b", "#556b2f", "#ff8c00", "#9932cc", "#8b0000", "#e9967a", "#8fbc8f", "#483d8b",
            "#2f4f4f", "#00ced1", "#9400d3", "#ff1493", "#00bfff", "#696969", "#1e90ff", "#b22222", "#fffaf0",
            "#228b22", "#f0f", "#dcdcdc", "#f8f8ff", "#ffd700", "#daa520", "#808080", "#008000", "#adff2f", "#f0fff0",
            "#ff69b4", "#cd5c5c", "#4b0082", "#fffff0", "#f0e68c", "#e6e6fa", "#fff0f5", "#7cfc00", "#fffacd",
            "#add8e6", "#f08080", "#e0ffff", "#fafad2", "#d3d3d3", "#90ee90", "#ffb6c1", "#ffa07a", "#20b2aa",
            "#87cefa", "#789", "#b0c4de", "#ffffe0", "#0f0", "#32cd32", "#faf0e6", "#f0f", "#800000", "#66cdaa",
            "#0000cd", "#ba55d3", "#9370d8", "#3cb371", "#7b68ee", "#00fa9a", "#48d1cc", "#c71585", "#191970",
            "#f5fffa", "#ffe4e1", "#ffe4b5", "#ffdead", "#000080", "#fdf5e6", "#808000", "#6b8e23", "#ffa500",
            "#ff4500", "#da70d6", "#eee8aa", "#98fb98", "#afeeee", "#d87093", "#ffefd5", "#ffdab9", "#cd853f",
            "#ffc0cb", "#dda0dd", "#b0e0e6", "#800080", "#f00", "#bc8f8f", "#4169e1", "#8b4513", "#fa8072", "#f4a460",
            "#2e8b57", "#fff5ee", "#a0522d", "#c0c0c0", "#87ceeb", "#6a5acd", "#708090", "#fffafa", "#00ff7f",
            "#4682b4", "#d2b48c", "#008080", "#d8bfd8", "#ff6347", "#40e0d0", "#ee82ee", "#f5deb3", "#fff", "#f5f5f5",
            "#ff0", "#9acd32" };

    /**
     * Symbolic font weight names
     */
    public static final String[] FONT_WEIGHT_NAMES = { "normal", "bold", "bolder", "lighter" };

    /**
     * Corresponding numeric font weight values
     */
    public static final String[] FONT_WEIGHT_VALUES = { "400", "700", "900", "100" };

    /**
     * End of file
     */
    public static final int EOF = -1;

    public static final String[] DEFAULT_EXCLUDES = {
            // Miscellaneous typical temporary files
            "**/*~",
            "**/#*#",
            "**/.#*",
            "**/%*%",
            "**/._*",

            // CVS
            "**/CVS",
            "**/CVS/**",
            "**/.cvsignore",

            // RCS
            "**/RCS",
            "**/RCS/**",

            // SCCS
            "**/SCCS",
            "**/SCCS/**",

            // Visual SourceSafe
            "**/vssver.scc",

            // MKS
            "**/project.pj",

            // Subversion
            "**/.svn",
            "**/.svn/**",

            // Arch
            "**/.arch-ids",
            "**/.arch-ids/**",

            // Bazaar
            "**/.bzr",
            "**/.bzr/**",

            // SurroundSCM
            "**/.MySCMServerInfo",

            // Mac
            "**/.DS_Store",

            // Serena Dimensions Version 10
            "**/.metadata",
            "**/.metadata/**",

            // Mercurial
            "**/.hg",
            "**/.hg/**",

            // git
            "**/.git",
            "**/.git/**",
            "**/.gitignore",

            // BitKeeper
            "**/BitKeeper",
            "**/BitKeeper/**",
            "**/ChangeSet",
            "**/ChangeSet/**",

            // darcs
            "**/_darcs",
            "**/_darcs/**",
            "**/.darcsrepo",
            "**/.darcsrepo/**",
            "**/-darcs-backup*",
            "**/.darcs-temp-mail"
    };

}
