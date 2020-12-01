package tk.mgdev;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URL;


public class TextEditor extends JFrame {
    private volatile SearchResults searchResults;
    private final Map<String, ActionListener> actionListeners;

    private final JTextField patternSearchField;
    private final JTextArea contentTextArea;

    private final JCheckBox useRegExCheckbox;
    private volatile boolean isPatternOrContentChanged;


    public TextEditor() {
        System.out.println("EDT thread name: " + Thread.currentThread().getName());

        searchResults = SearchResults.createEmpty();
        actionListeners = new HashMap<>();

        patternSearchField = new JTextField();
        contentTextArea = new JTextArea();

        useRegExCheckbox = new JCheckBox("UseRegEx");
        isPatternOrContentChanged = false;

        File workingDirectory = FileSystemView.getFileSystemView().getDefaultDirectory();
        JFileChooser fileChooser = new JFileChooser(workingDirectory);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setName("FileChooser");

        ActionListener onOpenAction = (e -> {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                loadContentFromFile(file.getAbsolutePath());
            }
        });

        ActionListener onSaveAction = (e -> {
            int returnValue = fileChooser.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                saveContentToFile(file.getAbsolutePath());
            }
        });

        ActionListener onExitAction = (e -> dispose());

        ActionListener onSearchAction = (e -> searchAndSelectFirstResult());

        ActionListener onNextMatchAction = (e -> {
            if (isPatternOrContentChanged) {
                searchAndSelectFirstResult();
            } else {
                selectNextResult();
            }
        });

        ActionListener onPreviousMatchAction = (e -> {
            if (isPatternOrContentChanged) {
                searchAndSelectFirstResult();
            } else {
                selectPreviousResult();
            }
        });

        ActionListener onUseRegExAction = (e -> {
            if (e.getSource() instanceof JMenuItem) {
                useRegExCheckbox.setSelected(!useRegExCheckbox.isSelected());
            }
            searchAndSelectFirstResult();
        });

        actionListeners.put("onOpenAction", onOpenAction);
        actionListeners.put("onSaveAction", onSaveAction);
        actionListeners.put("onExitAction", onExitAction);
        actionListeners.put("onSearchAction", onSearchAction);
        actionListeners.put("onNextMatchAction", onNextMatchAction);
        actionListeners.put("onPreviousMatchAction", onPreviousMatchAction);
        actionListeners.put("onUseRegExAction", onUseRegExAction);

        DocumentListener onContentChangeListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                isPatternOrContentChanged = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                isPatternOrContentChanged = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                isPatternOrContentChanged = true;
            }
        };
        contentTextArea.getDocument().addDocumentListener(onContentChangeListener);
        patternSearchField.getDocument().addDocumentListener(onContentChangeListener);

        setTitle("Text Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(size.width / 2, size.height / 2);

        add(fileChooser);
        setJMenuBar(createMenuBar());
        add(createSearchPanel(), BorderLayout.NORTH);
        add(createMainTextArea(), BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        JMenuItem menuOpenItem = new JMenuItem("Open");
        JMenuItem menuSaveItem = new JMenuItem("Save");
        JMenuItem menuExitItem = new JMenuItem("Exit");

        JMenu menuSearch = new JMenu("Search");
        JMenuItem menuStartSearchItem = new JMenuItem("Start search");
        JMenuItem menuNextMatchItem = new JMenuItem("Next search");
        JMenuItem menuPreviousMatchItem = new JMenuItem("Previous search");
        JMenuItem menuUseRegExpItem = new JMenuItem("Use RegEx");

        menuFile.setName("MenuFile");
        menuOpenItem.setName("MenuOpen");
        menuSaveItem.setName("MenuSave");
        menuExitItem.setName("MenuExit");
        menuSearch.setName("MenuSearch");
        menuStartSearchItem.setName("MenuStartSearch");
        menuPreviousMatchItem.setName("MenuPreviousMatch");
        menuNextMatchItem.setName("MenuNextMatch");
        menuUseRegExpItem.setName("MenuUseRegExp");

        menuOpenItem.addActionListener(actionListeners.get("onOpenAction"));
        menuSaveItem.addActionListener(actionListeners.get("onSaveAction"));
        menuExitItem.addActionListener(actionListeners.get("onExitAction"));
        menuStartSearchItem.addActionListener(actionListeners.get("onSearchAction"));
        menuNextMatchItem.addActionListener(actionListeners.get("onNextMatchAction"));
        menuPreviousMatchItem.addActionListener(actionListeners.get("onPreviousMatchAction"));
        menuUseRegExpItem.addActionListener(actionListeners.get("onUseRegExAction"));

        for (JMenuItem menuItem : List.of(menuOpenItem,
                menuSaveItem,
                menuExitItem)) {
            menuFile.add(menuItem);
        }
        for (JMenuItem menuItem : List.of(menuStartSearchItem,
                menuNextMatchItem,
                menuPreviousMatchItem,
                menuUseRegExpItem)) {
            menuSearch.add(menuItem);
        }

        menuBar.add(menuFile);
        menuBar.add(menuSearch);
        return menuBar;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String iconsPath = "/icons/";
        int TOOLBAR_ELEMENT_SIZE = 30;
        int ICON_SIZE = (int) ((TOOLBAR_ELEMENT_SIZE / (100 * 1.0)) * 70);
        Icon openIcon = getImageIcon(iconsPath + "open.png", "Open", ICON_SIZE);
        Icon saveIcon = getImageIcon(iconsPath + "save.png", "Save", ICON_SIZE);
        Icon searchIcon = getImageIcon(iconsPath + "search.png", "Search", ICON_SIZE);
        Icon previousMatchIcon = getImageIcon(iconsPath + "previousMatchButton.png", "Previous", ICON_SIZE);
        Icon nextMatchIcon = getImageIcon(iconsPath + "nextMatchButton.png", "Next", ICON_SIZE);

        JButton openButton = new JButton(openIcon);
        JButton saveButton = new JButton(saveIcon);
        JButton startSearchButton = new JButton(searchIcon);
        JButton previousMatchButton = new JButton(previousMatchIcon);
        JButton nextMatchButton = new JButton(nextMatchIcon);

        openButton.setName("OpenButton");
        saveButton.setName("SaveButton");
        patternSearchField.setName("SearchField");
        startSearchButton.setName("StartSearchButton");
        nextMatchButton.setName("NextMatchButton");
        previousMatchButton.setName("PreviousMatchButton");
        useRegExCheckbox.setName("UseRegExCheckbox");

        openButton.addActionListener(actionListeners.get("onOpenAction"));
        saveButton.addActionListener(actionListeners.get("onSaveAction"));
        startSearchButton.addActionListener(actionListeners.get("onSearchAction"));
        nextMatchButton.addActionListener(actionListeners.get("onNextMatchAction"));
        previousMatchButton.addActionListener(actionListeners.get("onPreviousMatchAction"));
        useRegExCheckbox.addActionListener(actionListeners.get("onUseRegExAction"));

        for (JButton button : List.of(openButton, saveButton, previousMatchButton, startSearchButton, nextMatchButton)) {
            button.setPreferredSize(new Dimension(TOOLBAR_ELEMENT_SIZE, TOOLBAR_ELEMENT_SIZE));
        }
        int SEARCH_FIELD_WIDTH = 120;
        patternSearchField.setPreferredSize(new Dimension(SEARCH_FIELD_WIDTH, TOOLBAR_ELEMENT_SIZE));

        for (JComponent component : List.of(
                openButton,
                saveButton,
                patternSearchField,
                startSearchButton,
                previousMatchButton,
                nextMatchButton,
                useRegExCheckbox)) {
            searchPanel.add(component);
        }
        return searchPanel;
    }

    private JScrollPane createMainTextArea() {
        contentTextArea.setName("TextArea");
        JScrollPane mainTextView = new JScrollPane(contentTextArea);
        mainTextView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainTextView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        mainTextView.setName("ScrollPane");
        return mainTextView;
    }

    private ImageIcon getImageIcon(String path, String description, int SIZE) {
        URL imageURL = this.getClass().getResource(path);
        if (imageURL != null) {
            return new ImageIcon(new ImageIcon(imageURL, description)
                    .getImage()
                    .getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH));
        } else {
            return null;
        }
    }

    private void loadContentFromFile(String fileName) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Path.of(fileName)));
        } catch (IOException e) {
            System.out.println("Unable to open \"" + fileName + "\"");
        } finally {
            contentTextArea.setText(content);
        }
    }

    private void saveContentToFile(String fileName) {
        String content = contentTextArea.getText();
        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(fileName))) {
            bw.write(content);
        } catch (IOException e) {
            System.out.println("Can't save to \"" + fileName + "\"");
        }
    }

    private void selectNextResult() {
        if (searchResults.isEmpty()) {
            return;
        }
        selectResult(searchResults.nextResult());
    }

    private void selectPreviousResult() {
        if (searchResults.isEmpty()) {
            return;
        }
        selectResult(searchResults.previousResult());
    }

    private void selectResult(SearchResult searchResult) {
        contentTextArea.setCaretPosition(searchResult.getEndIndex());
        contentTextArea.select(searchResult.getStartIndex(), searchResult.getEndIndex());
        contentTextArea.grabFocus();
    }

    private synchronized void searchAndSelectFirstResult() {
        new SwingWorker<SearchResults, Void>() {
            @Override
            protected SearchResults doInBackground() {
                System.out.println("Worker Thread name: " + Thread.currentThread().getName());
                return SearchResults.of(patternSearchField.getText(), contentTextArea.getText(), useRegExCheckbox.isSelected());
            }

            @Override
            protected void done() {
                try {
                    searchResults = get();
                    isPatternOrContentChanged = false;
                    selectNextResult();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
