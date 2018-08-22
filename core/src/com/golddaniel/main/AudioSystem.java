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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ArrayMap;
import gold.daniel.level.Level;

/**
 *
 * @author wrksttn
 */
public class AudioSystem implements MessageListener
{
    
    private static float MUSIC_VOLUME = 0.3f;
    private static float SFX_VOLUME = 1f;

    public void update(Level level)
    {
    }
    
    public static enum SOUND
    {
        LASER_1,
        LASER_2,
        
        BOUNCER_DEATH,
        TRACKER_DEATH,
    }
    
    ArrayMap<SOUND, Sound> soundEffects;
    
    ArrayMap<SOUND, Sound> toPlay;
    
    Music backgroundSong;

    @Override
    public void onNotify(Messenger.EVENT event)
    {
        
        if(event == Messenger.EVENT.PLAYER_FIRE)
        {
            playSound(SOUND.LASER_2);
            backgroundSong.play();
        }
        if(event == Messenger.EVENT.BOUNCER_DEAD)
        {
            playSound(SOUND.BOUNCER_DEATH);
        }
        if(event == Messenger.EVENT.TRACKER_DEAD)
        {
            playSound(SOUND.TRACKER_DEATH);
        }
    }
   
    public AudioSystem()
    {
        Messenger.addListener(Messenger.EVENT.PLAYER_FIRE, (MessageListener)this);
        Messenger.addListener(Messenger.EVENT.GAME_UNPAUSE, (MessageListener)this);
        Messenger.addListener(Messenger.EVENT.GAME_PAUSE, (MessageListener)this);
        Messenger.addListener(Messenger.EVENT.BOUNCER_DEAD, (MessageListener)this);
        Messenger.addListener(Messenger.EVENT.TRACKER_DEAD, (MessageListener)this);        
        
        backgroundSong = Gdx.audio.newMusic(Gdx.files.internal("sounds/backgroundSong.mp3"));
        
        soundEffects = new ArrayMap<SOUND, Sound>();
        toPlay = new ArrayMap<SOUND, Sound>();
        
        soundEffects.put(SOUND.LASER_1, 
                Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_laser1.ogg")));
        soundEffects.put(SOUND.LASER_2, 
                Gdx.audio.newSound(Gdx.files.internal("sounds/sfx_laser2.ogg")));
        soundEffects.put(SOUND.BOUNCER_DEATH, 
                Gdx.audio.newSound(Gdx.files.internal("sounds/bouncer_death.mp3")));
        soundEffects.put(SOUND.TRACKER_DEATH, 
                Gdx.audio.newSound(Gdx.files.internal("sounds/bouncer_death.mp3")));
    }
    
    private void playSound(SOUND sound)
    {
        if(soundEffects.containsKey(sound))
        {
            long id = soundEffects.get(sound).play(SFX_VOLUME);
            
            if(sound == SOUND.TRACKER_DEATH)
                soundEffects.get(sound).setPitch(id, 1.2f);
        }
    }
}
