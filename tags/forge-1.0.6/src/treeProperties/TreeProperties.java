
package treeProperties;


/**
 *  TreeProperties.java
 * 
 * Created on 19.08.2009
 */


import static java.lang.String.*;
import static java.util.Collections.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import treeProperties.types.FileType;


/**
 * The class TreeProperties. This class allows for a tree-like structure of properties-files. This class lays some
 * restrictions on the keys used in properties-files:
 * <ul>
 * <li>The use of "--" is forbidden</li>
 * <li>The suffixes "--properties" and "--transparent-properties" are reserved for specifying additional
 * properties-files in the tree (relative paths are relative to the properties-file where they are referenced)</li>
 * <li>Other suffixes are used by {@link PropertyType}s. PropertyTypes are registered or unregistered using
 * {@link #addType(PropertyType)} or {@link #removeType(PropertyType)}.</li>
 * </ul>
 * Take a look at these files:
 * 
 * <pre>
 * #root.properties
 * title=directions
 * icons--properties=img/icons.properties
 * 
 * #img/icons.properties
 * left--file=left.jpg
 * right--file=right.jpg
 * up--file=up.jpg
 * down--file=down.jpg
 * #note that the path does not contain &quot;img/&quot;
 * size--transparent-properties=size.properties
 * 
 * #img/size.properties
 * width=20
 * height=20
 * </pre>
 * 
 * These properties are retrieved with
 * 
 * <pre>
 * getProperty(&quot;title&quot;) //directions
 * getFile(&quot;icons/left&quot;) //img/left.jpg
 * getFile(&quot;icons/right&quot;) //img/right.jpg
 * getFile(&quot;icons/up&quot;) //img/up.jpg
 * getFile(&quot;icons/down&quot;) //img/down.jpg
 * getProperty(&quot;icons/width&quot;) //20
 * getProperty(&quot;icons/height&quot;) //20
 * </pre>
 * 
 * As you can see, a properties file included with "--transparent-properties" hides its existence from the user. A
 * file included with "--properties" is not hidden. The child properties are accessible as if their keys were
 * prepended with the parent key, separated by a slash.
 * 
 * Note that --file, --properties and --transparent-properties entries will be relative to the included file, even
 * if the properties file is transparent.
 * 
 * Also, the TreeProperties can be retrieved:
 * 
 * <pre>
 * getChildProperties(&quot;icons&quot;)
 * getTransparentProperties(&quot;icons/size&quot;)
 * </pre>
 * 
 * 
 * TODO add edit & save support
 * 
 * @version V0.0 19.08.2009
 * @author Clemens Koza
 * @see Properties
 */
public class TreeProperties implements Iterable<PropertyElement> {
    private static final Map<String, PropertyType<?>>   suffixes;
    private static final Map<Class<?>, PropertyType<?>> types;
    
    private static final String                         transparent = "transparent-properties";
    private static final String                         child       = "properties";
    
    static {
        types = new HashMap<Class<?>, PropertyType<?>>();
        suffixes = new HashMap<String, PropertyType<?>>();
        PropertyType<?>[] pt = {new FileType()};
        for(PropertyType<?> type:pt) {
            addType(type);
        }
    }
    
    private File                                        path;
    private Map<Class<?>, PropertyType<?>>              instanceTypes;
    private Map<String, PropertyType<?>>                instanceSuffixes;
    private HashMap<String, Object>                     properties;
    private List<Exception>                             exceptions;
    
    public static void addType(PropertyType<?> type) {
        types.put(type.getType(), type);
        suffixes.put(type.getSuffix(), type);
    }
    
    public static void removeType(PropertyType<?> type) {
        types.remove(type.getType());
        suffixes.remove(type.getSuffix());
    }
    
    /**
     * Delegate to {@link #TreeProperties(File)} with a new {@link File#File(String)}
     */
    public TreeProperties(String f) throws IOException {
        this(new File(f));
    }
    
