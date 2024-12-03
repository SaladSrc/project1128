package hello.project1128.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

//project1128에서는 Filter 안쓰고 Interceptor 쓴다.


//이 인터셉터를 쓰려면 WebConfig에 @Bean 등록 해야함~!
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";
    public static final String LOG_ID1 = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        //요청의 고유값인 uuid를 뽑아낸다.(추척을 위해)
        String uuid = UUID.randomUUID().toString();

        //uuid를 LOG_ID에 넣는다.
        request.setAttribute(LOG_ID, uuid);

        //@RequestMapping(컨트롤러): HandlerMethod가 처리
        //정적 리소스: ResourceHttpRequestHandler가 처리

        //즉, handler가 컨트롤러라면
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;//호출할 컨트롤러 메서드의 모든 정보가 포함되어 있다.
        }

        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle [{}]", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        Object logId = (String) request.getAttribute(LOG_ID1);

        log.info("RESPONSE [{}][{}][{}]", logId, requestURI, handler);

        if (ex != null) {
            log.error("afterCompletion error!!", ex);
        }
    }
}
