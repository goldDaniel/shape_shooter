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
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 *
 * @author wrksttn
 */
public class PhysicsGrid
{


    final float STIFFNESS = 4.5f;
    final float DAMPING = 4f;
    final float INVERSE_MASS = 1f/0.0125f;
    
    private class Spring
    {
        Point end1;
        Point end2;
        
        final float TARGET_LENGTH;
        
        public Spring(Point end1, Point end2)
        {
            this.end1 = end1;
            this.end2 = end2;
            
            TARGET_LENGTH = Vector3.dst(
                    end1.position.x, end1.position.y, end1.position.z,
                    end2.position.x, end2.position.y, end2.position.z);
        }
        
        public void update(float delta)
        {
            Vector3 displacement = end1.position.cpy().sub(end2.position);

            float len = displacement.len();

            if(len > TARGET_LENGTH)
            {
                displacement.setLength(len - TARGET_LENGTH);

                Vector3 dv = end2.velocity.cpy().sub(end1.velocity);

                dv.scl(delta*4f);

                Vector3 force = displacement.scl(STIFFNESS).sub(dv.scl(DAMPING));

                end2.applyForce(force.cpy());
                end1.applyForce(force.scl(-1));
            }
        }
    }
    
    private class Point
    {
        //position point is created at and have to spring back to
        Vector3 desiredPosition;
        
        Vector3 position;
        Vector3 velocity;
        Vector3 acceleration;
        
        float inverseMass;
        
        public Point(Vector3 position, float inverseMass)
        {
            this.desiredPosition = position.cpy();
            this.position = position.cpy();
            this.inverseMass = inverseMass;
            velocity = new Vector3();
            acceleration = new Vector3();
        }
        
        public void update(float delta)
        {
            //there is an invisible spring that connects the initial
            //position to where the point currently is, these are the
            //calculations for said spring. Otherwise the grid effect
            //doesn't come back fast enough, also maintains the general shape
            //of the grid
            float stiffnessScale = 1/3f;
            
            // FORCE CALCULATIONS
            float springForceX = -STIFFNESS*stiffnessScale*(position.x - desiredPosition.x);
            float dampingForceX = DAMPING * velocity.x;
            float forceX = springForceX - dampingForceX;
            
            float springForceY = -STIFFNESS*stiffnessScale*(position.y - desiredPosition.y);
            float dampingForceY = DAMPING * velocity.y;
            float forceY = springForceY - dampingForceY;

            float springForceZ = -STIFFNESS*stiffnessScale*(position.z - desiredPosition.z);
            float dampingForceZ = DAMPING * velocity.z;
            float forceZ = springForceZ - dampingForceZ;

            applyForce(new Vector3(forceX, forceY, forceZ));
            
            velocity.x += acceleration.x * delta;
            velocity.y += acceleration.y * delta;
            velocity.z += acceleration.z * delta;
            
            position.x += velocity.x * delta;
            position.y += velocity.y * delta;
            position.z += velocity.z * delta;
            
            //not really accurate but it works
            velocity.scl(DAMPING*delta);
            if(velocity.len2() < MathUtils.FLOAT_ROUNDING_ERROR)
            {
                velocity.x = velocity.y = velocity.z = 0;
            }
        }

        public void applyForce(Vector3 force)
        {
            acceleration.x += force.x*inverseMass;
            acceleration.y += force.y*inverseMass;
            acceleration.z += force.z*inverseMass;
        }
    }

    private Color color;
    private float borderHue;

    private ImmediateModeRenderer20 sh;

    private Vector2 gridDimensions;

    private Array<Spring> springs;
    private Point[][] points;
    
    private int rows;
    private int cols;
    
    public boolean enableInterpolatedLines = false;


    ImmediateModeRenderer20 immediateRenderer;

