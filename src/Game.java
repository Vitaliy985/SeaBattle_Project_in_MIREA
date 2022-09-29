import javax.swing.*;

public class Game {
    //поле игрока
    public int polePlayer[][] = new int [10][10];
    //поле компьютера
    public int poleComp[][] = new int [10][10];
    //игра (0 - в процессе, 1 - победил игрок, -1 - победил компьютер)
    public static int endGame = 3;
    //кол-во кораблей игрока
    public int P1, P2, P3, P4;
    //кол-во кораблей компьютера
    public int C1, C2, C3, C4;
    //количество ходов игрока
    public int kolHodPlay;
    //количество ходов компьютера
    public int kolHodComp;
    //пауза при выстреле
    public final int pause = 600;
    //чей ход
    public boolean myHod;
    //чей ход
    public boolean compHod;
    //поток для ходов пк
    Thread thread = new Thread();

    public void hodPlayer(int pole[][], int i, int j)
    {
        kolHodPlay++;
        pole[i][j]+=7;
        proverkaNaPopadanie(pole, i, j);
        endGame();
        thread =new Thread(new Runnable() {
            @Override
            public void run() {
                //если промах
                if (poleComp[i][j] < 8) {
                    myHod = false;
                    compHod = true; //передаем ход компьютеру
                    // Ходит компьютер - пока попадает в цель
                    while (compHod) {
                        try {
                            Thread.sleep(pause);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        compHod = hodComp(polePlayer);
                    }
                    myHod = true;//передаем ход игроку после промаха компьютера
                }
            }
        });
        thread.start();
    }
    public void endGame()
    {
        if (endGame==0){
            int sumEnd=330; // 15*4+16*2*3+17*3*2+18*4 = 330
            int Playsum=0;
            int Compsum=0;
            kolUbitPk(poleComp);
            kolUbitPlayer(polePlayer);
            if (endGame==0) {
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        // Суммируем подбитые палубы
                        if (polePlayer[i][j] >= 15)
                            Playsum += polePlayer[i][j];
                        if (poleComp[i][j] >= 15)
                            Compsum += poleComp[i][j];
                    }
                }
                if (Playsum == sumEnd) {
                    endGame = 2;
                    JOptionPane.showMessageDialog(null,
                            "Вы проиграли! Попробуйте еще раз",
                            "Вы проиграли", JOptionPane.INFORMATION_MESSAGE);

                } else if (Compsum == sumEnd) {
                    endGame = 1;
                    JOptionPane.showMessageDialog(null,
                            "Поздравляю! Вы выиграли!",
                            "Вы выиграли", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
    public void kolUbitPk(int[][]pole){
        P4=0;P3=0;P2=0;P1=0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (pole[i][j]==18) P4++;
                if (pole[i][j]==17) P3++;
                if (pole[i][j]==16) P2++;
                if (pole[i][j]==15) P1++;
            }
        }
        P4/=4;P3/=3;P2/=2;
    }
    public void kolUbitPlayer(int[][]pole) {
        C4 = 0;C3 = 0;C2 = 0;C1 = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (pole[i][j] == 18) C4 = (C4 + 1);
                if (pole[i][j] == 17) C3 = (C3 + 1);
                if (pole[i][j] == 16) C2 = (C2 + 1);
                if (pole[i][j] == 15) C1 = (C1 + 1);
            }
        }
        C4/=4;C3/=3;C2/=2;
    }
    private void proverkaNaPopadanie(int pole[][], int i, int j)
    {
        if (pole[i][j]==8)
        {
            pole[i][j]+=7;
            surroundADeadShip(pole, i, j);
        }
        if (pole[i][j]==9)
            proverkaNaUbiistvo(pole, i, j, 2);

        if (pole[i][j]==10)
            proverkaNaUbiistvo(pole, i, j, 3);

        if (pole[i][j]==11)
            proverkaNaUbiistvo(pole, i, j, 4);
    }
    private void proverkaNaUbiistvo(int pole[][], int i, int j, int paluba)
    {
        int ranen=0;
        for (int a=i-(paluba-1); a<=i+(paluba-1); a++)
        {
            for (int b=j-(paluba-1); b<=j+(paluba-1); b++)
            {
                if (indOutOfBounds(a, b) && pole[a][b]==paluba+5)
                    ranen++;
            }
        }
        if (ranen==paluba)
        {
            for (int a=i-(paluba-1); a<=i+(paluba-1); a++)
            {
                for (int b=j-(paluba-1); b<=j+(paluba-1); b++)
                {
                    if (indOutOfBounds(a, b) && pole[a][b]==paluba+5) {
                        pole[a][b] += 7;
                        surroundADeadShip(pole, a, b);
                    }
                }
            }
        }
    }
    private boolean indOutOfBounds(int i, int j)
    {
        return ((i >= 0) && (i <= 9)) && ((j >= 0) && (j <= 9));
    }
    public void setOkrKilled(int pole[][],int i,int j){
        if (indOutOfBounds(i, j)){
            if (pole[i][j]==-1 || pole[i][j]==6){
                pole[i][j]--;
            }
        }
    }
    private void surroundADeadShip(int pole[][], int i, int j)
    {
        setOkrKilled(pole, i - 1, j - 1); // сверху слева
        setOkrKilled(pole, i - 1, j); // сверху
        setOkrKilled(pole, i - 1, j + 1); // сверху справа
        setOkrKilled(pole, i, j + 1); // справа
        setOkrKilled(pole, i + 1, j + 1); // снизу справа
        setOkrKilled(pole, i + 1, j); // снизу
        setOkrKilled(pole, i + 1, j - 1); // снизу слева
        setOkrKilled(pole, i, j - 1); // слева
    }
    public boolean hodComp(int pole[][])
    {
        if ((endGame == 0) || compHod) {
            //увеличиваем на 1 количество ходов
            kolHodComp++;
            // Признак попадания в цель
            boolean popal = false;
            // Признак выстрела в раненый корабль
            boolean ranen = false;
            //признак направления корабля
            boolean horizontal = false;
            _metka:
                for (int i=0; i<10; i++)
                {
                    for (int j=0; j<10; j++) {
                        if (pole[i][j] >= 9 && pole[i][j] <= 11) {
                            ranen = true;
                            if ((indOutOfBounds(i - 3, j) && pole[i - 3][j] >= 9 && (pole[i - 3][j] <= 11))
                                    || (indOutOfBounds(i - 2, j) && pole[i - 2][j] >= 9 && (pole[i - 2][j] <= 11))
                                    || (indOutOfBounds(i - 1, j) && pole[i - 1][j] >= 9 && (pole[i - 1][j] <= 11))
                                    || (indOutOfBounds(i + 3, j) && pole[i + 3][j] >= 9 && (pole[i + 3][j] <= 11))
                                    || (indOutOfBounds(i + 2, j) && pole[i + 2][j] >= 9 && (pole[i + 2][j] <= 11))
                                    || (indOutOfBounds(i + 1, j) && pole[i + 1][j] >= 9 && (pole[i + 1][j] <= 11))) {
                                horizontal = true;
                            } else if ((indOutOfBounds(i, j + 3) && pole[i][j + 3] >= 9 && (pole[i][j + 3] <= 11))
                                    || (indOutOfBounds(i, j + 2) && pole[i][j + 2] >= 9 && (pole[i][j + 2] <= 11))
                                    || (indOutOfBounds(i, j + 1) && pole[i][j + 1] >= 9 && (pole[i][j + 1] <= 11))
                                    || (indOutOfBounds(i, j - 3) && pole[i][j - 3] >= 9 && (pole[i][j - 3] <= 11))
                                    || (indOutOfBounds(i, j - 2) && pole[i][j - 2] >= 9 && (pole[i][j - 2] <= 11))
                                    || (indOutOfBounds(i, j - 1) && pole[i][j - 1] >= 9 && (pole[i][j - 1] <= 11))) {
                                horizontal = false;
                            }
                        }
                        else {
                            for (int x = 0; x < 50; x++) {
                                int napr = (int) (Math.random() * 4);
                                if (napr == 0 && indOutOfBounds(i - 1, j) && (pole[i - 1][j] <= 4) && (pole[i - 1][j] != -2)) {
                                    pole[i - 1][j] += 7;
                                    //проверяем, убили или нет
                                    this.proverkaNaPopadanie(pole, i - 1, j);
                                    if (pole[i - 1][j] >= 8) popal = true;
                                    //прерываем цикл
                                    break _metka;
                                } else if (napr == 1 && indOutOfBounds(i + 1, j) && (pole[i + 1][j] <= 4) && (pole[i + 1][j] != -2)) {
                                    pole[i + 1][j] += 7;
                                    this.proverkaNaPopadanie(pole, i + 1, j);
                                    if (pole[i + 1][j] >= 8) popal = true;
                                    break _metka;
                                } else if (napr == 2 && indOutOfBounds(i, j - 1) && (pole[i][j - 1] <= 4) && (pole[i][j - 1] != -2)) {
                                    pole[i][j - 1] += 7;
                                    this.proverkaNaPopadanie(pole, i, j - 1);
                                    if (pole[i][j - 1] >= 8) popal = true;
                                    break _metka;
                                } else if (napr == 3 && indOutOfBounds(i, j + 1) && (pole[i][j + 1] <= 4) && (pole[i][j + 1] != -2)) {
                                    pole[i][j + 1] += 7;
                                    this.proverkaNaPopadanie(pole, i, j + 1);
                                    if (pole[i][j + 1] >= 8) popal = true;
                                    break _metka;
                                }

                            }
                        }
                    }
                }
        }
    return true;
    }
}