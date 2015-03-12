package com.bpellint.idea;

import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInspection.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Set;

/**
 * The inspection that runs globally.
 *
 * A local inspection would not work, as BPELlint validates only BPEL files, but produces violations for WSDL files as well.
 * Because of this, a two step process is required.
 * First, all BPEL files are extracted from the AnalysisScope and analyzed via BPELlint.
 * Second, all XmlFiles are iterated. When an XmlTag corresponds to a Violation, this is shown.
 *
 * @author Simon Harrer
 * @version 1.0
 */
@NonNls
public class StaticAnalysisInspectionsGlobal extends GlobalInspectionTool {

    @Override
    public void initialize(@NotNull GlobalInspectionContext context) {
        System.out.println("INITIALIZE GLOBAL");
        super.initialize(context);
    }

    @Override
    public void runInspection(@NotNull AnalysisScope scope,
                              @NotNull InspectionManager manager,
                              @NotNull GlobalInspectionContext globalContext,
                              @NotNull ProblemDescriptionsProcessor problemDescriptionsProcessor) {

        System.out.println("scope.getFileCount() = " + scope.getFileCount());
        System.out.println("scope.getDisplayName() = " + scope.getDisplayName());

        BPELlintAdapter adapter = new BPELlintAdapter();

        // setup BPELlintAdapter
        Set<Path> files = AnalysisScopeExtractor.getFiles(scope);
        adapter.populate(files);

        // reset before every inspection run

        AnalysisScopeExtractor.getPsiFiles(scope).stream().filter((f) -> f instanceof XmlFile).forEach((f) -> {
            ProblemsHolder holder = new ProblemsHolder(manager, f, true);

            PsiElementVisitor visitor = new XmlFileVisitor(adapter, holder);
            XmlFile xml = (XmlFile) f;

            ApplicationManager.getApplication().runReadAction(() -> XmlFileIterator.iterate(xml, visitor));

            ProblemDescriptor[] resultsArray = holder.getResultsArray();

            problemDescriptionsProcessor.addProblemElement(globalContext.getRefManager().getReference(f), resultsArray);
        });
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return "BPELlint";
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "BPEL static analysis rule violation";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "BPELStaticAnalysisViolation";
    }

}