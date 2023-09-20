package com.github.javatrix.jprops;

import com.github.javatrix.jprops.exception.InvalidPropertyTypeException;
import com.github.javatrix.jprops.exception.PropertyNotFoundException;

import java.io.*;
import java.util.*;

/**
 * @author Javatrix
 * Class representing .properties file.
 */
public class Properties {

    /**
     * Separator used to differentiate key from value in line.
     */
    public String SEPARATOR = "=";

    private final File propertiesFile;
    private final List<String> lines = new ArrayList<>();
    private final Map<String, String> values = new HashMap<>();

    /**
     * @param propertiesFile File object determining in which file the data is stored in
     * @param createFile     If true, this constructor will create new file on disk if it doesn't exist. Otherwise, it will just store the data at runtime.
     */
    public Properties(File propertiesFile, boolean createFile) {
        this.propertiesFile = propertiesFile;
        try {
            if (createFile) {
                createFileIfNeeded();
            }
            reloadFromFile();
        } catch (IOException ex) {
            System.err.println("Error reading properties file " + propertiesFile.getAbsolutePath() + ": " + ex + "\n\t" + Arrays.toString(ex.getStackTrace()));
        }
    }

    /**
     * @param propertiesFile File object determining in which file the data is stored in.
     */
    public Properties(File propertiesFile) {
        this(propertiesFile, true);
    }

    /**
     * Loads data from specified file. If the file does not exist, this method will do nothing. All changes made to the properties will not be saved until you call {@link Properties#save() save()}.
     *
     * @throws IOException in case of reading error.
     */
    public void reloadFromFile() throws IOException {
        if (!propertiesFile.exists()) {
            return;
        }

        clear();

        BufferedReader reader = new BufferedReader(new FileReader(propertiesFile));
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        reader.close();
    }

    /**
     * Saves all changed properties to specified file. If the file doesn't exist, it tries to create it.
     *
     * @throws IOException if there is an error writing to the file.
     */
    public void save() throws IOException {
        createFileIfNeeded();

        BufferedWriter writer = new BufferedWriter(new FileWriter(propertiesFile));
        List<String> newLines = new ArrayList<>(lines);
        for (String key : values.keySet()) {
            int i = 0;
            boolean valueSet = false;
            for (String line : lines) {
                if (line.startsWith(key + SEPARATOR)) {
                    newLines.remove(line);
                    newLines.add(i, key + SEPARATOR + values.get(key));
                    valueSet = true;
                    break;
                }
                i++;
            }
            if (!valueSet) {
                newLines.add(key + SEPARATOR + values.get(key));
            }
        }
        StringBuilder newContent = new StringBuilder();
        for (String line : newLines) {
            newContent.append(line).append("\n");
        }
        writer.write(newContent.toString());
        writer.close();
    }

    /**
     * @param key Key to get the value from.
     * @return String value stored under specified key, if the specified key contains different type of value, throws {@link InvalidPropertyTypeException InvalidPropertyTypeException}.
     * @throws PropertyNotFoundException if desired property does not exist.
     */
    public String getString(String key) throws PropertyNotFoundException {
        if (values.containsKey(key)) {
            return values.get(key);
        }

        for (String line : lines) {
            if (line.startsWith(key + SEPARATOR)) {
                String value = getLineValue(line);
                values.put(key, value);
                return value;
            }
        }

        throw new PropertyNotFoundException(key, propertiesFile.getName());
    }

    /**
     * @param key Key to get the value from.
     * @return Integer value stored under specified key, if the specified key contains different type of value, throws {@link InvalidPropertyTypeException InvalidPropertyTypeException}.
     * @throws PropertyNotFoundException if desired property does not exist.
     */
    public int getInt(String key) throws PropertyNotFoundException {
        try {
            return Integer.parseInt(getString(key));
        } catch (NumberFormatException ex) {
            throw new InvalidPropertyTypeException(key, Integer.class);
        }
    }

    /**
     * @param key Key to get the value from.
     * @return Boolean value stored under specified key, if the specified key contains different type of value, throws {@link InvalidPropertyTypeException InvalidPropertyTypeException}.
     * @throws PropertyNotFoundException if desired property does not exist.
     */
    public boolean getBoolean(String key) throws PropertyNotFoundException {
        try {
            return Boolean.parseBoolean(getString(key));
        } catch (NumberFormatException ex) {
            throw new InvalidPropertyTypeException(key, Boolean.class);
        }
    }

    /**
     * @param key Key to get the value from.
     * @return Double value stored under specified key, if the specified key contains different type of value, throws {@link InvalidPropertyTypeException InvalidPropertyTypeException}.
     * @throws PropertyNotFoundException if desired property does not exist.
     */
    public double getDouble(String key) throws PropertyNotFoundException {
        try {
            return Double.parseDouble(getString(key));
        } catch (NumberFormatException ex) {
            throw new InvalidPropertyTypeException(key, Double.class);
        }
    }

    /**
     * @param key Key to get the value from.
     * @return Float value stored under specified key, if the specified key contains different type of value, throws {@link InvalidPropertyTypeException InvalidPropertyTypeException}.
     * @throws PropertyNotFoundException if desired property does not exist.
     */
    public float getFloat(String key) throws PropertyNotFoundException {
        try {
            return Float.parseFloat(getString(key).replace(" ", ""));
        } catch (NumberFormatException ex) {
            throw new InvalidPropertyTypeException(key, Float.class);
        }
    }

    /**
     * @param key Key to get the value from.
     * @return Byte value stored under specified key, if the specified key contains different type of value, throws {@link InvalidPropertyTypeException InvalidPropertyTypeException}.
     * @throws PropertyNotFoundException if desired property does not exist.
     */
    public byte getByte(String key) throws PropertyNotFoundException {
        try {
            return Byte.parseByte(getString(key).replace(" ", ""));
        } catch (NumberFormatException ex) {
            throw new InvalidPropertyTypeException(key, Byte.class);
        }
    }

    /**
     * Assigns specified value to the key.
     *
     * @param key   Key which will be assigned the value.
     * @param value Value to assign.
     */
    public void setValue(String key, Object value) {
        values.put(key, String.valueOf(value));
    }

    /**
     * @param line The line to scan for value
     * @return Stored property if line is in the correct format, empty {@link String String} otherwise.
     */
    private String getLineValue(String line) {
        String[] tokens = line.split(SEPARATOR, 2);

        if (tokens.length == 2) {
            return tokens[1];
        }

        return "";
    }

    /**
     * Creates specified .properties file if it doesn't exist.
     *
     * @throws IOException if creation fails, e.g. if java process does not have permissions to write to disk.
     */
    public void createFileIfNeeded() throws IOException {
        if (!propertiesFile.exists()) {
            propertiesFile.createNewFile();
        }
    }

    /**
     * Resets all values and clears cache.
     */
    public void clear() {
        lines.clear();
        values.clear();
    }

    /**
     * @return The file in which properties are stored. This file doesn't have to exist, and is only an indicator of desired location.
     */
    public File getFile() {
        return propertiesFile;
    }
}
