package com.vaggv.livetranslation;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class Utils {
    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) return 1.0; // both strings are zero length

        LevenshteinDistance x = new LevenshteinDistance();
        return (longerLength - x.apply(longer, shorter)) / (double) longerLength;
    }

    public static final String[] languages = {"English","Greek","Afrikaans","Albanian","Arabic","Belarusian","Bulgarian",
            "Bengali","Catalan","Czech","Welsh","Danish","German","Esperanto",
            "Spanish","Estonian","Persian","Finnish","French","Irish","Galician","Gujarati",
            "Hebrew","Hindi","Croatian","Haitian","Hungarian","Indonesian","Icelandic","Italian",
            "Japanese","Georgian","Kannada","Korean","Lithuanian","Latvian","Macedonian","Marathi",
            "Malay","Maltese","Dutch","Norwegian","Polish","Portuguese","Romanian","Russian",
            "Slovak","Slovenian","Swedish","Swahili","Tamil","Telugu","Thai","Tagalog",
            "Turkish","Ukrainian","Urdu","Vietnamese","Chinese"};


    public static String toFullLangString(String lang){
        switch (lang){
            case "af":
                return "Afrikaans";
            case "ar-Latn":
            case "ar":
                return "Arabic";
            case "az":
                return "Azerbaijani";
            case "bg-Latn":
            case "bg":
                return "Bulgarian";
            case "bs":
                return "Bosnian";
            case "ca":
                return "Catalan";
            case "ceb":
                return "Cebuano";
            case "co":
                return "Corsican";
            case "cs":
                return "Czech";
            case "cy":
                return "Welsh";
            case "da":
                return "Danish";
            case "de":
                return "German";
            case "el-Latn":
            case "el":
                return "Greek";
            case "en":
                return "English";
            case "eo":
                return "Esperanto";
            case "es":
                return "Spanish";
            case "et":
                return "Estonian";
            case "eu":
                return "Basque";
            case "fi":
                return "Finnish";
            case "fil":
                return "Filipino";
            case "fr":
                return "French";
            case "fy":
                return "Western Frisian";
            case "ga":
                return "Irish";
            case "gd":
                return "Scots Gaelic";
            case "gl":
                return "Galician";
            case "ha":
                return "Hausa";
            case "haw":
                return "Hawaiian";
            case "hi":
            case "hi-Latn":
                return "Hindi";
            case "hmn":
                return "Hmong";
            case "hr":
                return "Croatian";
            case "ht":
                return "Haitian";
            case "hu":
                return "Hungarian";
            case "id":
                return "Indonesian";
            case "ig":
                return "Igbo";
            case "is":
                return "Icelandic";
            case "it":
                return "Italian";
            case "ja-Latn":
            case "ja":
                return "Japanese";
            case "jv":
                return "Javanese";
            case "ku":
                return "Kurdish";
            case "la":
                return "Latin";
            case "lb":
                return "Luxembourgish";
            case "lt":
                return "Lithuanian";
            case "lv":
                return "Latvian";
            case "mg":
                return "Malagasy";
            case "mi":
                return "Maori";
            case "ms":
                return "Malay";
            case "mt":
                return "Maltese";
            case "nl":
                return "Dutch";
            case "no":
                return "Norwegian";
            case "ny":
                return "Nyanja";
            case "pl":
                return "Polish";
            case "pt":
                return "Portuguese";
            case "ro":
                return "Romanian";
            case "ru-Latn":
            case "ru":
                return "Russian";
            case "sk":
                return "Slovak";
            case "sl":
                return "Slovenian";
            case "sm":
                return "Samoan";
            case "sn":
                return "Shona";
            case "so":
                return "Somali";
            case "sq":
                return "Albanian";
            case "st":
                return "Sesotho";
            case "su":
                return "Sundanese";
            case "sv":
                return "Swedish";
            case "sw":
                return "Swahili";
            case "tr":
                return "Turkish";
            case "uz":
                return "Uzbek";
            case "vi":
                return "Vietnamese";
            case "xh":
                return "Xhosa";
            case "yo":
                return "Yoruba";
            case "zh-Latn":
            case "zh":
                return "Chinese";
            case "zu":
                return "Zulu";
            default:
                return "";
        }
    }

    public static String toShortLangString(String lang){
        switch (lang){
            case "Afrikaans":
                return "af";
            case "ar-Latn":
            case "Arabic":
                return "ar";
            case "Azerbaijani":
                return "az";
            case "Bulgarian":
                return "bg";
            case "Bosnian":
                return "bs";
            case "Catalan":
                return "ca";
            case "Cebuano":
                return "ceb";
            case "Corsican":
                return "co";
            case "Czech":
                return "cs";
            case "Welsh":
                return "cy";
            case "Danish":
                return "da";
            case "German":
                return "de";
            case "el-Latn":
            case "Greek":
                return "el";
            case "English":
                return "en";
            case "Esperanto":
                return "eo";
            case "Spanish":
                return "es";
            case "Estonian":
                return "et";
            case "Basque":
                return "eu";
            case "Finnish":
                return "fi";
            case "Filipino":
                return "fil";
            case "French":
                return "fr";
            case "Western Frisian":
                return "fy";
            case "Irish":
                return "ga";
            case "Scots Gaelic":
                return "gd";
            case "Galician":
                return "gl";
            case "Hausa":
                return "ha";
            case "Hawaiian":
                return "haw";
            case "Hindi":
            case "hi-Latn":
                return "hi";
            case "Hmong":
                return "hmn";
            case "Croatian":
                return "hr";
            case "Haitian":
                return "ht";
            case "Hungarian":
                return "hu";
            case "Indonesian":
                return "id";
            case "Igbo":
                return "ig";
            case "Icelandic":
                return "is";
            case "Italian":
                return "it";
            case "Japanese":
                return "ja";
            case "Javanese":
                return "jv";
            case "Kannada":
                return "kn";
            case "Kurdish":
                return "ku";
            case "Korean":
                return "ko";
            case "Latin":
                return "la";
            case "Luxembourgish":
                return "lb";
            case "Lithuanian":
                return "lt";
            case "Latvian":
                return "lv";
            case "Malagasy":
                return "mg";
            case "Maori":
                return "mi";
            case "Malay":
                return "ms";
            case "Maltese":
                return "mt";
            case "Dutch":
                return "nl";
            case "Norwegian":
                return "no";
            case "Nyanja":
                return "ny";
            case "Polish":
                return "pl";
            case "Portuguese":
                return "pt";
            case "Romanian":
                return "ro";
            case "Russian":
                return "ru";
            case "Slovak":
                return "sk";
            case "Slovenian":
                return "sl";
            case "Samoan":
                return "sm";
            case "Shona":
                return "sn";
            case "Somali":
                return "so";
            case "Albanian":
                return "sq";
            case "Sesotho":
                return "st";
            case "Sundanese":
                return "su";
            case "Swedish":
                return "sv";
            case "Swahili":
                return "sw";
            case "Turkish":
                return "tr";
            case "Uzbek":
                return "uz";
            case "Vietnamese":
                return "vi";
            case "Xhosa":
                return "xh";
            case "Yoruba":
                return "yo";
            case "Chinese":
                return "zh";
            case "Zulu":
                return "zu";
            default:
                return "";
        }
    }


}
