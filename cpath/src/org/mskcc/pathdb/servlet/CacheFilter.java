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
package org.mskcc.pathdb.servlet;

import com.opensymphony.module.oscache.base.Cache;
import com.opensymphony.module.oscache.base.NeedsRefreshException;
import com.opensymphony.module.oscache.web.ServletCacheAdministrator;
import com.opensymphony.module.oscache.web.filter.ResponseContent;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

/**
 * Cache Filter.
 * Copied from the com.opensymphony.module.oscache.web.
 * Modified to use the default Logging system.
 *
 * @author OSCache Team.
 */
public class CacheFilter implements Filter {
    /**
     * Filter Config Object.
     */
    private FilterConfig config;

    /**
     * Scope of Scaching.
     */
    private int cacheScope = PageContext.APPLICATION_SCOPE;

    /**
     * Administrator.
     */
    private ServletCacheAdministrator admin = null;

    /**
     * Time before cache should be refreshed - default one hour (in seconds)
     */
    private int time;

    /**
     * Initialize the filter
     *
     * @param filterConfig The filter configuration
     */
    public void init(FilterConfig filterConfig) {
        config = filterConfig;
        admin = ServletCacheAdministrator.getInstance
                (config.getServletContext());
        try {
            time = Integer.parseInt(config.getInitParameter("time"));
        } catch (Exception e) {
            time = 60 * 60;
        }
    }

    /**
     * Filter clean-up
     */
    public void destroy() {
        // No Op
    }

    /**
     * Process the doFilter
     *
     * @param request  The servlet request
     * @param response The servlet response
     * @param chain    The filet chain
     * @throws ServletException Indicates Servlet Error.
     * @throws IOException      Indicates I/O Error.
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        HttpServletRequest hRequest = (HttpServletRequest) request;
        log("Incoming request from  IP:  " + request.getRemoteAddr());
        log("Incoming request from  Host:  " + request.getRemoteHost());
        log("URL Requested:  " + hRequest.getQueryString());
        String key = admin.generateEntryKey(null, (HttpServletRequest)
                request, cacheScope);
        log("Using Cache Key:  " + key);
        Cache cache = admin.getCache((HttpServletRequest) request, cacheScope);
        try {
            ResponseContent respContent = (ResponseContent)
                    cache.getFromCache(key, time);
            log("Using cached entry");
            if (respContent == null) {
                throw new NeedsRefreshException(respContent);
            }
            respContent.writeTo(response);
        } catch (NeedsRefreshException nre) {
            log("New cache entry, or cache entry is stale");
//            CacheHttpServletResponseWrapper cacheResponse =
//                    new CacheHttpServletResponseWrapper
//                            ((HttpServletResponse) response);
//            chain.doFilter(request, cacheResponse);
//            // Store as the cache content the result of the response
//            ResponseContent content = cacheResponse.getContent();
//            cache.putInCache(key, content);
        }
    }

    private void log(String msg) {
        System.err.println(msg);
    }
}