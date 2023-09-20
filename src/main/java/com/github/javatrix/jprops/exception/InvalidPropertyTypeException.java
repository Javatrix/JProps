package com.github.javatrix.jprops.exception;

/**
 * @author Javatrix
 * Exception thrown when property value the user is trying to get does not match the one stored in .properites file.
 */
public class InvalidPropertyTypeException extends RuntimeException {

    public InvalidPropertyTypeException(String key, Class<?> expected) {
        super("Invalid property type, " + key + " is not an instance of " + expected.getName());
    }

}
