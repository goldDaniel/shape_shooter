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
    ImmediateModeRenderer20 immediateRenderer = new ImmediateModeRenderer20(false, true, 0);

    final float STIFFNESS = 4.5f;
    final float DAMPING = 4f;
    final float INVERSE_MASS = 1f/0.025f;
    
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

    private ShapeRenderer sh;

    private Vector2 gridDimensions;

    private Array<Spring> springs;
    private Point[][] points;
    
    private int rows;
    int cols;
    
    public boolean enableInterpolatedLines = false;


    public PhysicsGrid(Vector2 gridDimensions, float spacing)
    {
        this.rows = (int)(gridDimensions.x/spacing);
        this.cols = (int)(gridDimensions.y/spacing);
        this.gridDimensions = gridDimensions;
        sh = new ShapeRenderer();
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
    }
    
    public void update(float delta)
    {
        if(Gdx.input.isKeyJustPressed(Keys.I))
        {
            enableInterpolatedLines = !enableInterpolatedLines;
        }

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




    public void draw(SpriteBatch s)
    {
        s.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        immediateRenderer.begin(s.getProjectionMatrix(), GL20.GL_TRIANGLES);


        float scale = 12f;
        for(int i = 0; i < points.length - 1; i++)
        {
            for (int j = 0; j < points[i].length - 1; j++)
            {

                immediateRenderer.color(0f, 0.4f, 0.4f, abs(points[i][j].position.z)*scale);
                immediateRenderer.vertex(points[i][j].position.x,
                        points[i][j].position.y,
                        points[i][j].position.z);

                immediateRenderer.color(0.4f, 0f, 0.4f, abs(points[i + 1][j].position.z)*scale);
                immediateRenderer.vertex(points[i + 1][j].position.x,
                        points[i + 1][j].position.y,
                        points[i + 1][j].position.z);

                immediateRenderer.color(0.4f, 0f, 0.4f, abs(points[i][j + 1].position.z)*scale);
                immediateRenderer.vertex(points[i][j + 1].position.x,
                                         points[i][j + 1].position.y,
                                         points[i][j + 1].position.z);

                immediateRenderer.color(0f, 0f, 0.4f, abs(points[i][j + 1].position.z)*scale);
                immediateRenderer.vertex(points[i][j + 1].position.x,
                        points[i][j + 1].position.y,
                        points[i][j + 1].position.z);

                immediateRenderer.color(0f, 0.4f, 0f, abs(points[i][j].position.z)*scale);
                immediateRenderer.vertex(points[i + 1][j].position.x,
                        points[i + 1][j].position.y,
                        points[i + 1][j].position.z);

                immediateRenderer.color(0.4f, 0f, 0f, abs(points[i + 1][j + 1].position.z)*scale);
                immediateRenderer.vertex(points[i + 1][j + 1].position.x,
                        points[i + 1][j + 1].position.y,
                        points[i + 1][j + 1].position.z);
            }
        }

        immediateRenderer.end();

        sh.setProjectionMatrix(s.getProjectionMatrix());
        sh.begin(ShapeRenderer.ShapeType.Line);
        sh.setColor(color.fromHsv(borderHue, 1f, 1f));

        //BORDER
        Gdx.gl20.glLineWidth(4f);
        sh.line(-gridDimensions.x, gridDimensions.y/2f, gridDimensions.x, gridDimensions.y/2f);
        sh.line(-gridDimensions.x, -gridDimensions.y/2f, gridDimensions.x, -gridDimensions.y/2f);

        sh.line(-gridDimensions.x/2f, -gridDimensions.y, -gridDimensions.x/2f, gridDimensions.y);
        sh.line(gridDimensions.x/2f, -gridDimensions.y, gridDimensions.x/2f, gridDimensions.y);


        sh.end();
        Gdx.gl20.glLineWidth(1f);

        s.begin();
    }



/*
    public void draw(SpriteBatch s)
    {
        s.end();
     
        Gdx.gl.glLineWidth(1f);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        
        
        sh.setProjectionMatrix(s.getProjectionMatrix());
        color = Color.TEAL.cpy();
        
        
        sh.begin(ShapeRenderer.ShapeType.Line);
        
        //FOR NON INTERPOLATED LINES
        float regularAlpha = 0.6f;
        //FOR INTERPOLATED LINES
       float interpolatedAlpha = 0.3f;
        
        for(int i = 0; i < points.length - 1; i++)
        {
            for (int j = 0; j < points[i].length - 1; j++)
            {
                color.a = regularAlpha;
                sh.setColor(color);
                sh.line(points[i][j].position, points[i+1][j].position);

                if(enableInterpolatedLines)
                {
                    if(j < points[i].length - 1)
                    {
                        color.a = interpolatedAlpha;
                        Vector3 mid1 = new Vector3();
                        mid1.x = points[i+1][j].position.x + points[i][j].position.x;
                        mid1.x /= 2;
                        mid1.y = points[i][j].position.y;
                        mid1.z = points[i][j].position.z;


                        Vector3 mid2 = new Vector3();
                        mid2.x = points[i][j + 1].position.x + points[i+1][j + 1].position.x;
                        mid2.x /= 2;
                        mid2.y = points[i][j + 1].position.y;
                        mid2.z = points[i][j].position.z;

                        sh.setColor(color);
                        sh.line(mid2, mid1);
                    }
                }


                color.a =  regularAlpha;
                sh.setColor(color);
                sh.line(points[i][j].position, points[i][j+1].position);

                if(enableInterpolatedLines)
                {
                    if(i < points.length - 1)
                    {
                        color.a = interpolatedAlpha;
                        Vector3 mid1 = new Vector3();
                        mid1.x = points[i][j].position.x;
                        mid1.y = points[i][j].position.y + points[i][j + 1].position.y;
                        mid1.y /= 2;
                        mid1.z = points[i][j].position.z;

                        Vector3 mid2 = new Vector3();
                        mid2.x = points[i+ 1][j].position.x;
                        mid2.y = points[i+ 1][j].position.y + points[i + 1][j + 1].position.y;
                        mid2.y /= 2;
                        mid2.z = points[i][j].position.z;

                        sh.setColor(color);
                        sh.line(mid2, mid1);
                    }
                }
            }
        }
        sh.setColor(color.fromHsv(borderHue, 1f, 1f));
        
        //BORDER
        Gdx.gl20.glLineWidth(4f);
        sh.line(-gridDimensions.x, gridDimensions.y/2f, gridDimensions.x, gridDimensions.y/2f);
        sh.line(-gridDimensions.x, -gridDimensions.y/2f, gridDimensions.x, -gridDimensions.y/2f);

        sh.line(-gridDimensions.x/2f, -gridDimensions.y, -gridDimensions.x/2f, gridDimensions.y);
        sh.line(gridDimensions.x/2f, -gridDimensions.y, gridDimensions.x/2f, gridDimensions.y);


        sh.end();
        Gdx.gl20.glLineWidth(1f);

        
        s.begin();
    }
*/
    public void setColor(Color color)
    {
        this.color = color;
    }           
}
