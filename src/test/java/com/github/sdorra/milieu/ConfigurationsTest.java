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

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link Configurations}.
 * 
 * @author Sebastian Sdorra
 */
public class ConfigurationsTest {

    /**
     * Tests {@link Configurations#get(ConfigurationResolver, Class)}.
     */
    @Test
    public void testGet() {
        Map<String,String> config = new HashMap<String, String>();
        config.put("ONE", "cfg-one");
        config.put("two", "cfg-two");
        config.put("THREE", "3");
        config.put("FOUR", "true");
        
        ConfigOne cfg = Configurations.get(new MapConfigurationResolver(config), ConfigOne.class);
        
        assertEquals("cfg-one", cfg.simple);
        assertEquals("cfg-two", cfg.two);
        assertEquals(3, cfg.intValue);
        assertTrue(cfg.boolValue);
        assertEquals("default", cfg.defaultValue);
        assertEquals("without", cfg.withoutConfiguration);
    }
    
    static class ConfigOne {
        
        @Configuration("ONE")
        private String simple;
        
        @Configuration
        private String two;
        
        @Configuration("THREE")
        private int intValue;
        
        @Configuration("FOUR")
        private boolean boolValue;
        
        @Configuration("FIVE")
        private String defaultValue = "default";
        
        private String withoutConfiguration = "without";
        
    }

}