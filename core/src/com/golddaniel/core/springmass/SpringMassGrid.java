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

package com.golddaniel.core.springmass;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 *
 * @author wrksttn
 */
public class SpringMassGrid
{
    private static float lerp(float a, float b, float t)
    {
        return a + (b-a)*t;
    }

    //these are here for easy tweaking//////////////////////
    protected final float STIFFNESS = 3.25f;
    protected final float DAMPING = 2.25f;
    protected final float INVERSE_MASS = 1f/0.025f;


    protected Vector2 gridDimensions;
    protected Array<Spring> springs;
    protected Point[][] points;

    private int rows;
    private int cols;

    private ImmediateModeRenderer20 sh;
    private Color color;
    private float borderHue;

    //USED IN DRAW TO AVOID GARBAGE COLLECTION EACH FRAME
    //creating a normal vector for every point was madness,
    //800MB of allocations after about 20 seconds
    private final Color lerp = new Color();
    //////////////////////////////////////////////////////





    public SpringMassGrid(Vector2 gridDimensions, float spacing)
    {
        this.rows = (int)(gridDimensions.x/spacing);
        this.cols = (int)(gridDimensions.y/spacing);
        this.gridDimensions = gridDimensions;

        sh = new ImmediateModeRenderer20(rows*cols*7, false, true, 0);
        color = Color.BLUE.cpy();
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

                float alpha = 1f;
                Color c = Color.WHITE.cpy().fromHsv(0f, 0f, 0.55f);
                c.a = alpha;

                points[i][j] = new Point(this,
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
                Spring s1 = new Spring(this, points[i][j], points[i + 1][j]);
                Spring s2 = new Spring(this, points[i][j], points[i][j + 1]);
                
                springs.add(s1);
                springs.add(s2);
            }
        }


    }


    public void updateBorderColor(float delta)
    {
        borderHue += 45f*delta;
        color.fromHsv(borderHue, 1f, 1f);
    }



    public void draw(Matrix4 proj)
    {
        Gdx.gl.glLineWidth(4f);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);


        Color disabled = new Color().fromHsv(borderHue, 0.5f, 0.5f);
        disabled.a = 0f;

        sh.begin(proj, GL20.GL_LINES);

        //push each points vertex data into mesh for rendering
        for(int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                {
                    float dist;
                    final float DIST_SCALE = 5f;

                    dist = points[i][j].position.dst2(points[i][j].desiredPosition) * DIST_SCALE;
                    lerp.set(   lerp(disabled.r, points[i][j].color.r, dist),
                                lerp(disabled.g, points[i][j].color.g, dist),
                                lerp(disabled.b, points[i][j].color.b, dist),
                                lerp(disabled.a, points[i][j].color.a, dist));
                    sh.color(lerp);
                    sh.vertex(
                            points[i][j].position.x,
                            points[i][j].position.y,
                            points[i][j].position.z);

                    dist = points[i + 1][j].position.dst2(points[i + 1][j].desiredPosition) * DIST_SCALE;
                    lerp.set(   lerp(disabled.r, points[i + 1][j].color.r, dist),
                                lerp(disabled.g, points[i + 1][j].color.g, dist),
                                lerp(disabled.b, points[i + 1][j].color.b, dist),
                                lerp(disabled.a, points[i + 1][j].color.a, dist));
                    sh.color(lerp);
                    sh.vertex(
                            points[i + 1][j].position.x,
                            points[i + 1][j].position.y,
                            points[i + 1][j].position.z);

                    dist = points[i][j].position.dst2(points[i][j].desiredPosition) * DIST_SCALE;
                    lerp.set(   lerp(disabled.r, points[i][j].color.r, dist),
                                lerp(disabled.g, points[i][j].color.g, dist),
                                lerp(disabled.b, points[i][j].color.b, dist),
                                lerp(disabled.a, points[i][j].color.a, dist));
                    sh.color(lerp);
                    sh.vertex(
                            points[i][j].position.x,
                            points[i][j].position.y,
                            points[i][j].position.z);

                    dist = points[i][j + 1].position.dst2(points[i][j + 1].desiredPosition) * DIST_SCALE;
                    lerp.set(   lerp(disabled.r, points[i][j + 1].color.r, dist),
                                lerp(disabled.g, points[i][j + 1].color.g, dist),
                                lerp(disabled.b, points[i][j + 1].color.b, dist),
                                lerp(disabled.a, points[i][j + 1].color.a, dist));
                    sh.color(lerp);
                    sh.vertex(
                            points[i][j + 1].position.x,
                            points[i][j + 1].position.y,
                            points[i][j + 1].position.z);

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
}
