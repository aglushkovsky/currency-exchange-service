package io.github.aglushkovsky.mapper;

public interface Mapper<T, F> {
    F mapFrom(T t);
}
