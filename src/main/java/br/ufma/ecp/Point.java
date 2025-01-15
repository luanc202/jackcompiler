package br.ufma.ecp;

public class Point {
    private int x, y;
    private static int pointCount = 0;

    // Construtor
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        pointCount++;
    }

    // Métodos getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static int getPointCount() {
        return pointCount;
    }

    // Método de distância entre dois pontos
    public double distance(Point other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }


    public Point plus(Point other) {
        return new Point(this.x + other.x, this.y + other.y);
    }
    
    public void print() {
        System.out.println(this.toString());
    }

    public class PointFactory {
        public static Point createPoint(int x, int y) {
            return new Point(x, y);
        }
    }
    
}
