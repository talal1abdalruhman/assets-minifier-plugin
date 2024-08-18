![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/talal1abdalruhman/assets-minifier-plugin/build.yml)
![GitHub Release](https://img.shields.io/github/v/release/talal1abdalruhman/assets-minifier-plugin)

Assets Minifier Plugin
=====================

What is this?
-------------
This project offers a Maven plugin designed to "minify" web resources with an emphasis on safety. 
The primary goal is to reduce file size without making any significant changes to the content or achieving the 
smallest possible size. Instead, it focuses on providing a reliable reduction in file size in most situations.

Using assets-minifier-plugin
---------------------------
You can use `assets-minifier-plugin` in your projects by including it in
the `build/plugins` section of your project's POM.

    <plugin>
      <groupId>com.progressoft.juno</groupId>
      <artifactId>assets-minifier-plugin</artifactId>
      <version>v1.0.0</version>
      <executions>
        <execution>
          <id>assets-minify</id>
          <configuration>
            <sourceDir>${project.build.directory}/${project.build.finalName}/</sourceDir>
            <targetDir>${project.build.directory}/${project.build.finalName}-min/</targetDir>
          </configuration>
          <goals>
            <goal>minify</goal>
          </goals>
        </execution>
      </executions>
    </plugin>

**Mandatory properties are:**

* `sourceDir`: the parent directory in which to scan for resources.
* `targetDir`: the corresponding directory in the built package.

**Optional properties are:**
* `minifyJs`: enable/disable minify javascript files, the default value is true.
* `jsIncludes`: include javascript files pattern, the default is:
   
    ```
    <jsIncludes>
          <jsInclude>**/*.js</jsInclude>
    </jsIncludes>
  
 * `jsExcludes`: exclude javascript files pattern:

    ````
    <jsExcludes>
          <jsExclude>**/*.min.js</jsExclude>
          <jsExclude>**/*.slim.js</jsExclude>
            ...
    </jsExcludes>

* `minifyCss`: enable/disable minify Css files, the default value is true.
* `cssIncludes`: include javascript files pattern, the default is:

    ```
    <cssIncludes>
          <cssInclude>**/*.css</cssInclude>
    </cssIncludes>

* `cssExcludes`: exclude css files pattern:

   ````
   <cssExcludes>
         <cssExclude>**/*.min.css</cssExclude>
            ...
   </cssExcludes>