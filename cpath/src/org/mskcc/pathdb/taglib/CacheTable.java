package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.model.XmlCacheRecord;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoXmlCache;
import org.mskcc.pathdb.util.XssFilter;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.http.HttpUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Custom JSP Tag for Displaying the Cache Contents.
 *
 * @author Ethan Cerami
 */
public class CacheTable extends HtmlTable {

    /**
     * Executes JSP Custom Tag
     *
     * @throws Exception Exception in writing to JspWriter.
     */
    public void subDoStartTag() throws Exception {
        String headers[] = {"#", "URL", "Query", "Key", "Last Used"};
        createHeader("XML Cache Contents");
        startTable();
        createTableHeaders(headers);
        outputRecords();
        endTable();
    }

    /**
     * Output Cached Records.
     */
    private void outputRecords() throws DaoException {
        DaoXmlCache dao = new DaoXmlCache(new XDebug());
        ArrayList records = dao.getAllRecords();
        if (records.size() == 0) {
            startRow();
            append("<TD COLSPAN=5>No Records in Cache</TD>");
            endRow();
        } else {
            for (int i = 0; i < records.size(); i++) {
                XmlCacheRecord record = (XmlCacheRecord) records.get(i);
                startRow(i);
                outputDataField(Integer.toString(i));

                outputProtocolRequest(record);
                outputDataField(record.getMd5());
                outputDataField(record.getLastUsed());
                endRow();
            }
        }
    }

    private void outputProtocolRequest(XmlCacheRecord record) {
        try {
            Hashtable params1 = HttpUtils.parseQueryString(record.getUrl());
            HashMap params2 = XssFilter.filterAllParameters(params1);
            ProtocolRequest request = new ProtocolRequest(params2);
            outputDataField("<A HREF='" + request.getUri() + "'>URL Link</A>");
            append("<TD><UL>");
            Iterator keys = params2.keySet().iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                Object value = params2.get(key);
                append("<LI>" + key + ":  " + value);
            }
            append("</UL></TD>");
        } catch (Exception e) {
            append ("<TD>" + record.getUrl() + "</TD>");
            append ("<TD>N/A</TD>");
        }
    }
}