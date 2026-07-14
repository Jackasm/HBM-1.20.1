package com.hbm.util;

public class Tuple {

    public record Pair<X, Y>(X key, Y value) {

        public static <X, Y> Pair<X, Y> of(X key, Y value) {
                return new Pair<>(key, value);
            }

        @Override
            public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null || getClass() != obj.getClass()) return false;

                Pair<?, ?> other = (Pair<?, ?>) obj;
                if (key == null) {
                    if (other.key != null) return false;
                } else if (!key.equals(other.key)) return false;

                if (value == null) {
                    return other.value == null;
                } else return value.equals(other.value);
            }
        }


    public static class Triplet<X, Y, Z> {

        private final X x;
        private final Y y;
        private final Z z;

        public Triplet(X x, Y y, Z z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public X getX() {
            return this.x;
        }

        public Y getY() {
            return this.y;
        }

        public Z getZ() {
            return this.z;
        }

        @Override
        public int hashCode() {
            final int prime = 27644437;
            int result = 1;
            result = prime * result + ((x == null) ? 0 : x.hashCode());
            result = prime * result + ((y == null) ? 0 : y.hashCode());
            result = prime * result + ((z == null) ? 0 : z.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Triplet<?, ?, ?> other = (Triplet<?, ?, ?>) obj;

            if (x == null) {
                if (other.x != null) return false;
            } else if (!x.equals(other.x)) return false;

            if (y == null) {
                if (other.y != null) return false;
            } else if (!y.equals(other.y)) return false;

            if (z == null) {
                if (other.z != null) return false;
            } else if (!z.equals(other.z)) return false;

            return true;
        }

        @Override
        public String toString() {
            return "Triplet{" + x + ", " + y + ", " + z + "}";
        }
    }

    public static class Quartet<W, X, Y, Z> {

        private final W w;
        private final X x;
        private final Y y;
        private final Z z;

        public Quartet(W w, X x, Y y, Z z) {
            this.w = w;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public W getW() {
            return this.w;
        }

        public X getX() {
            return this.x;
        }

        public Y getY() {
            return this.y;
        }

        public Z getZ() {
            return this.z;
        }

        @Override
        public int hashCode() {
            final int prime = 27644437;
            int result = 1;
            result = prime * result + ((w == null) ? 0 : w.hashCode());
            result = prime * result + ((x == null) ? 0 : x.hashCode());
            result = prime * result + ((y == null) ? 0 : y.hashCode());
            result = prime * result + ((z == null) ? 0 : z.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Quartet<?, ?, ?, ?> other = (Quartet<?, ?, ?, ?>) obj;

            if (w == null) {
                if (other.w != null) return false;
            } else if (!w.equals(other.w)) return false;

            if (x == null) {
                if (other.x != null) return false;
            } else if (!x.equals(other.x)) return false;

            if (y == null) {
                if (other.y != null) return false;
            } else if (!y.equals(other.y)) return false;

            if (z == null) {
                if (other.z != null) return false;
            } else if (!z.equals(other.z)) return false;

            return true;
        }

        @Override
        public String toString() {
            return "Quartet{" + w + ", " + x + ", " + y + ", " + z + "}";
        }
    }
    // ... остальные классы Quintet)
}