/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.event;

import java.util.EventObject;
import seourl.pack.ex.PackAbstract;

/**
 *
 * @author Yuri
 */
public class PackUpdateEvent extends EventObject {

    private PackAbstract doorState;

    public PackUpdateEvent(Object source, PackAbstract doorState) {
        super(source);
        this.doorState = doorState;
    }

    public void setDoorState(PackAbstract doorState) {
        this.doorState = doorState;
    }

    public PackAbstract getDoorState() {
        return this.doorState;
    }

}
