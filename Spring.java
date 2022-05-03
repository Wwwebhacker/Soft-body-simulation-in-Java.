public class Spring {
    double k=40;
    double damping=1;

    public Ball a;
    public Ball b;
    private double restlen;

    public Spring(Ball a,Ball b,double restlen){
        this.a=a;
        this.b=b;
        this.restlen =restlen;
    }
    private double damping(){
        Vector v1=Vector.sub(b.pos,a.pos);
        v1.normalize();
        Vector v2=Vector.sub(b.vel,a.vel);
        return Vector.dotProduct(v1,v2)*damping;
    }
    public void applyForce(){
        Vector force=Vector.sub(b.pos,a.pos);
        double delta= force.mag() - restlen;
        double Fs=delta*k;
        double Fd=damping();
        double Ft=Fs+Fd;
        Vector b_a=Vector.sub(b.pos,a.pos);
        Vector a_b=Vector.sub(a.pos,b.pos);
        b_a.normalize();
        a_b.normalize();

        b_a.mul(Ft);
        a_b.mul(Ft);

        a.applyForce(b_a);
        b.applyForce(a_b);
    }
    public double getRestlen() {
        return restlen;
    }
}
