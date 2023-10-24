package com.springbot.tttn.application.utils;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class PdfUtils {
    public static PdfPTable getHeader(BaseFont unicodeFontBase, BaseFont unicodeFontBold) {
        PdfPTable header = new PdfPTable(2);
        header.setHorizontalAlignment(Element.ALIGN_CENTER); // text align center
        header.setWidthPercentage(100); // width table = page

        Paragraph schoolEmblem = new Paragraph();
        Paragraph ministry = new Paragraph("BỘ GIAO THÔNG VẬN TẢI", new Font(unicodeFontBase, 11));
        Paragraph school = new Paragraph("\nTRƯỜNG ĐẠI HỌC GIAO THÔNG VẬN TẢI\nTHÀNH PHỐ HỒ CHÍ MINH\n----------------", new Font(unicodeFontBold, 11));
        schoolEmblem.add(ministry);
        schoolEmblem.add(school);

        Paragraph nationalLanguage = new Paragraph("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM\nĐộc lập - Tự do - Hạnh phúc\n------------------------------------", new Font(unicodeFontBold, 11));
        PdfPCell cell1 = new PdfPCell();
        cell1.setPhrase(schoolEmblem);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.addCell(cell1);

        PdfPCell cell2 = new PdfPCell();
        cell2.setPhrase(nationalLanguage);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.addCell(cell2);

        return header;
    }

    public static PdfPTable getTypeAndDate(BaseFont unicodeFontBase, BaseFont unicodeFontItalic) {
        PdfPTable typeAndDate = new PdfPTable(2);
        typeAndDate.setHorizontalAlignment(Element.ALIGN_CENTER); // text align center
        typeAndDate.setWidthPercentage(100); // width table = page

        Paragraph type = new Paragraph("Số:      /QĐ-ĐHGTVT", new Font(unicodeFontBase, 11));
        Paragraph date = new Paragraph("TP. Hồ Chí Minh, ngày      tháng      năm 2023", new Font(unicodeFontItalic, 11));

        PdfPCell cell1 = new PdfPCell();
        cell1.setPhrase(type);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        typeAndDate.addCell(cell1);

        PdfPCell cell2 = new PdfPCell();
        cell2.setPhrase(date);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        typeAndDate.addCell(cell2);

        return typeAndDate;
    }
}
