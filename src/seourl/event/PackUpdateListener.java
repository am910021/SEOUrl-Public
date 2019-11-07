/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seourl.event;

import java.util.EventListener;

/**
 *
 * @author Yuri
 */
public interface PackUpdateListener extends EventListener {
     public void doorEvent(PackUpdateEvent event);
}
