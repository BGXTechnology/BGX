package net.bgx.build;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Check dependency presented by property elements
 */
public class DependencyChecker extends Task {
    private LinkedList _properties = new LinkedList();
    private Path _classpath = null;
    private String _classpathRefName = null;

    public String getClasspathRefName() {
        return _classpathRefName;
    }

    public void setClasspathRefName(String aClasspathRefName) {
        _classpathRefName = aClasspathRefName;
    }

    public Property createProperty() {
        return new Property();
    }

    public void addConfiguredProperty(Property aProperty) {
        _properties.add(aProperty);
    }

    public void execute() throws BuildException {
        _classpath = new Path(getProject());
        for (Iterator i = _properties.iterator(); i.hasNext();) {
            Property property = (Property) i.next();
            File file = new File(property.getValue());
            if (file.exists() == false)
                throw new BuildException("dependency checking failed:\n" +
                        "\tcan't find file '" + property.getValue() + "' " +
                        "specified by property '" + property.getName() + "'");
            getProject().setProperty(property.getName(), property.getValue());
            _classpath.append(new Path(getProject(), property.getValue()));
        }
        if (getClasspathRefName() != null) getProject().addReference(getClasspathRefName(),
                _classpath);
    }
}
