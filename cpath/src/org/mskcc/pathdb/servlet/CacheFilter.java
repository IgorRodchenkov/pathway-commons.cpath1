package org.mskcc.pathdb.servlet;

import com.opensymphony.module.oscache.base.Cache;
import com.opensymphony.module.oscache.base.NeedsRefreshException;
import com.opensymphony.module.oscache.web.ServletCacheAdministrator;
import com.opensymphony.module.oscache.web.filter.
        CacheHttpServletResponseWrapper;
import com.opensymphony.module.oscache.web.filter.ResponseContent;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.logging.Logger;

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
    private int time = 60 * 60;

    /**
     * Logger.
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Initialize the filter
     *
     * @param filterConfig The filter configuration
     */
    public void init(FilterConfig filterConfig) {
        //Get whatever settings we want...
        config = filterConfig;
        admin = ServletCacheAdministrator.getInstance
                (config.getServletContext());
        try {
            time = Integer.parseInt(config.getInitParameter("time"));
        } catch (Exception e) {
            logger.warning("Error Parsing Time:  " + e.toString());
        }
    }

    /**
     * Filter clean-up
     */
    public void destroy() {
        logger.info("Shuting down CacheFilter");
    }

    /**
     * Process the doFilter
     *
     * @param request The servlet request
     * @param response The servlet response
     * @param chain The filet chain
     * @throws ServletException Indicates Servlet Error.
     * @throws IOException Indicates I/O Error.
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        HttpServletRequest hRequest = (HttpServletRequest) request;
        logger.info("Processing URL Request:  " + hRequest.getQueryString());
        String key = admin.generateEntryKey(null, (HttpServletRequest)
                request, cacheScope);
        logger.info("cache:  Using Cache Key:  " + key);
        Cache cache = admin.getCache((HttpServletRequest) request, cacheScope);
        try {
            ResponseContent respContent = (ResponseContent)
                    cache.getFromCache(key, time);
            logger.info("cache: Using cached entry for " + key);
            respContent.writeTo(response);
        } catch (NeedsRefreshException nre) {
            logger.info("cache: New cache entry, cache stale or "
                    + "cache scope flushed for " + key);
            CacheHttpServletResponseWrapper cacheResponse =
                    new CacheHttpServletResponseWrapper
                            ((HttpServletResponse) response);
            chain.doFilter(request, cacheResponse);
            // Store as the cache content the result of the response
            ResponseContent content = cacheResponse.getContent();
            cache.putInCache(key, content);
        }
    }
}