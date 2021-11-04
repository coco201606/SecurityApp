package sample.app;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.SecurityContext;

@Named
@RequestScoped
public class SecurityContextBean {
    @Inject
    private SecurityContext securityContext;

    /**
     * @return the securityContext
     */
    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    /**
     * @param securityContext the securityContext to set
     */
    public void setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }
    
}
