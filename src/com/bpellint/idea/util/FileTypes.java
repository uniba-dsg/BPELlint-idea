package com.bpellint.idea.util;

import java.nio.file.Path;

public class FileTypes {

    public static boolean isBpelFile(Path file) {
        return file.toString().endsWith(".bpel");
    }

    public static boolean isWsdlFile(Path file) {
        return file.toString().endsWith(".wsdl");
    }

}
