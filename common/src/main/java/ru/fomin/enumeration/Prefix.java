package ru.fomin.enumeration;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Prefix for client and serves side.
 * Parts of name for view in GUI.
 */
@RequiredArgsConstructor
@Getter
public enum Prefix {

    FILE_NAME_PREFIX("file: "),
    DIRECTORY_NAME_PREFIX("directory: ");

  private final String value;


    @Override
    public String toString() {
        return value;
    }
}
