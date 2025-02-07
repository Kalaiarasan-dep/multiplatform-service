package in.hashconnect.multitenancy.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import in.hashconnect.multitenancy.vo.TenantContext;

//@Component
//@Order(1)
public class TenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String tenantName = req.getHeader("X-TenantID");
        TenantContext.setCurrentTenant(tenantName);
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.setCurrentTenant("");
        }
    }

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
