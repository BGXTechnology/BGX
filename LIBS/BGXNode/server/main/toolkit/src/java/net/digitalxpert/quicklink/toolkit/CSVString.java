/* $Id: //depot/MNS/bgxnetwork/server/main/toolkit/src/java/net/bgx/bgxnetwork/toolkit/CSVString.java#1 $
 * $DateTime: 2006/03/07 13:52:51 $
 * $Change: 6772 $
 * $Author: grouzintsev $
 */
package net.bgx.bgxnetwork.toolkit;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

/**
 * Provides methods to present collection as string of comma separated values and vice versa.
 * <p/>
 * For usage look at corresponding JUnit test case.
 */
public class CSVString {
    /**
     * Provides way to convert an object from the collection to string value.
     * If method {@link #getValue(java.lang.Object)} will return <code>null</code>
     * then value will be skipped.
     */
    public interface ValueBuilder {
        String getValue(Object anObject);
    }

    /**
     * Provides way to convert a string value to object.
     */
    public interface ObjectBuilder {
        Object getObject(String aValue);
    }

    /**
     * Wraps given collection into string of comma separated values.
     * @param aCollection collection of objects that should be wrapped to CSV string.
     * @param aValueProvider provides way to convert an object from the collection to value.
     * @return string of comma separated values or <code>null</code> if collection is empty.
     */
    public static String create(Collection aCollection, ValueBuilder aValueProvider) {
        if (aCollection.isEmpty()) return null;
        StringBuffer s = new StringBuffer();
        int valueCounter = 0;
        for (Iterator values = aCollection.iterator(); values.hasNext();) {
            String value = aValueProvider.getValue(values.next());
            if (value == null) continue;
            if (valueCounter > 0) s.append(',');
            s.append(value);
            ++valueCounter;
        }
        return s.toString();
    }

    /**
     * Parse given string and produces list of objects produced by given {@link ObjectBuilder}.
     * <p/>
     * <i>Warning: the first string value will be pad from left side by one space (' ') character.</i>
     * @param aCommaSeparatedValues a string of comma separated values.
     * @param aCollection
     * @param skipNull if <code>true</code> then <code>null</code> object will not be added to
     * produced list.
     * @param anObjectProvider
     * @return list of produced objects.
     */
    public static Collection parse(String aCommaSeparatedValues, Collection aCollection, boolean skipNull,
                                   ObjectBuilder anObjectProvider) {
        if (aCommaSeparatedValues == null) return aCollection;
        String s = " " + aCommaSeparatedValues + ","; // simplify empty string and last value processing
        int c = 0, l = 0;
        while ((c = s.indexOf(',', c)) != (-1)) {
            Object object = anObjectProvider.getObject(s.substring(l + 1, c));
            if (object!=null || (object==null && skipNull==false) ) aCollection.add(object);
            l = c;
            c++;
        }
        return aCollection;
    }

    /**
     * Synonim for <pre><code>return (Set) parse(aCommaSeparatedValues, new HashSet(), true, anObjectProvider);</code></pre>
     */
    public static Set parseSet(String aCommaSeparatedValues, ObjectBuilder anObjectProvider) {
        return (Set) parse(aCommaSeparatedValues, new HashSet(), true, anObjectProvider);
    }
}