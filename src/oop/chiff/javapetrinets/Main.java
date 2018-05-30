package oop.chiff.javapetrinets;

import javax.swing.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class Main {
    private static Document petrinet = null;
    public static SendDevMessage sender = new SendDevMessage(true);

    public static void main(String[] args) {
        Window w = new Window();
        CustomCanvas c = new CustomCanvas();

        String[] nazvy = {"Place", "Transition", "Arc", "+", "-", "Remove", "Move", "Play"};
        ArrayList<Button> buttons = new ArrayList<>();
        for(String n : nazvy) buttons.add(new Button(n));

        Button nahraj = new Button("Open");
        Button uloz = new Button("Save");
        Label stav = new Label("Action : none");

        nahraj.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            if (fileChooser.showOpenDialog(nahraj) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                Main.sender.send("Loaded file: " + selectedFile.getAbsolutePath());

                try {
                    XmlReader document = new XmlReader(selectedFile);
                    petrinet = document.getPetrinet();
                    petrinet.setDocuments();
                    petrinet.createHashMap();
                    petrinet.arcValidate();
                    Main.sender.send("Petrinent nacitany");
                    c.setPetrinet(petrinet);
                } catch (JAXBException | IncorrectArcDefinition | NonExistingElementException err) {
                    System.err.println(err.getMessage());
                    if (err instanceof JAXBException) {
                        err.printStackTrace();
                    }
                }
            }
        });

        uloz.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

                if (fileChooser.showSaveDialog(w) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    Main.sender.send("Saved to file: " + file.getAbsolutePath());

                    JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);
                    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    jaxbMarshaller.marshal(petrinet, new File(file.getAbsolutePath()));
                }
            } catch (JAXBException ee) {
                ee.printStackTrace();
            }
        });

        JScrollPane scrollPane = new JScrollPane(c, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        Panel buttonsPanel = new Panel();

        buttonsPanel.add(nahraj);
        buttonsPanel.add(uloz);
        buttons.forEach(b -> buttonsPanel.add(b));
        buttonsPanel.add(stav);

        w.add(scrollPane, BorderLayout.CENTER);
        w.add(buttonsPanel, BorderLayout.NORTH);

        w.setVisible(true);
        c.setButtonManager(new ButtonManager(buttonsPanel));

        try {
            XmlReader document = new XmlReader();
            petrinet = document.getPetrinet();
            petrinet.setDocuments();
            petrinet.createHashMap();
            c.setPetrinet(petrinet);
        } catch (JAXBException err) {
            System.err.println(err.getMessage());
            err.printStackTrace();
        }

    }

    public static void fireTransitions(int[] transitionIds) {
        for (int transitionId : transitionIds) {
            try {
                Node node = petrinet.getNodeById(transitionId);
                Transition t;

                if (node instanceof Transition)
                    t = (Transition) node;
                else throw new WrongPetriflowInputException(transitionId);

                t.fireTransition();
            } catch (NonExistingElementException | WrongPetriflowInputException | TransitionCannotBeFiredException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
