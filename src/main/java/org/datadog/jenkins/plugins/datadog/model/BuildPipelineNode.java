package org.datadog.jenkins.plugins.datadog.model;

import hudson.model.Action;
import hudson.model.Result;
import org.jenkinsci.plugins.workflow.actions.PersistentAction;
import org.jenkinsci.plugins.workflow.graph.FlowNode;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represent a stage of the Jenkins Pipeline.
 */
public class BuildPipelineNode {

    private final BuildStageKey key;
    private final List<BuildPipelineNode> children;

    private FlowNode node;
    private StepData stepData;
    private Long startTime;
    private Long endTime;
    private String result;
    private boolean error;

    public static BuildStageBuilder buildStage(final BuildStageKey key) {
        return new BuildStageBuilder(key);
    }

    public static BuildStageBuilder buildStage(final String stageId, final String stageName) {
        return new BuildStageBuilder(stageId, stageName);
    }

    public static BuildStageKey buildStageKey(final String stageId, final String stageName) {
        return new BuildStageKey(stageId, stageName);
    }

    public BuildPipelineNode(final BuildStageBuilder builder) {
        this.key = builder.key;
        this.children = new ArrayList<>();
        this.node = builder.node;
        this.stepData = builder.stepData;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.result = builder.result;
        this.error = builder.error;
    }

    public void addChild(final BuildPipelineNode child) {
        children.add(child);
    }

    public BuildStageKey getKey() {
        return key;
    }

    public String getId() {
        return key.id;
    }

    public String getName() {
        return key.name;
    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getResult() {
        return result;
    }

    public boolean isError() {
        return error;
    }

    public List<BuildPipelineNode> getChildren() {
        return children;
    }

    public FlowNode getNode() {
        return node;
    }

    public BuildPipelineNode getChild(final BuildStageKey id) {
        if(children.isEmpty()) {
            return null;
        }

        for(final BuildPipelineNode child : children) {
            if(id.equals(child.getKey())){
                return child;
            }
        }

        return null;
    }

    public StepData getStepData() {
        return stepData;
    }

    public void updateData(final BuildPipelineNode stage) {
        this.startTime = stage.startTime;
        this.endTime = stage.endTime;
        this.result = stage.result;
        this.error = stage.error;
        this.node = stage.node;
        this.stepData = stage.stepData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildPipelineNode that = (BuildPipelineNode) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BuildPipelineNode{");
        sb.append("key=").append(key);
        sb.append(", children=").append(children);
        sb.append(", node=").append(node);
        sb.append(", stepData=").append(stepData);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", result='").append(result).append('\'');
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }

    public static class BuildStageBuilder {
        private final BuildStageKey key;
        private FlowNode node;
        private StepData stepData;
        private Long startTime;
        private Long endTime;
        private String result;
        private boolean error;

        public BuildStageBuilder(String stageId, String stageName) {
            this.key = new BuildStageKey(stageId, stageName);
        }

        public BuildStageBuilder(final BuildStageKey key) {
            this.key = key;
        }

        public BuildStageBuilder withStepData(final StepData stepData) {
            this.stepData = stepData;
            return this;
        }

        public BuildStageBuilder withFlowNode(final FlowNode node) {
            this.node = node;
            return this;
        }

        public BuildStageBuilder withStartTime(final Long startTime) {
            this.startTime = startTime;
            return this;
        }

        public BuildStageBuilder withEndTime(final Long endTime) {
            this.endTime = endTime;
            return this;
        }

        public BuildStageBuilder withResult(final String result) {
            this.result = result;
            if(Result.FAILURE.toString().equals(this.result)){
                this.error = true;
            }
            return this;
        }

        public BuildPipelineNode build() {
            return new BuildPipelineNode(this);
        }
    }


    public static class BuildStageKey {
        private final String id;
        private final String name;

        private BuildStageKey(final String stageId, final String stageName) {
            this.id = stageId;
            this.name = stageName;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BuildStageKey that = (BuildStageKey) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BuildStageKey{");
            sb.append("id='").append(id).append('\'');
            sb.append(", name='").append(name).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
