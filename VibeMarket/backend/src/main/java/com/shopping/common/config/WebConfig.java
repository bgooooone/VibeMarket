package com.shopping.common.config;

import com.shopping.common.interceptor.AuthInterceptor;
import com.shopping.common.resolver.CurrentUserMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private AuthInterceptor authInterceptor;

    @Value("${file.upload-dir:/app/upload}")
    private String uploadDir;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/v1/auth/login",
                        "/api/v1/auth/register",
                        "/api/v1/categories",
                        "/api/v1/products",
                        "/api/v1/products/**",
                        "/api/v1/admin/auth/login"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserMethodArgumentResolver());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保上传目录存在
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            boolean created = uploadPath.mkdirs();
            if (created) {
                System.out.println("成功创建上传目录: " + uploadDir);
            } else {
                System.out.println("创建上传目录失败: " + uploadDir);
            }
        } else {
            System.out.println("上传目录已存在: " + uploadDir);
        }

        // 检查目录权限
        System.out.println("上传目录可读: " + uploadPath.canRead());
        System.out.println("上传目录可写: " + uploadPath.canWrite());
        System.out.println("上传目录绝对路径: " + uploadPath.getAbsolutePath());

        // 配置静态资源访问路径
        String resourceLocation = "file:" + uploadDir + (uploadDir.endsWith("/") ? "" : "/");
        System.out.println("静态资源路径映射: /upload/** -> " + resourceLocation);

        registry.addResourceHandler("/upload/**")
                .addResourceLocations(resourceLocation);
    }
}