    /**
     * Delegate to {@link #TreeProperties(File)} with a new {@link File#File(File, String)}
     */
    public TreeProperties(File parent, String f) throws IOException {
        this(new File(parent, f));
    }
    
    /**
     * The constructor is forgiving in the way that Exceptions are not directly forwarded. The only fatal exception
     * is if the parameter is null or not found. Instead, the rest of the properties are processed, so that the
     * erroneous properties are the only ones not present in this TreeProperties. Afterwards, the exceptions can be
     * accessed.
     */
    public TreeProperties(File f) throws IOException {
        if(f == null) throw new FileNotFoundException("null");
        this.path = f.getParentFile();
        instanceTypes = new HashMap<Class<?>, PropertyType<?>>(types);
        instanceSuffixes = new HashMap<String, PropertyType<?>>(suffixes);
        Properties p = new Properties();
        
//        BufferedReader r = new BufferedReader(new FileReader(f));
//        p.load(r);
//        r.close();
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(f));
        p.load(is);
        is.close();
        
        Set<Entry<Object, Object>> entries = p.entrySet();
        properties = new HashMap<String, Object>();
        List<Exception> exceptions = new ArrayList<Exception>();
        this.exceptions = unmodifiableList(exceptions);
        for(Entry<Object, Object> entry:entries) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            try {
                String[] parts = key.split("--", 2);
                Object result;
                if(parts.length == 1) {
                    //Regular
                    result = value;
                } else {
                    //suffix
                    if(parts[1].equals(transparent) || parts[1].equals(child)) {
                        TreeProperties child = new TreeProperties(path, FileType.getPath(value));
                        exceptions.addAll(child.exceptions);
                        result = child;
                    } else {
                        PropertyType<?> t = instanceSuffixes.get(parts[1]);
                        if(t == null) throw new IllegalArgumentException("No content type: " + parts[1]);
                        result = t.toObject(this, value);
                    }
                }
                properties.put(key, result);
            } catch(Exception ex) {
                exceptions.add(new Exception(format("File '%s', Property '%s':%n    %s", f, key, ex.getMessage()),
                        ex.getCause()));
            }
        }
    }
    
    /**
     * Returns the exceptions that were thrown while creating the tree properties.
     */
    public List<Exception> getExceptions() {
        return exceptions;
    }
    
    /**
     * If exceptions occurred during construction, this method throws an IOException that combines the messages of
     * those exceptions.
     */
    public void rethrow() throws IOException {
        if(exceptions.isEmpty()) return;
        StringBuilder sb = new StringBuilder("The following exceptions occurred:");
        for(Exception ex:exceptions) {
            sb.append("\n");
            sb.append(ex.getMessage());
        }
        throw new IOException(sb.toString());
    }
    
    /**
     * Returns the parent directory of this TreeProperties.
     */
    public File getPath() {
        return path;
    }
    
    /**
     * Checks if the key is valid for a query and throws an {@link IllegalArgumentException} if not. Slashes are
     * allowed in this method, but suffixes are not
     */
    private void checkQueryKey(String key) {
        if(key.contains("--")) throw new IllegalArgumentException("Invalid key: " + key);
    }
    
    /**
     * Retrieves the string property for the given key.
     */
    public String getProperty(String key) {
        return getProperty(key, String.class);
    }
    
    /**
     * Convenience for {@code getProperty(key, File.class)}.
     */
    public File getFile(String key) {
        return getProperty(key, File.class);
    }
    
    /**
     * Retrieves the child properties for the given key. Transparent properties can't be retrieved this way.
     */
    public TreeProperties getChildProperties(String key) {
        return (TreeProperties) getProperty(key, "--" + child, true);
    }
    
    /**
     * Retrieves the child properties for the given key.
     */
    public TreeProperties getTransparentProperties(String key) {
        return (TreeProperties) getProperty(key, "--" + transparent, true);
    }
    
    /**
     * Returns a property of the given type. This does not work to retrieve tree properties.
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, Class<T> cls) {
        String suffix;
        if(cls == String.class) suffix = "";
        else {
            PropertyType<?> t = instanceTypes.get(cls);
            suffix = "--" + t.getSuffix();
        }
        return (T) getProperty(key, suffix, true);
    }
    
    private Object getProperty(String key, String suffix, boolean top) {
        checkQueryKey(key);
        //first, try the key in the current file, as if there were no slash
        //No subpath - either directly in the properties...
        Object result;
        if((result = properties.get(key + suffix)) != null) return result;
        
        //...or in a transparent properties
        
        //look for all --transparent-properties
        for(Entry<String, Object> entry:properties.entrySet()) {
            if(entry.getKey().endsWith("--" + transparent)) {
                TreeProperties p = (TreeProperties) entry.getValue();
                if((result = p.getProperty(key, suffix, false)) != null) {
                    return result;
                }
            }
        }
        
        //if there is no immediate containment, try the children
        //try every combination
        //for a/b/c, there could be a child "a/b--properties" that contains "c"
        //or "a--properties" with "b/c"
        int index = -1;
        while((index = key.indexOf('/', index + 1)) != -1) {
            String first = key.substring(0, index), second = key.substring(index + 1);
            
            TreeProperties p = (TreeProperties) getProperty(first, "--" + child, false);
            if(p == null) continue;
            if((result = p.getProperty(second, suffix, false)) != null) {
                return result;
            }
        }
        if(top) {
            Exception ex = new Exception("TreeProperties returns null for " + key + suffix);
//        ex.printStackTrace();
            System.err.println(ex);
        }
        return null;
    }
    
    /**
     * Returns an iterator over all the regular entries of this object. That means that transparent or child tree
     * properties are not included.
     */
    public Iterator<PropertyElement> iterator() {
        return iterator("");
    };
    
    private TreePropertiesIterator iterator(String prefix) {
        return new TreePropertiesIterator(prefix);
    }
    
    private final class TreePropertiesIterator implements Iterator<PropertyElement> {
        private final String                    prefix;
        private Iterator<Entry<String, Object>> entries;
        private TreePropertiesIterator          child;
        private PropertyElement                 next;
        
        private TreePropertiesIterator(String prefix) {
            entries = properties.entrySet().iterator();
            this.prefix = prefix;
        }
        
        //After this call, the next element is determined, or the child iterator has next
        
        public boolean hasNext() {
            if(next != null) return true;
            else if(child != null && child.hasNext()) return true;
            else if(entries.hasNext()) {
                Entry<String, Object> entry = entries.next();
                final String[] parts = entry.getKey().split("--");
                final Class<?> cls;
                final Object value = entry.getValue();
                
                if(parts.length == 1) {
                    cls = String.class;
                } else if(parts[1].equals(transparent)) {
                    child = ((TreeProperties) entry.getValue()).iterator(prefix);
                    //recursive, for the case that the child iterator is empty
                    return hasNext();
                } else if(parts[1].equals(TreeProperties.child)) {
                    child = ((TreeProperties) entry.getValue()).iterator(prefix + parts[0] + "/");
                    //recursive, for the case that the child iterator is empty
                    return hasNext();
                } else {
                    //class is determined by the content type
                    PropertyType<?> t = instanceSuffixes.get(parts[1]);
                    cls = t.getType();
                }
                next = new PropertyElement() {
                    
                    public String getKey() {
                        return prefix + parts[0];
                    }
                    
                    
                    public Class<?> getType() {
                        return cls;
                    }
                    
                    
                    public Object getValue() {
                        return value;
                    }
                    
                    
                    public void setValue(String value) {}
                };
                return true;
            } else return false;
        }
        
        
        public PropertyElement next() {
            if(!hasNext()) throw new NoSuchElementException();
            else if(next != null) {
                PropertyElement next = this.next;
                this.next = null;
                return next;
            } else return child.next();
        }
        
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
