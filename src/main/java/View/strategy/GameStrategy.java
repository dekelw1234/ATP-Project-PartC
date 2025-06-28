package View.strategy;

import javafx.scene.Parent;

/**
 * כל משחק יודע "לייצר" את ה-UI שלו (Parent)
 * ולבצע איניציאליזציה אם צריך.
 */
public interface GameStrategy {
    /**
     * @return השורש של הסצנה/פנל שמציג את המשחק
     */
    Parent createGamePane();
}
