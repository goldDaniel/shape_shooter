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
package com.golddaniel.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.golddaniel.main.WorldModel;

/**
 *
 * @author wrksttn
 */
public class ParticleSpin extends Particle
{
    
    public ParticleSpin(Vector2 pos, float dir, float lifespan, Color startColor, Color endColor, float speed)
    {
        super(pos, dir, lifespan, startColor, endColor, speed);
    }
    
    @Override
    public void update(WorldModel model, float delta)
    {
        super.update(model, delta);
        angle += MathUtils.random(270f, 360f) * delta;
    }
}
