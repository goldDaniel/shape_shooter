package com.golddaniel.main;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioSystem
{
    public enum SoundEffect
    {
        ENEMY_DEATH,
        PLAYER_DEATH,
        PICKUP,
        LASER,
        RESPAWN,
    }

    private static Music backgroundMusic;

    private static Sound enemyDeath;
    private static Sound pickup;
    private static Sound laser;
    private static Sound respawn;
    private static Sound playerDeath;


    public static void setMusicVolume(float volume)
    {
        backgroundMusic.setVolume(volume*0.7f);
    }

    public static void startMusic()
    {
        backgroundMusic.play();
    }

    public static void pauseMusic()
    {
        backgroundMusic.pause();
    }

    public static void stopMusic()
    {
        backgroundMusic.stop();
    }

    public static void loadSounds(AssetManager assets)
    {
        enemyDeath = assets.get("sounds/bouncer_death.mp3", Sound.class);
        playerDeath = assets.get("sounds/player_death.wav", Sound.class);
        pickup = assets.get("sounds/pickup.wav", Sound.class);
        laser = assets.get("sounds/laser.wav", Sound.class);
        respawn = assets.get("sounds/respawn.wav", Sound.class);

        backgroundMusic = assets.get("sounds/background.mp3", Music.class);
    }

    public static void playSound(SoundEffect s)
    {
        if(s == SoundEffect.ENEMY_DEATH)
        {
            enemyDeath.play(1f, 1f, 0f);
        }
        else if(s == SoundEffect.PICKUP)
        {
            pickup.play(1f, 2f, 0f);
        }
        else if(s == SoundEffect.LASER)
        {
            laser.play(0.4f, 1.5f, 0f);
        }
        else if(s == SoundEffect.RESPAWN)
        {
            respawn.play(1f, 1.5f, 0f);
        }
        else if(s == SoundEffect.PLAYER_DEATH)
        {
            playerDeath.play(1f, 1.5f, 0f);
        }
    }

    public static void dispose()
    {
        backgroundMusic.dispose();
        enemyDeath.dispose();
        pickup.dispose();
        laser.dispose();
        respawn.dispose();
        playerDeath.dispose();
    }
}