    public PhysicsGrid(Vector2 gridDimensions, float spacing)
    {
        this.rows = (int)(gridDimensions.x/spacing);
        this.cols = (int)(gridDimensions.y/spacing);
        this.gridDimensions = gridDimensions;
        sh = new ImmediateModeRenderer20(500000, true, true, 0);
        color = Color.MAGENTA.cpy();
        color.a = 1f;

        points = new Point[rows + 1][cols + 1];
        
        springs = new Array<Spring>();

        for(int i = 0; i < points.length; i++)
        {
            for (int j = 0; j < points[i].length; j++)
            {
                float invMass = INVERSE_MASS;
                if (i == 0 || i == points.length - 1 ||
                        j == 0 || j == points[i].length - 1)
                {
                    invMass = 0;
                }

                points[i][j] = new Point(
                        new Vector3(i * gridDimensions.x / rows - gridDimensions.x / 2f,
                                j * gridDimensions.y / cols - gridDimensions.y / 2f,
                                0),
                        invMass);
            }
        }
        
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
        immediateRenderer = new ImmediateModeRenderer20(false, true, 0);
    }
    
    public void update(float delta)
    {
        if(Gdx.input.isKeyJustPressed(Keys.I))
        {
            enableInterpolatedLines = !enableInterpolatedLines;
        }

        borderHue += 30f*delta;
        
        color.fromHsv(borderHue, 1f, 1f);

        if(delta > 1f / 60f)
        {
            float steps = delta / (1f / 60f);
            float increment = 1f / 60f;

            for(int i = 0; i < steps; i++)
            {
                for(Spring s : springs)
                {
                    s.update(increment);
                }
                for (Point[] pointArr : points)
                {
                    for (Point point : pointArr)
                    {
                        point.update(increment);
                    }
                }
            }

        }
        else
        {
            for (Spring s : springs)
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
    }

    public void applyRadialForce(Vector3 pos, float force, float radius)
    {
        for (Point[] pointArr : points)
        {
            for (Point point : pointArr)
            {
                float dist = Vector3.dst(pos.x, pos.y, pos.z,
                                         point.position.x, point.position.y, point.position.z);
                if (dist < radius)
                {
                    Vector3 dir = point.position.cpy().sub(pos);
                    dir.nor().scl(force * (1f - (dist/radius)*(dist/radius)));
                    point.applyForce(dir);
                }
            }
        }
    }

    private float abs(float a)
    {
        return a < 0 ? -a : a;
    }

    boolean fill = true;

    public void draw(SpriteBatch s)
    {
        s.end();

        Gdx.gl.glLineWidth(2f);


        Color disabled = color.cpy();
        Color enabledInital = color.cpy();

        disabled.r /= 32f;
        disabled.g /= 32f;
        disabled.b /= 32f;

        Color enabled;

        if(Gdx.input.isKeyJustPressed(Keys.P))
        {
            fill = !fill;
        }

        if(!fill)
        {
            sh.begin(s.getProjectionMatrix(), GL20.GL_LINES);
        }
        else
        {
            sh.begin(s.getProjectionMatrix(), GL20.GL_TRIANGLES);
        }


        for(int i = 0; i < points.length - 1f; i++)
        {
            for (int j = 0; j < points[i].length - 1f; j++)
            {
                enabled = enabledInital.cpy();
                enabled.fromHsv(((float)i * (float)j / (float)points.length * (float)points[i].length), 1f, 1f);

                enabled.lerp(enabledInital, 0.5f);

                Vector3 normal = points[i][j].position.cpy().sub(points[i][j].desiredPosition);
                float dist = normal.len();
                Color lerp = disabled.cpy().lerp(enabled, dist);


                if(!fill)
                {
                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp.r, lerp.g, lerp.b, lerp.a);
                    sh.vertex(
                            points[i][j].position.x,
                            points[i][j].position.y,
                            points[i][j].position.z);

                    normal = points[i + 1][j].position.cpy().sub(points[i + 1][j].desiredPosition);
                    dist = normal.len();
                    lerp = disabled.cpy().lerp(enabled, dist);

                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp.r, lerp.g, lerp.b, lerp.a);
                    sh.vertex(
                            points[i + 1][j].position.x,
                            points[i + 1][j].position.y,
                            points[i + 1][j].position.z);

                    normal = points[i][j].position.cpy().sub(points[i][j].desiredPosition);
                    dist = normal.len();
                    lerp = disabled.cpy().lerp(enabled, dist);

                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp.r, lerp.g, lerp.b, lerp.a);
                    sh.vertex(
                            points[i][j].position.x,
                            points[i][j].position.y,
                            points[i][j].position.z);

                    normal = points[i][j + 1].position.cpy().sub(points[i][j + 1].desiredPosition);
                    dist = normal.len();
                    lerp = disabled.cpy().lerp(enabled, dist);

                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp.r, lerp.g, lerp.b, lerp.a);
                    sh.vertex(
                            points[i][j + 1].position.x,
                            points[i][j + 1].position.y,
                            points[i][j + 1].position.z);
                }
                else
                {
                    normal = points[i][j].position.cpy().sub(points[i][j].desiredPosition);
                    dist = normal.len();
                    lerp = disabled.cpy().lerp(enabled, dist);

                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp.r, lerp.g, lerp.b, lerp.a);
                    sh.vertex(
                            points[i][j].position.x,
                            points[i][j].position.y,
                            points[i][j].position.z);

