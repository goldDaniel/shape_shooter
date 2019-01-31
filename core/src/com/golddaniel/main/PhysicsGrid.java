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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Queue;

import java.awt.MediaTracker;

/**
 *
 * @author wrksttn
 */
public class PhysicsGrid
{


    final float STIFFNESS = 3f;
    final float DAMPING = 5.5f;
    final float INVERSE_MASS = 1f/0.0125f;

    static private String createVertexShader()
    {
        String shader =

        "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
        "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE    + ";\n" +
        "uniform mat4 u_projModelView;\n" +
        "varying vec4 v_col;\n" +
        "void main() {\n" +
        "   gl_Position = u_projModelView * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"+
        "   v_col = " + ShaderProgram.COLOR_ATTRIBUTE    + ";\n" +
        "}\n";

        return shader;
    }

    static private String createFragmentShader()
    {
        String shader =

        "#ifdef GL_ES\n" +
        "precision mediump float;\n" +
        "#endif\n" +
        "varying vec4 v_col;\n" +
        "void main() {\n" +
            "gl_FragColor = v_col;\n" +
        "}\n";

        return shader;
    }

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
            float stiffnessScale = 1/4f;
            
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
            position.z = abs(position.z);

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

    ShaderProgram shader;
    ImmediateModeRenderer20 immediateRenderer;

    boolean fill = false;

    private final Thread thread1;
    private final Thread thread2;
    private final Thread thread3;
    private final Thread thread4;
    private final PointRunnable runnable1;
    private final PointRunnable runnable2;
    private final PointRunnable runnable3;
    private final PointRunnable runnable4;

    class PointRunnable implements Runnable
    {

        Array<Point> points;
        float dt;

        PointRunnable(Array<Point> points)
        {
            this.points = points;
        }

        public void setDelta(float dt)
        {
            this.dt = dt;
        }

        @Override
        public void run()
        {
           for(int i = 0; i < points.size; i++)
            {
                float delta = dt / 4f;
                for(float a = 0; a < dt; a += delta)
                {
                    points.get(i).update(delta);
                }
            }
        }
    }

    public PhysicsGrid(Vector2 gridDimensions, float spacing)
    {
        this.rows = (int)(gridDimensions.x/spacing);
        this.cols = (int)(gridDimensions.y/spacing);
        this.gridDimensions = gridDimensions;

        sh = new ImmediateModeRenderer20(2500000, true, true, 0);
        shader = new ShaderProgram(createVertexShader(), createFragmentShader());

        if(!shader.isCompiled())
        {
            Gdx.app.log("SHADER", shader.getLog());
        }

        sh.setShader(shader);

        color = Color.MAGENTA.cpy();

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

        final Array<Point> points1 = new Array<Point>();
        final Array<Point> points2 = new Array<Point>();
        final Array<Point> points3 = new Array<Point>();
        final Array<Point> points4 = new Array<Point>();

        for(int i = 0; i < points.length/4; i++)
        {
            for(int j = 0; j < points[i].length; j++)
            {
                points1.add(points[i][j]);
            }
        }
        for(int i = points.length/4; i < points.length*2/4; i++)
        {
            for(int j = 0; j < points[i].length; j++)
            {
                points2.add(points[i][j]);
            }
        }
        for(int i = points.length*2/4; i < points.length*3/4; i++)
        {
            for(int j = 0; j < points[i].length; j++)
            {
                points3.add(points[i][j]);
            }
        }
        for(int i = points.length*3/4; i < points.length; i++)
        {
            for(int j = 0; j < points[i].length; j++)
            {
                points4.add(points[i][j]);
            }
        }

        thread1 = new Thread(runnable1 = new PointRunnable(points1));
        thread2 = new Thread(runnable2 = new PointRunnable(points2));
        thread3 = new Thread(runnable3 = new PointRunnable(points3));
        thread4 = new Thread(runnable4 = new PointRunnable(points4));

        immediateRenderer = new ImmediateModeRenderer20(false, true, 0);
    }
    
    public void update(final float delta)
    {
        borderHue += 45f*delta;
        
        color.fromHsv(borderHue, 1f, 1f);

        for (Spring s : springs)
        {
            s.update(delta);
        }

        runnable1.setDelta(delta);
        runnable2.setDelta(delta);
        runnable3.setDelta(delta);
        runnable4.setDelta(delta);

        thread1.run();
        thread2.run();
        thread3.run();
        thread4.run();
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
        Gdx.gl.glLineWidth(4f);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);


