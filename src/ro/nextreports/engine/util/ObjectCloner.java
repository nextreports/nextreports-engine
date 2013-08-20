/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.nextreports.engine.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class used to make a deep copy of an serializable object.
 *
 * User: Decebal Suiu
 * Date: May 12, 2004
 * Time: 11:19:38 AM
 */
public final class ObjectCloner {

	// So that nobody can accidentally create an ObjectCloner object
    private ObjectCloner() {
    }

    /**
     * Returns a deep copy of an object.
     */
    @SuppressWarnings("unchecked")
	public static final <T> T deepCopy(T object) throws Exception {
        ObjectOutputStream output = null;
        ObjectInputStream input = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            output = new ObjectOutputStream(bos);

            // serialize and pass the object
            output.writeObject(object);
            output.flush();

            ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
            input = new ObjectInputStream(bin);

            // return the new object
            return (T) input.readObject();
        } catch (Exception e) {
            throw(e);
        } finally {
            close(output);
            close(input);
        }
    }
    
    /**
     * Throws a RuntimeException if an exception occurs. 
     */
    public static final <T> T silenceDeepCopy(T object) {
    	try {
			return deepCopy(object);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }

    private static void close(Closeable closeable) {
    	if (closeable != null) {
    		try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
}
