package com.example.paksahara.model;

public interface Manageable <T>{
        void add(T item);
        void update(T item);
        void delete(T item);
}
