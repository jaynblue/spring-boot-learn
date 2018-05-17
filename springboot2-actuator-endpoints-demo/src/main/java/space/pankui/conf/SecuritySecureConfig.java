package space.pankui.conf;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * @author pankui
 * @date 16/05/2018
 * <pre>
 *
 *  默认情况下，自动配置会保护所有端点.
 *
 * </pre>
 */

@Configuration
public class SecuritySecureConfig extends WebSecurityConfigurerAdapter {
    private final String adminContextPath;

    public SecuritySecureConfig(AdminServerProperties adminServerProperties) {
        this.adminContextPath = adminServerProperties.getContextPath();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");

        http
                //authorizeRequests()配置路径拦截，表明路径访问所对应的权限，角色，认证信息。
                .authorizeRequests()
                .antMatchers(adminContextPath + "/assets/**").permitAll()
                .antMatchers(adminContextPath + "/login").permitAll()
                .anyRequest().authenticated()
                .and()
                //对应表单认证相关的配置
                .formLogin()
                //指定url为"/login"作为自定义的登录界面(登录页面的访问路径)
                .loginPage(adminContextPath + "/login")
                .successHandler(successHandler)
                .and()
                //用户退出操作
                .logout()
                //用户退出所访问的路径，
                .logoutUrl(adminContextPath + "/logout")
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
    }
}
