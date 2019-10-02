package com.example.myapp.builder;

import javax.persistence.EntityManager;

public class AbstractEntityBuilder<T> extends AbstractBuilder<T> {

    private final EntityManager entityManager;

    protected AbstractEntityBuilder(T object, EntityManager entityManager) {
        super(object);
        this.entityManager = entityManager;
    }

    @Override
    public T build() {
        if (entityManager != null) {
            entityManager.persist(getObject());
        }

        return getObject();
    }
}
