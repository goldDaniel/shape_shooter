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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.golddaniel.entities.Entity;

/**
 *
 * @author wrksttn
 */
public class PhysicsGrid extends Entity
{    
    
    private final float STIFFNESS = 12.5f;
    private final float DAMPING = 10f;
    private final float INVERSE_MASS = 1f/0.2f;
    
    private class Spring
    {
        Point end1;
        Point end2;
        
        final float TARGET_LENGTH;
        
        public Spring(Point end1, Point end2)
        {
            this.end1 = end1;
            this.end2 = end2;
            
            TARGET_LENGTH = Vector2.dst(
                    end1.position.x, end1.position.y, 
                    end2.position.x, end2.position.y);
        }
        
        public void update(float delta)
        {
            Vector2 displacement = end1.position.cpy().sub(end2.position);

            float len = displacement.len();

            if(len > TARGET_LENGTH)
            {
                //normalize
                displacement.nor().scl(len - TARGET_LENGTH);

                Vector2 dv = end2.velocity.cpy().sub(end1.velocity);

                Vector2 force = displacement.scl(STIFFNESS).sub(dv.scl(DAMPING));

                end2.applyForce(force.cpy());
                end1.applyForce(force.scl(-1));
            }
        }
    }
    
    private class Point
    {
        Vector2 desiredPosition;
        
        Vector2 position;
        Vector2 velocity;
        Vector2 acceleration;
        
        float inverseMass;
        
        
        public Point(Vector2 position, float inverseMass)
        {
            this.desiredPosition = position.cpy();
            this.position = position.cpy();
            this.inverseMass = inverseMass;
            velocity = new Vector2();
            acceleration = new Vector2();
        }
        
        public void update(float delta)
        {
            // FORCE CALCULATIONS
            float springForceX = -STIFFNESS/2f*(position.x - desiredPosition.x);
            float dampingForceX = DAMPING * velocity.x;
            float forceX = springForceX - dampingForceX;
            
            float springForceY = -STIFFNESS/2f*(position.y - desiredPosition.y);
            float dampingForceY = DAMPING * velocity.y;
            float forceY = springForceY - dampingForceY;

            
            applyForce(new Vector2(forceX, forceY));
            
            velocity.x += acceleration.x * delta;
            velocity.y += acceleration.y * delta;
            
            
            position.x += velocity.x * delta;
            position.y += velocity.y * delta;
            
            velocity.scl(DAMPING*delta);
            if(velocity.len2() < MathUtils.FLOAT_ROUNDING_ERROR)
            {
                velocity.x = velocity.y = 0;
            }
        }
        
        
        public void applyForce(Vector2 force)
        {
            acceleration.x += force.x*inverseMass;
            acceleration.y += force.y*inverseMass;
        }
    }
    
    Color color;
    float borderHue;
    
    ShapeRenderer sh;
    
    Vector2 worldSize;
    Array<Spring> springs;
    Point[][] points;
    
    int rows;
    int cols;
    
    final float lerpValue = 0.035f;
    
    public boolean enableInterpolatedLines = true;
    
    public PhysicsGrid(Vector2 worldSize, int rows, int cols)
    {
        this.rows = rows;
        this.cols = cols;
        
        
        this.worldSize = worldSize;
        
        color = Color.MAGENTA.cpy();
        color.a = 1f;
        
        points = new Point[rows + 1][cols + 1];
        
        springs = new Array<Spring>();
        
        for(int i = 0; i < points.length; i++)
        {
            for (int j = 0; j < points[i].length; j++)
            {
                float mass = INVERSE_MASS;
                if(i == 0 || i == points.length - 1 ||
                   j == 0 || j == points[i].length - 1)
                {
                    mass = 0;
                }
                points[i][j] = new Point(
                    new Vector2(i*worldSize.x/rows, j*worldSize.y/cols), mass);
            }
        }
        
        sh = new ShapeRenderer();
        
        for (int i = 0; i < points.length - 1; i++)
        {
            for (int j = 0; j < points[i].length - 1; j++)
            {
                Spring s1 = new Spring(points[i][j], points[i + 1][j]);
                Spring s2 = new Spring(points[i][j], points[i][j + 1]);
                
                springs.add(s1);
                springs.add(s2);
            }
        }
        
        
        isAlive = true;
    }

    @Override
    public void onNotify(Messenger.EVENT event)
    {
    }

    private float abs(float a)
    {
        return a > 0 ? a : -a;
    }
    
