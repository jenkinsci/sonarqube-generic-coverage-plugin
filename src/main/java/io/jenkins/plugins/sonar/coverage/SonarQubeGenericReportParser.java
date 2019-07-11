package io.jenkins.plugins.sonar.coverage;

import io.jenkins.plugins.coverage.adapter.parser.CoverageParser;
import io.jenkins.plugins.coverage.targets.CoverageElement;
import io.jenkins.plugins.coverage.targets.CoverageResult;
import io.jenkins.plugins.coverage.targets.Ratio;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

public class SonarQubeGenericReportParser extends CoverageParser {
    /**
     * Report name will show in the UI, to differentiate different report.
     *
     * @param reportName name of the report
     */
    public SonarQubeGenericReportParser(String reportName) {
        super(reportName);
    }

    @Override
    protected CoverageResult processElement(Element current, CoverageResult parentResult) {
        CoverageResult result = null;
        switch (current.getLocalName()) {
            case "coverage":
                result = new CoverageResult(CoverageElement.REPORT, null, "SonarQube Generic: " + getReportName());
                break;
            case "file":
                result = new CoverageResult(CoverageElement.get("File"), parentResult, getAttribute(current, "path"));
                result.setRelativeSourcePath(getAttribute(current, "path"));
                break;
            case "lineToCover":
                String lineNumberStr = getAttribute(current, "lineNumber");
                String coveredStr = getAttribute(current, "covered");

                if (!StringUtils.isNumeric(lineNumberStr) || !coveredStr.equals("true") && !coveredStr.equals("false")) {
                    break;
                }

                boolean covered = Boolean.parseBoolean(coveredStr);
                int lineNumber = Integer.parseInt(lineNumberStr);
                int coveredBranches = 0;
                int branchesToCover = 0;

                String branchesToCoverStr = getAttribute(current, "branchesToCover");
                String coveredBranchesStr = getAttribute(current, "coveredBranches");

                if (branchesToCoverStr != null && coveredBranchesStr != null) {
                    if (StringUtils.isNumeric(branchesToCoverStr) && StringUtils.isNumeric(coveredBranchesStr)) {
                        coveredBranches = Integer.parseInt(coveredBranchesStr);
                        branchesToCover = Integer.parseInt(branchesToCoverStr);

                        parentResult.updateCoverage(CoverageElement.CONDITIONAL, Ratio.create(coveredBranches, branchesToCover));
                    }
                }


                int hits = covered ? 1 : 0;
                if (branchesToCover == 0) {
                    parentResult.paint(lineNumber, hits);
                } else {
                    parentResult.paint(lineNumber, hits, coveredBranches, branchesToCover);
                }
                parentResult.updateCoverage(CoverageElement.LINE, Ratio.create(hits, 1));
                break;
            default:
                break;

        }
        return result;
    }
}
