package com.example.myapp.config;

import com.example.myapp.dao.INoteDao;
import com.example.myapp.dao.NoteDaoImpl;
import com.example.myapp.domain.Note;
import com.example.myapp.service.INoteService;
import com.example.myapp.service.NoteServiceImpl;
import com.example.myapp.web.HomeController;
import com.example.myapp.web.NoteController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource("classpath:app.properties")
@EnableTransactionManagement
public class TestConfig {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .build();
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        factoryBean.setDataSource(dataSource());

        Properties props = new Properties();
        props.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        props.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));

        factoryBean.setHibernateProperties(props);
        factoryBean.setAnnotatedClasses(Note.class);

        return factoryBean;
    }

    @Bean
    public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());

        return transactionManager;
    }

    @Bean
    public INoteDao noteDao() {
        return new NoteDaoImpl(sessionFactory().getObject());
    }

    @Bean
    public INoteService noteService() {
        return new NoteServiceImpl(noteDao());
    }

    @Bean
    public HomeController homeController() {
        return new HomeController();
    }

    @Bean
    public NoteController noteController() {
        return new NoteController(noteService());
    }
}
