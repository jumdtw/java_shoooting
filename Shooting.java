public class Shooting{

    public static class Mogura{
        private String name;
        private int birth;
        private int x = 0;

        public void move(){
            x += 3;
        }

        public void PPrint(){
            System.out.println(x);
        }
    }

    public static void main(String[] args){
        Mogura mogura = new Mogura();
        mogura.PPrint();
        mogura.move();
        mogura.PPrint();
    }
}