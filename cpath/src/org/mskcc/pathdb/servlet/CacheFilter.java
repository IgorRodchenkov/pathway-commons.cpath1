package org.mskcc.pathdb.servlet;

import org.mskcc.pathdb.util.cache.EhCache;
import org.mskcc.pathdb.util.cache.CharArrayWrapper;
import org.mskcc.pathdb.util.security.Md5Util;
import org.mskcc.pathdb.action.admin.AdminWebLogging;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.CacheException;

/**
 * Intercepts all Servlet Requests, and provides built-in caching framework.
 *
 * @author Ethan Cerami.
 */
public class CacheFilter implements Filter {
    private Logger log = Logger.getLogger(CacheFilter.class);

    /**
     * Initializes the Cache Filter.  Currently, a no-op.
     * @param filterConfig FilterConfig Object.
     * @throws ServletException Servlet Error.
     */
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Process Request via Cache Filter.
     * @param servletRequest    ServletRequest Object.
     * @param servletResponse   ServletResponse Object.
     * @param filterChain       FilterChain Object.
     * @throws IOException      IO Error.
     * @throws ServletException Servlet Error.
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            //  Obtain Cache Manager
            CacheManager manager = CacheManager.getInstance();

            //  Obtain Persistent Cache
            Cache cache = manager.getCache(EhCache.PERSISTENT_CACHE);

            //  Determine Complete URL (w/ URL Parameters included)
            String url = getUrl(request);
            log.info("Got URL request:  " + url);

            HttpSession session = request.getSession(true);
            String xdebugSession = (String) session.getAttribute
                (AdminWebLogging.WEB_LOGGING);
            String xdebugParameter = request.getParameter(AdminWebLogging.WEB_LOGGING);
            if (xdebugSession == null && xdebugParameter == null) {
                //  Translate to MD5 Hash Key
                String key = getHashKey (url);
                if (key !=  null) {
                    log.info("Translates to key:  " + key);

                    //  Query Cache
                    Element element = cache.get(key);
                    log.info ("Checking Cache");
                    if (element != null) {
                        processCacheHit(cache, key, element, response);
                    } else {
                        processCacheMiss(response, filterChain, request, key, cache);
                    }
                } else {
                    log.error("Could not obtain key.  Bypassing Cache Filter.");
                    filterChain.doFilter(request, response);
                }
            } else {
                log.info ("In Debug Mode:  Bypassing Cache Filter");
                filterChain.doFilter(request, response);
            }
        } catch (CacheException e) {
            log.error ("Got Cache Exception", e);
            log.error ("Bypassing Cache Completely.");
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error ("Got Error:  " + e.getMessage(), e);
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Processes Cache Miss.
     */
    private void processCacheMiss(HttpServletResponse response, FilterChain filterChain,
            HttpServletRequest request, String key, Cache cache) throws IOException,
            ServletException {
        Element element;
        //  Cache Miss
        log.info("--> Miss");
        CharArrayWrapper responseWrapper = new CharArrayWrapper (response);
        filterChain.doFilter(request, responseWrapper);
        PrintWriter writer = response.getWriter();
        writer.println(responseWrapper.toString());
        element = new Element (key, responseWrapper.toString());
        cache.put (element);
        writer.flush();
        writer.close();
    }

    /**
     * Processes Cache Hit.
     */
    private void processCacheHit(Cache cache, String key, Element element,
            HttpServletResponse response) throws IOException {
        //  Cache Hit
        log.info ("--> Hit");
        Object value = element.getValue();
        if (value instanceof String) {
            String cachedHtml = (String) element.getValue();
            PrintWriter writer = response.getWriter();

            //  Set Correct Content Type
            if (cachedHtml.startsWith("<?xml ")) {
                response.setContentType("text/xml");
            } else {
                response.setContentType("text/html");
            }
            writer.println(cachedHtml);
            writer.flush();
            writer.close();
        } else {
            cache.remove(key);
            throw new ClassCastException("Cached object is not of type String.  Is of type:  "
                    + value.getClass().getName());
        }
    }

    /**
     * Destroy Cache Filter.  Currently, a no-op.
     */
    public void destroy() {
    }

    /**
     * Gets Complete URL with URL Parameters.
     * @param request HttpServletRequest Object.
     * @return Complete URL.
     */
    private String getUrl(HttpServletRequest request) {
        String url = request.getRequestURI();
        if (request.getQueryString() != null) {
            url = url + "?" + request.getQueryString();
        }
        return url;
    }

    /**
     * Translates URL into MD5 Hash Key.
     * @param url URL String.
     * @return MD5 Hash String.
     */
    private String getHashKey(String url) {
        try {
            return Md5Util.createMd5Hash(url);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}