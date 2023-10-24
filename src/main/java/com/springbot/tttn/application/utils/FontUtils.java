package com.springbot.tttn.application.utils;

import com.itextpdf.text.pdf.BaseFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FontUtils {
    private static final Logger logger = LoggerFactory.getLogger(FontUtils.class);

    public static BaseFont timesNewRoman() {
        try {
            return BaseFont.createFont("times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED); //regular
        } catch (Exception e) {
            logger.error("\t\t\tError: " + e.getMessage());
            return null;
        }
    }

    public static BaseFont timesNewRomanBold() {
        try {
            return BaseFont.createFont("timesbold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED); //bold
        } catch (Exception e) {
            logger.error("\t\t\tError: " + e.getMessage());
            return null;
        }
    }

    public static BaseFont timesNewRomanItalic() {
        try {
            return BaseFont.createFont("timesitalic.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED); //italic
        } catch (Exception e) {
            logger.error("\t\t\tError: " + e.getMessage());
            return null;
        }
    }
}
