import javax.swing.JOptionPane;

public class Game {
    public int polePlayer[][] = new int [10][10];
    public int poleComp[][] = new int [10][10];
    public static int endGame=3;

    public int P1,P2,P3,P4;

    public int C1,C2,C3,C4;
    public final int pause=600;
    public boolean myHod;
    public boolean compHod;
    Thread thread=new Thread();
    Game() {
        poleComp = new int[10][10];
    }
    public void start() {
        //если вдруг компьютер еще не закончил ход, то ждем
        //обнуляем массив
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                polePlayer[i][j] = 0;
                poleComp[i][j] = 0;
            }
        }
        myHod =true; //мой ход
        compHod=false;
        endGame=0;// игра идет
        kolvoUbitPk(poleComp);
        kolvoUbitPlayer(polePlayer);
        if (!Screen.rasstanovka) {
            setPalubaPlay();
        }
        setPalubaComp();
    }
    public void hodPlayer(int pole[][], int i, int j) {
        pole[i][j] += 7;
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
                        compHod = compHodit(polePlayer);
                        //воспроизводим звук при попадании компьютера
                    }
                    myHod = true;//передаем ход игроку после промаха компьютера
                }
            }
        });
        thread.start();
    }
    public void endGame(){
        if (endGame==0){
            int sumEnd=330; //когда все корабли убиты
            int sumPlay=0; // Сумма убитых палуб игрока
            int sumComp=0; // Сумма убитых палуб компьютера
            kolvoUbitPk(poleComp);
            kolvoUbitPlayer(polePlayer);
            if (endGame==0) {
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        // Суммируем подбитые палубы
                        if (polePlayer[i][j] >= 15) sumPlay += polePlayer[i][j];
                        if (poleComp[i][j] >= 15) sumComp += poleComp[i][j];
                    }
                }
                if (sumPlay == sumEnd) {
                    endGame = 2;
                    //выводим диалоговое окно игроку
                    JOptionPane.showMessageDialog(null,
                            "Вы проиграли! Попробуйте еще раз",
                            "Вы проиграли", JOptionPane.INFORMATION_MESSAGE);

                } else if (sumComp == sumEnd) {
                    endGame = 1;
                    //выводим диалоговое окно игроку
                    JOptionPane.showMessageDialog(null,
                            "Поздравляю! Вы выиграли!",
                            "Вы выиграли", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
    public void kolvoUbitPk(int[][]mas){
        P4=0;P3=0;P2=0;P1=0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (mas[i][j]==18) P4++;
                if (mas[i][j]==17) P3++;
                if (mas[i][j]==16) P2++;
                if (mas[i][j]==15) P1++;
            }
        }
        P4/=4;P3/=3;P2/=2;
    }
    public void kolvoUbitPlayer(int[][]mas) {
        C4 = 0;C3 = 0;C2 = 0;C1 = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (mas[i][j] == 18) C4 = (C4 + 1);
                if (mas[i][j] == 17) C3 = (C3 + 1);
                if (mas[i][j] == 16) C2 = (C2 + 1);
                if (mas[i][j] == 15) C1 = (C1 + 1);
            }
        }
        C4/=4;C3/=3;C2/=2;
    }
    private void proverkaNaPopadanie(int mas[][], int i, int j){
        if (mas[i][j]==8) { //Если однопалубный
            mas[i][j] += 7; //прибавляем к убитому +7
            surroundADeadShip(mas,i,j);//Уменьшаем окружение убитого на 1
        }
        else if (mas[i][j]==9){
            proverkaNaUbiistvo(mas,i,j,2);
        }
        else if (mas[i][j]==10){
            proverkaNaUbiistvo(mas,i,j,3);
        }
        else if (mas[i][j]==11){
            proverkaNaUbiistvo(mas,i,j,4);
        }
    }
    private void proverkaNaUbiistvo(int pole[][], int i, int j, int kolpalub) {
        //Количество раненых палуб
        int ranen=0;
        //Выполняем подсчет раненых палуб
        for (int a=i-(kolpalub-1);a<=i+(kolpalub-1  );a++) {
            for (int b = j - (kolpalub - 1); b <= j + (kolpalub - 1); b++) {
                // Если это палуба раненого корабля
                if (indOutOfBounds(a, b) && (pole[a][b] == kolpalub + 7))
                    ranen++;
            }
        }
        // Если количество раненых палуб совпадает с количеством палуб
        // корабля, то он убит - прибавляем число 7
        if (ranen==kolpalub) {
            for (int x=i-(kolpalub-1);x<=i+(kolpalub-1);x++) {
                for (int y=j-(kolpalub-1);y<=j+(kolpalub-1);y++) {
                    // Если это палуба раненого корабля
                    if (indOutOfBounds(x, y)&&(pole[x][y]==kolpalub+7)) {
                        // помечаем палубой убитого корабля
                        pole[x][y]+=7;
                        // уменьшаем на 1 окружение убитого корабля
                        surroundADeadShip(pole, x, y);
                    }
                }
            }
        }
    }
    public void setOkrKilled(int mas[][],int i,int j){
        if (indOutOfBounds(i, j)){
            if (mas[i][j]==-1 || mas[i][j]==6){
                mas[i][j]--;
            }
        }
    }

    private void surroundADeadShip(int[][] mas, int i, int j) {
        setOkrKilled(mas, i - 1, j - 1); // сверху слева
        setOkrKilled(mas, i - 1, j); // сверху
        setOkrKilled(mas, i - 1, j + 1); // сверху справа
        setOkrKilled(mas, i, j + 1); // справа
        setOkrKilled(mas, i + 1, j + 1); // снизу справа
        setOkrKilled(mas, i + 1, j); // снизу
        setOkrKilled(mas, i + 1, j - 1); // снизу слева
        setOkrKilled(mas, i, j - 1); // слева
    }
    boolean compHodit(int pole[][]) {
        //если идет автоигра или ход компьютера
        if ((endGame == 0) || (compHod)) {
            // Признак попадания в цель
            boolean popal = false;
            // Признак выстрела в раненый корабль
            boolean ranen = false;
            //признак направления корабля
            boolean horiz = false;
            _for1:
            // break метка
            // Пробегаем все игровое поле игрока
            for (int i = 0; i < 10; i++)
                for (int j = 0; j < 10; j++)
                    //если находим раненую палубу
                    if ((pole[i][j] >= 9) && (pole[i][j] <= 11)) {
                        ranen = true;
                        //ищем подбитую палубу слева и справа
                        if ((indOutOfBounds(i - 3, j) && pole[i - 3][j] >= 9 && (pole[i - 3][j] <= 11))
                                || (indOutOfBounds(i - 2, j) && pole[i - 2][j] >= 9 && (pole[i - 2][j] <= 11))
                                || (indOutOfBounds(i - 1, j) && pole[i - 1][j] >= 9 && (pole[i - 1][j] <= 11))
                                || (indOutOfBounds(i + 3, j) && pole[i + 3][j] >= 9 && (pole[i + 3][j] <= 11))
                                || (indOutOfBounds(i + 2, j) && pole[i + 2][j] >= 9 && (pole[i + 2][j] <= 11))
                                || (indOutOfBounds(i + 1, j) && pole[i + 1][j] >= 9 && (pole[i + 1][j] <= 11))) {
                            horiz = true;
                        } else if ((indOutOfBounds(i, j + 3) && pole[i][j + 3] >= 9 && (pole[i][j + 3] <= 11))
                                //ищем подбитую палубу сверху и снизу
                                || (indOutOfBounds(i, j + 2) && pole[i][j + 2] >= 9 && (pole[i][j + 2] <= 11))
                                || (indOutOfBounds(i, j + 1) && pole[i][j + 1] >= 9 && (pole[i][j + 1] <= 11))
                                || (indOutOfBounds(i, j - 3) && pole[i][j - 3] >= 9 && (pole[i][j - 3] <= 11))
                                || (indOutOfBounds(i, j - 2) && pole[i][j - 2] >= 9 && (pole[i][j - 2] <= 11))
                                || (indOutOfBounds(i, j - 1) && pole[i][j - 1] >= 9 && (pole[i][j - 1] <= 11))) {
                            horiz = false;
                        }
                        //если не удалось определить направление корабля, то выбираем произвольное направление для обстрела
                        else for (int x = 0; x < 50; x++) {
                                int napr = (int) (Math.random() * 4);
                                if (napr == 0 && indOutOfBounds(i - 1, j) && (pole[i - 1][j] <= 4) && (pole[i - 1][j] != -2)) {
                                    pole[i - 1][j] += 7;
                                    //проверяем, убили или нет
                                    proverkaNaPopadanie(pole, i - 1, j);
                                    if (pole[i - 1][j] >= 8) popal = true;
                                    //прерываем цикл
                                    break _for1;
                                } else if (napr == 1 && indOutOfBounds(i + 1, j) && (pole[i + 1][j] <= 4) && (pole[i + 1][j] != -2)) {
                                    pole[i + 1][j] += 7;
                                    proverkaNaPopadanie(pole, i + 1, j);
                                    if (pole[i + 1][j] >= 8) popal = true;
                                    break _for1;
                                } else if (napr == 2 && indOutOfBounds(i, j - 1) && (pole[i][j - 1] <= 4) && (pole[i][j - 1] != -2)) {
                                    pole[i][j - 1] += 7;
                                    proverkaNaPopadanie(pole, i, j - 1);
                                    if (pole[i][j - 1] >= 8) popal = true;
                                    break _for1;
                                } else if (napr == 3 && indOutOfBounds(i, j + 1) && (pole[i][j + 1] <= 4) && (pole[i][j + 1] != -2)) {
                                    pole[i][j + 1] += 7;
                                    proverkaNaPopadanie(pole, i, j + 1);
                                    if (pole[i][j + 1] >= 8) popal = true;
                                    break _for1;
                                }
                            }
                        //если определили направление, то производим выстрел только в этом напрвлении
                        if (horiz) { //по горизонтали
                            if (indOutOfBounds(i - 1, j) && (pole[i - 1][j] <= 4) && (pole[i - 1][j] != -2)) {
                                pole[i - 1][j] += 7;
                                proverkaNaPopadanie(pole, i - 1, j);
                                if (pole[i - 1][j] >= 8) popal = true;
                                break _for1;
                            } else if (indOutOfBounds(i + 1, j) && (pole[i + 1][j] <= 4) && (pole[i + 1][j] != -2)) {
                                pole[i + 1][j] += 7;
                                proverkaNaPopadanie(pole, i + 1, j);
                                if (pole[i + 1][j] >= 8) popal = true;
                                break _for1;
                            }
                        }//по вертикали
                        else if (indOutOfBounds(i, j - 1) && (pole[i][j - 1] <= 4) && (pole[i][j - 1] != -2)) {
                            pole[i][j - 1] += 7;
                            proverkaNaPopadanie(pole, i, j - 1);
                            if (pole[i][j - 1] >= 8) popal = true;
                            break _for1;
                        } else if (indOutOfBounds(i, j + 1) && (pole[i][j + 1] <= 4) && (pole[i][j + 1] != -2)) {
                            pole[i][j + 1] += 7;
                            proverkaNaPopadanie(pole, i, j + 1);
                            if (pole[i][j + 1] >= 8) popal = true;
                            break _for1;
                        }
                    }

            // если нет ранненых кораблей
            if (!ranen) {
                // делаем 100 случайных попыток выстрела
                // в случайное место
                for (int l = 1; l <= 100; l++) {
                    // Находим случайную позицию на игровом поле
                    int i = (int) (Math.random() * 10);
                    int j = (int) (Math.random() * 10);
                    //Проверяем, что можно сделать выстрел
                    if ((pole[i][j] <= 4) && (pole[i][j] != -2)) {
                        //делаем выстрел
                        pole[i][j] += 7;
                        //проверяем, что убит
                        proverkaNaPopadanie(pole, i, j);
                        // если произошло попадание
                        if (pole[i][j] >= 8)
                            popal = true;
                        //выстрел произошел
                        ranen = true;
                        //прерываем цикл
                        break;
                    }
                }
            }
            // проверяем конец игры
            endGame();
            // возвращаем результат
            return popal;
        }else return false;
    }
    private boolean indOutOfBounds(int i, int j) {
        if (((i >= 0) && (i <= 9)) && ((j >= 0) && (j <= 9))) {
            return true;
        } else return false;
    }

    private void setOkr(int[][] pole, int i, int j, int val) {
        if (indOutOfBounds(i, j) && pole[i][j] == 0) {
            pole[i][j] = val;
        }
    }

    private void setOkrBeforeGame(int[][] pole, int i, int j, int k) {
        setOkr(pole, i - 1, j - 1, k);
        setOkr(pole, i - 1, j, k);
        setOkr(pole, i - 1, j + 1, k);
        setOkr(pole, i, j + 1, k);
        setOkr(pole, i, j - 1, k);
        setOkr(pole, i + 1, j + 1, k);
        setOkr(pole, i + 1, j, k);
        setOkr(pole, i + 1, j - 1, k);
    }
    public boolean handSetPaluba(int i, int j, int kolPal, boolean napr){
        //признак установки палубы
        boolean flag = false;
        // Если можно расположить палубу
        if (testNewPaluba(polePlayer, i, j)) {
            if (napr==false){ // вправо
                if (testNewPaluba(polePlayer, i, j + (kolPal - 1)))
                    flag = true;
            }
            else if (napr){ // вниз
                if (testNewPaluba(polePlayer, i + (kolPal - 1), j))
                    flag = true;
            }
        }
        if (flag) {
            //Помещаем в ячейку число палуб
            polePlayer[i][j] = kolPal;
            // Окружаем минус двойками
            setOkrBeforeGame(polePlayer, i, j, -2);
            if (napr){ // вправо
                for (int k = kolPal - 1; k >= 1; k--) {
                    polePlayer[i][j + k] = kolPal;
                    setOkrBeforeGame(polePlayer, i, j + k, -2);
                }
            }
            else if (napr){ // вниз
                for (int k = kolPal - 1; k >= 1; k--) {
                    polePlayer[i + k][j] = kolPal;
                    setOkrBeforeGame(polePlayer, i + k, j, -2);
                }
            }
        }
        okrEnd(polePlayer); //заменяем -2 на -1
        return flag;
    }
    private boolean testNewPaluba(int [][]pole,int i, int j){
        if (indOutOfBounds(i, j)==false) return false;
        if ((pole[i][j]==0) || (pole[i][j]==-2)) return true;
        else return false;
    }
    private void okrEnd(int[][] pole) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (pole[i][j] == -2)
                    pole[i][j] = -1;
            }
        }
    }
    private void autoSetPaluba(int [][]mas, int kolPal){
        int i = 0, j = 0;
        while (true) {
            boolean flag = false;
            i = (int) (Math.random() * 10);
            j = (int) (Math.random() * 10);
            int napr = (int) (Math.random() * 4); // 0 - вверх. 1 - вправо. 2 - вниз. 3 - влево

            // Если можно расположить палубу
            if (testNewPaluba(mas, i, j)) {
                if (napr == 0) { //вверх
                    if (testNewPaluba(mas, i - (kolPal - 1), j))  //если можно расположить палубу вверх, то flag = true
                        flag = true;
                }
                else if (napr == 1){ // вправо
                    if (testNewPaluba(mas, i, j + (kolPal - 1)))
                        flag = true;
                }
                else if (napr == 2){ // вниз
                    if (testNewPaluba(mas, i + (kolPal - 1), j))
                        flag = true;
                }
                else if (napr == 3){ // влево
                    if (testNewPaluba(mas, i, j -(kolPal - 1)))
                        flag = true;
                }
            }
            if (flag) {
                //Помещаем в ячейку число палуб
                mas[i][j] = kolPal;
                // Окружаем минус двойками
                setOkrBeforeGame(mas, i, j, -2);
                if (napr == 0) {// вверх
                    for (int k = kolPal - 1; k >= 1; k--) {
                        mas[i -k][j] = kolPal;
                        setOkrBeforeGame(mas, i - k, j, -2);
                    }
                }
                else if (napr == 1){ // вправо
                    for (int k = kolPal - 1; k >= 1; k--) {
                        mas[i][j + k] = kolPal;
                        setOkrBeforeGame(mas, i, j + k, -2);
                    }
                }
                else if (napr == 2){ // вниз
                    for (int k = kolPal - 1; k >= 1; k--) {
                        mas[i + k][j] = kolPal;
                        setOkrBeforeGame(mas, i + k, j, -2);
                    }
                }
                else { // влево
                    for (int k = kolPal - 1; k >= 1; k--) {
                        mas[i][j -k] = kolPal;
                        setOkrBeforeGame(mas, i, j - k, -2);
                    }
                }
                //прерываем цикл
                break;
            }
        }
        okrEnd(mas); //заменяем -2 на -1
    }
    private void setPalubaPlay(){
        autoSetPaluba(polePlayer, 4);
        for (int i = 1; i <= 2; i++) {
            autoSetPaluba(polePlayer, 3);
        }
        for (int i = 1; i <= 3; i++) {
            autoSetPaluba(polePlayer, 2);
        }
        for(int i = 1;i<= 4;i++){
            autoSetPaluba(polePlayer,1);
        }
    }
    private void setPalubaComp(){
        autoSetPaluba(poleComp, 4);
        for (int i = 1; i <= 2; i++) {
            autoSetPaluba(poleComp, 3);
        }
        for (int i = 1; i <= 3; i++) {
            autoSetPaluba(poleComp, 2);
        }
        for (int i = 1;i<= 4;i++){
            autoSetPaluba(poleComp,1);
        }
    }
}