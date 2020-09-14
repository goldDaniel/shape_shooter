package com.golddaniel.core.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.golddaniel.entities.Player;

public class CameraController
{
    private WorldModel model;

    private PerspectiveCamera cam;

    private Vector3 target;

    public CameraController(WorldModel model)
    {
        cam = new PerspectiveCamera(67, 1, 1);
        this.model = model;

        cam.position.x = 0;
        cam.position.y = 0;
        cam.position.z = 64f;

        cam.lookAt(cam.position.x, cam.position.y, 0f);

        cam.near = 1f;
        cam.far = 5000f;

        target = new Vector3();
    }

    public void update(float dt)
    {
        Player player = model.getPlayer();

        if(player.isAlive())
        {
            target.set(player.position, 5.5f);

            if (player.position.x < -model.WORLD_WIDTH / 2f)
            {
                target.x = -model.WORLD_WIDTH / 2f;
            }
            if (player.position.x > model.WORLD_WIDTH / 2f)
            {
                target.x = model.WORLD_WIDTH / 2f;
            }

            if (player.position.y < -model.WORLD_HEIGHT / 2f)
            {
                target.y = -model.WORLD_HEIGHT / 2f;
            }

            if (player.position.y > model.WORLD_HEIGHT / 2f)
            {
                target.y = model.WORLD_HEIGHT / 2f;
            }
        }
        else
        {
            target.x = 0;
            target.y = 0;
            target.z = 16.5f;
        }

        cam.position.x = MathUtils.lerp(cam.position.x, target.x, 0.05f);
        cam.position.y = MathUtils.lerp(cam.position.y, target.y, 0.05f);
        cam.position.z = MathUtils.lerp(
                cam.position.z,
                target.z,
                dt * 2f);

        cam.lookAt(cam.position.x, cam.position.y, 0f);

        //maintain our rotation around Z axis after lookAt, otherwise
        //we get weird rotation due to floating point error
        cam.up.set(0f, 1f, 0f);

        cam.update();
    }

    public Camera getCamera()
    {
        return cam;
    }
}
