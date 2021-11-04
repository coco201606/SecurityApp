package sample.app;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
public class CustomAuthentication implements HttpAuthenticationMechanism {

    public static final String COOKIE_GLOBAL_SESSION_ID = "GLOBAL_JSESSIONID";
    public static final String REDIRECT_TO = "http://localhost:8080/GlobalSignOn";

    @Inject
    SecurityContext securityContext;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            HttpMessageContext httpMessageContext) throws AuthenticationException {

        System.out.println("CustomAuthentication.validateRequest called.");

        try {
            // CookieからグローバルセッションIDを取得
            String globalSessionID = null;
            Cookie cookies[] = httpServletRequest.getCookies();
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    System.out.println("CustomAuthentication.validateRequest: Cookie " + cookies[i].getName() + "=" + cookies[i].getValue());
                    if (cookies[i].getName().equals(COOKIE_GLOBAL_SESSION_ID)) {
                        globalSessionID = cookies[i].getValue();
                        break;
                    }
                }
            }

            if (globalSessionID != null) {
                System.out.println("CustomAuthentication.validateRequest: GlobalSession ID = " + globalSessionID);
                // グローバルセッションIDからユーザ情報を取得
                UserInfo userInfo = getUserInfo(globalSessionID);
                if (userInfo != null) {
                    // コンテナへログインを通知
                    AuthenticationStatus result
                            = httpMessageContext.notifyContainerAboutLogin(userInfo.getName(), userInfo.getRoles());
                    System.out.println("CustomAuthentication.validateRequest: AuthenticationStatus = " + result);
                    // クッキーを更新
                    Cookie gsidCookie = new Cookie(COOKIE_GLOBAL_SESSION_ID, globalSessionID);
                    gsidCookie.setMaxAge(60 * 60 * 24);
                    gsidCookie.setHttpOnly(true);
                    gsidCookie.setPath("/");
                    httpServletResponse.addCookie(gsidCookie);
                    System.out.println("CustomAuthentication.validateRequest Cookie updated.");
                    return result;
                } else {
                    System.out.println("CustomAuthentication.validateRequest Can't get UserInfo form given Global Session ID.");
                    return AuthenticationStatus.SEND_FAILURE;
                }
            } else {
                // GlobalセッションIDが無い場合はグローバルセッション管理アプリへ転送
                String RedirectTo = (new StringBuffer(REDIRECT_TO)).append("?redirect-to=")
                        .append(httpServletRequest.getRequestURL().toString()).toString();
                System.out.println("CustomAuthentication.validateRequest: No global session found, then redirect to " + RedirectTo);
                httpServletResponse.sendRedirect(RedirectTo);
                return AuthenticationStatus.SEND_CONTINUE;
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new AuthenticationException(ex);
        }

    }

    private UserInfo getUserInfo(String globalSessionID) {

        // グローバルセッションIDからユーザ情報を取得
        // 本来はDBから情報を検索
        UserInfo userInfo = null;
        if (globalSessionID.startsWith("admin")) {
            System.out.println("CustomAuthentication.getUserInfo: generate UserInfo for admin");
            userInfo = new UserInfo("admin");
            userInfo.addRole("ADMIN");
            userInfo.addRole("USER");
            System.out.println("CustomAuthentication.getUserInfo: generate UserInfo for admin: " + userInfo.getName() + ", " + userInfo.getRoles().toString());
        } else if (globalSessionID.startsWith("user")) {
            userInfo = new UserInfo("user");
            userInfo.addRole("USER");
            System.out.println("CustomAuthentication.getUserInfo: generate UserInfo for user: " + userInfo.getName() + ", " + userInfo.getRoles().toString());
        }
        return userInfo;
    }
}
