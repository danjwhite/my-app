package com.example.myapp.test;

import com.example.myapp.config.SecurityConfig;
import com.example.myapp.config.WebConfig;
import com.example.myapp.converter.RoleConverter;
import com.example.myapp.service.UserService;
import com.example.myapp.web.AccessDeniedHandlerImpl;
import com.example.myapp.web.GlobalExceptionHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

@RunWith(SpringRunner.class)
public abstract class WebMvcBaseTest {

    private static final UserDetailsService userDetailsServiceMock = EasyMock.strictMock(UserDetailsService.class);
    private static final RoleConverter roleConverterMock = EasyMock.strictMock(RoleConverter.class);

    private static Object[] mocks;

    protected static void initMocks(Object... mocks) {
        Object[] defaultMocks = new Object[] {userDetailsServiceMock, roleConverterMock};

        if (mocks != null && mocks.length > 0) {
            WebMvcBaseTest.mocks = ArrayUtils.addAll(defaultMocks, mocks);
        } else {
            WebMvcBaseTest.mocks = defaultMocks;
        }
    }

    protected void replayAll() {
        EasyMock.replay(Objects.requireNonNull(mocks, "Mocks have not been initialized."));
    }

    protected void verifyAll() {
        EasyMock.verify(Objects.requireNonNull(mocks, "Mocks have not been initialized."));
    }

    protected void resetAll() {
        EasyMock.reset(Objects.requireNonNull(mocks, "Mocks have not been initialized."));
    }

    @Configuration
    @Import(value = {SecurityConfig.class, WebConfig.class})
    protected static class TestConfig {

        @Bean
        public AccessDeniedHandler accessDeniedHandler() {
            return new AccessDeniedHandlerImpl();
        }

        @Bean
        public GlobalExceptionHandler globalExceptionHandler() {
            return new GlobalExceptionHandler();
        }

        @Bean("userDetailsServiceImpl")
        public UserDetailsService userDetailsService() {
            return userDetailsServiceMock;
        }

        @Bean
        public RoleConverter roleConverter() {
            return roleConverterMock;
        }
    }
}
