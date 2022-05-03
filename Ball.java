public class Ball {
    Vector pos;
    Vector vel;
    Vector force;
    double mass;

    public Ball(double x,double y,double m){
        pos=new Vector(x,y);
        vel=new Vector(0,0);
        force=new Vector(0,0);
        this.mass=m;
    }
    public void applyForce(Vector f){

        force.add(f);

    }
    public void calcPhysics(double delta){

        force.div(mass);
        //double delta=GamePanel.DELAY;
        delta/=1000;



        force.mul(delta);
        vel.add(force);
        Vector v_t=new Vector(vel);
        v_t.mul(delta);
        pos.add(v_t);

        force.set(0,0);
    }
}

