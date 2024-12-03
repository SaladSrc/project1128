package hello.project1128.web.argumentresolver;


import hello.project1128.domain.member.Member;
import hello.project1128.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

//ArgumentResolver : 값 해결사
//이 클래스는 요청을 처리할 때 특정 파라미터에 자동으로 특정 값(세션ID)을 주는 역할을 합니다.
@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    //이 메서드는 애너테이션과 파라미터 타입을 지정하는 역할
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");

        //해당 컨트롤러 메서드의 파라미터가 @Login 어노테이션을 가지고 있고,
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);

        //그 파라미터의 타입이 **Member**일 때만 해당 파라미터에 값을 주입한다.
        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());

        return hasLoginAnnotation && hasMemberType; //이거 두개를 만족하면 아래 코드 실행됨
    }

    //이 메서드는 세션에서, 로그인된 사용자 정보, 즉 세션을 조회하여 해당 파라미터에 값을 주입하는 역할
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        log.info("resolveArgument 실행");

        HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();

        //요청에 세션이 있는지 확인
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        //세션이 있으면
        Object member = session.getAttribute(SessionConst.LOGIN_MEMBER);

        return member;
    }
}
