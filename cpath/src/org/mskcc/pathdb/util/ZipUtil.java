/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
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