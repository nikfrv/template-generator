package com.example.templategenerator.util;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBrType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class DocxMergeUtil {

    public static byte[] mergeDocuments(List<byte[]> documents) throws IOException {
        try (XWPFDocument finalDoc = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            boolean first = true;

            for (byte[] docBytes : documents) {
                try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(docBytes))) {
                    if (first) {
                        // Первый документ — используем как основу
                        CTBody body = finalDoc.getDocument().getBody();
                        body.set(doc.getDocument().getBody());
                        first = false;
                    } else {
                        // Вставляем разрыв страницы ПЕРЕД вставкой следующего задания
                        insertPageBreak(finalDoc);

                        // Добавляем содержимое документа
                        appendBodyElements(finalDoc, doc);
                    }
                }
            }

            finalDoc.write(out);
            return out.toByteArray();
        }
    }

    private static void appendBodyElements(XWPFDocument target, XWPFDocument source) {
        // Копируем стили
        XWPFStyles sourceStyles = source.getStyles();
        XWPFStyles targetStyles = target.createStyles();
        if (sourceStyles != null && targetStyles != null) {
            targetStyles.setStyles(sourceStyles.getCtStyles());
        }

        // Вставка элементов
        for (IBodyElement elem : source.getBodyElements()) {
            if (elem instanceof XWPFParagraph p) {
                XWPFParagraph newPar = target.createParagraph();
                newPar.getCTP().set(p.getCTP().copy());
            } else if (elem instanceof XWPFTable t) {
                XWPFTable newTable = target.createTable();
                newTable.getCTTbl().set(t.getCTTbl().copy());
            }
        }
    }

    private static void insertPageBreak(XWPFDocument document) {
        XWPFParagraph breakPar = document.createParagraph();
        CTR run = breakPar.getCTP().addNewR();
        CTBr br = run.addNewBr();
        br.setType(STBrType.PAGE);
    }
}

