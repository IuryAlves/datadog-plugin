package org.datadog.jenkins.plugins.datadog.steps;

import hudson.EnvVars;
import hudson.console.AnnotatedLargeText;
import hudson.model.InvisibleAction;
import hudson.model.Run;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nonnull;

import javax.xml.crypto.Data;
import org.datadog.jenkins.plugins.datadog.logs.DatadogTaskListenerDecorator;
import org.jenkinsci.plugins.workflow.flow.FlowExecution;
import org.jenkinsci.plugins.workflow.graph.FlowNode;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.log.LogStorage;
import org.jenkinsci.plugins.workflow.log.TaskListenerDecorator;
import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.BodyInvoker;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.TaskListener;
import java.util.Collections;
import java.util.Set;
import jenkins.YesNoMaybe;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Pipeline plug-in step for recording time-stamps.
 */
public class DatadogOptions extends Step implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean collectLogs = false;

    /** Constructor. */
    @DataBoundConstructor
    public DatadogOptions() {}

    public boolean getCollectLogs() {
        return collectLogs;
    }

    @DataBoundSetter
    public void setCollectLogs(boolean collectLogs) {
        this.collectLogs = collectLogs;
    }

    @Override
    public StepExecution start(StepContext context) {
        DatadogPipelineAction action = new DatadogPipelineAction(this.collectLogs);
        return new ExecutionImpl(context, action);
    }

    private static class ExecutionImpl extends StepExecution {

        private DatadogPipelineAction action;
        ExecutionImpl(StepContext context, DatadogPipelineAction action) {
            super(context);
            this.action = action;
        }

        private static final long serialVersionUID = 1L;
        private transient TaskListener listener;
        private transient Run<?, ?> run;

        /** {@inheritDoc} */
        @Override
        public boolean start() throws Exception {
            StepContext context = getContext();
            listener = context.get(TaskListener.class);
            listener.getLogger().println("Starting DatadogStep");
            run = context.get(Run.class);
            if(run.getAction(DatadogPipelineAction.class) == null) {
                run.addAction(action);
            } else {
                listener.getLogger().println("You already defined a datadog step");
            }

            return false;
        }

        /** {@inheritDoc} */
        @Override
        public void stop(@Nonnull Throwable cause) throws Exception {
            StepContext context = getContext();
            context.get(TaskListener.class).getLogger().println("Stop DatadogStep");
            context.get(TaskListener.class).getLogger().println(cause.getMessage());

        }
    }

    @Extension(dynamicLoadable = YesNoMaybe.YES, optional = true)
    public static class DescriptorImpl extends StepDescriptor {

        /** {@inheritDoc} */
        @Override
        public String getDisplayName() {
            return "DatadogOptions";
        }

        /** {@inheritDoc} */
        @Override
        public String getFunctionName() {
            return "datadog";
        }

        /** {@inheritDoc} */
        @Override
        public boolean takesImplicitBlockArgument() {
            return true;
        }


        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(TaskListener.class);
        }

    }

}