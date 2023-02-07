package com.hackhalo2.re2;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.hackhalo2.re2.nbt.tags.NBTTag;
import com.hackhalo2.re2.nbt.tags.TagCompound;
import com.hackhalo2.re2.nbt.tags.TagString;

@Mojo(name = "module-data-verify", defaultPhase = LifecyclePhase.VERIFY)
public class DataVerifierMojo extends AbstractMojo {

    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession mavenSession;

    /**
     * The author of the RenderEngine Module<p>
     * This is a required field
     */
    @Parameter(property = "author", required = true)
    private String author;
    
    /**
     * The Main Class for the RenderEngine Module<p>
     * This is a required field
     */
    @Parameter(property = "mainClass", required = true)
    private String mainClass;

    /**
     * The name of the RenderEngine Module<p>
     * This defaults to the project name in the pom.xml file
     */
    @Parameter(property = "name", defaultValue = "${project.name}")
    private String name;

    /**
     * The version of the RenderEngine Module<p>
     * This defaults to the version of the project in the pom.xml file
     */
    @Parameter(property = "version", defaultValue = "${project.version}")
    private String version;

    /**
     * The list of contributors for this RenderEngine Module<p>
     * This array can be empty, and isn't required
     */
    @Parameter(property = "contributors", required = false)
    private String[] contributors;

    /**
     * The Load Order for the Module<p>
     * This allows the RenderEngine to enable the Module when it needs to be enabled. It defaults to "after_all",
     * so if the Module needs to set up the Renderer, you have to change this to before the Renderer initializes
     * (for instance, to "before_all" or "renderer_init").<p>
     * This can be one of the following:
     * <ul>
     * <li>before_all</li>
     * <li>renderer_init</li>
     * <li>renderer_post</li>
     * <li>sound_init</li>
     * <li>sound_post</li>
     * <li>gui_init</li>
     * <li>gui_post</li>
     * <li>after_all</li>
     * </ul><p>
     * <b>NOTE:</b> The case doesn't matter as each string is sanitized before being written to the metadata file, so
     * "After All", "after all", "AFTER_ALL", and "after_all" are all treated as the same thing, but it needs to be 
     * spelled correctly with the right amount of spaces. "After  all" (with two spaces) will be sanitized to "after__all" (with two underscores), 
     * which will cause the RenderEngine to throw an error and not load the Module.
     */
    @Parameter(property = "loadOrder", defaultValue = "after_all")
    private String loadOrder;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.getLog().info("Verifying module.data file...");

        FileInputStream fileIn = null;
        TagCompound compound = null;
        try {
            String rootDir = this.mavenSession.getExecutionRootDirectory();
            fileIn = new FileInputStream(new File(rootDir+File.separator+"module.data"));

            compound = NBTTag.loadCompoundFromStream(new DataInputStream(new BufferedInputStream(fileIn)));
        } catch(Exception e) {
            throw new MojoFailureException("There was an issue loading the module.data file!", e);
        } finally {
            try {
                if(fileIn != null) {
                    fileIn.close();
                }
            } catch(Exception e) {
                throw new MojoFailureException("There was an issue trying to close the FileInputStream in the finally block!", e);
            }
        }

        this.checkMatch("Author", this.author, compound);
        this.checkMatch("MainClass", this.mainClass, compound);
        this.checkMatch("Name", this.name, compound);
        this.checkMatch("Version", this.version, compound);
        this.checkMatch("LoadOrder", this.loadOrder, compound);

        //TODO: Check Contributors

        this.getLog().info("File Verified!");
    }

    private void checkMatch(String name, String paraString, TagCompound compound) throws MojoFailureException {
        //Check the Author Parameter
        this.getLog().info("Checking for "+name+" match...");
        TagString checkString = (TagString)(compound.getTag(name));

        if(!paraString.equals(checkString.getValue())) {
            throw new MojoFailureException(name+" did not match Parameter! Parameter: "+paraString+" TagString: "+checkString.getName()+"|"+checkString.getValue());
        }
    }
    
}
