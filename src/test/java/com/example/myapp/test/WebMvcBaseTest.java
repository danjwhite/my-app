package com.example.myapp.test;

import com.example.myapp.config.SecurityConfig;
import com.example.myapp.web.AccessDeniedHandlerImpl;
import com.example.myapp.web.GlobalExceptionHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.easymock.EasyMock;
import org.junit.After;
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

    private static Object[] mocks;

    protected static void initMocks(Object... mocks) {
        Object[] defaultMocks = new Object[] {userDetailsServiceMock};

        if (mocks != null && mocks.length > 0) {
            WebMvcBaseTest.mocks = ArrayUtils.addAll(defaultMocks, mocks);
        } else {
            WebMvcBaseTest.mocks = defaultMocks;
        }
    }

    @After
    public void tearDown() {
        EasyMock.reset(getMocks());
    }

    protected void replayAll() {
        EasyMock.replay(getMocks());
    }

    protected void verifyAll() {
        EasyMock.verify(getMocks());
    }

    protected void resetAll() {
        EasyMock.reset(getMocks());
    }

    private Object[] getMocks() {
        return Objects.requireNonNull(mocks, "Mocks have not been initialized.");
    }

    @Configuration
    @Import(value = {SecurityConfig.class})
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
    }
}
