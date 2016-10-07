/*
 * Title: ObjectCloner
 * Author: Matthew Boyette
 * Date: 4/13/2016
 * 
 * This class allows you to easily make deep copies of serializable objects.
 * This is merely for convenience; writing a clone method by hand is much more efficient in terms of run-time speed.
 * So if you are writing performance-critical code, do not use this class!
 * 
 * Known Issues:
 * [1]: Objects being copied must implement the Serializable interface.
 * [2]: The serialization deep copy technique creates a new object that is not unique. So in instances where you need to control the number of instances of an object within the Java VM, such as with the Singleton pattern, then this class will not meet your needs.
 * [3]: Transient variables cannot be serialized; they will be initialized with the appropriate Java default value (null, false, or zero). This will not produce any compile-time or run-time errors! You have been warned.
 */

package api.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectCloner
{
    private static ByteArrayInputStream  bin = null;
    private static ByteArrayOutputStream bos = null;
    private static ObjectInputStream     ois = null;
    private static ObjectOutputStream    oos = null;

    public static Object deepCopy(final Object oldObj) throws Exception
    {
        try
        {
            ObjectCloner.bos = new ByteArrayOutputStream();
            ObjectCloner.oos = new ObjectOutputStream(ObjectCloner.bos);
            ObjectCloner.oos.writeObject(oldObj);
            ObjectCloner.oos.flush();
            ObjectCloner.bin = new ByteArrayInputStream(ObjectCloner.bos.toByteArray());
            ObjectCloner.ois = new ObjectInputStream(ObjectCloner.bin);
            return ObjectCloner.ois.readObject();
        }
        catch ( final Exception e )
        {
            throw ( e );
        }
        finally
        {
            ObjectCloner.oos.close();
            ObjectCloner.ois.close();
            ObjectCloner.oos = null;
            ObjectCloner.ois = null;
            ObjectCloner.bos = null;
            ObjectCloner.bin = null;
        }
    }

    private ObjectCloner()
    {
    }
}
