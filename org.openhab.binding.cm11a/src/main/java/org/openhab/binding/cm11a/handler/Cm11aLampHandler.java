/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cm11a.handler;

import java.io.IOException;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.IncreaseDecreaseType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.cm11a.internal.InvalidAddressException;
import org.openhab.binding.cm11a.internal.X10Interface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for Lamp modules. These modules support ON, OFF and brightness level states
 *
 * @author Bob
 *
 */
public class Cm11aLampHandler extends Cm11aAbstractHandler {

    private Logger logger = LoggerFactory.getLogger(Cm11aBridgeHandler.class);

    // Light levels (0 - 22)
    protected int currentLevel = -1;
    protected int desiredLevel = 22; // Arbitrary start value. Should be overwritten on first update.
    protected static int DIM_LEVELS = 22;

    /**
     * Constructor for the Thing
     * 
     * @param thing
     */
    public Cm11aLampHandler(Thing thing) {
        super(thing);

        Configuration config = thing.getConfiguration();
        if (config != null) {
            houseUnitCode = (String) config.get("HouseUnitCode");
            logger.debug("**** Cm11aSwitchHandler houseUnitCode = " + houseUnitCode);
        }
    }

    // @Override
    // public void initialize() {
    // logger.debug("**** Cm11aSwitchHandler initialize *** ");
    // }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("**** Cm11aSwitchHandler handleCommand command = " + command.toString() + ", channelUID = "
                + channelUID.getAsString());

        x10Function = 0;
        Bridge bridge = getBridge();
        this.channelUID = channelUID;

        // Make sure the bridge handler has been initialized and is online before processing requests for any of the
        // attached devices.
        if (bridge != null) {
            Cm11aBridgeHandler cm11aHandler = (Cm11aBridgeHandler) bridge.getHandler();
            if (cm11aHandler != null && cm11aHandler.getThing().getStatus().equals(ThingStatus.ONLINE)) {
                desiredLevel = -1; // Use this to determine if one of the following if conditions was accessed
                if (OnOffType.ON.equals(command)) {
                    desiredLevel = DIM_LEVELS;
                } else if (OnOffType.OFF.equals(command)) {
                    desiredLevel = 0;
                } else if (command instanceof PercentType) {
                    PercentType perc = (PercentType) command;
                    desiredLevel = Math.round((perc.floatValue() / PercentType.HUNDRED.floatValue()) * DIM_LEVELS);
                } else if (IncreaseDecreaseType.INCREASE.equals(command)) {
                    desiredLevel = Math.min(desiredLevel + 1, DIM_LEVELS);
                } else if (IncreaseDecreaseType.DECREASE.equals(command)) {
                    desiredLevel = Math.max(desiredLevel - 1, 0);
                } else {
                    logger.error("Ignoring unknown command received for device: " + houseUnitCode);
                }

                if (desiredLevel >= 0) {
                    X10Interface x10Interface = cm11aHandler.getX10Interface();
                    x10Interface.scheduleHWUpdate(this);
                } else {
                    logger.info(
                            "Received invalid command for switch " + houseUnitCode + " command: " + command.toString());
                }
            } else {
                logger.error("Attenpted to change switch " + houseUnitCode + " cm11a is not online");
            }
        } else {
            logger.error(
                    "Attenpted to change switch " + houseUnitCode + " but the cm11 module has not bee loaded yet.");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openhab.binding.cm11a.handler.Cm11aAbstractHandler#updateHardware(org.openhab.binding.cm11a.internal.
     * X10Interface)
     */
    @Override
    public void updateHardware(X10Interface x10Interface) throws IOException, InvalidAddressException {

        if (desiredLevel != currentLevel) {
            try {
                boolean x10Status;
                if (desiredLevel > 0) {
                    // First we need to get the light on
                    x10Status = x10Interface.sendFunction(houseUnitCode, X10Interface.FUNC_ON);
                } else {
                    // Desired level must be 0, turn it off
                    x10Status = x10Interface.sendFunction(houseUnitCode, X10Interface.FUNC_OFF);
                }

                if (currentLevel == -1 && desiredLevel > 0 && x10Status) {
                    // If we don't know the current level we have to dim to o to get a known starting point
                    x10Status = x10Interface.sendFunction(houseUnitCode, X10Interface.FUNC_DIM, DIM_LEVELS);
                    currentLevel = 0;
                }

                int dimChange = desiredLevel - currentLevel;
                if (desiredLevel >= DIM_LEVELS) {
                    dimChange = DIM_LEVELS;
                } else if (desiredLevel <= 0) {
                    dimChange = 0 - DIM_LEVELS;
                }

                if (dimChange > 0 && x10Status) {
                    x10Status = x10Interface.sendFunction(houseUnitCode, X10Interface.FUNC_BRIGHT, dimChange);
                } else if (dimChange < 0) {
                    x10Status = x10Interface.sendFunction(houseUnitCode, X10Interface.FUNC_DIM, Math.abs(dimChange));
                }

                // Now the hardware should have been updated. If successful update the status
                if (x10Status) {
                    // Hardware update was successful so update OpenHAB
                    PercentType setTo = new PercentType(
                            Math.round((PercentType.HUNDRED.floatValue() / DIM_LEVELS) * desiredLevel));
                    updateState(channelUID, setTo);
                    currentLevel = desiredLevel;
                } else {
                    // Hardware update failed, log
                    logger.error("cm11a failed to update device: " + houseUnitCode);
                }
            } catch (InvalidAddressException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
