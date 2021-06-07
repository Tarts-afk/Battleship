import javax.swing.*;

public class AppMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Model model = new Model(); //Teeb mudeli
                View view = new View(model); //Teeb vaate ehk JFrame
                Controller controller = new Controller(model, view);

                view.registerGameboardMouse(controller); //Hiire liikumise töölesaamiseks

                view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Et ristike nurgs töötaks ja sulgeks rakenduse
                view.pack(); // Vaate kokkupakkime, et objektid Jframil oma koha leiaks
                view.setLocationRelativeTo(null); //Jframe keset ekraani
                view.setResizable(false); //Akna suurust ei saa muuta
                view.setVisible(true); //Jframe nähtavaks
            }
        });
    }

}
