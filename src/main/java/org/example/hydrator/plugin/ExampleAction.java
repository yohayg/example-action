/*
 * Copyright © 2017 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

// If you change this, make sure to update the exported-packages variable in the pom.xml file
package org.example.hydrator.plugin;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Macro;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.cdap.api.plugin.PluginConfig;
import co.cask.cdap.etl.api.PipelineConfigurer;
import co.cask.cdap.etl.api.action.Action;
import co.cask.cdap.etl.api.action.ActionContext;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;


/**
 * Example Action Plugin - This example action plugin can be used as a starting point for
 * developing new Action plugins. For full documentation, please visit:
 * https://docs.cask.co/cdap/4.2.0/en/developer-manual/pipelines/developing-plugins/creating-a-plugin.html
 */
@Plugin(type = Action.PLUGIN_TYPE)
@Name(ExampleAction.PLUGIN_NAME) // <- NOTE: The name of the plugin should match the
                                 //    name of the docs and widget json files.
@Description("This example action plugin should give you a good starting point for building your own plugin.")
public class ExampleAction extends Action {
  // You can use this to log messages for debugging your plugin.
  private static final Logger LOG = LoggerFactory.getLogger(ExampleAction.class);

  /**
   * This plugin name is what will be displayed in the UI when you deploy your plugin.
   * It should also match the name of the Widgets config and the Docs file. For example,
   * there should be files named <project root>/widgets/ExampleAction-action.json and
   * <project root>/docs/ExampleAction-action.md
   */
  public static final String PLUGIN_NAME = "ExampleAction";

  /**
   * This variable will hold all the settings that were entered by the user when configuring the plugin.
   * See {@link ExampleActionConfig} class at the bottom of this file for more details.
   */
  private final ExampleActionConfig config;

  @VisibleForTesting
  public ExampleAction(ExampleActionConfig config) {
    this.config = config;
  }

  /**
   * This function is executed by the Pipelines framework when the Pipeline is deployed. This
   * is a good place to validate any configuration options the user has entered. If this throws
   * an exception, the Pipeline will not be deployed and the user will be shown the error message.
   */
  @Override
  public void configurePipeline(PipelineConfigurer pipelineConfigurer) throws IllegalArgumentException {
    super.configurePipeline(pipelineConfigurer);
    LOG.debug(String.format("Running the 'configurePipeline' method of the %s plugin.", PLUGIN_NAME));
    config.validate();
  }

  /**
   * This is where you add the main logic of the plugin. An Action plugin executes in a single container
   * before or after executing a Spark or MapReduce pipeline. You can add any Java code you'd like here such as
   * validating or moving files, setting additional parameters for the rest of the pipeline, or sending
   * notifications such as Email or external web calls.
   */
  @Override
  public void run(ActionContext context) throws Exception {
    LOG.debug(String.format("Running the 'run' method of the %s plugin.", PLUGIN_NAME));
    // It's a good idea to validate the configuration one last time. This is in case the user
    // entered any macros (E.g. ${file-path}) in the configuration options which can only be
    // validated when the pipeline is executed.
    config.validate();

    // The main logic of your action plugin goes here.

    // This is an example of setting arguments that can be used later by plugins in your Pipeline.
    // This can be useful if you are pulling configuration from an external webservice for example.
    context.getArguments().set("example.action.arg",
                               "This value can be used by other plugins in the Pipeline by " +
                                 "specifying ${example.action.arg}.");
  }

  /**
   * The config class for {@link ExampleAction} that contains all properties that need to be filled in by
   * the user when building a Pipeline.
   */
  public static class ExampleActionConfig extends PluginConfig {

    @Macro // <-- Optional annotation that enables the user to enter Macros into the value of the config
           // This means the value of this option might not be known at deploy time.
    @Description("This is the description for the config option that will be displayed in the UI when the " +
                  "user hovers over the 'info' icon.")
    @Nullable // <-- Indicates that the config is Optional.
    private String exampleConfigOption;

    @VisibleForTesting
    public ExampleActionConfig(@Nullable String exampleConfigOption) {
      this.exampleConfigOption = exampleConfigOption;
    }

    /**
     * You can leverage this function to validate the configure options entered by the user.
     */
    public void validate() throws IllegalArgumentException {
      // The containsMacro function can be used to check if there is a macro in the config option.
      // At runtime, the containsMacro function will always return false.
      if (!containsMacro("exampleConfigOption") && !Strings.isNullOrEmpty(exampleConfigOption)) {
        if (exampleConfigOption.contains("test")) {
          throw new IllegalArgumentException("The config value cannot contain the word 'test' for some reason.");
        }
      }
    }
  }
}
