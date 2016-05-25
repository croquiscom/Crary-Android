package com.croquis.crary;

public interface OnTaskComplete<T extends Throwable, U> {
    void onComplete(T error, U result);
}
