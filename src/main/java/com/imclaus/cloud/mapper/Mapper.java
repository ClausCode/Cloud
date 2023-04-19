package com.imclaus.cloud.mapper;

import io.r2dbc.spi.Row;

public interface Mapper<T> {
    T map(Row row);
}
