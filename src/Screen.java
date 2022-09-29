//для работы с изображениями
import javax.imageio.ImageIO;
//для работы с графикой
import javax.swing.*;
//для работы с окнами
import java.awt.*;
//для обработки событий мыши
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class Screen extends JPanel {
    private final int DXY = 60;
    private final int H = 23;
    private String number[] = {"А", "Б", "В", "Г", "В", "Е", "Ж", "З", "И", "К"};
    // Переменная для реализации логики игры
    private Game game;
    private int mX, mY; //коорд мыши
    //Таймер отрисовки и изменения логики игры
    private Timer timer;
    //Изображения, используемые в игре
    private BufferedImage  ranen, boom,killed,paluba;
    private Rectangle2D line4,line3,line2,line1;
    private boolean isSelectP4=false;
    private boolean isSelectP3=false;
    private boolean isSelectP2=false;
    private boolean isSelectP1=false;
    private int p4,p3,p2,p1;
    public boolean vert=true; //направление расстановки
    private JButton checkNapr;
    public static boolean rasstanovka;


    public Screen() {
        addMouseListener(new Mouse());
        addMouseMotionListener(new Mouse());
        setFocusable(true);
        game = new Game();
        setSize(800,520);
        try {
            ranen = ImageIO.read(getClass().getResource("image/ranen.png"));
            boom = ImageIO.read(getClass().getResource("image/boom.png"));
            killed = ImageIO.read(getClass().getResource("image/killed.png"));
            paluba = ImageIO.read(getClass().getResource("image/paluba.png"));
        } catch (IOException e) {e.printStackTrace();}
        //Создаем, настраиваем таймер отрисовки
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        setLayout(null);
        checkNapr=new JButton("Повернуть");
        checkNapr.setBackground(new Color(248, 248, 255));
        checkNapr.setBounds(DXY+24*H,DXY+8*H,7*H,H);
        checkNapr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (vert)vert=false;
                else vert=true;
            }
        });
        add(checkNapr);
        checkNapr.setVisible(false);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2= (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        g.setColor(new Color(248, 248, 255));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setFont(new Font("Times New Roman", 0, H - 5));
        g.setColor(new Color(330099));
        if (rasstanovka) {
            g2.setStroke(new BasicStroke(2));
            if (vert) {
                line4 = new Rectangle2D.Double(DXY + 24 * H, DXY, 4 * H, H);
                line3 = new Rectangle2D.Double(DXY + 24 * H, DXY + 2 * H, 3 * H, H);
                line2 = new Rectangle2D.Double(DXY + 24 * H, DXY + 4 * H, 2 * H, H);
                line1 = new Rectangle2D.Double(DXY + 24 * H, DXY + 6 * H, 1 * H, H);
            } else {
                line4 = new Rectangle2D.Double(DXY + 24 * H, DXY, H, 4 * H);
                line3 = new Rectangle2D.Double(DXY + 26 * H, DXY, H, 3 * H);
                line2 = new Rectangle2D.Double(DXY + 28 * H, DXY, H, 2 * H);
                line1 = new Rectangle2D.Double(DXY + 30 * H, DXY, H, 1 * H);
            }

            if (p4 != 0) ((Graphics2D) g).draw(line4);
            if (p3 != 0) ((Graphics2D) g).draw(line3);
            if (p2 != 0) ((Graphics2D) g).draw(line2);
            if (p1 != 0) ((Graphics2D) g).draw(line1);
            if ((p1+p2+p3+p4)!=0) {
                g.drawString("Расставьте корабли", DXY + 24 * H, DXY-H);
                checkNapr.setVisible(true);
            }
            else {
                checkNapr.setVisible(false);
            }
        }
        //Выведение надписей
        g.drawString("Игрок", DXY + 4 * H, DXY - H);
        g.drawString("Компьютер", DXY + 16 * H, DXY - H);
        g.drawString("Ходов игрока: ", DXY + 24 * H, DXY + 13 * H - (H/4));
        g.drawString(String.valueOf(game.kolHodPlay), DXY + 29 * H, DXY + 13 * H - (H / 4));
        g.drawString("Ходов комьютера: ", DXY + 24 * H, DXY + 14 * H - (H/4));
        g.drawString(String.valueOf(game.kolHodComp), DXY + 30 * H + (H / 2), DXY + 14 * H - (H / 4));

        //Выводим цифры и буквы
        for (int i = 1; i <= 10; i++) {
            //12345678910
            g.drawString(String.valueOf(i), DXY - H, DXY + i * H - (H / 4));
            g.drawString(String.valueOf(i), DXY + 12 * H, DXY + i * H - (H / 4));
            //абвгдежзик
            g.drawString(number[i-1], DXY + (i-1) * H + (H / 4), DXY - 3);
            g.drawString(number[i-1], 13 * H + DXY + (i-1) * H + (H / 4), DXY - 3);
        }

        //отрисовка игрового поля на основании массива
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                //корабли противника
                if (game.poleComp[i][j]!=0) {
                    //Если это палуба раненного корабля, то выводим соотвествующее изображение
                    if ((game.poleComp[i][j] >= 8) && (game.poleComp[i][j] <= 11)) {
                        g.drawImage(ranen, DXY + 13 * H + H * i, DXY + H * j, H, H, null);
                    }
                    else if ((game.poleComp[i][j] >= 15)) {
                        //рисуем палубу убитого корабля
                        g.drawImage(killed, DXY + 13 * H + H * i, DXY + H * j, H, H, null);
                    }
                    else if ((game.poleComp[i][j] >= 5 && game.poleComp[i][j]<8 || game.poleComp[i][j]==-2)) {
                        //если выстрел мимо и это окружение убитого корабля
                        g.drawImage(boom, DXY + 13 * H + H * i, DXY + H * j, H, H, null);
                    }
                    else if (Game.endGame!=0 && (game.poleComp[i][j] >= 1 && game.poleComp[i][j] <= 4)) {
                        //показываем корабли после конца игры
                        g.drawImage(paluba, DXY + 13 * H + H * i, DXY + H * j, H, H, null);
                        g.setColor(new Color(0x8B0000));
                        g.drawRect(DXY + 13 * H + H * i,DXY + H * j,H,H);
                    }
                }

                //корабли игрока
                if (game.polePlayer[i][j]!=0){
                    if ((game.polePlayer[i][j] >= 1) && (game.polePlayer[i][j] <= 4)) {
                        //палуба
                        g.drawImage(paluba, DXY + H * i, DXY + H * j, H, H, null);
                    }else if ((game.polePlayer[i][j] >= 8) && (game.polePlayer[i][j] <= 11)) {
                        //ранен
                        g.drawImage(ranen, DXY + + H * i, DXY + H * j, H, H, null);
                    }else if ((game.polePlayer[i][j] >= 15)) {
                        //убит
                        g.drawImage(killed, DXY + H * i, DXY + H * j, H, H, null);
                    }else if ((game.polePlayer[i][j] >= 5) && game.polePlayer[i][j]<8) {
                        //мимо
                        g.drawImage(boom, DXY +  + H * i, DXY + H * j, H, H, null);
                    }
                }
            }
        }


        //линии
        for (int i = DXY; i <= DXY + 10 * H; i += H) {
            g2.setStroke(new BasicStroke(1));
            g.setColor(new Color(202,202,255));
            g.drawLine(DXY, i, DXY + 10 * H, i); // ----
            g.drawLine(i, DXY, i, DXY + 10 * H);
            g.drawLine(DXY + 13 * H, i, DXY + 23 * H, i); //бот ---
            g.drawLine(i + 13 * H, DXY, i + 13 * H, DXY + 10 * H);

            g2.setStroke(new BasicStroke(2));
            g.setColor(new Color(330099));
            g.drawRect(DXY, DXY, 10 * H, 10 * H);
            g.drawRect(DXY+13*H,DXY,10 * H,10 * H);
        }

        g.setFont(new Font("Times New Roman", 0, H));
        g.setColor(new Color(330099));

        //количество кораблей игрока
        g.drawRect(DXY, DXY + 11 * H, 4 * H, H);
        ((Graphics2D) g).drawString(String.valueOf(1 - game.C4), DXY + 5 * H, DXY + 12 * H - (H / 4));
        g.drawRect(DXY, DXY + 12 * H + 10, 3 * H, H);
        ((Graphics2D) g).drawString(String.valueOf(2 - game.C3), DXY + 4 * H, DXY + 13 * H + 10);
        g.drawRect(DXY, DXY + 13 * H + 20, 2 * H, H);
        ((Graphics2D) g).drawString(String.valueOf(3 - game.C2), DXY + 3 * H, DXY + 14 * H + 20);
        g.drawRect(DXY, DXY + 14 * H + 30, H, H);
        ((Graphics2D) g).drawString(String.valueOf(4 - game.C1), DXY + 2 * H, DXY + 15 * H + 30);

        g.drawRect(DXY + 13 * H, DXY + 11 * H, 4 * H, H);//4 палуб
        ((Graphics2D) g).drawString(String.valueOf(1-game.P4), DXY + 18 * H, DXY + 12 * H-(H/4));
        g.drawRect(DXY + 13 * H, DXY + 12 * H + 10, 3 * H, H);  //3
        ((Graphics2D) g).drawString(String.valueOf(2-game.P3), DXY + 17 * H, DXY + 13 * H+10);
        g.drawRect(DXY + 13 * H, DXY + 13 * H + 20, 2 * H, H);//2
        ((Graphics2D) g).drawString(String.valueOf(3-game.P2), DXY + 16 * H, DXY + 14 * H+20);
        g.drawRect(DXY + 13 * H, DXY + 14 * H + 30, 1 * H, H);
        ((Graphics2D) g).drawString(String.valueOf(4-game.P1), DXY + 15 * H, DXY + 15 * H+30);


        if (Game.endGame==0 && (p1+p2+p3+p4)==0 && rasstanovka || Game.endGame==0 && !rasstanovka){
            g.setFont(new Font("Times New Roman", 0, H - 5));
            if (game.myHod) {
                g.setColor(Color.green);
                g.drawString("Ход игрока", DXY + 24 * H, DXY + 12 * H - (H / 4));
            }
            else {
                g.setColor(Color.red);
                g.drawString("Ходит компьютер", DXY + 24 * H, DXY + 12 * H - (H / 4));
            }
        }if (Game.endGame == 1) {
            timer.stop();

        }if (Game.endGame == 2) {
            timer.stop();
        }
    }

    public void start() {
        rasstanovka = false;
        checkNapr.setVisible(false);
        timer.start();
        game.start();
    }

    public void startRasstanovka(){
        rasstanovka = true;
        timer.start();
        game.start();
        p1 = 4;
        p2 = 3;
        p3 = 2;
        p4 = 1;
    }

    public void startAutoGame(){
        rasstanovka = false;
        checkNapr.setVisible(false);
        timer.start();
    }

    public void exit() {
        System.exit(0);
    }

    public class Mouse implements MouseListener,MouseMotionListener {
        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            // Если сделано одиночное нажатие левой клавишей мыши
            if ((e.getButton() == 1) && (e.getClickCount() == 1)) {
                mX = e.getX();
                mY = e.getY();
                if ((rasstanovka && p1+p2+p3+p4==0) || !rasstanovka
                        && mX > (DXY + 13 * H) && mY > (DXY) && mX < (DXY + 23 * H) && mY < DXY + 10 * H) {
                    //если внутри поля бота и если не конец игры и ход игрока
                    if (game.myHod && Game.endGame ==0 && !game.compHod){
                        //то вычисляем элемент массива:
                        int i=(mX-(DXY+13*H))/H;
                        int j=(mY-DXY)/H;
                        if ((i>=0 && i<=9) && (j>=0 && j<=9)) {
                            // System.out.println("Мы нажали на " + i+ " " +j);
                            if (game.poleComp[i][j] <= 4 && game.poleComp[i][j] >= -1) {
                                //-1 это окружение не убитого корабля
                                game.hodPlayer(game.poleComp, i, j);
                            }
                        }
                    }

                }
            }
            if (rasstanovka){
                if (line4.contains(e.getPoint())){
                    isSelectP4 =true;isSelectP3 =false;isSelectP2 =false;isSelectP1 =false;
                }if (line3.contains(e.getPoint())){
                    isSelectP4 =false;isSelectP3 =true;isSelectP2 =false;isSelectP1 =false;
                }if (line2.contains(e.getPoint())){
                    isSelectP4 =false;isSelectP3 =false;isSelectP2 =true;isSelectP1 =false;
                }if (line1.contains(e.getPoint())){
                    isSelectP4 =false;isSelectP3 =false;isSelectP2 =false;isSelectP1 =true;
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (rasstanovka) {
                mX = e.getX();
                mY = e.getY();
                int i = (mX - (DXY)) / H;
                int j = (mY - DXY) / H;
                if (p4 != 0 && isSelectP4 && mX > (DXY) && mY > (DXY) && mX < (DXY + 10 * H) && mY < DXY + 10 * H) {
                    isSelectP4 = false;
                    //line4 = new Rectangle2D.Double(DXY + 24 * H, DXY, 4 * H, H);
                    if (game.setPaluba(i, j, 4, vert)) {
                        p4--;
                    }

                } else if (p3 != 0 && isSelectP3 && mX > (DXY) && mY > (DXY) && mX < (DXY + 10 * H) && mY < DXY + 10 * H) {
                    isSelectP3 = false;
                    // line3 = new Rectangle2D.Double(DXY + 24 * H, DXY + 2 * H, 3 * H, H);
                    if (game.setPaluba(i, j, 3, vert)) {
                        p3--;
                    }

                } else if (p2 != 0 && isSelectP2 && mX > (DXY) && mY > (DXY) && mX < (DXY + 10 * H) && mY < DXY + 10 * H) {
                    isSelectP2 = false;
                    //line2 = new Rectangle2D.Double(DXY + 24 * H, DXY + 4 * H, 2 * H, H);
                    if (game.setPaluba(i, j, 2, vert)) {
                        p2--;
                    }

                } else if (p1 != 0 && isSelectP1 && mX > (DXY) && mY > (DXY) && mX < (DXY + 10 * H) && mY < DXY + 10 * H) {
                    isSelectP1 = false;
                    //line1 = new Rectangle2D.Double(DXY + 24 * H, DXY + 6 * H, 1 * H, H);
                    if (game.setPaluba(i, j, 1, vert)) {
                        p1--;
                    }
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (rasstanovka) {
                mX = e.getX();
                mY = e.getY();
                int i = (mX - (DXY)) / H;
                int j = (mY - DXY) / H;
                Graphics g = getGraphics();
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(2));
                g.setColor(new Color(330099));
                if (isSelectP4) {
                    if(vert) g.drawRect(DXY + H * i, DXY + H * j, H*4, H);
                    else g.drawRect(DXY + H * i, DXY + H * j, H, H*4);
                }
                if (isSelectP3) {
                    if(vert) g.drawRect(DXY + H * i, DXY + H * j, H*3, H);
                    else g.drawRect(DXY + H * i, DXY + H * j, H, H*3);
                }
                if (isSelectP2) {
                    if(vert) g.drawRect(DXY + H * i, DXY + H * j, H*2, H);
                    else g.drawRect(DXY + H * i, DXY + H * j, H, H*2);
                }
                if (isSelectP1) {
                    g.drawRect(DXY + H * i, DXY + H * j, H, H);
                }

            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }


}