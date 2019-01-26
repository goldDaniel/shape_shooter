package com.golddaniel.utils;

import com.badlogic.gdx.math.MathUtils;

public class Noise
{

    private static float[][] generateSmoothNoise(float[][] noise, int octave)
    {
        int w = noise.length;
        int h = noise[0].length;
        float[][] result = new float[w][h];

        int samplePeriod = 1 << octave;
        float sampleFrequency = 1f / samplePeriod;
        for(int i = 0; i < w; i++)
        {
            int sample0i = (i / samplePeriod) * samplePeriod;
            int sample1i = (sample0i + samplePeriod) % w;
            float horzontalBlend = (i - sample0i) * sampleFrequency;

            for(int j = 0; j < h; j++)
            {
                int sample0j = (j / samplePeriod) * samplePeriod;
                int sample1j = (sample0j + samplePeriod) % h;
                float verticalBlend = (j - sample0j) * sampleFrequency;

                float top = MathUtils.lerp(noise[sample0i][sample0j],
                        noise[sample1i][sample0j],
                        horzontalBlend);
                float bottom = MathUtils.lerp(noise[sample0i][sample1j],
                        noise[sample1i][sample1j],
                        horzontalBlend);

                result[i][j] = MathUtils.lerp(top, bottom, verticalBlend);
            }
        }

        return  result;
    }

    public static float[][] generatePerlinNoise(int w, int h, float persistance,
                                                float amplitude, int octaves)
    {
        float[][] whiteNoise = new float[w][h];
        //generate white noise to generate perlin
        for (int i = 0; i < whiteNoise.length; i++)
        {
            for (int j = 0; j < whiteNoise[i].length; j++)
            {
                whiteNoise[i][j] = MathUtils.random(0f, 1f);
            }
        }


        float[][][] smoothNoise = new float[octaves][][];
        for (int i = 0; i < octaves; i++)
        {
            smoothNoise[i] = generateSmoothNoise(whiteNoise, i);
        }

        float[][] perlinNoise = new float[w][h];

        float totalAmplitude = 0f;

        for(int octave = octaves - 1; octave >= 0; octave--)
        {
            amplitude*= persistance;
            totalAmplitude += amplitude;

            for(int i = 0; i < w; i++)
            {
                for(int j = 0; j < h; j++)
                {
                    perlinNoise[i][j] += smoothNoise[octave][i][j] * amplitude;
                }
            }
        }
        for(int i = 0; i < w; i++)
        {
            for(int j = 0; j < h; j++)
            {
                perlinNoise[i][j] /= totalAmplitude;
            }
        }

        return perlinNoise;
    }

}
