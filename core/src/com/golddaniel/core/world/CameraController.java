package com.golddaniel.core.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.golddaniel.entities.Player;

/**
 * Calculates the details for camera movement
 */
public class CameraController
{
    private WorldModel model;

    private PerspectiveCamera cam;

    private Vector3 targetPosition;

    public CameraController(WorldModel model)
    {
        cam = new PerspectiveCamera(67, 1, 1);
        this.model = model;

        cam.position.x = 0;
        cam.position.y = 0;
        cam.position.z = 64f;

        cam.lookAt(cam.position.x, cam.position.y, 0f);

        cam.near = 1f;
        cam.far = 1000f;

        targetPosition = new Vector3();
    }

    public void update(float dt)
    {
        Player player = model.getPlayer();

        //if we lock the camera within the area bounds here
        if(player.isAlive())
        {
            targetPosition.set(player.position, 5.5f);

            if (player.position.x < -model.WORLD_WIDTH / 2f)
            {
                targetPosition.x = -model.WORLD_WIDTH / 2f;
            }
            if (player.position.x > model.WORLD_WIDTH / 2f)
            {
                targetPosition.x = model.WORLD_WIDTH / 2f;
            }

            if (player.position.y < -model.WORLD_HEIGHT / 2f)
            {
                targetPosition.y = -model.WORLD_HEIGHT / 2f;
            }

            if (player.position.y > model.WORLD_HEIGHT / 2f)
            {
                targetPosition.y = model.WORLD_HEIGHT / 2f;
            }
        }
        else
        {
            //zoom the camera out if the player is not alive
            targetPosition.x = 0;
            targetPosition.y = 0;
            targetPosition.z = 16.5f;
        }

        cam.position.x = MathUtils.lerp(cam.position.x, targetPosition.x, 0.05f);
        cam.position.y = MathUtils.lerp(cam.position.y, targetPosition.y, 0.05f);
        cam.position.z = MathUtils.lerp(
                cam.position.z,
                targetPosition.z,
                dt * 2f);

        cam.lookAt(cam.position.x, cam.position.y, 0f);

        //maintain our rotation around Z axis after lookAt, otherwise we get weird rotation issues
        cam.up.set(0f, 1f, 0f);

        cam.update();
    }

    public Camera getCamera()
    {
        return cam;
    }
}