    public void update(float delta)
    {
        borderHue += 15f*delta;
        borderHue %= 360;
        
        color.fromHsv(borderHue, 1f, 1f);
        
        for(Spring s : springs)
        {
            s.update(delta);
        }
        for (Point[] pointArr : points)
        {
            for (Point point : pointArr)
            {
                point.update(delta);
            }
        }
    }
    @Override
    public void update(WorldModel model, float delta)
    {
        if(delta < 0.025f)
            update(delta);
    }

    public void applyRadialForce(Vector2 pos, float force, float radius)
    {
        for (int i = 0; i < points.length; i++)
        {
            for (int j = 0; j < points[i].length; j++)
            {
                float dist = Vector2.dst(
                        pos.x, pos.y,
                        points[i][j].position.x, points[i][j].position.y);
                if(dist < radius)
                {
                    Vector2 dir = points[i][j].position.cpy().sub(pos);
                    dir.nor().scl(force);
                    
                    points[i][j].applyForce(dir.scl(1f - dist/radius));
                }
            }
        }
    }
    
    public void applyDirectionalForce(Vector2 pos, Vector2 force, float radius)
    {
        for (int i = 0; i < points.length; i++)
        {
            for (int j = 0; j < points[i].length; j++)
            {
                float dist = Vector2.dst(pos.x, pos.y,
                        points[i][j].position.x, points[i][j].position.y);
                if(dist < radius)
                {
                    points[i][j].applyForce(force.cpy().scl(1f- dist/radius));
                }
            }
        }
    }
    
    @Override
    public void draw(SpriteBatch s)
    {
        s.end();
     
        Gdx.gl.glLineWidth(1f);
        
        Gdx.gl.glEnable(GL20.GL_BLEND);
        
        
        sh.setProjectionMatrix(s.getProjectionMatrix()); 
        
        color = Color.MAGENTA.cpy();
        
        sh.begin(ShapeRenderer.ShapeType.Line);
        
        for(int i = 0; i < points.length; i++)
        {
            for (int j = 0; j < points[i].length; j++)
            {
                if(i < points.length-1)
                {
                    color.a = 0.5f;
                    sh.setColor(color);
                    sh.line(points[i][j].position, points[i+1][j].position);
                    
                    if(enableInterpolatedLines)
                    {
                        if(j < points[i].length - 1)
                        {
                            color.a = 0.3f;
                            Vector2 mid1 = new Vector2();
                            mid1.x = points[i][j].position.x + points[i+1][j].position.x;
                            mid1.x /= 2;
                            mid1.y = points[i][j].position.y;

                            Vector2 mid2 = new Vector2();
                            mid2.x = points[i][j + 1].position.x + points[i+1][j + 1].position.x;
                            mid2.x /= 2;
                            mid2.y = points[i][j + 1].position.y;

                            sh.setColor(color);
                            sh.line(mid2, mid1);
                        }
                    }
                }
                
                if(j < points[i].length-1)
                {
                    color.a = 0.5f;
                    sh.setColor(color);
                    sh.line(points[i][j].position, points[i][j+1].position); 
                    
                    if(enableInterpolatedLines)
                    {
                        if(i < points.length - 1)
                        {
                            color.a = 0.3f;
                            Vector2 mid1 = new Vector2();
                            mid1.x = points[i][j].position.x;
                            mid1.y = points[i][j].position.y + points[i][j + 1].position.y;
                            mid1.y /= 2;

                            Vector2 mid2 = new Vector2();
                            mid2.x = points[i+ 1][j].position.x;
                            mid2.y = points[i+ 1][j].position.y + points[i + 1][j + 1].position.y;
                            mid2.y /= 2;

                            sh.setColor(color);
                            sh.line(mid2, mid1);
                        }
                    }
                }
            }
        }
        
        sh.setColor(color.fromHsv(borderHue, 1f, 1f));
        
        //BORDER
        Gdx.gl20.glLineWidth(8f);
        sh.line(0, 0, 0, worldSize.y);
        sh.line(0, 0, worldSize.x, 0);
        sh.line(0, worldSize.y, worldSize.x, worldSize.y);
        sh.line(worldSize.x, worldSize.y, worldSize.x, 0);        
        Gdx.gl20.glLineWidth(1f);
        
        
        sh.end();
        
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        s.begin();
    }

    @Override
    public void dispose()
    {
    }
    
    @Override
    public Rectangle getBoundingBox()
    {
        return null;
    }
    
    @Override
    public void kill(WorldModel moidel) {}
    
    public void setColor(Color color)
    {
        this.color = color;
    }
            
}
