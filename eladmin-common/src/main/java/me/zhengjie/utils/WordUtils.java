package me.zhengjie.utils;

import com.spire.doc.*;
import com.spire.doc.collections.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


public class WordUtils {

    public static void prntBill(Map<String, String> map,InputStream in, OutputStream out) {
        //加载Word文档
        Document doc = new Document();
        doc.loadFromStream(in,FileFormat.Docx_2013);
        SectionCollection sections = doc.getSections();
        for (int i = 0; i < sections.getCount(); i++) {
            TableCollection tables = sections.get(i).getTables();
            for (int j = 0; j < tables.getCount(); j++) {
                RowCollection rows=tables.get(j).getRows();
                for (int k = 0; k < rows.getCount(); k++) {
                    CellCollection cells=rows.get(k).getCells();
                    for (int l = 0; l < cells.getCount(); l++) {
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            cells.get(l).getParagraphs().get(0).replace("${"+entry.getKey()+"}",entry.getValue(),false,false);
                        }
                    }
                }
            }
        }
        doc.saveToStream(out, FileFormat.Html);
    }
}
