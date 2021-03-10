package edu.kit.informatik.graphProcessing;

/**
 * This exception is thrown if the input *.txt file contains mistakes or invalid syntax.
 * @author Johanna Thiemich
 * @version 1.0
 *
 */
public class IncorrectInputFileException extends Exception {
        
    /**
     * automatically generated serialVersionUID
     */
    private static final long serialVersionUID = 5593148517980301715L;

    /**
     * This method creates a new IncorrectInputFileException.
     * @param pOutput String containing information about why the exception has been thrown
     */
    public IncorrectInputFileException(String pOutput)   {
        super(pOutput);
    }
    
}
