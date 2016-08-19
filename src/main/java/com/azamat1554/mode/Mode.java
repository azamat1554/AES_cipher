package com.azamat1554.mode;

import java.util.stream.Stream;

/**
 * Перечисление, которое содержит режимы блочноно шифрования
 */
public enum Mode {
    ECB, CBC

    // TODO: 8/17/16 delete 
//    public static String[] getNames() {
//        return Stream.of(Mode.values()).map(Mode::name).toArray(String[]::new);
//    }
}
