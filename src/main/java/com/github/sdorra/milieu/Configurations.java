/**
 * The MIT License
 *
 * Copyright 2016 Sebastian Sdorra
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.github.sdorra.milieu;

import com.github.drapostolos.typeparser.TypeParser;
import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configurations turns configuration classes into full configured objects. Configurations searches the given object
 * type for {@link Configuration} annotation and resolves them with the given {@link ConfigurationResolver}.
 * 
 * @author Sebastian Sdorra
 */
public final class Configurations {

    private static final Logger LOG = LoggerFactory.getLogger(Configurations.class);
    
    private static final TypeParser PARSER = TypeParser.newBuilder().build();

    private static final ConfigurationResolver RESOLVER = new EnvironmentConfigurationResolver();
    
    private Configurations() {
    }

    /**
     * Delegates to {@link #get(ConfigurationResolver, Class)} with the default {@link ConfigurationResolver}. The
     * default resolver is {@link EnvironmentConfigurationResolver}.
     * 
     * @param <T> type of configuration object
     * @param clazz class of configuration object
     * 
     * @return instance of the configuration object
     */
    public static <T> T get(Class<T> clazz) {
        return get(RESOLVER, clazz);
    }

    /**
     * Returns an configured instance of the given class. The method searches the given class for {@link Configuration}
     * annotations and applies the resolved values to the them. The given class must be public and must have a public 
     * constructor without arguments. The method throws a {@link ConfigurationException}, if the class could not be 
     * instantiated or a value could be applied to a configuration field.
     * 
     * @param <T> type of configuration object
     * @param resolver configuration resolver
     * @param clazz class of configuration object
     * 
     * @return instance of the configuration object
     */
    public static <T> T get(ConfigurationResolver resolver, Class<T> clazz) {
        T instance = createInstance(clazz);
        for (Field field : clazz.getDeclaredFields()) {
            Configuration config = field.getAnnotation(Configuration.class);
            if (config != null) {
                String key = getConfigurationKey(field, config);
                String value = resolver.getValue(key);
                if (value != null) {
                    Object objectValue = PARSER.parse(value, field.getType());
                    if (objectValue != null) {
                        LOG.trace("set value {} to field {} resolved by key {}", value, key, field.getName());
                        setField(instance, field, objectValue);
                    } else {
                        LOG.trace("resolver returned null for key {}, use fields default value", key);
                    }
                } else {
                    LOG.trace("resolver returned null for key {}, use fields default value", key);
                }
            }
        }
        return instance;
    }
    
    private static String getConfigurationKey(Field field, Configuration configuration){
        String key = configuration.value();
        if ("".equals(key)){
            key = field.getName();
        }
        return key;
    }

    private static void setField(Object instance, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalArgumentException ex) {
            throw new ConfigurationException("failed to set configuration field ".concat(field.getName()), ex);
        } catch (IllegalAccessException ex) {
            throw new ConfigurationException("failed to set configuration field ".concat(field.getName()), ex);
        }
    }

    private static <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException ex) {
            throw new ConfigurationException("could not create instance for configuration", ex);
        } catch (IllegalAccessException ex) {
            throw new ConfigurationException("could not create instance for configuration", ex);
        }
    }

}
