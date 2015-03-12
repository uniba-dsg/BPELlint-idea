package com.bpellint.idea.util;

import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ToXpathUtil {

    public static String toXpath(XmlTag tag) {
        return toXPath(tag, "");
    }

    public static String toXpath(XmlAttribute attribute) {
        return toXPath(attribute.getParent(), String.format("/@%s", attribute.getLocalName()));
    }

    private static String toXPath(XmlTag tag, String xpath) {
        if (tag == null) {
            return "";
        }
        String elementName = tag.getLocalName();
        XmlTag parent = tag.getParentTag();
        if (parent == null) {
            return String.format("/%s%s", elementName, xpath);
        }
        List<XmlTag> xmlTags = Arrays.asList(parent.getSubTags());
        Map<String, List<XmlTag>> result = xmlTags.stream().collect(Collectors.groupingBy(XmlTag::getLocalName));

        List<XmlTag> childrenOfSameTypeAsCurrentElement = result.get(tag.getLocalName());
        if (childrenOfSameTypeAsCurrentElement == null || childrenOfSameTypeAsCurrentElement.size() == 1) {
            return toXPath(parent, String.format("/%s%s", elementName, xpath));
        } else {
            int index = childrenOfSameTypeAsCurrentElement.indexOf(tag);
            return toXPath(parent, String.format("/%s[%d]%s", elementName, index, xpath));
        }
    }

}
