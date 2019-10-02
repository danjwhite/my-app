package com.example.myapp.builder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AbstractBuilder<T> {

    @Getter(AccessLevel.PROTECTED)
    private final T object;

    public T build() {
        return object;
    }
}
