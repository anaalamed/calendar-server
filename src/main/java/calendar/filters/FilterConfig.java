package calendar.filters;

import calendar.service.AuthService;
import calendar.service.RoleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    public static final Logger logger = LogManager.getLogger(FilterConfig.class);
    private final AuthService authService;
    private final RoleService roleService;

    @Autowired
    public FilterConfig(AuthService authService, RoleService roleService) {
        System.out.println("AppConfig is created");
        this.authService = authService;
        this.roleService = roleService;
    }

    /**
     * this method is used to register the cors filter
     *
     * @return FilterRegistrationBean<CorsFilter>
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterBean() {
        logger.info("CorsFilterBean has been created");
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
        CorsFilter corsFilter = new CorsFilter();
        registrationBean.setFilter(corsFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1); //set precedence
        return registrationBean;
    }

    /**
     * his method is used to register the token filter
     * the token filter initialized with the auth service
     * the token filter is running first in the filter chain
     *
     * @return FilterRegistrationBean<TokenFilter>
     */
    @Bean
    public FilterRegistrationBean<TokenFilter> filterRegistrationBean() {
        logger.info("FilterRegistrationBean has been created");
        FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<>();
        TokenFilter customURLFilter = new TokenFilter(authService);
        registrationBean.setFilter(customURLFilter);
        registrationBean.addUrlPatterns("/user/update", "/user/delete",
                "/role/removeGuest", "/role/inviteGuest",
                "/event/updateEvent/isPublic", "/event/updateEvent/location",
                "/event/updateEvent/time", "/event/updateEvent/duration","/event/updateEvent/event", "/event/updateEvent/date", "/event/updateEvent/description",
                "/event/updateEvent/title", "/event/updateEvent", "/event/deleteEvent","/role/switchRole", "/event/saveEvent");
        registrationBean.setOrder(2); //set precedence
        return registrationBean;
    }

    /**
     * his method is used to register the token filter
     * the token filter initialized with the auth service
     * the token filter is running first in the filter chain
     *
     * @return FilterRegistrationBean<TokenFilter>
     */
    @Bean
    public FilterRegistrationBean<RoleFilter> RoleFilterBean() {
        logger.info("Filter Role Bean has been created");
        FilterRegistrationBean<RoleFilter> registrationBean = new FilterRegistrationBean<>();
        RoleFilter customURLFilter = new RoleFilter(roleService);
        registrationBean.setFilter(customURLFilter);
        registrationBean.addUrlPatterns("/role/removeGuest", "/role/inviteGuest", "/event/updateEvent/isPublic", "/event/updateEvent/location",
                "/event/updateEvent/time", "/event/updateEvent/duration", "/event/updateEvent/date", "/event/updateEvent/description",
                "/event/updateEvent/title", "/event/updateEvent", "/event/deleteEvent","/role/switchRole");
        registrationBean.setOrder(3); //set precedence
        return registrationBean;
    }

}