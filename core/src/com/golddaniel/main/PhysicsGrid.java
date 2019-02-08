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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author wrksttn
 */
public class PhysicsGrid
{


    final float STIFFNESS = 8f;
    final float DAMPING = 3.5f;
    final float INVERSE_MASS = 1f/0.0125f;

    static private String createVertexShader()
    {
        String shader =

        "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
        "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE    + ";\n" +
        "attribute vec3 " + ShaderProgram.NORMAL_ATTRIBUTE   + ";\n" +
        "uniform mat4 u_projModelView;\n" +
        "\n" +
        "varying vec4 v_col;\n" +
        "varying vec3 barycentric;\n" +
        "void main() {\n" +
        "   gl_Position = u_projModelView * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"+
        "   v_col = " + ShaderProgram.COLOR_ATTRIBUTE    + ";\n" +
        "   barycentric = " + ShaderProgram.NORMAL_ATTRIBUTE    + ";\n" +
        "}\n";

        return shader;
    }

    static private String createFragmentShader()
    {
        String shader =

        "#ifdef GL_ES\n" +
        "precision mediump float;\n" +
        "#endif\n" +
        "#define PI 3.1415926535897932384626433832795\n" +
        "varying vec4 v_col;\n" +
        "varying vec3 barycentric;\n" +
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

        Color color;
        
        public Point(Vector3 position, float inverseMass, Color color)
        {
            this.desiredPosition = position.cpy();
            this.position = position.cpy();
            this.inverseMass = inverseMass;
            this.color = color;
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
            float stiffnessScale = 1f/16f;
            
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

    Array<PointRunnable> runnables;

    ExecutorService es;

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
               points.get(i).update(dt);
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

                float colorSpectrum = 360f;
                float saturation = 0.8f;
                float alpha = 1f;
                Color c = Color.MAGENTA.cpy().fromHsv((float)i / (float)(points.length - 1) * colorSpectrum +
                                (float)j / (float)(points[i].length - 1) * colorSpectrum,
                        saturation, 1f);
                c.a = alpha;

                points[i][j] = new Point(
                        new Vector3(i * gridDimensions.x / rows - gridDimensions.x / 2f,
                                j * gridDimensions.y / cols - gridDimensions.y / 2f,
                                0),
                        invMass,
                        c);
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

        runnables = new Array<PointRunnable>();
        buildThreads(runnables, 4);

        immediateRenderer = new ImmediateModeRenderer20(false, true, 0);
    }

    private void buildThreads(Array<PointRunnable> runnables, int threadCount)
    {
        es = Executors.newFixedThreadPool(threadCount);
        Array<Point>[] pointArrays = new Array[threadCount];

        for(int i = 0; i < pointArrays.length; i++)
        {
            pointArrays[i] = new Array<Point>();

            for(int index = points.length*i/threadCount;
                    index < points.length*(i + 1) / threadCount;
                    index++)
            {
                for(int j = 0; j < points[i].length; j++)
                {
                    pointArrays[i].add(points[index][j]);
                }
            }

            PointRunnable r = new PointRunnable(pointArrays[i]);
            runnables.add(r);
        }
    }

    public void update( float delta)
    {
        borderHue += 45f*delta;
        
        color.fromHsv(borderHue, 1f, 1f);

        delta = 1f/60f;

        for (Spring s : springs)
        {
            s.update(delta);
        }

        for(PointRunnable r : runnables)
        {
            r.setDelta(delta);
            es.execute(r);
        }
        try
        {
            es.awaitTermination(16, TimeUnit.NANOSECONDS);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
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
                    dir.nor().scl(force * (1f - (dist/radius)));
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

        disabled.r /= 8f;
        disabled.g /= 8f;
        disabled.b /= 8f;
        disabled.a = 0.05f;

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
                {
                    Vector3 normal;
                    float dist;
                    Color lerp;


                    normal = points[i][j].position.cpy().sub(points[i][j].desiredPosition);
                    dist = normal.len()*2f;
                    lerp = disabled.cpy().lerp(points[i][j].color, dist);

                    sh.normal(1, 0, 0);
                    sh.color(lerp);
                    sh.vertex(
                            points[i][j].position.x,
                            points[i][j].position.y,
                            points[i][j].position.z);

                    normal = points[i + 1][j].position.cpy().sub(points[i + 1][j].desiredPosition);
                    dist = normal.len()*2f;
                    lerp = disabled.cpy().lerp(points[i + 1][j].color, dist);

                    sh.normal(0, 1, 0);
                    sh.color(lerp);
                    sh.vertex(
                            points[i + 1][j].position.x,
                            points[i + 1][j].position.y,
                            points[i + 1][j].position.z);

                    normal = points[i][j + 1].position.cpy().sub(points[i][j + 1].desiredPosition);
                    dist = normal.len()*2f;
                    lerp = disabled.cpy().lerp(points[i][j + 1].color, dist);

                    sh.normal(0, 0, 1);
                    sh.color(lerp);
                    sh.vertex(
                            points[i][j + 1].position.x,
                            points[i][j + 1].position.y,
                            points[i][j + 1].position.z);

                    normal = points[i][j + 1].position.cpy().sub(points[i][j + 1].desiredPosition);
                    dist = normal.len()*2f;
                    lerp = disabled.cpy().lerp(points[i][j + 1].color, dist);

                    sh.normal(0, 0, 1);
                    sh.color(lerp);
                    sh.vertex(
                            points[i][j + 1].position.x,
                            points[i][j + 1].position.y,
                            points[i][j + 1].position.z);

                    normal = points[i + 1][j].position.cpy().sub(points[i + 1][j].desiredPosition);
                    dist = normal.len()*2f;
                    lerp = disabled.cpy().lerp(points[i + 1][j].color, dist);

                    sh.normal(0, 1, 0);
                    sh.color(lerp);
                    sh.vertex(
                            points[i + 1][j].position.x,
                            points[i + 1][j].position.y,
                            points[i + 1][j].position.z);

                    normal = points[i + 1][j + 1].position.cpy().sub(points[i + 1][j + 1].desiredPosition);
                    dist = normal.len()*2f;
                    lerp = disabled.cpy().lerp(points[i + 1][j + 1].color, dist);

                    sh.normal(1, 0, 0);
                    sh.color(lerp);
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
        es.shutdown();
    }
}
