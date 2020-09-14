package com.golddaniel.core.springmass;

import com.badlogic.gdx.math.Vector3;

class Spring
{
    private SpringMassGrid owner;

    protected Point end1;
    protected Point end2;

    final float TARGET_LENGTH;

    Vector3 dv = new Vector3();

    public Spring(SpringMassGrid owner, Point end1, Point end2)
    {
        this.owner = owner;
        this.end1 = end1;
        this.end2 = end2;

        TARGET_LENGTH = Vector3.dst(
                end1.position.x, end1.position.y, end1.position.z,
                end2.position.x, end2.position.y, end2.position.z);
    }

    public void update(float delta)
    {
        float len2 = end1.position.dst2(end2.position);

        if(len2 > TARGET_LENGTH*TARGET_LENGTH)
        {
            dv.set(end2.velocity);
            dv.sub(end1.velocity);

            dv.scl(delta*4f*owner.DAMPING);



            float forceX  = (end1.position.x - end2.position.x) * owner.STIFFNESS;
            forceX -= dv.x;

            float forceY  = (end1.position.y - end2.position.y) * owner.STIFFNESS;
            forceY -= dv.y;

            float forceZ  = (end1.position.z - end2.position.z) * owner.STIFFNESS;
            forceZ -= dv.z;

            end2.applyForce(forceX, forceY, forceZ);
            end1.applyForce(-forceX, -forceY, -forceZ);
        }
    }
}