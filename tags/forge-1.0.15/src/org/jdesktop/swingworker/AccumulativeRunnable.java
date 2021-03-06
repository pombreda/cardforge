/* 
 * $Id: AccumulativeRunnable.java,v 1.2 2006/09/28 20:20:28 idk Exp $
 * 
 * Copyright � 2005 Sun Microsystems, Inc. All rights
 * reserved. Use is subject to license terms.
 */

package org.jdesktop.swingworker;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An abstract class to be used in the cases where we need {@code Runnable}
 * to perform  some actions on an appendable set of data.
 * The set of data might be appended after the {@code Runnable} is
 * sent for the execution. Usually such {@code Runnables} are sent to
 * the EDT.
 * <p/>
 * <p/>
 * Usage example:
 * <p/>
 * <p/>
 * Say we want to implement JLabel.setText(String text) which sends
 * {@code text} string to the JLabel.setTextImpl(String text) on the EDT.
 * In the event JLabel.setText is called rapidly many times off the EDT
 * we will get many updates on the EDT but only the last one is important.
 * (Every next updates overrides the previous one.)
 * We might want to implement this {@code setText} in a way that only
 * the last update is delivered.
 * <p/>
 * Here is how one can do this using {@code AccumulativeRunnable}:
 * <pre>
 * AccumulativeRunnable<String> doSetTextImpl =
 * new  AccumulativeRunnable<String>() {
 *
 * @param <T> the type this {@code Runnable} accumulates
 * @author Igor Kushnirskiy
 * @version $Revision: 1.2 $ $Date: 2006/09/28 20:20:28 $
 */
abstract class AccumulativeRunnable<T> implements Runnable {
    private List<T> arguments = null;

    /**
     * Equivalent to {@code Runnable.run} method with the
     * accumulated arguments to process.
     *
     * @param args accumulated arguments to process.
     * @param T a T object.
     */
    protected abstract void run(List<T> args);

    /**
     * {@inheritDoc}
     * <p/>
     * <p/>
     * This implementation calls {@code run(List<T> args)} method
     * with the list of accumulated arguments.
     */
    public final void run() {
        run(flush());
    }

    /**
     * appends arguments and sends this {@code Runnable} for the
     * execution if needed.
     * <p/>
     * This implementation uses see submit to send this
     * {@code Runnable} for execution.
     *
     * @param args the arguments to accumulate
     */
    public final synchronized void add(T... args) {
        boolean isSubmitted = true;
        if (arguments == null) {
            isSubmitted = false;
            arguments = new ArrayList<T>();
        }
        Collections.addAll(arguments, args);
        if (!isSubmitted) {
            submit();
        }
    }

    /**
     * Sends this {@code Runnable} for the execution
     * <p/>
     * <p/>
     * This method is to be executed only from {@code add} method.
     * <p/>
     * <p/>
     * This implementation uses {@code SwingWorker.invokeLater}.
     */
    protected void submit() {
        SwingUtilities.invokeLater(this);
    }

    /**
     * Returns accumulated arguments and flashes the arguments storage.
     *
     * @return accumulated arguments
     */
    private final synchronized List<T> flush() {
        List<T> list = arguments;
        arguments = null;
        return list;
    }
}

