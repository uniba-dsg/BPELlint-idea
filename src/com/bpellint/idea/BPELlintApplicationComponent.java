package com.bpellint.idea;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

public class BPELlintApplicationComponent extends ApplicationComponent.Adapter implements InspectionToolProvider {

    private static final Class[] INSPECTION_CLASSES = {
            StaticAnalysisInspectionsGlobal.class
    };

    public Class[] getInspectionClasses() {
        return INSPECTION_CLASSES;
    }

}
