package org.mskcc.pathdb.util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Zip Utility Class.
 *
 * @author Ethan Cermai
 */
public class ZipUtil {

    /**
     * Zips the specified Data String.
     *
     * @param data Data String.
     * @return Array of bytes.
     * @throws IOException Input Output Exception.
     */
    public static byte[] zip(String data) throws IOException {
        byte bytes[] = data.getBytes();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(byteStream);
        ZipEntry entry = new ZipEntry("data");
        zipOut.putNextEntry(entry);
        zipOut.write(bytes, 0, bytes.length);
        zipOut.closeEntry();
        zipOut.close();
        byte zipData[] = byteStream.toByteArray();
        return zipData;
    }

    /**
     * Unzips the specified byte array.
     *
     * @param zipData Array of Bytes.
     * @return Unzipped Data String.
     * @throws IOException Input Output Exception.
     */
    public static String unzip(byte zipData[]) throws IOException {
        StringBuffer unzippedData = new StringBuffer();
        ByteArrayInputStream byteStream = new ByteArrayInputStream(zipData);
        ZipInputStream zipIn = new ZipInputStream(byteStream);
        BufferedReader in = new BufferedReader(new InputStreamReader(zipIn));

        //  Get first Entry
        ZipEntry entry = zipIn.getNextEntry();
        String line = in.readLine();
        while (line != null) {
            unzippedData.append(line + "\n");
            line = in.readLine();
        }

        //  Close Everything up
        zipIn.closeEntry();
        zipIn.close();
        return unzippedData.toString();
    }
}