        Color disabled = color.cpy();

        disabled.r /= 16f;
        disabled.g /= 16f;
        disabled.b /= 16f;
        disabled.a = 1f;

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

        for(int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                Color enabled = color.cpy();

                float colorSpectrum = 1080f;

                {
                    Vector3 normal;
                    float dist;
                    Color lerp;

                    enabled.fromHsv((float)i / (float)(points.length - 1) * colorSpectrum +
                                    (float)j / (float)(points[i].length - 1) * colorSpectrum,
                            1f, 1f);
                    enabled.a = 1f;
                    normal = points[i][j].position.cpy().sub(points[i][j].desiredPosition);
                    dist = normal.len()*2f;
                    lerp = disabled.cpy().lerp(enabled, dist);

                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp.r, lerp.g, lerp.b, lerp.a);
                    sh.vertex(
                            points[i][j].position.x,
                            points[i][j].position.y,
                            points[i][j].position.z);


                    enabled.fromHsv((float)(i + 1) / (float)(points.length - 1) * colorSpectrum +
                                    (float)j / (float)(points[i].length - 1) * colorSpectrum,
                            1f, 1f);
                    enabled.a = 1f;
                    normal = points[i + 1][j].position.cpy().sub(points[i + 1][j].desiredPosition);
                    dist = normal.len()*2f;
                    lerp = disabled.cpy().lerp(enabled, dist);

                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp.r, lerp.g, lerp.b, lerp.a);
                    sh.vertex(
                            points[i + 1][j].position.x,
                            points[i + 1][j].position.y,
                            points[i + 1][j].position.z);


                    enabled.fromHsv((float)i / (float)(points.length - 1) * colorSpectrum +
                                    (float)(j + 1) / (float)(points[i].length - 1) * colorSpectrum,
                            1f, 1f);
                    enabled.a = 1f;
                    normal = points[i][j + 1].position.cpy().sub(points[i][j + 1].desiredPosition);
                    dist = normal.len()*2f;
                    lerp = disabled.cpy().lerp(enabled, dist);

                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp.r, lerp.g, lerp.b, lerp.a);
                    sh.vertex(
                            points[i][j + 1].position.x,
                            points[i][j + 1].position.y,
                            points[i][j + 1].position.z);


                    enabled.fromHsv((float)i / (float)(points.length - 1) * colorSpectrum +
                                    (float)(j + 1) / (float)(points[i].length - 1) * colorSpectrum,
                            1f, 1f);
                    enabled.a = 1f;
                    normal = points[i][j + 1].position.cpy().sub(points[i][j + 1].desiredPosition);
                    dist = normal.len()*2f;
                    lerp = disabled.cpy().lerp(enabled, dist);

                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp.r, lerp.g, lerp.b, lerp.a);
                    sh.vertex(
                            points[i][j + 1].position.x,
                            points[i][j + 1].position.y,
                            points[i][j + 1].position.z);


                    enabled.fromHsv((float)(i + 1) / (float)(points.length - 1) * colorSpectrum +
                                    (float)j / (float)(points[i].length - 1) * colorSpectrum,
                            1f, 1f);
                    enabled.a = 1f;
                    normal = points[i + 1][j].position.cpy().sub(points[i + 1][j].desiredPosition);
                    dist = normal.len()*2f;
                    lerp = disabled.cpy().lerp(enabled, dist);

                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp.r, lerp.g, lerp.b, lerp.a);
                    sh.vertex(
                            points[i + 1][j].position.x,
                            points[i + 1][j].position.y,
                            points[i + 1][j].position.z);

                    enabled.fromHsv((float)(i + 1) / (float)(points.length - 1) * colorSpectrum +
                                    (float)(j + 1) / (float)(points[i].length - 1) * colorSpectrum,
                            1f, 1f);
                    enabled.a = 1f;
                    normal = points[i + 1][j + 1].position.cpy().sub(points[i + 1][j + 1].desiredPosition);
                    dist = normal.len()*2f;
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

    public void dispose()
    {
        sh.dispose();
    }
}
