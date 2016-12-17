/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
package org.openhab.binding.cm11a.handler;

import java.io.IOException;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.cm11a.internal.InvalidAddressException;
import org.openhab.binding.cm11a.internal.X10Interface;

/**
 * This is an abstract base class for the "Thing" handlers (i.e. Cm11aApplianceHandler and Cm11aLampHandler).
 * It is not used the the Bridge handler (Cm11aHandler)
 *
 * @author Bob Raker
 *
 */
public abstract class Cm11aAbstractHandler extends BaseThingHandler {

    /**
     * The House and Unit codes set on the module, i.e. A1, J14
     */
    protected String houseUnitCode;

    /**
     * The X10 function
     */
    protected int x10Function;

    /**
     * The channel ID
     */
    protected ChannelUID channelUID;

    /**
     * The construction
     * 
     * @param thing The "Thing" to be handled
     */
    public Cm11aAbstractHandler(Thing thing) {
        super(thing);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.smarthome.core.thing.binding.ThingHandler#handleCommand(org.eclipse.smarthome.core.thing.ChannelUID,
     * org.eclipse.smarthome.core.types.Command)
     */
    @Override
    public abstract void handleCommand(ChannelUID channelUID, Command command);

    /**
     * Will be called by the X10Interface when it is ready for this X10 device to use the X10 bus.
     * Child classes should override this method with the specific process necessary to update the
     * hardware with the latest data.
     *
     * <p>
     * Warning: This will be called in a different thread. It must be thread safe.
     * </p>
     *
     * <p>
     * Retries in the event of interface problems will be handled by the X10Interface. If a comms
     * problem occurs and the method throws an exception, this device will be rescheduled again later.
     * </p>
     */
    abstract public void updateHardware(X10Interface x10Interface) throws IOException, InvalidAddressException;

}
