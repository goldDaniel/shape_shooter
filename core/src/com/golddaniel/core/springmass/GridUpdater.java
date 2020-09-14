package com.golddaniel.core.springmass;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class GridUpdater
{

    private SpringMassGrid grid;

    //Any forces that were applied during the iteration will be held in this list
    //this way nothing wonky happens when iterating
    private Array<ForceData> forceDataList;


    private final Vector3 scratch = new Vector3();
    private boolean isUpdating = false;

    public GridUpdater(SpringMassGrid grid)
    {
        this.grid = grid;

        forceDataList = new Array<ForceData>();
    }

    public SpringMassGrid getGrid()
    {
        return grid;
    }

    public void update()
    {
        isUpdating  = true;
        float delta = 1.f/60.f;

        for (Spring s : grid.springs)
        {
            s.update(delta);
        }

        for(Point[] parr : grid.points)
        {
            for(Point p : parr)
            {
                p.update(delta);
            }
        }
        isUpdating = false;

        //applies any forces that were applied during the iteration
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

    public void applyRadialForce(Vector2 pos, float force, float radius)
    {
        if(!isUpdating)
        {
            //we do a distance check from every point
            //if we are in range, apply the appropriate force
            for (Point[] pointArr : grid.points)
            {
                for (Point point : pointArr)
                {

                    if(point.position.x < pos.x + radius && point.position.x > pos.x - radius &&
                            point.position.y < pos.y + radius && point.position.y > pos.y - radius)
                    {
                        float dist = Vector3.dst(pos.x, pos.y, 0,
                                point.position.x, point.position.y, point.position.z);
                        if (dist < radius)
                        {

                            scratch.x = point.position.x - pos.x;
                            scratch.y = point.position.y - pos.y;
                            scratch.z = point.position.z;


                            scratch.nor().scl(force * (1f - (dist / radius)));
                            point.applyForce(scratch.x, scratch.y, scratch.z);
                        }
                    }

                }
            }
        }
    }

    public void applyRadialForce(Vector2 pos, float force, float radius, Color c)
    {
        if(!isUpdating)
        {
            //we do a distance check from every point
            //if we are in range, apply the appropriate force
            for (Point[] pointArr : grid.points)
            {
                for (Point point : pointArr)
                {

                    if(point.position.x < pos.x + radius && point.position.x > pos.x - radius &&
                            point.position.y < pos.y + radius && point.position.y > pos.y - radius)
                    {
                        float dist = Vector3.dst(pos.x, pos.y, 0,
                                point.position.x, point.position.y, point.position.z);
                        if (dist < radius)
                        {

                            scratch.x = point.position.x - pos.x;
                            scratch.y = point.position.y - pos.y;
                            scratch.z = point.position.z;


                            scratch.nor().scl(force * (1f - (dist / radius)));
                            point.applyForce(scratch.x, scratch.y, scratch.z);

                            point.color.set(c);
                        }
                    }
                }
            }
        }
    }
}
