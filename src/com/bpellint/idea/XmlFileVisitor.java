package com.bpellint.idea;

import api.Violation;
import com.bpellint.idea.util.FileTypes;
import com.bpellint.idea.util.PsiUtil;
import com.bpellint.idea.util.ToXpathUtil;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.XmlElementVisitor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;

import java.nio.file.Path;
import java.util.List;

public class XmlFileVisitor extends XmlElementVisitor {

    private final BPELlintAdapter adapter;
    private final ProblemsHolder holder;

    public XmlFileVisitor(BPELlintAdapter adapter, ProblemsHolder holder) {
        this.adapter = adapter;
        this.holder = holder;
    }

    @Override
    public void visitFile(PsiFile file) {
        Path path = PsiUtil.toPath(file);
        System.out.println("visiting file " + path);
        if (!(FileTypes.isBpelFile(path) || FileTypes.isWsdlFile(path))) {
            System.out.println("Only work with .bpel or .wsdl files");

            return;
        }

        List<Violation> result = adapter.getViolations(path);

        if (!result.isEmpty()) {
            super.visitFile(file);
        }
    }

    @Override
    public void visitXmlTag(XmlTag tag) {
        super.visitXmlTag(tag);

        String xpath = ToXpathUtil.toXpath(tag);

        PsiFile originalFile = tag.getContainingFile();
        Path filePath = PsiUtil.toPath(originalFile);

        List<Violation> validationResult = adapter.getViolations(filePath);

        if (validationResult == null) {
            //System.out.println("COULD NOT CHECK - no validation result for " + filePath + " and xpath " + xpath);
            return;
        }

        //System.out.println("xpath = " + xpath);

        for (Violation violation : validationResult) {
            String violationXpath = violation.getLocation().getXpath().get();
            if (xpath.equals(violationXpath)) {
                String message = getShownMessage(violation);
                System.out.println("VIOLATION FOUND: " + message);
                createViolation(tag, message);
            }
        }
    }

    private void createViolation(PsiElement tag, String message) {
        holder.registerProblem(holder.getManager().createProblemDescriptor(tag,
                message,
                true,
                LocalQuickFix.EMPTY_ARRAY,
                ProblemHighlightType.ERROR));
    }

    @Override
    public void visitXmlAttribute(XmlAttribute attribute) {
        super.visitXmlAttribute(attribute);

        String xpath = ToXpathUtil.toXpath(attribute);

        PsiFile originalFile = attribute.getContainingFile();
        Path filePath = PsiUtil.toPath(originalFile);

        List<Violation> validationResult = adapter.getViolations(filePath);

        if (validationResult == null) {
            //System.out.println("COULD NOT CHECK - no validation result for " + filePath + " and xpath " + xpath);
            return;
        }

        //System.out.println("xpath = " + xpath);

        for (Violation violation : validationResult) {
            String violationXpath = violation.getLocation().getXpath().get();
            if (xpath.equals(violationXpath)) {
                String message = getShownMessage(violation);
                System.out.println("VIOLATION FOUND: " + message);
                createViolation(attribute, message);
            }
        }
    }

    private String getShownMessage(Violation violation) {
        return String.format("%s %s",
                violation.getConstraint(),
                escapeXmlElements(violation.getMessage()));
    }

    public static String escapeXmlElements(String xml) {
        return xml.replaceAll("&", "&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;");
    }
}
