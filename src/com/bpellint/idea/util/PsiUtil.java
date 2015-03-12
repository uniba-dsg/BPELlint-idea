package com.bpellint.idea.util;

import com.intellij.psi.PsiFile;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PsiUtil {

    public static Path toPath(PsiFile originalFile) {
        return Paths.get(originalFile.getVirtualFile().getPath());
    }

}
