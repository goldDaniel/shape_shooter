/*
 * Copyright 2018 .
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.golddaniel.main;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

/**
 *
 * @author wrksttn
 */
public class Messenger
{   
    
    
    public static enum EVENT
    {
        KEY_DOWN,
        KEY_UP,
        
        CONTROLLER_CONNECTED,
        CONTROLLER_DISCONNECTED,
        
        GAME_UNPAUSE,
        GAME_PAUSE,
        
        PLAYER_FIRE,
        
        BOUNCER_DEAD,
        TRACKER_DEAD,
    }
    
    
    static boolean pauseMessaging = true;
    static ArrayMap<EVENT, Array<MessageListener>> listeners = 
            new ArrayMap<EVENT, Array<MessageListener>>();
    
    public static void stopNotifying()
    {
        pauseMessaging = true;
    }
    
    public static void startNotifying()
    {
        pauseMessaging = false;
    }
    
    public static boolean removeListener(EVENT event, MessageListener listener)
    {
        boolean result = false;
        
        if(listeners.containsKey(event))
        {
            if(listeners.get(event).contains(listener, true))
            {
                listeners.get(event).removeValue(listener, true);
                result = true;
            }
        }
        
        return result;
    }
    
    public static boolean addListener(EVENT event, MessageListener listener)
    {
        boolean result = true;
        
        if(listeners.containsKey(event))
        {
            if(listeners.get(event).contains(listener, true))
            {
               result = false; 
            }
            else
            {
                listeners.get(event).add(listener);
            }
        }
        else
        {
            listeners.put(event, new Array<MessageListener>());
            listeners.get(event).add(listener);
        }
        
        return result;
    }
    
    public static void notify(EVENT event)
    {
        if(pauseMessaging) return;
        
        
        if(listeners.containsKey(event))
        {
            for(MessageListener msg : listeners.get(event))
            {
                msg.onNotify(event);
            }
        }
    }
}
