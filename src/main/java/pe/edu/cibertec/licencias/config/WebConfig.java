package pe.edu.cibertec.licencias.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer
{
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(new RolInterceptor())
                .addPathPatterns("/solicitante/**", "/revisor/**", "/tecnico/**",
                                 "/subgerente/**", "/admin/**");
    }
}