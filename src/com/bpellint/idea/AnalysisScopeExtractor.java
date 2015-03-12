package com.bpellint.idea;

import com.intellij.analysis.AnalysisScope;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AnalysisScopeExtractor {

    public static Set<PsiFile> getPsiFiles(AnalysisScope scope) {
        Set<PsiFile> result = new HashSet<>();
        scope.accept(new PsiElementVisitor() {
            @Override
            public void visitFile(PsiFile file) {
                result.add(file);
            }
        });
        return result;
    }

    public static Set<Path> getFiles(AnalysisScope scope) {
        return getPsiFiles(scope).stream().map((f) -> Paths.get(f.getVirtualFile().getPath())).collect(Collectors.toSet());
    }

}
