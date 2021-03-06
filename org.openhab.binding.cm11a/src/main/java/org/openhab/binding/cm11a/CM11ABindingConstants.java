/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cm11a;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link CM11ABinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author bob raker - Initial contribution
 */
/**
 * @author Bob
 *
 */
public class CM11ABindingConstants {

    public static final String BINDING_ID = "cm11a";

    /**
     * Bridge Type UIDs
     */
    public final static ThingTypeUID THING_TYPE_CM11A = new ThingTypeUID(BINDING_ID, "cm11a");

    /**
     * List of all Thing Type UIDs
     */
    public final static ThingTypeUID THING_TYPE_DIMMER = new ThingTypeUID(BINDING_ID, "dimmer");
    public final static ThingTypeUID THING_TYPE_SWITCH = new ThingTypeUID(BINDING_ID, "switch");

    /**
     * List of all Channel ids
     */
    public final static String CHANNEL_LIGHTLEVEL = "lightlevel";
    public final static String CHANNEL_SWITCH = "switchstatus";

}
