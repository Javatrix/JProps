package com.github.javatrix.jprops.exception;

/**
 * @author Javatrix
 * Exception thrown when specified .properties file does not contain the property user is trying to get.
 */
public class PropertyNotFoundException extends Exception {
    public PropertyNotFoundException(String key, String file) {
        super("Property " + key + " not found in configuration file " + file);
    }

}