                    normal = points[i + 1][j].position.cpy().sub(points[i + 1][j].desiredPosition);
                    dist = normal.len();
                    lerp = disabled.cpy().lerp(enabled, dist);

                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp.r, lerp.g, lerp.b, lerp.a);
                    sh.vertex(
                            points[i + 1][j].position.x,
                            points[i + 1][j].position.y,
                            points[i + 1][j].position.z);

                    normal = points[i][j + 1].position.cpy().sub(points[i][j + 1].desiredPosition);
                    dist = normal.len();
                    lerp = disabled.cpy().lerp(enabled, dist);

                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp.r, lerp.g, lerp.b, lerp.a);
                    sh.vertex(
                            points[i][j + 1].position.x,
                            points[i][j + 1].position.y,
                            points[i][j + 1].position.z);

                    normal = points[i][j + 1].position.cpy().sub(points[i][j + 1].desiredPosition);
                    dist = normal.len();
                    lerp = disabled.cpy().lerp(enabled, dist);

                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp.r, lerp.g, lerp.b, lerp.a);
                    sh.vertex(
                            points[i][j + 1].position.x,
                            points[i][j + 1].position.y,
                            points[i][j + 1].position.z);

                    normal = points[i + 1][j].position.cpy().sub(points[i + 1][j].desiredPosition);
                    dist = normal.len();
                    lerp = disabled.cpy().lerp(enabled, dist);

                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp.r, lerp.g, lerp.b, lerp.a);
                    sh.vertex(
                            points[i + 1][j].position.x,
                            points[i + 1][j].position.y,
                            points[i + 1][j].position.z);

                    normal = points[i + 1][j + 1].position.cpy().sub(points[i + 1][j + 1].desiredPosition);
                    dist = normal.len();
                    lerp = disabled.cpy().lerp(enabled, dist);

                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp.r, lerp.g, lerp.b, lerp.a);
                    sh.vertex(
                            points[i + 1][j + 1].position.x,
                            points[i + 1][j + 1].position.y,
                            points[i + 1][j + 1].position.z);
                }
            }
        }

        sh.end();
        sh.begin(s.getProjectionMatrix(), GL20.GL_LINES);

        sh.color(color.fromHsv(borderHue, 1f, 1f));
        sh.vertex(-gridDimensions.x/2f, gridDimensions.y/2f, 0);
        sh.color(color.fromHsv(borderHue, 1f, 1f));
        sh.vertex(gridDimensions.x/2f, gridDimensions.y/2f, 0);

        sh.color(color.fromHsv(borderHue, 1f, 1f));
        sh.vertex(-gridDimensions.x/2f, -gridDimensions.y/2f, 0);
        sh.color(color.fromHsv(borderHue, 1f, 1f));
        sh.vertex(gridDimensions.x/2f, -gridDimensions.y/2f, 0);

        sh.color(color.fromHsv(borderHue, 1f, 1f));
        sh.vertex(-gridDimensions.x/2f, -gridDimensions.y/2f, 0);
        sh.color(color.fromHsv(borderHue, 1f, 1f));
        sh.vertex(-gridDimensions.x/2f, gridDimensions.y/2f, 0);

        sh.color(color.fromHsv(borderHue, 1f, 1f));
        sh.vertex(gridDimensions.x/2f, -gridDimensions.y/2f, 0);
        sh.color(color.fromHsv(borderHue, 1f, 1f));
        sh.vertex(gridDimensions.x/2f, gridDimensions.y/2f, 0);

        sh.end();

        s.begin();
    }
}
