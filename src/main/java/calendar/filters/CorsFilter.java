package calendar.filters;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class CorsFilter implements Filter {
    private Set<String> origins = new HashSet<>(Set.of("http://localhost:9000"));

    public static final Logger logger = LogManager.getLogger(TokenFilter.class);


    /**
     * Called by the web container to indicate to a filter that it is being placed into service.
     * The servlet container calls the init method exactly once after instantiating the filter.
     * The init method must complete successfully before the filter is asked to do any filtering work.
     * @param filterConfig The configuration information associated with the
     *                     filter instance being initialised
     *
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    /**
     * This method is called by the container each time a request/response pair is passed through the chain due to a client request for a resource at the end of the chain.
     * The FilterChain passed in to this method allows the Filter to pass on the request and response to the next entity in the chain.
     * @param servletRequest  The request to process
     * @param servletResponse The response associated with the request
     * @param filterChain    Provides access to the next filter in the chain for this
     *                 filter to pass the request and response to for further
     *                 processing
     *
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("Cors filter is working on the following request: " + servletRequest);

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse res=((HttpServletResponse)servletResponse);
        String header = request.getHeader("origin");
        if(origins.contains(header)){
            res.addHeader("Access-Control-Allow-Origin", header);
            res.addHeader("Access-Control-Allow-Headers", "*");
            res.addHeader("Access-Control-Allow-Methods",
                    "GET, OPTIONS, HEAD, PUT, POST, DELETE");
            res.addHeader("Access-Control-Allow-Credentials", "true");

            if (request.getMethod().equals("OPTIONS")) {
                res.setStatus(HttpServletResponse.SC_ACCEPTED);
                return;
            }
        }
        filterChain.doFilter(servletRequest, res);
    }

    /**
     * indicate to a filter that it is being taken out of service.
     * This method is only called once all threads within the filter's doFilter method have exited or after a timeout period has passed.
     * After the web container calls this method, it will not call the doFilter method again on this instance of the filter.
     */
    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}