package hello.project1128;

import hello.project1128.web.argumentresolver.LoginMemberArgumentResolver;
import hello.project1128.web.formatter.MyNumberFormatter;
import hello.project1128.web.interceptor.LoginCheckInterceptor;
import hello.project1128.web.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }

    //스프링 인터셉터
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/new/**", "/css/**", "/*.ico", "/error", "/images/**", "/image/**", "/pictures/**");

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/members/new", "/login", "/logout", "/css/**", "/*.ico", "/error", "/images/**", "/image/**", "/pictures/**");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {


        registry.addFormatter(new MyNumberFormatter());
    }

}
