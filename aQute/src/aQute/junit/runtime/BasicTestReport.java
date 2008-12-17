package aQute.junit.runtime;

import java.lang.reflect.*;
import java.util.*;

import junit.framework.*;

import org.osgi.framework.*;

class BasicTestReport implements TestListener {
    int            errors;
    boolean        verbose = true;
    private Bundle targetBundle;

    public void begin(Framework fw, Bundle targetBundle, List tests, int realcount) {
        this.targetBundle = targetBundle;
        if (verbose) {
            System.out
                    .println("=====================================================");
        }
    }

    public void addError(Test test, Throwable t) {
        errors++;
        if (verbose) {
            System.out.println(test + " : ");
            t.printStackTrace(System.out);
            System.out.println();
        }
    }

    public void addFailure(Test test, AssertionFailedError t) {
        errors++;
        if (verbose) {
            System.out.println();
            System.out.print(test + " : ");
            t.getMessage();
        }
    }

    public void endTest(Test test) {
        if (verbose) {
            System.out.print("<< " + test + "\r");
        }
    }

    public void startTest(Test test) {
        try {
            Method m = test.getClass().getMethod("setBundleContext",
                    new Class[] { BundleContext.class });
            m.invoke(test, new Object[] { targetBundle.getBundleContext() });
        } catch (Exception e) {
            // Ok, no problem
        }
        if (verbose)
            System.out.println(">> " + test + "\r");
    }

    public void end() {
        if (verbose) {
            System.out
                    .println("-----------------------------------------------------");
            if (errors == 0)
                System.out.println("No errors :-)");
            else if (errors == 1)
                System.out.println("One error :-|");
            else
                System.out.println(errors + " errors :-(");
            System.out.println();
            System.out.println();
        }
    }

}
