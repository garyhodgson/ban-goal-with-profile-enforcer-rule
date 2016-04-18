package com.garyhodgson.maven.enforcer.rule;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

public class BanGoalWithProfileRule implements EnforcerRule {

    private final List<String> profiles = Collections.EMPTY_LIST;
    private final List<String> goals = Collections.EMPTY_LIST;
    private String artifactId = null;
    private boolean verbose = false;
    private String message = "";

    public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {

        try {
            MavenProject project = (MavenProject) helper.evaluate("${project}");
            MavenSession session = (MavenSession) helper.evaluate("${session}");

            if (verbose) {
                helper.getLog().info("banned profiles: " + profiles);
                helper.getLog().info("banned goals: " + goals);
                helper.getLog().info("session goals: " + session.getGoals());
                helper.getLog().info("active profiles: " + project.getInjectedProfileIds());
            }

            String projectArtifactId = project.getArtifactId();
            if (artifactId != null && !projectArtifactId.equals(artifactId)) {
                if (verbose) {
                    helper.getLog().info(String.format("Configured artifactId [%s] does not match current project artifactId [%s], skipping.", artifactId, projectArtifactId));
                }
                return;
            }

            String goalMatch = null;
            String profileMatch = null;

            for (String goal : ((List<String>) session.getGoals())) {
                if (goals.contains(goal)) {
                    goalMatch = goal;
                    break;
                }
            }

            for (Map.Entry<String, List<String>> entry : project.getInjectedProfileIds().entrySet()) {
                String source = entry.getKey();
                List<String> profileIds = entry.getValue();
                for (String bannedProfile : profiles) {
                    if (profileIds.contains(bannedProfile)) {
                        profileMatch = String.format("%s (source: %s)", bannedProfile, source);
                        break;
                    }
                }
                if (profileMatch != null){
                    break;
                }
            }

            if (goalMatch != null && profileMatch != null) {
                throw new EnforcerRuleException(String.format("Failing because goal [%s] was called with active profile [%s]. %s", goalMatch, profileMatch, message));
            }

        } catch (ExpressionEvaluationException e) {
            throw new EnforcerRuleException("Unable to lookup an expression " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * If your rule is cacheable, you must return a unique id when parameters or
     * conditions change that would cause the result to be different. Multiple
     * cached results are stored based on their id.
     *
     * The easiest way to do this is to return a hash computed from the values
     * of your parameters.
     *
     * If your rule is not cacheable, then the result here is not important, you
     * may return anything.
     */
    public String getCacheId() {
        return "";
    }

    /**
     * This tells the system if the results are cacheable at all. Keep in mind
     * that during forked builds and other things, a given rule may be executed
     * more than once for the same project. This means that even things that
     * change from project to project may still be cacheable in certain
     * instances.
     */
    public boolean isCacheable() {
        return false;
    }

    /**
     * If the rule is cacheable and the same id is found in the cache, the
     * stored results are passed to this method to allow double checking of the
     * results. Most of the time this can be done by generating unique ids, but
     * sometimes the results of objects returned by the helper need to be
     * queried. You may for example, store certain objects in your rule and then
     * query them later.
     */
    public boolean isResultValid(EnforcerRule arg0) {
        return false;
    }
}
