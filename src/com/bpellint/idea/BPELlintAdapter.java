package com.bpellint.idea;

import api.ValidationException;
import api.ValidationResult;
import api.Violation;
import bpellint.core.BpelLint;
import com.bpellint.idea.util.FileTypes;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class BPELlintAdapter {

    private final Map<Path, List<Violation>> errors = new HashMap<>();
    private final Object lock = new Object();

    public void populate(Set<Path> files) {
        System.out.println("[PREPOPULATING] " + Arrays.toString(files.toArray()));
        synchronized(lock) {
            files.stream().filter(FileTypes::isBpelFile).forEach(this::addViolationsUsingBpelFileAsStartingPoint);
        }
        System.out.println("[LOADED] " + Arrays.toString(errors.keySet().toArray()));
    }

    public List<Violation> getViolations(Path path) {
        //System.out.println("Check for violations for path " + path);
        synchronized (lock) {
            List<Violation> violations = errors.get(path);
            if (violations == null) {
                //System.out.println("NONE found for path " + path);
                return Collections.emptyList();
            }
            return violations;
        }
    }

    private void addViolationsUsingBpelFileAsStartingPoint(Path path) {
        try {
            ValidationResult result = new BpelLint().validate(path);
            if(result.isValid()) {
                // nothing to do
                return;
            }
            System.out.println("Violations found");
            result.getViolations().stream().collect(Collectors.groupingBy((p) -> p.getLocation().getFileName())).forEach(errors::put);

            errors.forEach((k, v) -> System.out.println("" + k + ": " +
                    v.stream().map((vio) -> vio.getConstraint() + " " + vio.getMessage() + " " + vio.getLocation().getXpath().get()).collect(Collectors.toList())));

        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

}
