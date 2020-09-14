package com.golddaniel.core.springmass;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Point
{
    SpringMassGrid owner;

    //position point is created at and have to spring back to
    Vector3 desiredPosition;
    Vector3 position;

    Vector3 velocity;
    Vector3 acceleration;

    float inverseMass;

    Color desiredColor;
    Color color;

    protected Point(SpringMassGrid owner, Vector3 position, float inverseMass, Color color)
    {
        this.owner = owner;

        this.desiredPosition = position.cpy();
        this.position = position.cpy();
        this.inverseMass = inverseMass;
        this.desiredColor = color;
        this.color = desiredColor.cpy(); // we start off at the desired color
        velocity = new Vector3();
        acceleration = new Vector3();
    }

    protected void update(float delta)
    {
        //there is an invisible spring that connects the initial
        //position to where the point currently is, these are the
        //calculations for said spring. Otherwise the grid effect
        //doesn't come back fast enough, also maintains the general shape
        //of the grid
        float stiffnessScale = 1f/16f;

        // FORCE CALCULATIONS
        float springForceX = -owner.STIFFNESS*stiffnessScale*(position.x - desiredPosition.x);
        float dampingForceX = owner.DAMPING * velocity.x;
        float forceX = springForceX - dampingForceX;

        float springForceY = -owner.STIFFNESS*stiffnessScale*(position.y - desiredPosition.y);
        float dampingForceY = owner.DAMPING * velocity.y;
        float forceY = springForceY - dampingForceY;

        float springForceZ = -owner.STIFFNESS*stiffnessScale*(position.z - desiredPosition.z);
        float dampingForceZ = owner.DAMPING * velocity.z;
        float forceZ = springForceZ - dampingForceZ;

        applyForce(forceX, forceY, forceZ);


        velocity.x += acceleration.x * delta;
        velocity.y += acceleration.y * delta;
        velocity.z += acceleration.z * delta;

        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
        position.z += velocity.z * delta;
        //only want positive Z, as that is how the alpha color on the grid is determined
        position.z = abs(position.z);

        //not really accurate but it works
        velocity.scl(owner.DAMPING*delta);
        if(velocity.len2() < MathUtils.FLOAT_ROUNDING_ERROR)
        {
            velocity.x = velocity.y = velocity.z = 0;
        }

        color.lerp(desiredColor, delta * 0.5f);
    }

    protected void applyForce(float x, float y, float z)
    {
        acceleration.x += x*inverseMass;
        acceleration.y += y*inverseMass;
        acceleration.z += z*inverseMass;
    }

    private static float abs(float val)
    {
        return val > 0 ? val : -val;
    }
}