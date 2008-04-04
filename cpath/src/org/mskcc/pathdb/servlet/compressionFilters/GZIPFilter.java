/*
 * Copyright 2003 Jayson Falkner (jayson@jspinsider.com)
 * This code is from "Servlets and JavaServer pages; the J2EE Web Tier",
 * http://www.jspbook.com. You may freely use the code both commercially
 * and non-commercially. If you like the code, please pick up a copy of
 * the book and help support the authors, development of more free code,
 * and the JSP/Servlet/J2EE community.
 */
package org.mskcc.pathdb.servlet.compressionFilters;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GZIPFilter implements Filter {
    private Logger log = Logger.getLogger(GZIPFilter.class);

    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {
        if (req instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            String ae = request.getHeader("accept-encoding");
            log.info("1:35 pm:  In Gzip filter, got URL:  " + request.getRequestURI());
            boolean imageRequested = false;
            String uri = request.getRequestURI();
            if (uri.endsWith("gif") || uri.endsWith("jpg") || uri.endsWith("png")) {
                imageRequested = true;
                log.info("Image requested.  Do no GZIP.");
            }

            if (ae != null && ae.indexOf("gzip") != -1 && !imageRequested) {
                log.info("GZIP supported, compressing.");
                GZIPResponseWrapper wrappedResponse =
                        new GZIPResponseWrapper(response);
                chain.doFilter(req, wrappedResponse);
                wrappedResponse.finishResponse();
                return;
            } else {
                log.info ("GZIP not supported.");
            }
            chain.doFilter(req, res);
        }
    }

    public void init(FilterConfig filterConfig) {
        // noop
    }

    public void destroy() {
        // noop
    }
}
