package calendar.filters;

import calendar.entities.Role;
import calendar.entities.enums.RoleType;
import calendar.filters.entity.MutableHttpServletRequest;
import calendar.service.EventService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.*;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;


public class RoleFilter implements Filter {

    private final EventService eventService;
    public static final Logger logger = LogManager.getLogger(TokenFilter.class);

    public RoleFilter(EventService eventService) {
        this.eventService = eventService;
    }


    /**
     * Called by the web container to indicate to a filter that it is being placed into service.
     * The servlet container calls the init method exactly once after instantiating the filter.
     * The init method must complete successfully before the filter is asked to do any filtering work.
     *
     * @param filterConfig The configuration information associated with the
     *                     filter instance being initialised
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    /**
     * Processes a request/response pair passed through the Filter Chain due to a client request for a resource at the end of the chain.
     * The token in the header of the request is being checked, if token is valid and correct, this filter passes on the request and response to the next entity in the chain.
     * If token invalid the filter return an Unauthorized response.
     *
     * @param servletRequest  The request to process
     * @param servletResponse The response associated with the request
     * @param filterChain     Provides access to the next filter in the chain for this
     *                        filter to pass the request and response to for further
     *                        processing
     * @throws IOException      if an I/O exception occurs during the processing of the request/response.
     * @throws ServletException if the processing fails.
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        logger.info("Role filter is working on the following request: " + servletRequest);

        String[] listOfAdminPermissions = {"/event/removeGuest", "/event/inviteGuest", "/event/updateEvent/isPublic",
                "/event/updateEvent/location", "/event/updateEvent/description", "/event/updateEvent/event","/event/leaveEvent"};

        MutableHttpServletRequest req = new MutableHttpServletRequest((HttpServletRequest) servletRequest);
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        String tempEventId = req.getParameter("eventId");
        String url = req.getRequestURI();

        int userId = (int) req.getAttribute("userId");
        int eventId = 0;

        if (tempEventId != null) {
            eventId = Integer.parseInt(tempEventId);
        }

        Role role = eventService.getSpecificRole(userId, eventId);

        if (role != null) {

            // ~~~~~~ Organizer ~~~~~~
            if (role.getRoleType() == RoleType.ORGANIZER) {
                if(url.equals("/event/leaveEvent")){
                    returnBadResponse(res);
                } // Organizer cant use the leave event function, use delete event instead!
                req.setAttribute("role", role);
                req.setAttribute("roleType", role.getRoleType());
                filterChain.doFilter(req, res);


            // ~~~~~~ Admin ~~~~~~
            } else if (role.getRoleType() == RoleType.ADMIN) {
                if (Arrays.asList(listOfAdminPermissions).contains(url)) {
                    req.setAttribute("role", role);
                    req.setAttribute("roleType", role.getRoleType());
                    filterChain.doFilter(req, res);
                } else { // Something that admin cant update
                    returnBadResponse(res);
                }

                // ~~~~~~ Guest ~~~~~~
            } else if(role.getRoleType() == RoleType.GUEST){
                if(url.equals("/event/leaveEvent")){
                    req.setAttribute("role", role);
                    req.setAttribute("roleType", role.getRoleType());
                    filterChain.doFilter(req, res);
                }else {
                    returnBadResponse(res);
                }
            }

        } else { // Role does not exist.
            returnBadResponse(res);
        }
    }

    /**
     * Sends an error response to the client using status code 401, with message 'Invalid role type'.
     *
     * @param res, HttpServletResponse object, contains response to a servlet request.
     * @throws IOException, if an input or output exception occurs.
     */
    private void returnBadResponse(HttpServletResponse res) throws IOException {
        res.sendError(401, "Invalid role type");
    }

    /**
     * indicate to a filter that it is being taken out of service.
     * This method is only called once all threads within the filter's doFilter method have exited or after a timeout period has passed.
     * After the web container calls this method, it will not call the doFilter method again on this instance of the filter.
     * This method gives the filter an opportunity to clean up any resources that are being held.
     */
    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}