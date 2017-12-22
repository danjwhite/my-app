package com.example.myapp.config;

import com.example.myapp.dao.INoteDao;
import com.example.myapp.dao.NoteDaoImpl;
import com.example.myapp.service.INoteService;
import com.example.myapp.service.NoteServiceImpl;
import com.example.myapp.web.NoteController;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@Configuration
@ContextConfiguration(classes = {H2DataConfig.class})
public class BeanConfig {

    @Autowired
    private SessionFactory sessionFactory;

    @Bean
    public INoteDao noteDao() {
        return new NoteDaoImpl(sessionFactory);
    }

    @Bean
    public INoteService noteService() {
        return new NoteServiceImpl(noteDao());
    }

    @Bean
    public NoteController noteController() {
        return new NoteController(noteService());
    }
}
