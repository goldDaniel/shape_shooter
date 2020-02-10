package com.golddaniel.utils;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.golddaniel.entities.Entity;
;

public class QuadTree<T extends Entity>
{
    private int MAX_OBJECTS = 5;
    private int MAX_LEVELS = 5;

    private int level;
    private Array<T> objects;
    private Rectangle bounds;
    private QuadTree[] nodes;


    public QuadTree(int level, Rectangle bounds)
    {
        this.level = level;
        objects = new Array<T>();
        this.bounds = bounds;
        nodes = new QuadTree[4];
    }

    public Array<T> retrieve(Array<T> returnObjects, T data) {
        int index = getNodeIndex(data.getBoundingBox());

        if (index != -1 && nodes[0] != null)
        {
            nodes[index].retrieve(returnObjects, data);
        }

        returnObjects.addAll(objects);

        return returnObjects;
    }

    public void clear()
    {
        objects.clear();

        for(int i = 0; i < nodes.length; i++)
        {
            if(nodes[i] != null)
            {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    public void insert(T data)
    {
        Rectangle rect = data.getBoundingBox();

        if (nodes[0] != null)
        {
            int index = getNodeIndex(rect);

            if (index != -1) {
                nodes[index].insert(data);

                return;
            }
        }

        objects.add(data);

        if (objects.size > MAX_OBJECTS && level < MAX_LEVELS)
        {
            if (nodes[0] == null)
            {
                divide();
            }

            int i = 0;
            while (i < objects.size)
            {
                int index = getNodeIndex(rect);
                if (index != -1)
                {
                    T toAdd = objects.removeIndex(i);
                    nodes[index].insert(toAdd);
                }
                else
                    {
                    i++;
                }
            }
        }
    }


    private int getNodeIndex(Rectangle rect)
    {
        int index = -1;

        double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
        double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

        boolean top = (rect.getY() < horizontalMidpoint && rect.getY() + rect.getHeight() < horizontalMidpoint);
        boolean bottom = (rect.getY() > horizontalMidpoint);

        //left
        if(rect.getX() < verticalMidpoint && rect.getX() + rect.getWidth() < verticalMidpoint)
        {
            if(top)
            {
                index = 1;
            }
            else if(bottom)
            {
                index = 2;
            }
        }
        //right
        else if(rect.getX() > verticalMidpoint)
        {
            if(top)
            {
                index = 0;
            }
            else if(top)
            {
                index = 3;
            }
        }

        return index;
    }

    private void divide()
    {
        float w = bounds.getWidth() / 2f;
        float h = bounds.getHeight() / 2f;
        float x = bounds.x;
        float y = bounds.y;

        nodes[0] = new QuadTree(level + 1, new Rectangle(x + w, y, w, h));
        nodes[1] = new QuadTree(level + 1, new Rectangle(x, y, w, h));
        nodes[2] = new QuadTree(level + 1, new Rectangle(x, y + h, w, h));
        nodes[3] = new QuadTree(level + 1, new Rectangle(x + w, y + h, w, h));
    }
}
