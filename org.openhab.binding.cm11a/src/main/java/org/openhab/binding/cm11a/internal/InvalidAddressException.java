/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cm11a.internal;

/**
 * Exception for invalid X10 House / Unit code
 * 
 * @author Bob
 *
 */
public class InvalidAddressException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -1049253542819790311L;

    public InvalidAddressException(String string) {
        super(string);
    }

}
