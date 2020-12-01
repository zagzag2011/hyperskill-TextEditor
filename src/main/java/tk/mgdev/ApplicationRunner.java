package tk.mgdev;

import javax.swing.*;

public class ApplicationRunner {
    public static void main(String[] args) {
        System.out.println("Init thread name: " + Thread.currentThread().getName());
        SwingUtilities.invokeLater(TextEditor::new);
    }
}
