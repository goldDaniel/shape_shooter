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
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PerformanceCounter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author wrksttn
 */
public class PhysicsGrid
{
    private static float abs(float a) { return a < 0 ? -a : a; }

    private static float lerp(float a, float b, float t)
    {
        return a + (b-a)*t;
    }

    //these are here for easy tweaking//////////////////////
    final float STIFFNESS = 3.25f;
    final float DAMPING = 2.25f;
    final float INVERSE_MASS = 1f/0.025f;
    ////////////////////////////////////////////////////////


    //can play with the shader easily///////////////////////
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
    //can play with the shader easily///////////////////////

    //SPRING///////////////////////////////////////////////////////////////////////////////
    private class Spring
    {
        Point end1;
        Point end2;

        final float TARGET_LENGTH;

        Vector3 dv = new Vector3();

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

                dv.set(end2.velocity);
                dv.sub(end1.velocity);

                dv.scl(delta*4f);

                Vector3 force = displacement.scl(STIFFNESS).sub(dv.scl(DAMPING));

                end2.applyForce(force.x, force.y, force.z);
                end1.applyForce(-force.x, -force.y, -force.z);
            }
        }
    }
    //SPRING///////////////////////////////////////////////////////////////////////////////


    //POINT///////////////////////////////////////////////////////////////////////////////
    private class Point
    {
        //position point is created at and have to spring back to
        Vector3 desiredPosition;
        Vector3 position;

        Vector3 velocity;
        Vector3 acceleration;

        float inverseMass;

        Color desiredColor;
        Color color;



        private Point(Vector3 position, float inverseMass, Color color)
        {
            this.desiredPosition = position.cpy();
            this.position = position.cpy();
            this.inverseMass = inverseMass;
            this.desiredColor = color;
            this.color = desiredColor.cpy(); // we start off at the desired color
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

            applyForce(forceX, forceY, forceZ);


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

            color.lerp(desiredColor, delta * 0.5f);
        }

        private void applyForce(float x, float y, float z)
        {
            acceleration.x += x*inverseMass;
            acceleration.y += y*inverseMass;
            acceleration.z += z*inverseMass;
        }
    }
    //POINT///////////////////////////////////////////////////////////////////////////////


    private Color color;
    private float borderHue;

    private ImmediateModeRenderer20 sh;

    private Vector2 gridDimensions;

    private Array<Spring> springs;
    private Point[][] points;
    
    private int rows;
    private int cols;

    private Array<PointRunnable> runnables;

    private ExecutorService es;

    boolean isUpdating;


    Array<ForceData> forceDataList;

    //USED IN DRAW TO AVOID GARBAGE COLLECTION EACH FRAME
    //creating a normal vector for every point was madness,
    //800MB of allocations after about 20 seconds
    final Vector3 normal = new Vector3();
    final Color lerp = new Color();
    //////////////////////////////////////////////////////


    private class PointRunnable implements Runnable
    {
        Array<Point> points;
        float dt;

        PointRunnable(Array<Point> points)
        {
            this.points = points;
        }

        public void setDelta(float dt) { this.dt = dt; }

        @Override
        public void run()
        {
           for(int i = 0; i < points.size; i++)
           {
               points.get(i).update(dt);
           }
        }
    }

    private class ForceData
    {
        public Vector3 pos;
        public float radius;
        public float force;
        Color color;

        public ForceData(Vector3 pos, float force, float radius, Color color)
        {
            this.pos = pos;
            this.force = force;
            this.radius = radius;
            this.color = color;
        }
    }

    public PhysicsGrid(Vector2 gridDimensions, float spacing)
    {
        this.rows = (int)(gridDimensions.x/spacing);
        this.cols = (int)(gridDimensions.y/spacing);
        this.gridDimensions = gridDimensions;

        sh = new ImmediateModeRenderer20(rows*cols*64, true, true, 0);
        ShaderProgram shader = new ShaderProgram(createVertexShader(), createFragmentShader());

        if(!shader.isCompiled())
        {
            Gdx.app.log("SHADER", shader.getLog());
        }

        forceDataList = new Array<ForceData>();

        color = Color.BLUE.cpy();

        sh.setShader(shader);

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

                float color = 30f + i * 360f + j * 360f;
                float saturation = 0.8f;
                float alpha = 1f;
                Color c = Color.WHITE.cpy().fromHsv(0f, 0f, 0.55f);
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
        buildThreads(runnables, Runtime.getRuntime().availableProcessors());
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

        isUpdating = true;

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

        isUpdating = false;
        for(ForceData f : forceDataList)
        {
            if(f.color == null)
            {
                applyRadialForce(f.pos, f.force, f.radius);
            }
            else
            {
                applyRadialForce(f.pos, f.force, f.radius, f.color);
            }
        }
        forceDataList.clear();

    }

    protected void applyRadialForce(Vector3 pos, float force, float radius)
    {
        if(!isUpdating)
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
                        dir.nor().scl(force * (1f - (dist / radius)));
                        point.applyForce(dir.x, dir.y, dir.z);
                    }
                }
            }
        }
        else
        {
            forceDataList.add(new ForceData(pos, force, radius, null));
        }
    }

    protected void applyRadialForce(Vector3 pos, float force, float radius, Color c)
    {
        if(!isUpdating)
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
                        dir.nor().scl(force*2 * (1f - (dist / radius)));
                        point.applyForce(dir.x, dir.y, dir.z);
                        point.color.set(c);
                    }
                }
            }
        }
        else
        {
            forceDataList.add(new ForceData(pos, force, radius, c));
        }
    }



    public void draw(Matrix4 proj)
    {
        Gdx.gl.glLineWidth(4f);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);


        Color disabled = new Color(0.05f, 0.05f, 0.05f, 0f);

        sh.begin(proj, GL20.GL_LINES);

        //push each points vertex data into mesh for rendering
        for(int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                {
                    float dist;

                    normal.set( points[i][j].position.x - points[i][j].desiredPosition.x,
                                points[i][j].position.y - points[i][j].desiredPosition.y,
                                points[i][j].position.z - points[i][j].desiredPosition.z);
                        dist = normal.len()*3f;
                    lerp.set(   lerp(disabled.r, points[i][j].color.r, dist),
                                lerp(disabled.g, points[i][j].color.g, dist),
                                lerp(disabled.b, points[i][j].color.b, dist),
                                lerp(disabled.a, points[i][j].color.a, dist));
                    normal.nor();
                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp);
                    sh.vertex(
                            points[i][j].position.x,
                            points[i][j].position.y,
                            points[i][j].position.z);

                    normal.set( points[i + 1][j].position.x - points[i + 1][j].desiredPosition.x,
                                points[i + 1][j].position.y - points[i + 1][j].desiredPosition.y,
                                points[i + 1][j].position.z - points[i + 1][j].desiredPosition.z);
                    dist = normal.len()*3f;
                    lerp.set(   lerp(disabled.r, points[i + 1][j].color.r, dist),
                                lerp(disabled.g, points[i + 1][j].color.g, dist),
                                lerp(disabled.b, points[i + 1][j].color.b, dist),
                                lerp(disabled.a, points[i + 1][j].color.a, dist));
                    normal.nor();
                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp);
                    sh.vertex(
                            points[i + 1][j].position.x,
                            points[i + 1][j].position.y,
                            points[i + 1][j].position.z);

                    normal.set( points[i][j].position.x - points[i][j].desiredPosition.x,
                                points[i][j].position.y - points[i][j].desiredPosition.y,
                                points[i][j].position.z - points[i][j].desiredPosition.z);
                    dist = normal.len()*3f;
                    lerp.set(   lerp(disabled.r, points[i][j].color.r, dist),
                                lerp(disabled.g, points[i][j].color.g, dist),
                                lerp(disabled.b, points[i][j].color.b, dist),
                                lerp(disabled.a, points[i][j].color.a, dist));
                    normal.nor();
                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp);
                    sh.vertex(
                            points[i][j].position.x,
                            points[i][j].position.y,
                            points[i][j].position.z);

                    normal.set( points[i][j + 1].position.x - points[i][j + 1].desiredPosition.x,
                                points[i][j + 1].position.y - points[i][j + 1].desiredPosition.y,
                                points[i][j + 1].position.z - points[i][j + 1].desiredPosition.z);
                    dist = normal.len()*3f;
                    lerp.set(   lerp(disabled.r, points[i][j + 1].color.r, dist),
                                lerp(disabled.g, points[i][j + 1].color.g, dist),
                                lerp(disabled.b, points[i][j + 1].color.b, dist),
                                lerp(disabled.a, points[i][j + 1].color.a, dist));
                    normal.nor();
                    sh.normal(normal.x, normal.y, normal.z);
                    sh.color(lerp);
                    sh.vertex(
                            points[i][j + 1].position.x,
                            points[i][j + 1].position.y,
                            points[i][j + 1].position.z);

                    //INTERPOLATED LINES============================================================
                    float interpolation = 0.5f;
                    doInterpolatedLines(disabled, i, j, interpolation);
                    //==============================================================================
                }
            }
        }

        sh.end();
        
        sh.begin(proj, GL20.GL_LINES);

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
    }

    private void doInterpolatedLines(Color disabled, int i, int j, float interpolation)
    {
        float dist;
        //note: fuck I miss operator overloading
        normal.set( (points[i][j].position.x - points[i][j].desiredPosition.x) +
                        (points[i + 1][j].position.x - points[i + 1][j].desiredPosition.x),
                (points[i][j].position.y - points[i][j].desiredPosition.y) +
                        (points[i + 1][j].position.y - points[i + 1][j].desiredPosition.y),
                (points[i][j].position.z     - points[i][j].desiredPosition.z) +
                        (points[i + 1][j].position.z - points[i + 1][j].desiredPosition.z));
        normal.scl(1f/2f);
        dist = normal.len()*3f;
        lerp.set(
                lerp(disabled.r, lerp(points[i][j].color.r, points[i + 1][j].color.r, interpolation), dist),
                lerp(disabled.g, lerp(points[i][j].color.g, points[i + 1][j].color.g, interpolation), dist),
                lerp(disabled.b, lerp(points[i][j].color.b, points[i + 1][j].color.b, interpolation), dist),
                lerp(disabled.a, lerp(points[i][j].color.a, points[i + 1][j].color.a, interpolation), dist));
        normal.nor();
        sh.normal(normal.x, normal.y, normal.z);
        sh.color(lerp);
        sh.vertex(
                lerp(points[i][j].position.x, points[i + 1][j].position.x, interpolation),
                lerp(points[i][j].position.y,  points[i + 1][j].position.y, interpolation),
                lerp(points[i][j].position.z,  points[i + 1][j].position.z, interpolation));

        normal.set( (points[i][j + 1].position.x - points[i][j + 1].desiredPosition.x) +
                            (points[i + 1][j + 1].position.x - points[i + 1][j + 1].desiredPosition.x),
                    (points[i][j + 1].position.y - points[i][j + 1].desiredPosition.y) +
                            (points[i + 1][j + 1].position.y - points[i + 1][j + 1].desiredPosition.y),
                    (points[i][j + 1].position.z     - points[i][j + 1].desiredPosition.z) +
                            (points[i + 1][j + 1].position.z - points[i + 1][j + 1].desiredPosition.z));
        normal.scl(1f/2f);
        dist = normal.len()*3f;
        lerp.set(
                lerp(disabled.r, lerp(points[i][j + 1].color.r, points[i + 1][j + 1].color.r, interpolation), dist),
                lerp(disabled.g, lerp(points[i][j + 1].color.g, points[i + 1][j + 1].color.g, interpolation), dist),
                lerp(disabled.b, lerp(points[i][j + 1].color.b, points[i + 1][j + 1].color.b, interpolation), dist),
                lerp(disabled.a, lerp(points[i][j + 1].color.a, points[i + 1][j + 1].color.a, interpolation), dist));
        normal.nor();
        sh.normal(normal.x, normal.y, normal.z);
        sh.color(lerp);
        sh.vertex(
                lerp(points[i][j + 1].position.x, points[i + 1][j + 1].position.x, interpolation),
                lerp(points[i][j + 1].position.y, points[i + 1][j + 1].position.y, interpolation),
                lerp(points[i][j + 1].position.z, points[i + 1][j + 1].position.z, interpolation));


        normal.set( (points[i][j].position.x - points[i][j].desiredPosition.x) +
                            (points[i][j + 1].position.x - points[i][j + 1].desiredPosition.x),
                    (points[i][j].position.y - points[i][j].desiredPosition.y) +
                            (points[i][j + 1].position.y - points[i][j + 1].desiredPosition.y),
                    (points[i][j].position.z     - points[i][j].desiredPosition.z) +
                            (points[i][j + 1].position.z - points[i][j + 1].desiredPosition.z));
        normal.scl(1f/2f);
        dist = normal.len()*3f;
        lerp.set(
                lerp(disabled.r, lerp(points[i][j].color.r, points[i][j + 1].color.r, interpolation), dist),
                lerp(disabled.g, lerp(points[i][j].color.g, points[i][j + 1].color.g, interpolation), dist),
                lerp(disabled.b, lerp(points[i][j].color.b, points[i][j + 1].color.b, interpolation), dist),
                lerp(disabled.a, lerp(points[i][j].color.a, points[i][j + 1].color.a, interpolation), dist));
        normal.nor();
        sh.normal(normal.x, normal.y, normal.z);
        sh.color(lerp);
        sh.vertex(
                lerp(points[i][j].position.x, points[i][j + 1].position.x, interpolation),
                lerp(points[i][j].position.y, points[i][j + 1].position.y, interpolation),
                lerp(points[i][j].position.z, points[i][j + 1].position.z, interpolation));


        normal.set( (points[i + 1][j].position.x - points[i + 1][j].desiredPosition.x) +
                            (points[i + 1][j + 1].position.x - points[i + 1][j + 1].desiredPosition.x),
                    (points[i + 1][j].position.y - points[i + 1][j].desiredPosition.y) +
                            (points[i + 1][j + 1].position.y - points[i + 1][j + 1].desiredPosition.y),
                    (points[i + 1][j].position.z     - points[i + 1][j].desiredPosition.z) +
                            (points[i + 1][j + 1].position.z - points[i + 1][j + 1].desiredPosition.z));
        normal.scl(1f/2f);
        dist = normal.len()*3f;
        lerp.set(
                lerp(disabled.r, lerp(points[i + 1][j].color.r, points[i + 1][j + 1].color.r, interpolation), dist),
                lerp(disabled.g, lerp(points[i + 1][j].color.g, points[i + 1][j + 1].color.g, interpolation), dist),
                lerp(disabled.b, lerp(points[i + 1][j].color.b, points[i + 1][j + 1].color.b, interpolation), dist),
                lerp(disabled.a, lerp(points[i + 1][j].color.a, points[i + 1][j + 1].color.a, interpolation), dist));
        normal.nor();
        sh.normal(normal.x, normal.y, normal.z);
        sh.color(lerp);
        sh.vertex(
                lerp(points[i + 1][j].position.x, points[i + 1][j + 1].position.x, interpolation),
                lerp(points[i + 1][j].position.y, points[i + 1][j + 1].position.y, interpolation),
                lerp(points[i + 1][j].position.z, points[i + 1][j + 1].position.z, interpolation));

    }


    public void dispose()
    {
        es.shutdown();
    }
}
