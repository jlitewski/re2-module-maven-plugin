package com.hackhalo2.re2;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.hackhalo2.re2.nbt.exceptions.NBTException;
import com.hackhalo2.re2.nbt.tags.CompoundBuilder;
import com.hackhalo2.re2.nbt.tags.NBTTag;
import com.hackhalo2.re2.nbt.tags.TagCompound;
import com.hackhalo2.re2.nbt.tags.TagList;
import com.hackhalo2.re2.nbt.tags.TagString;
import com.hackhalo2.re2.nbt.tags.TagType;

/**
 * This Mojo builds the metadata file RenderEngine Modules use to function
 */
@Mojo(name = "module-bundle", defaultPhase = LifecyclePhase.COMPILE)
public class ModuleDataMojo extends AbstractMojo {

    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession mavenSession;

    /**
     * The author of the RenderEngine Module<p>
     * This is a required field
     */
    @Parameter(property = "author", required = true)
    @NotNull
    private String author;
    
    /**
     * The Main Class for the RenderEngine Module<p>
     * This is a required field
     */
    @Parameter(property = "mainClass", required = true)
    @NotNull
    private String mainClass;

    /**
     * The name of the RenderEngine Module<p>
     * This defaults to the project name in the pom.xml file
     */
    @Parameter(property = "name", defaultValue = "${project.name}")
    @NotNull
    private String name;

    /**
     * The version of the RenderEngine Module<p>
     * This defaults to the version of the project in the pom.xml file
     */
    @Parameter(property = "version", defaultValue = "${project.version}")
    @NotNull
    private String version;

    /**
     * The list of contributors for this RenderEngine Module<p>
     * This array can be empty, and isn't required
     */
    @Parameter(property = "contributors", required = false)
    @Nullable
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
    @NotNull
    private String loadOrder;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        CompoundBuilder builder = new CompoundBuilder();

        try {
            //Add all the tags that we can here
            builder.start(this.name)
                .addString("Author", this.author).addString("MainClass", this.mainClass)
                .addString("Name", this.name).addString("Version", this.version)
                .addString("LoadOrder", this.loadOrder);
            
            //If the contributors list isn't null, add it
            if(this.contributors != null && this.contributors.length > 0) {
                TagList tagContributors = new TagList("Contributors", TagType.STRING);
                for(String contributor : this.contributors) {
                    tagContributors.addTag(new TagString(null, contributor));
                }
                builder.addList(tagContributors);
            }

        } catch (NBTException e) {
            throw new MojoFailureException("There was a problem with constructing the NBT Tag!", e);
        }

        FileOutputStream fileOut = null;
        try {
            String rootDir = this.mavenSession.getExecutionRootDirectory();
            fileOut = new FileOutputStream(new File(rootDir));

            TagCompound compound = builder.build();
            if(compound == null) {
                throw new NullPointerException("We got a null tag when it shouldn't have been null!");
            }

            NBTTag.saveCompoundToStream(compound, new DataOutputStream(fileOut));

        } catch (Exception e) {
            throw new MojoFailureException("There was an issue saving the NBT Tag!", e);
        } finally {
            try {
                if(fileOut != null) {
                    fileOut.close();
                }
            } catch(Exception e) {
                throw new MojoFailureException("There was an issue trying to close the FileOutputStream in the finally block!", e);
            }
        }
    }
    
}
