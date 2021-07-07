package com.appliedcoding.video8.spring;

import com.appliedcoding.io.CanvasBase;

public class Spring {

    private final Ball a;
    private final Ball b;
    private float k;
    private float restLength;

    public Spring(float k, float restLength, Ball a, Ball b) {
        this.k = k;
        this.restLength = restLength;
        this.a = a;
        this.b = b;
    }

    public void update(float time) {
        applyForces();
        a.update(time);
        b.update(time);
    }

    public void show(CanvasBase canvas) {
        canvas.setColor(45);
        canvas.line(a.getX(), a.getY(), b.getX(), b.getY());

        a.show(canvas);
        b.show(canvas);
    }

    private void applyForces() {
        Vector bPosition = b.getPosition();
        Vector aPosition = a.getPosition();
        Vector toA = bPosition.subtract(aPosition);
        Vector springForce = getForceSpring(toA);

        a.applyForce(springForce.multiply(-1));
        a.applyGravity();

        b.applyForce(springForce);
        b.applyGravity();
    }

    // F = -k * x;
    private Vector getForceSpring(Vector forceApplied) {
        float x = forceApplied.getMagnitude() - restLength;
        Vector result = forceApplied.normalize();
        result = result.multiply(-k);
        result = result.multiply(x);

        return result;
    }
}
