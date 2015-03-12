package com.bpellint.idea;

import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

public class XmlFileIterator {

    public static void iterate(XmlFile xml, PsiElementVisitor visitor) {
        iterate(xml.getRootTag(), visitor);
    }

    private static void iterate(XmlTag xml, PsiElementVisitor psiElementVisitor) {
        xml.accept(psiElementVisitor);
        for(XmlTag e : xml.getSubTags()) {
            iterate(e, psiElementVisitor);
        }
        for(XmlAttribute e : xml.getAttributes()  ) {
            e.accept(psiElementVisitor);
        }
    }

}
