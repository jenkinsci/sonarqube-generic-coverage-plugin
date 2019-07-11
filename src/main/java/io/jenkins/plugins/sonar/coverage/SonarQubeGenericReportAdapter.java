package io.jenkins.plugins.sonar.coverage;

import hudson.Extension;
import io.jenkins.plugins.coverage.adapter.CoverageAdapterDescriptor;
import io.jenkins.plugins.coverage.adapter.CoverageReportAdapter;
import io.jenkins.plugins.coverage.adapter.CoverageReportAdapterDescriptor;
import io.jenkins.plugins.coverage.adapter.util.XMLUtils;
import io.jenkins.plugins.coverage.exception.CoverageException;
import io.jenkins.plugins.coverage.targets.CoverageResult;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.w3c.dom.Document;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.xml.transform.TransformerException;
import java.io.File;


public final class SonarQubeGenericReportAdapter extends CoverageReportAdapter {

    @DataBoundConstructor
    public SonarQubeGenericReportAdapter(String path) {
        super(path);
    }

    @Override
    protected Document convert(File source) throws CoverageException {
        try {
            return XMLUtils.getInstance().readXMLtoDocument(source);
        } catch (TransformerException e) {
            e.printStackTrace();
            throw new CoverageException(e);
        }
    }

    @CheckForNull
    @Override
    protected CoverageResult parseToResult(Document document, String reportName) throws CoverageException {
        return new SonarQubeGenericReportParser(reportName).parse(document);
    }


    @Extension
    @Symbol({"sonarGenericCoverageAdapter", "sonarGenericCoverage"})
    public static final class SonarQubeGenericCoverageReportAdapterDescriptor extends CoverageReportAdapterDescriptor<CoverageReportAdapter> {

        public SonarQubeGenericCoverageReportAdapterDescriptor() {
            super(SonarQubeGenericReportAdapter.class);
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.SonarQubeGenericReportAdapter_displayName();
        }
    }

}
