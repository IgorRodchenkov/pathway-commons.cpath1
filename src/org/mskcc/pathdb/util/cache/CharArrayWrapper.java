// $Id: CharArrayWrapper.java,v 1.3 2006-06-09 19:22:04 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.util.cache;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

/**
 * A response wrapper that takes everything the client
 * would normally output and saves it in one big
 * character array.
 * <p/>
 * Taken from More Servlets and JavaServer Pages
 * from Prentice Hall and Sun Microsystems Press,
 * http://www.moreservlets.com/.
 * &copy; 2002 Marty Hall; may be freely used or adapted.
 *
 * @author Marty Hall.
 */

public class CharArrayWrapper
        extends HttpServletResponseWrapper {
    private CharArrayWriter charWriter;

    /**
     * Initializes wrapper.
     * <p/>
     * First, this constructor calls the parent
     * constructor. That call is crucial so that the response
     * is stored and thus setHeader, setStatus, addCookie,
     * and so forth work normally.
     * <p/>
     * Second, this constructor creates a CharArrayWriter
     * that will be used to accumulate the response.
     *
     * @param response HttpServletResponse Object.
     */
    public CharArrayWrapper(HttpServletResponse response) {
        super(response);
        charWriter = new CharArrayWriter();
    }

    /**
     * When servlets or JSP pages ask for the Writer,
     * don't give them the real one. Instead, give them
     * a version that writes into the character array.
     * The filter needs to send the contents of the
     * array to the client (perhaps after modifying it).
     *
     * @return PrintWriter Object.
     */
    public PrintWriter getWriter() {
        return (new PrintWriter(charWriter));
    }

    /**
     * Get a String representation of the entire buffer.
     * <p/>
     * Be sure <B>not</B> to call this method multiple times
     * on the same wrapper. The API for CharArrayWriter
     * does not guarantee that it "remembers" the previous
     * value, so the call is likely to make a new String
     * every time.
     *
     * @return String representation.
     */
    public String toString() {
        return (charWriter.toString());
    }

    /**
     * Get the underlying character array.
     *
     * @return Character Array.
     */
    public char[] toCharArray() {
        return (charWriter.toCharArray());
    }
}