import javax.swing.*; //Graafiline timer
import java.awt.event.ActionEvent;

public class GameTimer {
    private int minutes;
    private int seconds;
    private boolean running; //Kas käib
    private final Timer timer;

    public GameTimer(View view) {
        this.minutes = 0;
        this.seconds = 0;
        this.running = false;

        timer = new Timer(1000, (ActionEvent evt) -> {
           seconds++; //Sekundite kasv
            if(seconds > 59) { //Kui minuti jagu sekundeid saab täis siis tekib minut
                seconds = 0;
                minutes++;
            }
            view.getLblTime().setText(formatGameTime()); //Infoboardil näitab aega
        });
    }

    //Timeri ajaformaatimine
    public String formatGameTime() {
        return String.format("%02d:%02d", minutes,seconds);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    //Timeri käivitamine
    public void startTimer() {
        this.timer.start();
    }
    //Timeri peatamine
    public void stopTimer() {
        this.timer.stop();
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    //Tagastatakse mänguaeg sekundites edetabeli jaoks
    public int getPlayedTimeInSeconds() {
        return (this.minutes * 60) + seconds;
    }
}
