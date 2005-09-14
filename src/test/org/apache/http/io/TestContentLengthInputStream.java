/*
 * $HeadURL$
 * $Revision$
 * $Date$
 * ====================================================================
 *
 *  Copyright 2002-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.mockup.HttpDataReceiverMockup;
import org.apache.http.util.EncodingUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestContentLengthInputStream extends TestCase {

    public TestContentLengthInputStream(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- TestCase Methods

    public static Test suite() {
        return new TestSuite(TestContentLengthInputStream.class);
    }

    // ------------------------------------------------------------------- Main
    public static void main(String args[]) {
        String[] testCaseName = { TestContentLengthInputStream.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    private static final String CONTENT_CHARSET = "ISO-8859-1";
        
    public void testConstructors() throws Exception {
        new ContentLengthInputStream(new HttpDataReceiverMockup(new byte[] {}), 10);
        try {
            new ContentLengthInputStream(null, 10);
            fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            new ContentLengthInputStream(new HttpDataReceiverMockup(new byte[] {}), -10);
            fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testBasics() throws IOException {
        String correct = "1234567890123456";
        InputStream in = new ContentLengthInputStream(new HttpDataReceiverMockup(
            EncodingUtil.getBytes(correct, CONTENT_CHARSET)), 10L);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buffer = new byte[50];
        int len = in.read(buffer, 0, 2);
        out.write(buffer, 0, len);
        len = in.read(buffer);
        out.write(buffer, 0, len);
        
        String result = EncodingUtil.getString(out.toByteArray(), CONTENT_CHARSET);
        assertEquals(result, "1234567890");
    }

    public void testSkip() throws IOException {
        InputStream in = new ContentLengthInputStream(new HttpDataReceiverMockup(new byte[20]), 10L);
        assertEquals(10, in.skip(10));
        assertTrue(in.read() == -1);

        in = new ContentLengthInputStream(new HttpDataReceiverMockup(new byte[20]), 10L);
        in.read();
        assertEquals(9, in.skip(10));
        assertTrue(in.read() == -1);

        in = new ContentLengthInputStream(new HttpDataReceiverMockup(new byte[20]), 2L);
        in.read();
        in.read();
        assertTrue(in.skip(10) <= 0);
        assertTrue(in.read() == -1);
    }

    public void testClose() throws IOException {
        String correct = "1234567890123456";
        InputStream in = new ContentLengthInputStream(new HttpDataReceiverMockup(
            EncodingUtil.getBytes(correct, CONTENT_CHARSET)), 10L);
        in.close();
        in.close();
        try {
            in.read();
            fail("IOException should have been thrown");
        } catch (IOException ex) {
            // expected
        }
        byte[] tmp = new byte[10]; 
        try {
            in.read(tmp);
            fail("IOException should have been thrown");
        } catch (IOException ex) {
            // expected
        }
        try {
            in.read(tmp, 0, tmp.length);
            fail("IOException should have been thrown");
        } catch (IOException ex) {
            // expected
        }
    }
    
}

