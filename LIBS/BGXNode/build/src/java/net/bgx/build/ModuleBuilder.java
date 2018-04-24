package net.bgx.build;

import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.BuildException;

import java.io.File;

/**
 * TODO Document type ModuleBuilder
 */
public class ModuleBuilder extends Ant {
    private String _module;

    public String getModule() {
        return _module;
    }

    public void setModule(String aModule) {
        _module = aModule;
    }

    public void execute() throws BuildException {
        String dir = getProject().getProperty(getModule()+".module.dir");
        setDir(new File(dir));
        setInheritAll(true);
        super.execute();
    }
}
