package edu.kit.informatik.graphProcessing;

/**
 * This exception is thrown if a method cannot find the given object. (E.g.
 * towns or paths do not exist in given graph).
 * 
 * @author Johanna Thiemich
 * @version 1.0
 *
 */
public class ObjectNotFoundException extends Exception {

    /**
     * automatically generated serivalVersionUID
     */
    private static final long serialVersionUID = 1239488633958261505L;

    /**
     * This method creates a new ObjectNotFoundException
     * 
     * @param pOutput
     *            String containing information about why the exception has been
     *            thrown
     */
    public ObjectNotFoundException(String pOutput) {
        super(pOutput);
    }
}
